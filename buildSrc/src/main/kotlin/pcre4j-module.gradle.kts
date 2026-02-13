/*
 * Convention plugin for PCRE4J submodules.
 *
 * Centralizes shared build configuration:
 *   - java-library, checkstyle, maven-publish, jacoco plugins
 *   - Java 21 toolchain with sources and Javadoc JARs
 *   - JUnit 5 test dependencies
 *   - Resolution strategy (fail on version conflict)
 *   - JaCoCo test report configuration
 *   - Maven publication boilerplate
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

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    testImplementation(platform(libs.findLibrary("junit-bom").get()))
    testImplementation(libs.findLibrary("junit-jupiter").get())
    testRuntimeOnly(libs.findLibrary("junit-platform-launcher").get())
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
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required = true
        html.required = true
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = project.name
        }
    }
}
