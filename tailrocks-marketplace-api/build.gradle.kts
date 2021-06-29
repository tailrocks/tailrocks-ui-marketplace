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
    api("com.tailrocks.domain:tailrocks-marketplace-jooq:${Versions.tailrocksMarketplaceJooq}")

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

    // TODO replace with final version
    api("io.micronaut.opentelemetry:micronaut-opentelemetry:1.0.0-SNAPSHOT")

    // MapStruct
    annotationProcessor("org.mapstruct:mapstruct-processor:${Versions.mapstruct}")
    api("org.mapstruct:mapstruct:${Versions.mapstruct}")

    // PGV
    api("io.envoyproxy.protoc-gen-validate:pgv-java-grpc:${Versions.pgv}")

    // Jambalaya
    annotationProcessor("io.github.expatiat.jambalaya:jambalaya-mapstruct-processor:${Versions.jambalayaMapstructProcessor}")
    api("io.github.expatiat.jambalaya:jambalaya-checks:${Versions.jambalayaChecks}")
    api("io.github.expatiat.jambalaya:jambalaya-protobuf:${Versions.jambalayaProtobuf}")
    api("io.github.expatiat.jambalaya:jambalaya-micronaut-mapstruct-protobuf:${Versions.jambalayaMicronautMapstructProtobuf}")
    api("io.github.expatiat.jambalaya:jambalaya-opentelemetry:${Versions.jambalayaOpentelemetry}")
    api("io.github.expatiat.jambalaya:jambalaya-tenancy:${Versions.jambalayaTenancy}")

    // Logback
    api("ch.qos.logback:logback-classic")
}
