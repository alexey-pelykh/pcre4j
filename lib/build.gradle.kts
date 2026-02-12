/*
 * Copyright (C) 2024 Oleksii PELYKH
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
    `java-test-fixtures`
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
    testFixturesApi(libs.junit.jupiter)
    // Runtime-only: lib tests discover backends reflectively to avoid compile-time coupling
    testRuntimeOnly(project(":jna"))
    testRuntimeOnly(project(":ffm"))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

configurations {
    implementation {
        resolutionStrategy.failOnVersionConflict()
    }
}

sourceSets {
    main {
        java.srcDir("src/main/java")
    }
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

tasks.test {
    useJUnitPlatform()
    jvmArgs("--enable-preview")

    systemProperty(
        "jna.library.path", listOf(
            providers.systemProperty("pcre2.library.path").orNull,
            providers.systemProperty("jna.library.path").orNull
        ).joinToString(":")
    )

    systemProperty(
        "java.library.path", listOf(
            providers.systemProperty("pcre2.library.path").orNull,
            providers.systemProperty("java.library.path").orNull
        ).joinToString(":")
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

tasks.named<JavaCompile>("compileTestJava") {
    options.compilerArgs.add("--enable-preview")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required = true
        html.required = true
    }
}

tasks.named<Jar>("sourcesJar") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = project.name

            pom {
                name = "org.pcre4j:${project.name}"
                description = "PCRE4J Library"
            }
        }
    }
}
