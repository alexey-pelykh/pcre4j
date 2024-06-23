/*
 * Copyright (C) 2024 Oleksii PELYKH
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
package org.pcre4j;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * {@link Pcre4jUtils} tests.
 */
public class Pcre4jUtilsTests {

    @Test
    void convertOvectorToStringIndices() {
        final var subject = "0123456789";

        assertArrayEquals(
                new int[]{-1, -1},
                Pcre4jUtils.convertOvectorToStringIndices(subject, new long[]{-1, -1})
        );
        assertArrayEquals(
                new int[]{-1, -1, 0, 0},
                Pcre4jUtils.convertOvectorToStringIndices(subject, new long[]{-1, -1, 0, 0})
        );
        assertArrayEquals(
                new int[]{-1, -1, 0, 10},
                Pcre4jUtils.convertOvectorToStringIndices(subject, new long[]{-1, -1, 0, 10})
        );
    }

}
