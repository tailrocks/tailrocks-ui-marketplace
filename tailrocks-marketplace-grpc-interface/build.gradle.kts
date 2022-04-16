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
    implementation(platform(marketplaceLibs.boms.grpc))
    compileOnly(platform(marketplaceLibs.boms.grpc))

    // gRPC
    api("io.grpc:grpc-protobuf")
    api("io.grpc:grpc-services")
    api("io.grpc:grpc-stub")
    api("io.grpc:grpc-netty")

    // PGV
    api(marketplaceLibs.pgv.java.stub)

    // Google
    api("com.google.api.grpc:proto-google-common-protos:2.3.2")

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
