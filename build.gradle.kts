plugins {
    kotlin("jvm") version "1.5.21"
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
