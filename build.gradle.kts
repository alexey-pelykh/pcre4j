plugins {
    base
    `jacoco-report-aggregation`
    alias(libs.plugins.jreleaser)
}

version = findProperty("pcre4j.version") as String? ?: "0.0.0-SNAPSHOT"

jreleaser {
    configFile = file("jreleaser.yml")
}

repositories {
    mavenCentral()
}

dependencies {
    jacocoAggregation(project(":api"))
    jacocoAggregation(project(":lib"))
    jacocoAggregation(project(":jna"))
    jacocoAggregation(project(":ffm"))
    jacocoAggregation(project(":regex"))
}

reporting {
    reports {
        register<JacocoCoverageReport>("jacocoAggregatedTestReport") {
            testSuiteName = "test"
        }
    }
}

// ==========================================================================
// Module dependency constraints
// Enforces: api ← lib ← backends (jna, ffm) ← regex
// ==========================================================================
val nativeModules = setOf(
    ":native:linux-x86_64",
    ":native:linux-aarch64",
    ":native:macos-x86_64",
    ":native:macos-aarch64",
    ":native:windows-x86_64"
)

val allowedProjectDependencies = mapOf(
    ":api" to emptySet(),
    ":lib" to setOf(":api"),
    ":jna" to setOf(":api"),
    ":ffm" to setOf(":api"),
    ":regex" to setOf(":api", ":lib"),
    ":native:linux-x86_64" to emptySet<String>(),
    ":native:linux-aarch64" to emptySet<String>(),
    ":native:macos-x86_64" to emptySet<String>(),
    ":native:macos-aarch64" to emptySet<String>(),
    ":native:windows-x86_64" to emptySet<String>(),
    ":native:all" to nativeModules
)

tasks.register("checkModuleDependencies") {
    description = "Validates module dependency constraints (api ← lib ← backends ← regex)"
    group = "verification"

    // Resolve all dependency data at configuration time into plain collections so the doLast
    // action captures only serializable values (compatible with the configuration cache)
    val checkedConfigurations = setOf("api", "implementation", "compileOnly", "compileOnlyApi")
    val allowed = allowedProjectDependencies.toMap()
    val actual = allowedProjectDependencies.keys.associate { modulePath ->
        modulePath to project(modulePath).configurations
            .filter { it.name in checkedConfigurations }
            .flatMap { config ->
                config.dependencies.filterIsInstance<ProjectDependency>()
                    .map { dep -> config.name to dep.path }
            }
    }

    doLast {
        val violations = mutableListOf<String>()
        allowed.forEach { (modulePath, allowedDeps) ->
            actual[modulePath]?.forEach { (configName, depPath) ->
                if (depPath !in allowedDeps) {
                    violations.add(
                        "$modulePath → $depPath (via '$configName') " +
                            "violates allowed dependencies: $allowedDeps"
                    )
                }
            }
        }
        if (violations.isNotEmpty()) {
            throw GradleException(
                "Module dependency constraint violations:\n" +
                    violations.joinToString("\n") { "  - $it" }
            )
        }
        logger.lifecycle("Module dependency constraints verified: all modules comply")
    }
}

tasks.named("check") {
    dependsOn("checkModuleDependencies")
}

// ==========================================================================
// Shared POM metadata for all published modules
// ==========================================================================
subprojects {
    tasks.withType<Jar>().configureEach {
        if (name == "sourcesJar") {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
    }

    pluginManager.withPlugin("maven-publish") {
        configure<PublishingExtension> {
            publications.withType<MavenPublication> {
                groupId = "org.pcre4j"
                version = project.version.toString()

                pom {
                    url = "https://pcre4j.org"

                    licenses {
                        license {
                            name = "GNU Lesser General Public License v3.0"
                            url = "https://www.gnu.org/licenses/lgpl-3.0.txt"
                        }
                    }
                    developers {
                        developer {
                            name = "Alexey Pelykh"
                            email = "alexey.pelykh@gmail.com"
                            organization = "The PCRE4J Project"
                            organizationUrl = "https://pcre4j.org"
                        }
                    }
                    scm {
                        connection = "scm:git:git://github.com/alexey-pelykh/pcre4j.git"
                        developerConnection = "scm:git:ssh://github.com:alexey-pelykh/pcre4j.git"
                        url = "https://github.com/alexey-pelykh/pcre4j"
                    }
                }
            }

            repositories {
                maven {
                    name = "StagingDeploy"
                    url = uri(layout.buildDirectory.dir("staging-deploy"))
                }
            }
        }
    }
}
