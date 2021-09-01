
<br/>
<p align="center">
  <a href="https://github.com/ivancarras/graphfity">
    <img src="https://user-images.githubusercontent.com/23535893/131651895-b9c82af7-7ad3-4922-a7f9-c82fbbaa4c52.png" height="%40" width="40%" >
  </a>
</p>

<p align="center">
  <b>Graphfity</b> creates a dependency nodes diagram graph about your internal modules dependencies, specially useful if you are developing a <b>multi-module application</b>
  <br />
  <br />
  <a href="https://camo.githubusercontent.com/ccc47017152f0c4505623e9ddceedb1a95f5e8c8975344ecbc2d59915b8b6cd7/68747470733a2f2f64726976652e676f6f676c652e636f6d2f75633f6578706f72743d766965772669643d312d71774d4b6b4e4c66687745327065494e72584277687143336772614e745975">View Demo</a>
  ·
  <a href="https://github.com/ivancarras/graphfity/issues">Report Bug</a>
  ·
  <a href="https://github.com/ivancarras/graphfity/issues">Request Feature</a>
</p>
<br/>
<p align="center">
  <a href="https://plugins.gradle.org/plugin/com.github.ivancarras.graphfity">
    <img src="https://img.shields.io/maven-metadata/v?label=Plugin&metadataUrl=https%3A//plugins.gradle.org/m2/com/graphfity/com.graphfity.gradle.plugin/maven-metadata.xml"/>
  </a>
  <a href=" https://github.com/ivancarras/graphfity/blob/main/LICENSE">
    <img src="https://img.shields.io/github/license/ivancarras/graphfity.svg"/>
  </a>
  <a href="https://github.com/ivancarras/graphfity/graphs/contributors">
    <img src="https://img.shields.io/github/contributors/ivancarras/graphfity.svg"/>
  </a>
  <a href="https://github.com/ivancarras/graphfity/network/members">
    <img src="https://img.shields.io/github/forks/ivancarras/graphfity.svg"/>
  </a>
  <a href="https://github.com/ivancarras/graphfity/issues">
    <img src="https://img.shields.io/github/issues/ivancarras/graphfity.svg"/>
  </a>
  <a href="https://github.com/ivancarras/graphfity/stargazers">
    <img src="https://img.shields.io/github/stars/ivancarras/graphfity.svg"/>
  </a>
</p>

<br/>


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
        <li><a href="#installation">Installation</a></li>
        <li><a href="#plugin-configuration">Plugin configuration</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->

## About The Project

![product-image](https://user-images.githubusercontent.com/23535893/131650974-228a131e-81b1-40cd-b2f9-fe6d41777d23.png)

As a software engineer, you should know how difficult is to maintain a project without a previous initial good
architecture. The project scales, new libraries, new features, new dependencies between the internal modules
are added...

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

### Installation

**Groovy DSL**

*root build.gradle*

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

*root build.gradle.kts*

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

- App
- Features
- Components
- Libraries
- Core

<p>
    <img src="https://user-images.githubusercontent.com/23535893/131650988-8865b419-22ba-4eb1-868b-f0780ed04f58.png" height="70%" width="70%">
</p>

*nodeTypes.json*

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

**Node explanation**

```
{
"name": "App", //Node name
"regex": "^:app$", //This regex corresponds to the modules which will be draw as this node type
"isEnabled": true, //Enable o disable the visualization of this node
"shape": "box3d", // Graphviz node shape you can choose another one using: https://graphviz.org/doc/info/shapes.html
"fillColor": "#BAFFC9"//Hexadecimal color for these nodes
}
```

**Copy this initial configuration file in an accessible path in your project. (the root path is perfect)**

Now is time to configure the plugin:

**Groovy DSL**

``` groovy
graphfityExtension {
  nodeTypesPath = "<nodesTypes.json>" //(mandatory) Examples: graphfityConfig/nodesTypes.json establish the route to your nodeTypes.json
  projectRootName = "<projectNameToAnalise>" //(optional) Examples: ":app", ":feature:wishlist"... is up to you
  graphImagePath = "<graphsFolder>" //(optional)the folder where will be placed your graph.png image
}
```

**Kotlin DSL**

``` kotlin
configure<GraphfityPluginExtension> {
  nodeTypesPath.set("<nodesTypes.json>") //(mandatory) Examples: graphfityConfig/nodesTypes.json establish the route to your nodeTypes.json
  projectRootName.set("<projectNameToAnalise>") //(optional) Examples: ":app", ":feature:wishlist"... is up to you
  graphImagePath.set("<graphsFolder>") //(optional)the folder where will be placed your graph.png image
}
```

## Usage

When your configuration is done now you can execute:

#### Mac 🍏 & Linux 🐧

```shell
./gradlew graphfity
```

#### Windows 🪟

```shell
gradle graphfity
```

The graph is going to be generated in the respective **graphImagePath** defined in the configuration

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

[linkedin-shield]: https://img.shields.io/badge/LinkedIn-0077B5?logo=linkedin&logoColor=white

[linkedin-url]: https://www.linkedin.com/in/iv%C3%A1n-carrasco-alonso-22a852119/
