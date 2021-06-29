plugins {
    `java-library`
    `maven-publish`
    id("io.micronaut.library") version Versions.gradleMicronautPlugin
}

version = Versions.tailrocksMarketplace

micronaut {
    version(Versions.micronaut)
    processing {
        incremental(true)
        annotations("com.tailrocks.marketplace.api.*")
    }
}

dependencies {
    // subprojects
    api(project(":tailrocks-marketplace-grpc-interface"))

    // Jambalaya
    api("io.github.expatiat.jambalaya:jambalaya-tenancy:${Versions.jambalayaTenancy}")

    // Logback
    api("ch.qos.logback:logback-classic")
}
