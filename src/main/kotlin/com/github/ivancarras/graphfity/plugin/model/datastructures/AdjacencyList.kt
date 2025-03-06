package com.github.ivancarras.graphfity.plugin.model.datastructures

class AdjacencyList<T> {

    private val adjacencyMap = mutableMapOf<Node<T>, ArrayList<Edge<T>>>()

    val nodes: List<Node<T>>
        get() = adjacencyMap.keys.toList()

    val edges: List<Edge<T>>
        get() = adjacencyMap.values.flatten().toList()

    fun addNode(node: Node<T>): Node<T> {
        adjacencyMap[node] = arrayListOf()
        return node
    }

    fun addDirectedEdge(source: Node<T>, destination: Node<T>) {
        val edge = Edge(source = source, destination = destination)
        adjacencyMap[source]?.add(edge)
    }

    fun contains(node: Node<T>): Boolean =
        adjacencyMap.containsKey(node)

    fun bfsWithLevelMap(startNode: Node<T>): Map<Int, List<Node<T>>> {
        val visited = mutableSetOf<Node<T>>()
        val queue = mutableListOf<Pair<Node<T>, Int>>()
        val result = mutableMapOf<Int, MutableList<Node<T>>>() // To store the level-nodes map

        if (!contains(startNode)) {
            return result
        }

        visited.add(startNode)
        queue.add(Pair(startNode, 0))

        while (queue.isNotEmpty()) {
            val (currentNode, currentLevel) = queue.removeAt(0)

            // Add the node to the list for the current level
            result.getOrPut(currentLevel) { mutableListOf() }.add(currentNode)

            val neighbors = adjacencyMap[currentNode]?.map { it.destination } ?: listOf()
            neighbors.forEach { neighbor ->
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor)
                    queue.add(Pair(neighbor, currentLevel + 1))
                }
            }
        }

        return result
    }
}
