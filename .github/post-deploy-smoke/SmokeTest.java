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
import org.pcre4j.Pcre4j;
import org.pcre4j.api.Pcre2NativeLoader;
import org.pcre4j.jna.Pcre2;
import org.pcre4j.regex.Pattern;

/**
 * Post-deploy smoke test for the {@code pcre4j-native-<platform>} Maven Central artifacts.
 * <p>
 * Run by {@code .github/workflows/post-deploy-smoke.yaml} on a 3-OS matrix after the Release
 * workflow publishes a new version. The runner classpath is composed entirely from artifacts
 * resolved from Maven Central (NOT the local build), and the JVM is launched without any
 * {@code -Dpcre2.library.path}/{@code -Djna.library.path}/{@code -Djava.library.path} so
 * the only path that succeeds is the one through the bundled native.
 * <p>
 * Three checks, in order:
 * <ol>
 *   <li>{@link Pcre2NativeLoader#load} returns a non-empty path. Catches the #556 regression
 *       directly: an empty {@code pcre4j-native-*} JAR (only {@code .gitkeep}) returns
 *       {@link java.util.Optional#empty} here, before any backend is constructed.</li>
 *   <li>{@code new Pcre2()} succeeds. Catches a corrupt or unloadable native — JNA loads the
 *       extracted file by absolute path and surfaces an {@link UnsatisfiedLinkError} that we
 *       turn into a non-zero exit.</li>
 *   <li>{@link Pattern#compile} of {@code "a(b|c)+"} matches {@code "abbc"}. The end-to-end
 *       smoke that the bundled library actually executes a match.</li>
 * </ol>
 * <p>
 * The workflow runs this with {@code -Dpcre2.library.discovery=false} so that
 * {@link org.pcre4j.api.Pcre2LibraryFinder} cannot mask an empty bundle by discovering a
 * system-installed PCRE2 via {@code pcre2-config}/{@code pkg-config}/well-known paths.
 */
public class SmokeTest {

    public static void main(String[] args) {
        // 1. Bundle resolves to an extracted, non-empty file.
        var bundled = Pcre2NativeLoader.load("pcre2-8");
        if (bundled.isEmpty()) {
            System.err.println(
                "FAIL: pcre4j-native-* bundle did not provide an extractable native library "
                    + "(see issue #556 for the canonical empty-bundle failure mode)."
            );
            System.exit(1);
        }
        System.out.println("OK: bundled native extracted to " + bundled.get());

        // 2. Backend loads from the extracted absolute path. A corrupt bundle surfaces here.
        try {
            Pcre4j.setup(new Pcre2());
        } catch (Throwable t) {
            System.err.println("FAIL: JNA backend initialization failed: " + t);
            t.printStackTrace();
            System.exit(1);
        }
        System.out.println("OK: JNA backend initialized");

        // 3. End-to-end: compile + match per #559 acceptance criteria.
        try {
            var pattern = Pattern.compile("a(b|c)+");
            var matcher = pattern.matcher("abbc");
            if (!matcher.matches()) {
                System.err.println("FAIL: Pattern \"a(b|c)+\" did not match \"abbc\"");
                System.exit(1);
            }
        } catch (Throwable t) {
            System.err.println("FAIL: pattern compile/match raised: " + t);
            t.printStackTrace();
            System.exit(1);
        }
        System.out.println("OK: Pattern \"a(b|c)+\" matched \"abbc\"");

        System.out.println();
        System.out.println("Smoke test PASSED");
    }
}
