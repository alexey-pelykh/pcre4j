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
package org.pcre4j.api;

/**
 * Interface for native memory access operations.
 * <p>
 * This interface is separate from {@link IPcre2} because native memory access is a utility concern,
 * not part of the PCRE2 API contract. Backend implementations (JNA, FFM) provide their own native
 * memory reading capabilities alongside their PCRE2 API implementations.
 */
public interface INativeMemoryAccess {

    /**
     * Read bytes from a native memory pointer.
     *
     * @param pointer the native memory pointer
     * @param length  the number of bytes to read
     * @return the bytes read from the pointer
     */
    byte[] readBytes(long pointer, int length);
}
