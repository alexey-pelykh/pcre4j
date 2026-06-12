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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertyMapTest {

    @Test
    void inPrefixStrip() {
        // PR #606 review F3: InXxxx now materialises to a Character.UnicodeBlock range,
        // not the (potentially-incorrect) PCRE2 script alias.
        assertEquals("[\\x{370}-\\x{3FF}]", PropertyMap.apply("InGreek"));
    }

    @Test
    void isPrefixStrip() {
        assertEquals("L", PropertyMap.apply("IsL"));
    }

    @Test
    void unknownReturnsNull() {
        assertNull(PropertyMap.apply("FooBarBaz"));
    }

    @Test
    void l1ExpandsToRange() {
        assertEquals("[\\x{00}-\\x{FF}]", PropertyMap.apply("L1"));
    }

    @Test
    void javaLowerCase() {
        // PR #606 review F4: javaLowerCase must NOT be a Ll alias — Character.isLowerCase()
        // is a strict superset (e.g. U+00AA ª has gc=Lo but isLowerCase).
        final String result = PropertyMap.apply("javaLowerCase");
        assertTrue(result.startsWith("[") && result.contains("\\x{AA}"),
                "Expected materialised class with U+00AA, got: " + result);
    }

    @Test
    void highSurrogatesNeverMatchSentinel() {
        // PR #606 review F2: PCRE2 in UTF mode refuses surrogate ranges; PropertyMap signals
        // this with the NEVER_MATCH sentinel that the translator turns into (?!).
        assertEquals(PropertyMap.NEVER_MATCH, PropertyMap.apply("InHIGH_SURROGATES"));
    }

    @Test
    void lowSurrogatesNeverMatchSentinel() {
        assertEquals(PropertyMap.NEVER_MATCH, PropertyMap.apply("InLOW_SURROGATES"));
    }

    @Test
    void isAsciiStripsIs() {
        assertEquals("ASCII", PropertyMap.apply("IsASCII"));
    }

    @Test
    void javaDefinedMapsToNegatedCn() {
        assertEquals("\\P{Cn}", PropertyMap.apply("javaDefined"));
    }
}
