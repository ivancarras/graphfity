package com.github.ivancarras.graphfity.plugin.task

import com.github.ivancarras.graphfity.plugin.model.NodeData
import com.github.ivancarras.graphfity.plugin.model.NodeType
import groovy.json.JsonSlurper
import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.util.GradleVersion

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

        obtainNodesAndDependencies(
            project = rootProject,
            nodes = nodes,
            dependencies = dependencies,
            nodeTypes = nodeTypes,
        )
        obtainNodesLevels(
            rootProjectName = projectRootName,
            dependencies = dependencies,
            nodeLevel = nodesLevel,
        )
        addNodesToFile(
            dotFile = dotFile,
            nodes = nodes,
        )
        addDependenciesToFile(
            dotFile = dotFile,
            dependencies = dependencies
        )
        addNodeLevelsToFile(
            dotFile = dotFile,
            nodeLevels = nodesLevel,
        )
        generateGraph(
            dotFile = dotFile,
        )
    }

    private fun getRootProject(projectRootName: String): Project {
        return requireNotNull(
            project.findProject(projectRootName)
        ) {
            "The property provided as projectRootPath: $projectRootName does not correspond to any project"
        }
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
        val dotCommand = listOf("dot", "-Tpng", "-o", GRAPH_PNG, GRAPH_DOT)
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
                    println("Project module dependency graph created at ${dotFile.path}.png")
                }
            }
    }

    private fun obtainNodesAndDependencies(
        project: Project,
        nodes: HashSet<NodeData>,
        dependencies: HashSet<Pair<NodeData, NodeData>>,
        nodeTypes: List<NodeType>,
    ) {
        val projectNodeData = mapProjectToNode(project, nodeTypes)

        if (projectNodeData != null && projectNodeData.nodeType.isEnabled) {
            nodes.add(projectNodeData)
        }

        project.configurations.forEach { config ->
            config.dependencies
                .withType(ProjectDependency::class.java)
                .mapToProject(project)
                .forEach { dependencyProject ->
                    val dependencyProjectNodeData = mapProjectToNode(project = dependencyProject, nodeTypes = nodeTypes)
                    if (dependencyProjectNodeData != null && projectNodeData != null &&
                        dependencyProjectNodeData.nodeType.isEnabled
                    ) {
                        dependencies.add(Pair(projectNodeData, dependencyProjectNodeData))
                        if (dependencyProjectNodeData !in nodes) {
                            obtainNodesAndDependencies(
                                project = dependencyProject,
                                nodes = nodes,
                                dependencies = dependencies,
                                nodeTypes = nodeTypes,
                            )
                        }
                    }
                }
        }
    }

    @Suppress("deprecation")
    private fun Iterable<ProjectDependency>.mapToProject(project: Project): List<Project> = mapNotNull {
        // https://docs.gradle.org/8.11/release-notes.html
        // https://github.com/gradle/gradle/issues/30992
        // path attribute starts to be supported on Gradle 8.11 so we need to check the version before using it
        val dependencyProject = if (GradleVersion.current() > GradleVersion.version("8.11")) {
            project.project(it.path)
        } else {
            it.dependencyProject
        }
        if (project == dependencyProject) return@mapNotNull null
        dependencyProject
    }

    private fun obtainNodesLevels(
        rootProjectName: String,
        dependencies: HashSet<Pair<NodeData, NodeData>>,
        nodeLevel: HashMap<String, Int>,
    ) {
        var currentLevelPaths = listOf(rootProjectName)
        var level = 0
        while (currentLevelPaths.isNotEmpty()) {
            currentLevelPaths.forEach { nodeLevel[it] = level }

            val nextLevelPaths = dependencies
                .filter { it.first.path in currentLevelPaths && nodeLevel[it.first.path] == null }
                .map { it.second.path }

            currentLevelPaths = nextLevelPaths
            level++
        }
    }

    private fun createDotFile(dotPath: String): File = File(dotPath + GRAPH_DOT).apply {
        delete()
        parentFile.mkdirs()
        appendText(
            "digraph {\n" +
                "  graph [ranksep=1.2];\n"
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
        val adjacencyList = mutableMapOf<NodeData, MutableList<NodeData>>()

        dependencies.forEach { (from, to) ->
            adjacencyList.computeIfAbsent(from) { mutableListOf() }.add(to)
        }

        val cyclicEdges = detectCycles(adjacencyList)

        // Write to DOT file
        dependencies.filter { it.first.nodeType.isEnabled && it.second.nodeType.isEnabled }
            .forEach { (from, to) ->
                val isCyclic = Pair(from, to) in cyclicEdges
                val style = if (isCyclic) "[color=red, style=dashed]" else ""
                dotFile.appendText("  \"${from.path}\" -> \"${to.path}\" $style\n")
            }
    }

    private fun detectCycles(graph: Map<NodeData, List<NodeData>>): Set<Pair<NodeData, NodeData>> {
        val visited = mutableSetOf<NodeData>()
        val stack = mutableSetOf<NodeData>()
        val cyclicEdges = mutableSetOf<Pair<NodeData, NodeData>>()

        fun dfs(node: NodeData) {
            if (node in stack) return
            if (node !in visited) {
                visited.add(node)
                stack.add(node)

                graph[node]?.forEach { neighbor ->
                    if (neighbor in stack) {
                        cyclicEdges.add(Pair(node, neighbor))
                    }
                    dfs(neighbor)
                }

                stack.remove(node)
            }
        }

        graph.keys.forEach { node -> if (node !in visited) dfs(node) }
        return cyclicEdges
    }


    private fun addNodeLevelsToFile(
        dotFile: File,
        nodeLevels: HashMap<String, Int>
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
        private const val GRAPH_NAME = "graphify"
        private const val GRAPH_DOT = "$GRAPH_NAME.dot"
        private const val GRAPH_PNG = "$GRAPH_NAME.png"
    }
}
