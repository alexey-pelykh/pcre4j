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
    id("pcre4j-module")
    id("pcre4j-native-test")
}

dependencies {
    api(project(":api"))
    implementation(libs.jna.platform)
    testImplementation(project(":lib"))
    testImplementation(testFixtures(project(":lib")))
}

publishing {
    publications.named<MavenPublication>("mavenJava") {
        pom {
            name = "PCRE4J JNA Backend"
            description = "PCRE4J JNA Backend"
        }
    }
}
