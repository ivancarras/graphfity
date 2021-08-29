plugins {
    id("java")
    id("java-gradle-plugin")
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm") version "1.5.21"
    id("com.gradle.plugin-publish") version "0.15.0"
}

repositories {
    mavenCentral()
}

pluginBundle {
    website = "https://github.com/ivancarras/graphfity"
    vcsUrl = "https://github.com/ivancarras/graphfity.git"
    tags = listOf("kotlin-DSL", "graph", "graphviz", "plugin", "multi-modular", "android")
}

group = "com.graphfity"
version = "1.0"

gradlePlugin {
    plugins {
        create("graphfityPlugin") {
            id = "com.graphfity"
            displayName = "Graphfity Plugin"
            description = "This plugin draws your multi-modular application dependency graph"
            implementationClass = "com.graphfity.plugin.main.GraphfityPlugin"
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

publishing {
    repositories {
        maven {
            url = uri("path/to/standalone/plugin/project")
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.21")
    implementation("org.codehaus.groovy:groovy-all:3.0.8")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
}
