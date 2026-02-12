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
package org.pcre4j.jna;

import org.pcre4j.test.Pcre2LoadingContractTest;

/**
 * Tests for JNA backend native library loading failure modes.
 *
 * <p>Verifies that the JNA {@link Pcre2} backend produces clear, actionable errors when the PCRE2 native library
 * cannot be loaded.</p>
 */
class Pcre2LoadingTests implements Pcre2LoadingContractTest {

    @Override
    public void createBackend(String library, String suffix) {
        new Pcre2(library, suffix);
    }
}
