plugins {
    id("io.micronaut.library")
    id("com.tailrocks.maven-publish")
}

version = marketplaceLibs.versions.tailrocks.marketplace.asProvider().get()

java {
    withJavadocJar()
    withSourcesJar()
}

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
