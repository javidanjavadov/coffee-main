plugins {
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "2.3.7"
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.motycka.edu"
version = "0.0.1"

application {
    mainClass.set("com.motycka.edu.ApplicationKt")
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}
