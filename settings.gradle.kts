apply(from = File(settingsDir, "gradle/repositoriesSettings.gradle.kts"))

dependencyResolutionManagement {
    versionCatalogs {
        create("marketplaceLibs") {
            from(files("gradle/libs.versions.toml"))
        }
    }
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
    ":tailrocks-marketplace-api-kotest",
    ":tailrocks-marketplace-api-test"
)
