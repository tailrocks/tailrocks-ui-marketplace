plugins {
    java
    idea
    `maven-publish`
    id("com.diffplug.spotless") version Versions.gradleSpotlessPlugin
    id("com.gorylenko.gradle-git-properties") version Versions.gradleGitPropertiesPlugin apply false
}

val javaVersion = 16

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

allprojects {
    apply(plugin = "idea")
    apply(plugin = "com.diffplug.spotless")

    apply(from = "${project.rootDir}/gradle/dependencyUpdates.gradle.kts")

    group = "com.tailrocks.marketplace"

    idea {
        module {
            isDownloadJavadoc = false
            isDownloadSources = false
        }
    }

    spotless {
        java {
            licenseHeaderFile("$rootDir/gradle/licenseHeader.txt")
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
        }
        kotlin {
            licenseHeaderFile("$rootDir/gradle/licenseHeader.txt")
        }
        kotlinGradle {
            ktlint()
        }
    }
}

val publishingProjects = setOf(
    "tailrocks-marketplace-api-client",
    "tailrocks-marketplace-grpc-interface"
)

subprojects {
    apply(plugin = "java")
    if (publishingProjects.contains(project.name)) {
        apply(plugin = "java-library")
        apply(plugin = "maven-publish")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        }

        withJavadocJar()
        withSourcesJar()
    }

    if (publishingProjects.contains(project.name)) {
        publishing {
            publications {
                create<MavenPublication>("mavenJava") {
                    from(components["java"])
                    versionMapping {
                        allVariants {
                            fromResolutionResult()
                        }
                    }
                }
            }
        }
    }
}
