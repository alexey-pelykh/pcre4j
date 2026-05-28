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
package org.pcre4j.regex.translate;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps Java regex property names (as used in {@code \p{...}}) to PCRE2 equivalents.
 *
 * <p>Return convention for {@link #apply(String)}:
 * <ul>
 *   <li>A bare name like {@code "Greek"} → caller emits {@code \p{Greek}} / {@code \P{Greek}}</li>
 *   <li>A string starting with {@code '['} → caller substitutes the whole {@code \p{name}} token
 *       with this string (used for expanded ranges and multi-class expressions)</li>
 *   <li>{@code null} → no rewrite; leave token as-is</li>
 * </ul>
 */
public final class PropertyMap {

    /**
     * Well-known explicit mappings. Values starting with {@code '['} replace the whole token.
     */
    private static final Map<String, String> TABLE = new HashMap<>();

    static {
        // --- Block properties with surrogate-range expansion ---
        // Lone surrogate ranges: PCRE2_UTF will reject code points in the surrogate range, but we
        // translate to the explicit hex ranges so the syntax is at least accepted by the parser.
        // TODO: If PCRE2 still rejects these under PCRE2_UTF, users must disable UTF mode manually.
        TABLE.put("InHIGH_SURROGATES",       "[\\x{D800}-\\x{DB7F}]");
        TABLE.put("InHIGH_PRIVATE_USE_SURROGATES", "[\\x{DB80}-\\x{DBFF}]");
        TABLE.put("InLOW_SURROGATES",        "[\\x{DC00}-\\x{DFFF}]");

        // --- Short alias: L1 (JDK's Latin-1 shorthand) ---
        TABLE.put("L1",     "[\\x{00}-\\x{FF}]");

        // --- \p{javaXxx} Java-specific properties ---
        // These are best-effort approximations; documented deviations noted inline.
        TABLE.put("javaLowerCase",                "Ll");
        TABLE.put("javaUpperCase",                "Lu");
        TABLE.put("javaTitleCase",                "Lt");
        TABLE.put("javaDigit",                    "Nd");
        TABLE.put("javaLetter",                   "L");
        TABLE.put("javaLetterOrDigit",            "[\\p{L}\\p{Nd}]");
        TABLE.put("javaAlphabetic",               "Alphabetic");
        TABLE.put("javaIdeographic",              "Ideographic");
        TABLE.put("javaSpaceChar",                "Zs");
        TABLE.put("javaMirrored",                 "Bidi_Mirrored");
        TABLE.put("javaDefined",                  "\\P{Cn}");  // not-unassigned; whole-token replacement
        TABLE.put("javaISOControl",               "[\\x00-\\x1F\\x{7F}-\\x{9F}]");
        TABLE.put("javaJavaIdentifierStart",      "[\\p{L}\\p{Nl}_$]");
        TABLE.put("javaJavaIdentifierPart",       "[\\p{L}\\p{Nl}\\p{Mn}\\p{Mc}\\p{Nd}\\p{Pc}_$]");
        TABLE.put("javaUnicodeIdentifierStart",   "[\\p{L}\\p{Nl}]");
        TABLE.put("javaUnicodeIdentifierPart",    "[\\p{L}\\p{Nl}\\p{Mn}\\p{Mc}\\p{Nd}\\p{Pc}]");
        // Per Character.isWhitespace() Javadoc:
        TABLE.put("javaWhitespace",
                "[\\t\\n\\x0B\\f\\r \\x{1C}-\\x{1F}\\x{1680}" +
                "\\x{2000}-\\x{200A}\\x{2028}\\x{2029}\\x{205F}\\x{3000}]");
    }

    private PropertyMap() {
    }

    /**
     * Resolves a Java regex property name to a PCRE2 equivalent.
     *
     * @param name the property name between the braces, e.g. {@code "InGreek"}, {@code "IsL"},
     *             {@code "javaLowerCase"}
     * @return the PCRE2 replacement string, or {@code null} if no rewrite is needed
     */
    public static String apply(final String name) {
        // 1. Exact table match
        final String exact = TABLE.get(name);
        if (exact != null) {
            return exact;
        }

        // 2. \p{IsXxx} → strip "Is" prefix and pass through (handles IsL, IsLC, IsASCII, IsPf, IsP, …)
        if (name.startsWith("Is") && name.length() > 2) {
            return name.substring(2);
        }

        // 3. \p{InXxx} → strip "In" prefix; PCRE2 recognises both block and script names without prefix.
        //    Special case: ALL_CAPS_WITH_UNDERSCORES names (e.g. HIGH_SURROGATES after stripping "In")
        //    are Unicode block names that were already handled by exact lookup above; if we reach here
        //    it means they had no explicit range entry, so just strip the prefix and let PCRE2 decide.
        if (name.startsWith("In") && name.length() > 2) {
            return name.substring(2);
        }

        // 4. No rewrite
        return null;
    }
}
