package com.github.ivancarras.graphfity.plugin.task

import com.github.ivancarras.graphfity.plugin.model.datastructures.AdjacencyList
import com.github.ivancarras.graphfity.plugin.model.ProjectModuleData
import com.github.ivancarras.graphfity.plugin.model.ProjectModuleAdjacencyList
import com.github.ivancarras.graphfity.plugin.model.ProjectModuleData.Type
import com.github.ivancarras.graphfity.plugin.model.ProjectModuleNode
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
    val nodeTypesPathProperty: Property<String> = project.objects.property(String::class.java)

    @Input
    val graphImagePathProperty: Property<String> = project.objects.property(String::class.java)

    @Input
    val projectRootNameProperty: Property<String> = project.objects.property(String::class.java)

    private val nodeTypesPath: String by lazy { nodeTypesPathProperty.get() }
    private val dotPath: String by lazy { graphImagePathProperty.get() }
    private val projectRootName: String by lazy { projectRootNameProperty.get() }
    private val types: Set<Type> by lazy { readNodeTypesFile(nodeTypesPath) }

    @TaskAction
    fun graphfity() {
        val adjacencyList: ProjectModuleAdjacencyList = mapProjectToAdjacencyList(
            project = getRootProject(projectRootName),
        )

        val adjacencyListString = adjacencyList.adjacencyMap.map {
            "path: ${it.key.data.path} - dependencies: ${
                it.value.map {
                    it.destination.data.path
                }
            }"
        }.joinToString("\n")

        println(adjacencyListString)

        /*  obtainNodesLevels(
             rootProjectName = projectRootName,
             dependencies = dependencies,
             nodeLevel = nodesLevel,
         )
         addNodesToFile(
             dotFile = dotFile,
             nodes = adjacencyList,
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
         ) */

        //        val dotFile = createDotFile(dotPath)
    }

    private fun getRootProject(projectRootName: String): Project {
        return requireNotNull(
            project.findProject(projectRootName)
        ) {
            "The property provided as projectRootPath: $projectRootName does not correspond to any project"
        }
    }

    private fun readNodeTypesFile(nodeTypesPath: String): Set<Type> {
        val jsonFile = File(nodeTypesPath)
        val jsonObjects = JsonSlurper().parseText(jsonFile.readText())
        return if (jsonObjects is List<*>) {
            jsonObjects.fold(setOf()) { acc, item ->
                if (item is Map<*, *>) {
                    acc + Type(
                        name = item["name"] as String,
                        regex = item["regex"] as String,
                        isEnabled = item["isEnabled"] as Boolean,
                        shape = item["shape"] as String,
                        fillColor = item["fillColor"] as String,
                    )
                } else {
                    acc
                }
            }
        } else {
            error("Malformed json file")
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

    private fun mapProjectToAdjacencyList(
        project: Project,
    ): ProjectModuleAdjacencyList {
        val rootProjectModuleData = buildProjectModuleData(
            path = project.path,
            types = types,
        ) ?: throw IllegalArgumentException("Root project path does not match any node type")
        val adjacencyList = AdjacencyList<ProjectModuleData>()
        val rootNode = ProjectModuleNode(rootProjectModuleData)
        adjacencyList.addNode(rootNode)
        addChildNodesForProjectDependencies(
            project = project,
            parentNode = rootNode,
            adjacencyList = adjacencyList,
        )
        return adjacencyList
    }

    private fun addChildNodesForProjectDependencies(
        parentNode: ProjectModuleNode,
        project: Project,
        adjacencyList: AdjacencyList<ProjectModuleData>,
    ) {
        project.configurations.forEach { config ->
            config.dependencies
                .withType(ProjectDependency::class.java)
                .mapToProject(project)
                .filterNot { it.path == project.path }
                .forEach { childProject ->
                    buildProjectModuleData(
                        path = childProject.path,
                        types = types,
                    )?.let { dependencyProjectNodeData ->
                        val childNode = ProjectModuleNode(data = dependencyProjectNodeData)
                        val isChildNodeAlreadyAdded = adjacencyList.contains(data = dependencyProjectNodeData)
                        if (!isChildNodeAlreadyAdded) {
                            adjacencyList.addNode(childNode)
                        }
                        adjacencyList.addDirectedEdge(source = parentNode, destination = childNode)
                        if (!isChildNodeAlreadyAdded) {
                            addChildNodesForProjectDependencies(
                                parentNode = childNode,
                                project = childProject,
                                adjacencyList = adjacencyList,
                            )
                        }
                    }
                }
        }
    }

    @Suppress("deprecation")
    private fun Iterable<ProjectDependency>.mapToProject(project: Project): List<Project> = map {
        // https://docs.gradle.org/8.11/release-notes.html
        // https://github.com/gradle/gradle/issues/30992
        // path attribute starts to be supported on Gradle 8.11 so we need to check the version before using it
        if (GradleVersion.current() > GradleVersion.version("8.11")) {
            project.project(it.path)
        } else {
            it.dependencyProject
        }
    }

    private fun obtainNodesLevels(
        rootProjectName: String,
        dependencies: HashSet<Pair<ProjectModuleData, ProjectModuleData>>,
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

    /*   private fun addNodeToFile(dotFile: File, projectModuleData: ProjectModuleData) {
          if (projectModuleData.config.isEnabled) {
              dotFile.appendText("node [style=filled, shape = ${projectModuleData.config.shape} fillcolor=\"${projectModuleData.config.fillColor}\"];\n")
              dotFile.appendText("\"${projectModuleData.path}\"\n")
          }
      } */

    /*     private fun addNodesToFile(dotFile: File, nodes: HashSet<ProjectModuleData>) {
            nodes.forEach { node ->
                addNodeToFile(dotFile, node)
            }
        } */

    private fun buildProjectModuleData(
        path: String,
        types: Set<Type>
    ): ProjectModuleData? =
        types.find { nodeType ->
            nodeType.regex.toRegex().matches(path) && nodeType.isEnabled
        }?.let { nodeType ->
            ProjectModuleData(path = path, type = nodeType)
        }

    /*    private fun addDependenciesToFile(
           dotFile: File,
           dependencies: HashSet<Pair<ProjectModuleData, ProjectModuleData>>
       ) {
           val adjacencyList = mutableMapOf<ProjectModuleData, MutableList<ProjectModuleData>>()

           dependencies.forEach { (from, to) ->
               adjacencyList.computeIfAbsent(from) { mutableListOf() }.add(to)
           }

           val cyclicEdges = detectCycles(adjacencyList)

           // Write to DOT file
           dependencies.filter { it.first.config.isEnabled && it.second.config.isEnabled }
               .forEach { (from, to) ->
                   val isCyclic = Pair(from, to) in cyclicEdges
                   val style = if (isCyclic) "[color=red, style=dashed]" else ""
                   dotFile.appendText("  \"${from.path}\" -> \"${to.path}\" $style\n")
               }
       } */

    private fun detectCycles(graph: Map<ProjectModuleData, List<ProjectModuleData>>): Set<Pair<ProjectModuleData, ProjectModuleData>> {
        val visited = mutableSetOf<ProjectModuleData>()
        val stack = mutableSetOf<ProjectModuleData>()
        val cyclicEdges = mutableSetOf<Pair<ProjectModuleData, ProjectModuleData>>()

        fun dfs(node: ProjectModuleData) {
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
