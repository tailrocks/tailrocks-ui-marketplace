plugins {
    `java-library`
    `maven-publish`
    id("io.micronaut.library")
}

version = marketplaceLibs.versions.tailrocks.marketplace.asProvider().get()

micronaut {
    version(marketplaceLibs.versions.micronaut.get())
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
    api(marketplaceLibs.pgv.java.grpc)

    // Jambalaya
    api(marketplaceLibs.jambalaya.tenancy)
    api(marketplaceLibs.jambalaya.tenancy.grpc.api)
    api(marketplaceLibs.jambalaya.protobuf)

    // Logback
    api("ch.qos.logback:logback-classic")
}
