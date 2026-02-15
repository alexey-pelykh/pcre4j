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
package org.pcre4j.regex;

import org.pcre4j.exception.Pcre2MatchLimitException;
import org.pcre4j.api.IPcre2;

/**
 * Thrown when a match operation exceeds a configured resource limit.
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
 * <p>
 * Limits can be configured via system properties:
 * <ul>
 *   <li>{@code pcre2.regex.match.limit} - maximum number of match function calls</li>
 *   <li>{@code pcre2.regex.depth.limit} - maximum backtracking depth</li>
 *   <li>{@code pcre2.regex.heap.limit} - maximum heap memory in kibibytes</li>
 * </ul>
 *
 * @see Matcher#MATCH_LIMIT_PROPERTY
 * @see Matcher#DEPTH_LIMIT_PROPERTY
 * @see Matcher#HEAP_LIMIT_PROPERTY
 */
public class MatchLimitException extends Pcre2MatchLimitException {

    /**
     * Creates a new {@link MatchLimitException} with the given message and PCRE2 error code.
     *
     * @param message   the error message from PCRE2
     * @param errorCode the PCRE2 error code (one of {@link IPcre2#ERROR_MATCHLIMIT},
     *                  {@link IPcre2#ERROR_DEPTHLIMIT}, or {@link IPcre2#ERROR_HEAPLIMIT})
     */
    public MatchLimitException(String message, int errorCode) {
        super(message, errorCode);
    }

    /**
     * Returns the PCRE2 error code that caused this exception.
     *
     * @return the PCRE2 error code
     * @deprecated Use {@link #errorCode()} instead.
     */
    @Deprecated(forRemoval = true)
    public int getErrorCode() {
        return errorCode();
    }
}
