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

import org.pcre4j.option.Pcre2PatternInfo;

/**
 * An error indicating an unexpected data size for a {@link Pcre2PatternInfo} query.
 *
 * @deprecated Use {@link Pcre2PatternInfoSizeException} instead. This class will be removed in a future release.
 */
@Deprecated(forRemoval = true)
public class Pcre2PatternInfoSizeError extends Pcre2PatternInfoSizeException {

    /**
     * Create a new pattern info size error.
     *
     * @param info the pattern info that had an unexpected size
     * @param size the unexpected size in bytes
     * @deprecated Use {@link Pcre2PatternInfoSizeException#Pcre2PatternInfoSizeException(Pcre2PatternInfo, long)}
     *     instead.
     */
    @Deprecated(forRemoval = true)
    public Pcre2PatternInfoSizeError(Pcre2PatternInfo info, long size) {
        this(info, size, null);
    }

    /**
     * Create a new pattern info size error.
     *
     * @param info  the pattern info that had an unexpected size
     * @param size  the unexpected size in bytes
     * @param cause the cause of the error
     * @deprecated Use
     *     {@link Pcre2PatternInfoSizeException#Pcre2PatternInfoSizeException(Pcre2PatternInfo, long, Throwable)}
     *     instead.
     */
    @Deprecated(forRemoval = true)
    public Pcre2PatternInfoSizeError(Pcre2PatternInfo info, long size, Throwable cause) {
        super(info, size, cause);
    }
}
