package com.github.ivancarras.graphfity.plugin.task

import com.github.ivancarras.graphfity.plugin.mapper.toDot
import com.github.ivancarras.graphfity.plugin.mapper.toProjectGraph
import com.github.ivancarras.graphfity.plugin.model.ProjectGraph
import com.github.ivancarras.graphfity.plugin.model.ProjectModuleData.NodeType
import groovy.json.JsonSlurper
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class GraphfityTask : DefaultTask() {
    @Input
    val nodeTypesPathProperty: Property<String> = project.objects.property(String::class.java)

    @Input
    val graphImagePathProperty: Property<String> = project.objects.property(String::class.java)

    @Input
    val projectRootNameProperty: Property<String> = project.objects.property(String::class.java)

    private val nodeTypesPath: String by lazy { nodeTypesPathProperty.get() }
    private val graphFileImagePath: String by lazy { graphImagePathProperty.get() }
    private val projectRootName: String by lazy { projectRootNameProperty.get() }
    private val nodeTypes: Set<NodeType> by lazy { readNodeTypesFile(nodeTypesPath) }

    @TaskAction
    fun graphfity() {
        val rootProject = getRootProject(projectRootName)
        val adjacencyList: ProjectGraph = rootProject.toProjectGraph(nodeTypes = nodeTypes)
        val dot = adjacencyList.toDot()
        generateGraphFile(dot = dot)
    }

    private fun getRootProject(projectRootName: String): Project {
        return requireNotNull(
            project.findProject(projectRootName)
        ) {
            "The property provided as projectRootPath: $projectRootName does not correspond to any project"
        }
    }

    private fun readNodeTypesFile(nodeTypesPath: String): Set<NodeType> {
        val jsonFile = File(nodeTypesPath)
        val jsonObjects = JsonSlurper().parseText(jsonFile.readText())
        return if (jsonObjects is List<*>) {
            jsonObjects.fold(setOf()) { acc, item ->
                if (item is Map<*, *>) {
                    acc + NodeType(
                        name = item["name"] as String,
                        regex = item["regex"] as String,
                        isEnabled = item["isEnabled"] as Boolean,
                        shape = item["shape"] as String,
                        fillColor = item["fillColor"] as String,
                    )
                } else {
                    acc
                }
            }
        } else {
            error("Malformed json file")
        }
    }

    private fun generateGraphFile(dot: String) {
        try {
            val fullImagepath = graphFileImagePath + GRAPH_FILE_NAME
            val process = ProcessBuilder("dot", "-Tpng", "-o", fullImagepath)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            BufferedWriter(OutputStreamWriter(process.outputStream)).use { writer ->
                writer.write(dot)
                writer.flush()
            }

            val exitCode = process.waitFor()
            if (exitCode != 0) {
                throw RuntimeException(process.errorStream.bufferedReader().readText())
            } else {
                println("Graph successfully created at $fullImagepath")
            }
        } catch (e: IOException) {
            throw RuntimeException("Error executing dot command", e)
        }
    }

    companion object {
        private const val GRAPH_FILE_NAME = "graph.png"
    }
}
