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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Expands PCRE2 property tokens (e.g. {@code \p{L}}, {@code \p{Greek}}, {@code \P{InGreek}}) into
 * {@link RangeSet}s by iterating over the full Unicode code-point space using the JDK's
 * {@link Character} API.
 *
 * <p>The expensive per-property scan over 0..0x10FFFF is deferred to first use of each property
 * (lazy initialisation): patterns that never touch {@code \p{…}} pay nothing. Each property's
 * {@link RangeSet} is memoised in a {@link ConcurrentHashMap}.
 *
 * <p>Supports:
 * <ul>
 *   <li>Unicode general categories: single ({@code Lu}, {@code Ll}, …) and combined
 *       ({@code L}, {@code M}, {@code N}, {@code P}, {@code S}, {@code Z}, {@code C},
 *       {@code LC})</li>
 *   <li>Unicode script names (e.g. {@code Greek}, {@code Latin}, {@code Hiragana}) —
 *       matched case-insensitively against {@link Character.UnicodeScript} enum names</li>
 *   <li>{@code ASCII} (U+0000–U+007F)</li>
 * </ul>
 */
final class JdkPropertyExpander {

    /** Sentinel stored in {@link #CACHE} to represent "this token is not supported". */
    private static final RangeSet NOT_FOUND = RangeSet.fromSortedPairs(new int[0]);

    /** Memoisation cache: token string → expanded RangeSet (or NOT_FOUND sentinel). */
    private static final ConcurrentHashMap<String, RangeSet> CACHE = new ConcurrentHashMap<>();

    /**
     * Maps {@link Character#getType(int)} return values to the two-letter Unicode general-category
     * short names used as POSITIVE map keys (uppercase, e.g. {@code "LU"}, {@code "MN"}).
     * Index 17 is unused (no Character type constant has that value).
     */
    private static final String[] TYPE_TO_CAT = buildTypeMap();

    /**
     * Maps uppercase property name → positive {@link RangeSet}. Built lazily on first call to
     * {@link #expand} via {@link #positiveMap()} so unused patterns don't pay the ~50 ms
     * full-Unicode scan.
     */
    private static volatile Map<String, RangeSet> positive;

    private JdkPropertyExpander() {
    }

    private static Map<String, RangeSet> positiveMap() {
        Map<String, RangeSet> map = positive;
        if (map == null) {
            synchronized (JdkPropertyExpander.class) {
                map = positive;
                if (map == null) {
                    map = buildPositiveMap();
                    positive = map;
                }
            }
        }
        return map;
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Expands a PCRE2 property token to a {@link RangeSet}, or returns {@code null} if the
     * property is not recognised.
     *
     * <p>Recognised token forms: {@code \p{X}} and {@code \P{X}} where {@code X} is a Unicode
     * general-category name or script name.
     *
     * @param pcre2Token the token string, e.g. {@code "\\p{L}"} or {@code "\\P{Greek}"}
     * @return the corresponding {@link RangeSet}, or {@code null} if not supported
     */
    static RangeSet expand(final String pcre2Token) {
        if (pcre2Token == null) {
            return null;
        }
        final RangeSet cached = CACHE.computeIfAbsent(pcre2Token, JdkPropertyExpander::compute);
        return cached == NOT_FOUND ? null : cached;
    }

    // -----------------------------------------------------------------------
    // Computation (called at most once per token due to computeIfAbsent)
    // -----------------------------------------------------------------------

    private static RangeSet compute(final String token) {
        final boolean negate;
        final String name;
        if (token.startsWith("\\p{") && token.endsWith("}")) {
            negate = false;
            name = token.substring(3, token.length() - 1).toUpperCase();
        } else if (token.startsWith("\\P{") && token.endsWith("}")) {
            negate = true;
            name = token.substring(3, token.length() - 1).toUpperCase();
        } else {
            // \d, \w, \s etc. are handled by Evaluator directly; not our concern.
            return NOT_FOUND;
        }

        final RangeSet base = positiveMap().get(name);
        if (base == null) {
            return NOT_FOUND;
        }
        return negate ? base.complement() : base;
    }

    // -----------------------------------------------------------------------
    // Eager initialisation helpers
    // -----------------------------------------------------------------------

    private static String[] buildTypeMap() {
        final String[] map = new String[31];
        map[Character.UPPERCASE_LETTER]         = "LU";
        map[Character.LOWERCASE_LETTER]         = "LL";
        map[Character.TITLECASE_LETTER]         = "LT";
        map[Character.MODIFIER_LETTER]          = "LM";
        map[Character.OTHER_LETTER]             = "LO";
        map[Character.NON_SPACING_MARK]         = "MN";
        map[Character.ENCLOSING_MARK]           = "ME";
        map[Character.COMBINING_SPACING_MARK]   = "MC";
        map[Character.DECIMAL_DIGIT_NUMBER]     = "ND";
        map[Character.LETTER_NUMBER]            = "NL";
        map[Character.OTHER_NUMBER]             = "NO";
        map[Character.SPACE_SEPARATOR]          = "ZS";
        map[Character.LINE_SEPARATOR]           = "ZL";
        map[Character.PARAGRAPH_SEPARATOR]      = "ZP";
        map[Character.CONTROL]                  = "CC";
        map[Character.FORMAT]                   = "CF";
        map[Character.SURROGATE]                = "CS";
        map[Character.PRIVATE_USE]              = "CO";
        map[Character.UNASSIGNED]               = "CN";
        map[Character.DASH_PUNCTUATION]         = "PD";
        map[Character.START_PUNCTUATION]        = "PS";
        map[Character.END_PUNCTUATION]          = "PE";
        map[Character.CONNECTOR_PUNCTUATION]    = "PC";
        map[Character.OTHER_PUNCTUATION]        = "PO";
        map[Character.MATH_SYMBOL]              = "SM";
        map[Character.CURRENCY_SYMBOL]          = "SC";
        map[Character.MODIFIER_SYMBOL]          = "SK";
        map[Character.OTHER_SYMBOL]             = "SO";
        map[Character.INITIAL_QUOTE_PUNCTUATION] = "PI";
        map[Character.FINAL_QUOTE_PUNCTUATION]  = "PF";
        return map;
    }

    private static Map<String, RangeSet> buildPositiveMap() {
        // One builder per leaf general-category name (uppercase)
        final Map<String, SpanBuilder> catBuilders = new HashMap<>();
        for (final String cat : new String[]{
                "LU", "LL", "LT", "LM", "LO",
                "MN", "ME", "MC",
                "ND", "NL", "NO",
                "PC", "PD", "PS", "PE", "PI", "PF", "PO",
                "SM", "SC", "SK", "SO",
                "ZS", "ZL", "ZP",
                "CC", "CF", "CS", "CO", "CN",
        }) {
            catBuilders.put(cat, new SpanBuilder());
        }

        // One builder per script name (uppercase enum name, e.g. "GREEK")
        final Map<String, SpanBuilder> scriptBuilders = new HashMap<>();

        // Single pass over all Unicode code points
        for (int cp = 0; cp <= Character.MAX_CODE_POINT; cp++) {
            final int type = Character.getType(cp);
            if (type < TYPE_TO_CAT.length) {
                final String cat = TYPE_TO_CAT[type];
                if (cat != null) {
                    catBuilders.get(cat).add(cp);
                }
            }

            try {
                final String sname = Character.UnicodeScript.of(cp).name();
                scriptBuilders.computeIfAbsent(sname, k -> new SpanBuilder()).add(cp);
            } catch (final IllegalArgumentException ignored) {
                // Skipped; shouldn't happen for valid code points
            }
        }

        final Map<String, RangeSet> map = new HashMap<>();

        // Add leaf categories
        for (final Map.Entry<String, SpanBuilder> e : catBuilders.entrySet()) {
            map.put(e.getKey(), e.getValue().build());
        }

        // Add combined categories
        map.put("L",  unionOf(map, "LU", "LL", "LT", "LM", "LO"));
        map.put("LC", unionOf(map, "LU", "LL", "LT"));
        map.put("M",  unionOf(map, "MN", "ME", "MC"));
        map.put("N",  unionOf(map, "ND", "NL", "NO"));
        map.put("P",  unionOf(map, "PC", "PD", "PS", "PE", "PI", "PF", "PO"));
        map.put("S",  unionOf(map, "SM", "SC", "SK", "SO"));
        map.put("Z",  unionOf(map, "ZS", "ZL", "ZP"));
        map.put("C",  unionOf(map, "CC", "CF", "CS", "CO", "CN"));

        // Add scripts (key already uppercase)
        for (final Map.Entry<String, SpanBuilder> e : scriptBuilders.entrySet()) {
            map.put(e.getKey(), e.getValue().build());
        }

        // Special: ASCII range
        map.put("ASCII", RangeSet.range(0, 0x7F));

        return Collections.unmodifiableMap(map);
    }

    private static RangeSet unionOf(final Map<String, RangeSet> map, final String... keys) {
        RangeSet result = RangeSet.EMPTY;
        for (final String key : keys) {
            final RangeSet rs = map.get(key);
            if (rs != null) {
                result = result.union(rs);
            }
        }
        return result;
    }

    // -----------------------------------------------------------------------
    // SpanBuilder: accumulates code points added in ascending order into ranges
    // -----------------------------------------------------------------------

    private static final class SpanBuilder {
        private final List<Integer> pairs = new ArrayList<>();
        private int spanStart = -1;
        private int spanEnd = -1;

        void add(final int cp) {
            if (spanStart < 0) {
                spanStart = cp;
                spanEnd = cp;
            } else if (cp == spanEnd + 1) {
                spanEnd = cp;
            } else {
                pairs.add(spanStart);
                pairs.add(spanEnd);
                spanStart = cp;
                spanEnd = cp;
            }
        }

        RangeSet build() {
            if (spanStart >= 0) {
                pairs.add(spanStart);
                pairs.add(spanEnd);
                spanStart = -1;
            }
            final int[] arr = new int[pairs.size()];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = pairs.get(i);
            }
            return RangeSet.fromSortedPairs(arr);
        }
    }
}
