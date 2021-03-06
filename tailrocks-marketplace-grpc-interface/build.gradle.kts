import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    `java-library`
    id("com.google.protobuf")
    id("com.tailrocks.maven-publish")
}

version = marketplaceLibs.versions.tailrocks.marketplace.asProvider().get()

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    // BOM
    api(platform(marketplaceLibs.boms.grpc))

    // gRPC
    api("io.grpc:grpc-protobuf")
    api("io.grpc:grpc-services")
    api("io.grpc:grpc-stub")
    api("io.grpc:grpc-netty")

    // PGV
    api(marketplaceLibs.pgv.java.stub)

    // Google
    api(marketplaceLibs.proto.google.common.protos)

    // TODO remove me later
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${marketplaceLibs.versions.protobuf.get()}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${marketplaceLibs.versions.grpc.get()}"
        }
        id("javapgv") {
            artifact = "io.envoyproxy.protoc-gen-validate:protoc-gen-validate:${marketplaceLibs.versions.pgv.get()}"
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
