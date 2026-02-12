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
package org.pcre4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link Pcre4j} bootstrap failure modes.
 *
 * <p>Verifies that the bootstrap singleton produces clear, actionable errors for misconfiguration scenarios.</p>
 */
public class Pcre4jTests {

    @BeforeEach
    void resetSingleton() throws Exception {
        Field apiField = Pcre4j.class.getDeclaredField("api");
        apiField.setAccessible(true);
        apiField.set(null, null);
    }

    @Test
    void api_beforeSetup_throwsIllegalStateException() {
        var error = assertThrows(
                IllegalStateException.class,
                () -> Pcre4j.api()
        );
        assertNotNull(error.getMessage(), "Error message must not be null");
        assertTrue(
                error.getMessage().contains("setup"),
                "Error message should mention setup(), got: " + error.getMessage()
        );
    }

    @Test
    void setup_nullApi_throwsIllegalArgumentException() {
        var error = assertThrows(
                IllegalArgumentException.class,
                () -> Pcre4j.setup(null)
        );
        assertNotNull(error.getMessage(), "Error message must not be null");
        assertTrue(
                error.getMessage().contains("null"),
                "Error message should indicate the null argument, got: " + error.getMessage()
        );
    }
}
