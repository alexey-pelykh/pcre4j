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
 * A handler for PCRE2 callouts during pattern matching.
 * <p>
 * When a callout point is reached during a match operation, the handler is invoked with information
 * about the current match state. The handler can inspect the state and decide whether to continue
 * or abort matching.
 *
 * @see Pcre2CalloutBlock
 * @see <a href="https://www.pcre.org/current/doc/html/pcre2callout.html">PCRE2 Callouts</a>
 */
@FunctionalInterface
public interface Pcre2CalloutHandler {

    /**
     * Called by PCRE2 during matching when a callout point is reached.
     *
     * @param block the callout information block
     * @return 0 to continue matching, a positive value to force a match failure at the current point
     *         (causing backtracking), or a negative value less than {@code PCRE2_ERROR_NOMATCH} to
     *         abort matching with that error code
     */
    int onCallout(Pcre2CalloutBlock block);
}
