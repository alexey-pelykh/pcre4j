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
import static org.junit.jupiter.api.Assertions.*;

class TxtCaseRunnerTest {

    @Test
    void parsesTriple() {
        String body = """
                // comment
                \\d+
                abc123
                true 123

                ^x$
                y
                false
                """;
        List<TxtCaseRunner.Case> cases = TxtCaseRunner.parse(body);
        assertEquals(2, cases.size());
        assertEquals("\\d+", cases.get(0).pattern());
        assertEquals("abc123", cases.get(0).input());
        assertTrue(cases.get(0).expectedMatch());
        assertEquals("y", cases.get(1).input());
        assertFalse(cases.get(1).expectedMatch());
    }

    @Test
    void parsesRealTestCasesTxt() throws Exception {
        String body = new String(getClass().getResourceAsStream("/imported/TestCases.txt").readAllBytes());
        var cases = TxtCaseRunner.parse(body);
        assertTrue(cases.size() > 50, "expected > 50 cases, got " + cases.size());
        assertTrue(cases.stream().anyMatch(TxtCaseRunner.Case::expectedMatch));
        assertTrue(cases.stream().anyMatch(c -> !c.expectedMatch()));
    }
}
