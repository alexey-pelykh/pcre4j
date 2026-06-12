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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchProbeTest {
    @Test
    void okProbe_carriesAllFields() {
        var hit = new Hit(0, 3, "abc", List.of(new Group(null, 0, 3, "abc")));
        var p = new MatchProbe(new Outcome.Ok(), true, true, List.of(hit));
        assertTrue(p.compile() instanceof Outcome.Ok);
        assertEquals(Boolean.TRUE, p.matchesFull());
        assertEquals(1, p.findAll().size());
    }

    @Test
    void compileErrorProbe_hasNullMatchFields() {
        var p = new MatchProbe(new Outcome.SyntaxError("bad"), null, null, List.of());
        assertTrue(p.compile() instanceof Outcome.SyntaxError se && se.message().equals("bad"));
        assertNull(p.matchesFull());
    }
}
