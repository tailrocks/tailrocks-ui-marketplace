plugins {
    id("io.micronaut.library") version Versions.gradleMicronautPlugin
}

micronaut {
    version(Versions.micronaut)
    runtime("netty")
    processing {
        incremental(true)
        annotations("com.tailrocks.marketplace.api.*")
    }
}

dependencies {
    // subprojects
    api(project(":tailrocks-marketplace-grpc-interface"))

    // tailrocks
    api(marketplaceLibs.tailrocks.marketplace.jooq)

    // Micronaut
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    api("io.micronaut:micronaut-runtime")
    api("io.micronaut:micronaut-management")
    api("io.micronaut:micronaut-http-client")
    api("io.micronaut.grpc:micronaut-grpc-server-runtime")
    api("io.micronaut.flyway:micronaut-flyway")
    api("io.micronaut.sql:micronaut-jdbc-hikari")
    api("io.micronaut.sql:micronaut-jooq")
    api("io.micronaut.data:micronaut-data-tx")

    // MapStruct
    annotationProcessor(marketplaceLibs.mapstruct.processor)
    api(marketplaceLibs.mapstruct)

    // PGV
    api(marketplaceLibs.pgv.java.grpc)

    // BSON
    api(marketplaceLibs.bson)

    // Jambalaya
    annotationProcessor(marketplaceLibs.jambalaya.mapstruct.processor)
    api(marketplaceLibs.jambalaya.checks)
    api(marketplaceLibs.jambalaya.protobuf)
    api(marketplaceLibs.jambalaya.micronaut.mapstruct.protobuf)
    api(marketplaceLibs.jambalaya.opentelemetry)
    api(marketplaceLibs.jambalaya.seo)
    api(marketplaceLibs.jambalaya.tenancy)
    api(marketplaceLibs.jambalaya.tenancy.flyway)
    api(marketplaceLibs.jambalaya.tenancy.grpc.api)
    api(marketplaceLibs.jambalaya.tenancy.jooq)

    // Logback
    api("ch.qos.logback:logback-classic")

    // TODO remove me later
    api("javax.inject:javax.inject:1")
}
