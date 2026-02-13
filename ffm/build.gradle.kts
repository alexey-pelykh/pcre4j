/*
 * Copyright (C) 2024-2026 Oleksii PELYKH
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
plugins {
    `java-library`
    checkstyle
    `maven-publish`
    jacoco
}

version = findProperty("pcre4j.version") as String? ?: "0.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":api"))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(project(":lib"))
    testImplementation(testFixtures(project(":lib")))
    testRuntimeOnly(libs.junit.platform.launcher)
}

configurations {
    implementation {
        resolutionStrategy.failOnVersionConflict()
    }
}

// ============================================================
// MRJAR: Java 22+ source set for finalized FFM API
// ============================================================
val java22: SourceSet by sourceSets.creating {
    java {
        // Java 22-specific ArenaHelper.java only
        srcDir("src/main/java22")
    }
}

// Java 22 source set inherits main's dependencies
configurations["java22Implementation"].extendsFrom(configurations.implementation.get())
configurations["java22CompileOnly"].extendsFrom(configurations.compileOnly.get())

dependencies {
    // Java 22 source set only has ArenaHelper.java; Pcre2.java is shared from main
    "java22Implementation"(project(":api"))
}

// ============================================================
// Java version configuration - Java 21 as base (with preview)
// ============================================================
// The Foreign Function & Memory (FFM) API was a preview feature in Java 21
// and became GA in Java 22. Since this module targets Java 21 as the base,
// --enable-preview is required for:
//   - compileJava / compileTestJava: compiler must accept preview API usage
//   - test JVM args: runtime must enable preview features
//   - javadoc: must recognize preview API references
// The java22 source set targets Java 22+ where FFM is GA, so no preview
// flag is needed there.
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }

    withSourcesJar()
    withJavadocJar()
}

// Compile main (Java 21) with preview features
tasks.compileJava {
    options.compilerArgs.add("--enable-preview")
}

// Compile tests (Java 21) with preview features
tasks.named<JavaCompile>("compileTestJava") {
    options.compilerArgs.add("--enable-preview")
}

// Compile java22 source set with Java 22 toolchain (no preview needed)
tasks.named<JavaCompile>("compileJava22Java") {
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(22))
    })
    // Include Pcre2.java from main source (avoids duplication)
    // ArenaHelper.java comes from src/main/java22 with Java 22-specific implementation
    source(fileTree("src/main/java") {
        include("**/Pcre2.java")
    })
}

// ============================================================
// Multi-Release JAR configuration
// ============================================================
tasks.jar {
    manifest {
        attributes("Multi-Release" to "true")
    }
    // Include Java 22 classes under META-INF/versions/22/
    into("META-INF/versions/22") {
        from(java22.output)
    }
}

// ============================================================
// Testing configuration
// ============================================================

// Test with Java 21 (preview features)
tasks.test {
    useJUnitPlatform()
    jvmArgs("--enable-preview")

    systemProperty(
        "java.library.path", listOf(
            providers.systemProperty("pcre2.library.path").orNull,
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

    finalizedBy(tasks.jacocoTestReport)
}

// ============================================================
// Java 22+ Testing (MRJAR verification)
// ============================================================

// Create a separate source set for Java 22 test compilation
// This reuses the same test sources but compiles with Java 22 (no preview needed)
val testJava22Classes: SourceSet by sourceSets.creating {
    java {
        srcDir("src/test/java")
    }
}

// Configure testJava22Classes compilation with Java 22 toolchain
configurations["testJava22ClassesImplementation"].extendsFrom(configurations.testImplementation.get())
configurations["testJava22ClassesRuntimeOnly"].extendsFrom(configurations.testRuntimeOnly.get())

dependencies {
    // Java 22 test classes need to see the main JAR (for MRJAR resolution)
    "testJava22ClassesImplementation"(platform(libs.junit.bom))
    "testJava22ClassesImplementation"(project(":api"))
    "testJava22ClassesImplementation"(project(":lib"))
    "testJava22ClassesImplementation"(testFixtures(project(":lib")))
    "testJava22ClassesImplementation"(libs.junit.jupiter)
}

// Compile test sources with Java 22 (no preview needed since FFM is GA)
val compileTestJava22ClassesJava by tasks.named<JavaCompile>("compileTestJava22ClassesJava") {
    dependsOn("compileJava22Java")
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(22))
    })
    // Use Java 22 classes directly (MRJAR is runtime-only, not compile-time)
    // Overlay Java 22 classes on top of main classes (Java 22 takes precedence)
    classpath = files(java22.output.classesDirs) +
            files(sourceSets.main.get().output.classesDirs) +
            configurations["testJava22ClassesCompileClasspath"]
}

// Test with Java 22+ (test MRJAR resolution)
val testJava22 by tasks.registering(Test::class) {
    description = "Runs tests with Java 22+ runtime to verify MRJAR resolution"
    group = "verification"

    dependsOn(tasks.jar)
    dependsOn(compileTestJava22ClassesJava)
    useJUnitPlatform()

    // Use configurable Java launcher (defaults to 22, can be overridden via -PtestJavaVersion=25)
    val testJavaVersion = (findProperty("testJavaVersion") as String?)?.toInt() ?: 22
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(testJavaVersion))
    })

    // Use Java 22-compiled test classes
    testClassesDirs = testJava22Classes.output.classesDirs
    // Test against the JAR (not classes dir) to trigger MRJAR class selection
    classpath = files(tasks.jar.get().archiveFile) +
            testJava22Classes.output +
            configurations["testJava22ClassesRuntimeClasspath"]

    systemProperty(
        "java.library.path", listOf(
            providers.systemProperty("pcre2.library.path").orNull,
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
}

// Run both test suites during check
tasks.check {
    dependsOn(testJava22)
}

// ============================================================
// JaCoCo configuration
// ============================================================
tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required = true
        html.required = true
    }
}

// ============================================================
// Sources JAR includes both source sets
// ============================================================
tasks.named<Jar>("sourcesJar") {
    // Include Java 22-specific ArenaHelper.java
    from(java22.allSource) {
        into("META-INF/versions/22")
    }
    // Include shared Pcre2.java (same source, compiled for Java 22)
    from(fileTree("src/main/java") {
        include("**/Pcre2.java")
    }) {
        into("META-INF/versions/22")
    }
}

// ============================================================
// Javadoc configuration (Java 21 base with preview)
// ============================================================
tasks.withType<Javadoc> {
    val javadocOptions = options as CoreJavadocOptions

    javadocOptions.addStringOption("source", "21")
    javadocOptions.addBooleanOption("-enable-preview", true)
}

// ============================================================
// Publishing
// ============================================================
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = project.name

            pom {
                name = "PCRE4J FFM Backend"
                description = "PCRE4J FFM Backend"
            }
        }
    }
}
