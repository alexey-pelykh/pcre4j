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
package org.pcre4j.api;

/**
 * Information about a callout point reached during matching.
 * <p>
 * This record provides a Java-friendly view of the PCRE2 {@code pcre2_callout_block} structure.
 * Instances are passed to {@link Pcre2CalloutHandler} during match operations when a callout point
 * is reached in the pattern.
 *
 * @param calloutNumber       the callout number compiled into the pattern via {@code (?Cn)}, or 0 for
 *                            string callouts ({@code (?C"string")}), or 255 for auto-callouts
 * @param captureTop          one more than the number of the highest numbered captured group so far
 * @param captureLast         the number of the most recently captured group
 * @param startMatch          the offset in the subject where the current match attempt started
 * @param currentPosition     the current position in the subject
 * @param patternPosition     the offset in the pattern of the next item to be matched
 * @param nextItemLength      the length of the next item in the pattern
 * @param calloutStringOffset the offset to the callout string within the pattern (for string callouts)
 * @param calloutStringLength the length of the callout string (for string callouts)
 * @param calloutString       the string compiled into the pattern via {@code (?C"string")}, or {@code null}
 *                            for numbered or auto-callouts
 * @param calloutFlags        flags providing additional information:
 *                            bit 0 ({@code PCRE2_CALLOUT_STARTMATCH}) is set for each bumpalong,
 *                            bit 1 ({@code PCRE2_CALLOUT_BACKTRACK}) is set after a backtrack
 * @see <a href="https://www.pcre.org/current/doc/html/pcre2callout.html">PCRE2 Callouts</a>
 */
public record Pcre2CalloutBlock(
        int calloutNumber,
        int captureTop,
        int captureLast,
        long startMatch,
        long currentPosition,
        long patternPosition,
        long nextItemLength,
        long calloutStringOffset,
        long calloutStringLength,
        String calloutString,
        int calloutFlags
) {
}
