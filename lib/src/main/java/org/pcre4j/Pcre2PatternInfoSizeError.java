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

public class Pcre2PatternInfoSizeError extends RuntimeException {

    public Pcre2PatternInfoSizeError(Pcre2PatternInfo info, long size) {
        this(info, size, null);
    }

    public Pcre2PatternInfoSizeError(Pcre2PatternInfo info, long size, Throwable cause) {
        super("Unexpected size of %d bytes for %s".formatted(size, info), cause);
    }

}
