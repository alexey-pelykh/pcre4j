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
package org.pcre4j;

import org.pcre4j.api.IPcre2;
import org.pcre4j.api.Pcre2CalloutHandler;
import org.pcre4j.option.Pcre2CompileOption;

import java.lang.ref.Cleaner;

/**
 * A match context for controlling pattern matching settings.
 */
public class Pcre2MatchContext {

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
     * The cleanup state shared between the match context and the cleaner
     */
    private final Clean cleanState;

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
        this.cleanState = new Clean(api, handle);
        this.cleanable = Pcre4jCleaner.INSTANCE.register(this, cleanState);
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

    /**
     * Set a callout handler for match operations using this context.
     * <p>
     * When a callout point is reached during matching (either auto-generated via
     * {@link Pcre2CompileOption#AUTO_CALLOUT} or explicitly placed using {@code (?C)} or
     * {@code (?Cn)} syntax), the handler is invoked with information about the current
     * match state.
     * <p>
     * The handler must be thread-safe if this match context is used from multiple threads.
     *
     * @param handler the callout handler, or {@code null} to disable callouts
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_callout.html">pcre2_set_callout</a>
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2callout.html">PCRE2 Callouts</a>
     */
    public void setCallout(Pcre2CalloutHandler handler) {
        // Free any existing callback
        final var oldCallbackHandle = cleanState.calloutCallbackHandle;
        if (oldCallbackHandle != 0) {
            api.freeCalloutCallback(oldCallbackHandle);
            cleanState.calloutCallbackHandle = 0;
        }

        if (handler != null) {
            final var callbackHandle = api.createCalloutCallback(handler);
            cleanState.calloutCallbackHandle = callbackHandle;
            api.setCallout(handle, callbackHandle, 0);
        } else {
            api.setCallout(handle, 0, 0);
        }
    }

    /**
     * Set the match limit for this match context.
     * <p>
     * The match limit is used to limit the amount of backtracking during a match.
     * If the limit is reached, the match attempt fails with a match limit error.
     *
     * @param limit the match limit value (must be non-negative)
     * @throws IllegalArgumentException if the limit is negative
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_match_limit.html">pcre2_set_match_limit</a>
     */
    public void setMatchLimit(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("limit must be non-negative");
        }
        api.setMatchLimit(handle, limit);
    }

    /**
     * Set the backtracking depth limit for this match context.
     * <p>
     * The depth limit is used to limit the amount of backtracking depth during a match.
     * If the limit is reached, the match attempt fails with a depth limit error.
     *
     * @param limit the depth limit value (must be non-negative)
     * @throws IllegalArgumentException if the limit is negative
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_depth_limit.html">pcre2_set_depth_limit</a>
     */
    public void setDepthLimit(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("limit must be non-negative");
        }
        api.setDepthLimit(handle, limit);
    }

    /**
     * Set the heap memory limit for this match context.
     * <p>
     * The heap limit is used to limit the amount of heap memory used during a match.
     * If the limit is reached, the match attempt fails with a heap limit error.
     *
     * @param limit the heap limit value in kibibytes (1024 bytes) (must be non-negative)
     * @throws IllegalArgumentException if the limit is negative
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_heap_limit.html">pcre2_set_heap_limit</a>
     */
    public void setHeapLimit(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("limit must be non-negative");
        }
        api.setHeapLimit(handle, limit);
    }

    /**
     * Set the offset limit for this match context.
     * <p>
     * The offset limit sets a limit on how far into the subject the start of a match can be.
     * The pattern must be compiled with the {@link org.pcre4j.api.IPcre2#USE_OFFSET_LIMIT} option
     * for this to take effect.
     *
     * @param limit the offset limit value (must be non-negative)
     * @throws IllegalArgumentException if the limit is negative
     * @see <a href="https://www.pcre.org/current/doc/html/pcre2_set_offset_limit.html">pcre2_set_offset_limit</a>
     */
    public void setOffsetLimit(long limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("limit must be non-negative");
        }
        api.setOffsetLimit(handle, limit);
    }

    private static final class Clean implements Runnable {
        private final IPcre2 api;
        private final long matchContext;
        volatile long calloutCallbackHandle;

        Clean(IPcre2 api, long matchContext) {
            this.api = api;
            this.matchContext = matchContext;
        }

        @Override
        public void run() {
            if (calloutCallbackHandle != 0) {
                api.freeCalloutCallback(calloutCallbackHandle);
            }
            api.matchContextFree(matchContext);
        }
    }

}
