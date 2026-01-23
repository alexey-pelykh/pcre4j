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

    /**
     * Force pattern anchoring
     */
    public static final int ANCHORED = 0x80000000;

    /**
     * Do not check the pattern for UTF validity (only relevant if UTF is set)
     */
    public static final int NO_UTF_CHECK = 0x40000000;

    /**
     * Pattern can match only at end of subject
     */
    public static final int ENDANCHORED = 0x20000000;

    /**
     * Allow empty classes
     */
    public static final int ALLOW_EMPTY_CLASS = 0x00000001;

    /**
     * Alternative handling of ⧵u, ⧵U, and ⧵x
     */
    public static final int ALT_BSUX = 0x00000002;

    /**
     * Compile automatic callouts
     */
    public static final int AUTO_CALLOUT = 0x00000004;

    /**
     * Do caseless matching
     */
    public static final int CASELESS = 0x00000008;

    /**
     * $ not to match newline at end
     */
    public static final int DOLLAR_ENDONLY = 0x00000010;

    /**
     * . matches anything including NL
     */
    public static final int DOTALL = 0x00000020;

    /**
     * Allow duplicate names for subpatterns
     */
    public static final int DUPNAMES = 0x00000040;

    /**
     * Ignore white space and # comments
     */
    public static final int EXTENDED = 0x00000080;

    /**
     * Force matching to be before newline
     */
    public static final int FIRSTLINE = 0x00000100;

    /**
     * Match unset backreferences
     */
    public static final int MATCH_UNSET_BACKREF = 0x00000200;

    /**
     * ^ and $ match newlines within data
     */
    public static final int MULTILINE = 0x00000400;

    /**
     * Lock out PCRE2_UCP, e.g. via (*UCP)
     */
    public static final int NEVER_UCP = 0x00000800;

    /**
     * Lock out PCRE2_UTF, e.g. via (*UTF)
     */
    public static final int NEVER_UTF = 0x00001000;

    /**
     * Disable numbered capturing parentheses (named ones available)
     */
    public static final int NO_AUTO_CAPTURE = 0x00002000;

    /**
     * Disable auto-possessification
     */
    public static final int NO_AUTO_POSSESS = 0x00004000;

    /**
     * Disable automatic anchoring for .*
     */
    public static final int NO_DOTSTAR_ANCHOR = 0x00008000;

    /**
     * Disable match-time start optimizations
     */
    public static final int NO_START_OPTIMIZE = 0x00010000;

    /**
     * Use Unicode properties for \d, \w, etc.
     */
    public static final int UCP = 0x00020000;

    /**
     * Invert greediness of quantifiers
     */
    public static final int UNGREEDY = 0x00040000;

    /**
     * Treat pattern and subjects as UTF strings
     */
    public static final int UTF = 0x00080000;

    /**
     * Lock out the use of \C in patterns
     */
    public static final int NEVER_BACKSLASH_C = 0x00100000;

    /**
     * Alternative handling of ^ in multiline mode
     */
    public static final int ALT_CIRCUMFLEX = 0x00200000;

    /**
     * Process backslashes in verb names
     */
    public static final int ALT_VERBNAMES = 0x00400000;

    /**
     * Enable offset limit for unanchored matching
     */
    public static final int USE_OFFSET_LIMIT = 0x00800000;

    public static final int EXTENDED_MORE = 0x01000000;

    /**
     * Pattern characters are all literal
     */
    public static final int LITERAL = 0x02000000;

    /**
     * Enable support for matching invalid UTF
     */
    public static final int MATCH_INVALID_UTF = 0x04000000;

    public static final int EXTRA_ALLOW_SURROGATE_ESCAPES = 0x00000001;
    public static final int EXTRA_BAD_ESCAPE_IS_LITERAL = 0x00000002;
    public static final int EXTRA_MATCH_WORD = 0x00000004;
    public static final int EXTRA_MATCH_LINE = 0x00000008;
    public static final int EXTRA_ESCAPED_CR_IS_LF = 0x00000010;
    public static final int EXTRA_ALT_BSUX = 0x00000020;
    public static final int EXTRA_ALLOW_LOOKAROUND_BSK = 0x00000040;
    public static final int EXTRA_CASELESS_RESTRICT = 0x00000080;
    public static final int EXTRA_ASCII_BSD = 0x00000100;
    public static final int EXTRA_ASCII_BSS = 0x00000200;
    public static final int EXTRA_ASCII_BSW = 0x00000400;
    public static final int EXTRA_ASCII_POSIX = 0x00000800;
    public static final int EXTRA_ASCII_DIGIT = 0x00001000;

    /**
     * Compile code for full matching
     */
    public static final int JIT_COMPLETE = 0x00000001;

    /**
     * Compile code for soft partial matching
     */
    public static final int JIT_PARTIAL_SOFT = 0x00000002;

    /**
     * Compile code for hard partial matching
     */
    public static final int JIT_PARTIAL_HARD = 0x00000004;

    /**
     * @deprecated Use {@link #MATCH_INVALID_UTF}
     */
    @Deprecated
    public static final int JIT_INVALID_UTF = 0x00000100;

    /**
     * Subject string is not the beginning of a line
     */
    public static final int NOTBOL = 0x00000001;

    /**
     * Subject string is not the end of a line
     */
    public static final int NOTEOL = 0x00000002;

    /**
     * An empty string is not a valid match
     */
    public static final int NOTEMPTY = 0x00000004;

    /**
     * An empty string at the start of the subject is not a valid match
     */
    public static final int NOTEMPTY_ATSTART = 0x00000008;

    /**
     * Return {@link IPcre2#ERROR_PARTIAL} for a partial match even if there is a full match
     */
    public static final int PARTIAL_SOFT = 0x00000010;

    /**
     * Return {@link IPcre2#ERROR_PARTIAL} for a partial match if no full matches are found
     */
    public static final int PARTIAL_HARD = 0x00000020;
    public static final int DFA_RESTART = 0x00000040;
    public static final int DFA_SHORTEST = 0x00000080;
    public static final int SUBSTITUTE_GLOBAL = 0x00000100;
    public static final int SUBSTITUTE_EXTENDED = 0x00000200;
    public static final int SUBSTITUTE_UNSET_EMPTY = 0x00000400;
    public static final int SUBSTITUTE_UNKNOWN_UNSET = 0x00000800;
    public static final int SUBSTITUTE_OVERFLOW_LENGTH = 0x00001000;

    /**
     * Do not use JIT matching
     */
    public static final int NO_JIT = 0x00002000;

    /**
     * On success, make a private subject copy
     */
    public static final int COPY_MATCHED_SUBJECT = 0x00004000;

    public static final int SUBSTITUTE_LITERAL = 0x00008000;
    public static final int SUBSTITUTE_MATCHED = 0x00010000;
    public static final int SUBSTITUTE_REPLACEMENT_ONLY = 0x00020000;
    public static final int DISABLE_RECURSELOOP_CHECK = 0x00040000;

    public static final int CONVERT_UTF = 0x00000001;
    public static final int CONVERT_NO_UTF_CHECK = 0x00000002;
    public static final int CONVERT_POSIX_BASIC = 0x00000004;
    public static final int CONVERT_POSIX_EXTENDED = 0x00000008;
    public static final int CONVERT_GLOB = 0x00000010;
    public static final int CONVERT_GLOB_NO_WILD_SEPARATOR = 0x00000030;
    public static final int CONVERT_GLOB_NO_STARSTAR = 0x00000050;

    /**
     * Carriage return only (\r)
     */
    public static final int NEWLINE_CR = 1;

    /**
     * Linefeed only (\n)
     */
    public static final int NEWLINE_LF = 2;

    /**
     * CR followed by LF only (\r\n)
     */
    public static final int NEWLINE_CRLF = 3;

    /**
     * Any Unicode newline sequence
     */
    public static final int NEWLINE_ANY = 4;

    /**
     * Any of {@link #NEWLINE_CR}, {@link #NEWLINE_LF}, or {@link #NEWLINE_CRLF}
     */
    public static final int NEWLINE_ANYCRLF = 5;

    /**
     * NUL character (\0)
     */
    public static final int NEWLINE_NUL = 6;

    /**
     * \R corresponds to the Unicode line endings
     */
    public static final int BSR_UNICODE = 1;

    /**
     * \R corresponds to CR, LF, and CRLF only
     */
    public static final int BSR_ANYCRLF = 2;

    public static final int ERROR_END_BACKSLASH = 101;
    public static final int ERROR_END_BACKSLASH_C = 102;
    public static final int ERROR_UNKNOWN_ESCAPE = 103;
    public static final int ERROR_QUANTIFIER_OUT_OF_ORDER = 104;
    public static final int ERROR_QUANTIFIER_TOO_BIG = 105;
    public static final int ERROR_MISSING_SQUARE_BRACKET = 106;
    public static final int ERROR_ESCAPE_INVALID_IN_CLASS = 107;
    public static final int ERROR_CLASS_RANGE_ORDER = 108;
    public static final int ERROR_QUANTIFIER_INVALID = 109;
    public static final int ERROR_INTERNAL_UNEXPECTED_REPEAT = 110;
    public static final int ERROR_INVALID_AFTER_PARENS_QUERY = 111;
    public static final int ERROR_POSIX_CLASS_NOT_IN_CLASS = 112;
    public static final int ERROR_POSIX_NO_SUPPORT_COLLATING = 113;
    public static final int ERROR_MISSING_CLOSING_PARENTHESIS = 114;
    public static final int ERROR_BAD_SUBPATTERN_REFERENCE = 115;
    public static final int ERROR_NULL_PATTERN = 116;
    public static final int ERROR_BAD_OPTIONS = 117;
    public static final int ERROR_MISSING_COMMENT_CLOSING = 118;
    public static final int ERROR_PARENTHESES_NEST_TOO_DEEP = 119;
    public static final int ERROR_PATTERN_TOO_LARGE = 120;
    public static final int ERROR_HEAP_FAILED = 121;
    public static final int ERROR_UNMATCHED_CLOSING_PARENTHESIS = 122;
    public static final int ERROR_INTERNAL_CODE_OVERFLOW = 123;
    public static final int ERROR_MISSING_CONDITION_CLOSING = 124;
    public static final int ERROR_LOOKBEHIND_NOT_FIXED_LENGTH = 125;
    public static final int ERROR_ZERO_RELATIVE_REFERENCE = 126;
    public static final int ERROR_TOO_MANY_CONDITION_BRANCHES = 127;
    public static final int ERROR_CONDITION_ASSERTION_EXPECTED = 128;
    public static final int ERROR_BAD_RELATIVE_REFERENCE = 129;
    public static final int ERROR_UNKNOWN_POSIX_CLASS = 130;
    public static final int ERROR_INTERNAL_STUDY_ERROR = 131;
    public static final int ERROR_UNICODE_NOT_SUPPORTED = 132;
    public static final int ERROR_PARENTHESES_STACK_CHECK = 133;
    public static final int ERROR_CODE_POINT_TOO_BIG = 134;
    public static final int ERROR_LOOKBEHIND_TOO_COMPLICATED = 135;
    public static final int ERROR_LOOKBEHIND_INVALID_BACKSLASH_C = 136;
    public static final int ERROR_UNSUPPORTED_ESCAPE_SEQUENCE = 137;
    public static final int ERROR_CALLOUT_NUMBER_TOO_BIG = 138;
    public static final int ERROR_MISSING_CALLOUT_CLOSING = 139;
    public static final int ERROR_ESCAPE_INVALID_IN_VERB = 140;
    public static final int ERROR_UNRECOGNIZED_AFTER_QUERY_P = 141;
    public static final int ERROR_MISSING_NAME_TERMINATOR = 142;
    public static final int ERROR_DUPLICATE_SUBPATTERN_NAME = 143;
    public static final int ERROR_INVALID_SUBPATTERN_NAME = 144;
    public static final int ERROR_UNICODE_PROPERTIES_UNAVAILABLE = 145;
    public static final int ERROR_MALFORMED_UNICODE_PROPERTY = 146;
    public static final int ERROR_UNKNOWN_UNICODE_PROPERTY = 147;
    public static final int ERROR_SUBPATTERN_NAME_TOO_LONG = 148;
    public static final int ERROR_TOO_MANY_NAMED_SUBPATTERNS = 149;
    public static final int ERROR_CLASS_INVALID_RANGE = 150;
    public static final int ERROR_OCTAL_BYTE_TOO_BIG = 151;
    public static final int ERROR_INTERNAL_OVERRAN_WORKSPACE = 152;
    public static final int ERROR_INTERNAL_MISSING_SUBPATTERN = 153;
    public static final int ERROR_DEFINE_TOO_MANY_BRANCHES = 154;
    public static final int ERROR_BACKSLASH_O_MISSING_BRACE = 155;
    public static final int ERROR_INTERNAL_UNKNOWN_NEWLINE = 156;
    public static final int ERROR_BACKSLASH_G_SYNTAX = 157;
    public static final int ERROR_PARENS_QUERY_R_MISSING_CLOSING = 158;
    @Deprecated
    public static final int ERROR_VERB_ARGUMENT_NOT_ALLOWED = 159;
    public static final int ERROR_VERB_UNKNOWN = 160;
    public static final int ERROR_SUBPATTERN_NUMBER_TOO_BIG = 161;
    public static final int ERROR_SUBPATTERN_NAME_EXPECTED = 162;
    public static final int ERROR_INTERNAL_PARSED_OVERFLOW = 163;
    public static final int ERROR_INVALID_OCTAL = 164;
    public static final int ERROR_SUBPATTERN_NAMES_MISMATCH = 165;
    public static final int ERROR_MARK_MISSING_ARGUMENT = 166;
    public static final int ERROR_INVALID_HEXADECIMAL = 167;
    public static final int ERROR_BACKSLASH_C_SYNTAX = 168;
    public static final int ERROR_BACKSLASH_K_SYNTAX = 169;
    public static final int ERROR_INTERNAL_BAD_CODE_LOOKBEHINDS = 170;
    public static final int ERROR_BACKSLASH_N_IN_CLASS = 171;
    public static final int ERROR_CALLOUT_STRING_TOO_LONG = 172;
    public static final int ERROR_UNICODE_DISALLOWED_CODE_POINT = 173;
    public static final int ERROR_UTF_IS_DISABLED = 174;
    public static final int ERROR_UCP_IS_DISABLED = 175;
    public static final int ERROR_VERB_NAME_TOO_LONG = 176;
    public static final int ERROR_BACKSLASH_U_CODE_POINT_TOO_BIG = 177;
    public static final int ERROR_MISSING_OCTAL_OR_HEX_DIGITS = 178;
    public static final int ERROR_VERSION_CONDITION_SYNTAX = 179;
    public static final int ERROR_INTERNAL_BAD_CODE_AUTO_POSSESS = 180;
    public static final int ERROR_CALLOUT_NO_STRING_DELIMITER = 181;
    public static final int ERROR_CALLOUT_BAD_STRING_DELIMITER = 182;
    public static final int ERROR_BACKSLASH_C_CALLER_DISABLED = 183;
    public static final int ERROR_QUERY_BARJX_NEST_TOO_DEEP = 184;
    public static final int ERROR_BACKSLASH_C_LIBRARY_DISABLED = 185;
    public static final int ERROR_PATTERN_TOO_COMPLICATED = 186;
    public static final int ERROR_LOOKBEHIND_TOO_LONG = 187;
    public static final int ERROR_PATTERN_STRING_TOO_LONG = 188;
    public static final int ERROR_INTERNAL_BAD_CODE = 189;
    public static final int ERROR_INTERNAL_BAD_CODE_IN_SKIP = 190;
    public static final int ERROR_NO_SURROGATES_IN_UTF16 = 191;
    public static final int ERROR_BAD_LITERAL_OPTIONS = 192;
    public static final int ERROR_SUPPORTED_ONLY_IN_UNICODE = 193;
    public static final int ERROR_INVALID_HYPHEN_IN_OPTIONS = 194;
    public static final int ERROR_ALPHA_ASSERTION_UNKNOWN = 195;
    public static final int ERROR_SCRIPT_RUN_NOT_AVAILABLE = 196;
    public static final int ERROR_TOO_MANY_CAPTURES = 197;
    public static final int ERROR_CONDITION_ATOMIC_ASSERTION_EXPECTED = 198;
    public static final int ERROR_BACKSLASH_K_IN_LOOKAROUND = 199;

    public static final int ERROR_NOMATCH = -1;
    public static final int ERROR_PARTIAL = -2;
    public static final int ERROR_UTF8_ERR1 = -3;
    public static final int ERROR_UTF8_ERR2 = -4;
    public static final int ERROR_UTF8_ERR3 = -5;
    public static final int ERROR_UTF8_ERR4 = -6;
    public static final int ERROR_UTF8_ERR5 = -7;
    public static final int ERROR_UTF8_ERR6 = -8;
    public static final int ERROR_UTF8_ERR7 = -9;
    public static final int ERROR_UTF8_ERR8 = -10;
    public static final int ERROR_UTF8_ERR9 = -11;
    public static final int ERROR_UTF8_ERR10 = -12;
    public static final int ERROR_UTF8_ERR11 = -13;
    public static final int ERROR_UTF8_ERR12 = -14;
    public static final int ERROR_UTF8_ERR13 = -15;
    public static final int ERROR_UTF8_ERR14 = -16;
    public static final int ERROR_UTF8_ERR15 = -17;
    public static final int ERROR_UTF8_ERR16 = -18;
    public static final int ERROR_UTF8_ERR17 = -19;
    public static final int ERROR_UTF8_ERR18 = -20;
    public static final int ERROR_UTF8_ERR19 = -21;
    public static final int ERROR_UTF8_ERR20 = -22;
    public static final int ERROR_UTF8_ERR21 = -23;
    public static final int ERROR_UTF16_ERR1 = -24;
    public static final int ERROR_UTF16_ERR2 = -25;
    public static final int ERROR_UTF16_ERR3 = -26;
    public static final int ERROR_UTF32_ERR1 = -27;
    public static final int ERROR_UTF32_ERR2 = -28;
    public static final int ERROR_BADDATA = -29;
    public static final int ERROR_MIXEDTABLES = -30;
    public static final int ERROR_BADMAGIC = -31;
    public static final int ERROR_BADMODE = -32;
    public static final int ERROR_BADOFFSET = -33;
    public static final int ERROR_BADOPTION = -34;
    public static final int ERROR_BADREPLACEMENT = -35;
    public static final int ERROR_BADUTFOFFSET = -36;
    public static final int ERROR_CALLOUT = -37;
    public static final int ERROR_DFA_BADRESTART = -38;
    public static final int ERROR_DFA_RECURSE = -39;
    public static final int ERROR_DFA_UCOND = -40;
    public static final int ERROR_DFA_UFUNC = -41;
    public static final int ERROR_DFA_UITEM = -42;
    public static final int ERROR_DFA_WSSIZE = -43;
    public static final int ERROR_INTERNAL = -44;
    public static final int ERROR_JIT_BADOPTION = -45;
    public static final int ERROR_JIT_STACKLIMIT = -46;
    public static final int ERROR_MATCHLIMIT = -47;
    public static final int ERROR_NOMEMORY = -48;
    public static final int ERROR_NOSUBSTRING = -49;
    public static final int ERROR_NOUNIQUESUBSTRING = -50;
    public static final int ERROR_NULL = -51;
    public static final int ERROR_RECURSELOOP = -52;
    public static final int ERROR_DEPTHLIMIT = -53;
    @Deprecated
    public static final int ERROR_RECURSIONLIMIT = -53;
    public static final int ERROR_UNAVAILABLE = -54;
    public static final int ERROR_UNSET = -55;
    public static final int ERROR_BADOFFSETLIMIT = -56;
    public static final int ERROR_BADREPESCAPE = -57;
    public static final int ERROR_REPMISSINGBRACE = -58;
    public static final int ERROR_BADSUBSTITUTION = -59;
    public static final int ERROR_BADSUBSPATTERN = -60;
    public static final int ERROR_TOOMANYREPLACE = -61;
    public static final int ERROR_BADSERIALIZEDDATA = -62;
    public static final int ERROR_HEAPLIMIT = -63;
    public static final int ERROR_CONVERT_SYNTAX = -64;
    public static final int ERROR_INTERNAL_DUPMATCH = -65;
    public static final int ERROR_DFA_UINVALID_UTF = -66;
    public static final int ERROR_INVALIDOFFSET = -67;

    /**
     * Final options after compiling
     */
    public static final int INFO_ALLOPTIONS = 0;

    /**
     * Options passed to {@link #compile}
     */
    public static final int INFO_ARGOPTIONS = 1;

    /**
     * Number of highest backreference
     */
    public static final int INFO_BACKREFMAX = 2;

    /**
     * What \R matches:
     * PCRE2_BSR_UNICODE: Unicode line endings
     * PCRE2_BSR_ANYCRLF: CR, LF, or CRLF only
     */
    public static final int INFO_BSR = 3;

    /**
     * Number of capturing subpatterns
     */
    public static final int INFO_CAPTURECOUNT = 4;

    /**
     * First code unit when type is 1
     */
    public static final int INFO_FIRSTCODEUNIT = 5;

    /**
     * Type of start-of-match information
     * 0 nothing set
     * 1 first code unit is set
     * 2 start of string or after newline
     */
    public static final int INFO_FIRSTCODETYPE = 6;

    /**
     * Bitmap of first code units, or 0
     */
    public static final int INFO_FIRSTBITMAP = 7;

    /**
     * Return 1 if explicit CR or LF matches exist in the pattern
     */
    public static final int INFO_HASCRORLF = 8;

    /**
     * Return 1 if (?J) or (?-J) was used
     */
    public static final int INFO_JCHANGED = 9;

    /**
     * Size of JIT compiled code, or 0
     */
    public static final int INFO_JITSIZE = 10;

    /**
     * Last code unit when type is 1
     */
    public static final int INFO_LASTCODEUNIT = 11;

    /**
     * Type of must-be-present information
     * 0 nothing set
     * 1 code unit is set
     */
    public static final int INFO_LASTCODETYPE = 12;

    /**
     * 1 if the pattern can match an empty string, 0 otherwise
     */
    public static final int INFO_MATCHEMPTY = 13;

    /**
     * Match limit if set, otherwise {@link #ERROR_UNSET}
     */
    public static final int INFO_MATCHLIMIT = 14;

    /**
     * Length (in characters) of the longest lookbehind assertion
     */
    public static final int INFO_MAXLOOKBEHIND = 15;

    /**
     * Lower bound length of matching strings
     */
    public static final int INFO_MINLENGTH = 16;

    /**
     * Number of named subpatterns
     */
    public static final int INFO_NAMECOUNT = 17;

    /**
     * Size of name table entries
     */
    public static final int INFO_NAMEENTRYSIZE = 18;

    /**
     * Pointer to name table
     */
    public static final int INFO_NAMETABLE = 19;

    /**
     * Code for the newline sequence:
     * {@link #NEWLINE_CR}
     * {@link #NEWLINE_LF}
     * {@link #NEWLINE_CRLF}
     * {@link #NEWLINE_ANY}
     * {@link #NEWLINE_ANYCRLF}
     * {@link #NEWLINE_NUL}
     */
    public static final int INFO_NEWLINE = 20;

    /**
     * Backtracking depth limit if set, otherwise {@link #ERROR_UNSET}
     */
    public static final int INFO_DEPTHLIMIT = 21;

    /**
     * Obsolete synonym for {@link #INFO_DEPTHLIMIT}
     */
    @Deprecated
    public static final int INFO_RECURSIONLIMIT = 21;

    /**
     * Size of compiled pattern
     */
    public static final int INFO_SIZE = 22;

    /**
     * Return 1 if pattern contains \C
     */
    public static final int INFO_HASBACKSLASHC = 23;

    /**
     * Size of backtracking frame
     */
    public static final int INFO_FRAMESIZE = 24;

    /**
     * Heap memory limit if set, otherwise {@link #ERROR_UNSET}
     */
    public static final int INFO_HEAPLIMIT = 25;

    /**
     * Extra options that were passed in the compile context
     */
    public static final int INFO_EXTRAOPTIONS = 26;

    public static final int CONFIG_BSR = 0;
    public static final int CONFIG_JIT = 1;
    public static final int CONFIG_JITTARGET = 2;
    public static final int CONFIG_LINKSIZE = 3;
    public static final int CONFIG_MATCHLIMIT = 4;
    public static final int CONFIG_NEWLINE = 5;
    public static final int CONFIG_PARENSLIMIT = 6;
    public static final int CONFIG_DEPTHLIMIT = 7;
    @Deprecated
    public static final int CONFIG_RECURSIONLIMIT = 7;
    @Deprecated
    public static final int CONFIG_STACKRECURSE = 8;
    public static final int CONFIG_UNICODE = 9;
    public static final int CONFIG_UNICODE_VERSION = 10;
    public static final int CONFIG_VERSION = 11;
    public static final int CONFIG_HEAPLIMIT = 12;
    public static final int CONFIG_NEVER_BACKSLASH_C = 13;
    public static final int CONFIG_COMPILED_WIDTHS = 14;
    public static final int CONFIG_TABLES_LENGTH = 15;

    /**
     * Get the amount of memory needed to store the information referred to by {@param what} about the optional features
     * of the PCRE2 library.
     * <p>
     * Suitable for any information except:
     * {@link #CONFIG_JITTARGET} Target architecture for the JIT compiler
     * {@link #CONFIG_UNICODE_VERSION} Unicode version
     * {@link #CONFIG_VERSION} PCRE2 version
     *
     * @param what the information to query the memory requirements for
     * @return the amount of memory needed to store the information referred to by {@param what}.
     */
    public int config(int what);

    /**
     * Get the information referred to by {@param what} about the optional features of the PCRE2 library.
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
    public int config(int what, int[] where);

    /**
     * Get the information referred to by {@param what} about the optional features of the PCRE2 library.
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
    public int config(int what, ByteBuffer where);

    /**
     * Create a new general context.
     *
     * @param privateMalloc the private malloc function or 0 to use the system function
     * @param privateFree   the private free function or 0 to use the system function
     * @param memoryData    the memory data to pass to the private malloc and free functions
     * @return the general context handle
     */
    public long generalContextCreate(long privateMalloc, long privateFree, long memoryData);

    /**
     * Create a copy of a general context.
     *
     * @param gcontext the general context handle to copy
     * @return the new general context handle
     */
    public long generalContextCopy(long gcontext);

    /**
     * Free a general context.
     *
     * @param gcontext the general context handle
     */
    public void generalContextFree(long gcontext);

    /**
     * Create a new compile context.
     *
     * @param gcontext the general context handle or 0
     * @return the compile context handle
     */
    public long compileContextCreate(long gcontext);

    /**
     * Create a copy of a compile context.
     *
     * @param ccontext the compile context handle to copy
     * @return the new compile context handle
     */
    public long compileContextCopy(long ccontext);

    /**
     * Free a compile context.
     *
     * @param ccontext the compile context handle
     */
    public void compileContextFree(long ccontext);

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
    public long compile(String pattern, int options, int[] errorcode, long[] erroroffset, long ccontext);

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
    public long codeCopy(long code);

    /**
     * Free a compiled pattern resources.
     *
     * @param code the compiled pattern handle
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_code_free.html">pcre2_code_free</a>
     */
    public void codeFree(long code);

    /**
     * Get the error message for the given error code.
     *
     * @param errorcode the error code
     * @param buffer    the buffer to store the error message
     * @return the length of the error message or {@link #ERROR_NOMEMORY} if the buffer is too small and if
     * {@param errorcode} is not a valid error code, {@link #ERROR_BADDATA} is returned
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_get_error_message.html">pcre2_get_error_message</a>
     */
    public int getErrorMessage(int errorcode, ByteBuffer buffer);

    /**
     * Retrieve size of the information about a compiled pattern.
     *
     * @param code the compiled pattern handle
     * @param what the information to retrieve
     * @return Size of the information, otherwise an error code:
     * {@link #ERROR_NULL} the argument code is 0
     * {@link #ERROR_BADMAGIC} the "magic number" was not found
     * {@link #ERROR_BADOPTION} the value of {@param what} is invalid
     * {@link #ERROR_BADMODE} the pattern was compiled in the wrong mode
     * {@link #ERROR_UNSET} the requested information is not set
     */
    public int patternInfo(long code, int what);

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
     * {@link #ERROR_BADOPTION} the value of {@param what} is invalid
     * {@link #ERROR_BADMODE} the pattern was compiled in the wrong mode
     * {@link #ERROR_UNSET} the requested information is not set
     */
    public int patternInfo(long code, int what, int[] where);

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
     * {@link #ERROR_BADOPTION} the value of {@param what} is invalid
     * {@link #ERROR_BADMODE} the pattern was compiled in the wrong mode
     * {@link #ERROR_UNSET} the requested information is not set
     */
    public int patternInfo(long code, int what, long[] where);

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
     * {@link #ERROR_BADOPTION} the value of {@param what} is invalid
     * {@link #ERROR_BADMODE} the pattern was compiled in the wrong mode
     * {@link #ERROR_UNSET} the requested information is not set
     */
    public int patternInfo(long code, int what, ByteBuffer where);

    /**
     * JIT-compile a compiled pattern.
     *
     * @param code    the compiled pattern handle
     * @param options option bits
     * @return 0 on success, otherwise a negative error code
     */
    public int jitCompile(long code, int options);

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
    public int jitMatch(long code, String subject, int startoffset, int options, long matchData, long mcontext);

    /**
     * Create a JIT stack.
     *
     * @param startsize the initial stack size
     * @param maxsize   the maximum stack size
     * @param gcontext  the general context handle or 0
     * @return the JIT stack handle
     */
    public long jitStackCreate(long startsize, long maxsize, long gcontext);

    /**
     * Free a JIT stack.
     *
     * @param jitStack the JIT stack handle
     */
    public void jitStackFree(long jitStack);

    /**
     * Assign the JIT stack to a match context.
     *
     * @param mcontext the match context handle
     * @param callback a callback function handle or 0
     * @param data     a JIT stack handle or a value to be passed to the callback function
     */
    public void jitStackAssign(long mcontext, long callback, long data);

    /**
     * Create a new match data block.
     *
     * @param ovecsize the size of the ovector
     * @param gcontext the general context handle or 0
     * @return the match data handle
     */
    public long matchDataCreate(int ovecsize, long gcontext);

    /**
     * Create a new match data block from a compiled pattern.
     *
     * @param code     the compiled pattern handle
     * @param gcontext the general context handle or 0
     * @return the match data handle
     */
    public long matchDataCreateFromPattern(long code, long gcontext);

    /**
     * Free a match data block.
     *
     * @param matchData the match data handle
     */
    public void matchDataFree(long matchData);

    /**
     * Create a new match context.
     *
     * @param gcontext the general context handle or 0
     * @return the match context handle
     */
    public long matchContextCreate(long gcontext);

    /**
     * Create a copy of a match context.
     *
     * @param mcontext the match context handle to copy
     * @return the new match context handle
     */
    public long matchContextCopy(long mcontext);

    /**
     * Free a match context.
     *
     * @param mcontext the match context handle
     */
    public void matchContextFree(long mcontext);

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
    public int match(long code, String subject, int startoffset, int options, long matchData, long mcontext);

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
    public int dfaMatch(
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
    public int getOvectorCount(long matchData);

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
    public long getMatchDataSize(long matchData);

    /**
     * Get the output vector of the match data
     *
     * @param matchData the match data handle
     * @param ovector   the array to store the output vector
     */
    public void getOvector(long matchData, long[] ovector);

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
    public long getStartchar(long matchData);

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
    public long getMark(long matchData);

    /**
     * Set the newline convention within a compile context
     *
     * @param ccontext the compile context handle
     * @param newline  the newline convention
     * @return 0 on success, otherwise a negative error code
     */
    public int setNewline(long ccontext, int newline);

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
    public int setBsr(long ccontext, int value);

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
    public int setParensNestLimit(long ccontext, int limit);

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
    public int setMaxPatternLength(long ccontext, long length);

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
    public int setCompileExtraOptions(long ccontext, int extraOptions);

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
    public int setMatchLimit(long mcontext, int limit);

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
    public int setDepthLimit(long mcontext, int limit);

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
    public int setHeapLimit(long mcontext, int limit);

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
    public int setOffsetLimit(long mcontext, long limit);

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
    public int substitute(
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
    public int substringGetByNumber(long matchData, int number, long[] bufferptr, long[] bufflen);

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
    public int substringCopyByNumber(long matchData, int number, ByteBuffer buffer, long[] bufflen);

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
    public int substringGetByName(long matchData, String name, long[] bufferptr, long[] bufflen);

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
    public int substringCopyByName(long matchData, String name, ByteBuffer buffer, long[] bufflen);

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
    public int substringLengthByName(long matchData, String name, long[] length);

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
    public int substringLengthByNumber(long matchData, int number, long[] length);

    /**
     * Free memory that was allocated by {@link #substringGetByNumber} or {@link #substringGetByName}.
     *
     * @param buffer the pointer to the string to free (may be 0, in which case the function does nothing)
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_substring_free.html">pcre2_substring_free</a>
     */
    public void substringFree(long buffer);

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
    public int substringListGet(long matchData, long[] listptr, long[] lengthsptr);

    /**
     * Free memory that was allocated by {@link #substringListGet}.
     *
     * @param list the pointer to the string list to free (may be 0, in which case the function does nothing)
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_substring_list_free.html">pcre2_substring_list_free</a>
     */
    public void substringListFree(long list);

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
    public int substringNumberFromName(long code, String name);

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
    public int substringNametableScan(long code, String name, long[] first, long[] last);

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
    public int serializeEncode(long[] codes, int numberOfCodes, long[] serializedBytes, long[] serializedSize,
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
    public int serializeDecode(long[] codes, int numberOfCodes, byte[] bytes, long gcontext);

    /**
     * Read bytes from a native memory pointer.
     * <p>
     * This is a utility method used internally to read string data from native memory.
     *
     * @param pointer the native memory pointer
     * @param length  the number of bytes to read
     * @return the bytes read from the pointer
     */
    public byte[] readBytes(long pointer, int length);
}
