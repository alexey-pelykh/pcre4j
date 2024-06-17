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
        if (code == null) {
            throw new IllegalArgumentException("code must not be null");
        }
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null");
        }
        if (matchData == null) {
            throw new IllegalArgumentException("matchData must not be null");
        }

        final var ovector = matchData.ovector();
        final var matchGroups = new String[ovector.length];
        for (var matchIndex = 0; matchIndex < ovector.length; matchIndex++) {
            final var match = ovector[matchIndex];
            matchGroups[matchIndex] = subject.substring(match.start(), match.end());
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
        if (code == null) {
            throw new IllegalArgumentException("code must not be null");
        }
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null");
        }
        if (matchData == null) {
            throw new IllegalArgumentException("matchData must not be null");
        }

        final var groupNames = getGroupNames(code);
        final var ovector = matchData.ovector();
        final var matchGroups = new HashMap<String, String>();
        for (var matchIndex = 1; matchIndex < ovector.length; matchIndex++) {
            final var match = ovector[matchIndex];
            final var groupName = groupNames[matchIndex - 1];
            if (groupName != null) {
                matchGroups.put(groupName, subject.substring(match.start(), match.end()));
            }
        }
        return matchGroups;
    }
}
