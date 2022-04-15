import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    `java-library`
    `maven-publish`
    id("com.google.protobuf") version Versions.gradleProtobufPlugin
}

version = Versions.tailrocksMarketplace

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    // BOM
    implementation(platform("io.grpc:grpc-bom:${Versions.grpc}"))
    compileOnly(platform("io.grpc:grpc-bom:${Versions.grpc}"))

    // gRPC
    api("io.grpc:grpc-protobuf")
    api("io.grpc:grpc-services")
    api("io.grpc:grpc-stub")
    api("io.grpc:grpc-netty")

    // PGV
    api("io.envoyproxy.protoc-gen-validate:pgv-java-stub:${Versions.pgv}")

    // Google
    api("com.google.api.grpc:proto-google-common-protos:2.3.2")

    // TODO remove me later
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${Versions.protobuf}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${Versions.grpc}"
        }
        id("javapgv") {
            artifact = "io.envoyproxy.protoc-gen-validate:protoc-gen-validate:${Versions.pgv}"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("javapgv") { option("lang=java") }
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDirs(
                "${protobuf.protobuf.generatedFilesBaseDir}/main/grpc",
                "${protobuf.protobuf.generatedFilesBaseDir}/main/java"
            )
        }
    }
}
