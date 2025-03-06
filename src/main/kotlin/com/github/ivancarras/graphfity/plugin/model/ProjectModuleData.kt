package com.github.ivancarras.graphfity.plugin.model

data class ProjectModuleData(val path: String, val type: Type) {
    data class Type(
        val name: String,
        val regex: String,
        val isEnabled: Boolean,
        val shape: String,
        val fillColor: String,
    )
}
