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
package org.pcre4j;

import org.pcre4j.api.IPcre2;

/**
 * An exception thrown when a match operation exceeds a configured resource limit.
 * <p>
 * This exception is thrown when PCRE2 terminates a match operation because a match limit,
 * backtracking depth limit, or heap memory limit was exceeded. These limits provide protection
 * against catastrophic backtracking (ReDoS) and excessive resource consumption.
 * <p>
 * The specific limit that was exceeded can be determined via {@link #errorCode()}:
 * <ul>
 *   <li>{@link IPcre2#ERROR_MATCHLIMIT} - the match limit was exceeded</li>
 *   <li>{@link IPcre2#ERROR_DEPTHLIMIT} - the backtracking depth limit was exceeded</li>
 *   <li>{@link IPcre2#ERROR_HEAPLIMIT} - the heap memory limit was exceeded</li>
 * </ul>
 */
public class Pcre2MatchLimitException extends Pcre2MatchException {

    /**
     * Creates a new match limit exception.
     *
     * @param message   the error message from PCRE2
     * @param errorCode the PCRE2 error code (one of {@link IPcre2#ERROR_MATCHLIMIT},
     *                  {@link IPcre2#ERROR_DEPTHLIMIT}, or {@link IPcre2#ERROR_HEAPLIMIT})
     */
    public Pcre2MatchLimitException(String message, int errorCode) {
        this(message, errorCode, null);
    }

    /**
     * Creates a new match limit exception.
     *
     * @param message   the error message from PCRE2
     * @param errorCode the PCRE2 error code
     * @param cause     the cause of the exception, or {@code null}
     */
    public Pcre2MatchLimitException(String message, int errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
