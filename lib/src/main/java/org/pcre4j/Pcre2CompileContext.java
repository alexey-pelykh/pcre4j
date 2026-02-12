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

public class Pcre2CompileContext {

    private static final Cleaner cleaner = Cleaner.create();

    /**
     * The compile context handle
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
     * Create a new compile context
     *
     * @param generalContext the general context to use or {@code null} to use the default context
     */
    public Pcre2CompileContext(Pcre2GeneralContext generalContext) {
        this(Pcre4j.api(), generalContext);
    }

    /**
     * Create a new compile context
     *
     * @param api            the PCRE2 API to use
     * @param generalContext the general context to use or {@code null} to use the default context
     */
    public Pcre2CompileContext(IPcre2 api, Pcre2GeneralContext generalContext) {
        if (api == null) {
            throw new IllegalArgumentException("api cannot be null");
        }

        final var handle = api.compileContextCreate(
                generalContext != null ? generalContext.handle : 0
        );
        if (handle == 0) {
            throw new IllegalStateException("Failed to create compile context");
        }

        this.api = api;
        this.handle = handle;
        this.cleanable = cleaner.register(this, new Pcre2CompileContext.Clean(api, handle));
    }

    /**
     * Get the PCRE2 API backing this compile context
     *
     * @return the PCRE2 API
     */
    public IPcre2 api() {
        return api;
    }

    /**
     * Get the handle of the compile context
     *
     * @return the handle of the compile context
     */
    public long handle() {
        return handle;
    }

    /**
     * Set the newline convention
     *
     * @param newline the newline convention
     */
    public void setNewline(Pcre2Newline newline) {
        if (newline == null) {
            throw new IllegalArgumentException("newline cannot be null");
        }
        final var result = api.setNewline(handle, newline.value());
        if (result != 0) {
            final var errorMessage = Pcre4jUtils.getErrorMessage(api, result);
            throw new IllegalStateException(errorMessage);
        }
    }

    /**
     * Set the BSR (backslash-R) convention
     *
     * @param bsr the BSR convention
     */
    public void setBsr(Pcre2Bsr bsr) {
        if (bsr == null) {
            throw new IllegalArgumentException("bsr cannot be null");
        }
        final var result = api.setBsr(handle, bsr.value());
        if (result != 0) {
            final var errorMessage = Pcre4jUtils.getErrorMessage(api, result);
            throw new IllegalStateException(errorMessage);
        }
    }

    /**
     * Set the parentheses nesting limit.
     * <p>
     * This limit is used to prevent patterns with excessive parentheses nesting from consuming
     * too many resources during compilation. The default limit is 250, but this can be changed
     * at build time.
     * <p>
     * If a pattern exceeds this limit during compilation, the error {@code PCRE2_ERROR_PARENTHESES_NEST_TOO_DEEP}
     * is returned.
     *
     * @param limit the maximum depth of nested parentheses allowed in a pattern
     */
    public void setParensNestLimit(int limit) {
        final var result = api.setParensNestLimit(handle, limit);
        if (result != 0) {
            final var errorMessage = Pcre4jUtils.getErrorMessage(api, result);
            throw new IllegalStateException(errorMessage);
        }
    }

    /**
     * Set the maximum pattern length.
     * <p>
     * This limit restricts the maximum length (in code units) of a pattern that can be compiled.
     * If a pattern longer than this limit is passed to compilation, the compilation immediately
     * fails with an error.
     * <p>
     * By default, there is no limit (the value is the maximum that a PCRE2_SIZE variable can hold).
     * This can be used for security purposes to prevent excessively long patterns from being processed.
     *
     * @param length the maximum pattern length in code units
     */
    public void setMaxPatternLength(long length) {
        final var result = api.setMaxPatternLength(handle, length);
        if (result != 0) {
            final var errorMessage = Pcre4jUtils.getErrorMessage(api, result);
            throw new IllegalStateException(errorMessage);
        }
    }

    /**
     * Set the extra compile options.
     * <p>
     * This method sets additional option bits for pattern compilation that are housed in the compile context.
     * These options completely replace any previously set extra options.
     * <p>
     * The extra options provide fine-grained control over pattern compilation, such as restricting character
     * class matching to ASCII or allowing special escape sequences.
     *
     * @param extraOptions the extra compile options to set
     */
    public void setCompileExtraOptions(Pcre2CompileExtraOption... extraOptions) {
        if (extraOptions == null) {
            throw new IllegalArgumentException("extraOptions cannot be null");
        }
        var extraOptionsValue = 0;
        for (var extraOption : extraOptions) {
            if (extraOption == null) {
                throw new IllegalArgumentException("extraOptions cannot contain null");
            }
            extraOptionsValue |= extraOption.value();
        }
        final var result = api.setCompileExtraOptions(handle, extraOptionsValue);
        if (result != 0) {
            final var errorMessage = Pcre4jUtils.getErrorMessage(api, result);
            throw new IllegalStateException(errorMessage);
        }
    }

    private record Clean(IPcre2 api, long compileContext) implements Runnable {
        @Override
        public void run() {
            api.compileContextFree(compileContext);
        }
    }

}
