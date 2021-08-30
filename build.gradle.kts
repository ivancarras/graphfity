group = "com.github.ivancarras"
version = "1.0.0"

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
    tags = listOf("kotlin-DSL", "graph", "graphviz", "plugin", "multi-modular", "android", "architecture")
}

gradlePlugin {
    plugins {
        create("GraphfityPlugin") {
            id = "com.github.ivancarras.graphfity"
            displayName = "Graphfity Plugin"
            description =
                "Graphfity creates a dependency node graph about your internal modules dependencies, helping you to analise and optimize the internal dependencies between your project modules, generating a png image about your project which is specially useful if you are developing a multi-modular application"
            implementationClass = "com.github.ivancarras.graphfity.plugin.main.GraphfityPlugin"
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
