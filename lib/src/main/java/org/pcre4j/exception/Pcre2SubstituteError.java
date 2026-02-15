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
 * An error that occurs during a substitute operation.
 *
 * @deprecated Use {@link Pcre2SubstituteException} instead. This class will be removed in a future release.
 */
@Deprecated(forRemoval = true)
public class Pcre2SubstituteError extends Pcre2SubstituteException {

    /**
     * Create a new substitute error.
     *
     * @param message the error message
     * @deprecated Use {@link Pcre2SubstituteException#Pcre2SubstituteException(String, int)} instead.
     */
    @Deprecated(forRemoval = true)
    public Pcre2SubstituteError(String message) {
        this(message, null);
    }

    /**
     * Create a new substitute error.
     *
     * @param message the error message
     * @param cause   the cause of the error
     * @deprecated Use {@link Pcre2SubstituteException#Pcre2SubstituteException(String, int, Throwable)} instead.
     */
    @Deprecated(forRemoval = true)
    public Pcre2SubstituteError(String message, Throwable cause) {
        super(message, 0, cause);
    }
}
