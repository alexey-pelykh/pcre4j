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
 * Convention plugin for PCRE4J modules whose tests load native PCRE2 libraries.
 *
 * Passes through PCRE2-related system properties to test JVMs:
 *   - pcre2.library.path  → jna.library.path / java.library.path
 *   - pcre2.library.name  → pcre2.library.name
 *   - pcre2.function.suffix → pcre2.function.suffix
 *
 * Applied by: lib, jna, ffm, regex (not api — it has no native dependency)
 *
 * Requires: pcre4j-module (or any plugin that provides the Test task type)
 */

tasks.withType<Test>().configureEach {
    val pcre2LibraryPath = providers.systemProperty("pcre2.library.path").orNull

    systemProperty(
        "jna.library.path", listOf(
            pcre2LibraryPath,
            providers.systemProperty("jna.library.path").orNull
        ).joinToString(File.pathSeparator)
    )

    systemProperty(
        "java.library.path", listOf(
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
}
