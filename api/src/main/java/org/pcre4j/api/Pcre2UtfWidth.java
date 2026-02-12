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

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

/**
 * The UTF character width.
 * <p>
 * This enum provides metadata for each PCRE2 code unit width, including the library name, function suffix, charset,
 * and code unit size. This enables parameterized backends that can work with UTF-8, UTF-16, or UTF-32 encoding.
 */
public enum Pcre2UtfWidth {
    /**
     * UTF-8 encoding (1-4 bytes per character, 1 byte code unit).
     */
    UTF8(0b1, "pcre2-8", "_8", StandardCharsets.UTF_8, 1),

    /**
     * UTF-16 encoding (2 or 4 bytes per character, 2 byte code unit).
     * <p>
     * Uses native byte order (little-endian on most systems) for optimal performance with Java's internal
     * string representation.
     */
    UTF16(0b10, "pcre2-16", "_16", nativeOrderUtf16Charset(), 2),

    /**
     * UTF-32 encoding (4 bytes per character, 4 byte code unit).
     * <p>
     * Uses native byte order for consistency with UTF-16.
     */
    UTF32(0b1000, "pcre2-32", "_32", nativeOrderUtf32Charset(), 4);

    /**
     * The PCRE2 config bit value.
     */
    private final int value;

    /**
     * The default PCRE2 library name (e.g., "pcre2-8", "pcre2-16", "pcre2-32").
     */
    private final String libraryName;

    /**
     * The function suffix used by PCRE2 (e.g., "_8", "_16", "_32").
     */
    private final String functionSuffix;

    /**
     * The charset to use for encoding/decoding strings.
     */
    private final Charset charset;

    /**
     * The size of a code unit in bytes.
     */
    private final int codeUnitSize;

    /**
     * Create an enum entry with the given parameters.
     *
     * @param value          the PCRE2 config bit value
     * @param libraryName    the default library name
     * @param functionSuffix the function suffix
     * @param charset        the charset for string encoding
     * @param codeUnitSize   the code unit size in bytes
     */
    Pcre2UtfWidth(int value, String libraryName, String functionSuffix, Charset charset, int codeUnitSize) {
        this.value = value;
        this.libraryName = libraryName;
        this.functionSuffix = functionSuffix;
        this.charset = charset;
        this.codeUnitSize = codeUnitSize;
    }

    /**
     * Get the UTF-16 charset with native byte order.
     *
     * @return the UTF-16 charset
     */
    private static Charset nativeOrderUtf16Charset() {
        return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN
                ? StandardCharsets.UTF_16LE
                : StandardCharsets.UTF_16BE;
    }

    /**
     * Get the UTF-32 charset with native byte order.
     *
     * @return the UTF-32 charset
     */
    private static Charset nativeOrderUtf32Charset() {
        return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN
                ? Charset.forName("UTF-32LE")
                : Charset.forName("UTF-32BE");
    }

    /**
     * Get the enum entry by its integer value.
     *
     * @param value the integer value
     * @return the enum entry
     */
    public static Optional<Pcre2UtfWidth> fromValue(int value) {
        return Arrays.stream(values())
                .filter(entry -> entry.value == value)
                .findFirst();
    }

    /**
     * Get the PCRE2 config bit value.
     *
     * @return the integer value
     */
    public int value() {
        return value;
    }

    /**
     * Get the default PCRE2 library name.
     *
     * @return the library name (e.g., "pcre2-8", "pcre2-16", "pcre2-32")
     */
    public String libraryName() {
        return libraryName;
    }

    /**
     * Get the PCRE2 function suffix.
     *
     * @return the function suffix (e.g., "_8", "_16", "_32")
     */
    public String functionSuffix() {
        return functionSuffix;
    }

    /**
     * Get the charset for string encoding/decoding.
     *
     * @return the charset
     */
    public Charset charset() {
        return charset;
    }

    /**
     * Get the code unit size in bytes.
     *
     * @return the code unit size (1 for UTF-8, 2 for UTF-16, 4 for UTF-32)
     */
    public int codeUnitSize() {
        return codeUnitSize;
    }
}
