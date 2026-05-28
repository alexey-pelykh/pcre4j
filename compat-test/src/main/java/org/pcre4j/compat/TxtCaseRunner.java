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
package org.pcre4j.compat;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses OpenJDK-style {@code .txt} regex test-case files (e.g. {@code TestCases.txt}).
 *
 * <p>File format mirrors {@code RegExTest.processFile} / {@code grabLine} semantics:
 * blank lines and lines starting with {@code //} are skipped everywhere.
 * Each test case is three consecutive non-blank/non-comment lines: pattern, input, result.
 * The result is {@code error} (skip), {@code false groupCount}, or
 * {@code true group0 groupCount [group1 ...]}.
 * Within any line the two-character sequences backslash-n and backslash-uXXXX are replaced
 * by the corresponding characters.
 */
public final class TxtCaseRunner {

    /**
     * A single parsed test case.
     *
     * @param index          zero-based ordinal within the file
     * @param pattern        regex pattern string (flags already extracted)
     * @param patternFlags   {@link java.util.regex.Pattern} flag bits encoded in the pattern prefix
     * @param input          input string to match against
     * @param expectedMatch  {@code true} when the result line starts with {@code true}
     * @param expectedGroups tokens after the {@code true}/{@code false} keyword in the result line
     */
    public record Case(
            int index,
            String pattern,
            int patternFlags,
            String input,
            boolean expectedMatch,
            List<String> expectedGroups) {}

    private TxtCaseRunner() {}

    /**
     * Parses the full content of a {@code .txt} test-case file and returns the list of cases.
     * Cases whose result is {@code error} are included with {@code expectedMatch=false};
     * cases with unrecognized result format (e.g. GraphemeTestCases.txt data lines) are skipped.
     *
     * @param body the entire file content as a string
     * @return parsed cases in file order
     */
    public static List<Case> parse(String body) {
        // Collect all non-blank, non-comment lines and apply grabLine escape processing.
        // Note: '#' is NOT a comment prefix in the test case files (it appears in regex patterns).
        // Only '//' is used as a comment prefix.
        // GraphemeTestCases.txt has a different format entirely; its data lines don't start
        // with 'true'/'false'/'error' so they are filtered out at the result-parsing step.
        List<String> lines = new ArrayList<>();
        for (String raw : body.split("\\R", -1)) {
            if (raw.isBlank() || raw.startsWith("//")) {
                continue;
            }
            lines.add(applyEscapes(raw));
        }

        List<Case> out = new ArrayList<>();
        int i = 0;
        int idx = 0;
        while (i + 2 < lines.size()) {
            String patternLine = lines.get(i);
            String inputLine = lines.get(i + 1);
            String resultLine = lines.get(i + 2);
            i += 3;

            // Skip lines that are neither true/false/error (e.g. GraphemeTestCases.txt).
            if (!resultLine.startsWith("true") && !resultLine.startsWith("false")
                    && !resultLine.equals("error")) {
                continue;
            }

            // Extract pattern flags from 'pattern'flags wrapper.
            int patternFlags = 0;
            String pattern = patternLine;
            if (patternLine.startsWith("'")) {
                int close = patternLine.lastIndexOf('\'');
                if (close > 0) {
                    String flagStr = patternLine.substring(close + 1);
                    pattern = patternLine.substring(1, close);
                    patternFlags = decodeFlags(flagStr);
                }
            }

            // Parse result line.
            // "error" means the pattern is expected to throw PatternSyntaxException.
            boolean expectedError = resultLine.equals("error");
            boolean match = resultLine.startsWith("true");
            List<String> groups = new ArrayList<>();
            if (!expectedError) {
                String tail = match
                        ? resultLine.substring("true".length()).trim()
                        : resultLine.substring("false".length()).trim();
                if (!tail.isEmpty()) {
                    for (String token : tail.split("\\s+")) {
                        groups.add(token);
                    }
                }
            }

            out.add(new Case(idx++, pattern, patternFlags, inputLine, match, List.copyOf(groups)));
        }
        return out;
    }

    /**
     * Applies {@code grabLine}-style escape substitutions: two-char backslash-n becomes a newline,
     * and backslash-uXXXX sequences are replaced with the corresponding Unicode character.
     */
    static String applyEscapes(String line) {
        // Replace \n (two chars: backslash + n) with actual newline.
        StringBuilder sb = new StringBuilder(line);
        int idx;
        while ((idx = sb.indexOf("\\n")) != -1) {
            sb.replace(idx, idx + 2, "\n");
        }
        // Replace backslash-uXXXX sequences with the Unicode character.
        while ((idx = sb.indexOf("\\u")) != -1) {
            if (idx + 6 > sb.length()) break;
            String hex = sb.substring(idx + 2, idx + 6);
            try {
                char c = (char) Integer.parseInt(hex, 16);
                sb.replace(idx, idx + 6, String.valueOf(c));
            } catch (NumberFormatException e) {
                // Not a valid backslash-uXXXX sequence; leave as-is and skip past it.
                break;
            }
        }
        return sb.toString();
    }

    /**
     * Decodes the flag suffix string from a {@code 'pattern'flags} pattern line into
     * {@link java.util.regex.Pattern} flag bits.
     */
    private static int decodeFlags(String flagStr) {
        int flags = 0;
        if (flagStr.contains("i")) flags |= java.util.regex.Pattern.CASE_INSENSITIVE;
        if (flagStr.contains("m")) flags |= java.util.regex.Pattern.MULTILINE;
        return flags;
    }
}
