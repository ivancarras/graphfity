package com.github.ivancarras.graphfity.plugin.mapper

import com.github.ivancarras.graphfity.plugin.model.ProjectGraph
import com.github.ivancarras.graphfity.plugin.model.ProjectModuleData
import com.github.ivancarras.graphfity.plugin.model.ProjectModuleData.NodeType
import com.github.ivancarras.graphfity.plugin.model.ProjectModuleNode
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.util.GradleVersion

private const val ROOT_LEVEL = 0

fun Project.toRootNode(nodeTypes: Set<NodeType>): ProjectModuleNode {
    val rootProjectModuleData = buildProjectModuleData(
        path = path,
        nodeTypes = nodeTypes,
        level = ROOT_LEVEL,
    ) ?: throw IllegalArgumentException("Root project path does not match any node type")
    return ProjectModuleNode(id = rootProjectModuleData.path, data = rootProjectModuleData)
}

fun Project.toProjectGraph(nodeTypes: Set<NodeType>): ProjectGraph {
    val adjacencyList = ProjectGraph()
    val rootNode = project.toRootNode(nodeTypes)
    adjacencyList.addNode(rootNode)
    addChildNodesForProjectDependencies(
        project = project,
        parentNode = rootNode,
        adjacencyList = adjacencyList,
        nodeTypes = nodeTypes,
        parentLevel = rootNode.data.level,
    )
    return adjacencyList
}


private fun addChildNodesForProjectDependencies(
    parentNode: ProjectModuleNode,
    project: Project,
    nodeTypes: Set<NodeType>,
    adjacencyList: ProjectGraph,
    parentLevel: Int,
) {
    project.configurations
        .forEach { config ->
            config.dependencies
                .withType(ProjectDependency::class.java)
                .mapToProject(project)
                .filterNot { it.path == project.path }
                .forEach { childProject ->
                    val childLevel = parentLevel + 1
                    buildProjectModuleData(
                        path = childProject.path,
                        nodeTypes = nodeTypes,
                        level = childLevel,
                    )?.let { dependencyProjectNodeData ->
                        val childNode =
                            ProjectModuleNode(id = dependencyProjectNodeData.path, data = dependencyProjectNodeData)
                        val isChildNodePresent = adjacencyList.contains(id = childNode.id)
                        if (!isChildNodePresent) {
                            adjacencyList.addNode(childNode)
                        }
                        adjacencyList.addDirectedEdge(source = parentNode, destination = childNode)

                        // To rank correctly the child level, if we find a new dependency with a higher level
                        // that the previously defined we update the node level
                        val isNeededToUpdateChildNodeLevel = isChildNodePresent && childLevel >
                            (adjacencyList.nodes.find { it.id == childNode.id }?.data?.level ?: Int.MAX_VALUE)
                        if (isNeededToUpdateChildNodeLevel) {
                            adjacencyList.updateNode(childNode)
                        }

                        if (!isChildNodePresent) {
                            addChildNodesForProjectDependencies(
                                parentNode = childNode,
                                project = childProject,
                                adjacencyList = adjacencyList,
                                nodeTypes = nodeTypes,
                                parentLevel = childNode.data.level,
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
    nodeTypes: Set<NodeType>,
    level: Int,
): ProjectModuleData? =
    nodeTypes.find { nodeType ->
        nodeType.regex.toRegex().matches(path) && nodeType.isEnabled
    }?.let { nodeType ->
        ProjectModuleData(path = path, nodeType = nodeType, level = level)
    }
