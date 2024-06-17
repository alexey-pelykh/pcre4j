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
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":api"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation(project(":lib"))
    testImplementation(project(":test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("--enable-preview")
    systemProperty(
        "java.library.path", listOf(
            System.getProperty("pcre2.library.path"),
            System.getProperty("java.library.path")
        ).joinToString(":")
    )
}

tasks.named<Jar>("sourcesJar") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.withType<Javadoc> {
    val javadocOptions = options as CoreJavadocOptions

    javadocOptions.addStringOption("source", "21")
    javadocOptions.addBooleanOption("-enable-preview", true)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            artifact(tasks.named("sourcesJar"))
            artifact(tasks.named("javadocJar"))

            groupId = "org.pcre4j"
            artifactId = project.name
            version = findProperty("pcre4j.version") as String? ?: "0.0.0-SNAPSHOT"
        }
    }

    repositories {
        mavenCentral {
            credentials {
                username = findProperty("pcre4j.mavenCentral.user") as String? ?: ""
                password = findProperty("pcre4j.mavenCentral.password") as String? ?: ""
            }
        }
    }
}
