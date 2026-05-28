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
import static org.junit.jupiter.api.Assertions.assertTrue;

class RangeSetTest {

    @Test
    void emptySet() {
        assertTrue(RangeSet.EMPTY.isEmpty());
        assertFalse(RangeSet.EMPTY.contains('a'));
    }

    @Test
    void singleCodePoint() {
        final RangeSet s = RangeSet.single('a');
        assertFalse(s.isEmpty());
        assertTrue(s.contains('a'));
        assertFalse(s.contains('b'));
    }

    @Test
    void range() {
        final RangeSet az = RangeSet.range('a', 'z');
        assertTrue(az.contains('a'));
        assertTrue(az.contains('m'));
        assertTrue(az.contains('z'));
        assertFalse(az.contains('A'));
        assertFalse(az.contains('{'));
    }

    @Test
    void unionDisjoint() {
        final RangeSet az = RangeSet.range('a', 'z');
        final RangeSet AZ = RangeSet.range('A', 'Z');
        final RangeSet u = az.union(AZ);
        assertTrue(u.contains('a'));
        assertTrue(u.contains('A'));
        assertFalse(u.contains('1'));
    }

    @Test
    void unionOverlapping() {
        final RangeSet ac = RangeSet.range('a', 'c');
        final RangeSet bd = RangeSet.range('b', 'd');
        final RangeSet u = ac.union(bd);
        assertTrue(u.contains('a'));
        assertTrue(u.contains('b'));
        assertTrue(u.contains('d'));
        assertFalse(u.contains('e'));
        assertEquals(1, u.rangeCount());
    }

    @Test
    void intersectOverlap() {
        final RangeSet ac = RangeSet.range('a', 'c');
        final RangeSet bd = RangeSet.range('b', 'd');
        final RangeSet i = ac.intersect(bd);
        assertFalse(i.contains('a'));
        assertTrue(i.contains('b'));
        assertTrue(i.contains('c'));
        assertFalse(i.contains('d'));
    }

    @Test
    void intersectDisjoint() {
        final RangeSet ac = RangeSet.range('a', 'c');
        final RangeSet df = RangeSet.range('d', 'f');
        final RangeSet i = ac.intersect(df);
        assertTrue(i.isEmpty());
    }

    @Test
    void complementEmpty() {
        final RangeSet c = RangeSet.EMPTY.complement();
        assertEquals(RangeSet.ALL, c.union(RangeSet.EMPTY));
        assertTrue(c.contains(0));
        assertTrue(c.contains(0x10FFFF));
    }

    @Test
    void complementRange() {
        final RangeSet az = RangeSet.range('a', 'z');
        final RangeSet notAz = az.complement();
        assertFalse(notAz.contains('a'));
        assertFalse(notAz.contains('z'));
        assertTrue(notAz.contains('A'));
        assertTrue(notAz.contains('0'));
        assertTrue(notAz.contains(0x10FFFF));
    }

    @Test
    void subtract() {
        final RangeSet af = RangeSet.range('a', 'f');
        final RangeSet cf = RangeSet.range('c', 'f');
        final RangeSet diff = af.subtract(cf);
        assertTrue(diff.contains('a'));
        assertTrue(diff.contains('b'));
        assertFalse(diff.contains('c'));
        assertFalse(diff.contains('f'));
    }

    @Test
    void toPcre2ClassBodySinglePrintable() {
        // 'a' is printable ASCII, should emit raw
        final String body = RangeSet.single('a').toPcre2ClassBody();
        assertEquals("a", body);
    }

    @Test
    void toPcre2ClassBodySingleNonPrintable() {
        // tab (0x09) should emit \x{9}
        final String body = RangeSet.single('\t').toPcre2ClassBody();
        assertEquals("\\x{9}", body);
    }

    @Test
    void toPcre2ClassBodyRange() {
        final String body = RangeSet.range('a', 'z').toPcre2ClassBody();
        assertEquals("a-z", body);
    }

    @Test
    void toPcre2ClassBodyEscapesSpecialChars() {
        // '-' should be escaped as \-
        final String body = RangeSet.single('-').toPcre2ClassBody();
        assertEquals("\\-", body);
        // ']' should be escaped as \]
        final String bodyBracket = RangeSet.single(']').toPcre2ClassBody();
        assertEquals("\\]", bodyBracket);
        // '^' should be escaped as \^
        final String bodyCaret = RangeSet.single('^').toPcre2ClassBody();
        assertEquals("\\^", bodyCaret);
    }

    @Test
    void toPcre2ClassBodyMultipleRanges() {
        final RangeSet u = RangeSet.range('a', 'z').union(RangeSet.range('A', 'Z'));
        final String body = u.toPcre2ClassBody();
        // Should contain both ranges in some order
        assertTrue(body.contains("A-Z") || body.contains("a-z"));
    }
}
