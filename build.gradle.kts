plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("kapt") version "1.6.21"
    kotlin("plugin.allopen") version "1.6.21"

    // TODO temp workaround: https://github.com/micronaut-projects/micronaut-gradle-plugin/issues/363
    id("com.bmuschko.docker-remote-api") version "7.4.0"

    // https://plugins.gradle.org/plugin/com.tailrocks.java
    id("com.tailrocks.java") version "0.1.4"

    // https://plugins.gradle.org/plugin/com.tailrocks.spotless
    id("com.tailrocks.spotless") version "0.1.4"

    // https://plugins.gradle.org/plugin/com.tailrocks.idea
    id("com.tailrocks.idea") version "0.1.3"

    // https://plugins.gradle.org/plugin/com.tailrocks.versions
    id("com.tailrocks.versions") version "0.1.4"

    // https://plugins.gradle.org/plugin/com.tailrocks.maven-publish
    id("com.tailrocks.maven-publish") version "0.1.6" apply false

    // https://plugins.gradle.org/plugin/com.gorylenko.gradle-git-properties
    id("com.gorylenko.gradle-git-properties") version "2.4.1" apply false

    // https://plugins.gradle.org/plugin/io.micronaut.library
    id("io.micronaut.library") version "3.4.1" apply false

    // https://plugins.gradle.org/plugin/io.micronaut.application
    id("io.micronaut.application") version "3.4.1" apply false

    // https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false

    // https://plugins.gradle.org/plugin/com.google.protobuf
    id("com.google.protobuf") version "0.8.19" apply false

    // https://plugins.gradle.org/plugin/com.adarshr.test-logger
    id("com.adarshr.test-logger") version "3.2.0" apply false
}

allprojects {
    apply(plugin = "com.tailrocks.idea")
    apply(plugin = "com.tailrocks.spotless")
    apply(plugin = "com.tailrocks.versions")

    group = "com.tailrocks.marketplace"

    spotless {
        java {
            licenseHeaderFile("$rootDir/gradle/licenseHeader.txt")
        }
        kotlin {
            licenseHeaderFile("$rootDir/gradle/licenseHeader.txt")
        }
    }
}
