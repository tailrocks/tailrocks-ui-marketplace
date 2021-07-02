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

    // Micronaut
    api("io.micronaut.grpc:micronaut-grpc-annotation")

    // PGV
    api("io.envoyproxy.protoc-gen-validate:pgv-java-grpc:${Versions.pgv}")

    // Jambalaya
    api("io.github.expatiat.jambalaya:jambalaya-tenancy:${Versions.jambalayaTenancy}")
    api("io.github.expatiat.jambalaya:jambalaya-tenancy-grpc-interface:${Versions.jambalayaTenancyGrpcInterface}")

    // Logback
    api("ch.qos.logback:logback-classic")
}
