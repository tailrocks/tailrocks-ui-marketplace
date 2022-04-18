plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen")

    id("com.github.johnrengelman.shadow")
    id("io.micronaut.application")
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

    // Jambalaya
    implementation(marketplaceLibs.jambalaya.junit.opentelemetry)

    // PGV
    implementation(marketplaceLibs.pgv.java.grpc)

    // Kotest
    testImplementation("io.kotest:kotest-assertions-core-jvm")

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    // Jackson
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
}

application {
    mainClass.set("com.tailrocks.example.api.ExampleApiApplication")
}
