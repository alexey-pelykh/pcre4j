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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassRendererTest {

    private static String render(final String classStr) {
        final int[] pos = {0};
        final ClassNode node = ClassBodyParser.parseClass(classStr, pos);
        return ClassRenderer.render(node);
    }

    @Test
    void simpleLiterals() {
        final String result = render("[abc]");
        assertEquals("[abc]", result);
    }

    @Test
    void simpleRange() {
        final String result = render("[a-z]");
        assertEquals("[a-z]", result);
    }

    @Test
    void negatedRange() {
        final String result = render("[^a-z]");
        assertEquals("[^a-z]", result);
    }

    @Test
    void nestedUnionFlattens() {
        // [abc[def]] should flatten to [abcdef] (order preserved)
        final String result = render("[abc[def]]");
        assertEquals("[abcdef]", result);
    }

    @Test
    void negatedNestedFlattens() {
        // [^a-d[0-9]] — no intersection, flatten via RangeSet evaluation
        final String result = render("[^a-d[0-9]]");
        // Should produce a flat class that PCRE2 accepts
        // The inner [0-9] is a nested union; the outer is negated
        // Since no intersection, we flatten the nested union
        // Result should be [^a-d0-9] or equivalent
        assertFalse(result.contains("[["), "Result should not have nested [[: " + result);
        assertTrue(result.startsWith("[^"), "Should be negated: " + result);
    }

    @Test
    void intersectionLiteralRange() {
        // [a-c&&b-d] should evaluate to [b-c]
        final String result = render("[a-c&&b-d]");
        // b-c range
        assertTrue(result.contains("b") && result.contains("c"),
                "Expected b-c range in: " + result);
        assertFalse(result.contains("a"), "Should not contain 'a': " + result);
        assertFalse(result.contains("d"), "Should not contain 'd': " + result);
    }

    @Test
    void intersectionDisjoint() {
        // [a-c&&d-f] = empty set → empty-class sentinel
        final String result = render("[a-c&&d-f]");
        assertEquals("[^\\x{0}-\\x{10FFFF}]", result);
    }

    @Test
    void wDashHashEscapesDash() {
        // [\w-#] should render so '-' is escaped, preventing PCRE2 from treating it as a range
        final String result = render("[\\w-#]");
        // Should contain \w and escaped dash
        assertTrue(result.contains("\\w"), "Should contain \\w: " + result);
        assertTrue(result.contains("\\-") || result.contains("-"), "Should contain dash: " + result);
        // Crucially, the '-' should be escaped so PCRE2 doesn't try \w-# as a range
        assertTrue(result.contains("\\-"), "Dash should be escaped in: " + result);
    }

    @Test
    void intersectionWithKnownProperty() {
        // [\d&&[0-3]] should evaluate to [0-3] since \d=[0-9] and intersection with [0-3]=[0-3]
        final String result = render("[\\d&&[0-3]]");
        // Should contain 0-3
        assertTrue(result.contains("0") && result.contains("3"),
                "Expected 0-3 in result: " + result);
        assertFalse(result.contains("&&"), "Should not contain && after evaluation: " + result);
    }

    @Test
    void intersectionWithJdkExpandableProperty() {
        // [\p{L}&&[a-z]] — \p{L} is expanded via JdkPropertyExpander; intersection = [a-z]
        final String result = render("[\\p{L}&&[a-z]]");
        // After expansion the && is gone and the result is a flat class
        assertNotNull(result);
        assertFalse(result.contains("&&"), "Should not contain && after evaluation: " + result);
    }

    @Test
    void nestedNegatedIntersection() {
        // [^[a-c]&&[d-f]] — disjoint intersection is empty; negated empty = match anything.
        // Critical regression: previous code dropped the '^' silently in some fallback paths.
        final String result = render("[^[a-c]&&[d-f]]");
        assertEquals("[\\x{0}-\\x{10FFFF}]", result);
    }

    @Test
    void negatedIntersectionOfRanges() {
        // [^a-c&&b-d] = ^[b-c] — strategy 1 evaluates+complements, producing a positive
        // class covering all code points except b,c.  Exact rendered form: [\x{0}-ad-\x{10FFFF}].
        final String result = render("[^a-c&&b-d]");
        assertEquals("[\\x{0}-ad-\\x{10FFFF}]", result);
    }

    @Test
    void propertyLeafPassesThrough() {
        final String result = render("[\\d\\w]");
        assertTrue(result.contains("\\d"), "Should contain \\d: " + result);
        assertTrue(result.contains("\\w"), "Should contain \\w: " + result);
    }

    @Test
    void multipleIntersectionOperands() {
        // [a-m&&m-z&&a-c] = {m} ∩ [a-c] = empty since 'm' > 'c'
        final String result = render("[a-m&&m-z&&a-c]");
        assertEquals("[^\\x{0}-\\x{10FFFF}]", result);
    }

    @Test
    void nestedNegatedWithUnknownPropertyPreservesNegation() {
        // [abc[^\p{UnknownXyz}]] — the inner Negated wraps an un-evaluable property leaf.
        // emitFlat must surface this through the fallback so the resulting class still contains
        // a "[^...]" sub-class (the negation must not be silently dropped).
        final String result = render("[abc[^\\p{UnknownXyz}]]");
        assertTrue(result.contains("[^"),
                "nested negation must be preserved in rendered class: " + result);
        assertTrue(result.contains("\\p{UnknownXyz}"),
                "unknown property must be passed through verbatim: " + result);
    }
}
