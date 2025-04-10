package com.github.ivancarras.graphfity.plugin.model

import com.github.ivancarras.graphfity.plugin.model.datastructures.AdjacencyList
import com.github.ivancarras.graphfity.plugin.model.datastructures.Edge
import com.github.ivancarras.graphfity.plugin.model.datastructures.Node

class ProjectGraph : AdjacencyList<ProjectModuleData>() {

    override fun addDirectedEdge(source: Node<ProjectModuleData>, destination: Node<ProjectModuleData>) {
        val edge = Edge(source = source, destination = destination)
        if (adjacencyMap[source]?.contains(edge) == false) {
            super.addDirectedEdge(source = source, destination = destination)
        }
    }

    fun updateNode(node: Node<ProjectModuleData>) {
        val updatedAdjacencyMap = ProjectGraph().adjacencyMap
        adjacencyMap.forEach { entry ->
            val updatedKey = if (entry.key.id == node.id) {
                node
            } else {
                entry.key
            }
            val updatedValue = ArrayList(entry.value.map { edge ->
                val updatedSource = if (edge.source.id == node.id) {
                    node
                } else {
                    edge.source
                }
                val updatedDestination = if (edge.destination.id == node.id) {
                    node
                } else {
                    edge.destination
                }
                Edge(source = updatedSource, destination = updatedDestination)
            })
            updatedAdjacencyMap[updatedKey] = updatedValue
        }
        adjacencyMap.clear()
        adjacencyMap.putAll(updatedAdjacencyMap)
    }

    fun contains(id: String): Boolean =
        nodes.any { it.id == id }
}

