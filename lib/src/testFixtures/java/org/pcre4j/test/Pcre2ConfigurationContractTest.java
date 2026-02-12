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
package org.pcre4j.test;

import org.junit.jupiter.api.Test;
import org.pcre4j.Pcre4jUtils;
import org.pcre4j.api.IPcre2;
import org.pcre4j.api.Pcre2UtfWidth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contract tests for PCRE2 configuration and version queries.
 *
 * @param <T> the PCRE2 API implementation type
 */
public interface Pcre2ConfigurationContractTest<T extends IPcre2> {

    /**
     * Returns the PCRE2 API implementation to test.
     *
     * @return the PCRE2 API implementation
     */
    T getApi();

    @Test
    default void getVersion() {
        var version = Pcre4jUtils.getVersion(getApi());
        assertNotNull(version);
        assertFalse(version.isEmpty(), "version must not be empty");
        assertTrue(version.matches("\\d+\\.\\d+.*"), "version must match 'major.minor...' format: " + version);
    }

    @Test
    default void getUnicodeVersion() {
        var unicodeVersion = Pcre4jUtils.getUnicodeVersion(getApi());
        assertNotNull(unicodeVersion);
        assertFalse(unicodeVersion.isEmpty(), "unicode version must not be empty");
        assertTrue(
                unicodeVersion.matches("\\d+\\.\\d+\\.\\d+"),
                "unicode version must match 'major.minor.patch' format: " + unicodeVersion
        );
    }

    @Test
    default void getDefaultParenthesesNestingLimit() {
        var limit = Pcre4jUtils.getDefaultParenthesesNestingLimit(getApi());
        assertTrue(limit > 0, "parentheses nesting limit must be positive: " + limit);
    }

    @Test
    default void getDefaultNewline() {
        var newline = Pcre4jUtils.getDefaultNewline(getApi());
        assertNotNull(newline);
    }

    @Test
    default void getDefaultMatchLimit() {
        var limit = Pcre4jUtils.getDefaultMatchLimit(getApi());
        assertTrue(limit > 0, "match limit must be positive: " + limit);
    }

    @Test
    default void getInternalLinkSize() {
        var linkSize = Pcre4jUtils.getInternalLinkSize(getApi());
        assertTrue(
                linkSize == 2 || linkSize == 3 || linkSize == 4,
                "internal link size must be 2, 3, or 4: " + linkSize
        );
    }

    @Test
    default void getJitTarget() {
        var jitTarget = Pcre4jUtils.getJitTarget(getApi());
        if (jitTarget != null) {
            assertFalse(jitTarget.isEmpty(), "JIT target must not be empty when present");
        }
    }

    @Test
    default void getDefaultHeapLimit() {
        var limit = Pcre4jUtils.getDefaultHeapLimit(getApi());
        assertTrue(limit >= 0, "heap limit must be non-negative: " + limit);
    }

    @Test
    default void getDefaultDepthLimit() {
        var limit = Pcre4jUtils.getDefaultDepthLimit(getApi());
        assertTrue(limit > 0, "depth limit must be positive: " + limit);
    }

    @Test
    default void getCompiledWidths() {
        var widths = Pcre4jUtils.getCompiledWidths(getApi());
        assertNotNull(widths);
        assertFalse(widths.isEmpty(), "compiled widths must not be empty");
        assertTrue(widths.contains(Pcre2UtfWidth.UTF8), "compiled widths must include UTF-8");
    }

    @Test
    default void getDefaultBsr() {
        var bsr = Pcre4jUtils.getDefaultBsr(getApi());
        assertNotNull(bsr);
    }
}
