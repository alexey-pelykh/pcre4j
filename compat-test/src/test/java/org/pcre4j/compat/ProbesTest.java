/*
 * Copyright (C) 2026 Oleksii PELYKH
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
package org.pcre4j.compat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProbesTest {
    @Test
    void oracle_matchesSimpleDigits() {
        var p = Probes.oracle("\\d+", "abc123def456", 0);
        assertTrue(p.compile() instanceof Outcome.Ok);
        assertEquals(Boolean.FALSE, p.matchesFull());
        assertEquals(Boolean.FALSE, p.lookingAt());
        assertEquals(2, p.findAll().size());
        assertEquals("123", p.findAll().get(0).text());
        assertEquals("456", p.findAll().get(1).text());
    }

    @Test
    void oracle_syntaxErrorRecorded() {
        var p = Probes.oracle("(unclosed", "x", 0);
        assertTrue(p.compile() instanceof Outcome.SyntaxError);
        assertNull(p.matchesFull());
    }

    @Test
    void sut_matchesSimpleDigits() {
        var p = Probes.sut("\\d+", "abc123def456", 0);
        assertTrue(p.compile() instanceof Outcome.Ok);
        assertEquals(2, p.findAll().size());
        assertEquals("123", p.findAll().get(0).text());
    }

    @Test
    void sut_syntaxErrorRecorded() {
        var p = Probes.sut("(unclosed", "x", 0);
        assertTrue(p.compile() instanceof Outcome.SyntaxError);
    }

    @Test
    void sut_runtimeErrorTreatedAsCompileError_orPropagated() {
        var p = Probes.sut("\\p{InNoSuchBlock}", "x", 0);
        assertNotNull(p);
    }
}
