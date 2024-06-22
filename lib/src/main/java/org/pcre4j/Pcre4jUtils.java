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

import org.pcre4j.api.IPcre2;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class Pcre4jUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private Pcre4jUtils() {
    }

    /**
     * Get the error message for the given error code.
     *
     * @param api       the PCRE2 API
     * @param errorcode the error code
     * @return the error message
     */
    public static String getErrorMessage(IPcre2 api, int errorcode) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        var buffer = ByteBuffer.allocateDirect(256);
        while (true) {
            final var size = api.getErrorMessage(errorcode, buffer);
            if (size == IPcre2.ERROR_NOMEMORY) {
                buffer = ByteBuffer.allocateDirect(buffer.capacity() * 2);
                continue;
            } else if (size < 0) {
                throw new IllegalStateException("Error getting error message: %d".formatted(size));
            }

            return StandardCharsets.UTF_8.decode(buffer.slice(0, size)).toString();
        }
    }

    /**
     * Get the group names for the given code.
     *
     * @param code the PCRE2 compiled pattern
     * @return an array where the index is the group number and the value is the group name or {@code null} if the group
     * has no name
     */
    public static String[] getGroupNames(Pcre2Code code) {
        if (code == null) {
            throw new IllegalArgumentException("code must not be null");
        }

        final var groupNames = new String[code.captureCount()];
        for (var nameTableEntry : code.nameTable()) {
            groupNames[nameTableEntry.group() - 1] = nameTableEntry.name();
        }

        return groupNames;
    }

    /**
     * Get the match groups
     *
     * @param code      the compiled pattern the match was performed with
     * @param subject   the subject string the match was performed against
     * @param matchData the match data with the match results
     * @return an array of strings where the index is the group number and the value is the matched group or
     * {@code null}
     */
    public static String[] getMatchGroups(Pcre2Code code, String subject, Pcre2MatchData matchData) {
        if (matchData == null) {
            throw new IllegalArgumentException("matchData must not be null");
        }

        return getMatchGroups(code, subject, matchData.ovector());
    }

    /**
     * Get the match groups
     *
     * @param code    the compiled pattern the match was performed with
     * @param subject the subject string the match was performed against
     * @param ovector an array of offset pairs corresponding to the match results
     * @return an array of strings where the index is the group number and the value is the matched group or
     * {@code null}
     */
    public static String[] getMatchGroups(Pcre2Code code, String subject, long[] ovector) {
        if (code == null) {
            throw new IllegalArgumentException("code must not be null");
        }
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null");
        }
        if (ovector == null) {
            throw new IllegalArgumentException("ovector must not be null");
        }

        final var stringIndices = convertOvectorToStringIndices(subject, ovector);

        final var matchGroupsCount = ovector.length / 2;
        final var matchGroups = new String[matchGroupsCount];
        for (var matchIndex = 0; matchIndex < matchGroupsCount; matchIndex++) {
            matchGroups[matchIndex] = subject.substring(
                    stringIndices[matchIndex * 2],
                    stringIndices[matchIndex * 2 + 1]
            );
        }
        return matchGroups;
    }

    /**
     * Get the match named groups
     *
     * @param code      the compiled pattern the match was performed with
     * @param subject   the subject string the match was performed against
     * @param matchData the match data with the match results
     * @return a map of group names to the matched group or {@code null}
     */
    public static Map<String, String> getNamedMatchGroups(Pcre2Code code, String subject, Pcre2MatchData matchData) {
        if (matchData == null) {
            throw new IllegalArgumentException("matchData must not be null");
        }

        return getNamedMatchGroups(code, subject, matchData.ovector());
    }

    /**
     * Get the match named groups
     *
     * @param code    the compiled pattern the match was performed with
     * @param subject the subject string the match was performed against
     * @param ovector an array of offset pairs corresponding to the match results
     * @return a map of group names to the matched group or {@code null}
     */
    public static Map<String, String> getNamedMatchGroups(Pcre2Code code, String subject, long[] ovector) {
        if (code == null) {
            throw new IllegalArgumentException("code must not be null");
        }
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null");
        }
        if (ovector == null) {
            throw new IllegalArgumentException("ovector must not be null");
        }

        final var stringIndices = convertOvectorToStringIndices(subject, ovector);

        final var groupNames = getGroupNames(code);
        final var matchGroups = new HashMap<String, String>();
        for (var matchIndex = 1; matchIndex < ovector.length; matchIndex++) {
            final var groupName = groupNames[matchIndex - 1];
            if (groupName != null) {
                matchGroups.put(groupName, subject.substring(
                        stringIndices[matchIndex * 2],
                        stringIndices[matchIndex * 2 + 1]
                ));
            }
        }
        return matchGroups;
    }

    /**
     * Convert the byte-based ovector offset pairs to string index pairs
     *
     * @param subject the string to which the ovector values correspond
     * @param ovector the byte-based ovector offset pairs
     * @return a string index pairs
     */
    public static int[] convertOvectorToStringIndices(String subject, long[] ovector) {
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null");
        }

        return convertOvectorToStringIndices(subject, subject.getBytes(StandardCharsets.UTF_8), ovector);
    }

    /**
     * Convert the byte-based ovector offset pairs to string index pairs
     *
     * @param subject the string to which the ovector values correspond
     * @param ovector the byte-based ovector offset pairs
     * @return a string index pairs
     */
    public static int[] convertOvectorToStringIndices(String subject, byte[] subjectUtf8, long[] ovector) {
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null");
        }
        if (subjectUtf8 == null) {
            throw new IllegalArgumentException("subjectUtf8 must not be null");
        }
        if (ovector == null) {
            throw new IllegalArgumentException("ovector must not be null");
        }
        if (ovector.length < 2) {
            throw new IllegalArgumentException("ovector must have at least 2 elements");
        }
        if (ovector.length % 2 != 0) {
            throw new IllegalArgumentException("ovector must have an even number of elements");
        }
        if (ovector[0] > ovector[1]) {
            throw new IllegalArgumentException("ovector start must be less than or equal to ovector end");
        }

        // Match region size in bytes is determined by the first offset pair in the ovector
        final var matchSizeInBytes = ovector[1] - ovector[0];

        // Calculate the mapping of byte offsets to string indices for the relevant subject region of the match
        var stringIndex = 0;
        final var byteOffsetToStringIndex = new int[(int) matchSizeInBytes + 1];
        for (var byteIndex = 0; byteIndex < ovector[1]; ) {
            if (byteIndex >= ovector[0]) {
                byteOffsetToStringIndex[(int) (byteIndex - ovector[0])] = stringIndex;
            }

            final var subjectChar = subject.charAt(stringIndex);

            final int subjectCharByteLength;
            if (subjectChar <= 0x7F) {
                subjectCharByteLength = 1;
            } else if (subjectChar <= 0x7FF) {
                subjectCharByteLength = 2;
            } else if (Character.isHighSurrogate(subjectChar) || Character.isLowSurrogate(subjectChar)) {
                subjectCharByteLength = 2;
            } else {
                subjectCharByteLength = 3;
            }

            for (var subjectCharByteIndex = 0; subjectCharByteIndex < subjectCharByteLength; subjectCharByteIndex++) {
                if (byteIndex >= ovector[0]) {
                    byteOffsetToStringIndex[(int) (byteIndex - ovector[0])] = stringIndex;
                }
                byteIndex += 1;
            }

            stringIndex++;
        }
        byteOffsetToStringIndex[(int) matchSizeInBytes] = stringIndex;

        // Convert byte offsets to string indices
        final var stringIndices = new int[ovector.length];
        for (var valueIndex = 0; valueIndex < ovector.length; valueIndex++) {
            final var byteIndex = ovector[valueIndex];

            // Handle case when group was not matched
            if (byteIndex == -1) {
                stringIndices[valueIndex] = -1;
                continue;
            }

            stringIndices[valueIndex] = byteOffsetToStringIndex[(int) (byteIndex - ovector[0])];
        }

        return stringIndices;
    }
}
