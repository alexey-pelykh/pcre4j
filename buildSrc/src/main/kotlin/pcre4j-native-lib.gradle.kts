/*
 * Copyright (C) 2026 Oleksii PELYKH
 *
 * This file is a part of the PCRE4J. The PCRE4J is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see
 * <https://www.gnu.org/licenses/>.
 */

/*
 * Convention plugin for PCRE4J native library bundle submodules.
 *
 * Centralizes shared configuration for modules that package a platform-specific
 * PCRE2 native library as a JAR resource under META-INF/native/{os}-{arch}/.
 *
 * Provides:
 *   - java-library and maven-publish plugins
 *   - Java 21 toolchain
 *   - Maven publication with artifactId = "pcre4j-native-{platform}"
 *   - No test dependencies (native bundles contain only resources)
 *   - No checkstyle (no source code to check)
 *   - `verifyNativeBundles` verification task (see issue #557)
 */

import java.util.zip.ZipFile

plugins {
    `java-library`
    `maven-publish`
}

version = findProperty("pcre4j.version") as String? ?: "0.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }

    withSourcesJar()
    withJavadocJar()
}

// Compute artifact name: :native:linux-x86_64 → pcre4j-native-linux-x86_64
val nativeArtifactId = "pcre4j-" + project.path
    .removePrefix(":")
    .replace(':', '-')

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = nativeArtifactId
        }
    }
}

// Release safety net: verify that platform-specific native bundle JARs actually
// contain the PCRE2 shared library before they can be staged for deploy.
// See issue #557.
//
// The :native:all module aggregates the 5 platform modules via `api` dependencies
// and does not bundle a library of its own, so it is excluded here.
if (project.name != "all") {
    val platform = project.name
    val expectedLibName = when {
        platform.startsWith("linux-") -> "libpcre2-8.so"
        platform.startsWith("macos-") -> "libpcre2-8.dylib"
        platform.startsWith("windows-") -> "pcre2-8.dll"
        else -> throw GradleException(
            "pcre4j-native-lib: unknown native platform '$platform' (expected linux-*, macos-*, or windows-*)"
        )
    }
    val expectedPath = "META-INF/native/$platform/$expectedLibName"
    val minSizeBytes = 10L * 1024L
    val projectPath = project.path

    // The task is invoked explicitly by .github/workflows/release.yaml before the
    // "Stage artifacts" step. It is intentionally NOT wired into the default `check`
    // or `publish*` task graphs, so it does not break CI snapshot staging for PRs
    // where the library-copy workflow step (see umbrella #556) has not run yet.
    tasks.register("verifyNativeBundles") {
        group = "verification"
        description = "Verifies the platform-specific native bundle JAR contains " +
            "the expected PCRE2 shared library of at least 10 KB."

        // The jar producer dependency is inferred via `inputs.file(jarFile)` below —
        // `jarFile` is a Provider<RegularFile> derived from the jar TaskProvider,
        // so Gradle automatically wires the task dependency.
        val jarFile = tasks.named<Jar>("jar").flatMap { it.archiveFile }
        inputs.file(jarFile)

        doLast {
            val jar = jarFile.get().asFile
            ZipFile(jar).use { zip ->
                val entry = zip.getEntry(expectedPath)
                    ?: throw GradleException(
                        "Native bundle verification failed for $projectPath: " +
                            "JAR ${jar.name} is missing expected library entry '$expectedPath'. " +
                            "The PCRE2 shared library was not copied into " +
                            "native/$platform/src/main/resources/$expectedPath before building. " +
                            "See .github/workflows/build-natives.yaml for the per-platform build/copy steps."
                    )
                if (entry.size < minSizeBytes) {
                    throw GradleException(
                        "Native bundle verification failed for $projectPath: " +
                            "entry '$expectedPath' in ${jar.name} is ${entry.size} bytes " +
                            "(expected >= $minSizeBytes). This indicates a placeholder file " +
                            "(e.g. .gitkeep) was bundled instead of the real PCRE2 library."
                    )
                }
                logger.lifecycle(
                    "Verified $projectPath: ${jar.name} contains $expectedPath (${entry.size} bytes)"
                )
            }
        }
    }
}
