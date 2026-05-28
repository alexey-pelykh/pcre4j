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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EvaluatorTest {

    // Verifies every entry in the POSIX/shorthand expansion table resolves to a non-null RangeSet
    // that contains the canonical sample code point for that class. Adding/removing a row here
    // must be paired with a corresponding change in Evaluator.expandProperty.
    @ParameterizedTest
    @CsvSource({
            // token,     sample-cp-that-must-be-in-the-set
            "'\\d',         48",   // '0'
            "'\\w',         95",   // '_'
            "'\\s',         32",   // ' '
            "'\\p{ASCII}',  65",   // 'A'
            "'\\p{Alpha}',  65",
            "'\\p{Alnum}',  48",
            "'\\p{Lower}',  97",
            "'\\p{Upper}',  65",
            "'\\p{Digit}',  48",
            "'\\p{XDigit}', 102",  // 'f'
            "'\\p{Space}',  32",
            "'\\p{Blank}',  9",    // tab
            "'\\p{Cntrl}',  0",
            "'\\p{Graph}',  33",   // '!'
            "'\\p{Print}',  32",
            "'\\p{Punct}',  46",   // '.'
    })
    void positivePosixShorthandsContainExpectedCodePoint(final String token, final int cp)
            throws EvaluationFailedException {
        final RangeSet rs = Evaluator.toRangeSet(new ClassNode.PropertyLeaf(token, false));
        assertNotNull(rs, "expected RangeSet for " + token);
        assertTrue(rs.contains(cp),
                token + " should contain U+" + Integer.toHexString(cp) + " but didn't: " + rs);
    }

    // Spot-check negated complements: a code point NOT in the positive set must be in \D / \W / \S
    @Test
    void negatedShorthandsComplementCorrectly() throws EvaluationFailedException {
        final RangeSet nd = Evaluator.toRangeSet(new ClassNode.PropertyLeaf("\\D", true));
        assertTrue(nd.contains('a'), "\\D must contain letters");
        assertFalse(nd.contains('0'), "\\D must NOT contain digits");

        final RangeSet ns = Evaluator.toRangeSet(new ClassNode.PropertyLeaf("\\S", true));
        assertFalse(ns.contains(' '), "\\S must NOT contain space");
        assertTrue(ns.contains('a'), "\\S must contain 'a'");
    }

    @Test
    void unknownPropertyThrowsEvaluationFailed() {
        final ClassNode.PropertyLeaf leaf =
                new ClassNode.PropertyLeaf("\\p{ThisPropertyDoesNotExistXyz}", false);
        assertThrows(EvaluationFailedException.class,
                () -> Evaluator.toRangeSet(leaf));
    }

    @Test
    void unknownPropertyInsideIntersectionThrows() {
        // [\p{UnknownXyz} && [a-z]] — the property cannot expand, so the whole intersection fails.
        final var unknown = new ClassNode.PropertyLeaf("\\p{UnknownXyz}", false);
        final var letters = new ClassNode.Range('a', 'z');
        final var inter = new ClassNode.Intersection(java.util.List.of(unknown, letters));
        assertThrows(EvaluationFailedException.class, () -> Evaluator.toRangeSet(inter));
    }

    @Test
    void tryToRangeSetReturnsNullOnFailure() {
        final ClassNode.PropertyLeaf leaf =
                new ClassNode.PropertyLeaf("\\p{UnknownXyz}", false);
        assertNull(Evaluator.tryToRangeSet(leaf),
                "tryToRangeSet must return null instead of throwing on unknown property");
    }

    @Test
    void tryToRangeSetReturnsRangeSetOnSuccess() {
        final ClassNode.PropertyLeaf leaf = new ClassNode.PropertyLeaf("\\d", false);
        final RangeSet rs = Evaluator.tryToRangeSet(leaf);
        assertNotNull(rs);
        assertTrue(rs.contains('5'));
    }
}
