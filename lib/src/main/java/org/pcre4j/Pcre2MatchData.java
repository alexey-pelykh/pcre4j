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

import java.lang.ref.Cleaner;

/**
 * The match data where the results of the match are stored
 */
public class Pcre2MatchData {

    private static final Cleaner cleaner = Cleaner.create();
    /**
     * The match data handle
     */
    /* package-private */ final long handle;

    /**
     * The PCRE2 API reference to use across the entire lifecycle of the object
     */
    /* package-private */ final IPcre2 api;

    /**
     * The cleaner to free the resources
     */
    private final Cleaner.Cleanable cleanable;

    /**
     * Create a new match data object
     *
     * @param ovecsize the size of the output vector
     */
    public Pcre2MatchData(int ovecsize) {
        final var api = Pcre4j.api();

        final var handle = api.matchDataCreate(
                ovecsize,
                0
        );
        if (handle == 0) {
            throw new IllegalStateException("Failed to create match data");
        }

        this.api = api;
        this.handle = handle;
        this.cleanable = cleaner.register(this, new Pcre2MatchData.Clean(api, handle));
    }

    /**
     * Create a new match data object
     *
     * @param code the compiled pattern to create the match data for
     */
    public Pcre2MatchData(Pcre2Code code) {
        final var api = Pcre4j.api();

        final var handle = api.matchDataCreateFromPattern(
                code.handle,
                0
        );
        if (handle == 0) {
            throw new IllegalStateException("Failed to create match data from pattern");
        }

        this.api = api;
        this.handle = handle;
        this.cleanable = cleaner.register(this, new Pcre2MatchData.Clean(api, handle));
    }

    /**
     * Get the PCRE2 API backing this match data
     *
     * @return the PCRE2 API
     */
    public IPcre2 api() {
        return api;
    }

    /**
     * Get the handle of the match data
     *
     * @return the handle of the match data
     */
    public long handle() {
        return handle;
    }

    /**
     * Get number of the offset pairs in the output vector
     *
     * @return the number of the offset pairs in the output vector
     */
    public int ovectorCount() {
        return api.getOvectorCount(handle);
    }

    /**
     * Get the output vector composed of offset pairs, each offset pair represents the start and end of the match. The
     * value of the offset is the index of the byte where the character starts, not the charcater index.
     *
     * @return the output vector
     */
    public long[] ovector() {
        final var ovector = new long[api.getOvectorCount(handle) * 2];
        api.getOvector(handle, ovector);
        return ovector;
    }

    private record Clean(IPcre2 api, long matchData) implements Runnable {
        @Override
        public void run() {
            api.matchDataFree(matchData);
        }
    }

}
