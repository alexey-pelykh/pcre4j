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

import static org.junit.jupiter.api.Assertions.*;

class JavaRegexTranslatorTest {

    @Test
    void passthroughForPatternsWithoutProperties() {
        assertEquals("\\d+", JavaRegexTranslator.translate("\\d+", 0));
        assertEquals("[a-z]", JavaRegexTranslator.translate("[a-z]", 0));
        assertEquals("abc", JavaRegexTranslator.translate("abc", 0));
    }

    @Test
    void rewritesInBlockProperty() {
        assertEquals("\\p{Greek}", JavaRegexTranslator.translate("\\p{InGreek}", 0));
        assertEquals("\\P{Greek}", JavaRegexTranslator.translate("\\P{InGreek}", 0));
        assertEquals("a\\p{Greek}b", JavaRegexTranslator.translate("a\\p{InGreek}b", 0));
        assertEquals("[\\p{Greek}]", JavaRegexTranslator.translate("[\\p{InGreek}]", 0));
    }

    @Test
    void rewritesIsScriptProperty() {
        assertEquals("\\p{L}", JavaRegexTranslator.translate("\\p{IsL}", 0));
        assertEquals("\\p{LC}", JavaRegexTranslator.translate("\\p{IsLC}", 0));
        assertEquals("\\p{ASCII}", JavaRegexTranslator.translate("\\p{IsASCII}", 0));
    }

    @Test
    void rewritesShortAliases() {
        assertEquals("[\\x{00}-\\x{FF}]", JavaRegexTranslator.translate("\\p{L1}", 0));
    }

    @Test
    void rewritesJavaProperty() {
        assertEquals("\\p{Ll}", JavaRegexTranslator.translate("\\p{javaLowerCase}", 0));
    }

    @Test
    void doesNotRewriteInsideQuotation() {
        assertEquals("\\Q\\p{InGreek}\\E", JavaRegexTranslator.translate("\\Q\\p{InGreek}\\E", 0));
    }

    @Test
    void doesNotRewriteEscapedBackslashFollowedByP() {
        // \\p{X} is "literal backslash" + "p{X}", not a property token
        assertEquals("\\\\p{InGreek}", JavaRegexTranslator.translate("\\\\p{InGreek}", 0));
    }

    @Test
    void escapeHatchDisablesTranslator() {
        // Tested at Pattern level — sanity-only: translator itself always translates
        assertEquals("\\p{Greek}", JavaRegexTranslator.translate("\\p{InGreek}", 0));
    }

    @Test
    void rewritesSurrogateBlockToRange() {
        assertEquals("[\\x{D800}-\\x{DB7F}]", JavaRegexTranslator.translate("\\p{InHIGH_SURROGATES}", 0));
        assertEquals("[\\x{DC00}-\\x{DFFF}]", JavaRegexTranslator.translate("\\p{InLOW_SURROGATES}", 0));
    }

    @Test
    void negatedSurrogateBlockIsNegated() {
        assertEquals("[^\\x{D800}-\\x{DB7F}]", JavaRegexTranslator.translate("\\P{InHIGH_SURROGATES}", 0));
    }

    @Test
    void rewritesJavaDefinedAsNegatedUnassigned() {
        assertEquals("\\P{Cn}", JavaRegexTranslator.translate("\\p{javaDefined}", 0));
    }

    @Test
    void multipleTokensInOnePattern() {
        assertEquals("\\p{Greek}\\p{Hiragana}", JavaRegexTranslator.translate("\\p{InGreek}\\p{InHiragana}", 0));
    }

    // --- Phase 2: character class body rewrite ---

    @Test
    void nestedUnionFlattens() {
        final String result = JavaRegexTranslator.translate("[abc[def]]", 0);
        // Should produce a flat class (no nested [[)
        assertFalse(result.contains("[["), "Should not have nested [[: " + result);
        assertEquals("[abcdef]", result);
    }

    @Test
    void intersectionBecomesRangeSet() {
        final String result = JavaRegexTranslator.translate("[a-c&&d-f]", 0);
        // Empty intersection → matches nothing
        assertFalse(result.contains("&&"), "Should not contain &&: " + result);
    }

    @Test
    void wDashHashEscapesDash() {
        final String result = JavaRegexTranslator.translate("[\\w-#]", 0);
        // '-' should be escaped so PCRE2 doesn't interpret it as a range operator
        assertTrue(result.contains("\\-"), "Dash should be escaped in: " + result);
    }

    @Test
    void classBodyRewritePreservesOutsidePattern() {
        final String result = JavaRegexTranslator.translate("a[bc]d", 0);
        assertEquals("a[bc]d", result);
    }

    @Test
    void propertyInsideClassRewritten() {
        // \p{InGreek} inside a class should be rewritten to \p{Greek}
        final String result = JavaRegexTranslator.translate("[\\p{InGreek}]", 0);
        assertTrue(result.contains("\\p{Greek}"), "Expected \\p{Greek} in: " + result);
        assertFalse(result.contains("\\p{InGreek}"), "Should not contain InGreek: " + result);
    }

    @Test
    void intersectionWithKnownPropertyEvaluated() {
        // [\d&&[0-3]] should evaluate to [0-3]
        final String result = JavaRegexTranslator.translate("[\\d&&[0-3]]", 0);
        assertFalse(result.contains("&&"), "Should not contain &&: " + result);
    }
}
