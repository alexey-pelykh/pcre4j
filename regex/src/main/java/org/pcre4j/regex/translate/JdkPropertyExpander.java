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
     * Per-Java-property RangeSet cache, populated lazily by {@link #javaPropertyMap()}.
     * Keys are the Java property identifiers as they appear in {@code \p{…}}: {@code "javaLowerCase"},
     * {@code "javaUpperCase"}, {@code "javaSpaceChar"}, etc.
     */
    private static volatile Map<String, RangeSet> javaProperties;

    /**
     * Per-Unicode-block RangeSet cache. Keys are normalised (underscores stripped, lower-cased)
     * block names so {@code "GreekExtended"}, {@code "Greek_Extended"} and {@code "GREEK EXTENDED"}
     * all map to the same entry. {@link #NOT_FOUND} marks names without a matching block.
     */
    private static final ConcurrentHashMap<String, RangeSet> BLOCK_CACHE = new ConcurrentHashMap<>();

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

    private static Map<String, RangeSet> javaPropertyMap() {
        Map<String, RangeSet> map = javaProperties;
        if (map == null) {
            synchronized (JdkPropertyExpander.class) {
                map = javaProperties;
                if (map == null) {
                    map = buildJavaPropertyMap();
                    javaProperties = map;
                }
            }
        }
        return map;
    }

    /**
     * Materialises each supported {@code javaXxx} property by scanning the full code-point space
     * with the matching {@link Character} predicate. Only properties whose Java semantics genuinely
     * diverge from a single Unicode general category live here; entries that already have an exact
     * GC equivalent stay in {@link PropertyMap} as cheap aliases.
     */
    private static Map<String, RangeSet> buildJavaPropertyMap() {
        final Map<String, SpanBuilder> b = new HashMap<>();
        b.put("javaLowerCase",  new SpanBuilder());
        b.put("javaUpperCase",  new SpanBuilder());
        b.put("javaTitleCase",  new SpanBuilder());
        b.put("javaSpaceChar",  new SpanBuilder());
        b.put("javaMirrored",   new SpanBuilder());
        b.put("javaDefined",    new SpanBuilder());
        b.put("javaDigit",      new SpanBuilder());
        b.put("javaAlphabetic", new SpanBuilder());
        b.put("javaIdeographic", new SpanBuilder());
        b.put("javaISOControl", new SpanBuilder());
        b.put("javaWhitespace", new SpanBuilder());
        b.put("javaLetter",     new SpanBuilder());
        b.put("javaLetterOrDigit", new SpanBuilder());
        b.put("javaJavaIdentifierStart", new SpanBuilder());
        b.put("javaJavaIdentifierPart",  new SpanBuilder());
        b.put("javaUnicodeIdentifierStart", new SpanBuilder());
        b.put("javaUnicodeIdentifierPart",  new SpanBuilder());
        b.put("javaIdentifierIgnorable", new SpanBuilder());

        for (int cp = 0; cp <= Character.MAX_CODE_POINT; cp++) {
            if (Character.isLowerCase(cp))       b.get("javaLowerCase").add(cp);
            if (Character.isUpperCase(cp))       b.get("javaUpperCase").add(cp);
            if (Character.isTitleCase(cp))       b.get("javaTitleCase").add(cp);
            if (Character.isSpaceChar(cp))       b.get("javaSpaceChar").add(cp);
            if (Character.isMirrored(cp))        b.get("javaMirrored").add(cp);
            if (Character.isDefined(cp))         b.get("javaDefined").add(cp);
            if (Character.isDigit(cp))           b.get("javaDigit").add(cp);
            if (Character.isAlphabetic(cp))      b.get("javaAlphabetic").add(cp);
            if (Character.isIdeographic(cp))     b.get("javaIdeographic").add(cp);
            if (Character.isISOControl(cp))      b.get("javaISOControl").add(cp);
            if (Character.isWhitespace(cp))      b.get("javaWhitespace").add(cp);
            if (Character.isLetter(cp))          b.get("javaLetter").add(cp);
            if (Character.isLetterOrDigit(cp))   b.get("javaLetterOrDigit").add(cp);
            if (Character.isJavaIdentifierStart(cp)) b.get("javaJavaIdentifierStart").add(cp);
            if (Character.isJavaIdentifierPart(cp))  b.get("javaJavaIdentifierPart").add(cp);
            if (Character.isUnicodeIdentifierStart(cp)) b.get("javaUnicodeIdentifierStart").add(cp);
            if (Character.isUnicodeIdentifierPart(cp))  b.get("javaUnicodeIdentifierPart").add(cp);
            if (Character.isIdentifierIgnorable(cp))    b.get("javaIdentifierIgnorable").add(cp);
        }

        final Map<String, RangeSet> out = new HashMap<>(b.size());
        for (final Map.Entry<String, SpanBuilder> e : b.entrySet()) {
            out.put(e.getKey(), e.getValue().build());
        }
        return Collections.unmodifiableMap(out);
    }

    /**
     * Resolves a Java {@link Character.UnicodeBlock} by name (accepting JDK's both
     * {@code "GreekExtended"} and {@code "GREEK_EXTENDED"} spellings) and enumerates its code
     * points. Returns {@link #NOT_FOUND} for unrecognised names.
     */
    private static RangeSet computeBlock(final String name) {
        Character.UnicodeBlock block = lookupBlock(name);
        if (block == null) {
            return NOT_FOUND;
        }
        final SpanBuilder span = new SpanBuilder();
        for (int cp = 0; cp <= Character.MAX_CODE_POINT; cp++) {
            if (Character.UnicodeBlock.of(cp) == block) {
                span.add(cp);
            }
        }
        final RangeSet rs = span.build();
        return rs.isEmpty() ? NOT_FOUND : rs;
    }

    private static Character.UnicodeBlock lookupBlock(final String name) {
        // JDK accepts spellings like "GreekExtended", "GREEK_EXTENDED", "Greek_Extended" and
        // "Greek Extended"; UnicodeBlock.forName is permissive about case and separators but
        // not about CamelCase, so probe both variants.
        try {
            return Character.UnicodeBlock.forName(name);
        } catch (final IllegalArgumentException ignored) {
            // Fall through to the CamelCase-normalised attempt.
        }
        final String snake = camelToSnake(name);
        if (!snake.equals(name)) {
            try {
                return Character.UnicodeBlock.forName(snake);
            } catch (final IllegalArgumentException ignored) {
                return null;
            }
        }
        return null;
    }

    private static String camelToSnake(final String s) {
        final StringBuilder sb = new StringBuilder(s.length() + 4);
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            if (i > 0 && Character.isUpperCase(c) && Character.isLowerCase(s.charAt(i - 1))) {
                sb.append('_');
            }
            sb.append(c);
        }
        return sb.toString();
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

    /**
     * Materialises a Java {@code Character.is…}-defined property (e.g. {@code "javaLowerCase"},
     * {@code "javaSpaceChar"}) by scanning the full code-point space with the actual JDK predicate
     * and emitting a PCRE2 character class body.
     *
     * <p>This is the only correct way to translate properties whose Java semantics are a strict
     * superset of any Unicode general category — for instance {@code \p{javaLowerCase}} matches
     * U+00AA (ª, gc=Lo) but {@code \p{Ll}} does not. Mapping {@code javaLowerCase→Ll} silently
     * narrows the result; emitting the full {@code [\\x{aa}\\x{b5}\\x{ba}…]} class preserves it.
     *
     * @param javaPropertyName the Java property identifier (e.g. {@code "javaLowerCase"})
     * @return a PCRE2-ready character class string starting with {@code [} and ending with
     *         {@code ]}, or {@code null} if the property is not recognised
     */
    static String materializeJavaProperty(final String javaPropertyName) {
        final RangeSet rs = javaPropertyMap().get(javaPropertyName);
        if (rs == null) {
            return null;
        }
        return "[" + rs.toPcre2ClassBody() + "]";
    }

    /**
     * Materialises a Java {@link Character.UnicodeBlock} (e.g. {@code "GreekExtended"} or
     * {@code "Greek_Extended"}) by enumerating the code points belonging to that block.
     *
     * <p>PCRE2 does not understand JDK's {@code In<Block>} syntax for multi-word block names,
     * so we cannot pass them through — we must emit explicit ranges instead.
     *
     * @param blockName the block name as it would appear after stripping the {@code In} prefix
     * @return a PCRE2-ready character class string, or {@code null} if no such block exists
     */
    static String materializeUnicodeBlock(final String blockName) {
        final RangeSet rs = BLOCK_CACHE.computeIfAbsent(blockName, JdkPropertyExpander::computeBlock);
        return rs == NOT_FOUND ? null : "[" + rs.toPcre2ClassBody() + "]";
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
        if (base != null) {
            return negate ? base.complement() : base;
        }
        // Java block names (\p{InXxxx}) are not in positiveMap because PCRE2's script table
        // doesn't include blocks. Try resolving them via Character.UnicodeBlock so the
        // intersection evaluator inside ClassRenderer can compose a RangeSet for them.
        if (name.startsWith("IN") && name.length() > 2) {
            final RangeSet block = computeBlock(name.substring(2));
            if (block != NOT_FOUND) {
                return negate ? block.complement() : block;
            }
        }
        // Java javaXxx properties (\p{javaLowerCase} etc.). The original input token has its
        // letter case preserved on the way in, but `name` was uppercased above. Re-derive the
        // original-case suffix from the input token.
        if (name.startsWith("JAVA")) {
            final int braceOpen = token.indexOf('{');
            final String original = token.substring(braceOpen + 1, token.length() - 1);
            final RangeSet rs = javaPropertyMap().get(original);
            if (rs != null) {
                return negate ? rs.complement() : rs;
            }
        }
        return NOT_FOUND;
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

            // Note: Character.UnicodeScript.of(int) only throws IllegalArgumentException for
            // codePoints outside [0, MAX_CODE_POINT] — by construction we never pass such a value.
            final String sname = Character.UnicodeScript.of(cp).name();
            scriptBuilders.computeIfAbsent(sname, k -> new SpanBuilder()).add(cp);
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
