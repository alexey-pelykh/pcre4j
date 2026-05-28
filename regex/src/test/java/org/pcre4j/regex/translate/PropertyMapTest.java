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

class PropertyMapTest {

    @Test
    void inPrefixStrip() {
        assertEquals("Greek", PropertyMap.apply("InGreek"));
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
        assertEquals("Ll", PropertyMap.apply("javaLowerCase"));
    }

    @Test
    void highSurrogatesExpandToRange() {
        assertEquals("[\\x{D800}-\\x{DB7F}]", PropertyMap.apply("InHIGH_SURROGATES"));
    }

    @Test
    void lowSurrogatesExpandToRange() {
        assertEquals("[\\x{DC00}-\\x{DFFF}]", PropertyMap.apply("InLOW_SURROGATES"));
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
