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

import java.lang.ref.Cleaner;

/**
 * Shared {@link Cleaner} instance for all PCRE4J native resource wrappers.
 * <p>
 * Using a single shared cleaner reduces daemon thread overhead from one thread per wrapper class to a single thread
 * for the entire library.
 */
/* package-private */ final class Pcre4jCleaner {

    /**
     * The shared {@link Cleaner} instance.
     */
    static final Cleaner INSTANCE = Cleaner.create();

    private Pcre4jCleaner() {
    }

}
