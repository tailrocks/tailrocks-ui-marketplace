pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        // uncomment if you need to use snapshot versions
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

rootProject.name = "tailrocks-ui-marketplace"

include(
    // libraries
    ":tailrocks-marketplace-api",
    ":tailrocks-marketplace-api-client",
    ":tailrocks-marketplace-grpc-interface",

    // apps
    ":tailrocks-marketplace-api-app",

    // tests
    ":tailrocks-marketplace-api-test"
)
