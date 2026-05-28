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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdkPropertyExpanderTest {

    @Test
    void asciiLetterCoverage() {
        final RangeSet l = JdkPropertyExpander.expand("\\p{L}");
        assertNotNull(l);
        assertTrue(l.contains('a'));
        assertTrue(l.contains('Z'));
        assertFalse(l.contains('0'));
        assertFalse(l.contains(' '));
    }

    @Test
    void greekScript() {
        final RangeSet g = JdkPropertyExpander.expand("\\p{Greek}");
        assertNotNull(g);
        assertTrue(g.contains(0x03B1));  // α (GREEK SMALL LETTER ALPHA)
        assertFalse(g.contains('a'));     // Latin 'a' is not Greek
    }

    @Test
    void negatedProperty() {
        final RangeSet notL = JdkPropertyExpander.expand("\\P{L}");
        assertNotNull(notL);
        assertFalse(notL.contains('a'));
        assertTrue(notL.contains('0'));
    }

    @Test
    void unknownReturnsNull() {
        assertNull(JdkPropertyExpander.expand("\\p{FooBarBaz}"));
    }

    @Test
    void caches() {
        final RangeSet first = JdkPropertyExpander.expand("\\p{L}");
        final RangeSet second = JdkPropertyExpander.expand("\\p{L}");
        assertSame(first, second);
    }

    @Test
    void greekIntersectionWithLetters() {
        // \p{L} ∩ \P{Greek} should contain 'a' but not α
        final RangeSet letters = JdkPropertyExpander.expand("\\p{L}");
        final RangeSet notGreek = JdkPropertyExpander.expand("\\P{Greek}");
        assertNotNull(letters);
        assertNotNull(notGreek);
        final RangeSet lettersNotGreek = letters.intersect(notGreek);
        assertTrue(lettersNotGreek.contains('a'));      // Latin 'a' — letter, not Greek
        assertTrue(lettersNotGreek.contains(0x6000));   // CJK ideograph — letter, not Greek
        assertFalse(lettersNotGreek.contains(0x03B1));  // α — letter BUT Greek
    }

    @Test
    void leafCategoryLu() {
        final RangeSet lu = JdkPropertyExpander.expand("\\p{Lu}");
        assertNotNull(lu);
        assertTrue(lu.contains('A'));
        assertFalse(lu.contains('a'));
        assertFalse(lu.contains('0'));
    }

    @Test
    void combinedCategoryN() {
        final RangeSet n = JdkPropertyExpander.expand("\\p{N}");
        assertNotNull(n);
        assertTrue(n.contains('0'));    // Nd
        assertFalse(n.contains('a'));
    }

    @Test
    void nonPropertyTokenReturnsNull() {
        // \d, \w are handled by Evaluator directly; JdkPropertyExpander should not claim them
        assertNull(JdkPropertyExpander.expand("\\d"));
        assertNull(JdkPropertyExpander.expand("\\w"));
    }
}
