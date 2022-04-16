plugins {
    id("com.github.johnrengelman.shadow") version Versions.gradleShadowPlugin
    id("io.micronaut.application") version Versions.gradleMicronautPlugin
    kotlin("jvm") version Versions.kotlin
    kotlin("kapt") version Versions.kotlin
    kotlin("plugin.allopen") version Versions.kotlin
}

micronaut {
    version(Versions.micronaut)
    runtime("netty")
    testRuntime("junit5")
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
    kapt(enforcedPlatform(marketplaceLibs.boms.micronaut))
    kapt("io.micronaut:micronaut-inject-java")
    kaptTest(enforcedPlatform(marketplaceLibs.boms.micronaut))
    kaptTest("io.micronaut:micronaut-inject-java")

    // Jambalaya
    implementation(marketplaceLibs.jambalaya.junit.opentelemetry)

    // PGV
    implementation(marketplaceLibs.pgv.java.grpc)

    // Kotest
    testImplementation(marketplaceLibs.kotest.assertions.core)

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    // Jackson
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
}

application {
    mainClass.set("com.tailrocks.example.api.ExampleApiApplication")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "15"
        javaParameters = true
    }
}

tasks {
    "run" { enabled = false }
    "runShadow" { enabled = false }
    "dockerBuild" { enabled = false }
    "dockerBuildNative" { enabled = false }
    "nativeImage" { enabled = false }
}
