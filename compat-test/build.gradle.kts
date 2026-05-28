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
plugins {
    `java-library`
    checkstyle
    id("pcre4j-native-test")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)

    testImplementation(project(":regex"))
    testImplementation(project(":ffm"))
    testImplementation(project(":jna"))
}

configurations {
    implementation {
        resolutionStrategy.failOnVersionConflict()
    }
    testImplementation {
        resolutionStrategy.failOnVersionConflict()
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("--enable-preview")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("--enable-preview")
    val backend = (project.findProperty("compat.backend") as String?) ?: "ffm"
    systemProperty("pcre4j.test.backends", backend)
    maxHeapSize = (project.findProperty("compat.test.heapSize") as String?) ?: "4g"
    ignoreFailures = true
}

tasks.withType<Checkstyle>().configureEach {
    exclude("org/pcre4j/compat/imported/**")
}
