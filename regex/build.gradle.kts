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
    id("pcre4j-module")
    id("pcre4j-native-test")
}

dependencies {
    api(project(":api"))
    api(project(":lib"))
    testImplementation(testFixtures(project(":lib")))
    testImplementation(project(":jna"))
    testImplementation(project(":ffm"))
}

// --enable-preview on test tasks only: regex's own code does not use preview
// features, but tests load the FFM backend (which uses the preview FFM API
// on Java 21) at runtime. The flag is needed for:
//   - compileTestJava: test code may reference FFM-backed types
//   - test JVM args: JVM must enable preview features for FFM backend loading
tasks.test {
    jvmArgs("--enable-preview")
}

tasks.named<JavaCompile>("compileTestJava") {
    options.compilerArgs.add("--enable-preview")
}

publishing {
    publications.named<MavenPublication>("mavenJava") {
        pom {
            name = "PCRE4J Regex"
            description = "PCRE4J java.util.regex-compatible API"
        }
    }
}
