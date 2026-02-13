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

import org.pcre4j.api.INativeMemoryAccess;
import org.pcre4j.api.IPcre2;

import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * A compiled pattern.
 */
public class Pcre2Code {

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
     * Create a compiled pattern from a pattern string
     *
     * @param pattern the pattern to compile
     */
    public Pcre2Code(String pattern) {
        this(pattern, null, null);
    }

    /**
     * Create a compiled pattern from a pattern string
     *
     * @param pattern the pattern to compile
     * @param options the flags to compile the pattern with, see {@link Pcre2CompileOption} or null for default
     *                options
     */
    public Pcre2Code(
            String pattern,
            EnumSet<Pcre2CompileOption> options
    ) {
        this(pattern, options, null);
    }

    /**
     * Create a compiled pattern from a pattern string
     *
     * @param pattern        the pattern to compile
     * @param options        the flags to compile the pattern with, see {@link Pcre2CompileOption} or null for default
     *                       options
     * @param compileContext the compile context to use or null
     */
    public Pcre2Code(
            String pattern,
            EnumSet<Pcre2CompileOption> options,
            Pcre2CompileContext compileContext
    ) {
        this(Pcre4j.api(), pattern, options, compileContext);
    }

    /**
     * Create a compiled pattern from a pattern string
     *
     * @param api     the PCRE2 API to use
     * @param pattern the pattern to compile
     */
    public Pcre2Code(
            IPcre2 api,
            String pattern
    ) {
        this(api, pattern, null, null);
    }

    /**
     * Create a compiled pattern from a pattern string
     *
     * @param api     the PCRE2 API to use
     * @param pattern the pattern to compile
     * @param options the flags to compile the pattern with, see {@link Pcre2CompileOption} or null for default
     *                options
     */
    public Pcre2Code(
            IPcre2 api,
            String pattern,
            EnumSet<Pcre2CompileOption> options
    ) {
        this(api, pattern, options, null);
    }

    /**
     * Create a compiled pattern from a pattern string
     *
     * @param api            the PCRE2 API to use
     * @param pattern        the pattern to compile
     * @param options        the flags to compile the pattern with, see {@link Pcre2CompileOption} or null for default
     *                       options
     * @param compileContext the compile context to use or null
     */
    public Pcre2Code(
            IPcre2 api,
            String pattern,
            EnumSet<Pcre2CompileOption> options,
            Pcre2CompileContext compileContext
    ) {
        if (api == null) {
            throw new IllegalArgumentException("api cannot be null");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("pattern cannot be null");
        }
        if (options == null) {
            options = EnumSet.noneOf(Pcre2CompileOption.class);
        }

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
            throw new Pcre2CompileException(
                    pattern, erroroffset[0], Pcre4jUtils.getErrorMessage(api, errorcode[0]), errorcode[0]
            );
        }

        this.api = api;
        this.handle = handle;
        this.cleanable = Pcre4jCleaner.INSTANCE.register(this, new Clean(api, handle));
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
            throw new Pcre2PatternInfoSizeException(Pcre2PatternInfo.valueOf(info).orElseThrow(), size);
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
        final var infoSize = api.patternInfo(handle, info);

        if (infoSize == 4) {
            final var where = new int[1];
            final var error = api.patternInfo(handle, info, where);
            if (error != 0) {
                throw new IllegalStateException(Pcre4jUtils.getErrorMessage(api, error));
            }

            return where[0];
        } else if (infoSize == 8) {
            final var where = new long[1];
            final var error = api.patternInfo(handle, info, where);
            if (error != 0) {
                throw new IllegalStateException(Pcre4jUtils.getErrorMessage(api, error));
            }

            return where[0];
        }

        throw new Pcre2PatternInfoSizeException(Pcre2PatternInfo.valueOf(info).orElseThrow(), infoSize);
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

    /**
     * Get the final options after compiling.
     * <p>
     * This returns the compile options as modified by any top-level option settings such as {@code (*UTF)}
     * at the start of the pattern. In other words, it reflects the options that were actually in effect
     * during compilation, which may differ from the original options passed to
     * {@link #Pcre2Code(IPcre2, String, EnumSet, Pcre2CompileContext)}.
     *
     * @return the final compile options
     */
    public EnumSet<Pcre2CompileOption> allOptions() {
        final var allOptions = getPatternIntInfo(IPcre2.INFO_ALLOPTIONS);
        return Arrays.stream(Pcre2CompileOption.values())
                .filter(flag -> (allOptions & flag.value()) != 0)
                .collect(() -> EnumSet.noneOf(Pcre2CompileOption.class), EnumSet::add, EnumSet::addAll);
    }

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
        return getPatternIntInfo(IPcre2.INFO_DEPTHLIMIT);
    }

    /**
     * Get the size of backtracking frame
     *
     * @return the size of backtracking frame
     */
    public long frameSize() {
        return getPatternSizeInfo(IPcre2.INFO_FRAMESIZE);
    }

    /**
     * Get the extra options that were passed in the compile context.
     *
     * @return the extra compile options
     */
    public EnumSet<Pcre2CompileExtraOption> extraOptions() {
        final var extraOptions = getPatternIntInfo(IPcre2.INFO_EXTRAOPTIONS);
        return Arrays.stream(Pcre2CompileExtraOption.values())
                .filter(flag -> (extraOptions & flag.value()) != 0)
                .collect(
                        () -> EnumSet.noneOf(Pcre2CompileExtraOption.class), EnumSet::add, EnumSet::addAll
                );
    }

    /**
     * Get the bitmap of first code units, or {@code null} if no bitmap is available.
     * <p>
     * When a pattern does not start with a specific character but PCRE2 can determine a set of possible
     * first code units, this method returns a 256-bit (32-byte) bitmap where each set bit indicates
     * that the corresponding code unit value may start a match.
     * <p>
     * A bitmap is typically available when {@link #firstCodeType()} returns 0 (no single fixed start
     * character) and the pattern has a class or alternation at the start.
     *
     * @return a 32-byte array representing the 256-bit bitmap of possible first code units,
     *         or {@code null} if no bitmap is available
     */
    public byte[] firstBitmap() {
        final var where = new long[1];
        final var error = api.patternInfo(handle, IPcre2.INFO_FIRSTBITMAP, where);
        if (error != 0) {
            throw new IllegalStateException(Pcre4jUtils.getErrorMessage(api, error));
        }

        if (where[0] == 0) {
            return null;
        }

        return ((INativeMemoryAccess) api).readBytes(where[0], 32);
    }

    /**
     * Get the type of start-of-match information.
     * <p>
     * This indicates how the pattern is anchored at the start:
     * <ul>
     *   <li>0 - nothing set (pattern not anchored to start)</li>
     *   <li>1 - first code unit is set (pattern starts with a specific character)</li>
     *   <li>2 - start of string or after newline (pattern is anchored with ^ or similar)</li>
     * </ul>
     *
     * @return the first code type (0, 1, or 2)
     */
    public int firstCodeType() {
        return getPatternIntInfo(IPcre2.INFO_FIRSTCODETYPE);
    }

    /**
     * Get the first code unit of the compiled pattern.
     * <p>
     * This is meaningful only when {@link #firstCodeType()} returns 1, indicating that the pattern
     * always starts with a specific code unit value.
     *
     * @return the value of the first code unit
     */
    public int firstCodeUnit() {
        return getPatternIntInfo(IPcre2.INFO_FIRSTCODEUNIT);
    }

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
        return getPatternIntInfo(IPcre2.INFO_HEAPLIMIT);
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

    /**
     * Get the type of must-be-present information for the last code unit.
     * <p>
     * This indicates whether PCRE2 has determined a required last code unit for the pattern:
     * <ul>
     *   <li>0 - nothing set (no required last code unit)</li>
     *   <li>1 - a last code unit is set (retrieve it with {@link #lastCodeUnit()})</li>
     * </ul>
     *
     * @return the last code type (0 or 1)
     */
    public int lastCodeType() {
        return getPatternIntInfo(IPcre2.INFO_LASTCODETYPE);
    }

    /**
     * Get the last code unit of the compiled pattern.
     * <p>
     * This is meaningful only when {@link #lastCodeType()} returns 1, indicating that the pattern
     * has a required last code unit value that must be present in any matching string.
     *
     * @return the value of the last code unit
     */
    public int lastCodeUnit() {
        return getPatternIntInfo(IPcre2.INFO_LASTCODEUNIT);
    }

    /**
     * Get the match limit
     *
     * @return the match limit
     */
    public int matchLimit() {
        return getPatternIntInfo(IPcre2.INFO_MATCHLIMIT);
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
     * Convert a named capturing group to its group number.
     * <p>
     * This method is useful for pre-resolving named group references before a matching loop,
     * avoiding repeated name lookups during matching.
     *
     * @param name the name of the capturing group
     * @return the group number (1-based index)
     * @throws IllegalArgumentException if name is null
     * @throws Pcre2NoSubstringException if the name does not correspond to any capturing group
     * @throws Pcre2NoUniqueSubstringException if the name is not unique (when using the {@code (?J)} option
     *                                         for duplicate names)
     */
    public int groupNumberFromName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }

        final var result = api.substringNumberFromName(handle, name);
        if (result == IPcre2.ERROR_NOSUBSTRING) {
            throw new Pcre2NoSubstringException(
                    "Named group '" + name + "' does not exist", IPcre2.ERROR_NOSUBSTRING
            );
        }
        if (result == IPcre2.ERROR_NOUNIQUESUBSTRING) {
            throw new Pcre2NoUniqueSubstringException(
                    "Named group '" + name + "' is not unique", IPcre2.ERROR_NOUNIQUESUBSTRING
            );
        }
        if (result < 0) {
            throw new IllegalStateException(Pcre4jUtils.getErrorMessage(api, result));
        }

        return result;
    }

    /**
     * Scan the name table for all group numbers associated with a given capture group name.
     * <p>
     * This method is particularly useful when duplicate names are allowed (via DUPNAMES option),
     * as a name may map to multiple group numbers. The returned array contains all group numbers
     * associated with the specified name, in ascending order.
     *
     * @param name the name of the capturing group to look up
     * @return an array of group numbers (1-based indices) associated with the name
     * @throws IllegalArgumentException if name is null
     * @throws Pcre2NoSubstringException if the name does not correspond to any capturing group
     */
    public int[] scanNametable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }

        final long[] first = new long[1];
        final long[] last = new long[1];
        final var entrySize = api.substringNametableScan(handle, name, first, last);
        if (entrySize == IPcre2.ERROR_NOSUBSTRING) {
            throw new Pcre2NoSubstringException(
                    "Named group '" + name + "' does not exist", IPcre2.ERROR_NOSUBSTRING
            );
        }
        if (entrySize < 0) {
            throw new IllegalStateException(Pcre4jUtils.getErrorMessage(api, entrySize));
        }

        // Calculate the number of entries
        final var numEntries = (int) ((last[0] - first[0]) / entrySize) + 1;
        final int[] groupNumbers = new int[numEntries];

        // Read the group numbers from each entry
        // Each entry starts with a 2-byte group number (big-endian)
        for (int i = 0; i < numEntries; i++) {
            final var entryStart = first[0] + ((long) i * entrySize);
            final var bytes = ((INativeMemoryAccess) api).readBytes(entryStart, 2);
            // Group number is stored as big-endian 16-bit value
            groupNumbers[i] = ((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF);
        }

        return groupNumbers;
    }

    /**
     * Match this compiled pattern against a given subject string.
     *
     * @param subject      the subject string to match this pattern against
     * @param startOffset  offset in the subject at which to start matching
     * @param options      the options, see {@link Pcre2MatchOption}
     * @param matchData    the match data to store the results in
     * @param matchContext the match context to use or null
     * @return the number of captures plus one, zero if the {@param matchData} is too small, or a negative value if
     * there was no match or an actual error occurred
     */
    public int match(
            String subject,
            int startOffset,
            EnumSet<Pcre2MatchOption> options,
            Pcre2MatchData matchData,
            Pcre2MatchContext matchContext
    ) {
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null");
        }
        if (startOffset < 0) {
            throw new IllegalArgumentException("startOffset must be greater than or equal to zero");
        }
        if (startOffset > subject.length()) {
            throw new IllegalArgumentException("startOffset must be less than or equal to the length of the subject");
        }
        if (matchData == null) {
            throw new IllegalArgumentException("matchData must not be null");
        }

        return api.match(
                handle,
                subject,
                Pcre4jUtils.convertCharacterIndexToByteOffset(subject, startOffset),
                options
                        .stream()
                        .mapToInt(Pcre2MatchOption::value)
                        .sum(),
                matchData.handle,
                matchContext != null ? matchContext.handle : 0
        );
    }

    /**
     * Substitute matches of this compiled pattern in the given subject string.
     *
     * @param subject      the subject string to perform substitution on
     * @param startOffset  offset in the subject at which to start matching
     * @param options      the options, see {@link Pcre2SubstituteOption}
     * @param matchData    the match data to use or null
     * @param matchContext the match context to use or null
     * @param replacement  the replacement string (supports backreferences like $1, ${name})
     * @return the result string after substitution
     * @throws Pcre2SubstituteError if an error occurs during substitution
     */
    public String substitute(
            String subject,
            int startOffset,
            EnumSet<Pcre2SubstituteOption> options,
            Pcre2MatchData matchData,
            Pcre2MatchContext matchContext,
            String replacement
    ) {
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null");
        }
        if (startOffset < 0) {
            throw new IllegalArgumentException("startOffset must be greater than or equal to zero");
        }
        if (startOffset > subject.length()) {
            throw new IllegalArgumentException("startOffset must be less than or equal to the length of the subject");
        }
        if (replacement == null) {
            throw new IllegalArgumentException("replacement must not be null");
        }
        if (options == null) {
            options = EnumSet.noneOf(Pcre2SubstituteOption.class);
        }

        final var optionBits = options
                .stream()
                .mapToInt(Pcre2SubstituteOption::value)
                .sum();

        // First, try with a reasonable initial buffer size
        var bufferSize = Math.max(subject.length() * 2 + replacement.length(), 256);
        var outputBuffer = ByteBuffer.allocateDirect(bufferSize);
        var outputLength = new long[]{bufferSize};

        var result = api.substitute(
                handle,
                subject,
                Pcre4jUtils.convertCharacterIndexToByteOffset(subject, startOffset),
                optionBits | IPcre2.SUBSTITUTE_OVERFLOW_LENGTH,
                matchData != null ? matchData.handle : 0,
                matchContext != null ? matchContext.handle : 0,
                replacement,
                outputBuffer,
                outputLength
        );

        // If buffer was too small, reallocate and retry
        if (result == IPcre2.ERROR_NOMEMORY) {
            bufferSize = (int) outputLength[0] + 1; // +1 for null terminator
            outputBuffer = ByteBuffer.allocateDirect(bufferSize);
            outputLength[0] = bufferSize;

            result = api.substitute(
                    handle,
                    subject,
                    Pcre4jUtils.convertCharacterIndexToByteOffset(subject, startOffset),
                    optionBits,
                    matchData != null ? matchData.handle : 0,
                    matchContext != null ? matchContext.handle : 0,
                    replacement,
                    outputBuffer,
                    outputLength
            );
        }

        if (result < 0) {
            throw new Pcre2SubstituteException(Pcre4jUtils.getErrorMessage(api, result), result);
        }

        // Extract the result string from the buffer
        final var resultBytes = new byte[(int) outputLength[0]];
        outputBuffer.get(resultBytes);
        return new String(resultBytes, StandardCharsets.UTF_8);
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
