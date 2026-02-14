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

import java.lang.ref.Cleaner;

/**
 * A convert context for controlling pattern conversion settings.
 * <p>
 * Convert contexts are used to hold parameters for the PCRE2 pattern conversion function, which
 * converts glob patterns or POSIX regular expressions into PCRE2-compatible patterns.
 * <p>
 * Settings that can be configured through a convert context include the glob escape character
 * and the glob path separator character.
 */
public class Pcre2ConvertContext {

    /**
     * The convert context handle
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
     * Create a new convert context using the default PCRE2 API.
     *
     * @param generalContext the general context to use or {@code null} to use the default context
     */
    public Pcre2ConvertContext(Pcre2GeneralContext generalContext) {
        this(Pcre4j.api(), generalContext);
    }

    /**
     * Create a new convert context.
     *
     * @param api            the PCRE2 API to use
     * @param generalContext the general context to use or {@code null} to use the default context
     */
    public Pcre2ConvertContext(IPcre2 api, Pcre2GeneralContext generalContext) {
        if (api == null) {
            throw new IllegalArgumentException("api cannot be null");
        }

        final var handle = api.convertContextCreate(
                generalContext != null ? generalContext.handle : 0
        );
        if (handle == 0) {
            throw new IllegalStateException("Failed to create convert context");
        }

        this.api = api;
        this.handle = handle;
        this.cleanable = Pcre4jCleaner.INSTANCE.register(this, new Pcre2ConvertContext.Clean(api, handle));
    }

    /**
     * Get the PCRE2 API backing this convert context.
     *
     * @return the PCRE2 API
     */
    public IPcre2 api() {
        return api;
    }

    /**
     * Get the handle of the convert context.
     *
     * @return the handle of the convert context
     */
    public long handle() {
        return handle;
    }

    /**
     * Set the escape character for glob pattern conversion.
     * <p>
     * The escape character allows special glob characters to be treated as literals. The default
     * escape character is the grave accent ({@code `}) on Windows systems and the backslash
     * ({@code \}) on other platforms.
     * <p>
     * Setting the escape character to zero disables escape processing entirely. The escape character
     * must be zero (to disable) or a punctuation character with a code point less than 256.
     *
     * @param escapeChar the escape character to use, or 0 to disable escape processing
     */
    public void setGlobEscape(int escapeChar) {
        final var result = api.setGlobEscape(handle, escapeChar);
        if (result != 0) {
            final var errorMessage = Pcre4jUtils.getErrorMessage(api, result);
            throw new IllegalStateException(errorMessage);
        }
    }

    /**
     * Set the path separator character for glob pattern conversion.
     * <p>
     * This affects how path-like patterns are parsed during glob conversion. The separator character
     * must be one of forward slash ({@code /}), backslash ({@code \}), or dot ({@code .}).
     * <p>
     * On Windows systems, backslash is the default separator; on other platforms, forward slash
     * is the default.
     *
     * @param separatorChar the separator character to use (must be {@code '/'}, {@code '\\'}, or {@code '.'})
     */
    public void setGlobSeparator(int separatorChar) {
        final var result = api.setGlobSeparator(handle, separatorChar);
        if (result != 0) {
            final var errorMessage = Pcre4jUtils.getErrorMessage(api, result);
            throw new IllegalStateException(errorMessage);
        }
    }

    private record Clean(IPcre2 api, long convertContext) implements Runnable {
        @Override
        public void run() {
            api.convertContextFree(convertContext);
        }
    }

}
