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

public class Pcre2CompileError extends IllegalArgumentException {

    /**
     * The size of the region around the error to show
     */
    private static final int PATTERN_REGION_SIZE = 3;

    /**
     * Create a new pattern compilation error.
     *
     * @param pattern the pattern
     * @param offset  the offset of the error in the pattern
     * @param message the error message
     */
    public Pcre2CompileError(String pattern, long offset, String message) {
        this(pattern, offset, message, null);
    }

    /**
     * Create a new pattern compilation error.
     *
     * @param pattern the pattern
     * @param offset  the offset of the error in the pattern
     * @param message the error message
     * @param cause   the cause of the error
     */
    public Pcre2CompileError(String pattern, long offset, String message, Throwable cause) {
        super("Error in pattern at %d (%s): %s".formatted(offset, getPatternRegion(pattern, offset), message), cause);
    }

    /**
     * Get the region around the error in the pattern.
     *
     * @param pattern the pattern
     * @param offset  the offset of the error in the pattern
     * @return the region around the error
     */
    private static String getPatternRegion(String pattern, long offset) {
        final var since = Math.max(0, offset - PATTERN_REGION_SIZE);
        final var until = Math.min(pattern.length(), offset + PATTERN_REGION_SIZE);

        var region = pattern.substring((int) since, (int) until);
        if (since > 0) {
            region = "…" + region;
        }
        if (until < pattern.length()) {
            region = region + "…";
        }

        return region;
    }

}
