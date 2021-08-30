[![Plugin][plugin-shield]][plugin-url]
[![Apache License][license-shield]][license-url]
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
<br/>
<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/ivancarras/graphfity">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a>
</p>

<h1 align="center">Graphfity</h1>

<p align="center">
  <b>Graphfity</b> creates a dependency node diagram graph about your internal modules dependencies, specially useful if you are developing a <b>multi-modular application</b>
  <br />
  <br />
  <br />
  <a href="https://github.com/ivanca0rras/graphfity">View Demo</a>
  ·
  <a href="https://github.com/ivancarras/graphfity/issues">Report Bug</a>
  ·
  <a href="https://github.com/ivancarras/graphfity/issues">Request Feature</a>
</p>


<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#Installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->

## About The Project

![product-image](https://raw.githubusercontent.com/ivancarras/graphfity/feature/readme-creation/graphfity_example.png)

<br/><br/> 

As a software engineer, you should know as difficult is to maintain a project without a previous initial good
architecture. The project scales, new libraries are added, new features, new dependencies between the internal modules
are included...

The purpose of this plugin is help to visualize all the project dependencies between the internal modules, as the
projects grows, having of this way a main screenshot of all the features, libraries, core modules, components, or
whatever kind of module you want to analise in your project.

### Built With

* [Graphviz](https://graphviz.org/) Graph visualization software
* [Kotlin-DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html) An alternative to the traditional Groovy DSL
  using a modern language as Kotlin
* [Kotlin](https://kotlinlang.org/) The natural Java evolution, a modern, concise and save programming language

<!-- GETTING STARTED -->

## Getting Started

### Prerequisites

**Graphviz setup**  full guide: https://graphviz.org/download/

#### Mac 🍏

###### Option #1

   ```sh
  sudo port install graphviz
  ```

###### Option #2

  ```sh
  brew install graphviz
  ```

#### Windows 🪟

###### Option #1

  ```sh
  winget install graphviz
  ```

###### Option #2

  ```sh
  choco install graphviz
  ```

#### Linux 🐧

###### Option #1

   ```sh
  sudo apt install graphviz
  ```

###### Option #2

  ```
  sudo yum install graphviz
  ```

###### Option #3

  ```
  sudo apt install graphviz
  ```

## Installation

**Groovy DSL**

root build.gradle

```groovy
plugins {
    id "com.github.ivancarras.graphfity" version "1.0.0"
}

buildscript {
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath "com.github.ivancarras:graphfity-plugin:1.0.0"
    }
}

apply plugin: com.github.ivancarras.graphfity.plugin.main.GraphfityPlugin
```

**Kotlin DSL**

root build.gradle.kts

  ```kotlin
plugins {
    id("com.github.ivancarras.graphfity") version "1.0.0"
}
buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath('com.github.ivancarras:graphfity-plugin:1.0.0')
    }
}

apply(plugin = "com.github.ivancarras.graphfity.plugin.main.GraphfityPlugin")
  ```

### Plugin configuration

The plugin admits 3 configuration properties:

- **nodeTypesPath** (mandatory): this is the path for your json node types configuration file (explanation below)
- **projectRootName** (optional): start point from the task draws the dependencies, the default value is the ":app"
  module
- **graphImagePath** (optional): path where your graph image will be placed

#### NodeTypes.json

This is the file used to establish the different nodeTypes of your project a perfect example could be a project divided
into:

- Features
- App
- Components
- Libraries
- Core

nodeTypes.json

``` json 
[
  {
    "name": "App",
    "regex": "^:app$",
    "isEnabled": true,
    "shape": "box3d",
    "fillColor": "#BAFFC9"
  },
  {
    "name": "Feature",
    "regex": "^.*feature.*$",
    "isEnabled": true,
    "shape": "tab",
    "fillColor": "#E6F98A"
  },
  {
    "name": "Component",
    "regex": "^.*component.*$",
    "isEnabled": true ,
    "shape": "component",
    "fillColor": "#8AD8F9"
  },
  {
    "name": "Libraries",
    "regex": "^.*libraries.*$",
    "isEnabled": true,
    "shape": "cylinder",
    "fillColor": "#FFACFA"
  },
  {
    "name": "Core",
    "regex": "^.*core.*$",
    "isEnabled": true,
    "shape": "hexagon",
    "fillColor": "#D5625A"
  }
]

```

Copy this initial configuration file in an accessible path in your project. (the root path is perfect)

Now is time to configure the plugin:

Groovy DSL

``` groovy
graphfityExtension {
  nodeTypesPath = "<nodesTypes.json>" //(mandatory) Examples: graphfityConfig/nodesTypes.json establish the route to your nodeTypes.json
  projectRootName = "<projectNameToAnalise>" //(optional) Examples: ":app", ":feature:wishlist"... is up to you
  graphImagePath = "<graphsFolder>" //(optional)the folder where will be placed your graph.png image
}
```

Kotlin DSL

``` kotlin
configure<GraphfityPluginExtension> {
  nodeTypesPath.set("<nodesTypes.json>") //(mandatory) Examples: graphfityConfig/nodesTypes.json establish the route to your nodeTypes.json
  projectRootName.set("<projectNameToAnalise>") //(optional) Examples: ":app", ":feature:wishlist"... is up to you
  graphImagePath.set("<graphsFolder>") //(optional)the folder where will be placed your graph.png image
}
```

## Usage

When your configuration is done now you can execute:

Mac 🍏 & Linux 🐧

```shell
./gradlew graphfity
```

Windows 🪟 
```shell
./gradlew graphfity
```
The graph is going to be generated in the respective graphImagePath defined in the configuration

## Roadmap

See the [open issues](https://github.com/ivancarras/graphfity/issues) for a list of proposed features (and known issues)


<!-- CONTRIBUTING -->

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any
contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<!-- LICENSE -->

## License

Distributed under the Apache License. See `LICENSE` for more information.


<!-- CONTACT -->

## Contact

[![LinkedIn][linkedin-shield]][linkedin-url]

ivan.carrasco.dev@gmail.com


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[plugin-url]: https://plugins.gradle.org/plugin/com.github.ivancarras.graphfity

[plugin-shield]:https://img.shields.io/maven-metadata/v?label=Plugin&metadataUrl=https%3A//plugins.gradle.org/m2/com/graphfity/com.graphfity.gradle.plugin/maven-metadata.xml

[issues-shield]: https://img.shields.io/github/issues/ivancarras/graphfity.svg

[issues-url]: https://github.com/ivancarras/graphfity/issues

[license-shield]: https://img.shields.io/github/license/ivancarras/graphfity.svg

[license-url]: https://github.com/ivancarras/graphfity/blob/main/LICENSE

[contributors-shield]: https://img.shields.io/github/contributors/ivancarras/graphfity.svg

[contributors-url]: https://github.com/ivancarras/graphfity/graphs/contributors

[forks-shield]: https://img.shields.io/github/forks/ivancarras/graphfity.svg

[forks-url]: https://github.com/ivancarras/graphfity/network/members

[stars-shield]: https://img.shields.io/github/stars/ivancarras/graphfity.svg

[stars-url]: https://github.com/ivancarras/graphfity/stargazers

[linkedin-shield]: https://img.shields.io/badge/LinkedIn-0077B5?logo=linkedin&logoColor=white

[linkedin-url]: https://www.linkedin.com/in/iv%C3%A1n-carrasco-alonso-22a852119/
