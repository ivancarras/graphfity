[![Plugin][plugin-shield]][plugin-url]
[![MIT License][license-shield]][license-url]
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]


<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/ivancarras/graphfity">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a>

<h1 align="center">Graphfity</h1>

  <p align="center">
    <b>Graphfity</b> creates a dependency node diagram graph about your internal modules dependencies, specially useful if you are developing a <b>multi-modular application<b></b>
    <br />
    <br />
    <br />
    <a href="https://github.com/ivanca0rras/graphfity">View Demo</a>
    ·
    <a href="https://github.com/ivancarras/graphfity/issues">Report Bug</a>
    ·
    <a href="https://github.com/ivancarras/graphfity/issues">Request Feature</a>
  </p>
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
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgements">Acknowledgements</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->

## About The Project

![product-image](https://raw.githubusercontent.com/ivancarras/graphfity/feature/readme-creation/graphfity_example.png)

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

#### Graphviz setup (https://graphviz.org/download/)

- Mac 🍏

  ```sh
  Macports:
  sudo port install graphviz
  
  or 
  
  Howebrew:
  brew install graphviz
  ```

- Windows 🪟
  ```sh
  Winget:
  winget install graphviz
  
  or
  
  Choco: 
  choco install graphviz
  ```

- Linux 🐧
   ```sh
  Ubuntu packages:
  sudo apt install graphviz
  
  or

  Fedora project: 
  sudo yum install graphviz
  
  or

  Debian packages:
  sudo apt install graphviz
  ```

### Configuration

#### Plugin addition

- Groovy DSL

  root build.gradle

    ```
    plugins {
    id "com.github.ivancarras.graphfity" version "1.0.0"
    }
  ```

  root build.gradle

    ```
    buildscript {
    repositories {
      maven {
        url "https://plugins.gradle.org/m2/"
      }
    }
    dependencies {
      classpath com.github.ivancarras.graphfity.graphfity-plugin:'1.0.0'
      }
    }
  
    apply plugin: com.github.ivancarras.graphfity.plugin.main.GraphfityPlugin
    ```

- Kotlin DSL

  root build.gradle.kts

    ```
    plugins {
      id("com.github.ivancarras.graphfity") version "1.0.0"
    }
    ```
  root build.gradle.kts
    ```
    buildscript {
      repositories {
        maven {
          url = uri("https://plugins.gradle.org/m2/")
        }
      }
      dependencies {
         classpath ('com.github.ivancarras.graphfity.GraphfityPlugin:'1.0.0')
      }
    }
    
    apply(plugin = "com.github.ivancarras.graphfity.plugin.main.GraphfityPlugin")
    ```

#### Plugin configuration

Default values json file Graphviz figures guide

<!-- USAGE EXAMPLES -->

## Usage





<!-- ROADMAP -->

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

Distributed under the MIT License. See `LICENSE` for more information.



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