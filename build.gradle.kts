import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation

plugins {
    kotlin("jvm") version "1.6.10"
    `java-gradle-plugin`
    `maven-publish`
    signing
    id("com.gradle.plugin-publish") version "0.18.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.github.arcticlampyrid"
version = "1.0.2"

repositories {
    mavenCentral()
}

val shadowed: Configuration by configurations.creating
configurations {
    compileOnly {
        extendsFrom(shadowed)
    }
    testImplementation {
        extendsFrom(shadowed)
    }
}

dependencies {
    compileOnly(gradleApi())
    shadowed(kotlin("stdlib"))
    shadowed("org.eclipse.jgit:org.eclipse.jgit:6.0.0.202111291000-r")
}

tasks {
    val relocateShadowJar by registering(ConfigureShadowRelocation::class) {
        target = shadowJar.get()
        prefix = "com.github.arcticlampyrid.gradle.gitversion.shadow"
    }
    shadowJar {
        dependsOn(relocateShadowJar)
        configurations = listOf(shadowed)
        exclude("META-INF/maven/**", "META-INF/proguard/**", "META-INF/*.kotlin_module")
        manifest {
            attributes["Implementation-Version"] = project.version
        }
        archiveClassifier.set("")
    }
    jar {
        enabled = false
    }
    withType<GenerateModuleMetadata> {
        enabled = false
    }
    pluginUnderTestMetadata {
        pluginClasspath.from.clear()
        pluginClasspath.from(shadowJar)
    }
    whenTaskAdded {
        if (name == "publishPluginJar" || name == "generateMetadataFileForPluginMavenPublication") {
            dependsOn(shadowJar)
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            withType<MavenPublication> {
                if (name == "pluginMaven") {
                    setArtifacts(listOf(tasks.shadowJar.get()))
                }
            }
        }
    }
}

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