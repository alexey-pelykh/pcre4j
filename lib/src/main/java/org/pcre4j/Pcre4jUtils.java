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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public final class Pcre4jUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private Pcre4jUtils() {
    }

    /**
     * Get the PCRE2 version.
     *
     * @param api the PCRE2 API
     * @return the PCRE2 version
     */
    public static String getVersion(IPcre2 api) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        final var versionSize = api.config(IPcre2.CONFIG_VERSION);
        if (versionSize < 0) {
            throw new IllegalStateException(getErrorMessage(api, versionSize));
        }

        final var versionBuffer = ByteBuffer.allocateDirect(versionSize);
        final var versionCopyResult = api.config(IPcre2.CONFIG_VERSION, versionBuffer);
        if (versionCopyResult < 0) {
            throw new IllegalStateException(getErrorMessage(api, versionCopyResult));
        }

        return StandardCharsets.UTF_8.decode(versionBuffer.limit(versionSize - 1)).toString();
    }

    /**
     * Get the Unicode version.
     *
     * @param api the PCRE2 API
     * @return the Unicode version
     */
    public static String getUnicodeVersion(IPcre2 api) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        final var versionSize = api.config(IPcre2.CONFIG_UNICODE_VERSION);
        if (versionSize < 0) {
            throw new IllegalStateException(getErrorMessage(api, versionSize));
        }

        final var versionBuffer = ByteBuffer.allocateDirect(versionSize);
        final var versionCopyResult = api.config(IPcre2.CONFIG_UNICODE_VERSION, versionBuffer);
        if (versionCopyResult < 0) {
            throw new IllegalStateException(getErrorMessage(api, versionCopyResult));
        }

        return StandardCharsets.UTF_8.decode(versionBuffer.limit(versionSize - 1)).toString();
    }

    /**
     * Check if Unicode is supported.
     *
     * @param api the PCRE2 API
     * @return {@code true} if Unicode is supported, {@code false} otherwise
     */
    public static boolean isUnicodeSupported(IPcre2 api) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        final var unicodeSupported = new int[1];
        final var result = api.config(IPcre2.CONFIG_UNICODE, unicodeSupported);
        if (result < 0) {
            throw new IllegalStateException(getErrorMessage(api, result));
        }

        return unicodeSupported[0] == 1;
    }

    /**
     * Get the default parentheses nesting limit.
     *
     * @param api the PCRE2 API
     * @return the default parentheses nesting limit
     */
    public static int getDefaultParenthesesNestingLimit(IPcre2 api) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        final var parensLimit = new int[1];
        final var result = api.config(IPcre2.CONFIG_PARENSLIMIT, parensLimit);
        if (result < 0) {
            throw new IllegalStateException(getErrorMessage(api, result));
        }

        return parensLimit[0];
    }

    /**
     * Get the default newline sequence.
     *
     * @param api the PCRE2 API
     * @return the default newline sequence
     */
    public static Pcre2Newline getDefaultNewline(IPcre2 api) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        final var newline = new int[1];
        final var result = api.config(IPcre2.CONFIG_NEWLINE, newline);
        if (result < 0) {
            throw new IllegalStateException(getErrorMessage(api, result));
        }

        return Pcre2Newline.valueOf(newline[0]).orElseThrow();
    }

    /**
     * Check if the \C is disabled.
     *
     * @param api the PCRE2 API
     * @return {@code true} if the \C is disabled, {@code false} otherwise
     */
    public static boolean isBackslashCDisabled(IPcre2 api) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        final var backslashCDisabled = new int[1];
        final var result = api.config(IPcre2.CONFIG_BSR, backslashCDisabled);
        if (result < 0) {
            throw new IllegalStateException(getErrorMessage(api, result));
        }

        return backslashCDisabled[0] == 1;
    }

    /**
     * Get the default match limit.
     *
     * @param api the PCRE2 API
     * @return the default match limit
     */
    public static int getDefaultMatchLimit(IPcre2 api) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        final var matchLimit = new int[1];
        final var result = api.config(IPcre2.CONFIG_MATCHLIMIT, matchLimit);
        if (result < 0) {
            throw new IllegalStateException(getErrorMessage(api, result));
        }

        return matchLimit[0];
    }

    /**
     * Get the internal link size.
     *
     * @param api the PCRE2 API
     * @return the internal link size
     */
    public static int getInternalLinkSize(IPcre2 api) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        final var internalLinkSize = new int[1];
        final var result = api.config(IPcre2.CONFIG_LINKSIZE, internalLinkSize);
        if (result < 0) {
            throw new IllegalStateException(getErrorMessage(api, result));
        }

        return internalLinkSize[0];
    }

    /**
     * Get the JIT target.
     *
     * @param api the PCRE2 API
     * @return the JIT target or {@code null} if JIT is not supported
     */
    public static String getJitTarget(IPcre2 api) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        final var targetSize = api.config(IPcre2.CONFIG_JITTARGET);
        if (targetSize < 0) {
            if (targetSize == IPcre2.ERROR_BADOPTION) {
                return null;
            }
            throw new IllegalStateException(getErrorMessage(api, targetSize));
        }

        final var targetBuffer = ByteBuffer.allocateDirect(targetSize);
        final var targetCopyResult = api.config(IPcre2.CONFIG_JITTARGET, targetBuffer);
        if (targetCopyResult < 0) {
            throw new IllegalStateException(getErrorMessage(api, targetCopyResult));
        }

        return StandardCharsets.UTF_8.decode(targetBuffer.limit(targetSize - 1)).toString();
    }

    /**
     * Check if JIT is supported.
     *
     * @param api the PCRE2 API
     * @return {@code true} if JIT is supported, {@code false} otherwise
     */
    public static boolean isJitSupported(IPcre2 api) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        final var jitSupported = new int[1];
        final var result = api.config(IPcre2.CONFIG_JIT, jitSupported);
        if (result < 0) {
            throw new IllegalStateException(getErrorMessage(api, result));
        }

        return jitSupported[0] == 1;
    }

    /**
     * Get the default heap memory limit.
     *
     * @param api the PCRE2 API
     * @return the default heap memory limit
     */
    public static int getDefaultHeapLimit(IPcre2 api) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        final var heapLimit = new int[1];
        final var result = api.config(IPcre2.CONFIG_HEAPLIMIT, heapLimit);
        if (result < 0) {
            throw new IllegalStateException(getErrorMessage(api, result));
        }

        return heapLimit[0];
    }

    /**
     * Get the default backtracking depth limit.
     *
     * @param api the PCRE2 API
     * @return the default backtracking depth limit
     */
    public static int getDefaultDepthLimit(IPcre2 api) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        final var depthLimit = new int[1];
        final var result = api.config(IPcre2.CONFIG_DEPTHLIMIT, depthLimit);
        if (result < 0) {
            throw new IllegalStateException(getErrorMessage(api, result));
        }

        return depthLimit[0];
    }

    /**
     * Get which of the character widths the PCRE2 library was compiled with.
     *
     * @param api the PCRE2 API
     * @return the compiled character width (8, 16, or 32)
     */
    public static EnumSet<Pcre2UtfWidth> getCompiledWidths(IPcre2 api) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        final var widthsMask = new int[1];
        final var result = api.config(IPcre2.CONFIG_COMPILED_WIDTHS, widthsMask);
        if (result < 0) {
            throw new IllegalStateException(getErrorMessage(api, result));
        }

        final var widths = EnumSet.noneOf(Pcre2UtfWidth.class);
        if ((widthsMask[0] & Pcre2UtfWidth.UTF8.value()) != 0) {
            widths.add(Pcre2UtfWidth.UTF8);
        }
        if ((widthsMask[0] & Pcre2UtfWidth.UTF16.value()) != 0) {
            widths.add(Pcre2UtfWidth.UTF16);
        }
        if ((widthsMask[0] & Pcre2UtfWidth.UTF32.value()) != 0) {
            widths.add(Pcre2UtfWidth.UTF32);
        }

        return widths;
    }

    /**
     * Convert a character index to a byte offset.
     *
     * @param input the input string
     * @param index the character index
     * @return the byte offset
     */
    public static int convertCharacterIndexToByteOffset(String input, int index) {
        if (input == null) {
            throw new IllegalArgumentException("input must not be null");
        }
        if (index < 0) {
            throw new IllegalArgumentException("index must be non-negative");
        }
        if (index >= input.length()) {
            throw new IllegalArgumentException("index must be within the bounds of the input string");
        }

        var offset = 0;
        for (var charIndex = 0; charIndex < index; charIndex++) {
            final var theChar = input.charAt(charIndex);
            if (theChar <= 0x007F) {
                offset += 1;
            } else if (theChar <= 0x07FF) {
                offset += 2;
            } else if (Character.isHighSurrogate(theChar) || Character.isLowSurrogate(theChar)) {
                offset += 2;
            } else {
                offset += 3;
            }
        }

        return offset;
    }

    /**
     * Get what \R matches by default.
     *
     * @param api the PCRE2 API
     * @return the default \R match
     */
    public static Pcre2Bsr getDefaultBsr(IPcre2 api) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        final var bsr = new int[1];
        final var result = api.config(IPcre2.CONFIG_BSR, bsr);
        if (result < 0) {
            throw new IllegalStateException(getErrorMessage(api, result));
        }

        return Pcre2Bsr.valueOf(bsr[0]).orElseThrow();
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
     * @param subject     the string to which the ovector values correspond
     * @param subjectUtf8 the {@param subject} string encoded as UTF-8 {@code byte[]}
     * @param ovector     the byte-based ovector offset pairs
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
