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

public class Pcre2MatchContext {

    private static final Cleaner cleaner = Cleaner.create();

    /**
     * The match context handle
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
     * Create a new match context
     *
     * @param generalContext the general context to use or {@code null} to use the default context
     */
    public Pcre2MatchContext(Pcre2GeneralContext generalContext) {
        this(Pcre4j.api(), generalContext);
    }

    /**
     * Create a new match context
     *
     * @param api            the PCRE2 API to use
     * @param generalContext the general context to use or {@code null} to use the default context
     */
    public Pcre2MatchContext(IPcre2 api, Pcre2GeneralContext generalContext) {
        if (api == null) {
            throw new IllegalArgumentException("api cannot be null");
        }

        final var handle = api.matchContextCreate(
                generalContext != null ? generalContext.handle : 0
        );
        if (handle == 0) {
            throw new IllegalStateException("Failed to create match context");
        }

        this.api = api;
        this.handle = handle;
        this.cleanable = cleaner.register(this, new Pcre2MatchContext.Clean(api, handle));
    }

    /**
     * Get the PCRE2 API backing this match context
     *
     * @return the PCRE2 API
     */
    public IPcre2 api() {
        return api;
    }

    /**
     * Get the handle of the match context
     *
     * @return the handle of the match context
     */
    public long handle() {
        return handle;
    }

    /**
     * Assign a JIT stack to the match context
     *
     * @param jitStack the JIT stack to assign
     */
    public void assignJitStack(Pcre2JitStack jitStack) {
        if (jitStack == null) {
            throw new IllegalArgumentException("jitStack must not be null");
        }
        api.jitStackAssign(handle, 0, jitStack.handle);
    }

    private record Clean(IPcre2 api, long matchContext) implements Runnable {
        @Override
        public void run() {
            api.matchContextFree(matchContext);
        }
    }

}
