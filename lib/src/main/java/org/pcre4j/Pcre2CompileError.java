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
 * An error that occurs when a pattern fails to compile.
 *
 * @deprecated Use {@link Pcre2CompileException} instead. This class will be removed in a future release.
 */
@Deprecated(forRemoval = true)
public class Pcre2CompileError extends Pcre2CompileException {

    /**
     * Create a new pattern compilation error.
     *
     * @param pattern the pattern
     * @param offset  the offset of the error in the pattern
     * @param message the error message
     * @deprecated Use {@link Pcre2CompileException#Pcre2CompileException(String, long, String, int)} instead.
     */
    @Deprecated(forRemoval = true)
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
     * @deprecated Use {@link Pcre2CompileException#Pcre2CompileException(String, long, String, int, Throwable)}
     *     instead.
     */
    @Deprecated(forRemoval = true)
    public Pcre2CompileError(String pattern, long offset, String message, Throwable cause) {
        super(pattern, offset, message, 0, cause);
    }
}
