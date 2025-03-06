package com.github.ivancarras.graphfity.plugin.model.datastructures

class AdjacencyList<T> {

    val adjacencyMap = mutableMapOf<Node<T>, ArrayList<Edge<T>>>()

    fun addNode(node: Node<T>): Node<T> {
        adjacencyMap[node] = arrayListOf()
        return node
    }

    fun addDirectedEdge(source: Node<T>, destination: Node<T>) {
        val edge = Edge(source = source, destination = destination)
        adjacencyMap[source]?.add(edge)
    }

    fun contains(data: T): Boolean =
        adjacencyMap.containsKey(Node(data))
}
