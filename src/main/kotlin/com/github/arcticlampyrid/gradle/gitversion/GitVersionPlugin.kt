package com.github.arcticlampyrid.gradle.gitversion

import org.eclipse.jgit.api.Git
import org.gradle.api.Plugin
import org.gradle.api.Project

class GitVersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        if (project.version == Project.DEFAULT_VERSION) {
            kotlin.runCatching {
                val describe: String
                val isClean: Boolean
                Git.open(project.rootDir).use { git ->
                    describe = git.describe().apply {
                        setAlways(true)
                        setLong(false)
                        setTags(true)
                    }.call()
                    isClean = git.status().call().isClean
                }
                project.version = buildString {
                    describe
                        .removePrefix("v")
                        .removeSuffix("-release")
                        .let(::append)
                    if (!isClean) {
                        append("-dirty")
                    }
                }
            }.onFailure {
                println("Failed to detect version for ${project.name} based on git info")
                it.printStackTrace()
            }
        }
    }
}