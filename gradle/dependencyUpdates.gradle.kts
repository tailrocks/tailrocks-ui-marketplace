import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.github.ben-manes:gradle-versions-plugin:${Versions.gradleVersionsPlugin}")
    }
}

apply<com.github.benmanes.gradle.versions.VersionsPlugin>()

tasks.getByName<DependencyUpdatesTask>("dependencyUpdates") {
    resolutionStrategy {
        componentSelection {
            all {
                var rejected = listOf("alpha", "beta", "rc", "cr", "m")
                    .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-]*") }
                    .any { it.matches(candidate.version) }

                if (candidate.version.toLowerCase().contains("alpha") ||
                    candidate.version.toLowerCase().contains("beta") ||
                    candidate.version.toLowerCase().contains("snapshot")
                ) {
                    rejected = true
                }

                if (!rejected) {
                    if (candidate.group == "javax.xml.bind" &&
                        candidate.module == "jaxb-api" &&
                        candidate.version.contains("-b")
                    ) {
                        rejected = true
                    }
                }
                if (rejected) {
                    reject("Release candidate")
                }
            }
        }
    }
}
