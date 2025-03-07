package com.github.ivancarras.graphfity.plugin.mapper

import com.github.ivancarras.graphfity.plugin.model.ProjectGraph
import com.github.ivancarras.graphfity.plugin.model.ProjectModuleData
import com.github.ivancarras.graphfity.plugin.model.ProjectModuleData.NodeType
import com.github.ivancarras.graphfity.plugin.model.ProjectModuleNode
import com.github.ivancarras.graphfity.plugin.model.datastructures.AdjacencyList
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.util.GradleVersion

fun Project.toRootNode(nodeTypes: Set<NodeType>): ProjectModuleNode {
    val rootProjectModuleData = buildProjectModuleData(
        path = path,
        nodeTypes = nodeTypes,
    ) ?: throw IllegalArgumentException("Root project path does not match any node type")
    return ProjectModuleNode(rootProjectModuleData)
}

fun Project.toProjectGraph(rootNode: ProjectModuleNode, nodeTypes: Set<NodeType>): ProjectGraph {
    val adjacencyList = AdjacencyList<ProjectModuleData>()
    adjacencyList.addNode(rootNode)
    addChildNodesForProjectDependencies(
        project = project,
        parentNode = rootNode,
        adjacencyList = adjacencyList,
        nodeTypes = nodeTypes,
    )
    return adjacencyList
}

private val configurationTargets = listOf(
    "implementation",
    "api",
    "compileOnly",
    "runtimeOnly",
    "ksp",
    "kapt",
    "annotationProcessor"
)

private fun addChildNodesForProjectDependencies(
    parentNode: ProjectModuleNode,
    project: Project,
    nodeTypes: Set<NodeType>,
    adjacencyList: AdjacencyList<ProjectModuleData>,
) {
    project.configurations
        .filter { it.name in configurationTargets }
        .forEach { config ->
            config.dependencies
                .withType(ProjectDependency::class.java)
                .mapToProject(project)
                .filterNot { it.path == project.path }
                .forEach { childProject ->
                    buildProjectModuleData(
                        path = childProject.path,
                        nodeTypes = nodeTypes,
                    )?.let { dependencyProjectNodeData ->
                        val childNode = ProjectModuleNode(data = dependencyProjectNodeData)
                        val isChildNodeAlreadyAdded = adjacencyList.contains(node = childNode)
                        if (!isChildNodeAlreadyAdded) {
                            adjacencyList.addNode(childNode)
                        }
                        adjacencyList.addDirectedEdge(source = parentNode, destination = childNode)
                        if (!isChildNodeAlreadyAdded) {
                            addChildNodesForProjectDependencies(
                                parentNode = childNode,
                                project = childProject,
                                adjacencyList = adjacencyList,
                                nodeTypes = nodeTypes,
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

fun buildProjectModuleData(
    path: String,
    nodeTypes: Set<NodeType>
): ProjectModuleData? =
    nodeTypes.find { nodeType ->
        nodeType.regex.toRegex().matches(path) && nodeType.isEnabled
    }?.let { nodeType ->
        ProjectModuleData(path = path, nodeType = nodeType)
    }
