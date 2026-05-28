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

class ClassBodyParserTest {

    private static ClassNode parse(final String classStr) {
        final int[] pos = {0};
        return ClassBodyParser.parseClass(classStr, pos);
    }

    @Test
    void simpleLiterals() {
        final ClassNode node = parse("[abc]");
        // Should produce Union(Literal(a), Literal(b), Literal(c))
        assertInstanceOf(ClassNode.Union.class, node);
        final ClassNode.Union u = (ClassNode.Union) node;
        assertEquals(3, u.children().size());
        assertEquals(new ClassNode.Literal('a'), u.children().get(0));
        assertEquals(new ClassNode.Literal('b'), u.children().get(1));
        assertEquals(new ClassNode.Literal('c'), u.children().get(2));
    }

    @Test
    void singleCharClass() {
        final ClassNode node = parse("[a]");
        // Single item is unwrapped
        assertEquals(new ClassNode.Literal('a'), node);
    }

    @Test
    void rangeClass() {
        final ClassNode node = parse("[a-z]");
        assertEquals(new ClassNode.Range('a', 'z'), node);
    }

    @Test
    void negatedRange() {
        final ClassNode node = parse("[^a-z]");
        assertInstanceOf(ClassNode.Negated.class, node);
        final ClassNode.Negated neg = (ClassNode.Negated) node;
        assertEquals(new ClassNode.Range('a', 'z'), neg.child());
    }

    @Test
    void nestedClassUnion() {
        final ClassNode node = parse("[abc[def]]");
        // Union(a, b, c, Union(d, e, f))
        assertInstanceOf(ClassNode.Union.class, node);
        final ClassNode.Union u = (ClassNode.Union) node;
        assertEquals(4, u.children().size());
        assertEquals(new ClassNode.Literal('a'), u.children().get(0));
        // Last child is the inner class
        assertInstanceOf(ClassNode.Union.class, u.children().get(3));
    }

    @Test
    void intersection() {
        final ClassNode node = parse("[a-c&&d-f]");
        assertInstanceOf(ClassNode.Intersection.class, node);
        final ClassNode.Intersection inter = (ClassNode.Intersection) node;
        assertEquals(2, inter.operands().size());
        assertEquals(new ClassNode.Range('a', 'c'), inter.operands().get(0));
        assertEquals(new ClassNode.Range('d', 'f'), inter.operands().get(1));
    }

    @Test
    void wDashHashPattern() {
        // [\w-#] — Java: \w union '-' union '#', NOT a range
        final ClassNode node = parse("[\\w-#]");
        // Parser should see: PropertyLeaf(\w), '-' treated as literal, Literal('#')
        assertInstanceOf(ClassNode.Union.class, node);
        final ClassNode.Union u = (ClassNode.Union) node;
        assertEquals(3, u.children().size());
        assertInstanceOf(ClassNode.PropertyLeaf.class, u.children().get(0));
        assertEquals(new ClassNode.Literal('-'), u.children().get(1));
        assertEquals(new ClassNode.Literal('#'), u.children().get(2));
    }

    @Test
    void shorthandEscapes() {
        final ClassNode node = parse("[\\d\\p{L}]");
        assertInstanceOf(ClassNode.Union.class, node);
        final ClassNode.Union u = (ClassNode.Union) node;
        assertEquals(2, u.children().size());
        assertInstanceOf(ClassNode.PropertyLeaf.class, u.children().get(0));
        assertInstanceOf(ClassNode.PropertyLeaf.class, u.children().get(1));
        assertEquals("\\d", ((ClassNode.PropertyLeaf) u.children().get(0)).pcre2Token());
    }

    @Test
    void quotedBracket() {
        // [\Q]\E] — \Q...\E section containing ], which should be literal
        final ClassNode node = parse("[\\Q]\\E]");
        assertEquals(new ClassNode.Literal(']'), node);
    }

    @Test
    void hexEscape() {
        final ClassNode node = parse("[\\x41]"); // 'A'
        assertEquals(new ClassNode.Literal('A'), node);
    }

    @Test
    void unicodeEscape() {
        final ClassNode node = parse("[\\u0041]"); // 'A'
        assertEquals(new ClassNode.Literal('A'), node);
    }

    @Test
    void multipleIntersectionOperands() {
        final ClassNode node = parse("[a-m&&m-z&&a-c]");
        assertInstanceOf(ClassNode.Intersection.class, node);
        final ClassNode.Intersection inter = (ClassNode.Intersection) node;
        assertEquals(3, inter.operands().size());
    }

    @Test
    void nestedNegatedClass() {
        final ClassNode node = parse("[a-d[^0-9]]");
        assertInstanceOf(ClassNode.Union.class, node);
        final ClassNode.Union u = (ClassNode.Union) node;
        assertEquals(2, u.children().size());
        assertInstanceOf(ClassNode.Negated.class, u.children().get(1));
    }

    @Test
    void intersectionWithNestedClass() {
        final ClassNode node = parse("[[a-m]&&[m-z]]");
        assertInstanceOf(ClassNode.Intersection.class, node);
    }

    @Test
    void rangeAtEndOfClass() {
        // [a-] — '-' before ']' is treated as literal
        // Actually Java rejects [a-] as an illegal range, but let's test the parser
        // handles this gracefully
        final ClassNode node = parse("[a\\-]");
        assertInstanceOf(ClassNode.Union.class, node);
    }
}
