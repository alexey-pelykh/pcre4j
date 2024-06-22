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
package org.pcre4j.jna;

import com.sun.jna.FunctionMapper;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import org.pcre4j.api.IPcre2;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * A PCRE2 API using the JNA.
 */
public class Pcre2 implements IPcre2 {

    /**
     * The PCRE2 library loaded by JNA.
     */
    private final Library library;

    /**
     * Constructs a new PCRE2 API using the common library name "pcre2-8".
     */
    public Pcre2() {
        this("pcre2-8", "_8");
    }

    /**
     * Constructs a new PCRE2 API using the specified library name and function suffix.
     *
     * @param libraryName the library name (e.g. "pcre2-8" for "pcre2-8.dll" on Windows, "libpcre2-8.so" on Linux,
     *                    "libpcre2-8.dylib" on macOS)
     * @param suffix      the function suffix (e.g. "_8" as in "pcre2_compile_8")
     */
    public Pcre2(String libraryName, String suffix) {
        this.library = Native.load(
                libraryName,
                Library.class,
                Map.of(Library.OPTION_FUNCTION_MAPPER, new SuffixFunctionMapper(suffix))
        );
    }

    @Override
    public int config(int what) {
        return library.pcre2_config(what, Pointer.NULL);
    }

    @Override
    public int config(int what, int[] where) {
        if (where == null) {
            throw new IllegalArgumentException("where must not be null");
        }
        if (where.length != 1) {
            throw new IllegalArgumentException("where must be an array of length 1");
        }

        IntByReference whereRef = new IntByReference();
        int result = library.pcre2_config(what, whereRef.getPointer());
        where[0] = whereRef.getValue();
        return result;
    }

    @Override
    public int config(int what, ByteBuffer where) {
        if (where == null) {
            throw new IllegalArgumentException("where must not be null");
        }
        if (!where.isDirect()) {
            throw new IllegalArgumentException("where must be a direct buffer");
        }

        Pointer pWhere = Native.getDirectBufferPointer(where);
        return library.pcre2_config(what, pWhere);
    }

    @Override
    public long generalContextCreate(long privateMalloc, long privateFree, long memoryData) {
        Pointer gContext = library.pcre2_general_context_create(
                new Pointer(privateMalloc),
                new Pointer(privateFree),
                new Pointer(memoryData)
        );
        return Pointer.nativeValue(gContext);
    }

    @Override
    public long generalContextCopy(long gcontext) {
        Pointer newGContext = library.pcre2_general_context_copy(new Pointer(gcontext));
        return Pointer.nativeValue(newGContext);
    }

    @Override
    public void generalContextFree(long gcontext) {
        library.pcre2_general_context_free(new Pointer(gcontext));
    }

    @Override
    public long compileContextCreate(long gcontext) {
        Pointer cContext = library.pcre2_compile_context_create(new Pointer(gcontext));
        return Pointer.nativeValue(cContext);
    }

    @Override
    public long compileContextCopy(long ccontext) {
        Pointer newCContext = library.pcre2_compile_context_copy(new Pointer(ccontext));
        return Pointer.nativeValue(newCContext);
    }

    @Override
    public void compileContextFree(long ccontext) {
        library.pcre2_compile_context_free(new Pointer(ccontext));
    }

    @Override
    public long compile(String pattern, int options, int[] errorcode, long[] erroroffset, long ccontext) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern must not be null");
        }
        if (errorcode == null || errorcode.length < 1) {
            throw new IllegalArgumentException("errorcode must be an array of length 1");
        }
        if (erroroffset == null || erroroffset.length < 1) {
            throw new IllegalArgumentException("erroroffset must be an array of length 1");
        }

        IntByReference errorCodeRef = new IntByReference();
        LongByReference errorOffsetRef = new LongByReference();

        final var pszPattern = pattern.getBytes(StandardCharsets.UTF_8);

        Pointer code = library.pcre2_compile(
                pszPattern,
                pszPattern.length,
                options,
                errorCodeRef,
                errorOffsetRef,
                new Pointer(ccontext)
        );

        errorcode[0] = errorCodeRef.getValue();
        erroroffset[0] = errorOffsetRef.getValue();

        return Pointer.nativeValue(code);
    }

    @Override
    public void codeFree(long code) {
        library.pcre2_code_free(new Pointer(code));
    }

    @Override
    public int getErrorMessage(int errorcode, ByteBuffer buffer) {
        if (buffer == null) {
            throw new IllegalArgumentException("buffer must not be null");
        }
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("buffer must be direct");
        }

        Pointer pszBuffer = Native.getDirectBufferPointer(buffer);
        return library.pcre2_get_error_message(errorcode, pszBuffer, buffer.capacity());
    }

    @Override
    public int patternInfo(long code, int what) {
        return library.pcre2_pattern_info(new Pointer(code), what, Pointer.NULL);
    }

    @Override
    public int patternInfo(long code, int what, int[] where) {
        if (where == null) {
            throw new IllegalArgumentException("where must not be null");
        }
        if (where.length != 1) {
            throw new IllegalArgumentException("where must be an array of length 1");
        }

        IntByReference whereRef = new IntByReference();
        int result = library.pcre2_pattern_info(new Pointer(code), what, whereRef.getPointer());
        where[0] = whereRef.getValue();
        return result;
    }

    @Override
    public int patternInfo(long code, int what, long[] where) {
        if (where == null) {
            throw new IllegalArgumentException("where must not be null");
        }
        if (where.length != 1) {
            throw new IllegalArgumentException("where must be an array of length 1");
        }

        LongByReference whereRef = new LongByReference();
        int result = library.pcre2_pattern_info(new Pointer(code), what, whereRef.getPointer());
        where[0] = whereRef.getValue();
        return result;
    }

    @Override
    public int patternInfo(long code, int what, ByteBuffer where) {
        if (where == null) {
            throw new IllegalArgumentException("where must not be null");
        }

        PointerByReference whereRef = new PointerByReference();
        int result = library.pcre2_pattern_info(new Pointer(code), what, whereRef.getPointer());
        where.put(whereRef.getValue().getByteArray(0, where.capacity()));
        return result;
    }

    @Override
    public long matchDataCreate(int ovecsize, long gcontext) {
        Pointer matchData = library.pcre2_match_data_create(ovecsize, new Pointer(gcontext));
        return Pointer.nativeValue(matchData);
    }

    @Override
    public long matchDataCreateFromPattern(long code, long gcontext) {
        Pointer matchData = library.pcre2_match_data_create_from_pattern(new Pointer(code), new Pointer(gcontext));
        return Pointer.nativeValue(matchData);
    }

    @Override
    public void matchDataFree(long matchData) {
        library.pcre2_match_data_free(new Pointer(matchData));
    }

    @Override
    public long matchContextCreate(long gcontext) {
        Pointer matchContext = library.pcre2_match_context_create(new Pointer(gcontext));
        return Pointer.nativeValue(matchContext);
    }

    @Override
    public long matchContextCopy(long mcontext) {
        Pointer newMatchContext = library.pcre2_match_context_copy(new Pointer(mcontext));
        return Pointer.nativeValue(newMatchContext);
    }

    @Override
    public void matchContextFree(long mcontext) {
        library.pcre2_match_context_free(new Pointer(mcontext));
    }

    @Override
    public int match(long code, String subject, int startoffset, int options, long matchData, long mcontext) {
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null");
        }

        final var pszSubject = subject.getBytes(StandardCharsets.UTF_8);

        return library.pcre2_match(
                new Pointer(code),
                pszSubject,
                pszSubject.length,
                startoffset,
                options,
                new Pointer(matchData),
                new Pointer(mcontext)
        );
    }

    @Override
    public int getOvectorCount(long matchData) {
        return library.pcre2_get_ovector_count(new Pointer(matchData));
    }

    @Override
    public void getOvector(long matchData, long[] ovector) {
        if (ovector == null) {
            throw new IllegalArgumentException("ovector must not be null");
        }

        Pointer pOvector = library.pcre2_get_ovector_pointer(new Pointer(matchData));
        pOvector.read(0, ovector, 0, ovector.length);
    }

    private interface Library extends com.sun.jna.Library {
        int pcre2_config(int what, Pointer where);

        Pointer pcre2_general_context_create(Pointer malloc, Pointer free, Pointer memoryData);

        Pointer pcre2_general_context_copy(Pointer gcontext);

        void pcre2_general_context_free(Pointer gcontext);

        Pointer pcre2_compile_context_create(Pointer gcontext);

        Pointer pcre2_compile_context_copy(Pointer ccontext);

        void pcre2_compile_context_free(Pointer ccontext);

        Pointer pcre2_compile(
                byte[] pattern,
                long length,
                int options,
                IntByReference errorcode,
                LongByReference erroroffset,
                Pointer ccontext
        );

        void pcre2_code_free(Pointer code);

        int pcre2_get_error_message(int errorcode, Pointer buffer, long bufferSize);

        int pcre2_pattern_info(Pointer code, int what, Pointer where);

        Pointer pcre2_match_data_create(int ovecsize, Pointer gcontext);

        Pointer pcre2_match_data_create_from_pattern(Pointer code, Pointer gcontext);

        void pcre2_match_data_free(Pointer matchData);

        Pointer pcre2_match_context_create(Pointer gcontext);

        Pointer pcre2_match_context_copy(Pointer mcontext);

        void pcre2_match_context_free(Pointer mcontext);

        int pcre2_match(
                Pointer code,
                byte[] subject,
                long length,
                long startoffset,
                int options,
                Pointer matchData,
                Pointer mcontext
        );

        int pcre2_get_ovector_count(Pointer matchData);

        Pointer pcre2_get_ovector_pointer(Pointer matchData);
    }

    private record SuffixFunctionMapper(String suffix) implements FunctionMapper {
        @Override
        public String getFunctionName(NativeLibrary nativeLibrary, Method method) {
            return method.getName() + suffix;
        }
    }
}
