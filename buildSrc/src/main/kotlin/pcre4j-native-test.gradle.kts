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
