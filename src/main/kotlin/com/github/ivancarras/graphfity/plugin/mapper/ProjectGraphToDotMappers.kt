package com.github.ivancarras.graphfity.plugin.mapper

import com.github.ivancarras.graphfity.plugin.model.ProjectGraph
import com.github.ivancarras.graphfity.plugin.model.ProjectModuleData
import com.github.ivancarras.graphfity.plugin.model.ProjectModuleEdge
import com.github.ivancarras.graphfity.plugin.model.ProjectModuleNode
import com.github.ivancarras.graphfity.plugin.model.datastructures.Node

private const val RANK_SEP = 1.2

fun ProjectGraph.toDot(rootNode: ProjectModuleNode): String {
    val nodesDot = nodes.joinToString("\n") { it.toDot() }
    val edgesDot = edges.joinToString("\n") { it.toDot() }
    // val ranksDot = bfsWithLevelMap(rootNode).toDot()
//
    return """
        digraph {
            graph [ranksep=$RANK_SEP];
            $nodesDot
            $edgesDot
         
        }
    """.trimIndent()
}

private fun Node<ProjectModuleData>.toDot(): String = """
        node [style=filled, shape = ${data.nodeType.shape} fillcolor="${data.nodeType.fillColor}"];
               "${data.path}"
               
    """

private fun ProjectModuleEdge.toDot(): String = """
        "${source.data.path}" -> "${destination.data.path}";
    """

private fun Map<Int, List<Node<ProjectModuleData>>>.toDot(): String {
    val dotString = StringBuilder()
    this.forEach { (level, nodes) ->
        dotString.appendLine("{ rank=same;")
        nodes.forEach { node ->
            dotString.appendLine("  \"${node.data.path}\";")
        }
        dotString.appendLine("}")
    }
    return dotString.toString()
}


