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
        this(
                System.getProperty("pcre2.library.name", "pcre2-8"),
                System.getProperty("pcre2.function.suffix", "_8")
        );
    }

    /**
     * Constructs a new PCRE2 API using the specified library name and function suffix.
     *
     * @param library the library name (e.g. "pcre2-8" for "pcre2-8.dll" on Windows, "libpcre2-8.so" on Linux,
     *                "libpcre2-8.dylib" on macOS) or an absolute path to the library file
     * @param suffix  the function suffix (e.g. "_8" as in "pcre2_compile_8")
     */
    public Pcre2(String library, String suffix) {
        if (library == null) {
            throw new IllegalArgumentException("library must not be null");
        }
        if (suffix == null) {
            throw new IllegalArgumentException("suffix must not be null");
        }

        this.library = Native.load(
                library,
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

        final var whereRef = new IntByReference();
        final var result = library.pcre2_config(what, whereRef.getPointer());
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

        final var pWhere = Native.getDirectBufferPointer(where);
        return library.pcre2_config(what, pWhere);
    }

    @Override
    public long generalContextCreate(long privateMalloc, long privateFree, long memoryData) {
        final var pPrivateMalloc = new Pointer(privateMalloc);
        final var pPrivateFree = new Pointer(privateFree);
        final var pMemoryData = new Pointer(memoryData);
        final var pGContext = library.pcre2_general_context_create(
                pPrivateMalloc,
                pPrivateFree,
                pMemoryData
        );
        return Pointer.nativeValue(pGContext);
    }

    @Override
    public long generalContextCopy(long gcontext) {
        final var pGContext = new Pointer(gcontext);
        final var pNewGContext = library.pcre2_general_context_copy(pGContext);
        return Pointer.nativeValue(pNewGContext);
    }

    @Override
    public void generalContextFree(long gcontext) {
        final var pGContext = new Pointer(gcontext);
        library.pcre2_general_context_free(pGContext);
    }

    @Override
    public long compileContextCreate(long gcontext) {
        final var pGContext = new Pointer(gcontext);
        final var pCContext = library.pcre2_compile_context_create(pGContext);
        return Pointer.nativeValue(pCContext);
    }

    @Override
    public long compileContextCopy(long ccontext) {
        final var pCContext = new Pointer(ccontext);
        final var pNewCContext = library.pcre2_compile_context_copy(pCContext);
        return Pointer.nativeValue(pNewCContext);
    }

    @Override
    public void compileContextFree(long ccontext) {
        final var pCContext = new Pointer(ccontext);
        library.pcre2_compile_context_free(pCContext);
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

        final var pszPattern = pattern.getBytes(StandardCharsets.UTF_8);
        final var patternSize = new Pointer(pszPattern.length);
        final var errorCodeRef = new IntByReference();
        final var errorOffsetRef = new LongByReference();
        final var pCContext = new Pointer(ccontext);

        final var pCode = library.pcre2_compile(
                pszPattern,
                patternSize,
                options,
                errorCodeRef,
                errorOffsetRef,
                pCContext
        );

        errorcode[0] = errorCodeRef.getValue();
        erroroffset[0] = errorOffsetRef.getValue();

        return Pointer.nativeValue(pCode);
    }

    @Override
    public void codeFree(long code) {
        final var pCode = new Pointer(code);
        library.pcre2_code_free(pCode);
    }

    @Override
    public int getErrorMessage(int errorcode, ByteBuffer buffer) {
        if (buffer == null) {
            throw new IllegalArgumentException("buffer must not be null");
        }
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("buffer must be direct");
        }

        final var pszBuffer = Native.getDirectBufferPointer(buffer);
        final var bufferSize = new Pointer(buffer.capacity());
        return library.pcre2_get_error_message(errorcode, pszBuffer, bufferSize);
    }

    @Override
    public int patternInfo(long code, int what) {
        final var pCode = new Pointer(code);
        return library.pcre2_pattern_info(pCode, what, Pointer.NULL);
    }

    @Override
    public int patternInfo(long code, int what, int[] where) {
        if (where == null) {
            throw new IllegalArgumentException("where must not be null");
        }
        if (where.length != 1) {
            throw new IllegalArgumentException("where must be an array of length 1");
        }

        final var pCode = new Pointer(code);
        final var whereRef = new IntByReference();
        final var result = library.pcre2_pattern_info(pCode, what, whereRef.getPointer());
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

        final var pCode = new Pointer(code);
        final var whereRef = new LongByReference();
        final var result = library.pcre2_pattern_info(pCode, what, whereRef.getPointer());
        where[0] = whereRef.getValue();
        return result;
    }

    @Override
    public int patternInfo(long code, int what, ByteBuffer where) {
        if (where == null) {
            throw new IllegalArgumentException("where must not be null");
        }

        final var pCode = new Pointer(code);
        final var whereRef = new PointerByReference();
        final var result = library.pcre2_pattern_info(pCode, what, whereRef.getPointer());
        where.put(whereRef.getValue().getByteArray(0, where.capacity()));
        return result;
    }

    @Override
    public int jitCompile(long code, int options) {
        final var pCode = new Pointer(code);
        return library.pcre2_jit_compile(pCode, options);
    }

    @Override
    public int jitMatch(long code, String subject, int startoffset, int options, long matchData, long mcontext) {
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null");
        }

        final var pCode = new Pointer(code);
        final var pszSubject = subject.getBytes(StandardCharsets.UTF_8);
        final var subjectLength = new Pointer(pszSubject.length);
        final var startOffset = new Pointer(startoffset);
        final var pMatchData = new Pointer(matchData);
        final var pMContext = new Pointer(mcontext);

        return library.pcre2_jit_match(
                pCode,
                pszSubject,
                subjectLength,
                startOffset,
                options,
                pMatchData,
                pMContext
        );
    }

    @Override
    public long jitStackCreate(long startsize, long maxsize, long gcontext) {
        final var startSize = new Pointer(startsize);
        final var maxSize = new Pointer(maxsize);
        final var pGContext = new Pointer(gcontext);
        final var jitStack = library.pcre2_jit_stack_create(startSize, maxSize, pGContext);
        return Pointer.nativeValue(jitStack);
    }

    @Override
    public void jitStackFree(long stack) {
        final var pStack = new Pointer(stack);
        library.pcre2_jit_stack_free(pStack);
    }

    @Override
    public void jitStackAssign(long mcontext, long callback, long data) {
        final var pMContext = new Pointer(mcontext);
        final var pCallback = new Pointer(callback);
        final var pData = new Pointer(data);
        library.pcre2_jit_stack_assign(pMContext, pCallback, pData);
    }

    @Override
    public long matchDataCreate(int ovecsize, long gcontext) {
        final var pGContext = new Pointer(gcontext);
        final var pMatchData = library.pcre2_match_data_create(ovecsize, pGContext);
        return Pointer.nativeValue(pMatchData);
    }

    @Override
    public long matchDataCreateFromPattern(long code, long gcontext) {
        final var pCode = new Pointer(code);
        final var pGContext = new Pointer(gcontext);
        final var pMatchData = library.pcre2_match_data_create_from_pattern(pCode, pGContext);
        return Pointer.nativeValue(pMatchData);
    }

    @Override
    public void matchDataFree(long matchData) {
        final var pMatchData = new Pointer(matchData);
        library.pcre2_match_data_free(pMatchData);
    }

    @Override
    public long matchContextCreate(long gcontext) {
        final var pGContext = new Pointer(gcontext);
        final var pMatchContext = library.pcre2_match_context_create(pGContext);
        return Pointer.nativeValue(pMatchContext);
    }

    @Override
    public long matchContextCopy(long mcontext) {
        final var pMContext = new Pointer(mcontext);
        final var pNewMatchContext = library.pcre2_match_context_copy(pMContext);
        return Pointer.nativeValue(pNewMatchContext);
    }

    @Override
    public void matchContextFree(long mcontext) {
        final var pMContext = new Pointer(mcontext);
        library.pcre2_match_context_free(pMContext);
    }

    @Override
    public int match(long code, String subject, int startoffset, int options, long matchData, long mcontext) {
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null");
        }

        final var pCode = new Pointer(code);
        final var pszSubject = subject.getBytes(StandardCharsets.UTF_8);
        final var subjectLength = new Pointer(pszSubject.length);
        final var startOffset = new Pointer(startoffset);
        final var pMatchData = new Pointer(matchData);
        final var pMContext = new Pointer(mcontext);

        return library.pcre2_match(
                pCode,
                pszSubject,
                subjectLength,
                startOffset,
                options,
                pMatchData,
                pMContext
        );
    }

    @Override
    public int getOvectorCount(long matchData) {
        final var pMatchData = new Pointer(matchData);
        return library.pcre2_get_ovector_count(pMatchData);
    }

    @Override
    public void getOvector(long matchData, long[] ovector) {
        if (ovector == null) {
            throw new IllegalArgumentException("ovector must not be null");
        }

        final var pMatchData = new Pointer(matchData);
        final var pOvector = library.pcre2_get_ovector_pointer(pMatchData);
        pOvector.read(0, ovector, 0, ovector.length);
    }

    @Override
    public int setNewline(long ccontext, int value) {
        final var pCContext = new Pointer(ccontext);
        return library.pcre2_set_newline(pCContext, value);
    }

    @Override
    public int substitute(
            long code,
            String subject,
            int startoffset,
            int options,
            long matchData,
            long mcontext,
            String replacement,
            ByteBuffer outputbuffer,
            long[] outputlength
    ) {
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null");
        }
        if (replacement == null) {
            throw new IllegalArgumentException("replacement must not be null");
        }
        if (outputbuffer == null) {
            throw new IllegalArgumentException("outputbuffer must not be null");
        }
        if (!outputbuffer.isDirect()) {
            throw new IllegalArgumentException("outputbuffer must be direct");
        }
        if (outputlength == null || outputlength.length < 1) {
            throw new IllegalArgumentException("outputlength must be an array of length 1");
        }

        final var pCode = new Pointer(code);
        final var pszSubject = subject.getBytes(StandardCharsets.UTF_8);
        final var subjectLength = new Pointer(pszSubject.length);
        final var startOffset = new Pointer(startoffset);
        final var pMatchData = new Pointer(matchData);
        final var pMContext = new Pointer(mcontext);
        final var pszReplacement = replacement.getBytes(StandardCharsets.UTF_8);
        final var replacementLength = new Pointer(pszReplacement.length);
        final var pOutputBuffer = Native.getDirectBufferPointer(outputbuffer);
        final var outputLengthRef = new LongByReference(outputlength[0]);

        final var result = library.pcre2_substitute(
                pCode,
                pszSubject,
                subjectLength,
                startOffset,
                options,
                pMatchData,
                pMContext,
                pszReplacement,
                replacementLength,
                pOutputBuffer,
                outputLengthRef
        );

        outputlength[0] = outputLengthRef.getValue();

        return result;
    }

    @Override
    public int substringGetByNumber(long matchData, int number, long[] bufferptr, long[] bufflen) {
        if (bufferptr == null || bufferptr.length < 1) {
            throw new IllegalArgumentException("bufferptr must be an array of length 1");
        }
        if (bufflen == null || bufflen.length < 1) {
            throw new IllegalArgumentException("bufflen must be an array of length 1");
        }

        final var pMatchData = new Pointer(matchData);
        final var bufferPtrRef = new PointerByReference();
        final var buffLenRef = new LongByReference();

        final var result = library.pcre2_substring_get_bynumber(
                pMatchData,
                number,
                bufferPtrRef,
                buffLenRef
        );

        bufferptr[0] = Pointer.nativeValue(bufferPtrRef.getValue());
        bufflen[0] = buffLenRef.getValue();

        return result;
    }

    @Override
    public int substringGetByName(long matchData, String name, long[] bufferptr, long[] bufflen) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if (bufferptr == null || bufferptr.length < 1) {
            throw new IllegalArgumentException("bufferptr must be an array of length 1");
        }
        if (bufflen == null || bufflen.length < 1) {
            throw new IllegalArgumentException("bufflen must be an array of length 1");
        }

        final var pMatchData = new Pointer(matchData);
        final var nameBytes = name.getBytes(StandardCharsets.UTF_8);
        final var pszName = new byte[nameBytes.length + 1]; // +1 for null terminator
        System.arraycopy(nameBytes, 0, pszName, 0, nameBytes.length);
        pszName[nameBytes.length] = 0; // null terminator
        final var bufferPtrRef = new PointerByReference();
        final var buffLenRef = new LongByReference();

        final var result = library.pcre2_substring_get_byname(
                pMatchData,
                pszName,
                bufferPtrRef,
                buffLenRef
        );

        bufferptr[0] = Pointer.nativeValue(bufferPtrRef.getValue());
        bufflen[0] = buffLenRef.getValue();

        return result;
    }

    @Override
    public void substringFree(long buffer) {
        final var pBuffer = new Pointer(buffer);
        library.pcre2_substring_free(pBuffer);
    }

    @Override
    public byte[] readBytes(long pointer, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length must not be negative");
        }
        if (length == 0) {
            return new byte[0];
        }

        final var pBuffer = new Pointer(pointer);
        final var bytes = new byte[length];
        pBuffer.read(0, bytes, 0, length);
        return bytes;
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
                Pointer length,
                int options,
                IntByReference errorcode,
                LongByReference erroroffset,
                Pointer ccontext
        );
        void pcre2_code_free(Pointer code);

        int pcre2_get_error_message(int errorcode, Pointer buffer, Pointer bufferSize);
        int pcre2_pattern_info(Pointer code, int what, Pointer where);

        int pcre2_jit_compile(Pointer code, int options);
        int pcre2_jit_match(
                Pointer code,
                byte[] subject,
                Pointer length,
                Pointer startoffset,
                int options,
                Pointer matchData,
                Pointer mcontext
        );
        Pointer pcre2_jit_stack_create(Pointer startSize, Pointer maxSize, Pointer gcontext);
        void pcre2_jit_stack_free(Pointer stack);
        void pcre2_jit_stack_assign(Pointer mcontext, Pointer callback, Pointer data);

        Pointer pcre2_match_data_create(int ovecsize, Pointer gcontext);
        Pointer pcre2_match_data_create_from_pattern(Pointer code, Pointer gcontext);
        void pcre2_match_data_free(Pointer matchData);

        Pointer pcre2_match_context_create(Pointer gcontext);
        Pointer pcre2_match_context_copy(Pointer mcontext);
        void pcre2_match_context_free(Pointer mcontext);

        int pcre2_match(
                Pointer code,
                byte[] subject,
                Pointer length,
                Pointer startoffset,
                int options,
                Pointer matchData,
                Pointer mcontext
        );

        int pcre2_get_ovector_count(Pointer matchData);
        Pointer pcre2_get_ovector_pointer(Pointer matchData);

        int pcre2_set_newline(Pointer ccontext, int value);

        int pcre2_substitute(
                Pointer code,
                byte[] subject,
                Pointer length,
                Pointer startoffset,
                int options,
                Pointer matchData,
                Pointer mcontext,
                byte[] replacement,
                Pointer rlength,
                Pointer outputbuffer,
                LongByReference outlengthptr
        );

        int pcre2_substring_get_bynumber(
                Pointer matchData,
                int number,
                PointerByReference bufferptr,
                LongByReference bufflen
        );

        int pcre2_substring_get_byname(
                Pointer matchData,
                byte[] name,
                PointerByReference bufferptr,
                LongByReference bufflen
        );

        void pcre2_substring_free(Pointer buffer);
    }

    private record SuffixFunctionMapper(String suffix) implements FunctionMapper {
        @Override
        public String getFunctionName(NativeLibrary nativeLibrary, Method method) {
            return method.getName() + suffix;
        }
    }
}
