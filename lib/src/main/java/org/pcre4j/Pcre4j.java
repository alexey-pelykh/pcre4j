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


public final class Pcre4j {

    private static final Object lock = new Object();
    private static IPcre2 api = null;

    private Pcre4j() {
    }

    /**
     * Setup the Pcre4j.
     *
     * @param api the API to use
     */
    public static void setup(IPcre2 api) {
        if (api == null) {
            throw new IllegalArgumentException("api must not be null");
        }

        synchronized (lock) {
            Pcre4j.api = api;
        }
    }

    /**
     * Get the API.
     *
     * @return the API
     */
    public static IPcre2 api() {
        synchronized (lock) {
            if (api == null) {
                throw new IllegalStateException("Call Pcre4j.setup() first.");
            }
            return api;
        }
    }

}
