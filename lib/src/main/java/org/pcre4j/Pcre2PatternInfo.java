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

import java.util.Arrays;
import java.util.Optional;

/**
 * Pattern information codes for {@link Pcre2Code} introspection.
 */
public enum Pcre2PatternInfo {
    /**
     * Final options after compiling
     */
    INFO_ALLOPTIONS(IPcre2.INFO_ALLOPTIONS),

    /**
     * Options passed to {@link IPcre2#compile}
     */
    INFO_ARGOPTIONS(IPcre2.INFO_ARGOPTIONS),

    /**
     * Number of highest backreference
     */
    INFO_BACKREFMAX(IPcre2.INFO_BACKREFMAX),

    /**
     * What \R matches:
     * PCRE2_BSR_UNICODE: Unicode line endings
     * PCRE2_BSR_ANYCRLF: CR, LF, or CRLF only
     */
    INFO_BSR(IPcre2.INFO_BSR),

    /**
     * Number of capturing subpatterns
     */
    INFO_CAPTURECOUNT(IPcre2.INFO_CAPTURECOUNT),

    /**
     * First code unit when type is 1
     */
    INFO_FIRSTCODEUNIT(IPcre2.INFO_FIRSTCODEUNIT),

    /**
     * Type of start-of-match information
     * 0 nothing set
     * 1 first code unit is set
     * 2 start of string or after newline
     */
    INFO_FIRSTCODETYPE(IPcre2.INFO_FIRSTCODETYPE),

    /**
     * Bitmap of first code units, or 0
     */
    INFO_FIRSTBITMAP(IPcre2.INFO_FIRSTBITMAP),

    /**
     * Return 1 if explicit CR or LF matches exist in the pattern
     */
    INFO_HASCRORLF(IPcre2.INFO_HASCRORLF),

    /**
     * Return 1 if (?J) or (?-J) was used
     */
    INFO_JCHANGED(IPcre2.INFO_JCHANGED),

    /**
     * Size of JIT compiled code, or 0
     */
    INFO_JITSIZE(IPcre2.INFO_JITSIZE),

    /**
     * Last code unit when type is 1
     */
    INFO_LASTCODEUNIT(IPcre2.INFO_LASTCODEUNIT),

    /**
     * Type of must-be-present information
     * 0 nothing set
     * 1 code unit is set
     */
    INFO_LASTCODETYPE(IPcre2.INFO_LASTCODETYPE),

    /**
     * 1 if the pattern can match an empty string, 0 otherwise
     */
    INFO_MATCHEMPTY(IPcre2.INFO_MATCHEMPTY),

    /**
     * Match limit if set, otherwise {@link IPcre2#ERROR_UNSET}
     */
    INFO_MATCHLIMIT(IPcre2.INFO_MATCHLIMIT),

    /**
     * Length (in characters) of the longest lookbehind assertion
     */
    INFO_MAXLOOKBEHIND(IPcre2.INFO_MAXLOOKBEHIND),

    /**
     * Lower bound length of matching strings
     */
    INFO_MINLENGTH(IPcre2.INFO_MINLENGTH),

    /**
     * Number of named subpatterns
     */
    INFO_NAMECOUNT(IPcre2.INFO_NAMECOUNT),

    /**
     * Size of name table entries
     */
    INFO_NAMEENTRYSIZE(IPcre2.INFO_NAMEENTRYSIZE),

    /**
     * Pointer to name table
     */
    INFO_NAMETABLE(IPcre2.INFO_NAMETABLE),

    /**
     * Code for the newline sequence:
     * {@link IPcre2#NEWLINE_CR}
     * {@link IPcre2#NEWLINE_LF}
     * {@link IPcre2#NEWLINE_CRLF}
     * {@link IPcre2#NEWLINE_ANY}
     * {@link IPcre2#NEWLINE_ANYCRLF}
     * {@link IPcre2#NEWLINE_NUL}
     */
    INFO_NEWLINE(IPcre2.INFO_NEWLINE),

    /**
     * Backtracking depth limit if set, otherwise {@link IPcre2#ERROR_UNSET}
     */
    INFO_DEPTHLIMIT(IPcre2.INFO_DEPTHLIMIT),

    /**
     * Obsolete synonym for {@link #INFO_DEPTHLIMIT}
     */
    @Deprecated INFO_RECURSIONLIMIT(IPcre2.INFO_RECURSIONLIMIT),

    /**
     * Size of compiled pattern
     */
    INFO_SIZE(IPcre2.INFO_SIZE),

    /**
     * Return 1 if pattern contains \C
     */
    INFO_HASBACKSLASHC(IPcre2.INFO_HASBACKSLASHC),

    /**
     * Size of backtracking frame
     */
    INFO_FRAMESIZE(IPcre2.INFO_FRAMESIZE),

    /**
     * Heap memory limit if set, otherwise {@link IPcre2#ERROR_UNSET}
     */
    INFO_HEAPLIMIT(IPcre2.INFO_HEAPLIMIT),

    /**
     * Extra options that were passed in the compile context
     */
    INFO_EXTRAOPTIONS(IPcre2.INFO_EXTRAOPTIONS);

    /**
     * The integer value
     */
    private final int value;

    /**
     * Create an enum entry with the given integer value.
     *
     * @param value the integer value
     */
    private Pcre2PatternInfo(int value) {
        this.value = value;
    }

    /**
     * Get the enum entry by its integer value.
     *
     * @param value the integer value
     * @return the enum entry
     */
    public static Optional<Pcre2PatternInfo> valueOf(int value) {
        return Arrays.stream(values())
                .filter(entry -> entry.value == value)
                .findFirst();
    }

    /**
     * Get the integer value.
     *
     * @return the integer value
     */
    public int value() {
        return value;
    }
}
