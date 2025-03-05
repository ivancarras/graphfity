package com.github.ivancarras.graphfity.plugin.model

data class ProjectModuleData(val path: String, val nodeType: NodeType, val level: Int) {
    data class NodeType(
        val name: String,
        val regex: String,
        val isEnabled: Boolean,
        val shape: String,
        val fillColor: String,
    )
}
