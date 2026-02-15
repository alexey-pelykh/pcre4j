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
package org.pcre4j.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/**
 * Helper class for Arena allocation operations - Java 22+ version using finalized FFM API.
 * <p>
 * This implementation is automatically selected by the JVM when running on Java 22 or later,
 * thanks to the Multi-Release JAR structure (META-INF/versions/22/).
 *
 * @see <a href="https://openjdk.org/jeps/238">JEP 238: Multi-Release JAR Files</a>
 * @see <a href="https://openjdk.org/jeps/454">JEP 454: Foreign Function &amp; Memory API</a>
 */
final class ArenaHelper {

    private ArenaHelper() {
        // Utility class
    }

    /**
     * Allocates a memory segment for a single element of the given layout.
     *
     * @param arena  the arena to allocate from
     * @param layout the value layout for the element
     * @return a memory segment sized for one element
     */
    static MemorySegment allocate(Arena arena, ValueLayout layout) {
        return arena.allocate(layout);
    }

    /**
     * Allocates a memory segment for multiple elements of the given layout.
     *
     * @param arena  the arena to allocate from
     * @param layout the value layout for elements
     * @param count  the number of elements
     * @return a memory segment sized for count elements
     */
    static MemorySegment allocate(Arena arena, ValueLayout layout, long count) {
        return arena.allocate(layout, count);
    }

    /**
     * Allocates and initializes a memory segment with int array values.
     *
     * @param arena  the arena to allocate from
     * @param values the int array to copy into the segment
     * @return a memory segment containing the array values
     */
    static MemorySegment allocateFrom(Arena arena, int[] values) {
        return arena.allocateFrom(ValueLayout.JAVA_INT, values);
    }

    /**
     * Allocates a UTF-8 encoded string with null terminator.
     *
     * @param arena the arena to allocate from
     * @param str   the string to encode as UTF-8
     * @return a memory segment containing the null-terminated UTF-8 string
     */
    static MemorySegment allocateFrom(Arena arena, String str) {
        return arena.allocateFrom(str);
    }

    /**
     * Reads a null-terminated UTF-8 string from a memory segment.
     *
     * @param segment the memory segment to read from
     * @param offset  the byte offset to start reading
     * @return the decoded string
     */
    static String getString(MemorySegment segment, long offset) {
        return segment.getString(offset);
    }
}
