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
    implementation(libs.jna.platform)
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

    systemProperty(
        "jna.library.path", listOf(
            providers.systemProperty("pcre2.library.path").orNull,
            providers.systemProperty("jna.library.path").orNull
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
                name = "PCRE4J JNA Backend"
                description = "PCRE4J JNA Backend"
            }
        }
    }
}
