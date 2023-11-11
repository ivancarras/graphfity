group = "com.github.ivancarras"
version = "1.0.1"

plugins {
    id("java")
    id("java-gradle-plugin")
    id("maven-publish")
    kotlin("jvm") version "1.8.0"
    id("com.gradle.plugin-publish") version "1.2.1"
}

repositories {
    mavenCentral()
}

gradlePlugin {
    website.set("https://github.com/ivancarras/graphfity")
    vcsUrl.set("https://github.com/ivancarras/graphfity.git")
    plugins {
        create("GraphfityPlugin") {
            id = "com.github.ivancarras.graphfity"
            displayName = "Graphfity Plugin"
            description = "Graphfity empowers you to visualize and analyze the intricate dependencies within your project's internal modules. By generating a comprehensive dependency node graph, it provides valuable insights to help you optimize the relationships between different modules. This tool proves particularly advantageous for developers working on multi-module applications, offering a clear and insightful PNG image that encapsulates the intricate interplay of your project components."
            implementationClass = "com.github.ivancarras.graphfity.plugin.main.GraphfityPlugin"
            tags.set(listOf("kotlin-DSL", "graph", "graphviz", "plugin", "multi-module","app modularization","kotlin", "android", "architecture"))
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin{
    jvmToolchain(17)
}

publishing {
    repositories {
        maven {
            url = uri("path/to/standalone/plugin/project")
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")
    implementation("org.codehaus.groovy:groovy-all:3.0.16")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}
