plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
    application
}

group = "io.github.eragoneq"
version = "1.0"

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
    implementation("org.jetbrains:markdown:0.7.9")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.11.0")
    implementation("com.charleskorn.kaml:kaml:0.104.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
}

kotlin {
    jvmToolchain(23)
}

tasks.test {
    useJUnitPlatform()
}

val generatedSiteDir = layout.buildDirectory.dir("generated-site")
val publicSiteDir = layout.projectDirectory.dir("public")
val siteConfigFile = layout.projectDirectory.file("site.yml")

tasks.register<JavaExec>("generateProductionSource") {
    group = "application"
    description = "Generates the unminified site consumed by the production asset pipeline."
    dependsOn(tasks.named("classes"))

    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set(application.mainClass)
    args(
        "generate",
        "--output", generatedSiteDir.get().asFile.absolutePath,
        "--config", siteConfigFile.asFile.absolutePath,
        "--strict",
    )

    inputs.dir(layout.projectDirectory.dir("content"))
    inputs.dir(layout.projectDirectory.dir("static"))
    inputs.file(siteConfigFile)
    outputs.dir(generatedSiteDir)
    outputs.upToDateWhen { false }

    doFirst {
        delete(generatedSiteDir.get().asFile)
    }
}

tasks.register<JavaExec>("validatePublicSite") {
    group = "verification"
    description = "Checks generated HTML links and verifies the sitemap and RSS XML."
    dependsOn(tasks.named("classes"))

    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set(application.mainClass)
    args(
        "validate",
        "--site", publicSiteDir.asFile.absolutePath,
        "--config", siteConfigFile.asFile.absolutePath,
    )

    inputs.dir(publicSiteDir)
    inputs.file(siteConfigFile)
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
