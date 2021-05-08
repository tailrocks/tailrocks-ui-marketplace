plugins {
    id("com.github.johnrengelman.shadow") version Versions.gradleShadowPlugin
    id("io.micronaut.application") version Versions.gradleMicronautPlugin
}

micronaut {
    version(Versions.micronaut)
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
}

application {
    mainClass.set("com.tailrocks.marketplace.api.MarketplaceApiApplication")
}
