package com.graphfity.plugin.main

import com.graphfity.plugin.task.GraphfityTask
import org.gradle.api.Plugin
import org.gradle.api.Project

//TODO review node visualization
//TODO review apply name class implementation
//TODO review naming in the graph

class GraphfityPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("graphfityExtension", GraphfityPluginExtension::class.java)
        project.tasks.create("graphfity", GraphfityTask::class.java) {
            it.graphImagePath.set(extension.graphImagePath)
            it.projectRootName.set(extension.projectRootName)
            it.nodeTypesPath.set(extension.nodeTypesPath)
        }
    }
}