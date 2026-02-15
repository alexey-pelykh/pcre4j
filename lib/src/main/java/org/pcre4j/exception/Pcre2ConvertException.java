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
package org.pcre4j.exception;

/**
 * An exception that occurs when a pattern conversion fails.
 * <p>
 * This exception is thrown when PCRE2's pattern conversion function encounters an error while
 * converting a glob or POSIX pattern to a PCRE2-compatible pattern.
 */
public class Pcre2ConvertException extends Pcre2Exception {

    /**
     * The pattern that caused the error.
     */
    private final String pattern;

    /**
     * Create a new pattern conversion exception.
     *
     * @param pattern   the pattern that failed to convert
     * @param message   the error message
     * @param errorCode the PCRE2 native error code
     */
    public Pcre2ConvertException(String pattern, String message, int errorCode) {
        this(pattern, message, errorCode, null);
    }

    /**
     * Create a new pattern conversion exception.
     *
     * @param pattern   the pattern that failed to convert
     * @param message   the error message
     * @param errorCode the PCRE2 native error code
     * @param cause     the cause of the exception
     */
    public Pcre2ConvertException(String pattern, String message, int errorCode, Throwable cause) {
        super(
                "Error converting pattern \"%s\": %s".formatted(pattern, message),
                errorCode,
                cause
        );
        this.pattern = pattern;
    }

    /**
     * Get the pattern that caused the error.
     *
     * @return the pattern
     */
    public String pattern() {
        return pattern;
    }
}
