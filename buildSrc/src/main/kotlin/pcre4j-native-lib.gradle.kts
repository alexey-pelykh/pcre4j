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
 */
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
