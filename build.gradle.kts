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
val allowedProjectDependencies = mapOf(
    ":api" to emptySet(),
    ":lib" to setOf(":api"),
    ":jna" to setOf(":api"),
    ":ffm" to setOf(":api"),
    ":regex" to setOf(":api", ":lib")
)

tasks.register("checkModuleDependencies") {
    description = "Validates module dependency constraints (api ← lib ← backends ← regex)"
    group = "verification"

    doLast {
        val violations = mutableListOf<String>()
        allowedProjectDependencies.forEach { (modulePath, allowed) ->
            val subproject = project(modulePath)
            subproject.configurations
                .filter { it.name in setOf("api", "implementation", "compileOnly", "compileOnlyApi") }
                .forEach { config ->
                    config.dependencies.filterIsInstance<ProjectDependency>().forEach { dep ->
                        val depPath = dep.path
                        if (depPath !in allowed) {
                            violations.add(
                                "$modulePath → $depPath (via '${config.name}') " +
                                    "violates allowed dependencies: $allowed"
                            )
                        }
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
