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
package org.pcre4j.option;

import org.pcre4j.api.IPcre2;

import java.util.Arrays;
import java.util.Optional;

/**
 * Extra compile options for PCRE2 patterns.
 * <p>
 * These options are set via
 * {@link org.pcre4j.Pcre2CompileContext#setCompileExtraOptions(java.util.EnumSet)} and provide
 * additional control over pattern compilation beyond the standard compile options.
 */
public enum Pcre2CompileExtraOption {
    /**
     * Allow surrogate escapes in UTF-8 mode.
     * <p>
     * This option allows \x{d800} to \x{dfff} surrogate code point escapes in UTF-8 mode,
     * which are normally forbidden.
     */
    ALLOW_SURROGATE_ESCAPES(IPcre2.EXTRA_ALLOW_SURROGATE_ESCAPES),

    /**
     * Treat unrecognized escape sequences as literals.
     * <p>
     * This option causes unrecognized escape sequences to be treated as literal characters
     * rather than generating an error.
     */
    BAD_ESCAPE_IS_LITERAL(IPcre2.EXTRA_BAD_ESCAPE_IS_LITERAL),

    /**
     * Pattern matches whole words.
     * <p>
     * This option implicitly wraps the pattern in \b...\b to match whole words only.
     */
    MATCH_WORD(IPcre2.EXTRA_MATCH_WORD),

    /**
     * Pattern matches whole lines.
     * <p>
     * This option implicitly wraps the pattern in ^..$ to match whole lines only.
     */
    MATCH_LINE(IPcre2.EXTRA_MATCH_LINE),

    /**
     * Interpret escaped CR as LF.
     * <p>
     * This option causes \r to be interpreted as \n in patterns.
     */
    ESCAPED_CR_IS_LF(IPcre2.EXTRA_ESCAPED_CR_IS_LF),

    /**
     * Extended alternate handling of &#92;u, &#92;U, and &#92;x.
     * <p>
     * This option provides ECMAScript-like handling of Unicode escapes, extending the
     * behavior of the ALT_BSUX compile option.
     */
    ALT_BSUX(IPcre2.EXTRA_ALT_BSUX),

    /**
     * Allow \K in lookaround assertions.
     * <p>
     * This option permits the use of \K within lookahead and lookbehind assertions,
     * which is normally forbidden.
     */
    ALLOW_LOOKAROUND_BSK(IPcre2.EXTRA_ALLOW_LOOKAROUND_BSK),

    /**
     * Restrict caseless matching to same-script characters.
     * <p>
     * This option prevents caseless matching between characters from different scripts
     * when UCP is enabled.
     */
    CASELESS_RESTRICT(IPcre2.EXTRA_CASELESS_RESTRICT),

    /**
     * Use ASCII for \d in Unicode mode.
     * <p>
     * This option restricts \d to match only ASCII digits [0-9] even when UCP is enabled.
     */
    ASCII_BSD(IPcre2.EXTRA_ASCII_BSD),

    /**
     * Use ASCII for \s in Unicode mode.
     * <p>
     * This option restricts \s to match only ASCII whitespace even when UCP is enabled.
     */
    ASCII_BSS(IPcre2.EXTRA_ASCII_BSS),

    /**
     * Use ASCII for \w in Unicode mode.
     * <p>
     * This option restricts \w to match only ASCII word characters [a-zA-Z0-9_] even when UCP is enabled.
     */
    ASCII_BSW(IPcre2.EXTRA_ASCII_BSW),

    /**
     * Use ASCII for POSIX classes in Unicode mode.
     * <p>
     * This option restricts POSIX character classes like [:alpha:] and [:digit:] to match
     * only ASCII characters even when UCP is enabled.
     */
    ASCII_POSIX(IPcre2.EXTRA_ASCII_POSIX),

    /**
     * Use ASCII for \d (alias for ASCII_BSD).
     * <p>
     * This option is an alias for ASCII_BSD and restricts \d to match only ASCII digits.
     */
    ASCII_DIGIT(IPcre2.EXTRA_ASCII_DIGIT);

    /**
     * The integer value of the option
     */
    private final int value;

    /**
     * Create a new enum value for the given option value.
     *
     * @param value the integer value of the option
     */
    private Pcre2CompileExtraOption(int value) {
        this.value = value;
    }

    /**
     * Get the enum value by its option value.
     *
     * @param value the integer value of the option
     * @return the flag
     */
    public static Optional<Pcre2CompileExtraOption> valueOf(int value) {
        return Arrays.stream(values())
                .filter(flag -> flag.value == value)
                .findFirst();
    }

    /**
     * Get the option value of the enum value.
     *
     * @return the integer value of the option
     */
    public int value() {
        return value;
    }

}
