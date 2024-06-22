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

import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumSet;

public class Pcre2Code {

    private static final Cleaner cleaner = Cleaner.create();

    /**
     * The compiled pattern handle
     */
    /* package-private */ final long handle;

    /**
     * The PCRE2 API reference to use across the entire lifecycle of the object
     */
    /* package-private */ final IPcre2 api;

    /**
     * The cleaner to free the compiled pattern
     */
    private final Cleaner.Cleanable cleanable;

    /**
     * Constructor for Pcre2Code
     *
     * @param pattern        the pattern to compile
     * @param options        the flags to compile the pattern with, see {@link Pcre2CompileOption}
     * @param compileContext the compile context to use or null
     */
    public Pcre2Code(String pattern, EnumSet<Pcre2CompileOption> options, Pcre2CompileContext compileContext) {
        final var api = Pcre4j.api();

        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var handle = api.compile(
                pattern,
                options
                        .stream()
                        .mapToInt(Pcre2CompileOption::value)
                        .sum(),
                errorcode,
                erroroffset,
                compileContext != null ? compileContext.handle : 0
        );
        if (handle == 0) {
            throw new Pcre2CompileError(pattern, erroroffset[0], Pcre4jUtils.getErrorMessage(api, errorcode[0]));
        }

        this.api = api;
        this.handle = handle;
        this.cleanable = cleaner.register(this, new Clean(api, handle));
    }

    /**
     * Get the pattern information that is an integer
     *
     * @param info the information to get
     * @return the information integer value
     */
    private int getPatternIntInfo(int info) {
        final var size = api.patternInfo(handle, info);
        if (size != 4) {
            throw new Pcre2PatternInfoSizeError(Pcre2PatternInfo.valueOf(info).orElseThrow(), size);
        }

        final var where = new int[1];
        final var error = api.patternInfo(handle, info, where);
        if (error != 0) {
            throw new IllegalStateException(Pcre4jUtils.getErrorMessage(api, error));
        }

        return where[0];
    }

    /**
     * Get the pattern size information as long
     *
     * @param info the information to get
     * @return the size information as long
     */
    private long getPatternSizeInfo(int info) {
        final var infoSize = api.patternInfo(handle, IPcre2.INFO_FRAMESIZE);

        if (infoSize == 4) {
            final var where = new int[1];
            final var error = api.patternInfo(handle, IPcre2.INFO_FRAMESIZE, where);
            if (error != 0) {
                throw new IllegalStateException(Pcre4jUtils.getErrorMessage(api, error));
            }

            return where[0];
        } else if (infoSize == 8) {
            final var where = new long[1];
            final var error = api.patternInfo(handle, IPcre2.INFO_FRAMESIZE, where);
            if (error != 0) {
                throw new IllegalStateException(Pcre4jUtils.getErrorMessage(api, error));
            }

            return where[0];
        }

        throw new Pcre2PatternInfoSizeError(Pcre2PatternInfo.valueOf(IPcre2.INFO_FRAMESIZE).orElseThrow(), infoSize);
    }

    /**
     * Get the PCRE2 API backing this compiled pattern
     *
     * @return the PCRE2 API
     */
    public IPcre2 api() {
        return api;
    }

    /**
     * Get the handle of the compiled pattern
     *
     * @return the handle of the compiled pattern
     */
    public long handle() {
        return handle;
    }

    /**
     * Get the number of highest backreference
     *
     * @return the number of highest backreference
     */
    public int backRefMax() {
        return getPatternIntInfo(IPcre2.INFO_BACKREFMAX);
    }

//   TODO: PCRE2_INFO_ALLOPTIONS      Final options after compiling

    /**
     * Get the compile options
     *
     * @return the compile options
     */
    public EnumSet<Pcre2CompileOption> argOptions() {
        final var argOptions = getPatternIntInfo(IPcre2.INFO_ARGOPTIONS);
        return Arrays.stream(Pcre2CompileOption.values())
                .filter(flag -> (argOptions & flag.value()) != 0)
                .collect(() -> EnumSet.noneOf(Pcre2CompileOption.class), EnumSet::add, EnumSet::addAll);
    }

    /**
     * Get the number of capturing subpatterns
     *
     * @return the number of capturing subpatterns
     */
    public int captureCount() {
        return getPatternIntInfo(IPcre2.INFO_CAPTURECOUNT);
    }

    /**
     * Get what \R matches:
     * {@link Pcre2Bsr#UNICODE} for Unicode line endings
     * {@link Pcre2Bsr#ANYCRLF} for CR, LF, or CRLF only
     *
     * @return what \R matches
     */
    public Pcre2Bsr bsr() {
        final var bsr = getPatternIntInfo(IPcre2.INFO_BSR);
        return Pcre2Bsr.valueOf(bsr).orElseThrow();
    }

    /**
     * Get the backtracking depth limit
     *
     * @return the backtracking depth limit
     */
    public int depthLimit() {
        final var depthLimit = getPatternIntInfo(IPcre2.INFO_DEPTHLIMIT);
        if (depthLimit == IPcre2.ERROR_UNSET) {
            throw new IllegalStateException("Depth limit is not set");
        }
        return depthLimit;
    }

    /**
     * Get the size of backtracking frame
     *
     * @return the size of backtracking frame
     */
    public long frameSize() {
        return getPatternSizeInfo(IPcre2.INFO_FRAMESIZE);
    }

//   TODO: PCRE2_INFO_EXTRAOPTIONS    Extra options that were passed in the compile context
//   TODO: PCRE2_INFO_FIRSTBITMAP     Bitmap of first code units, or NULL
//   TODO: PCRE2_INFO_FIRSTCODETYPE   Type of start-of-match information
//                                0 nothing set
//                                1 first code unit is set
//                                2 start of string or after newline
//   TODO: PCRE2_INFO_FIRSTCODEUNIT   First code unit when type is 1

    /**
     * Check if the pattern contains \C
     *
     * @return true if the pattern contains \C, false otherwise
     */
    public boolean hasBackslashC() {
        final var hasBackslashC = getPatternIntInfo(IPcre2.INFO_HASBACKSLASHC);
        return hasBackslashC == 1;
    }

    /**
     * Check if explicit CR or LF matches exist in the pattern
     *
     * @return true if explicit CR or LF matches exist in the pattern, false otherwise
     */
    public boolean hasCrOrLf() {
        final var hasCrOrLf = getPatternIntInfo(IPcre2.INFO_HASCRORLF);
        return hasCrOrLf == 1;
    }

    /**
     * Get the heap limit
     *
     * @return the heap limit
     */
    public int heapLimit() {
        final var heapLimit = getPatternIntInfo(IPcre2.INFO_HEAPLIMIT);
        if (heapLimit == IPcre2.ERROR_UNSET) {
            throw new IllegalStateException("Heap limit is not set");
        }
        return heapLimit;
    }

    /**
     * Check if the pattern uses (?J) or (?-J)
     *
     * @return true if the pattern uses (?J) or (?-J), false otherwise
     */
    public boolean jChanged() {
        final var jChanged = getPatternIntInfo(IPcre2.INFO_JCHANGED);
        return jChanged == 1;
    }

    /**
     * Get the size of JIT compiled code, or 0
     *
     * @return the size of JIT compiled code, or 0
     */
    public long jitSize() {
        return getPatternSizeInfo(IPcre2.INFO_JITSIZE);
    }

    /**
     * Check if the pattern can match an empty string
     *
     * @return true if the pattern can match an empty string, false otherwise
     */
    public boolean matchEmpty() {
        final var matchEmpty = getPatternIntInfo(IPcre2.INFO_MATCHEMPTY);
        return matchEmpty == 1;
    }

//   TODO:PCRE2_INFO_LASTCODETYPE    Type of must-be-present information
//                                0 nothing set
//                                1 code unit is set
//   TODO:PCRE2_INFO_LASTCODEUNIT    Last code unit when type is 1

    /**
     * Get the match limit
     *
     * @return the match limit
     */
    public int matchLimit() {
        final var matchLimit = getPatternIntInfo(IPcre2.INFO_DEPTHLIMIT);
        if (matchLimit == IPcre2.ERROR_UNSET) {
            throw new IllegalStateException("Match limit is not set");
        }
        return matchLimit;
    }

    /**
     * Get the length (in characters) of the longest lookbehind assertion
     *
     * @return the length (in characters) of the longest lookbehind assertion
     */
    public int maxLookBehind() {
        return getPatternIntInfo(IPcre2.INFO_MAXLOOKBEHIND);
    }

    /**
     * Get the lower bound length of matching strings
     *
     * @return the lower bound length of matching strings
     */
    public int minLength() {
        return getPatternIntInfo(IPcre2.INFO_MINLENGTH);
    }

    /**
     * Get the number of named subpatterns
     *
     * @return the number of named subpatterns
     */
    public int nameCount() {
        return getPatternIntInfo(IPcre2.INFO_NAMECOUNT);
    }

    /**
     * Get the newline sequence
     *
     * @return the newline sequence
     */
    public Pcre2Newline newline() {
        final var newline = getPatternIntInfo(IPcre2.INFO_NEWLINE);
        return Pcre2Newline.valueOf(newline).orElseThrow();
    }

    /**
     * Get the size of name table entries
     *
     * @return the size of name table entries
     */
    public int nameEntrySize() {
        return getPatternIntInfo(IPcre2.INFO_NAMEENTRYSIZE);
    }

    /**
     * Get the name table
     *
     * @return the name table
     */
    public NameTableEntry[] nameTable() {
        final var nameCount = nameCount();
        final var nameEntrySize = nameEntrySize();
        final var where = ByteBuffer.allocate(nameCount * nameEntrySize);
        final var error = api.patternInfo(handle, IPcre2.INFO_NAMETABLE, where);
        if (error != 0) {
            throw new IllegalStateException(Pcre4jUtils.getErrorMessage(api, error));
        }

        final var nameTable = new NameTableEntry[nameCount];
        for (var nameIndex = 0; nameIndex < nameCount; nameIndex++) {
            final var offset = nameIndex * nameEntrySize;
            final var groupIndex = where.slice(offset, 2).getShort();
            final var groupNameUtf8 = where.slice(offset + 2, nameEntrySize - 2);
            while (groupNameUtf8.remaining() > 0) {
                if (groupNameUtf8.get() == 0) {
                    groupNameUtf8.limit(groupNameUtf8.position() - 1);
                    groupNameUtf8.position(0);
                    break;
                }
            }
            final var groupName = StandardCharsets.UTF_8.decode(groupNameUtf8).toString();
            nameTable[nameIndex] = new NameTableEntry(groupIndex, groupName);
        }
        return nameTable;
    }

    /**
     * Get the size of the compiled pattern
     *
     * @return the size of the compiled pattern
     */
    public long size() {
        return getPatternSizeInfo(IPcre2.INFO_SIZE);
    }

    /**
     * Match this compiled pattern against a given subject string.
     *
     * @param subject      the subject string to match this pattern against
     * @param startoffset  offset in the subject at which to start matching
     * @param options      the options, see {@link Pcre2MatchOption}
     * @param matchData    the match data to store the results in
     * @param matchContext the match context to use or null
     * @return the number of captures plus one, zero if the {@param matchData} is too small, or a negative value if
     * there was no match or an actual error occurred
     */
    public int match(
            String subject,
            int startoffset,
            EnumSet<Pcre2MatchOption> options,
            Pcre2MatchData matchData,
            Pcre2MatchContext matchContext
    ) {
        if (subject == null) {
            throw new IllegalArgumentException("Subject cannot be null");
        }
        if (matchData == null) {
            throw new IllegalArgumentException("Match data cannot be null");
        }

        return api.match(
                handle,
                subject,
                startoffset,
                options
                        .stream()
                        .mapToInt(Pcre2MatchOption::value)
                        .sum(),
                matchData.handle,
                matchContext != null ? matchContext.handle : 0
        );
    }

    /**
     * A name table entry
     *
     * @param group the group
     * @param name  the name
     */
    public record NameTableEntry(int group, String name) {
    }

    private record Clean(IPcre2 api, long code) implements Runnable {
        @Override
        public void run() {
            api.codeFree(code);
        }
    }

}
