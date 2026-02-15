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
 * Convention plugin for PCRE4J JMH benchmark modules.
 *
 * Provides:
 *   - A 'jmh' source set with JMH core and annotation processor
 *   - A 'jmh' JavaExec task that runs org.openjdk.jmh.Main
 *   - Native library path forwarding (reuses pcre4j-native-test pattern)
 *   - FFM preview flags for Java 21 (required by the FFM backend)
 *
 * Does NOT apply maven-publish or jacoco — benchmarks are not published.
 */
plugins {
    java
    checkstyle
}

repositories {
    mavenCentral()
}

val libs = the<VersionCatalogsExtension>().named("libs")

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// ============================================================
// JMH source set
// ============================================================
val jmh: SourceSet by sourceSets.creating {
    java {
        srcDir("src/jmh/java")
    }
    resources {
        srcDir("src/jmh/resources")
    }
}

// JMH source set extends main's compile and runtime classpath
configurations[jmh.implementationConfigurationName].extendsFrom(configurations.implementation.get())
configurations[jmh.runtimeOnlyConfigurationName].extendsFrom(configurations.runtimeOnly.get())

dependencies {
    // JMH framework
    "jmhImplementation"(libs.findLibrary("jmh-core").get())
    "jmhAnnotationProcessor"(libs.findLibrary("jmh-generator-annprocess").get())
}

// ============================================================
// JMH compile configuration
// ============================================================
tasks.named<JavaCompile>("compileJmhJava") {
    // FFM backend requires --enable-preview on Java 21
    options.compilerArgs.add("--enable-preview")
}

// ============================================================
// JMH run task
// ============================================================
tasks.register<JavaExec>("jmh") {
    description = "Runs JMH benchmarks"
    group = "benchmark"

    mainClass = "org.openjdk.jmh.Main"
    classpath = jmh.runtimeClasspath

    // FFM backend requires --enable-preview on Java 21
    jvmArgs("--enable-preview")

    // Forward native library paths (same pattern as pcre4j-native-test)
    val pcre2LibraryPath = providers.systemProperty("pcre2.library.path").orNull
    systemProperty(
        "jna.library.path", listOfNotNull(
            pcre2LibraryPath,
            providers.systemProperty("jna.library.path").orNull
        ).joinToString(File.pathSeparator)
    )
    systemProperty(
        "java.library.path", listOfNotNull(
            pcre2LibraryPath,
            providers.systemProperty("java.library.path").orNull
        ).joinToString(File.pathSeparator)
    )

    val pcre2LibraryName = providers.systemProperty("pcre2.library.name").orNull
    if (pcre2LibraryName != null) {
        systemProperty("pcre2.library.name", pcre2LibraryName)
    }

    val pcre2FunctionSuffix = providers.systemProperty("pcre2.function.suffix").orNull
    if (pcre2FunctionSuffix != null) {
        systemProperty("pcre2.function.suffix", pcre2FunctionSuffix)
    }

    // Allow JMH args to be passed via -Pjmh.args="..."
    val jmhArgs = providers.gradleProperty("jmh.args").orNull
    if (jmhArgs != null) {
        args(jmhArgs.split(" "))
    }
}
