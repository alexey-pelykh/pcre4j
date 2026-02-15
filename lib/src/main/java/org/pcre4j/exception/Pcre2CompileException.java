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
package org.pcre4j.exception;

/**
 * An exception that occurs when a pattern fails to compile.
 */
public class Pcre2CompileException extends Pcre2Exception {

    /**
     * The size of the region around the error to show
     */
    private static final int PATTERN_REGION_SIZE = 3;

    /**
     * The pattern that caused the error
     */
    private final String pattern;

    /**
     * The offset of the error in the pattern
     */
    private final long offset;

    /**
     * The error message
     */
    private final String message;

    /**
     * Create a new pattern compilation exception.
     *
     * @param pattern   the pattern
     * @param offset    the offset of the error in the pattern
     * @param message   the error message
     * @param errorCode the PCRE2 native error code
     */
    public Pcre2CompileException(String pattern, long offset, String message, int errorCode) {
        this(pattern, offset, message, errorCode, null);
    }

    /**
     * Create a new pattern compilation exception.
     *
     * @param pattern   the pattern
     * @param offset    the offset of the error in the pattern
     * @param message   the error message
     * @param errorCode the PCRE2 native error code
     * @param cause     the cause of the exception
     */
    public Pcre2CompileException(String pattern, long offset, String message, int errorCode, Throwable cause) {
        super(
                "Error in pattern at %d \"%s\": %s".formatted(offset, getPatternRegion(pattern, offset), message),
                errorCode,
                cause
        );
        this.pattern = pattern;
        this.offset = offset;
        this.message = message;
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
            region = "\u2026" + region;
        }
        if (until < pattern.length()) {
            region = region + "\u2026";
        }

        return region;
    }

    /**
     * Get the pattern that caused the error.
     *
     * @return the pattern
     */
    public String pattern() {
        return pattern;
    }

    /**
     * Get the offset of the error in the pattern.
     *
     * @return the offset
     */
    public long offset() {
        return offset;
    }

    /**
     * Get the error message.
     *
     * @return the error message
     */
    public String message() {
        return message;
    }
}
