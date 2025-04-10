package com.github.ivancarras.graphfity.plugin.model.datastructures

open class AdjacencyList<T> {

    protected val adjacencyMap = mutableMapOf<Node<T>, ArrayList<Edge<T>>>()

    val nodes: List<Node<T>>
        get() = adjacencyMap.keys.toList()

    val edges: List<Edge<T>>
        get() = adjacencyMap.values.flatten().toList()

    fun addNode(node: Node<T>): Node<T> {
        adjacencyMap[node] = arrayListOf()
        return node
    }

    open fun addDirectedEdge(source: Node<T>, destination: Node<T>) {
        val edge = Edge(source = source, destination = destination)
        adjacencyMap[source]?.add(edge)
    }
}
