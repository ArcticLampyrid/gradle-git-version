import proguard.gradle.ProGuardTask

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.1.1")
    }
}

plugins {
    kotlin("jvm") version "1.6.10"
    `java-gradle-plugin`
    `maven-publish`
    signing
    id("com.gradle.plugin-publish") version "0.18.0"
}

group = "com.github.arcticlampyrid"
version = "1.0.4"

repositories {
    mavenCentral()
}

val shadowed: Configuration by configurations.creating {
    isCanBeResolved = true
}
configurations {
    compileOnly {
        extendsFrom(shadowed)
    }
    testImplementation {
        extendsFrom(shadowed)
    }
}

dependencies {
    implementation(gradleApi())
    @Suppress("GradlePackageUpdate")
    implementation("org.slf4j:slf4j-api:1.7.30")
    shadowed("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.10")
    @Suppress("GradlePackageUpdate")
    shadowed("org.eclipse.jgit:org.eclipse.jgit:5.13.0.202109080827-r") {
        exclude("org.slf4j", "slf4j-api")
    }
}

val minJar by tasks.creating(ProGuardTask::class)
val minJarPath = "$buildDir/libs/${project.name}-${project.version}-min.jar"
tasks {
    jar {
        archiveClassifier.set("unpacked")
    }
    withType<GenerateModuleMetadata> {
        enabled = false
    }
    minJar.apply {
        configuration("proguard-rules.pro")
        injars(jar)
        injars(mapOf(
            "filter" to "**.class"
        ), shadowed)
        outjars("$buildDir/libs/${project.name}-min.jar")
        val javaHome = System.getProperty("java.home")
        if (System.getProperty("java.version").startsWith("1.")) {
            // Before Java 9, the runtime classes were packaged in a single jar file.
            libraryjars("$javaHome/lib/rt.jar")
        } else {
            // As of Java 9, the runtime classes are packaged in modular jmod files.
            File(javaHome, "jmods").listFiles().orEmpty().forEach { file ->
                libraryjars(
                    mapOf(
                        "jarfilter" to "!**.jar",
                        "filter" to "!module-info.class"
                    ), file
                )
            }
        }
        libraryjars(configurations.compileClasspath.get().minus(shadowed.files))
    }
    pluginUnderTestMetadata {
        pluginClasspath.from.clear()
        pluginClasspath.from(minJar.libraryJarFiles, minJarPath)
    }
    whenTaskAdded {
        if (name == "publishPluginJar" || name == "generateMetadataFileForPluginMavenPublication") {
            dependsOn(minJar)
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            withType<MavenPublication> {
                if (name == "pluginMaven") {
                    setArtifacts(listOf(
                        artifact(minJarPath){
                            classifier = ""
                            extension = "jar"
                        }
                    ))
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