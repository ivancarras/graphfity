package com.github.ivancarras.graphfity.plugin.main

import com.github.ivancarras.graphfity.plugin.task.GraphfityTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class GraphfityPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("graphfityExtension", GraphfityPluginExtension::class.java)
        project.tasks.create("graphfity", GraphfityTask::class.java) {
            it.graphImagePathProperty.set(extension.graphImagePath)
            it.projectRootNameProperty.set(extension.projectRootName)
            it.nodeTypesPathProperty.set(extension.nodeTypesPath)
        }
    }
}
