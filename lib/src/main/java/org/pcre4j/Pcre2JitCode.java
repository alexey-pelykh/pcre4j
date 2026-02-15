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
package org.pcre4j;

import org.pcre4j.api.IPcre2;
import org.pcre4j.option.Pcre2CompileOption;
import org.pcre4j.option.Pcre2JitOption;
import org.pcre4j.option.Pcre2MatchOption;

import java.util.EnumSet;

/**
 * A JIT-compiled pattern.
 */
public class Pcre2JitCode extends Pcre2Code {

    /**
     * The supported match options for JIT-compiled patterns.
     */
    private static final EnumSet<Pcre2MatchOption> SUPPORTED_MATCH_OPTIONS = EnumSet.of(
            Pcre2MatchOption.NOTBOL,
            Pcre2MatchOption.NOTEOL,
            Pcre2MatchOption.NOTEMPTY,
            Pcre2MatchOption.NOTEMPTY_ATSTART,
            Pcre2MatchOption.PARTIAL_HARD,
            Pcre2MatchOption.PARTIAL_SOFT
    );

    /**
     * Constructor for Pcre2JitCode
     *
     * @param pattern        the pattern to compile
     * @param options        the flags to compile the pattern with, see {@link Pcre2CompileOption} or null for default
     *                       options
     * @param jitOptions     the flags to compile the pattern with JIT, see {@link Pcre2JitOption} or null for default
     *                       options
     * @param compileContext the compile context to use or null
     */
    public Pcre2JitCode(
            String pattern,
            EnumSet<Pcre2CompileOption> options,
            EnumSet<Pcre2JitOption> jitOptions,
            Pcre2CompileContext compileContext
    ) {
        this(Pcre4j.api(), pattern, options, jitOptions, compileContext);
    }

    /**
     * Constructor for Pcre2JitCode
     *
     * @param api            the PCRE2 API to use
     * @param pattern        the pattern to compile
     * @param options        the flags to compile the pattern with, see {@link Pcre2CompileOption} or null for default
     *                       options
     * @param jitOptions     the flags to compile the pattern with JIT, see {@link Pcre2JitOption} or null for default
     *                       options
     * @param compileContext the compile context to use or null
     */
    public Pcre2JitCode(
            IPcre2 api,
            String pattern,
            EnumSet<Pcre2CompileOption> options,
            EnumSet<Pcre2JitOption> jitOptions,
            Pcre2CompileContext compileContext
    ) {
        super(api, pattern, options, compileContext);

        if (jitOptions == null) {
            jitOptions = EnumSet.of(
                    Pcre2JitOption.COMPLETE,
                    Pcre2JitOption.PARTIAL_SOFT,
                    Pcre2JitOption.PARTIAL_HARD
            );
        }

        final var jitResult = api.jitCompile(
                handle,
                jitOptions
                        .stream()
                        .mapToInt(Pcre2JitOption::value).sum()
        );
        if (jitResult != 0) {
            throw new IllegalStateException(Pcre4jUtils.getErrorMessage(api, jitResult));
        }
    }

    /**
     * Get the supported match options for JIT-compiled patterns.
     *
     * @return the supported match options
     */
    public static EnumSet<Pcre2MatchOption> getSupportedMatchOptions() {
        return EnumSet.copyOf(SUPPORTED_MATCH_OPTIONS);
    }

    @Override
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

        return api.jitMatch(
                handle,
                subject,
                Pcre4jUtils.convertCharacterIndexToByteOffset(subject, startOffset),
                options.stream().mapToInt(Pcre2MatchOption::value).sum(),
                matchData.handle,
                matchContext != null ? matchContext.handle : 0
        );
    }
}
