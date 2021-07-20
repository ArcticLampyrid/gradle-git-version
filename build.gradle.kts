import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.5.21"
    `java-gradle-plugin`
    `maven-publish`
    signing
    id("com.gradle.plugin-publish") version "0.14.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.github.arcticlampyrid"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.12.0.202106070339-r")
}

tasks.getByName<ShadowJar>("shadowJar") {
    archiveClassifier.set("")
}
tasks.getByName("publishPlugins").dependsOn("shadowJar")

pluginBundle {
    website = "https://github.com/ArcticLampyrid/gradle-git-version"
    vcsUrl = "https://github.com/ArcticLampyrid/gradle-git-version"
    tags = listOf("git", "describe", "version")
}

gradlePlugin {
    plugins {
        create("gradleGitVersion") {
            id = "com.github.arcticlampyrid.gradle-git-version"
            displayName = "GitVersionPlugin"
            description = "A plugin to set version based on git info"
            implementationClass = "com.github.arcticlampyrid.gradle.gitversion.GitVersionPlugin"
        }
    }
}

signing {
    isRequired = false
    findProperty("signingKey")?.let {
        useInMemoryPgpKeys(it.toString(), "")
    }
    sign(publishing.publications)
}