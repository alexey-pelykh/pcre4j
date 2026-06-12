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
        // Surrogate blocks are intentionally NOT registered with explicit \x{D800}-\x{DFFF}
        // ranges: PCRE2 in UTF mode rejects code points in the surrogate area at compile time
        // (error 173). java.util.regex accepts these properties (they just never match against
        // any code point a UTF-8 PCRE2 can see). They are now routed to the never-match
        // construct in {@link #apply(String)} so a valid Java pattern containing
        // {@code \\p{InHighSurrogates}} no longer throws PatternSyntaxException.

        // --- Short alias: L1 (JDK's Latin-1 shorthand) ---
        TABLE.put("L1",     "[\\x{00}-\\x{FF}]");

        // --- \p{javaXxx} Java-specific properties ---
        // Most javaXxx are exact GC aliases (TitleCase=Lt, Mirrored=Bidi_Mirrored, etc.) and
        // stay here as cheap PropertyMap rewrites. The three properties below are intentionally
        // omitted because Java's predicate is a *superset* of the corresponding PCRE2 GC, and
        // returning the GC alias would silently narrow the match. They are materialised via
        // {@link JdkPropertyExpander#materializeJavaProperty(String)} in {@link #apply(String)}.
        //   - javaLowerCase  (incl. e.g. U+00AA ª, gc=Lo)
        //   - javaUpperCase  (incl. e.g. Roman numerals, gc=Nl)
        //   - javaSpaceChar  (drops U+2028/U+2029 if mapped to Zs)
        TABLE.put("javaTitleCase",                "Lt");
        TABLE.put("javaDigit",                    "Nd");
        TABLE.put("javaLetter",                   "L");
        TABLE.put("javaLetterOrDigit",            "[\\p{L}\\p{Nd}]");
        TABLE.put("javaAlphabetic",               "Alphabetic");
        TABLE.put("javaIdeographic",              "Ideographic");
        TABLE.put("javaMirrored",                 "Bidi_Mirrored");
        TABLE.put("javaDefined",                  "\\P{Cn}");  // not-unassigned; whole-token replacement
        TABLE.put("javaISOControl",               "[\\x00-\\x1F\\x{7F}-\\x{9F}]");
        TABLE.put("javaJavaIdentifierStart",      "[\\p{L}\\p{Nl}_$]");
        TABLE.put("javaJavaIdentifierPart",       "[\\p{L}\\p{Nl}\\p{Mn}\\p{Mc}\\p{Nd}\\p{Pc}_$]");
        TABLE.put("javaUnicodeIdentifierStart",   "[\\p{L}\\p{Nl}]");
        TABLE.put("javaUnicodeIdentifierPart",    "[\\p{L}\\p{Nl}\\p{Mn}\\p{Mc}\\p{Nd}\\p{Pc}]");
        TABLE.put("javaIdentifierIgnorable",
                "[\\x{00}-\\x{08}\\x{0E}-\\x{1B}\\x{7F}-\\x{9F}\\p{Cf}]");
        // Per Character.isWhitespace() Javadoc:
        TABLE.put("javaWhitespace",
                "[\\t\\n\\x0B\\f\\r \\x{1C}-\\x{1F}\\x{1680}" +
                "\\x{2000}-\\x{200A}\\x{2028}\\x{2029}\\x{205F}\\x{3000}]");

        // --- POSIX-style class names accepted by Java's \p{Xxx} (default, non-UNICODE) ---
        TABLE.put("Lower",  "[a-z]");
        TABLE.put("Upper",  "[A-Z]");
        TABLE.put("Alpha",  "[a-zA-Z]");
        TABLE.put("Digit",  "[0-9]");
        TABLE.put("Alnum",  "[a-zA-Z0-9]");
        TABLE.put("Punct",  "[!-/:-@\\[-`{-~]");
        TABLE.put("Graph",  "[!-~]");
        TABLE.put("Print",  "[ -~]");
        TABLE.put("Blank",  "[ \\t]");
        TABLE.put("Cntrl",  "[\\x00-\\x1F\\x{7F}]");
        TABLE.put("XDigit", "[0-9a-fA-F]");
        TABLE.put("Space",  "[ \\t\\n\\x0B\\f\\r]");

        // --- Java property names not recognised as PCRE2 long names ---
        // PCRE2 wants short general-category aliases here.
        TABLE.put("Control", "Cc");
        TABLE.put("Format",  "Cf");
        TABLE.put("TitleCase", "Lt");
        TABLE.put("UpperCase", "Lu");
        TABLE.put("LowerCase", "Ll");
        TABLE.put("Letter",      "L");
        TABLE.put("Mark",        "M");
        TABLE.put("Number",      "N");
        TABLE.put("Punctuation", "P");
        TABLE.put("Symbol",      "S");
        TABLE.put("Separator",   "Z");
        TABLE.put("Other",       "C");
        TABLE.put("Assigned",    "\\P{Cn}");
        TABLE.put("Unassigned",  "Cn");
    }

    /**
     * Sentinel returned by {@link #apply(String)} when a property should compile to a
     * never-matching pattern (e.g. JDK block names {@code InHighSurrogates} /
     * {@code InHighPrivateUseSurrogates} / {@code InLowSurrogates} that PCRE2 cannot accept
     * under its always-on UTF mode). The translator recognises this exact string and emits a
     * top-level {@code (?!)} (or its negated counterpart for {@code \\P{…}}).
     */
    public static final String NEVER_MATCH = "\u0001NEVER_MATCH\u0001";

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
        // 0. Strip Java/Unicode qualifier prefixes: gc=Lu, sc=Greek, blk=Latin, etc.
        final int eq = name.indexOf('=');
        if (eq > 0) {
            final String key = name.substring(0, eq).toLowerCase();
            final String value = name.substring(eq + 1);
            switch (key) {
                case "gc":
                case "general_category":
                    return resolveOrPass(value);
                case "sc":
                case "script":
                    return resolveOrPass(value);
                case "blk":
                case "block":
                    return resolveBlock(value);
                default:
                    return null;
            }
        }

        // 1a. \p{javaXxx} that diverges from any PCRE2 category: materialise via JDK predicate.
        //     Cheap exact-table aliases (javaTitleCase, etc.) are checked first below; only the
        //     three Character.isLowerCase/isUpperCase/isSpaceChar properties — whose Java
        //     semantics are a strict superset of the GC alias — go through the expander.
        if ("javaLowerCase".equals(name)
                || "javaUpperCase".equals(name)
                || "javaSpaceChar".equals(name)) {
            return JdkPropertyExpander.materializeJavaProperty(name);
        }

        // 1. Exact table match
        final String exact = TABLE.get(name);
        if (exact != null) {
            return exact;
        }

        // 2. \p{IsXxx} → strip "Is" prefix; if the stripped name is a known JDK alias
        //    (e.g. "IsControl" → "Control" → "Cc"), prefer that mapping over passthrough.
        if (name.startsWith("Is") && name.length() > 2) {
            final String stripped = name.substring(2);
            final String mapped = TABLE.get(stripped);
            return mapped != null ? mapped : stripped;
        }

        // 3. \p{InXxx} → Java block names. PCRE2 has no block table at all (only scripts), so
        //    we MUST materialise blocks ourselves via Character.UnicodeBlock; a CamelCase →
        //    snake_case rewrite alone would still fail on PCRE2 with error 147 (unknown
        //    property). Surrogate blocks resolve to an empty range (PCRE2 in UTF mode never
        //    matches surrogate code points anyway) and are emitted as the NEVER_MATCH sentinel
        //    so the translator can substitute a top-level {@code (?!)} construct.
        if (name.startsWith("In") && name.length() > 2) {
            return resolveBlock(name.substring(2));
        }

        // 4. No rewrite
        return null;
    }

    private static String resolveBlock(final String blockName) {
        // Surrogate blocks: PCRE2 in UTF mode (the only mode PCRE4J turns on) refuses
        // \\x{D800}-\\x{DFFF} ranges. Java's regex accepts these blocks; they just never
        // match against decoded UTF-16 input. Return the NEVER_MATCH sentinel so the
        // translator emits a never-matching construct rather than a compile error.
        // Compare against the underscore-stripped, upper-cased form so we catch every
        // JDK-accepted spelling: \p{InHighSurrogates}, \p{InHIGH_SURROGATES},
        // \p{InHigh_Surrogates}, \p{In_high_surrogates} etc.
        final String compact = blockName.toUpperCase().replace("_", "");
        if ("HIGHSURROGATES".equals(compact)
                || "HIGHPRIVATEUSESURROGATES".equals(compact)
                || "LOWSURROGATES".equals(compact)) {
            return NEVER_MATCH;
        }
        final String materialized = JdkPropertyExpander.materializeUnicodeBlock(blockName);
        if (materialized != null) {
            return materialized;
        }
        // Last-ditch fallback: hand PCRE2 the snake-cased name. PCRE2 will reject it with a
        // clear "unknown property" error rather than us silently dropping the token.
        return camelCaseToUnderscores(blockName);
    }

    private static String camelCaseToUnderscores(final String s) {
        if (s.indexOf('_') >= 0) {
            return s; // already underscored or mixed; leave alone
        }
        final StringBuilder sb = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            if (i > 0 && Character.isUpperCase(c) && Character.isLowerCase(s.charAt(i - 1))) {
                sb.append('_');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static String resolveOrPass(final String value) {
        final String mapped = TABLE.get(value);
        return mapped != null ? mapped : value;
    }
}
