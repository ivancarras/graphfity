package com.github.ivancarras.graphfity.plugin.model.datastructures

data class Edge<T>(
    val source: Node<T>,
    val destination: Node<T>,
)
