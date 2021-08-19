package com.graphfity.plugin.main

import com.graphfity.plugin.model.NodeData
import com.graphfity.plugin.model.NodeType
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import java.io.File

@Suppress("LeakingThis")
abstract class GraphfityPluginExtension {
    abstract val nodeTypes: ListProperty<NodeType>
    abstract val dotPath: Property<String>
    abstract val projectRootPath: Property<String>

    init {
        dotPath.convention(DEFAULT_DOT_CHILD_PATH)
        projectRootPath.convention(DEFAULT_PROJECT_ROOT)
    }

    companion object {
        private const val DEFAULT_DOT_CHILD_PATH = "gradle/dependency-graph/"
        private const val DEFAULT_PROJECT_ROOT = ":app"
    }
}

class GraphfityPlugin : Plugin<Project> {
    private val dotCommand = listOf("dot", "-Tpng", "-O", DOT_FILE)

    override fun apply(project: Project) {
        val extension = project.extensions.create("graphfityExtension", GraphfityPluginExtension::class.java)

        project.task("graphfity") {
            it.doLast {
                val nodeTypes = extension.nodeTypes.get()
                val dotPath = extension.dotPath.get()
                val projectRootPath = extension.projectRootPath.get()
                val rootProject = project.findProject(projectRootPath)
                    ?: throw kotlin.IllegalArgumentException("The property provided as projectRootPath: $projectRootPath does not correspond to any project")
                val nodes = HashSet<NodeData>()
                val dependencies = HashSet<Pair<NodeData, NodeData>>()
                val dotFile = createDotFile(project, dotPath)

                obtainDependenciesData(rootProject, nodes, dependencies, nodeTypes)
                addNodesToFile(dotFile, nodes)
                addDependenciesToFile(dotFile, dependencies)
                generateGraph(dotFile)
            }
        }
    }

    private fun generateGraph(dotFile: File) {
        ProcessBuilder(dotCommand)
            .directory(dotFile.parentFile)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
            .run {
                waitFor()
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
        nodeTypes: List<NodeType>
    ) {
        val projectNodeData = mapProjectToNode(project, nodeTypes)
        project.configurations.forEach { config ->
            config.dependencies
                .withType(ProjectDependency::class.java)
                .map { it.dependencyProject }
                .forEach { dependencyProject ->
                    val dependencyProjectNodeData = mapProjectToNode(dependencyProject, nodeTypes)

                    if (dependencyProjectNodeData != null && projectNodeData != null &&
                        dependencyProjectNodeData.nodeType.isEnabled &&
                        dependencyProjectNodeData != projectNodeData &&
                        projectNodeData.nodeType.isEnabled
                    ) {
                        projects.add(dependencyProjectNodeData)
                        projects.add(projectNodeData)
                        dependencies.add(Pair(projectNodeData, dependencyProjectNodeData))
                        obtainDependenciesData(dependencyProject, projects, dependencies, nodeTypes)
                    }
                }
        }
    }

    private fun createDotFile(project: Project, dotPath: String): File =
        File(project.rootDir, dotPath + DOT_FILE).apply {
            parentFile.mkdirs()
            delete()
            appendText(
                "digraph {\n" +
                        "  graph [label=\"${project.name}\",labelloc=t,fontsize=30,ranksep=1.2];\n" +
                        "node [style=filled, fillcolor=\"#bbbbbb\"];" +
                        "  rankdir=TB; splines=true;\n"
            )
        }

    private fun addNodeToFile(dotFile: File, nodeData: NodeData) {
        if (nodeData.nodeType.isEnabled) {
            dotFile.appendText("node [shape = ${nodeData.nodeType.shape} fillcolor=\"${nodeData.nodeType.fillColor}\"];\n")
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
            nodeType.regex.matches(project.path)
        }?.let { nodeType ->
            NodeData(
                path = project.path, nodeType = nodeType
            )
        }

    private fun addDependenciesToFile(
        dotFile: File,
        dependencies: HashSet<Pair<NodeData, NodeData>>
    ) {
        dotFile.appendText("\n  # Dependencies\n\n")

        dependencies.forEach { dependency ->
            if (dependency.first.nodeType.isEnabled && dependency.second.nodeType.isEnabled) {
                dotFile.appendText("  \"${dependency.first.path}\" -> \"${dependency.second.path}\"\n")
            }
            if (dependencies.last() == dependency) {
                dotFile.appendText("}\n")
            }
        }
    }

    companion object {
        private const val DOT_FILE = "project.dot"
    }
}