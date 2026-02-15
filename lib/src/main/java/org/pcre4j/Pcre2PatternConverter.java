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
import org.pcre4j.exception.Pcre2ConvertException;
import org.pcre4j.option.Pcre2ConvertOption;

import java.util.EnumSet;

/**
 * Utility class for converting glob patterns and POSIX regular expressions into PCRE2-compatible patterns.
 * <p>
 * This class wraps PCRE2's experimental pattern conversion function ({@code pcre2_pattern_convert})
 * to provide a convenient Java API. It supports three types of pattern conversion:
 * <ul>
 * <li><strong>Glob patterns</strong> — shell-style wildcards (e.g., {@code *.txt}, {@code src/**\/*.java})</li>
 * <li><strong>POSIX Basic Regular Expressions</strong> (BRE) — traditional Unix regex syntax</li>
 * <li><strong>POSIX Extended Regular Expressions</strong> (ERE) — modern Unix regex syntax</li>
 * </ul>
 * <p>
 * Example usage:
 * <pre>{@code
 * // Convert a glob pattern to PCRE2
 * String pcre2Pattern = Pcre2PatternConverter.fromGlob("*.txt");
 *
 * // Convert with glob options
 * String pcre2Pattern = Pcre2PatternConverter.fromGlob("src/**\/*.java",
 *         EnumSet.of(Pcre2ConvertOption.GLOB_NO_WILD_SEPARATOR));
 *
 * // Convert a POSIX extended regex to PCRE2
 * String pcre2Pattern = Pcre2PatternConverter.fromPosixEre("[[:alpha:]]+");
 * }</pre>
 */
public final class Pcre2PatternConverter {

    /**
     * Private constructor to prevent instantiation.
     */
    private Pcre2PatternConverter() {
    }

    /**
     * Convert a glob pattern to a PCRE2 regular expression using the default PCRE2 API.
     *
     * @param glob the glob pattern to convert (e.g., {@code *.txt}, {@code src/**\/*.java})
     * @return the equivalent PCRE2 regular expression pattern
     * @throws Pcre2ConvertException if the glob pattern has invalid syntax
     */
    public static String fromGlob(String glob) {
        return fromGlob(Pcre4j.api(), glob, EnumSet.noneOf(Pcre2ConvertOption.class), null);
    }

    /**
     * Convert a glob pattern to a PCRE2 regular expression using the default PCRE2 API.
     *
     * @param glob    the glob pattern to convert
     * @param options additional conversion options (e.g., {@link Pcre2ConvertOption#GLOB_NO_WILD_SEPARATOR})
     * @return the equivalent PCRE2 regular expression pattern
     * @throws Pcre2ConvertException if the glob pattern has invalid syntax
     */
    public static String fromGlob(String glob, EnumSet<Pcre2ConvertOption> options) {
        return fromGlob(Pcre4j.api(), glob, options, null);
    }

    /**
     * Convert a glob pattern to a PCRE2 regular expression using the default PCRE2 API.
     *
     * @param glob           the glob pattern to convert
     * @param options        additional conversion options
     * @param convertContext the convert context for additional settings (e.g., custom separator), or {@code null}
     * @return the equivalent PCRE2 regular expression pattern
     * @throws Pcre2ConvertException if the glob pattern has invalid syntax
     */
    public static String fromGlob(String glob, EnumSet<Pcre2ConvertOption> options,
                                  Pcre2ConvertContext convertContext) {
        return fromGlob(Pcre4j.api(), glob, options, convertContext);
    }

    /**
     * Convert a glob pattern to a PCRE2 regular expression.
     *
     * @param api            the PCRE2 API to use
     * @param glob           the glob pattern to convert
     * @param options        additional conversion options
     * @param convertContext the convert context for additional settings, or {@code null}
     * @return the equivalent PCRE2 regular expression pattern
     * @throws Pcre2ConvertException if the glob pattern has invalid syntax
     */
    public static String fromGlob(IPcre2 api, String glob, EnumSet<Pcre2ConvertOption> options,
                                  Pcre2ConvertContext convertContext) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }
        if (glob == null) {
            throw new IllegalArgumentException("glob must not be null");
        }
        if (options == null) {
            options = EnumSet.noneOf(Pcre2ConvertOption.class);
        }

        var optionBits = IPcre2.CONVERT_GLOB;
        for (var option : options) {
            optionBits |= option.value();
        }

        return convert(api, glob, optionBits, convertContext);
    }

    /**
     * Convert a POSIX Basic Regular Expression (BRE) to a PCRE2 pattern using the default PCRE2 API.
     *
     * @param bre the POSIX basic regular expression to convert
     * @return the equivalent PCRE2 regular expression pattern
     * @throws Pcre2ConvertException if the pattern has invalid syntax
     */
    public static String fromPosixBre(String bre) {
        return fromPosixBre(Pcre4j.api(), bre);
    }

    /**
     * Convert a POSIX Basic Regular Expression (BRE) to a PCRE2 pattern.
     *
     * @param api the PCRE2 API to use
     * @param bre the POSIX basic regular expression to convert
     * @return the equivalent PCRE2 regular expression pattern
     * @throws Pcre2ConvertException if the pattern has invalid syntax
     */
    public static String fromPosixBre(IPcre2 api, String bre) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }
        if (bre == null) {
            throw new IllegalArgumentException("bre must not be null");
        }

        return convert(api, bre, IPcre2.CONVERT_POSIX_BASIC, null);
    }

    /**
     * Convert a POSIX Extended Regular Expression (ERE) to a PCRE2 pattern using the default PCRE2 API.
     *
     * @param ere the POSIX extended regular expression to convert
     * @return the equivalent PCRE2 regular expression pattern
     * @throws Pcre2ConvertException if the pattern has invalid syntax
     */
    public static String fromPosixEre(String ere) {
        return fromPosixEre(Pcre4j.api(), ere);
    }

    /**
     * Convert a POSIX Extended Regular Expression (ERE) to a PCRE2 pattern.
     *
     * @param api the PCRE2 API to use
     * @param ere the POSIX extended regular expression to convert
     * @return the equivalent PCRE2 regular expression pattern
     * @throws Pcre2ConvertException if the pattern has invalid syntax
     */
    public static String fromPosixEre(IPcre2 api, String ere) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }
        if (ere == null) {
            throw new IllegalArgumentException("ere must not be null");
        }

        return convert(api, ere, IPcre2.CONVERT_POSIX_EXTENDED, null);
    }

    /**
     * Convert a pattern using the specified conversion options.
     * <p>
     * This is the general-purpose conversion method that accepts raw option bits. Prefer using
     * the typed methods ({@link #fromGlob}, {@link #fromPosixBre}, {@link #fromPosixEre}) for
     * most use cases.
     *
     * @param api            the PCRE2 API to use
     * @param pattern        the pattern to convert
     * @param options        the conversion options
     * @param convertContext the convert context for additional settings, or {@code null}
     * @return the equivalent PCRE2 regular expression pattern
     * @throws Pcre2ConvertException if the pattern has invalid syntax
     */
    public static String convert(IPcre2 api, String pattern, EnumSet<Pcre2ConvertOption> options,
                                 Pcre2ConvertContext convertContext) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("pattern must not be null");
        }
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("options must contain at least one conversion type");
        }

        var optionBits = 0;
        for (var option : options) {
            optionBits |= option.value();
        }

        return convert(api, pattern, optionBits, convertContext);
    }

    /**
     * Internal conversion method that calls the PCRE2 API.
     *
     * @param api            the PCRE2 API to use
     * @param pattern        the pattern to convert
     * @param options        the raw option bits
     * @param convertContext the convert context, or {@code null}
     * @return the converted PCRE2 pattern
     * @throws Pcre2ConvertException if conversion fails
     */
    private static String convert(IPcre2 api, String pattern, int options,
                                  Pcre2ConvertContext convertContext) {
        final var buffer = new long[]{0};
        final var blength = new long[]{0};

        final var result = api.patternConvert(
                pattern,
                options,
                buffer,
                blength,
                convertContext != null ? convertContext.handle : 0
        );

        if (result != 0) {
            throw new Pcre2ConvertException(pattern, Pcre4jUtils.getErrorMessage(api, result), result);
        }

        try {
            return api.readConvertedPattern(buffer[0], blength[0]);
        } finally {
            api.convertedPatternFree(buffer[0]);
        }
    }

}
