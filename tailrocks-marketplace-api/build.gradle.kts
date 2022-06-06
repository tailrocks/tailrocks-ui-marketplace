plugins {
    id("io.micronaut.library")
}

micronaut {
    version(marketplaceLibs.versions.micronaut.get())
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

    api("io.micronaut.tracing:micronaut-tracing-opentelemetry:4.0.4-SNAPSHOT")
    api("io.micronaut.tracing:micronaut-tracing-annotation:4.0.4-SNAPSHOT")
    api("io.micronaut.tracing:micronaut-tracing-core:4.1.1")

    // MapStruct
    annotationProcessor(marketplaceLibs.mapstruct.processor)
    api(marketplaceLibs.mapstruct)

    // PGV
    api(marketplaceLibs.pgv.java.grpc)

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

    // OpenTelemetry
    // TODO cleanup
    api("io.opentelemetry:opentelemetry-sdk:1.14.0")
    api("io.opentelemetry:opentelemetry-sdk-trace:1.14.0")
    api("io.opentelemetry:opentelemetry-exporter-otlp-trace:1.14.0")
    api("io.opentelemetry.instrumentation:opentelemetry-jdbc:1.12.1-alpha")
}
