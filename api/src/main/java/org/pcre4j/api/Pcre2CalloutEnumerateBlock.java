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

/**
 * Information about a callout point found during pattern enumeration.
 * <p>
 * This record provides a Java-friendly view of the PCRE2 {@code pcre2_callout_enumerate_block} structure.
 * Instances are passed to {@link Pcre2CalloutEnumerateHandler} when enumerating callout points in a
 * compiled pattern.
 *
 * @param patternPosition     the offset in the pattern of the next item after the callout
 * @param nextItemLength      the length of the next item in the pattern
 * @param calloutNumber       the callout number compiled into the pattern via {@code (?Cn)}, or 0 for
 *                            string callouts ({@code (?C"string")}), or 255 for auto-callouts
 * @param calloutStringOffset the offset to the callout string within the pattern (for string callouts)
 * @param calloutStringLength the length of the callout string (for string callouts)
 * @param calloutString       the string compiled into the pattern via {@code (?C"string")}, or {@code null}
 *                            for numbered or auto-callouts
 * @see <a href="https://www.pcre.org/current/doc/html/pcre2callout.html">PCRE2 Callouts</a>
 */
public record Pcre2CalloutEnumerateBlock(
        long patternPosition,
        long nextItemLength,
        int calloutNumber,
        long calloutStringOffset,
        long calloutStringLength,
        String calloutString
) {
}
