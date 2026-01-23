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

import org.pcre4j.api.IPcre2;

import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;

/**
 * The match data where the results of the match are stored
 */
public class Pcre2MatchData {

    private static final Cleaner cleaner = Cleaner.create();
    /**
     * The match data handle
     */
    /* package-private */ final long handle;

    /**
     * The PCRE2 API reference to use across the entire lifecycle of the object
     */
    /* package-private */ final IPcre2 api;

    /**
     * The cleaner to free the resources
     */
    private final Cleaner.Cleanable cleanable;

    /**
     * Create a new match data object
     *
     * @param ovecsize the size of the output vector
     */
    public Pcre2MatchData(int ovecsize) {
        this(Pcre4j.api(), ovecsize);
    }

    /**
     * Create a new match data object
     *
     * @param api      the PCRE2 API to use
     * @param ovecsize the size of the output vector
     */
    public Pcre2MatchData(IPcre2 api, int ovecsize) {
        if (api == null) {
            throw new IllegalArgumentException("api cannot be null");
        }

        final var handle = api.matchDataCreate(
                ovecsize,
                0
        );
        if (handle == 0) {
            throw new IllegalStateException("Failed to create match data");
        }

        this.api = api;
        this.handle = handle;
        this.cleanable = cleaner.register(this, new Pcre2MatchData.Clean(api, handle));
    }

    /**
     * Create a new match data object
     *
     * @param code the compiled pattern to create the match data for
     */
    public Pcre2MatchData(Pcre2Code code) {
        if (code == null) {
            throw new IllegalArgumentException("code cannot be null");
        }

        final var handle = code.api.matchDataCreateFromPattern(
                code.handle,
                0
        );
        if (handle == 0) {
            throw new IllegalStateException("Failed to create match data from pattern");
        }

        this.api = code.api;
        this.handle = handle;
        this.cleanable = cleaner.register(this, new Pcre2MatchData.Clean(api, handle));
    }

    /**
     * Get the PCRE2 API backing this match data
     *
     * @return the PCRE2 API
     */
    public IPcre2 api() {
        return api;
    }

    /**
     * Get the handle of the match data
     *
     * @return the handle of the match data
     */
    public long handle() {
        return handle;
    }

    /**
     * Get number of the offset pairs in the output vector
     *
     * @return the number of the offset pairs in the output vector
     */
    public int ovectorCount() {
        return api.getOvectorCount(handle);
    }

    /**
     * Get the size of this match data block in bytes.
     * <p>
     * This returns the size of the opaque match data block that was allocated when this object was created.
     *
     * @return the size of the match data block in bytes
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_get_match_data_size.html">pcre2_get_match_data_size</a>
     */
    public long size() {
        return api.getMatchDataSize(handle);
    }

    /**
     * Get the output vector composed of offset pairs, each offset pair represents the start and end of the match. The
     * value of the offset is the index of the byte where the character starts, not the charcater index.
     *
     * @return the output vector
     */
    public long[] ovector() {
        final var ovector = new long[api.getOvectorCount(handle) * 2];
        api.getOvector(handle, ovector);
        return ovector;
    }

    /**
     * Extract a captured substring by its group number.
     * <p>
     * The substring is extracted from the match result stored in this match data. Group 0 represents the entire match,
     * and groups 1 and higher represent capturing groups.
     * <p>
     * <b>Important:</b> When calling {@link Pcre2Code#match} before using this method, you must use the
     * {@link Pcre2MatchOption#COPY_MATCHED_SUBJECT} option. Without this option, PCRE2 stores only a pointer to the
     * original subject string, which may be garbage collected by the JVM before this method is called, resulting in
     * undefined behavior or corrupted data.
     *
     * @param number the group number (0 = entire match, 1+ = capturing groups)
     * @return the extracted substring as a byte array (UTF-8 encoded)
     * @throws IllegalArgumentException if the group number is negative
     * @throws IndexOutOfBoundsException if there are no groups of that number
     * @throws IllegalStateException if the ovector was too small for that group, the group did not participate in
     *                               the match, or memory could not be allocated
     */
    public byte[] getSubstring(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("number must not be negative");
        }

        final var bufferptr = new long[1];
        final var bufflen = new long[1];
        final var result = api.substringGetByNumber(handle, number, bufferptr, bufflen);

        if (result == 0) {
            try {
                return api.readBytes(bufferptr[0], (int) bufflen[0]);
            } finally {
                api.substringFree(bufferptr[0]);
            }
        }

        switch (result) {
            case IPcre2.ERROR_NOSUBSTRING -> throw new IndexOutOfBoundsException(
                    "No group of number " + number
            );
            case IPcre2.ERROR_UNAVAILABLE -> throw new IllegalStateException(
                    "The ovector was too small for group " + number
            );
            case IPcre2.ERROR_UNSET -> throw new IllegalStateException(
                    "Group " + number + " did not participate in the match"
            );
            case IPcre2.ERROR_NOMEMORY -> throw new IllegalStateException(
                    "Memory could not be allocated for substring extraction"
            );
            default -> throw new IllegalStateException(
                    "Unexpected error extracting substring: " + result
            );
        }
    }

    /**
     * Extract a captured substring by its group name.
     * <p>
     * The substring is extracted from the match result stored in this match data using the name of a named capturing
     * group defined in the pattern with the {@code (?<name>...)} syntax.
     * <p>
     * <b>Important:</b> When calling {@link Pcre2Code#match} before using this method, you must use the
     * {@link Pcre2MatchOption#COPY_MATCHED_SUBJECT} option. Without this option, PCRE2 stores only a pointer to the
     * original subject string, which may be garbage collected by the JVM before this method is called, resulting in
     * undefined behavior or corrupted data.
     *
     * @param name the name of the capturing group
     * @return the extracted substring as a byte array (UTF-8 encoded)
     * @throws IllegalArgumentException if the name is null
     * @throws IndexOutOfBoundsException if there are no groups of that name
     * @throws IllegalStateException if the ovector was too small for that group, the group did not participate in
     *                               the match, or memory could not be allocated
     */
    public byte[] getSubstring(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }

        final var bufferptr = new long[1];
        final var bufflen = new long[1];
        final var result = api.substringGetByName(handle, name, bufferptr, bufflen);

        if (result == 0) {
            try {
                return api.readBytes(bufferptr[0], (int) bufflen[0]);
            } finally {
                api.substringFree(bufferptr[0]);
            }
        }

        switch (result) {
            case IPcre2.ERROR_NOSUBSTRING -> throw new IndexOutOfBoundsException(
                    "No group of name '" + name + "'"
            );
            case IPcre2.ERROR_UNAVAILABLE -> throw new IllegalStateException(
                    "The ovector was too small for group '" + name + "'"
            );
            case IPcre2.ERROR_UNSET -> throw new IllegalStateException(
                    "Group '" + name + "' did not participate in the match"
            );
            case IPcre2.ERROR_NOMEMORY -> throw new IllegalStateException(
                    "Memory could not be allocated for substring extraction"
            );
            default -> throw new IllegalStateException(
                    "Unexpected error extracting substring: " + result
            );
        }
    }

    /**
     * Copy a captured substring by its group number into a caller-provided buffer.
     * <p>
     * This is a zero-allocation alternative to {@link #getSubstring(int)} for performance-critical paths.
     * The caller provides the buffer, and the method copies the substring into it.
     * <p>
     * <b>Important:</b> When calling {@link Pcre2Code#match} before using this method, you must use the
     * {@link Pcre2MatchOption#COPY_MATCHED_SUBJECT} option. Without this option, PCRE2 stores only a pointer to the
     * original subject string, which may be garbage collected by the JVM before this method is called, resulting in
     * undefined behavior or corrupted data.
     *
     * @param number the group number (0 = entire match, 1+ = capturing groups)
     * @param buffer a direct {@link ByteBuffer} to receive the extracted substring (must have sufficient capacity)
     * @return the number of bytes written to the buffer (excluding the null terminator that PCRE2 appends)
     * @throws IllegalArgumentException if the group number is negative, buffer is null, or buffer is not direct
     * @throws IndexOutOfBoundsException if there are no groups of that number
     * @throws IllegalStateException if the ovector was too small for that group, the group did not participate in
     *                               the match, or the buffer is too small
     */
    public int copySubstring(int number, ByteBuffer buffer) {
        if (number < 0) {
            throw new IllegalArgumentException("number must not be negative");
        }
        if (buffer == null) {
            throw new IllegalArgumentException("buffer must not be null");
        }
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("buffer must be a direct ByteBuffer");
        }

        final var bufflen = new long[]{buffer.remaining()};
        final var result = api.substringCopyByNumber(handle, number, buffer, bufflen);

        if (result == 0) {
            return (int) bufflen[0];
        }

        switch (result) {
            case IPcre2.ERROR_NOSUBSTRING -> throw new IndexOutOfBoundsException(
                    "No group of number " + number
            );
            case IPcre2.ERROR_UNAVAILABLE -> throw new IllegalStateException(
                    "The ovector was too small for group " + number
            );
            case IPcre2.ERROR_UNSET -> throw new IllegalStateException(
                    "Group " + number + " did not participate in the match"
            );
            case IPcre2.ERROR_NOMEMORY -> throw new IllegalStateException(
                    "Buffer is too small for group " + number + " (need at least " + (bufflen[0] + 1) + " bytes)"
            );
            default -> throw new IllegalStateException(
                    "Unexpected error copying substring: " + result
            );
        }
    }

    /**
     * Copy a captured substring by its group name into a caller-provided buffer.
     * <p>
     * This is a zero-allocation alternative to {@link #getSubstring(String)} for performance-critical paths.
     * The caller provides the buffer, and the method copies the substring into it.
     * <p>
     * <b>Important:</b> When calling {@link Pcre2Code#match} before using this method, you must use the
     * {@link Pcre2MatchOption#COPY_MATCHED_SUBJECT} option. Without this option, PCRE2 stores only a pointer to the
     * original subject string, which may be garbage collected by the JVM before this method is called, resulting in
     * undefined behavior or corrupted data.
     *
     * @param name   the name of the capturing group
     * @param buffer a direct {@link ByteBuffer} to receive the extracted substring (must have sufficient capacity)
     * @return the number of bytes written to the buffer (excluding the null terminator that PCRE2 appends)
     * @throws IllegalArgumentException if the name is null, buffer is null, or buffer is not direct
     * @throws IndexOutOfBoundsException if there are no groups of that name
     * @throws IllegalStateException if the ovector was too small for that group, the group did not participate in
     *                               the match, or the buffer is too small
     */
    public int copySubstring(String name, ByteBuffer buffer) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if (buffer == null) {
            throw new IllegalArgumentException("buffer must not be null");
        }
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("buffer must be a direct ByteBuffer");
        }

        final var bufflen = new long[]{buffer.remaining()};
        final var result = api.substringCopyByName(handle, name, buffer, bufflen);

        if (result == 0) {
            return (int) bufflen[0];
        }

        switch (result) {
            case IPcre2.ERROR_NOSUBSTRING -> throw new IndexOutOfBoundsException(
                    "No group of name '" + name + "'"
            );
            case IPcre2.ERROR_UNAVAILABLE -> throw new IllegalStateException(
                    "The ovector was too small for group '" + name + "'"
            );
            case IPcre2.ERROR_UNSET -> throw new IllegalStateException(
                    "Group '" + name + "' did not participate in the match"
            );
            case IPcre2.ERROR_NOMEMORY -> throw new IllegalStateException(
                    "Buffer is too small for group '" + name + "' (need at least " + (bufflen[0] + 1) + " bytes)"
            );
            default -> throw new IllegalStateException(
                    "Unexpected error copying substring: " + result
            );
        }
    }

    /**
     * Get the length of a captured substring by its group number.
     * <p>
     * This allows querying substring length before allocation, enabling efficient buffer sizing for copy operations.
     * After a partial match, only substring 0 is available.
     *
     * @param number the group number (0 = entire match, 1+ = capturing groups)
     * @return the length of the substring in code units (excluding the null terminator)
     * @throws IllegalArgumentException if the group number is negative
     * @throws IndexOutOfBoundsException if there are no groups of that number
     * @throws IllegalStateException if the ovector was too small for that group or the group did not participate in
     *                               the match
     */
    public long getSubstringLength(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("number must not be negative");
        }

        final var length = new long[1];
        final var result = api.substringLengthByNumber(handle, number, length);

        if (result == 0) {
            return length[0];
        }

        switch (result) {
            case IPcre2.ERROR_NOSUBSTRING -> throw new IndexOutOfBoundsException(
                    "No group of number " + number
            );
            case IPcre2.ERROR_UNAVAILABLE -> throw new IllegalStateException(
                    "The ovector was too small for group " + number
            );
            case IPcre2.ERROR_UNSET -> throw new IllegalStateException(
                    "Group " + number + " did not participate in the match"
            );
            default -> throw new IllegalStateException(
                    "Unexpected error getting substring length: " + result
            );
        }
    }

    /**
     * Get the length of a captured substring by its group name.
     * <p>
     * This allows querying substring length by name before allocation, enabling efficient buffer sizing for copy
     * operations. After a partial match, only substring 0 is available.
     *
     * @param name the name of the capturing group
     * @return the length of the substring in code units (excluding the null terminator)
     * @throws IllegalArgumentException if the name is null
     * @throws IndexOutOfBoundsException if there are no groups of that name
     * @throws IllegalStateException if the ovector was too small for that group or the group did not participate in
     *                               the match
     */
    public long getSubstringLength(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }

        final var length = new long[1];
        final var result = api.substringLengthByName(handle, name, length);

        if (result == 0) {
            return length[0];
        }

        switch (result) {
            case IPcre2.ERROR_NOSUBSTRING -> throw new IndexOutOfBoundsException(
                    "No group of name '" + name + "'"
            );
            case IPcre2.ERROR_UNAVAILABLE -> throw new IllegalStateException(
                    "The ovector was too small for group '" + name + "'"
            );
            case IPcre2.ERROR_UNSET -> throw new IllegalStateException(
                    "Group '" + name + "' did not participate in the match"
            );
            default -> throw new IllegalStateException(
                    "Unexpected error getting substring length: " + result
            );
        }
    }

    private record Clean(IPcre2 api, long matchData) implements Runnable {
        @Override
        public void run() {
            api.matchDataFree(matchData);
        }
    }

}
