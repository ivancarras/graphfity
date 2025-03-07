package com.github.ivancarras.graphfity.plugin.mapper

import com.github.ivancarras.graphfity.plugin.model.ProjectGraph
import com.github.ivancarras.graphfity.plugin.model.ProjectModuleData
import com.github.ivancarras.graphfity.plugin.model.ProjectModuleEdge
import com.github.ivancarras.graphfity.plugin.model.ProjectModuleNode
import com.github.ivancarras.graphfity.plugin.model.datastructures.Node

private const val RANK_SEP = 1.2

fun ProjectGraph.toDot(rootNode: ProjectModuleNode): String = graphDot {
    nodesDot(this@toDot)
    edgesDot(this@toDot)
}

private fun StringBuilder.nodesDot(
    adjacencyList: ProjectGraph,
) {
    adjacencyList.nodes.forEach { appendLine(it.toDot()) }
}

private fun StringBuilder.edgesDot(
    adjacencyList: ProjectGraph,
) {
    adjacencyList.edges.forEach { appendLine(it.toDot()) }
}

private fun graphDot(content: StringBuilder.() -> Unit): String = buildString {
    appendLine("digraph {")
    appendLine("    graph [ranksep=$RANK_SEP];")
    content()
    appendLine("}")
}

private fun Node<ProjectModuleData>.toDot(): String = buildString {
    appendLine("    node [style=filled, shape=${data.nodeType.shape} fillcolor=\"${data.nodeType.fillColor}\"];")
    appendLine("        \"${data.path}\"")
}

private fun ProjectModuleEdge.toDot(): String = buildString {
    appendLine("    \"${source.data.path}\" -> \"${destination.data.path}\";")
}


