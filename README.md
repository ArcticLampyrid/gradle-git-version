# gradle-git-version
A plugin to set version based on git info  
The version is similar to `git describe --always --dirty --tags`, but removing the prefix `v` or/and the suffix `-release`  
Note: Nothing will happened if `version` is set before this plugin applied
## Usage
This plugin has released to Gradle Plugin Portal as [com.github.arcticlampyrid.gradle-git-version](https://plugins.gradle.org/plugin/com.github.arcticlampyrid.gradle-git-version)  
You can use the plugins DSL to include this plugin: 
### Groovy DSL
```groovy
plugins {
  id "com.github.arcticlampyrid.gradle-git-version" version "1.0.1"
}
```
### Kotlin DSL
```kotlin
plugins {
  id("com.github.arcticlampyrid.gradle-git-version") version "1.0.1"
}
```
