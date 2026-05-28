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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        // \\p{X} is "literal backslash" + "p" + "{X}" — JDK itself rejects this with
        // "Illegal repetition" because {X} after the literal 'p' is not a valid quantifier.
        // Our translator must match JDK by throwing PatternSyntaxException.
        assertThrows(java.util.regex.PatternSyntaxException.class,
                () -> JavaRegexTranslator.translate("\\\\p{InGreek}", 0));
    }

    @Test
    void rejectsIllegalQuantifierBody() {
        // a{^InGreek} — JDK rejects, PCRE2 accepts. Translator pre-rejects for JDK parity.
        assertThrows(java.util.regex.PatternSyntaxException.class,
                () -> JavaRegexTranslator.translate("a{^InGreek}", 0));
        assertThrows(java.util.regex.PatternSyntaxException.class,
                () -> JavaRegexTranslator.translate("a{}", 0));
        assertThrows(java.util.regex.PatternSyntaxException.class,
                () -> JavaRegexTranslator.translate("a{,3}", 0));
    }

    @Test
    void acceptsValidQuantifiers() {
        assertEquals("a{3}", JavaRegexTranslator.translate("a{3}", 0));
        assertEquals("a{3,}", JavaRegexTranslator.translate("a{3,}", 0));
        assertEquals("a{3,5}", JavaRegexTranslator.translate("a{3,5}", 0));
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
        // [a-c] ∩ [d-f] is empty — translator emits an unmatched-by-anything class
        assertEquals("[^\\x{0}-\\x{10FFFF}]", result);
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

    // --- Phase 3: inline mode-flag translator ---

    @Test
    void dropsUFlagInModeModifier() {
        assertEquals("(?i)foo", JavaRegexTranslator.translate("(?iu)foo", 0));
        assertEquals("(?i)foo", JavaRegexTranslator.translate("(?ui)foo", 0));
        assertEquals("(?im)foo", JavaRegexTranslator.translate("(?ium)foo", 0));
    }

    @Test
    void dropsUInScopedGroup() {
        assertEquals("(?i:foo)", JavaRegexTranslator.translate("(?iu:foo)", 0));
    }

    @Test
    void dropsDFlag() {
        assertEquals("(?m)foo", JavaRegexTranslator.translate("(?dm)foo", 0));
    }

    @Test
    void emptyFlagsRemovedEntirely() {
        assertEquals("foo", JavaRegexTranslator.translate("(?u)foo", 0));
        assertEquals("(?:foo)", JavaRegexTranslator.translate("(?u:foo)", 0));
    }

    @Test
    void preservesNonModeGroups() {
        assertEquals("(?:foo)", JavaRegexTranslator.translate("(?:foo)", 0));
        assertEquals("(?=foo)", JavaRegexTranslator.translate("(?=foo)", 0));
        assertEquals("(?<name>foo)", JavaRegexTranslator.translate("(?<name>foo)", 0));
        assertEquals("(?#comment)foo", JavaRegexTranslator.translate("(?#comment)foo", 0));
    }

    @Test
    void handlesOnOffFlagGroup() {
        // (?iu-mU)foo — u dropped from on, U dropped from off → (?i-m)foo
        assertEquals("(?i-m)foo", JavaRegexTranslator.translate("(?iu-mU)foo", 0));
    }

    @Test
    void allFlagsDroppedFromOnOff() {
        // (?u-U)foo — both sides empty after filtering → drop entire token
        assertEquals("foo", JavaRegexTranslator.translate("(?u-U)foo", 0));
    }

    @Test
    void doesNotTouchInsideClass() {
        // `(?i)` inside `[…]` is literal chars — Phase 2 handles the class body
        assertEquals("[(?i)]", JavaRegexTranslator.translate("[(?i)]", 0));
    }

    @Test
    void propertyIntersectionEndToEnd() {
        // [\p{L}&&[\P{InGreek}]] — letters that are NOT Greek
        // After translation this should be a flat class (no &&) that PCRE2 can compile
        final String out = JavaRegexTranslator.translate("[\\p{L}&&[\\P{InGreek}]]", 0);
        assertFalse(out.contains("&&"), "Should not contain && after evaluation: " + out);
        assertFalse(out.contains("[["), "Should not have nested [[ after evaluation: " + out);
        // Positive content checks: the ASCII Latin letter ranges must survive the intersection
        // (they are Letters and not Greek), while the Greek block U+0370..U+03FF must be excluded
        // — no range may span any Greek code point.
        assertTrue(out.contains("A-Z"),
                "Latin uppercase range A-Z must be retained: " + out);
        assertTrue(out.contains("a-z"),
                "Latin lowercase range a-z must be retained: " + out);
        assertFalse(out.contains("\\x{3B1}") || out.contains("\\x{3A9}"),
                "Greek letters (α/Ω) must not appear as standalone code points: " + out);
    }

    // --- Escaped quantifier-brace must not be rejected (review critical #1) ---

    @Test
    void escapedBraceIsNotQuantifier() {
        // "\\{" is a literal '{' and must pass through unchanged, not raise "Illegal repetition".
        // JDK accepts this pattern; PCRE4J must too.
        assertEquals("\\{", JavaRegexTranslator.translate("\\{", 0));
        assertEquals("a\\{b}", JavaRegexTranslator.translate("a\\{b}", 0));
        assertEquals("\\{not-a-quantifier}", JavaRegexTranslator.translate("\\{not-a-quantifier}", 0));
    }

    @Test
    void doubleBackslashThenBraceStillQuantifier() {
        // "\\\\{x}" is "literal backslash" + "{x}" which IS in quantifier position → reject.
        assertThrows(java.util.regex.PatternSyntaxException.class,
                () -> JavaRegexTranslator.translate("\\\\{x}", 0));
    }
}
