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

/**
 * An exception indicating an unexpected internal error in PCRE4J.
 *
 * @see Pcre2PatternInfoSizeException
 */
public class Pcre2InternalException extends Pcre2Exception {

    /**
     * Creates a new internal exception.
     *
     * @param message   the error message
     * @param errorCode the PCRE2 native error code, or 0 if not applicable
     */
    public Pcre2InternalException(String message, int errorCode) {
        this(message, errorCode, null);
    }

    /**
     * Creates a new internal exception.
     *
     * @param message   the error message
     * @param errorCode the PCRE2 native error code, or 0 if not applicable
     * @param cause     the cause of the exception, or {@code null}
     */
    public Pcre2InternalException(String message, int errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
