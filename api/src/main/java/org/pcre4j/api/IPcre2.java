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
package org.pcre4j.api;

import java.nio.ByteBuffer;

/**
 * Interface for the PCRE2 API.
 *
 * @see <a href="https://www.pcre.org/current/doc/html/pcre2api.html">PCRE2 API</a>
 */
public interface IPcre2 {

    // Option bits for pcre2_compile(), pcre2_match(), pcre2_dfa_match(), pcre2_jit_match(), and pcre2_substitute().
    // Some also apply to pcre2_pattern_convert().

    /**
     * Force pattern anchoring
     */
    static final int ANCHORED = 0x80000000;

    /**
     * Do not check the pattern for UTF validity (only relevant if UTF is set)
     */
    static final int NO_UTF_CHECK = 0x40000000;

    /**
     * Pattern can match only at end of subject
     */
    static final int ENDANCHORED = 0x20000000;

    // Option bits for pcre2_compile() only

    /**
     * Allow empty classes
     */
    static final int ALLOW_EMPTY_CLASS = 0x00000001;

    /**
     * Alternative handling of ⧵u, ⧵U, and ⧵x
     */
    static final int ALT_BSUX = 0x00000002;

    /**
     * Compile automatic callouts
     */
    static final int AUTO_CALLOUT = 0x00000004;

    /**
     * Do caseless matching
     */
    static final int CASELESS = 0x00000008;

    /**
     * $ not to match newline at end
     */
    static final int DOLLAR_ENDONLY = 0x00000010;

    /**
     * . matches anything including NL
     */
    static final int DOTALL = 0x00000020;

    /**
     * Allow duplicate names for subpatterns
     */
    static final int DUPNAMES = 0x00000040;

    /**
     * Ignore white space and # comments
     */
    static final int EXTENDED = 0x00000080;

    /**
     * Force matching to be before newline
     */
    static final int FIRSTLINE = 0x00000100;

    /**
     * Match unset backreferences
     */
    static final int MATCH_UNSET_BACKREF = 0x00000200;

    /**
     * ^ and $ match newlines within data
     */
    static final int MULTILINE = 0x00000400;

    /**
     * Lock out PCRE2_UCP, e.g. via (*UCP)
     */
    static final int NEVER_UCP = 0x00000800;

    /**
     * Lock out PCRE2_UTF, e.g. via (*UTF)
     */
    static final int NEVER_UTF = 0x00001000;

    /**
     * Disable numbered capturing parentheses (named ones available)
     */
    static final int NO_AUTO_CAPTURE = 0x00002000;

    /**
     * Disable auto-possessification
     */
    static final int NO_AUTO_POSSESS = 0x00004000;

    /**
     * Disable automatic anchoring for .*
     */
    static final int NO_DOTSTAR_ANCHOR = 0x00008000;

    /**
     * Disable match-time start optimizations
     */
    static final int NO_START_OPTIMIZE = 0x00010000;

    /**
     * Use Unicode properties for \d, \w, etc.
     */
    static final int UCP = 0x00020000;

    /**
     * Invert greediness of quantifiers
     */
    static final int UNGREEDY = 0x00040000;

    /**
     * Treat pattern and subjects as UTF strings
     */
    static final int UTF = 0x00080000;

    /**
     * Lock out the use of \C in patterns
     */
    static final int NEVER_BACKSLASH_C = 0x00100000;

    /**
     * Alternative handling of ^ in multiline mode
     */
    static final int ALT_CIRCUMFLEX = 0x00200000;

    /**
     * Process backslashes in verb names
     */
    static final int ALT_VERBNAMES = 0x00400000;

    /**
     * Enable offset limit for unanchored matching
     */
    static final int USE_OFFSET_LIMIT = 0x00800000;

    /**
     * Like {@link #EXTENDED}, but also ignore unescaped space and horizontal tab in character classes
     */
    static final int EXTENDED_MORE = 0x01000000;

    /**
     * Pattern characters are all literal
     */
    static final int LITERAL = 0x02000000;

    /**
     * Enable support for matching invalid UTF
     */
    static final int MATCH_INVALID_UTF = 0x04000000;

    // Extra compile options available via pcre2_set_compile_extra_options()

    /**
     * Allow surrogate escape sequences in UTF-8 and UTF-32 modes
     */
    static final int EXTRA_ALLOW_SURROGATE_ESCAPES = 0x00000001;

    /**
     * Treat unrecognized escape sequences as literals
     */
    static final int EXTRA_BAD_ESCAPE_IS_LITERAL = 0x00000002;

    /**
     * Pattern matches whole words only (automatic word boundary assertions)
     */
    static final int EXTRA_MATCH_WORD = 0x00000004;

    /**
     * Pattern matches whole lines only (automatic line anchoring)
     */
    static final int EXTRA_MATCH_LINE = 0x00000008;

    /**
     * Interpret ⧵r as ⧵n in patterns
     */
    static final int EXTRA_ESCAPED_CR_IS_LF = 0x00000010;

    /**
     * Extend {@link #ALT_BSUX} to support ⧵u{hhh..} (ECMAScript 6)
     */
    static final int EXTRA_ALT_BSUX = 0x00000020;

    /**
     * Allow ⧵K in lookaround assertions
     */
    static final int EXTRA_ALLOW_LOOKAROUND_BSK = 0x00000040;

    /**
     * Restrict caseless matching to not cross ASCII/non-ASCII boundary
     */
    static final int EXTRA_CASELESS_RESTRICT = 0x00000080;

    /**
     * Force ⧵d to match ASCII digits only, overriding UCP
     */
    static final int EXTRA_ASCII_BSD = 0x00000100;

    /**
     * Force ⧵s to match ASCII space characters only, overriding UCP
     */
    static final int EXTRA_ASCII_BSS = 0x00000200;

    /**
     * Force ⧵w to match ASCII word characters only, overriding UCP
     */
    static final int EXTRA_ASCII_BSW = 0x00000400;

    /**
     * Force POSIX character classes to match ASCII characters only, overriding UCP
     */
    static final int EXTRA_ASCII_POSIX = 0x00000800;

    /**
     * Force POSIX digit classes to match ASCII digits only, overriding UCP
     */
    static final int EXTRA_ASCII_DIGIT = 0x00001000;

    // Option bits for pcre2_jit_compile()

    /**
     * Compile code for full matching
     */
    static final int JIT_COMPLETE = 0x00000001;

    /**
     * Compile code for soft partial matching
     */
    static final int JIT_PARTIAL_SOFT = 0x00000002;

    /**
     * Compile code for hard partial matching
     */
    static final int JIT_PARTIAL_HARD = 0x00000004;

    /**
     * @deprecated Use {@link #MATCH_INVALID_UTF}
     */
    @Deprecated
    static final int JIT_INVALID_UTF = 0x00000100;

    // Option bits for pcre2_match(), pcre2_dfa_match(), pcre2_jit_match(), and pcre2_substitute()

    /**
     * Subject string is not the beginning of a line
     */
    static final int NOTBOL = 0x00000001;

    /**
     * Subject string is not the end of a line
     */
    static final int NOTEOL = 0x00000002;

    /**
     * An empty string is not a valid match
     */
    static final int NOTEMPTY = 0x00000004;

    /**
     * An empty string at the start of the subject is not a valid match
     */
    static final int NOTEMPTY_ATSTART = 0x00000008;

    /**
     * Return {@link IPcre2#ERROR_PARTIAL} for a partial match even if there is a full match
     */
    static final int PARTIAL_SOFT = 0x00000010;

    /**
     * Return {@link IPcre2#ERROR_PARTIAL} for a partial match if no full matches are found
     */
    static final int PARTIAL_HARD = 0x00000020;
    /**
     * Restart DFA matching after a partial match
     */
    static final int DFA_RESTART = 0x00000040;

    /**
     * Return only the shortest match in DFA matching
     */
    static final int DFA_SHORTEST = 0x00000080;

    /**
     * Replace all occurrences, not just the first
     */
    static final int SUBSTITUTE_GLOBAL = 0x00000100;

    /**
     * Enable extended replacement string processing
     */
    static final int SUBSTITUTE_EXTENDED = 0x00000200;

    /**
     * Treat unset capture groups as empty strings in substitutions
     */
    static final int SUBSTITUTE_UNSET_EMPTY = 0x00000400;

    /**
     * Treat unknown capture groups as unset in substitutions
     */
    static final int SUBSTITUTE_UNKNOWN_UNSET = 0x00000800;

    /**
     * If output buffer overflows, compute the needed length
     */
    static final int SUBSTITUTE_OVERFLOW_LENGTH = 0x00001000;

    /**
     * Do not use JIT matching
     */
    static final int NO_JIT = 0x00002000;

    /**
     * On success, make a private subject copy
     */
    static final int COPY_MATCHED_SUBJECT = 0x00004000;

    /**
     * Treat the replacement string as literal text
     */
    static final int SUBSTITUTE_LITERAL = 0x00008000;

    /**
     * Use pre-existing match data for the first substitution match
     */
    static final int SUBSTITUTE_MATCHED = 0x00010000;

    /**
     * Return only the replacement string(s), not the whole subject
     */
    static final int SUBSTITUTE_REPLACEMENT_ONLY = 0x00020000;

    /**
     * Disable the recursion loop check during matching
     */
    static final int DISABLE_RECURSELOOP_CHECK = 0x00040000;

    // Option bits for pcre2_pattern_convert()

    /**
     * Treat the input pattern as a UTF string for pattern conversion
     */
    static final int CONVERT_UTF = 0x00000001;

    /**
     * Skip UTF validity check on the input pattern for pattern conversion
     */
    static final int CONVERT_NO_UTF_CHECK = 0x00000002;

    /**
     * Convert a POSIX basic regular expression
     */
    static final int CONVERT_POSIX_BASIC = 0x00000004;

    /**
     * Convert a POSIX extended regular expression
     */
    static final int CONVERT_POSIX_EXTENDED = 0x00000008;

    /**
     * Convert a glob pattern (wildcards do not match separator)
     */
    static final int CONVERT_GLOB = 0x00000010;

    /**
     * Convert a glob pattern (wildcards may match separator)
     */
    static final int CONVERT_GLOB_NO_WILD_SEPARATOR = 0x00000030;

    /**
     * Convert a glob pattern (double-star feature disabled)
     */
    static final int CONVERT_GLOB_NO_STARSTAR = 0x00000050;

    // Newline and \R settings, for use in compile contexts

    /**
     * Carriage return only (\r)
     */
    static final int NEWLINE_CR = 1;

    /**
     * Linefeed only (\n)
     */
    static final int NEWLINE_LF = 2;

    /**
     * CR followed by LF only (\r\n)
     */
    static final int NEWLINE_CRLF = 3;

    /**
     * Any Unicode newline sequence
     */
    static final int NEWLINE_ANY = 4;

    /**
     * Any of {@link #NEWLINE_CR}, {@link #NEWLINE_LF}, or {@link #NEWLINE_CRLF}
     */
    static final int NEWLINE_ANYCRLF = 5;

    /**
     * NUL character (\0)
     */
    static final int NEWLINE_NUL = 6;

    /**
     * \R corresponds to the Unicode line endings
     */
    static final int BSR_UNICODE = 1;

    /**
     * \R corresponds to CR, LF, and CRLF only
     */
    static final int BSR_ANYCRLF = 2;

    // Error codes: compile-time errors (positive values)

    /**
     * Compile error: ⧵ at end of pattern
     */
    static final int ERROR_END_BACKSLASH = 101;

    /**
     * Compile error: ⧵c at end of pattern
     */
    static final int ERROR_END_BACKSLASH_C = 102;

    /**
     * Compile error: unrecognized character follows ⧵
     */
    static final int ERROR_UNKNOWN_ESCAPE = 103;

    /**
     * Compile error: numbers out of order in {} quantifier
     */
    static final int ERROR_QUANTIFIER_OUT_OF_ORDER = 104;

    /**
     * Compile error: number too big in {} quantifier
     */
    static final int ERROR_QUANTIFIER_TOO_BIG = 105;

    /**
     * Compile error: missing terminating ] for character class
     */
    static final int ERROR_MISSING_SQUARE_BRACKET = 106;

    /**
     * Compile error: escape sequence is invalid in character class
     */
    static final int ERROR_ESCAPE_INVALID_IN_CLASS = 107;

    /**
     * Compile error: range out of order in character class
     */
    static final int ERROR_CLASS_RANGE_ORDER = 108;

    /**
     * Compile error: quantifier does not follow a repeatable item
     */
    static final int ERROR_QUANTIFIER_INVALID = 109;

    /**
     * Compile error: internal error: unexpected repeat
     */
    static final int ERROR_INTERNAL_UNEXPECTED_REPEAT = 110;

    /**
     * Compile error: unrecognized character after (? or (?-
     */
    static final int ERROR_INVALID_AFTER_PARENS_QUERY = 111;

    /**
     * Compile error: POSIX named classes are supported only within a class
     */
    static final int ERROR_POSIX_CLASS_NOT_IN_CLASS = 112;

    /**
     * Compile error: POSIX collating elements are not supported
     */
    static final int ERROR_POSIX_NO_SUPPORT_COLLATING = 113;

    /**
     * Compile error: missing closing parenthesis
     */
    static final int ERROR_MISSING_CLOSING_PARENTHESIS = 114;

    /**
     * Compile error: reference to non-existent subpattern
     */
    static final int ERROR_BAD_SUBPATTERN_REFERENCE = 115;

    /**
     * Compile error: pattern passed as NULL with non-zero length
     */
    static final int ERROR_NULL_PATTERN = 116;

    /**
     * Compile error: unrecognised compile-time option bit(s)
     */
    static final int ERROR_BAD_OPTIONS = 117;

    /**
     * Compile error: missing ) after (?# comment
     */
    static final int ERROR_MISSING_COMMENT_CLOSING = 118;

    /**
     * Compile error: parentheses are too deeply nested
     */
    static final int ERROR_PARENTHESES_NEST_TOO_DEEP = 119;

    /**
     * Compile error: regular expression is too large
     */
    static final int ERROR_PATTERN_TOO_LARGE = 120;

    /**
     * Compile error: failed to allocate heap memory
     */
    static final int ERROR_HEAP_FAILED = 121;

    /**
     * Compile error: unmatched closing parenthesis
     */
    static final int ERROR_UNMATCHED_CLOSING_PARENTHESIS = 122;

    /**
     * Compile error: internal error: code overflow
     */
    static final int ERROR_INTERNAL_CODE_OVERFLOW = 123;

    /**
     * Compile error: missing closing parenthesis for condition
     */
    static final int ERROR_MISSING_CONDITION_CLOSING = 124;

    /**
     * Compile error: length of lookbehind assertion is not limited
     */
    static final int ERROR_LOOKBEHIND_NOT_FIXED_LENGTH = 125;

    /**
     * Compile error: a relative value of zero is not allowed
     */
    static final int ERROR_ZERO_RELATIVE_REFERENCE = 126;

    /**
     * Compile error: conditional subpattern contains more than two branches
     */
    static final int ERROR_TOO_MANY_CONDITION_BRANCHES = 127;

    /**
     * Compile error: atomic assertion expected after (?( or (?(?C)
     */
    static final int ERROR_CONDITION_ASSERTION_EXPECTED = 128;

    /**
     * Compile error: digit expected after (?+
     */
    static final int ERROR_BAD_RELATIVE_REFERENCE = 129;

    /**
     * Compile error: unknown POSIX class name
     */
    static final int ERROR_UNKNOWN_POSIX_CLASS = 130;

    /**
     * Compile error: internal error in pcre2_study(): should not occur
     */
    static final int ERROR_INTERNAL_STUDY_ERROR = 131;

    /**
     * Compile error: this version of PCRE2 does not have Unicode support
     */
    static final int ERROR_UNICODE_NOT_SUPPORTED = 132;

    /**
     * Compile error: parentheses are too deeply nested (stack check)
     */
    static final int ERROR_PARENTHESES_STACK_CHECK = 133;

    /**
     * Compile error: character code point value in ⧵x{} or ⧵o{} is too large
     */
    static final int ERROR_CODE_POINT_TOO_BIG = 134;

    /**
     * Compile error: lookbehind is too complicated
     */
    static final int ERROR_LOOKBEHIND_TOO_COMPLICATED = 135;

    /**
     * Compile error: ⧵C is not allowed in a lookbehind assertion in UTF mode
     */
    static final int ERROR_LOOKBEHIND_INVALID_BACKSLASH_C = 136;

    /**
     * Compile error: PCRE2 does not support ⧵F, ⧵L, ⧵l, ⧵N{name}, ⧵U, or ⧵u
     */
    static final int ERROR_UNSUPPORTED_ESCAPE_SEQUENCE = 137;

    /**
     * Compile error: number after (?C is greater than 255
     */
    static final int ERROR_CALLOUT_NUMBER_TOO_BIG = 138;

    /**
     * Compile error: closing parenthesis for (?C expected
     */
    static final int ERROR_MISSING_CALLOUT_CLOSING = 139;

    /**
     * Compile error: invalid escape sequence in (*VERB) name
     */
    static final int ERROR_ESCAPE_INVALID_IN_VERB = 140;

    /**
     * Compile error: unrecognized character after (?P
     */
    static final int ERROR_UNRECOGNIZED_AFTER_QUERY_P = 141;

    /**
     * Compile error: syntax error in subpattern name (missing terminator?)
     */
    static final int ERROR_MISSING_NAME_TERMINATOR = 142;

    /**
     * Compile error: two named subpatterns have the same name
     */
    static final int ERROR_DUPLICATE_SUBPATTERN_NAME = 143;

    /**
     * Compile error: subpattern name must start with a non-digit
     */
    static final int ERROR_INVALID_SUBPATTERN_NAME = 144;

    /**
     * Compile error: this version of PCRE2 does not have support for ⧵P, ⧵p, or ⧵X
     */
    static final int ERROR_UNICODE_PROPERTIES_UNAVAILABLE = 145;

    /**
     * Compile error: malformed ⧵P or ⧵p sequence
     */
    static final int ERROR_MALFORMED_UNICODE_PROPERTY = 146;

    /**
     * Compile error: unknown property after ⧵P or ⧵p
     */
    static final int ERROR_UNKNOWN_UNICODE_PROPERTY = 147;

    /**
     * Compile error: subpattern name is too long
     */
    static final int ERROR_SUBPATTERN_NAME_TOO_LONG = 148;

    /**
     * Compile error: too many named subpatterns
     */
    static final int ERROR_TOO_MANY_NAMED_SUBPATTERNS = 149;

    /**
     * Compile error: invalid range in character class
     */
    static final int ERROR_CLASS_INVALID_RANGE = 150;

    /**
     * Compile error: octal value is greater than ⧵377 in 8-bit non-UTF-8 mode
     */
    static final int ERROR_OCTAL_BYTE_TOO_BIG = 151;

    /**
     * Compile error: internal error: overran compiling workspace
     */
    static final int ERROR_INTERNAL_OVERRAN_WORKSPACE = 152;

    /**
     * Compile error: internal error: previously-checked referenced subpattern not found
     */
    static final int ERROR_INTERNAL_MISSING_SUBPATTERN = 153;

    /**
     * Compile error: DEFINE subpattern contains more than one branch
     */
    static final int ERROR_DEFINE_TOO_MANY_BRANCHES = 154;

    /**
     * Compile error: missing opening brace after ⧵o
     */
    static final int ERROR_BACKSLASH_O_MISSING_BRACE = 155;

    /**
     * Compile error: internal error: unknown newline setting
     */
    static final int ERROR_INTERNAL_UNKNOWN_NEWLINE = 156;

    /**
     * Compile error: ⧵g is not followed by a braced, angle-bracketed, or quoted name/number
     */
    static final int ERROR_BACKSLASH_G_SYNTAX = 157;

    /**
     * Compile error: (?R (recursive pattern call) must be followed by a closing parenthesis
     */
    static final int ERROR_PARENS_QUERY_R_MISSING_CLOSING = 158;

    /**
     * @deprecated Obsolete error (should not occur)
     */
    @Deprecated
    static final int ERROR_VERB_ARGUMENT_NOT_ALLOWED = 159;

    /**
     * Compile error: (*VERB) not recognized or malformed
     */
    static final int ERROR_VERB_UNKNOWN = 160;

    /**
     * Compile error: subpattern number is too big
     */
    static final int ERROR_SUBPATTERN_NUMBER_TOO_BIG = 161;

    /**
     * Compile error: subpattern name expected
     */
    static final int ERROR_SUBPATTERN_NAME_EXPECTED = 162;

    /**
     * Compile error: internal error: parsed pattern overflow
     */
    static final int ERROR_INTERNAL_PARSED_OVERFLOW = 163;

    /**
     * Compile error: non-octal character in ⧵o{} (closing brace missing?)
     */
    static final int ERROR_INVALID_OCTAL = 164;

    /**
     * Compile error: different names for subpatterns of the same number are not allowed
     */
    static final int ERROR_SUBPATTERN_NAMES_MISMATCH = 165;

    /**
     * Compile error: (*MARK) must have an argument
     */
    static final int ERROR_MARK_MISSING_ARGUMENT = 166;

    /**
     * Compile error: non-hex character in ⧵x{} (closing brace missing?)
     */
    static final int ERROR_INVALID_HEXADECIMAL = 167;

    /**
     * Compile error: ⧵c must be followed by a printable ASCII character
     */
    static final int ERROR_BACKSLASH_C_SYNTAX = 168;

    /**
     * Compile error: ⧵k is not followed by a braced, angle-bracketed, or quoted name
     */
    static final int ERROR_BACKSLASH_K_SYNTAX = 169;

    /**
     * Compile error: internal error: unknown meta code in check_lookbehinds()
     */
    static final int ERROR_INTERNAL_BAD_CODE_LOOKBEHINDS = 170;

    /**
     * Compile error: ⧵N is not supported in a class
     */
    static final int ERROR_BACKSLASH_N_IN_CLASS = 171;

    /**
     * Compile error: callout string is too long
     */
    static final int ERROR_CALLOUT_STRING_TOO_LONG = 172;

    /**
     * Compile error: disallowed Unicode code point (&gt;= 0xd800 and &lt;= 0xdfff)
     */
    static final int ERROR_UNICODE_DISALLOWED_CODE_POINT = 173;

    /**
     * Compile error: using UTF is disabled by the application
     */
    static final int ERROR_UTF_IS_DISABLED = 174;

    /**
     * Compile error: using UCP is disabled by the application
     */
    static final int ERROR_UCP_IS_DISABLED = 175;

    /**
     * Compile error: name is too long in (*MARK), (*PRUNE), (*SKIP), or (*THEN)
     */
    static final int ERROR_VERB_NAME_TOO_LONG = 176;

    /**
     * Compile error: character code point value in ⧵u.... sequence is too large
     */
    static final int ERROR_BACKSLASH_U_CODE_POINT_TOO_BIG = 177;

    /**
     * Compile error: digits missing in ⧵x{}, ⧵o{}, or ⧵N{U+}
     */
    static final int ERROR_MISSING_OCTAL_OR_HEX_DIGITS = 178;

    /**
     * Compile error: syntax error or number too big in (?(VERSION condition
     */
    static final int ERROR_VERSION_CONDITION_SYNTAX = 179;

    /**
     * Compile error: internal error: unknown opcode in auto_possessify()
     */
    static final int ERROR_INTERNAL_BAD_CODE_AUTO_POSSESS = 180;

    /**
     * Compile error: missing terminating delimiter for callout with string argument
     */
    static final int ERROR_CALLOUT_NO_STRING_DELIMITER = 181;

    /**
     * Compile error: unrecognized string delimiter follows (?C
     */
    static final int ERROR_CALLOUT_BAD_STRING_DELIMITER = 182;

    /**
     * Compile error: using ⧵C is disabled by the application
     */
    static final int ERROR_BACKSLASH_C_CALLER_DISABLED = 183;

    /**
     * Compile error: (?| and/or (?J: or (?x: parentheses are too deeply nested
     */
    static final int ERROR_QUERY_BARJX_NEST_TOO_DEEP = 184;

    /**
     * Compile error: using ⧵C is disabled in this PCRE2 library
     */
    static final int ERROR_BACKSLASH_C_LIBRARY_DISABLED = 185;

    /**
     * Compile error: regular expression is too complicated
     */
    static final int ERROR_PATTERN_TOO_COMPLICATED = 186;

    /**
     * Compile error: lookbehind assertion is too long
     */
    static final int ERROR_LOOKBEHIND_TOO_LONG = 187;

    /**
     * Compile error: pattern string is longer than the limit set by the application
     */
    static final int ERROR_PATTERN_STRING_TOO_LONG = 188;

    /**
     * Compile error: internal error: unknown code in parsed pattern
     */
    static final int ERROR_INTERNAL_BAD_CODE = 189;

    /**
     * Compile error: internal error: bad code value in parsed_skip()
     */
    static final int ERROR_INTERNAL_BAD_CODE_IN_SKIP = 190;

    /**
     * Compile error: PCRE2_EXTRA_ALLOW_SURROGATE_ESCAPES is not allowed in UTF-16 mode
     */
    static final int ERROR_NO_SURROGATES_IN_UTF16 = 191;

    /**
     * Compile error: invalid option bits with PCRE2_LITERAL
     */
    static final int ERROR_BAD_LITERAL_OPTIONS = 192;

    /**
     * Compile error: ⧵N{U+dddd} is supported only in Unicode (UTF) mode
     */
    static final int ERROR_SUPPORTED_ONLY_IN_UNICODE = 193;

    /**
     * Compile error: invalid hyphen in option setting
     */
    static final int ERROR_INVALID_HYPHEN_IN_OPTIONS = 194;

    /**
     * Compile error: (*alpha_assertion) not recognized
     */
    static final int ERROR_ALPHA_ASSERTION_UNKNOWN = 195;

    /**
     * Compile error: script runs require Unicode support
     */
    static final int ERROR_SCRIPT_RUN_NOT_AVAILABLE = 196;

    /**
     * Compile error: too many capturing groups (maximum 65535)
     */
    static final int ERROR_TOO_MANY_CAPTURES = 197;

    /**
     * Compile error: assertion expected after (?( or (?(?C)
     */
    static final int ERROR_CONDITION_ATOMIC_ASSERTION_EXPECTED = 198;

    /**
     * Compile error: ⧵K is not allowed in lookarounds
     */
    static final int ERROR_BACKSLASH_K_IN_LOOKAROUND = 199;

    // Error codes: "expected" matching errors

    /**
     * Match error: no match was found
     */
    static final int ERROR_NOMATCH = -1;

    /**
     * Match error: partial match
     */
    static final int ERROR_PARTIAL = -2;

    // Error codes: UTF-8 validity check errors

    /**
     * UTF-8 error: 1 byte missing at end
     */
    static final int ERROR_UTF8_ERR1 = -3;

    /**
     * UTF-8 error: 2 bytes missing at end
     */
    static final int ERROR_UTF8_ERR2 = -4;

    /**
     * UTF-8 error: 3 bytes missing at end
     */
    static final int ERROR_UTF8_ERR3 = -5;

    /**
     * UTF-8 error: 4 bytes missing at end
     */
    static final int ERROR_UTF8_ERR4 = -6;

    /**
     * UTF-8 error: 5 bytes missing at end
     */
    static final int ERROR_UTF8_ERR5 = -7;

    /**
     * UTF-8 error: byte 2 top bits not 0x80
     */
    static final int ERROR_UTF8_ERR6 = -8;

    /**
     * UTF-8 error: byte 3 top bits not 0x80
     */
    static final int ERROR_UTF8_ERR7 = -9;

    /**
     * UTF-8 error: byte 4 top bits not 0x80
     */
    static final int ERROR_UTF8_ERR8 = -10;

    /**
     * UTF-8 error: byte 5 top bits not 0x80
     */
    static final int ERROR_UTF8_ERR9 = -11;

    /**
     * UTF-8 error: byte 6 top bits not 0x80
     */
    static final int ERROR_UTF8_ERR10 = -12;

    /**
     * UTF-8 error: 5-byte character is not allowed (RFC 3629)
     */
    static final int ERROR_UTF8_ERR11 = -13;

    /**
     * UTF-8 error: 6-byte character is not allowed (RFC 3629)
     */
    static final int ERROR_UTF8_ERR12 = -14;

    /**
     * UTF-8 error: code point greater than 0x10ffff
     */
    static final int ERROR_UTF8_ERR13 = -15;

    /**
     * UTF-8 error: code point in surrogate range (0xd800-0xdfff)
     */
    static final int ERROR_UTF8_ERR14 = -16;

    /**
     * UTF-8 error: overlong 2-byte sequence
     */
    static final int ERROR_UTF8_ERR15 = -17;

    /**
     * UTF-8 error: overlong 3-byte sequence
     */
    static final int ERROR_UTF8_ERR16 = -18;

    /**
     * UTF-8 error: overlong 4-byte sequence
     */
    static final int ERROR_UTF8_ERR17 = -19;

    /**
     * UTF-8 error: overlong 5-byte sequence
     */
    static final int ERROR_UTF8_ERR18 = -20;

    /**
     * UTF-8 error: overlong 6-byte sequence
     */
    static final int ERROR_UTF8_ERR19 = -21;

    /**
     * UTF-8 error: isolated byte with 0x80 bit set
     */
    static final int ERROR_UTF8_ERR20 = -22;

    /**
     * UTF-8 error: illegal byte (0xfe or 0xff)
     */
    static final int ERROR_UTF8_ERR21 = -23;

    // Error codes: UTF-16 validity check errors

    /**
     * UTF-16 error: missing low surrogate at end of string
     */
    static final int ERROR_UTF16_ERR1 = -24;

    /**
     * UTF-16 error: invalid low surrogate follows high surrogate
     */
    static final int ERROR_UTF16_ERR2 = -25;

    /**
     * UTF-16 error: isolated low surrogate
     */
    static final int ERROR_UTF16_ERR3 = -26;

    // Error codes: UTF-32 validity check errors

    /**
     * UTF-32 error: code point in surrogate range (0xd800-0xdfff)
     */
    static final int ERROR_UTF32_ERR1 = -27;

    /**
     * UTF-32 error: code point greater than 0x10ffff
     */
    static final int ERROR_UTF32_ERR2 = -28;

    // Error codes: miscellaneous errors for pcre2[_dfa]_match(), pcre2_substitute(), and serialization

    /**
     * Match error: bad data value
     */
    static final int ERROR_BADDATA = -29;

    /**
     * Match error: patterns do not all use the same character tables
     */
    static final int ERROR_MIXEDTABLES = -30;

    /**
     * Match error: magic number missing (pattern may be corrupt)
     */
    static final int ERROR_BADMAGIC = -31;

    /**
     * Match error: pattern compiled in wrong mode (8/16/32-bit error)
     */
    static final int ERROR_BADMODE = -32;

    /**
     * Match error: bad offset value
     */
    static final int ERROR_BADOFFSET = -33;

    /**
     * Match error: bad option value
     */
    static final int ERROR_BADOPTION = -34;

    /**
     * Match error: invalid replacement string
     */
    static final int ERROR_BADREPLACEMENT = -35;

    /**
     * Match error: bad offset into UTF string
     */
    static final int ERROR_BADUTFOFFSET = -36;

    /**
     * Match error: callout error code
     */
    static final int ERROR_CALLOUT = -37;

    /**
     * Match error: invalid data in workspace for DFA restart
     */
    static final int ERROR_DFA_BADRESTART = -38;

    /**
     * Match error: too much recursion for DFA matching
     */
    static final int ERROR_DFA_RECURSE = -39;

    /**
     * Match error: backreference condition or recursion test not supported for DFA
     */
    static final int ERROR_DFA_UCOND = -40;

    /**
     * Match error: function is not supported for DFA matching
     */
    static final int ERROR_DFA_UFUNC = -41;

    /**
     * Match error: pattern contains an item not supported for DFA matching
     */
    static final int ERROR_DFA_UITEM = -42;

    /**
     * Match error: workspace size exceeded in DFA matching
     */
    static final int ERROR_DFA_WSSIZE = -43;

    /**
     * Match error: internal error (pattern overwritten?)
     */
    static final int ERROR_INTERNAL = -44;

    /**
     * Match error: bad JIT option
     */
    static final int ERROR_JIT_BADOPTION = -45;

    /**
     * Match error: JIT stack limit reached
     */
    static final int ERROR_JIT_STACKLIMIT = -46;

    /**
     * Match error: match limit exceeded
     */
    static final int ERROR_MATCHLIMIT = -47;

    /**
     * Match error: no more memory available
     */
    static final int ERROR_NOMEMORY = -48;

    /**
     * Match error: unknown substring
     */
    static final int ERROR_NOSUBSTRING = -49;

    /**
     * Match error: non-unique substring name
     */
    static final int ERROR_NOUNIQUESUBSTRING = -50;

    /**
     * Match error: NULL argument passed with non-zero length
     */
    static final int ERROR_NULL = -51;

    /**
     * Match error: nested recursion at the same subject position
     */
    static final int ERROR_RECURSELOOP = -52;

    /**
     * Match error: matching depth limit exceeded
     */
    static final int ERROR_DEPTHLIMIT = -53;

    /**
     * @deprecated Use {@link #ERROR_DEPTHLIMIT}
     */
    @Deprecated
    static final int ERROR_RECURSIONLIMIT = -53;

    /**
     * Match error: requested value is not available
     */
    static final int ERROR_UNAVAILABLE = -54;

    /**
     * Match error: requested value is not set
     */
    static final int ERROR_UNSET = -55;

    /**
     * Match error: offset limit set without {@link #USE_OFFSET_LIMIT}
     */
    static final int ERROR_BADOFFSETLIMIT = -56;

    /**
     * Match error: bad escape sequence in replacement string
     */
    static final int ERROR_BADREPESCAPE = -57;

    /**
     * Match error: expected closing curly bracket in replacement string
     */
    static final int ERROR_REPMISSINGBRACE = -58;

    /**
     * Match error: bad substitution in replacement string
     */
    static final int ERROR_BADSUBSTITUTION = -59;

    /**
     * Match error: match with end before start or start moved backwards is not supported
     */
    static final int ERROR_BADSUBSPATTERN = -60;

    /**
     * Match error: too many replacements (more than INT_MAX)
     */
    static final int ERROR_TOOMANYREPLACE = -61;

    /**
     * Match error: bad serialized data
     */
    static final int ERROR_BADSERIALIZEDDATA = -62;

    /**
     * Match error: heap limit exceeded
     */
    static final int ERROR_HEAPLIMIT = -63;

    /**
     * Match error: invalid syntax in pattern conversion
     */
    static final int ERROR_CONVERT_SYNTAX = -64;

    /**
     * Match error: internal error: duplicate substitution match
     */
    static final int ERROR_INTERNAL_DUPMATCH = -65;

    /**
     * Match error: {@link #MATCH_INVALID_UTF} is not supported for DFA matching
     */
    static final int ERROR_DFA_UINVALID_UTF = -66;

    /**
     * Match error: internal error: invalid substring offset
     */
    static final int ERROR_INVALIDOFFSET = -67;

    // Request types for pcre2_pattern_info()

    /**
     * Final options after compiling
     */
    static final int INFO_ALLOPTIONS = 0;

    /**
     * Options passed to {@link #compile}
     */
    static final int INFO_ARGOPTIONS = 1;

    /**
     * Number of highest backreference
     */
    static final int INFO_BACKREFMAX = 2;

    /**
     * What \R matches:
     * PCRE2_BSR_UNICODE: Unicode line endings
     * PCRE2_BSR_ANYCRLF: CR, LF, or CRLF only
     */
    static final int INFO_BSR = 3;

    /**
     * Number of capturing subpatterns
     */
    static final int INFO_CAPTURECOUNT = 4;

    /**
     * First code unit when type is 1
     */
    static final int INFO_FIRSTCODEUNIT = 5;

    /**
     * Type of start-of-match information
     * 0 nothing set
     * 1 first code unit is set
     * 2 start of string or after newline
     */
    static final int INFO_FIRSTCODETYPE = 6;

    /**
     * Bitmap of first code units, or 0
     */
    static final int INFO_FIRSTBITMAP = 7;

    /**
     * Return 1 if explicit CR or LF matches exist in the pattern
     */
    static final int INFO_HASCRORLF = 8;

    /**
     * Return 1 if (?J) or (?-J) was used
     */
    static final int INFO_JCHANGED = 9;

    /**
     * Size of JIT compiled code, or 0
     */
    static final int INFO_JITSIZE = 10;

    /**
     * Last code unit when type is 1
     */
    static final int INFO_LASTCODEUNIT = 11;

    /**
     * Type of must-be-present information
     * 0 nothing set
     * 1 code unit is set
     */
    static final int INFO_LASTCODETYPE = 12;

    /**
     * 1 if the pattern can match an empty string, 0 otherwise
     */
    static final int INFO_MATCHEMPTY = 13;

    /**
     * Match limit if set, otherwise {@link #ERROR_UNSET}
     */
    static final int INFO_MATCHLIMIT = 14;

    /**
     * Length (in characters) of the longest lookbehind assertion
     */
    static final int INFO_MAXLOOKBEHIND = 15;

    /**
     * Lower bound length of matching strings
     */
    static final int INFO_MINLENGTH = 16;

    /**
     * Number of named subpatterns
     */
    static final int INFO_NAMECOUNT = 17;

    /**
     * Size of name table entries
     */
    static final int INFO_NAMEENTRYSIZE = 18;

    /**
     * Pointer to name table
     */
    static final int INFO_NAMETABLE = 19;

    /**
     * Code for the newline sequence:
     * {@link #NEWLINE_CR}
     * {@link #NEWLINE_LF}
     * {@link #NEWLINE_CRLF}
     * {@link #NEWLINE_ANY}
     * {@link #NEWLINE_ANYCRLF}
     * {@link #NEWLINE_NUL}
     */
    static final int INFO_NEWLINE = 20;

    /**
     * Backtracking depth limit if set, otherwise {@link #ERROR_UNSET}
     */
    static final int INFO_DEPTHLIMIT = 21;

    /**
     * Obsolete synonym for {@link #INFO_DEPTHLIMIT}
     */
    @Deprecated
    static final int INFO_RECURSIONLIMIT = 21;

    /**
     * Size of compiled pattern
     */
    static final int INFO_SIZE = 22;

    /**
     * Return 1 if pattern contains \C
     */
    static final int INFO_HASBACKSLASHC = 23;

    /**
     * Size of backtracking frame
     */
    static final int INFO_FRAMESIZE = 24;

    /**
     * Heap memory limit if set, otherwise {@link #ERROR_UNSET}
     */
    static final int INFO_HEAPLIMIT = 25;

    /**
     * Extra options that were passed in the compile context
     */
    static final int INFO_EXTRAOPTIONS = 26;

    // Request types for pcre2_config()

    /**
     * Query the default backslash-R (⧵R) convention
     */
    static final int CONFIG_BSR = 0;

    /**
     * Query JIT compilation availability (1 = available, 0 = not available)
     */
    static final int CONFIG_JIT = 1;

    /**
     * Query the target architecture string for the JIT compiler
     */
    static final int CONFIG_JITTARGET = 2;

    /**
     * Query the number of bytes used for internal linkage in compiled patterns
     */
    static final int CONFIG_LINKSIZE = 3;

    /**
     * Query the default match limit
     */
    static final int CONFIG_MATCHLIMIT = 4;

    /**
     * Query the default newline convention
     */
    static final int CONFIG_NEWLINE = 5;

    /**
     * Query the default parentheses nesting limit
     */
    static final int CONFIG_PARENSLIMIT = 6;

    /**
     * Query the default backtracking depth limit
     */
    static final int CONFIG_DEPTHLIMIT = 7;

    /**
     * @deprecated Use {@link #CONFIG_DEPTHLIMIT}
     */
    @Deprecated
    static final int CONFIG_RECURSIONLIMIT = 7;

    /**
     * @deprecated Obsolete, always returns 0
     */
    @Deprecated
    static final int CONFIG_STACKRECURSE = 8;

    /**
     * Query Unicode support availability (1 = available, 0 = not available)
     */
    static final int CONFIG_UNICODE = 9;

    /**
     * Query the Unicode version string
     */
    static final int CONFIG_UNICODE_VERSION = 10;

    /**
     * Query the PCRE2 version string
     */
    static final int CONFIG_VERSION = 11;

    /**
     * Query the default heap memory limit in kibibytes
     */
    static final int CONFIG_HEAPLIMIT = 12;

    /**
     * Query whether ⧵C is permanently disabled (1 = disabled, 0 = enabled)
     */
    static final int CONFIG_NEVER_BACKSLASH_C = 13;

    /**
     * Query a bitmask of compiled code unit widths (8-bit, 16-bit, 32-bit)
     */
    static final int CONFIG_COMPILED_WIDTHS = 14;

    /**
     * Query the length of PCRE2's character processing tables in bytes
     */
    static final int CONFIG_TABLES_LENGTH = 15;

    /**
     * Get the amount of memory needed to store the information referred to by {@code what} about the optional features
     * of the PCRE2 library.
     * <p>
     * Suitable for any information except:
     * {@link #CONFIG_JITTARGET} Target architecture for the JIT compiler
     * {@link #CONFIG_UNICODE_VERSION} Unicode version
     * {@link #CONFIG_VERSION} PCRE2 version
     *
     * @param what the information to query the memory requirements for
     * @return the amount of memory needed to store the information referred to by {@code what}.
     */
    int config(int what);

    /**
     * Get the information referred to by {@code what} about the optional features of the PCRE2 library.
     * <p>
     * Suitable for any information except:
     * {@link #CONFIG_JITTARGET} Target architecture for the JIT compiler
     * {@link #CONFIG_UNICODE_VERSION} Unicode version
     * {@link #CONFIG_VERSION} PCRE2 version
     *
     * @param what  the information to query
     * @param where the array to store the information
     * @return Non-negative value on success, otherwise a negative error code.
     */
    int config(int what, int[] where);

    /**
     * Get the information referred to by {@code what} about the optional features of the PCRE2 library.
     * <p>
     * Suitable only for the following information:
     * {@link #CONFIG_JITTARGET} Target architecture for the JIT compiler
     * {@link #CONFIG_UNICODE_VERSION} Unicode version
     * {@link #CONFIG_VERSION} PCRE2 version
     *
     * @param what  the information to query
     * @param where a buffer to store the information
     * @return Non-negative value on success, otherwise a negative error code.
     */
    int config(int what, ByteBuffer where);

    /**
     * Create a new general context.
     *
     * @param privateMalloc the private malloc function or 0 to use the system function
     * @param privateFree   the private free function or 0 to use the system function
     * @param memoryData    the memory data to pass to the private malloc and free functions
     * @return the general context handle
     */
    long generalContextCreate(long privateMalloc, long privateFree, long memoryData);

    /**
     * Create a copy of a general context.
     *
     * @param gcontext the general context handle to copy
     * @return the new general context handle
     */
    long generalContextCopy(long gcontext);

    /**
     * Free a general context.
     *
     * @param gcontext the general context handle
     */
    void generalContextFree(long gcontext);

    /**
     * Build character tables in the current locale.
     * <p>
     * This function builds a set of character tables for character values less than 256. These can be used to support
     * a locale that is different from the default. When pcre2_compile() is called with a compile context that contains
     * a pointer to character tables, the tables are used for pattern compilation.
     * <p>
     * The character tables are built using the current locale. The functions isprint(), isupper(), islower(),
     * isalnum(), isalpha(), iscntrl(), isdigit(), isgraph(), ispunct(), isspace(), isxdigit() and tolower() are used.
     * <p>
     * The memory for the tables is obtained via the general context if one is provided, or via malloc() otherwise.
     * The memory should be freed by calling {@link #maketablesFree} when the tables are no longer needed.
     *
     * @param gcontext the general context handle for memory allocation, or 0 to use default
     * @return a pointer to the character tables, or 0 if memory allocation fails
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_maketables.html">pcre2_maketables</a>
     */
    long maketables(long gcontext);

    /**
     * Free character tables that were obtained from {@link #maketables}.
     * <p>
     * This function frees the memory that was used to hold the character tables. If the tables argument is 0 (null),
     * the function returns immediately without doing anything.
     * <p>
     * The general context must be the same as the one that was used when pcre2_maketables() was called (or 0 if
     * that was 0). Otherwise the behaviour is undefined.
     *
     * @param gcontext the general context handle that was used for allocation, or 0 if default was used
     * @param tables   the pointer to the character tables to free (may be 0, in which case the function does nothing)
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_maketables_free.html">pcre2_maketables_free</a>
     */
    void maketablesFree(long gcontext, long tables);

    /**
     * Create a new compile context.
     *
     * @param gcontext the general context handle or 0
     * @return the compile context handle
     */
    long compileContextCreate(long gcontext);

    /**
     * Create a copy of a compile context.
     *
     * @param ccontext the compile context handle to copy
     * @return the new compile context handle
     */
    long compileContextCopy(long ccontext);

    /**
     * Free a compile context.
     *
     * @param ccontext the compile context handle
     */
    void compileContextFree(long ccontext);

    /**
     * Compile a regular expression pattern.
     *
     * @param pattern     the pattern to compile
     * @param options     a combination of the compile options
     * @param errorcode   an array to store the error code
     * @param erroroffset an array to store the error offset
     * @param ccontext    a compile context handle or 0
     * @return a compiled pattern handle
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_compile.html">pcre2_compile</a>
     */
    long compile(String pattern, int options, int[] errorcode, long[] erroroffset, long ccontext);

    /**
     * Create a copy of a compiled pattern.
     * <p>
     * This function makes a copy of the memory used for a compiled pattern, excluding any memory used by the JIT
     * compiler. Without a subsequent call to {@link #jitCompile(long, int)}, the copy can be used only for
     * non-JIT matching.
     *
     * @param code the compiled pattern handle
     * @return the new compiled pattern handle, or 0 if the input is 0 or memory allocation fails
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_code_copy.html">pcre2_code_copy</a>
     */
    long codeCopy(long code);

    /**
     * Create a copy of a compiled pattern and its character tables.
     * <p>
     * This function makes a copy of the memory used for a compiled pattern, including any character tables that were
     * passed to {@link #compile(String, int, int[], long[], long)} via a compile context. Without a subsequent call to
     * {@link #jitCompile(long, int)}, the copy can be used only for non-JIT matching.
     * <p>
     * Unlike {@link #codeCopy(long)}, which makes a copy that references the same character tables as the original,
     * this function creates a completely independent copy. If the original was compiled without external tables (i.e.,
     * {@link #setCharacterTables(long, long)} was not called), the copy will also not have external tables.
     *
     * @param code the compiled pattern handle
     * @return the new compiled pattern handle, or 0 if the input is 0 or memory allocation fails
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_code_copy_with_tables.html">pcre2_code_copy_with_tables</a>
     */
    long codeCopyWithTables(long code);

    /**
     * Free a compiled pattern resources.
     *
     * @param code the compiled pattern handle
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_code_free.html">pcre2_code_free</a>
     */
    void codeFree(long code);

    /**
     * Enumerate callouts in a compiled pattern.
     * <p>
     * This function scans a compiled pattern and calls the callback function for each callout in the pattern.
     * The callback receives a pointer to a callout enumeration block containing information about the callout,
     * and a user-supplied data pointer. The callback should return zero to continue enumeration; returning
     * any other value stops the enumeration and that value becomes the function's return value.
     *
     * @param code        the compiled pattern handle
     * @param callback    a callback function handle
     * @param calloutData a value to be passed to the callback function
     * @return 0 for successful completion, or a non-zero value if the callback returns non-zero or an error occurs
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_callout_enumerate.html">pcre2_callout_enumerate</a>
     */
    int calloutEnumerate(long code, long callback, long calloutData);

    /**
     * Get the error message for the given error code.
     *
     * @param errorcode the error code
     * @param buffer    the buffer to store the error message
     * @return the length of the error message or {@link #ERROR_NOMEMORY} if the buffer is too small and if
     * {@code errorcode} is not a valid error code, {@link #ERROR_BADDATA} is returned
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_get_error_message.html">pcre2_get_error_message</a>
     */
    int getErrorMessage(int errorcode, ByteBuffer buffer);

    /**
     * Retrieve size of the information about a compiled pattern.
     *
     * @param code the compiled pattern handle
     * @param what the information to retrieve
     * @return Size of the information, otherwise an error code:
     * {@link #ERROR_NULL} the argument code is 0
     * {@link #ERROR_BADMAGIC} the "magic number" was not found
     * {@link #ERROR_BADOPTION} the value of {@code what} is invalid
     * {@link #ERROR_BADMODE} the pattern was compiled in the wrong mode
     * {@link #ERROR_UNSET} the requested information is not set
     */
    int patternInfo(long code, int what);

    /**
     * Retrieve the information about a compiled pattern as Integer.
     * <p>
     * Suitable for any information except:
     * {@link #INFO_FIRSTBITMAP}
     * {@link #INFO_NAMETABLE}
     * May be suitable for the following information:
     * {@link #INFO_JITSIZE}
     * {@link #INFO_SIZE}
     * {@link #INFO_FRAMESIZE}
     *
     * @param code  the compiled pattern handle
     * @param what  the information to retrieve
     * @param where the array to store the information
     * @return Zero on success, otherwise an error code:
     * {@link #ERROR_NULL} the argument code is 0
     * {@link #ERROR_BADMAGIC} the "magic number" was not found
     * {@link #ERROR_BADOPTION} the value of {@code what} is invalid
     * {@link #ERROR_BADMODE} the pattern was compiled in the wrong mode
     * {@link #ERROR_UNSET} the requested information is not set
     */
    int patternInfo(long code, int what, int[] where);

    /**
     * Retrieve the information about a compiled pattern as Long.
     * <p>
     * May be suitable for the following information:
     * {@link #INFO_JITSIZE}
     * {@link #INFO_SIZE}
     * {@link #INFO_FRAMESIZE}
     *
     * @param code  the compiled pattern handle
     * @param what  the information to retrieve
     * @param where the array to store the information
     * @return Zero on success, otherwise an error code:
     * {@link #ERROR_NULL} the argument code is 0
     * {@link #ERROR_BADMAGIC} the "magic number" was not found
     * {@link #ERROR_BADOPTION} the value of {@code what} is invalid
     * {@link #ERROR_BADMODE} the pattern was compiled in the wrong mode
     * {@link #ERROR_UNSET} the requested information is not set
     */
    int patternInfo(long code, int what, long[] where);

    /**
     * Retrieve the information about a compiled pattern as byte buffer.
     * <p>
     * Only suitable for the following information:
     * {@link #INFO_NAMETABLE}
     *
     * @param code  the compiled pattern handle
     * @param what  the information to retrieve
     * @param where the buffer to store the information
     * @return Zero on success, otherwise an error code:
     * {@link #ERROR_NULL} the argument code is 0
     * {@link #ERROR_BADMAGIC} the "magic number" was not found
     * {@link #ERROR_BADOPTION} the value of {@code what} is invalid
     * {@link #ERROR_BADMODE} the pattern was compiled in the wrong mode
     * {@link #ERROR_UNSET} the requested information is not set
     */
    int patternInfo(long code, int what, ByteBuffer where);

    /**
     * JIT-compile a compiled pattern.
     *
     * @param code    the compiled pattern handle
     * @param options option bits
     * @return 0 on success, otherwise a negative error code
     */
    int jitCompile(long code, int options);

    /**
     * Match a compiled pattern against a subject string.
     *
     * @param code        the compiled pattern handle
     * @param subject     the subject string
     * @param startoffset the starting offset in the subject string
     * @param options     option bits
     * @param matchData   the match data handle
     * @param mcontext    the match context handle
     * @return the number of captures plus one, zero if the {@code matchData} is too small, or a negative value if there
     * was no match or an actual error occurred
     */
    int jitMatch(long code, String subject, int startoffset, int options, long matchData, long mcontext);

    /**
     * Create a JIT stack.
     *
     * @param startsize the initial stack size
     * @param maxsize   the maximum stack size
     * @param gcontext  the general context handle or 0
     * @return the JIT stack handle
     */
    long jitStackCreate(long startsize, long maxsize, long gcontext);

    /**
     * Free a JIT stack.
     *
     * @param jitStack the JIT stack handle
     */
    void jitStackFree(long jitStack);

    /**
     * Assign the JIT stack to a match context.
     *
     * @param mcontext the match context handle
     * @param callback a callback function handle or 0
     * @param data     a JIT stack handle or a value to be passed to the callback function
     */
    void jitStackAssign(long mcontext, long callback, long data);

    /**
     * Free unused JIT executable memory.
     * <p>
     * When JIT compilation is enabled, PCRE2 allocates executable memory for the JIT-compiled code. This memory
     * is normally held until the compiled patterns are freed. However, the JIT compiler may also allocate some
     * additional memory that is no longer needed after compilation. This function releases that unused memory.
     * <p>
     * In a multithreaded application, this function should be called in a thread-safe manner. It is safe to call
     * this function even if JIT support is not available (it will simply do nothing).
     * <p>
     * If a general context is provided, it must be the same context that was used when creating the JIT-compiled
     * patterns, as it may have custom memory management functions.
     *
     * @param gcontext the general context handle that was used for JIT compilation, or 0 if default was used
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_jit_free_unused_memory.html">pcre2_jit_free_unused_memory</a>
     */
    void jitFreeUnusedMemory(long gcontext);

    /**
     * Create a new match data block.
     *
     * @param ovecsize the size of the ovector
     * @param gcontext the general context handle or 0
     * @return the match data handle
     */
    long matchDataCreate(int ovecsize, long gcontext);

    /**
     * Create a new match data block from a compiled pattern.
     *
     * @param code     the compiled pattern handle
     * @param gcontext the general context handle or 0
     * @return the match data handle
     */
    long matchDataCreateFromPattern(long code, long gcontext);

    /**
     * Free a match data block.
     *
     * @param matchData the match data handle
     */
    void matchDataFree(long matchData);

    /**
     * Create a new match context.
     *
     * @param gcontext the general context handle or 0
     * @return the match context handle
     */
    long matchContextCreate(long gcontext);

    /**
     * Create a copy of a match context.
     *
     * @param mcontext the match context handle to copy
     * @return the new match context handle
     */
    long matchContextCopy(long mcontext);

    /**
     * Free a match context.
     *
     * @param mcontext the match context handle
     */
    void matchContextFree(long mcontext);

    /**
     * Create a new convert context.
     * <p>
     * A convert context is used to hold parameters for the pattern conversion functions
     * {@code pcre2_pattern_convert()} which can convert glob patterns or POSIX patterns
     * to PCRE2 regular expressions.
     *
     * @param gcontext the general context handle or 0 to use default memory management
     * @return the convert context handle, or 0 if memory allocation fails
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_convert_context_create.html">pcre2_convert_context_create</a>
     */
    long convertContextCreate(long gcontext);

    /**
     * Create a copy of a convert context.
     *
     * @param cvcontext the convert context handle to copy
     * @return the new convert context handle, or 0 if the input is 0 or memory allocation fails
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_convert_context_copy.html">pcre2_convert_context_copy</a>
     */
    long convertContextCopy(long cvcontext);

    /**
     * Free a convert context.
     * <p>
     * If the argument is 0 (null pointer), the function returns immediately without doing anything.
     *
     * @param cvcontext the convert context handle (may be 0, in which case the function does nothing)
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_convert_context_free.html">pcre2_convert_context_free</a>
     */
    void convertContextFree(long cvcontext);

    /**
     * Set the escape character for glob pattern conversion.
     * <p>
     * This is part of the experimental pattern conversion functions. It sets the escape character that is recognized
     * during glob pattern conversion. The escape character allows special glob characters to be treated as literals.
     * <p>
     * The default escape character is the grave accent (`) on Windows systems and the backslash (\) on other platforms.
     * Setting the escape character to zero disables escape processing entirely.
     * <p>
     * The escape character must be zero (to disable) or a punctuation character with a code point less than 256.
     *
     * @param cvcontext  the convert context handle
     * @param escapeChar the escape character to use, or 0 to disable escape processing
     * @return 0 on success, or {@link #ERROR_BADDATA} if the escape character is invalid
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_glob_escape.html">pcre2_set_glob_escape</a>
     */
    int setGlobEscape(long cvcontext, int escapeChar);

    /**
     * Set the path separator character for glob pattern conversion.
     * <p>
     * This is part of the experimental pattern conversion functions. It sets the component separator character
     * that is used during glob pattern conversion. This affects how path-like patterns are parsed.
     * <p>
     * The separator character must be one of forward slash (/), backslash (\), or dot (.). On Windows systems,
     * backslash is the default separator; on other platforms, forward slash is the default.
     *
     * @param cvcontext     the convert context handle
     * @param separatorChar the separator character to use (must be '/', '\\', or '.')
     * @return 0 on success, or {@link #ERROR_BADDATA} if the separator character is invalid
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_glob_separator.html">pcre2_set_glob_separator</a>
     */
    int setGlobSeparator(long cvcontext, int separatorChar);

    /**
     * Convert a foreign pattern (glob or POSIX) to a PCRE2 regular expression.
     * <p>
     * This experimental function converts glob patterns or POSIX regular expressions into PCRE2 patterns.
     * The conversion is useful for tools that need to accept multiple pattern syntaxes.
     * <p>
     * The {@code options} parameter specifies what type of pattern is being converted:
     * <ul>
     * <li>{@link #CONVERT_POSIX_BASIC} - Convert POSIX Basic Regular Expression</li>
     * <li>{@link #CONVERT_POSIX_EXTENDED} - Convert POSIX Extended Regular Expression</li>
     * <li>{@link #CONVERT_GLOB} - Convert glob pattern</li>
     * <li>{@link #CONVERT_GLOB_NO_WILD_SEPARATOR} - Glob with no wildcard for separator</li>
     * <li>{@link #CONVERT_GLOB_NO_STARSTAR} - Glob without ** support</li>
     * </ul>
     * <p>
     * Additionally, {@link #CONVERT_UTF} can be set to indicate UTF encoding, and
     * {@link #CONVERT_NO_UTF_CHECK} can be set to skip UTF validity checking.
     * <p>
     * When {@code buffer} contains 0, the function returns only the required buffer length without
     * performing the conversion. When {@code buffer} contains a non-zero pointer, that pointer must
     * point to a buffer of sufficient size, with the size specified in {@code blength}.
     * <p>
     * When this function allocates memory (buffer initially contains 0 and is updated with a pointer),
     * the memory must be freed using {@link #convertedPatternFree(long)}.
     *
     * @param pattern   the foreign pattern to convert (UTF-8 encoded)
     * @param options   conversion options specifying the pattern type and behavior
     * @param buffer    an array of length 1; on input, contains 0 to request allocation or a pointer to
     *                  a caller-provided buffer; on output, may contain a pointer to the converted pattern
     * @param blength   an array of length 1; on input when using a caller-provided buffer, contains the buffer
     *                  size; on output, contains the length of the converted pattern (excluding null terminator)
     * @param cvcontext a convert context handle for additional options, or 0 to use defaults
     * @return 0 on success, otherwise a negative error code:
     *         {@link #ERROR_NOMEMORY} if memory allocation failed,
     *         {@link #ERROR_BADOPTION} if invalid options were specified,
     *         {@link #ERROR_CONVERT_SYNTAX} if the pattern has invalid syntax
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_pattern_convert.html">pcre2_pattern_convert</a>
     */
    int patternConvert(String pattern, int options, long[] buffer, long[] blength, long cvcontext);

    /**
     * Free memory allocated by {@link #patternConvert(String, int, long[], long[], long)}.
     * <p>
     * This function frees memory that was allocated by {@code pcre2_pattern_convert()} when it created
     * a converted pattern. If the argument is 0 (null pointer), the function returns immediately
     * without doing anything.
     * <p>
     * Note: Only call this function on pointers that were allocated by {@code pcre2_pattern_convert()}.
     * Do not call it on buffers that were provided by the caller.
     *
     * @param convertedPattern the pointer to the converted pattern to free (may be 0, in which case the
     *                         function does nothing)
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_converted_pattern_free.html">pcre2_converted_pattern_free</a>
     */
    void convertedPatternFree(long convertedPattern);

    /**
     * Read a converted pattern from native memory as a Java String.
     * <p>
     * This method reads a UTF-8 encoded string from the native memory pointer returned by
     * {@link #patternConvert(String, int, long[], long[], long)}. The pointer and length should be
     * obtained from the {@code buffer} and {@code blength} output parameters of that method.
     *
     * @param convertedPattern the pointer to the converted pattern (as returned in the buffer parameter
     *                         of {@link #patternConvert(String, int, long[], long[], long)})
     * @param length           the length of the converted pattern in bytes (as returned in the blength
     *                         parameter of {@link #patternConvert(String, int, long[], long[], long)})
     * @return the converted pattern as a Java String
     */
    String readConvertedPattern(long convertedPattern, long length);

    /**
     * Match a compiled pattern against a subject string.
     *
     * @param code        the compiled pattern handle
     * @param subject     the subject string
     * @param startoffset the starting offset in the subject string
     * @param options     option bits
     * @param matchData   the match data handle
     * @param mcontext    the match context handle
     * @return the number of captures plus one, zero if the {@code matchData} is too small, or a negative value if there
     * was no match or an actual error occurred
     */
    int match(long code, String subject, int startoffset, int options, long matchData, long mcontext);

    /**
     * Match a compiled pattern against a subject string using the alternative DFA matching algorithm.
     * <p>
     * DFA (Deterministic Finite Automaton) matching finds all possible matches at a given point in the subject string.
     * This is useful for lexers and tokenizers. Note that DFA matching is not Perl-compatible.
     * <p>
     * Unlike the standard {@link #match} function, DFA matching requires a workspace array for internal use.
     * The workspace must be an array of integers, and its size should be at least 20 elements for simple patterns,
     * though more complex patterns may require larger workspaces.
     * <p>
     * Important differences from standard matching:
     * <ul>
     *   <li>The output vector contains matched strings in reverse order (longest first)</li>
     *   <li>Capturing parentheses are not supported (only the overall match is returned)</li>
     *   <li>The {@link #DFA_RESTART} option can be used to continue after a partial match</li>
     *   <li>The {@link #DFA_SHORTEST} option can be used to return only the shortest match</li>
     * </ul>
     *
     * @param code        the compiled pattern handle
     * @param subject     the subject string
     * @param startoffset the starting offset in the subject string
     * @param options     option bits (may include {@link #DFA_RESTART}, {@link #DFA_SHORTEST},
     *                    {@link #PARTIAL_SOFT}, {@link #PARTIAL_HARD})
     * @param matchData   the match data handle
     * @param mcontext    the match context handle (may be 0)
     * @param workspace   an array of integers used as working space by the matching algorithm
     * @param wscount     the number of elements in the workspace array
     * @return the number of matched substrings, zero if the output vector is too small,
     *         or a negative error code:
     *         {@link #ERROR_NOMATCH} if no match was found,
     *         {@link #ERROR_DFA_WSSIZE} if the workspace is too small,
     *         {@link #ERROR_DFA_RECURSE} if recursion is used in the pattern,
     *         {@link #ERROR_DFA_UCOND} if an unsupported condition is used,
     *         {@link #ERROR_DFA_UFUNC} if an unsupported function is used,
     *         {@link #ERROR_DFA_UITEM} if an unsupported pattern item is encountered,
     *         {@link #ERROR_DFA_BADRESTART} if {@link #DFA_RESTART} is used incorrectly
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_dfa_match.html">pcre2_dfa_match</a>
     */
    int dfaMatch(
            long code,
            String subject,
            int startoffset,
            int options,
            long matchData,
            long mcontext,
            int[] workspace,
            int wscount
    );

    /**
     * Get number of the offset pairs in the output vector of the match data
     *
     * @param matchData the match data handle
     * @return the number of the offset pairs
     */
    int getOvectorCount(long matchData);

    /**
     * Get the size of a match data block in bytes.
     * <p>
     * This function returns the size of the match data block that was obtained by a call to
     * {@code pcre2_match_data_create()} or {@code pcre2_match_data_create_from_pattern()}.
     * This is the size of the opaque data block.
     *
     * @param matchData the match data handle
     * @return the size of the match data block in bytes
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_get_match_data_size.html">pcre2_get_match_data_size</a>
     */
    long getMatchDataSize(long matchData);

    /**
     * Get the output vector of the match data
     *
     * @param matchData the match data handle
     * @param ovector   the array to store the output vector
     */
    void getOvector(long matchData, long[] ovector);

    /**
     * Get the starting character offset from a match.
     * <p>
     * After a successful match, this function returns the code unit offset of the character at which the successful
     * match started. For non-partial matches, this may differ from {@code ovector[0]} if the pattern uses the
     * {@code \K} escape sequence, which resets the start of the matched string.
     * <p>
     * After a partial match, this value is always the same as {@code ovector[0]} because {@code \K} does not affect
     * the result of a partial match.
     *
     * @param matchData the match data handle from a successful match
     * @return the code unit offset of the character at which the match started
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_get_startchar.html">pcre2_get_startchar</a>
     */
    long getStartchar(long matchData);

    /**
     * Get the last (*MARK), (*PRUNE), or (*THEN) name that was encountered during matching.
     * <p>
     * After a successful match, a partial match (error code {@code PCRE2_ERROR_PARTIAL}), or a failure to match
     * (error code {@code PCRE2_ERROR_NOMATCH}), this function returns the name from the last encountered
     * {@code (*MARK)}, {@code (*PRUNE)}, or {@code (*THEN)} item in the pattern. The name is a zero-terminated string.
     * <p>
     * If no mark name was set during the match, or if the pattern does not contain any mark items, this function
     * returns 0 (NULL pointer).
     * <p>
     * For a successful match, the returned name is the last mark encountered on the matching path. For a failed match,
     * the returned name is the last mark passed on the main matching path before the overall match failure.
     *
     * @param matchData the match data handle from a match operation
     * @return the address of a zero-terminated string containing the mark name, or 0 if no mark name is available
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_get_mark.html">pcre2_get_mark</a>
     */
    long getMark(long matchData);

    /**
     * Set the newline convention within a compile context
     *
     * @param ccontext the compile context handle
     * @param newline  the newline convention
     * @return 0 on success, otherwise a negative error code
     */
    int setNewline(long ccontext, int newline);

    /**
     * Set what \R matches within a compile context.
     * <p>
     * The value must be {@link #BSR_UNICODE} (to match any Unicode line ending) or {@link #BSR_ANYCRLF}
     * (to match only CR, LF, or CRLF).
     *
     * @param ccontext the compile context handle
     * @param value    the BSR value ({@link #BSR_UNICODE} or {@link #BSR_ANYCRLF})
     * @return 0 on success, otherwise a negative error code
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_bsr.html">pcre2_set_bsr</a>
     */
    int setBsr(long ccontext, int value);

    /**
     * Set the parentheses nesting limit within a compile context.
     * <p>
     * This limit is used to prevent patterns with excessive parentheses nesting from consuming
     * too many resources during compilation. The default limit is 250, but this can be changed
     * at build time.
     *
     * @param ccontext the compile context handle
     * @param limit    the maximum depth of nested parentheses allowed in a pattern
     * @return 0 always
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_parens_nest_limit.html">pcre2_set_parens_nest_limit</a>
     */
    int setParensNestLimit(long ccontext, int limit);

    /**
     * Set the maximum length of pattern that can be compiled.
     * <p>
     * This function sets the maximum length (in code units) of the pattern string that can be
     * passed to {@code pcre2_compile()}. If a pattern longer than this limit is passed, the
     * compile function will immediately return an error.
     * <p>
     * By default, there is no limit (the value is the maximum that a PCRE2_SIZE variable can hold).
     * This function can be used to set a lower limit for security purposes, to prevent excessively
     * long patterns from being processed.
     *
     * @param ccontext the compile context handle
     * @param length   the maximum pattern length in code units
     * @return 0 always
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_max_pattern_length.html">pcre2_set_max_pattern_length</a>
     */
    int setMaxPatternLength(long ccontext, long length);

    /**
     * Set additional compile options within a compile context.
     * <p>
     * This function sets additional option bits for {@code pcre2_compile()} that are housed in a compile context.
     * It completely replaces all the bits. The extra options are:
     * <ul>
     * <li>{@link #EXTRA_ALLOW_SURROGATE_ESCAPES} - Allow surrogate escapes in UTF-8 mode</li>
     * <li>{@link #EXTRA_BAD_ESCAPE_IS_LITERAL} - Treat unrecognized escapes as literal</li>
     * <li>{@link #EXTRA_MATCH_WORD} - Pattern matches whole words</li>
     * <li>{@link #EXTRA_MATCH_LINE} - Pattern matches whole lines</li>
     * <li>{@link #EXTRA_ESCAPED_CR_IS_LF} - Interpret escaped CR as LF</li>
     * <li>{@link #EXTRA_ALT_BSUX} - Extended alternate handling of &#92;u, &#92;U, and &#92;x</li>
     * <li>{@link #EXTRA_ALLOW_LOOKAROUND_BSK} - Allow \K in lookaround assertions</li>
     * <li>{@link #EXTRA_CASELESS_RESTRICT} - Restrict caseless matching to same-script</li>
     * <li>{@link #EXTRA_ASCII_BSD} - Use ASCII for \d in Unicode mode</li>
     * <li>{@link #EXTRA_ASCII_BSS} - Use ASCII for \s in Unicode mode</li>
     * <li>{@link #EXTRA_ASCII_BSW} - Use ASCII for \w in Unicode mode</li>
     * <li>{@link #EXTRA_ASCII_POSIX} - Use ASCII for POSIX classes in Unicode mode</li>
     * <li>{@link #EXTRA_ASCII_DIGIT} - Use ASCII for \d (alias for EXTRA_ASCII_BSD)</li>
     * </ul>
     *
     * @param ccontext     the compile context handle
     * @param extraOptions the extra compile options bit flags
     * @return 0 always
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_compile_extra_options.html">pcre2_set_compile_extra_options</a>
     */
    int setCompileExtraOptions(long ccontext, int extraOptions);

    /**
     * Set custom character tables for pattern compilation within a compile context.
     * <p>
     * This function sets a pointer to custom character tables within a compile context. The tables can be
     * generated using {@link #maketables} or the {@code pcre2_dftables} maintenance command.
     * <p>
     * When {@link #compile} is called with a compile context that contains a pointer to character tables,
     * those tables are used for pattern compilation instead of the default tables built into PCRE2.
     * <p>
     * Passing 0 as the tables pointer causes the compile context to use the default character tables.
     *
     * @param ccontext the compile context handle
     * @param tables   a pointer to character tables (from {@link #maketables}), or 0 to use default tables
     * @return 0 always
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_character_tables.html">pcre2_set_character_tables</a>
     */
    int setCharacterTables(long ccontext, long tables);

    /**
     * Set a compile recursion guard function within a compile context.
     * <p>
     * This function registers a guard callback that is called during {@link #compile} whenever it starts to
     * compile a parenthesized part of a pattern. The guard function can be used to check for available stack
     * space and prevent stack overflow crashes during compilation of deeply nested patterns.
     * <p>
     * The guard callback signature is: {@code int (*)(uint32_t, void *)}.
     * <ul>
     * <li>The first argument is the current parenthesis nesting depth.</li>
     * <li>The second argument is the user data pointer passed to this function.</li>
     * <li>The callback should return zero to allow compilation to continue, or non-zero to abort with
     *     {@link #ERROR_RECURSELOOP}.</li>
     * </ul>
     * <p>
     * Passing 0 as the guard function removes any previously set guard.
     *
     * @param ccontext      the compile context handle
     * @param guardFunction a pointer to the guard callback function, or 0 to remove the guard
     * @param userData      a pointer to user data that will be passed to the guard callback
     * @return 0 always
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_compile_recursion_guard.html">pcre2_set_compile_recursion_guard</a>
     */
    int setCompileRecursionGuard(long ccontext, long guardFunction, long userData);

    /**
     * Set the match limit within a match context.
     * <p>
     * The match limit is used to limit the amount of backtracking during a match.
     * If the limit is reached, the match attempt fails with {@link #ERROR_MATCHLIMIT}.
     *
     * @param mcontext the match context handle
     * @param limit    the match limit value
     * @return 0 always
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_match_limit.html">pcre2_set_match_limit</a>
     */
    int setMatchLimit(long mcontext, int limit);

    /**
     * Set the backtracking depth limit within a match context.
     * <p>
     * The depth limit is used to limit the amount of backtracking depth during a match.
     * If the limit is reached, the match attempt fails with {@link #ERROR_DEPTHLIMIT}.
     *
     * @param mcontext the match context handle
     * @param limit    the depth limit value
     * @return 0 always
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_depth_limit.html">pcre2_set_depth_limit</a>
     */
    int setDepthLimit(long mcontext, int limit);

    /**
     * Set the heap memory limit within a match context.
     * <p>
     * The heap limit is used to limit the amount of heap memory used during a match.
     * If the limit is reached, the match attempt fails with {@link #ERROR_HEAPLIMIT}.
     *
     * @param mcontext the match context handle
     * @param limit    the heap limit value in kibibytes (1024 bytes)
     * @return 0 always
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_heap_limit.html">pcre2_set_heap_limit</a>
     */
    int setHeapLimit(long mcontext, int limit);

    /**
     * Set the offset limit within a match context.
     * <p>
     * The offset limit sets a limit on how far into the subject the start of a match can be.
     * The pattern must be compiled with the {@link #USE_OFFSET_LIMIT} option for this to take effect.
     *
     * @param mcontext the match context handle
     * @param limit    the offset limit value
     * @return 0 always
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_offset_limit.html">pcre2_set_offset_limit</a>
     */
    int setOffsetLimit(long mcontext, long limit);

    /**
     * Set up a callout function within a match context.
     * <p>
     * This function sets a callout function for the match context. The callout function is called during matching
     * whenever a callout point is reached in the pattern. Callout points are either automatically generated
     * (when compiled with {@link #AUTO_CALLOUT}) or explicitly placed in the pattern using (?C) or (?Cn) syntax.
     * <p>
     * The callout function receives a pointer to a callout block structure containing information about the
     * current match state, and a user-supplied data pointer. The function should return zero to continue
     * matching, or a non-zero value to abort the match with that value as the match result.
     * <p>
     * Passing 0 as the callback disables callouts.
     *
     * @param mcontext    the match context handle
     * @param callback    a callback function handle, or 0 to disable callouts
     * @param calloutData a value to be passed to the callback function
     * @return 0 always
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_callout.html">pcre2_set_callout</a>
     */
    int setCallout(long mcontext, long callback, long calloutData);

    /**
     * Match a compiled pattern against a subject string and perform substitution.
     *
     * @param code          the compiled pattern handle
     * @param subject       the subject string
     * @param startoffset   the starting offset in the subject string
     * @param options       option bits
     * @param matchData     the match data handle or 0
     * @param mcontext      the match context handle or 0
     * @param replacement   the replacement string
     * @param outputbuffer  the buffer to store the result
     * @param outputlength  an array of length 1 to receive the output length (in bytes); on input, should contain
     *                      the buffer size
     * @return the number of substitutions made, or a negative error code
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_substitute.html">pcre2_substitute</a>
     */
    int substitute(
            long code,
            String subject,
            int startoffset,
            int options,
            long matchData,
            long mcontext,
            String replacement,
            ByteBuffer outputbuffer,
            long[] outputlength
    );

    /**
     * Extract a captured substring by its number into newly allocated memory.
     *
     * @param matchData the match data handle from a successful match
     * @param number    the group number (0 = entire match)
     * @param bufferptr an array of length 1 to receive the pointer to the allocated string
     * @param bufflen   an array of length 1 to receive the length of the string (in code units, excluding null)
     * @return zero on success, otherwise a negative error code:
     * {@link #ERROR_NOSUBSTRING} there are no groups of that number
     * {@link #ERROR_UNAVAILABLE} the ovector was too small for that group
     * {@link #ERROR_UNSET} the group did not participate in the match
     * {@link #ERROR_NOMEMORY} memory could not be obtained
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_substring_get_bynumber.html">pcre2_substring_get_bynumber</a>
     */
    int substringGetByNumber(long matchData, int number, long[] bufferptr, long[] bufflen);

    /**
     * Copy a captured substring by its number into a caller-provided buffer.
     * <p>
     * This is a zero-allocation alternative to {@link #substringGetByNumber} for performance-critical paths.
     * The caller provides the buffer, and the method copies the substring into it.
     *
     * @param matchData the match data handle from a successful match
     * @param number    the group number (0 = entire match)
     * @param buffer    a {@link ByteBuffer} to receive the extracted substring (must have sufficient capacity)
     * @param bufflen   an array of length 1; on input, contains the buffer size; on output, receives the actual
     *                  length of the extracted string (in code units, excluding the null terminator)
     * @return zero on success, otherwise a negative error code:
     * {@link #ERROR_NOSUBSTRING} there are no groups of that number
     * {@link #ERROR_UNAVAILABLE} the ovector was too small for that group
     * {@link #ERROR_UNSET} the group did not participate in the match
     * {@link #ERROR_NOMEMORY} the buffer is too small
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_substring_copy_bynumber.html">pcre2_substring_copy_bynumber</a>
     */
    int substringCopyByNumber(long matchData, int number, ByteBuffer buffer, long[] bufflen);

    /**
     * Extract a captured substring by its name into newly allocated memory.
     *
     * @param matchData the match data handle from a successful match
     * @param name      the name of the capturing group
     * @param bufferptr an array of length 1 to receive the pointer to the allocated string
     * @param bufflen   an array of length 1 to receive the length of the string (in code units, excluding null)
     * @return zero on success, otherwise a negative error code:
     * {@link #ERROR_NOSUBSTRING} there are no groups of that name
     * {@link #ERROR_UNAVAILABLE} the ovector was too small for that group
     * {@link #ERROR_UNSET} the group did not participate in the match
     * {@link #ERROR_NOMEMORY} memory could not be obtained
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_substring_get_byname.html">pcre2_substring_get_byname</a>
     */
    int substringGetByName(long matchData, String name, long[] bufferptr, long[] bufflen);

    /**
     * Copy a captured substring by its name into a caller-provided buffer.
     * <p>
     * This is a zero-allocation alternative to {@link #substringGetByName} for performance-critical paths.
     * The caller provides the buffer, and the method copies the substring into it.
     *
     * @param matchData the match data handle from a successful match
     * @param name      the name of the capturing group
     * @param buffer    a {@link ByteBuffer} to receive the extracted substring (must have sufficient capacity)
     * @param bufflen   an array of length 1; on input, contains the buffer size; on output, receives the actual
     *                  length of the extracted string (in code units, excluding the null terminator)
     * @return zero on success, otherwise a negative error code:
     * {@link #ERROR_NOSUBSTRING} there are no groups of that name
     * {@link #ERROR_UNAVAILABLE} the ovector was too small for that group
     * {@link #ERROR_UNSET} the group did not participate in the match
     * {@link #ERROR_NOMEMORY} the buffer is too small
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_substring_copy_byname.html">pcre2_substring_copy_byname</a>
     */
    int substringCopyByName(long matchData, String name, ByteBuffer buffer, long[] bufflen);

    /**
     * Get the length of a captured substring by its group name.
     * <p>
     * This allows querying substring length by name before allocation, enabling efficient buffer sizing for copy
     * operations. After a partial match, only substring 0 is available.
     *
     * @param matchData the match data handle from a successful match
     * @param name      the name of the capturing group
     * @param length    an array of length 1 to receive the length of the substring (in code units, excluding null),
     *                  or null if only checking whether the substring exists
     * @return zero on success, otherwise a negative error code:
     * {@link #ERROR_NOSUBSTRING} there are no groups of that name
     * {@link #ERROR_UNAVAILABLE} the ovector was too small for that group
     * {@link #ERROR_UNSET} the group did not participate in the match
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_substring_length_byname.html">pcre2_substring_length_byname</a>
     */
    int substringLengthByName(long matchData, String name, long[] length);

    /**
     * Get the length of a captured substring by its group number.
     * <p>
     * This allows querying substring length before allocation, enabling efficient buffer sizing for copy operations.
     * After a partial match, only substring 0 is available.
     *
     * @param matchData the match data handle from a successful match
     * @param number    the group number (0 = entire match)
     * @param length    an array of length 1 to receive the length of the substring (in code units, excluding null),
     *                  or null if only checking whether the substring exists
     * @return zero on success, otherwise a negative error code:
     * {@link #ERROR_NOSUBSTRING} there are no groups of that number
     * {@link #ERROR_UNAVAILABLE} the ovector was too small for that group
     * {@link #ERROR_UNSET} the group did not participate in the match
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_substring_length_bynumber.html">pcre2_substring_length_bynumber</a>
     */
    int substringLengthByNumber(long matchData, int number, long[] length);

    /**
     * Free memory that was allocated by {@link #substringGetByNumber} or {@link #substringGetByName}.
     *
     * @param buffer the pointer to the string to free (may be 0, in which case the function does nothing)
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_substring_free.html">pcre2_substring_free</a>
     */
    void substringFree(long buffer);

    /**
     * Extract all captured substrings into newly allocated memory.
     * <p>
     * This function extracts all captured substrings (including the full match at position 0) into a single block of
     * memory. This is more efficient than calling {@link #substringGetByNumber} repeatedly when multiple substrings
     * need to be extracted, as it reduces JNI/FFM call overhead.
     * <p>
     * The returned list pointer points to an array of string pointers, terminated by a NULL entry.
     * The lengths array (if requested) contains the length of each substring in code units, excluding null terminators.
     * <p>
     * The caller must free the returned memory using {@link #substringListFree} when done.
     *
     * @param matchData  the match data handle from a successful match
     * @param listptr    an array of length 1 to receive the pointer to the string list
     * @param lengthsptr an array of length 1 to receive the pointer to the lengths array, or null if not needed
     * @return zero on success, otherwise {@link #ERROR_NOMEMORY} if memory could not be obtained
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_substring_list_get.html">pcre2_substring_list_get</a>
     */
    int substringListGet(long matchData, long[] listptr, long[] lengthsptr);

    /**
     * Free memory that was allocated by {@link #substringListGet}.
     *
     * @param list the pointer to the string list to free (may be 0, in which case the function does nothing)
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_substring_list_free.html">pcre2_substring_list_free</a>
     */
    void substringListFree(long list);

    /**
     * Convert a named capturing group to its group number.
     *
     * @param code the compiled pattern handle
     * @param name the name of the capturing group
     * @return the group number on success, otherwise a negative error code:
     * {@link #ERROR_NOSUBSTRING} the name is not a valid capturing group name
     * {@link #ERROR_NOUNIQUESUBSTRING} the name is not unique (multiple groups with the same name when using
     *                                  the {@code (?J)} option)
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_substring_number_from_name.html">pcre2_substring_number_from_name</a>
     */
    int substringNumberFromName(long code, String name);

    /**
     * Find the first and last name table entries for a given capture group name.
     * <p>
     * This function locates entries in the name-to-number mapping table for named capture groups.
     * When duplicate names are allowed (via DUPNAMES option), a name may map to multiple group numbers.
     * This function returns pointers to the first and last table entries for the given name.
     * <p>
     * Each entry in the name table consists of a fixed-size record containing the group number followed by
     * the null-terminated name. The entry size can be obtained via {@link #patternInfo} with
     * {@link #INFO_NAMEENTRYSIZE}.
     *
     * @param code  the compiled pattern handle
     * @param name  the name of the capturing group to look up
     * @param first an array of length 1 to receive the pointer to the first matching entry,
     *              or null to just get a group number (returns any matching group number)
     * @param last  an array of length 1 to receive the pointer to the last matching entry,
     *              or null if not needed
     * @return On success: the length of each entry in code units (when first is not null),
     *         or a group number (when first is null and the name is found).
     *         On failure: {@link #ERROR_NOSUBSTRING} if the name is not found
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_substring_nametable_scan.html">pcre2_substring_nametable_scan</a>
     */
    int substringNametableScan(long code, String name, long[] first, long[] last);

    /**
     * Serialize one or more compiled patterns to a byte array.
     * <p>
     * This function serializes one or more compiled patterns into a contiguous block of memory
     * that can be saved to a file or other storage. The serialized data can later be restored
     * using {@code pcre2_serialize_decode()}.
     * <p>
     * The memory for the serialized data is obtained using the general context's memory
     * management functions (or {@code malloc()} if no context is provided). The caller
     * must free this memory using {@code pcre2_serialize_free()} when done.
     * <p>
     * <b>Important restrictions:</b>
     * <ul>
     *   <li>Serialized data is architecture-specific and cannot be transferred between
     *       machines with different characteristics (e.g., different byte order or pointer size)</li>
     *   <li>The same PCRE2 version must be used for encoding and decoding</li>
     *   <li>Patterns compiled with different character tables cannot be serialized together</li>
     * </ul>
     *
     * @param codes           an array of compiled pattern handles to serialize
     * @param numberOfCodes   the number of patterns to serialize (must be positive)
     * @param serializedBytes an array of length 1 to receive the pointer to the serialized data
     * @param serializedSize  an array of length 1 to receive the size of the serialized data in bytes
     * @param gcontext        the general context handle for memory allocation, or 0 to use default
     * @return the number of serialized patterns on success, otherwise a negative error code:
     *         {@link #ERROR_BADDATA} if {@code numberOfCodes} is zero or negative
     *         {@link #ERROR_BADMAGIC} if one of the patterns has an invalid magic number
     *         {@link #ERROR_NOMEMORY} if memory allocation failed
     *         {@link #ERROR_NULL} if any argument (except gcontext) is null
     *         {@link #ERROR_MIXEDTABLES} if patterns were compiled with different character tables
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_serialize_encode.html">pcre2_serialize_encode</a>
     */
    int serializeEncode(long[] codes, int numberOfCodes, long[] serializedBytes, long[] serializedSize,
            long gcontext);

    /**
     * Deserialize compiled patterns from a byte array.
     * <p>
     * This function decodes a serialized set of compiled patterns, recreating up to {@code numberOfCodes}
     * patterns from the serialized data. The decoded patterns are stored in the {@code codes} array.
     * <p>
     * The serialized data must have been created by {@link #serializeEncode} on a system with compatible
     * characteristics (same PCRE2 version, code unit width, byte order, and pointer size).
     * <p>
     * The memory for the decoded patterns is obtained using the general context's memory management
     * functions (or {@code malloc()} if no context is provided). Each decoded pattern must be freed
     * separately using {@link #codeFree}.
     * <p>
     * <b>Security warning:</b> The serialized data is only subject to simple consistency checking, not complete
     * validation. This function is intended for use with trusted data from within the same application. Do not
     * deserialize data from untrusted or external sources, as corrupted or malicious input may cause undefined
     * behavior, including reading beyond the end of the provided byte stream.
     *
     * @param codes          an array to receive the decoded compiled pattern handles
     * @param numberOfCodes  the number of slots available in the codes array (must be positive)
     * @param bytes          the serialized byte data (as obtained from {@link #serializeEncode})
     * @param gcontext       the general context handle for memory allocation, or 0 to use default
     * @return the number of decoded patterns on success, otherwise a negative error code:
     *         {@link #ERROR_BADDATA} if {@code numberOfCodes} is zero or less
     *         {@link #ERROR_BADMAGIC} if the data does not start with the correct bytes (possibly corrupted
     *                                 or from a different system endianness)
     *         {@link #ERROR_BADMODE} if the code unit size or PCRE2 version does not match
     *         {@link #ERROR_NOMEMORY} if memory allocation failed
     *         {@link #ERROR_NULL} if {@code codes} or {@code bytes} is null
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_serialize_decode.html">pcre2_serialize_decode</a>
     */
    int serializeDecode(long[] codes, int numberOfCodes, byte[] bytes, long gcontext);

    /**
     * Free memory that was allocated by {@link #serializeEncode} for holding a serialized byte stream.
     * <p>
     * This function deallocates the memory for a serialized set of compiled patterns. If the
     * argument is 0 (null pointer), the function returns without doing anything.
     *
     * @param bytes the pointer to the serialized byte stream to free (may be 0, in which case the function
     *              does nothing)
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_serialize_free.html">pcre2_serialize_free</a>
     */
    void serializeFree(long bytes);

    /**
     * Get the number of serialized patterns in a byte stream.
     * <p>
     * This function examines a serialized byte stream (created by {@link #serializeEncode}) and returns
     * the number of compiled patterns contained in it. This is useful when the number of patterns is not
     * known in advance, for example when loading serialized data from a file.
     * <p>
     * The function does not decode or otherwise process the patterns; it simply reads the count from the
     * header of the serialized data.
     *
     * @param bytes the serialized byte data (as obtained from {@link #serializeEncode})
     * @return the number of serialized patterns on success, otherwise a negative error code:
     *         {@link #ERROR_BADMAGIC} if the data does not start with the correct bytes (possibly corrupted
     *                                 or from a different system endianness)
     *         {@link #ERROR_BADMODE} if the code unit size or PCRE2 version does not match
     *         {@link #ERROR_NULL} if {@code bytes} is null
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_serialize_get_number_of_codes.html">pcre2_serialize_get_number_of_codes</a>
     */
    int serializeGetNumberOfCodes(byte[] bytes);

    /**
     * Create a native callback function pointer for a callout handler.
     * <p>
     * The returned handle represents a native function pointer that, when called by PCRE2 during matching,
     * reads the native {@code pcre2_callout_block} structure and delegates to the provided Java handler.
     * <p>
     * The handle must be freed with {@link #freeCalloutCallback(long)} when no longer needed.
     *
     * @param handler the Java callout handler to wrap
     * @return a handle representing the native callback, for use with {@link #setCallout(long, long, long)}
     * @throws IllegalArgumentException if handler is null
     */
    long createCalloutCallback(Pcre2CalloutHandler handler);

    /**
     * Free a native callback function pointer previously created by {@link #createCalloutCallback}.
     *
     * @param callbackHandle the handle returned by {@link #createCalloutCallback}
     */
    void freeCalloutCallback(long callbackHandle);

    /**
     * Create a native callback function pointer for a callout enumeration handler.
     * <p>
     * The returned handle represents a native function pointer that, when called by PCRE2 during
     * callout enumeration, reads the native {@code pcre2_callout_enumerate_block} structure and
     * delegates to the provided Java handler.
     * <p>
     * The handle must be freed with {@link #freeCalloutEnumerateCallback(long)} when no longer needed.
     *
     * @param handler the Java callout enumeration handler to wrap
     * @return a handle representing the native callback, for use with
     *         {@link #calloutEnumerate(long, long, long)}
     * @throws IllegalArgumentException if handler is null
     */
    long createCalloutEnumerateCallback(Pcre2CalloutEnumerateHandler handler);

    /**
     * Free a native callback function pointer previously created by
     * {@link #createCalloutEnumerateCallback}.
     *
     * @param callbackHandle the handle returned by {@link #createCalloutEnumerateCallback}
     */
    void freeCalloutEnumerateCallback(long callbackHandle);

}
