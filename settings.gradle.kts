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
        //maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

rootProject.name = "tailrocks-marketplace"

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
