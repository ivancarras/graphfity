package com.graphfity.plugin.task

import com.graphfity.plugin.model.NodeData
import com.graphfity.plugin.model.NodeType
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GraphfityTask : DefaultTask() {
    @Input
    val nodeTypesPath: Property<String> = project.objects.property(String::class.java)

    @Input
    val graphImagePath: Property<String> = project.objects.property(String::class.java)

    @Input
    val projectRootName: Property<String> = project.objects.property(String::class.java)

    @TaskAction
    fun graphfity() {

        val nodeTypesPath = nodeTypesPath.get()
        val nodeTypes = loadNodeTypes(nodeTypesPath)
        val dotPath = graphImagePath.get()
        val projectRootName = projectRootName.get()
        val rootProject = getRootProject(projectRootName)
        val nodes = HashSet<NodeData>()
        val dependencies = HashSet<Pair<NodeData, NodeData>>()
        val nodesLevel = HashMap<String, Int>()
        val dotFile = createDotFile(dotPath)

        obtainDependenciesData(rootProject, nodes, dependencies, nodeTypes, nodesLevel)
        addNodesToFile(dotFile, nodes)
        addDependenciesToFile(dotFile, dependencies)
        addNodeLevelsToFile(dotFile, nodesLevel)
        generateGraph(dotFile)
    }

    private fun getRootProject(projectRootName: String): Project {
        return project.findProject(projectRootName)
            ?: throw kotlin.IllegalArgumentException("The property provided as projectRootPath: $projectRootName does not correspond to any project")
    }

    private fun loadNodeTypes(nodeTypesPath: String): List<NodeType> {
        val jsonFile = File(nodeTypesPath)
        val jsonObjects = JsonSlurper().parseText(jsonFile.readText())
        return if (jsonObjects is List<*>) {
            jsonObjects.fold(emptyList()) { acc, item ->
                if (item is Map<*, *>) {
                    acc + NodeType(
                        name = item["name"] as String,
                        regex = item["regex"] as String,
                        isEnabled = item["isEnabled"] as Boolean,
                        shape = item["shape"] as String,
                        fillColor = item["fillColor"] as String
                    )
                } else {
                    acc
                }
            }
        } else {
            emptyList()
        }
    }

    private fun generateGraph(dotFile: File) {
        val dotCommand = listOf("dot", "-Tpng", "-O", DOT_FILE)
        ProcessBuilder(dotCommand)
            .directory(dotFile.parentFile)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
            .run {
                waitFor()
                dotFile.delete()
                if (exitValue() != 0) {
                    throw RuntimeException(errorStream.toString())
                } else {
                    println("Project module dependency graph created at ${dotFile.parentFile.absolutePath}.png")
                }
            }
    }

    private fun obtainDependenciesData(
        project: Project,
        projects: HashSet<NodeData>,
        dependencies: HashSet<Pair<NodeData, NodeData>>,
        nodeTypes: List<NodeType>,
        nodesLevel: HashMap<String, Int>,
        level: Int = 0
    ) {
        val projectNodeData = mapProjectToNode(project, nodeTypes)

        if (projectNodeData != null && projectNodeData.nodeType.isEnabled) {
            projects.add(projectNodeData)

            val nodeLevel = nodesLevel[project.path]
            if (nodeLevel == null || level > nodeLevel) {
                nodesLevel[projectNodeData.path] = level
            }
        }

        project.configurations.forEach { config ->
            config.dependencies
                .withType(ProjectDependency::class.java)
                .map { it.dependencyProject }
                .filterNot { project == it.project }
                .forEach { dependencyProject ->
                    val dependencyProjectNodeData = mapProjectToNode(dependencyProject, nodeTypes)
                    if (dependencyProjectNodeData != null && projectNodeData != null &&
                        dependencyProjectNodeData.nodeType.isEnabled
                    ) {
                        projects.add(dependencyProjectNodeData)

                        val nodeLevel = nodesLevel[dependencyProjectNodeData.path]
                        if (nodeLevel == null || level > nodeLevel) {
                            nodesLevel[dependencyProjectNodeData.path] = level + 1
                        }
                        dependencies.add(Pair(projectNodeData, dependencyProjectNodeData))
                        obtainDependenciesData(
                            dependencyProject,
                            projects,
                            dependencies,
                            nodeTypes,
                            nodesLevel,
                            level + 1
                        )
                    }
                }
        }
    }

    private fun createDotFile(dotPath: String): File = File(dotPath + DOT_FILE).apply {
        delete()
        parentFile.mkdirs()
        appendText(
            "digraph {\n" +
                    "  graph [ranksep=1.2];\n" +
                    "  rankdir=TB; splines=true;\n"
        )
    }

    private fun addNodeToFile(dotFile: File, nodeData: NodeData) {
        if (nodeData.nodeType.isEnabled) {
            dotFile.appendText("node [style=filled, shape = ${nodeData.nodeType.shape} fillcolor=\"${nodeData.nodeType.fillColor}\"];\n")
            dotFile.appendText("\"${nodeData.path}\"\n")
        }
    }

    private fun addNodesToFile(dotFile: File, nodes: HashSet<NodeData>) {
        nodes.forEach { node ->
            addNodeToFile(dotFile, node)
        }
    }

    private fun mapProjectToNode(project: Project, nodeTypes: List<NodeType>): NodeData? =
        nodeTypes.firstOrNull { nodeType ->
            nodeType.regex.toRegex().matches(project.path)
        }?.let { nodeType ->
            NodeData(
                path = project.path, nodeType = nodeType
            )
        }

    private fun addDependenciesToFile(
        dotFile: File,
        dependencies: HashSet<Pair<NodeData, NodeData>>
    ) {
        dependencies.forEach { dependency ->
            if (dependency.first.nodeType.isEnabled && dependency.second.nodeType.isEnabled) {
                dotFile.appendText("  \"${dependency.first.path}\" -> \"${dependency.second.path}\"\n")
            }
        }
    }

    private fun addNodeLevelsToFile(
        dotFile: File, nodeLevels: HashMap<String, Int>
    ) {
        nodeLevels.asSequence().groupBy({ it.value }, { it.key }).forEach {
            dotFile.appendText("\n{ rank=same;")
            it.value.forEach { value ->
                dotFile.appendText(" \"$value\";")
            }
            dotFile.appendText("}\n")
        }
        dotFile.appendText("}\n")
    }

    companion object {
        private const val DOT_FILE = "project.dot"
    }
}