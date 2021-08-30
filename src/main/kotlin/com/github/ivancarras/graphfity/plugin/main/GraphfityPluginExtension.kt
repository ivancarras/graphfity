package com.github.ivancarras.graphfity.plugin.main

import org.gradle.api.provider.Property

@Suppress("LeakingThis")
abstract class GraphfityPluginExtension {
    abstract val nodeTypesPath: Property<String>
    abstract val graphImagePath: Property<String>
    abstract val projectRootName: Property<String>

    init {
        graphImagePath.convention(DEFAULT_GRAPH_IMAGE_PATH)
        projectRootName.convention(DEFAULT_PROJECT_ROOT_NAME)
        nodeTypesPath.convention(DEFAULT_NODE_TYPES_PATH)
    }

    companion object {
        private const val DEFAULT_GRAPH_IMAGE_PATH = "gradle/dependency-graph/"
        private const val DEFAULT_PROJECT_ROOT_NAME = ":app"
        private const val DEFAULT_NODE_TYPES_PATH = "src/main/resources"
    }
}