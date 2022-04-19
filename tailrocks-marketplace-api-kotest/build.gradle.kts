plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen")

    id("com.github.johnrengelman.shadow")
    id("io.micronaut.application")
    id("com.adarshr.test-logger")
}

micronaut {
    version(marketplaceLibs.versions.micronaut.get())
    runtime("netty")
    testRuntime("kotest")
    enableNativeImage(false)
    processing {
        incremental(true)
        annotations("com.tailrocks.marketplace.api.*")
    }
}

dependencies {
    // subprojects
    implementation(project(":tailrocks-marketplace-api"))
    implementation(project(":tailrocks-marketplace-api-client"))

    // Micronaut
    implementation("io.micronaut.grpc:micronaut-grpc-client-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")

    // PGV
    implementation(marketplaceLibs.pgv.java.grpc)

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    // Jackson
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
}

application {
    mainClass.set("com.tailrocks.example.api.ExampleApiApplication")
}
