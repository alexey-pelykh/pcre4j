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
import org.pcre4j.Pcre4j;
import org.pcre4j.ffm.Pcre2;
import org.pcre4j.regex.Pattern;

/**
 * Smoke test for GraalVM native-image compilation of PCRE4J with the FFM backend.
 * <p>
 * Tests basic regex operations to verify that FFM downcalls, native library loading,
 * and the high-level API all work correctly in a native image.
 */
public class NativeImageSmokeTest {

    public static void main(String[] args) {
        int failures = 0;

        // Test 1: Explicit FFM backend initialization
        try {
            var api = new Pcre2();
            Pcre4j.setup(api);
            System.out.println("PASS: FFM backend initialized");
        } catch (Exception e) {
            System.err.println("FAIL: FFM backend initialization: " + e.getMessage());
            failures++;
        }

        // Test 2: Basic pattern compilation and matching
        try {
            var pattern = Pattern.compile("\\d{3}-(\\d{3})-(\\d{4})");
            var matcher = pattern.matcher("Call 555-123-4567 today");
            if (!matcher.find()) {
                throw new AssertionError("Expected match not found");
            }
            if (!matcher.group().equals("555-123-4567")) {
                throw new AssertionError("Full match mismatch: " + matcher.group());
            }
            if (!matcher.group(1).equals("123")) {
                throw new AssertionError("Group 1 mismatch: " + matcher.group(1));
            }
            if (!matcher.group(2).equals("4567")) {
                throw new AssertionError("Group 2 mismatch: " + matcher.group(2));
            }
            System.out.println("PASS: Basic pattern matching");
        } catch (Exception e) {
            System.err.println("FAIL: Basic pattern matching: " + e.getMessage());
            failures++;
        }

        // Test 3: Named capture groups
        try {
            var pattern = Pattern.compile("(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})");
            var matcher = pattern.matcher("Date: 2026-02-14");
            if (!matcher.find()) {
                throw new AssertionError("Expected match not found");
            }
            if (!matcher.group("year").equals("2026")) {
                throw new AssertionError("Named group mismatch: " + matcher.group("year"));
            }
            System.out.println("PASS: Named capture groups");
        } catch (Exception e) {
            System.err.println("FAIL: Named capture groups: " + e.getMessage());
            failures++;
        }

        // Test 4: Pattern.matches convenience method
        try {
            if (!Pattern.matches("^[a-z]+$", "hello")) {
                throw new AssertionError("Expected match");
            }
            if (Pattern.matches("^[a-z]+$", "Hello")) {
                throw new AssertionError("Expected no match");
            }
            System.out.println("PASS: Pattern.matches");
        } catch (Exception e) {
            System.err.println("FAIL: Pattern.matches: " + e.getMessage());
            failures++;
        }

        // Test 5: Case insensitive flag
        try {
            var pattern = Pattern.compile("hello", Pattern.CASE_INSENSITIVE);
            var matcher = pattern.matcher("Hello World");
            if (!matcher.find()) {
                throw new AssertionError("Case insensitive match failed");
            }
            System.out.println("PASS: Case insensitive matching");
        } catch (Exception e) {
            System.err.println("FAIL: Case insensitive matching: " + e.getMessage());
            failures++;
        }

        // Test 6: Replacement
        try {
            var pattern = Pattern.compile("\\bfoo\\b");
            var result = pattern.matcher("foo bar foo").replaceAll("baz");
            if (!result.equals("baz bar baz")) {
                throw new AssertionError("Replacement mismatch: " + result);
            }
            System.out.println("PASS: Replacement");
        } catch (Exception e) {
            System.err.println("FAIL: Replacement: " + e.getMessage());
            failures++;
        }

        // Summary
        System.out.println();
        if (failures == 0) {
            System.out.println("All tests passed!");
        } else {
            System.out.println(failures + " test(s) failed!");
            System.exit(1);
        }
    }
}
