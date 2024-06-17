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

public enum Pcre2CompileOption {
    /**
     * Force pattern anchoring
     */
    ANCHORED(IPcre2.ANCHORED),

    /**
     * Do not check the pattern for UTF validity (only relevant if UTF is set)
     */
    NO_UTF_CHECK(IPcre2.NO_UTF_CHECK),

    /**
     * Pattern can match only at end of subject
     */
    ENDANCHORED(IPcre2.ENDANCHORED),

    /**
     * Allow empty classes
     */
    ALLOW_EMPTY_CLASS(IPcre2.ALLOW_EMPTY_CLASS),

    /**
     * Alternative handling of ⧵u, ⧵U, and ⧵x
     */
    ALT_BSUX(IPcre2.ALT_BSUX),

    /**
     * Compile automatic callouts
     */
    AUTO_CALLOUT(IPcre2.AUTO_CALLOUT),

    /**
     * Do caseless matching
     */
    CASELESS(IPcre2.CASELESS),

    /**
     * $ not to match newline at end
     */
    DOLLAR_ENDONLY(IPcre2.DOLLAR_ENDONLY),

    /**
     * . matches anything including NL
     */
    DOTALL(IPcre2.DOTALL),

    /**
     * Allow duplicate names for subpatterns
     */
    DUPNAMES(IPcre2.DUPNAMES),

    /**
     * Ignore white space and # comments
     */
    EXTENDED(IPcre2.EXTENDED),

    /**
     * Force matching to be before newline
     */
    FIRSTLINE(IPcre2.FIRSTLINE),

    /**
     * Match unset backreferences
     */
    MATCH_UNSET_BACKREF(IPcre2.MATCH_UNSET_BACKREF),

    /**
     * ^ and $ match newlines within data
     */
    MULTILINE(IPcre2.MULTILINE),

    /**
     * Lock out PCRE2_UCP, e.g. via (*UCP)
     */
    NEVER_UCP(IPcre2.NEVER_UCP),

    /**
     * Lock out PCRE2_UTF, e.g. via (*UTF)
     */
    NEVER_UTF(IPcre2.NEVER_UTF),

    /**
     * Disable numbered capturing parentheses (named ones available)
     */
    NO_AUTO_CAPTURE(IPcre2.NO_AUTO_CAPTURE),

    /**
     * Disable auto-possessification
     */
    NO_AUTO_POSSESS(IPcre2.NO_AUTO_POSSESS),

    /**
     * Disable automatic anchoring for .*
     */
    NO_DOTSTAR_ANCHOR(IPcre2.NO_DOTSTAR_ANCHOR),

    /**
     * Disable match-time start optimizations
     */
    NO_START_OPTIMIZE(IPcre2.NO_START_OPTIMIZE),

    /**
     * Use Unicode properties for \d, \w, etc.
     */
    UCP(IPcre2.UCP),

    /**
     * Invert greediness of quantifiers
     */
    UNGREEDY(IPcre2.UNGREEDY),

    /**
     * Treat pattern and subjects as UTF strings
     */
    UTF(IPcre2.UTF),

    /**
     * Lock out the use of \C in patterns
     */
    NEVER_BACKSLASH_C(IPcre2.NEVER_BACKSLASH_C),

    /**
     * Alternative handling of ^ in multiline mode
     */
    ALT_CIRCUMFLEX(IPcre2.ALT_CIRCUMFLEX),

    /**
     * Process backslashes in verb names
     */
    ALT_VERBNAMES(IPcre2.ALT_VERBNAMES),

    /**
     * Enable offset limit for unanchored matching
     */
    USE_OFFSET_LIMIT(IPcre2.USE_OFFSET_LIMIT),

    EXTENDED_MORE(IPcre2.EXTENDED_MORE),

    /**
     * Pattern characters are all literal
     */
    LITERAL(IPcre2.LITERAL),

    /**
     * Enable support for matching invalid UTF
     */
    MATCH_INVALID_UTF(IPcre2.MATCH_INVALID_UTF);

    /**
     * The integer value of the option
     */
    private final int value;

    /**
     * Create a new enum value for the given option value.
     *
     * @param value the integer value of the option
     */
    private Pcre2CompileOption(int value) {
        this.value = value;
    }

    /**
     * Get the enum value by its option value.
     *
     * @param value the integer value of the option
     * @return the flag
     */
    public static Optional<Pcre2CompileOption> valueOf(int value) {
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
