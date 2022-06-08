plugins {
    id("com.github.johnrengelman.shadow")
    id("io.micronaut.application")
}

micronaut {
    version(marketplaceLibs.versions.micronaut.get())
    runtime("netty")
    enableNativeImage(true)
    processing {
        incremental(true)
        annotations("com.tailrocks.marketplace.api.*")
    }
}

dependencies {
    // subprojects
    implementation(project(":tailrocks-marketplace-api"))

    // GraalVM
    compileOnly("org.graalvm.nativeimage:svm")
}

application {
    mainClass.set("com.tailrocks.marketplace.api.MarketplaceApiApplication")
}

graalvmNative.toolchainDetection.set(false)

tasks {
    dockerfile {
        baseImage("eclipse-temurin:17-jammy")
    }
}
