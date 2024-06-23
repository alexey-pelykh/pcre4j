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
package org.pcre4j.ffm;

import org.pcre4j.api.IPcre2;

import java.io.File;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.ByteBuffer;

/**
 * A PCRE2 API using the Foreign Function {@literal &} Memory API.
 */
public class Pcre2 implements IPcre2 {

    private static final Linker LINKER = Linker.nativeLinker();
    private static final SymbolLookup SYMBOL_LOOKUP = SymbolLookup.loaderLookup();

    private final MethodHandle pcre2_config;

    private final MethodHandle pcre2_general_context_create;
    private final MethodHandle pcre2_general_context_copy;
    private final MethodHandle pcre2_general_context_free;

    private final MethodHandle pcre2_compile_context_create;
    private final MethodHandle pcre2_compile_context_copy;
    private final MethodHandle pcre2_compile_context_free;

    private final MethodHandle pcre2_compile;
    private final MethodHandle pcre2_code_free;

    private final MethodHandle pcre2_get_error_message;
    private final MethodHandle pcre2_pattern_info;

    private final MethodHandle pcre2_jit_compile;
    private final MethodHandle pcre2_jit_match;

    private final MethodHandle pcre2_match_data_create;
    private final MethodHandle pcre2_match_data_create_from_pattern;
    private final MethodHandle pcre2_match_data_free;

    private final MethodHandle pcre2_match_context_create;
    private final MethodHandle pcre2_match_context_copy;
    private final MethodHandle pcre2_match_context_free;

    private final MethodHandle pcre2_match;

    private final MethodHandle pcre2_get_ovector_count;
    private final MethodHandle pcre2_get_ovector_pointer;

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

        if (new File(library).exists()) {
            System.load(library);
        } else {
            System.loadLibrary(library);
        }

        pcre2_config = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_config" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS // void*
                )
        );

        pcre2_general_context_create = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_general_context_create" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // pcre2_general_context*
                        ValueLayout.ADDRESS, // void* (*)(PCRE2_SIZE, void *)
                        ValueLayout.ADDRESS, // void* (*)(PCRE2_SIZE, void *)
                        ValueLayout.ADDRESS // void*
                )
        );

        pcre2_general_context_copy = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_general_context_copy" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // pcre2_general_context*
                        ValueLayout.ADDRESS // pcre2_general_context*
                )
        );

        pcre2_general_context_free = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_general_context_free" + suffix).orElseThrow(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS // pcre2_general_context*
                )
        );

        pcre2_compile_context_create = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_compile_context_create" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // pcre2_compile_context*
                        ValueLayout.ADDRESS // pcre2_general_context*
                )
        );

        pcre2_compile_context_copy = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_compile_context_copy" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // pcre2_compile_context*
                        ValueLayout.ADDRESS // pcre2_compile_context*
                )
        );

        pcre2_compile_context_free = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_compile_context_free" + suffix).orElseThrow(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS // pcre2_compile_context*
                )
        );

        pcre2_compile = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_compile" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // pcre2_code*
                        ValueLayout.ADDRESS, // PCRE2_SPTR
                        ValueLayout.ADDRESS, // PCRE2_SIZE
                        ValueLayout.JAVA_INT, // uint32_t
                        ValueLayout.ADDRESS, // int*
                        ValueLayout.ADDRESS, // PCRE2_SIZE*
                        ValueLayout.ADDRESS // pcre2_compile_context*
                )
        );

        pcre2_code_free = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_code_free" + suffix).orElseThrow(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS // pcre2_code*
                )
        );

        pcre2_get_error_message = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_get_error_message" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // PCRE2_UCHAR*
                        ValueLayout.ADDRESS // PCRE2_SIZE
                )
        );

        pcre2_pattern_info = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_pattern_info" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_code*
                        ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS // void*
                )
        );

        pcre2_jit_compile = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_jit_compile" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_code*
                        ValueLayout.JAVA_INT // int
                )
        );

        pcre2_jit_match = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_jit_match" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_code*
                        ValueLayout.ADDRESS, // PCRE2_SPTR
                        ValueLayout.ADDRESS, // PCRE2_SIZE
                        ValueLayout.ADDRESS, // PCRE2_SIZE
                        ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_match_data*
                        ValueLayout.ADDRESS // pcre2_match_context*
                )
        );

        pcre2_match_data_create = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_match_data_create" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // pcre2_match_data*
                        ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS // pcre2_general_context*
                )
        );

        pcre2_match_data_create_from_pattern = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_match_data_create_from_pattern" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // pcre2_match_data*
                        ValueLayout.ADDRESS, // pcre2_code*
                        ValueLayout.ADDRESS // pcre2_general_context*
                )
        );

        pcre2_match_data_free = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_match_data_free" + suffix).orElseThrow(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS // pcre2_match_data*
                )
        );

        pcre2_match_context_create = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_match_context_create" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // pcre2_match_context*
                        ValueLayout.ADDRESS // pcre2_general_context*
                )
        );

        pcre2_match_context_copy = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_match_context_copy" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // pcre2_match_context*
                        ValueLayout.ADDRESS // pcre2_match_context*
                )
        );

        pcre2_match_context_free = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_match_context_free" + suffix).orElseThrow(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS // pcre2_match_context*
                )
        );

        pcre2_match = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_match" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_code*
                        ValueLayout.ADDRESS, // PCRE2_SPTR
                        ValueLayout.ADDRESS, // PCRE2_SIZE
                        ValueLayout.ADDRESS, // PCRE2_SIZE
                        ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_match_data*
                        ValueLayout.ADDRESS // pcre2_match_context*
                )
        );

        pcre2_get_ovector_count = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_get_ovector_count" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS // pcre2_match_data*
                )
        );

        pcre2_get_ovector_pointer = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_get_ovector_pointer" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // PCRE2_SIZE*
                        ValueLayout.ADDRESS // pcre2_match_data*
                )
        );
    }

    @Override
    public int config(int what) {
        try (var arena = Arena.ofConfined()) {
            final var pWhat = MemorySegment.ofAddress(0);

            return (int) pcre2_config.invokeExact(
                    what,
                    pWhat
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int config(int what, int[] where) {
        if (where == null) {
            throw new IllegalArgumentException("where must not be null");
        }
        if (where.length != 1) {
            throw new IllegalArgumentException("where must be an array of length 1");
        }

        try (var arena = Arena.ofConfined()) {
            final var pWhere = arena.allocateArray(ValueLayout.JAVA_INT, 1);

            final var result = (int) pcre2_config.invokeExact(
                    what,
                    pWhere
            );

            where[0] = pWhere.get(ValueLayout.JAVA_INT, 0);

            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int config(int what, ByteBuffer where) {
        if (where == null) {
            throw new IllegalArgumentException("where must not be null");
        }
        if (!where.isDirect()) {
            throw new IllegalArgumentException("where must be direct");
        }

        try (var arena = Arena.ofConfined()) {
            final var pWhere = MemorySegment.ofBuffer(where);

            return (int) pcre2_config.invokeExact(
                    what,
                    pWhere
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long generalContextCreate(long privateMalloc, long privateFree, long memoryData) {
        try (var arena = Arena.ofConfined()) {
            final var pPrivateMalloc = MemorySegment.ofAddress(privateMalloc);
            final var pPrivateFree = MemorySegment.ofAddress(privateFree);
            final var pMemoryData = MemorySegment.ofAddress(memoryData);

            final var pGContext = (MemorySegment) pcre2_general_context_create.invokeExact(
                    pPrivateMalloc,
                    pPrivateFree,
                    pMemoryData
            );

            return pGContext.address();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long generalContextCopy(long gcontext) {
        try (var arena = Arena.ofConfined()) {
            final var pGContext = MemorySegment.ofAddress(gcontext);

            final var pNewGContext = (MemorySegment) pcre2_general_context_copy.invokeExact(
                    pGContext
            );

            return pNewGContext.address();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void generalContextFree(long gcontext) {
        try (var arena = Arena.ofConfined()) {
            final var pGContext = MemorySegment.ofAddress(gcontext);

            pcre2_general_context_free.invokeExact(
                    pGContext
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long compileContextCreate(long gcontext) {
        try (var arena = Arena.ofConfined()) {
            final var pGContext = MemorySegment.ofAddress(gcontext);

            final var pCContext = (MemorySegment) pcre2_compile_context_create.invokeExact(
                    pGContext
            );

            return pCContext.address();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long compileContextCopy(long ccontext) {
        try (var arena = Arena.ofConfined()) {
            final var pCContext = MemorySegment.ofAddress(ccontext);

            final var pNewCContext = (MemorySegment) pcre2_compile_context_copy.invokeExact(
                    pCContext
            );

            return pNewCContext.address();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void compileContextFree(long ccontext) {
        try (var arena = Arena.ofConfined()) {
            final var pCContext = MemorySegment.ofAddress(ccontext);

            pcre2_compile_context_free.invokeExact(
                    pCContext
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
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

        try (var arena = Arena.ofConfined()) {
            final var pszPattern = arena.allocateUtf8String(pattern);
            final var patternSize = MemorySegment.ofAddress(pszPattern.byteSize() - 1);
            final var pErrorCode = arena.allocateArray(ValueLayout.JAVA_INT, 1);
            final var pErrorOffset = arena.allocateArray(ValueLayout.JAVA_LONG, 1);
            final var pContext = MemorySegment.ofAddress(ccontext);

            final var pCode = (MemorySegment) pcre2_compile.invokeExact(
                    pszPattern,
                    patternSize,
                    options,
                    pErrorCode,
                    pErrorOffset,
                    pContext
            );

            errorcode[0] = pErrorCode.get(ValueLayout.JAVA_INT, 0);
            erroroffset[0] = pErrorOffset.get(ValueLayout.JAVA_LONG, 0);

            return pCode.address();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void codeFree(long code) {
        try (var arena = Arena.ofConfined()) {
            final var pCode = MemorySegment.ofAddress(code);

            pcre2_code_free.invokeExact(
                    pCode
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getErrorMessage(int errorcode, ByteBuffer buffer) {
        if (buffer == null) {
            throw new IllegalArgumentException("buffer must not be null");
        }
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("buffer must be direct");
        }

        try (var arena = Arena.ofConfined()) {
            final var pszBuffer = MemorySegment.ofBuffer(buffer);
            final var bufferSize = MemorySegment.ofAddress(buffer.capacity());

            return (int) pcre2_get_error_message.invokeExact(
                    errorcode,
                    pszBuffer,
                    bufferSize
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int patternInfo(long code, int what) {
        try (var arena = Arena.ofConfined()) {
            final var pCode = MemorySegment.ofAddress(code);
            final var pWhere = MemorySegment.ofAddress(0);

            return (int) pcre2_pattern_info.invokeExact(
                    pCode,
                    what,
                    pWhere
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int patternInfo(long code, int what, int[] where) {
        if (where == null) {
            throw new IllegalArgumentException("where must not be null");
        }
        if (where.length != 1) {
            throw new IllegalArgumentException("where must be an array of length 1");
        }

        try (var arena = Arena.ofConfined()) {
            final var pCode = MemorySegment.ofAddress(code);
            final var pWhere = arena.allocateArray(ValueLayout.JAVA_INT, 1);

            final var result = (int) pcre2_pattern_info.invokeExact(
                    pCode,
                    what,
                    pWhere
            );

            where[0] = pWhere.get(ValueLayout.JAVA_INT, 0);

            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int patternInfo(long code, int what, long[] where) {
        if (where == null) {
            throw new IllegalArgumentException("where must not be null");
        }
        if (where.length != 1) {
            throw new IllegalArgumentException("where must be an array of length 1");
        }

        try (var arena = Arena.ofConfined()) {
            final var pCode = MemorySegment.ofAddress(code);
            final var pWhere = arena.allocateArray(ValueLayout.JAVA_LONG, 1);

            final var result = (int) pcre2_pattern_info.invokeExact(
                    pCode,
                    what,
                    pWhere
            );

            where[0] = pWhere.get(ValueLayout.JAVA_LONG, 0);

            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int patternInfo(long code, int what, ByteBuffer where) {
        if (where == null) {
            throw new IllegalArgumentException("where must not be null");
        }

        try (var arena = Arena.ofConfined()) {
            final var pCode = MemorySegment.ofAddress(code);
            final var pWhere = arena.allocateArray(ValueLayout.ADDRESS, 1);

            final var result = (int) pcre2_pattern_info.invokeExact(
                    pCode,
                    what,
                    pWhere
            );

            final var pTable = pWhere.get(ValueLayout.ADDRESS, 0).reinterpret(where.capacity());
            MemorySegment.ofBuffer(where).copyFrom(pTable);

            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int jitCompile(long code, int options) {
        try (var arena = Arena.ofConfined()) {
            final var pCode = MemorySegment.ofAddress(code);

            return (int) pcre2_jit_compile.invokeExact(
                    pCode,
                    options
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int jitMatch(long code, String subject, int startoffset, int options, long matchData, long mcontext) {
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null");
        }

        try (var arena = Arena.ofConfined()) {
            final var pCode = MemorySegment.ofAddress(code);
            final var pszSubject = arena.allocateUtf8String(subject);
            final var subjectLength = MemorySegment.ofAddress(pszSubject.byteSize() - 1);
            final var startOffset = MemorySegment.ofAddress(startoffset);
            final var pMatchData = MemorySegment.ofAddress(matchData);
            final var pMatchContext = MemorySegment.ofAddress(mcontext);

            return (int) pcre2_jit_match.invokeExact(
                    pCode,
                    pszSubject,
                    subjectLength,
                    startOffset,
                    options,
                    pMatchData,
                    pMatchContext
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long matchDataCreate(int ovecsize, long gcontext) {
        try (var arena = Arena.ofConfined()) {
            final var pGContext = MemorySegment.ofAddress(gcontext);

            final var pMatchData = (MemorySegment) pcre2_match_data_create.invokeExact(
                    ovecsize,
                    pGContext
            );

            return pMatchData.address();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long matchDataCreateFromPattern(long code, long gcontext) {
        try (var arena = Arena.ofConfined()) {
            final var pCode = MemorySegment.ofAddress(code);
            final var pGContext = MemorySegment.ofAddress(gcontext);

            final var pMatchData = (MemorySegment) pcre2_match_data_create_from_pattern.invokeExact(
                    pCode,
                    pGContext
            );

            return pMatchData.address();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void matchDataFree(long matchData) {
        try (var arena = Arena.ofConfined()) {
            final var pMatchData = MemorySegment.ofAddress(matchData);

            pcre2_match_data_free.invokeExact(
                    pMatchData
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long matchContextCreate(long gcontext) {
        try (var arena = Arena.ofConfined()) {
            final var pGContext = MemorySegment.ofAddress(gcontext);

            final var pMatchContext = (MemorySegment) pcre2_match_context_create.invokeExact(
                    pGContext
            );

            return pMatchContext.address();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long matchContextCopy(long mcontext) {
        try (var arena = Arena.ofConfined()) {
            final var pMatchContext = MemorySegment.ofAddress(mcontext);

            final var pNewMatchContext = (MemorySegment) pcre2_match_context_copy.invokeExact(
                    pMatchContext
            );

            return pNewMatchContext.address();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void matchContextFree(long mcontext) {
        try (var arena = Arena.ofConfined()) {
            final var pMatchContext = MemorySegment.ofAddress(mcontext);

            pcre2_match_context_free.invokeExact(
                    pMatchContext
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int match(long code, String subject, int startoffset, int options, long matchData, long mcontext) {
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null");
        }

        try (var arena = Arena.ofConfined()) {
            final var pCode = MemorySegment.ofAddress(code);
            final var pszSubject = arena.allocateUtf8String(subject);
            final var subjectLength = MemorySegment.ofAddress(pszSubject.byteSize() - 1);
            final var startOffset = MemorySegment.ofAddress(startoffset);
            final var pMatchData = MemorySegment.ofAddress(matchData);
            final var pMatchContext = MemorySegment.ofAddress(mcontext);

            return (int) pcre2_match.invokeExact(
                    pCode,
                    pszSubject,
                    subjectLength,
                    startOffset,
                    options,
                    pMatchData,
                    pMatchContext
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getOvectorCount(long matchData) {
        try (var arena = Arena.ofConfined()) {
            final var pMatchData = MemorySegment.ofAddress(matchData);

            return (int) pcre2_get_ovector_count.invokeExact(
                    pMatchData
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getOvector(long matchData, long[] ovector) {
        if (ovector == null) {
            throw new IllegalArgumentException("ovector must not be null");
        }

        try (var arena = Arena.ofConfined()) {
            final var pMatchData = MemorySegment.ofAddress(matchData);

            final var pOvector = (MemorySegment) pcre2_get_ovector_pointer.invokeExact(
                    pMatchData
            );

            MemorySegment.ofArray(ovector).copyFrom(pOvector.reinterpret(ovector.length * 8));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
