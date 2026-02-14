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

/**
 * Base exception for all PCRE4J-specific errors.
 * <p>
 * This class provides a unified exception hierarchy that allows catching all PCRE4J errors
 * with a single {@code catch (Pcre2Exception e)} clause. All exceptions carry the PCRE2 native
 * error code when applicable, enabling programmatic error handling.
 *
 * @see Pcre2CompileException
 * @see Pcre2ConvertException
 * @see Pcre2MatchException
 * @see Pcre2SubstituteException
 * @see Pcre2SubstringException
 * @see Pcre2InternalException
 */
public class Pcre2Exception extends RuntimeException {

    /**
     * The PCRE2 native error code, or 0 if not applicable.
     */
    private final int errorCode;

    /**
     * Creates a new {@link Pcre2Exception} with the given message and error code.
     *
     * @param message   the error message
     * @param errorCode the PCRE2 native error code, or 0 if not applicable
     */
    public Pcre2Exception(String message, int errorCode) {
        this(message, errorCode, null);
    }

    /**
     * Creates a new {@link Pcre2Exception} with the given message, error code, and cause.
     *
     * @param message   the error message
     * @param errorCode the PCRE2 native error code, or 0 if not applicable
     * @param cause     the cause of the exception, or {@code null}
     */
    public Pcre2Exception(String message, int errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Returns the PCRE2 native error code associated with this exception.
     *
     * @return the PCRE2 error code, or 0 if not applicable
     */
    public int errorCode() {
        return errorCode;
    }
}
