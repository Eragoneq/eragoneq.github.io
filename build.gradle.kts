plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
    application
}

group = "io.github.eragoneq"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    val kotlinxHtmlVersion = "0.12.0"
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinxHtmlVersion")
    implementation("com.github.ajalt.clikt:clikt:5.1.0")
    implementation("org.jetbrains:markdown:0.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.3")
    implementation("com.charleskorn.kaml:kaml:0.65.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}

kotlin {
    jvmToolchain(23)
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Jar>("fatJar") {
    archiveClassifier.set("all")
    manifest {
        attributes("Main-Class" to "MainKt")
    }
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    }) {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}