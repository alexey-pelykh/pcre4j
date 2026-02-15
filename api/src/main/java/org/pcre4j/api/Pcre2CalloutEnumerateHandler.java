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
 * A handler for enumerating callout points in a compiled PCRE2 pattern.
 * <p>
 * The handler is called once for each callout point found in the pattern during enumeration.
 *
 * @see Pcre2CalloutEnumerateBlock
 * @see <a href="https://www.pcre.org/current/doc/html/pcre2callout.html">PCRE2 Callouts</a>
 */
@FunctionalInterface
public interface Pcre2CalloutEnumerateHandler {

    /**
     * Called for each callout point found in a compiled pattern.
     *
     * @param block the callout enumeration information block
     * @return 0 to continue enumeration, or any non-zero value to stop enumeration
     *         (the value becomes the return value of the enumeration function)
     */
    int onCallout(Pcre2CalloutEnumerateBlock block);
}
