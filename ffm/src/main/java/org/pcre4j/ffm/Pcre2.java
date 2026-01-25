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

    private final MethodHandle pcre2_maketables;
    private final MethodHandle pcre2_maketables_free;

    private final MethodHandle pcre2_compile_context_create;
    private final MethodHandle pcre2_compile_context_copy;
    private final MethodHandle pcre2_compile_context_free;

    private final MethodHandle pcre2_compile;
    private final MethodHandle pcre2_code_copy;
    private final MethodHandle pcre2_code_copy_with_tables;
    private final MethodHandle pcre2_code_free;

    private final MethodHandle pcre2_callout_enumerate;

    private final MethodHandle pcre2_get_error_message;
    private final MethodHandle pcre2_pattern_info;

    private final MethodHandle pcre2_jit_compile;
    private final MethodHandle pcre2_jit_match;
    private final MethodHandle pcre2_jit_stack_create;
    private final MethodHandle pcre2_jit_stack_free;
    private final MethodHandle pcre2_jit_stack_assign;
    private final MethodHandle pcre2_jit_free_unused_memory;

    private final MethodHandle pcre2_match_data_create;
    private final MethodHandle pcre2_match_data_create_from_pattern;
    private final MethodHandle pcre2_match_data_free;

    private final MethodHandle pcre2_match_context_create;
    private final MethodHandle pcre2_match_context_copy;
    private final MethodHandle pcre2_match_context_free;

    private final MethodHandle pcre2_convert_context_create;
    private final MethodHandle pcre2_convert_context_copy;
    private final MethodHandle pcre2_convert_context_free;

    private final MethodHandle pcre2_pattern_convert;
    private final MethodHandle pcre2_converted_pattern_free;

    private final MethodHandle pcre2_match;
    private final MethodHandle pcre2_dfa_match;

    private final MethodHandle pcre2_get_ovector_count;
    private final MethodHandle pcre2_get_match_data_size;
    private final MethodHandle pcre2_get_ovector_pointer;
    private final MethodHandle pcre2_get_startchar;
    private final MethodHandle pcre2_get_mark;

    private final MethodHandle pcre2_set_newline;
    private final MethodHandle pcre2_set_bsr;
    private final MethodHandle pcre2_set_parens_nest_limit;
    private final MethodHandle pcre2_set_max_pattern_length;
    private final MethodHandle pcre2_set_compile_extra_options;
    private final MethodHandle pcre2_set_character_tables;
    private final MethodHandle pcre2_set_compile_recursion_guard;
    private final MethodHandle pcre2_set_match_limit;
    private final MethodHandle pcre2_set_depth_limit;
    private final MethodHandle pcre2_set_heap_limit;
    private final MethodHandle pcre2_set_offset_limit;
    private final MethodHandle pcre2_set_callout;

    private final MethodHandle pcre2_substitute;

    private final MethodHandle pcre2_substring_get_bynumber;
    private final MethodHandle pcre2_substring_copy_bynumber;
    private final MethodHandle pcre2_substring_get_byname;
    private final MethodHandle pcre2_substring_copy_byname;
    private final MethodHandle pcre2_substring_length_byname;
    private final MethodHandle pcre2_substring_length_bynumber;
    private final MethodHandle pcre2_substring_free;
    private final MethodHandle pcre2_substring_list_get;
    private final MethodHandle pcre2_substring_list_free;
    private final MethodHandle pcre2_substring_number_from_name;
    private final MethodHandle pcre2_substring_nametable_scan;

    private final MethodHandle pcre2_serialize_encode;
    private final MethodHandle pcre2_serialize_decode;
    private final MethodHandle pcre2_serialize_free;
    private final MethodHandle pcre2_serialize_get_number_of_codes;

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

        if (library.indexOf(File.separatorChar) != -1) {
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

        pcre2_maketables = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_maketables" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // const uint8_t*
                        ValueLayout.ADDRESS // pcre2_general_context*
                )
        );

        pcre2_maketables_free = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_maketables_free" + suffix).orElseThrow(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS, // pcre2_general_context*
                        ValueLayout.ADDRESS // const uint8_t*
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

        pcre2_code_copy = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_code_copy" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // pcre2_code*
                        ValueLayout.ADDRESS // pcre2_code*
                )
        );

        pcre2_code_copy_with_tables = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_code_copy_with_tables" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // pcre2_code*
                        ValueLayout.ADDRESS // pcre2_code*
                )
        );

        pcre2_code_free = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_code_free" + suffix).orElseThrow(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS // pcre2_code*
                )
        );

        pcre2_callout_enumerate = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_callout_enumerate" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // const pcre2_code*
                        ValueLayout.ADDRESS, // int (*)(pcre2_callout_enumerate_block *, void *)
                        ValueLayout.ADDRESS  // void*
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

        pcre2_jit_stack_create = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_jit_stack_create" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // pcre2_jit_stack*
                        ValueLayout.ADDRESS, // PCRE2_SIZE
                        ValueLayout.ADDRESS, // PCRE2_SIZE
                        ValueLayout.ADDRESS // pcre2_general_context*
                )
        );

        pcre2_jit_stack_free = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_jit_stack_free" + suffix).orElseThrow(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS // pcre2_jit_stack*
                )
        );

        pcre2_jit_stack_assign = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_jit_stack_assign" + suffix).orElseThrow(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS, // pcre2_code*
                        ValueLayout.ADDRESS, // pcre2_jit_callback
                        ValueLayout.ADDRESS // void*
                )
        );

        pcre2_jit_free_unused_memory = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_jit_free_unused_memory" + suffix).orElseThrow(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS // pcre2_general_context*
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

        pcre2_convert_context_create = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_convert_context_create" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // pcre2_convert_context*
                        ValueLayout.ADDRESS // pcre2_general_context*
                )
        );

        pcre2_convert_context_copy = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_convert_context_copy" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // pcre2_convert_context*
                        ValueLayout.ADDRESS // pcre2_convert_context*
                )
        );

        pcre2_convert_context_free = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_convert_context_free" + suffix).orElseThrow(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS // pcre2_convert_context*
                )
        );

        pcre2_pattern_convert = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_pattern_convert" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // PCRE2_SPTR pattern
                        ValueLayout.ADDRESS, // PCRE2_SIZE length
                        ValueLayout.JAVA_INT, // uint32_t options
                        ValueLayout.ADDRESS, // PCRE2_UCHAR** buffer
                        ValueLayout.ADDRESS, // PCRE2_SIZE* blength
                        ValueLayout.ADDRESS  // pcre2_convert_context*
                )
        );

        pcre2_converted_pattern_free = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_converted_pattern_free" + suffix).orElseThrow(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS // PCRE2_UCHAR*
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

        pcre2_dfa_match = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_dfa_match" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_code*
                        ValueLayout.ADDRESS, // PCRE2_SPTR
                        ValueLayout.ADDRESS, // PCRE2_SIZE length
                        ValueLayout.ADDRESS, // PCRE2_SIZE startoffset
                        ValueLayout.JAVA_INT, // uint32_t options
                        ValueLayout.ADDRESS, // pcre2_match_data*
                        ValueLayout.ADDRESS, // pcre2_match_context*
                        ValueLayout.ADDRESS, // int* workspace
                        ValueLayout.ADDRESS  // PCRE2_SIZE wscount
                )
        );

        pcre2_get_ovector_count = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_get_ovector_count" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS // pcre2_match_data*
                )
        );

        pcre2_get_match_data_size = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_get_match_data_size" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // PCRE2_SIZE
                        ValueLayout.ADDRESS // pcre2_match_data*
                )
        );

        pcre2_get_ovector_pointer = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_get_ovector_pointer" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // PCRE2_SIZE*
                        ValueLayout.ADDRESS // pcre2_match_data*
                )
        );

        pcre2_get_startchar = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_get_startchar" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // PCRE2_SIZE
                        ValueLayout.ADDRESS // pcre2_match_data*
                )
        );

        pcre2_get_mark = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_get_mark" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.ADDRESS, // PCRE2_SPTR
                        ValueLayout.ADDRESS // pcre2_match_data*
                )
        );

        pcre2_set_newline = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_set_newline" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_compile_context*
                        ValueLayout.JAVA_INT // int
                )
        );

        pcre2_set_bsr = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_set_bsr" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_compile_context*
                        ValueLayout.JAVA_INT // int
                )
        );

        pcre2_set_parens_nest_limit = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_set_parens_nest_limit" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_compile_context*
                        ValueLayout.JAVA_INT // uint32_t
                )
        );

        pcre2_set_max_pattern_length = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_set_max_pattern_length" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_compile_context*
                        ValueLayout.ADDRESS // PCRE2_SIZE
                )
        );

        pcre2_set_compile_extra_options = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_set_compile_extra_options" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_compile_context*
                        ValueLayout.JAVA_INT // uint32_t
                )
        );

        pcre2_set_character_tables = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_set_character_tables" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_compile_context*
                        ValueLayout.ADDRESS // const uint8_t*
                )
        );

        pcre2_set_compile_recursion_guard = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_set_compile_recursion_guard" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_compile_context*
                        ValueLayout.ADDRESS, // int (*)(uint32_t, void *)
                        ValueLayout.ADDRESS // void*
                )
        );

        pcre2_set_match_limit = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_set_match_limit" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_match_context*
                        ValueLayout.JAVA_INT // uint32_t
                )
        );

        pcre2_set_depth_limit = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_set_depth_limit" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_match_context*
                        ValueLayout.JAVA_INT // uint32_t
                )
        );

        pcre2_set_heap_limit = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_set_heap_limit" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_match_context*
                        ValueLayout.JAVA_INT // uint32_t
                )
        );

        pcre2_set_offset_limit = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_set_offset_limit" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_match_context*
                        ValueLayout.ADDRESS // PCRE2_SIZE
                )
        );

        pcre2_set_callout = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_set_callout" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_match_context*
                        ValueLayout.ADDRESS, // int (*)(pcre2_callout_block *, void *)
                        ValueLayout.ADDRESS  // void*
                )
        );

        pcre2_substitute = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_substitute" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_code*
                        ValueLayout.ADDRESS, // PCRE2_SPTR subject
                        ValueLayout.ADDRESS, // PCRE2_SIZE length
                        ValueLayout.ADDRESS, // PCRE2_SIZE startoffset
                        ValueLayout.JAVA_INT, // uint32_t options
                        ValueLayout.ADDRESS, // pcre2_match_data*
                        ValueLayout.ADDRESS, // pcre2_match_context*
                        ValueLayout.ADDRESS, // PCRE2_SPTR replacement
                        ValueLayout.ADDRESS, // PCRE2_SIZE rlength
                        ValueLayout.ADDRESS, // PCRE2_UCHAR* outputbuffer
                        ValueLayout.ADDRESS  // PCRE2_SIZE* outlengthptr
                )
        );

        pcre2_substring_get_bynumber = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_substring_get_bynumber" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_match_data*
                        ValueLayout.JAVA_INT, // uint32_t number
                        ValueLayout.ADDRESS, // PCRE2_UCHAR** bufferptr
                        ValueLayout.ADDRESS  // PCRE2_SIZE* bufflen
                )
        );

        pcre2_substring_copy_bynumber = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_substring_copy_bynumber" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_match_data*
                        ValueLayout.JAVA_INT, // uint32_t number
                        ValueLayout.ADDRESS, // PCRE2_UCHAR* buffer
                        ValueLayout.ADDRESS  // PCRE2_SIZE* bufflen
                )
        );

        pcre2_substring_get_byname = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_substring_get_byname" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_match_data*
                        ValueLayout.ADDRESS, // PCRE2_SPTR name
                        ValueLayout.ADDRESS, // PCRE2_UCHAR** bufferptr
                        ValueLayout.ADDRESS  // PCRE2_SIZE* bufflen
                )
        );

        pcre2_substring_copy_byname = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_substring_copy_byname" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_match_data*
                        ValueLayout.ADDRESS, // PCRE2_SPTR name
                        ValueLayout.ADDRESS, // PCRE2_UCHAR* buffer
                        ValueLayout.ADDRESS  // PCRE2_SIZE* bufflen
                )
        );

        pcre2_substring_length_byname = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_substring_length_byname" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_match_data*
                        ValueLayout.ADDRESS, // PCRE2_SPTR name
                        ValueLayout.ADDRESS  // PCRE2_SIZE* length
                )
        );

        pcre2_substring_length_bynumber = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_substring_length_bynumber" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_match_data*
                        ValueLayout.JAVA_INT, // uint32_t number
                        ValueLayout.ADDRESS  // PCRE2_SIZE* length
                )
        );

        pcre2_substring_free = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_substring_free" + suffix).orElseThrow(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS // PCRE2_UCHAR* buffer
                )
        );

        pcre2_substring_list_get = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_substring_list_get" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_match_data* match_data
                        ValueLayout.ADDRESS, // PCRE2_UCHAR*** listptr
                        ValueLayout.ADDRESS  // PCRE2_SIZE** lengthsptr
                )
        );

        pcre2_substring_list_free = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_substring_list_free" + suffix).orElseThrow(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS // PCRE2_UCHAR** list
                )
        );

        pcre2_substring_number_from_name = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_substring_number_from_name" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // pcre2_code*
                        ValueLayout.ADDRESS  // PCRE2_SPTR name
                )
        );

        pcre2_substring_nametable_scan = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_substring_nametable_scan" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int
                        ValueLayout.ADDRESS, // const pcre2_code *code
                        ValueLayout.ADDRESS, // PCRE2_SPTR name
                        ValueLayout.ADDRESS, // PCRE2_SPTR *first
                        ValueLayout.ADDRESS  // PCRE2_SPTR *last
                )
        );

        pcre2_serialize_encode = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_serialize_encode" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int32_t
                        ValueLayout.ADDRESS, // const pcre2_code **codes
                        ValueLayout.JAVA_INT, // int32_t number_of_codes
                        ValueLayout.ADDRESS, // uint8_t **serialized_bytes
                        ValueLayout.ADDRESS, // PCRE2_SIZE *serialized_size
                        ValueLayout.ADDRESS  // pcre2_general_context *gcontext
                )
        );

        pcre2_serialize_decode = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_serialize_decode" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int32_t
                        ValueLayout.ADDRESS, // pcre2_code **codes
                        ValueLayout.JAVA_INT, // int32_t number_of_codes
                        ValueLayout.ADDRESS, // const uint8_t *bytes
                        ValueLayout.ADDRESS  // pcre2_general_context *gcontext
                )
        );

        pcre2_serialize_free = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_serialize_free" + suffix).orElseThrow(),
                FunctionDescriptor.ofVoid(
                        ValueLayout.ADDRESS // uint8_t *bytes
                )
        );

        pcre2_serialize_get_number_of_codes = LINKER.downcallHandle(
                SYMBOL_LOOKUP.find("pcre2_serialize_get_number_of_codes" + suffix).orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT, // int32_t
                        ValueLayout.ADDRESS // const uint8_t *bytes
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
    public long maketables(long gcontext) {
        try (var arena = Arena.ofConfined()) {
            final var pGContext = MemorySegment.ofAddress(gcontext);

            final var pTables = (MemorySegment) pcre2_maketables.invokeExact(
                    pGContext
            );

            return pTables.address();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void maketablesFree(long gcontext, long tables) {
        try (var arena = Arena.ofConfined()) {
            final var pGContext = MemorySegment.ofAddress(gcontext);
            final var pTables = MemorySegment.ofAddress(tables);

            pcre2_maketables_free.invokeExact(
                    pGContext,
                    pTables
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
    public long codeCopy(long code) {
        try (var arena = Arena.ofConfined()) {
            final var pCode = MemorySegment.ofAddress(code);

            final var pNewCode = (MemorySegment) pcre2_code_copy.invokeExact(
                    pCode
            );

            return pNewCode.address();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long codeCopyWithTables(long code) {
        try (var arena = Arena.ofConfined()) {
            final var pCode = MemorySegment.ofAddress(code);

            final var pNewCode = (MemorySegment) pcre2_code_copy_with_tables.invokeExact(
                    pCode
            );

            return pNewCode.address();
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
    public int calloutEnumerate(long code, long callback, long calloutData) {
        try {
            final var pCode = MemorySegment.ofAddress(code);
            final var pCallback = MemorySegment.ofAddress(callback);
            final var pCalloutData = MemorySegment.ofAddress(calloutData);

            return (int) pcre2_callout_enumerate.invokeExact(
                    pCode,
                    pCallback,
                    pCalloutData
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
    public long jitStackCreate(long startsize, long maxsize, long gcontext) {
        try (var arena = Arena.ofConfined()) {
            final var startSize = MemorySegment.ofAddress(startsize);
            final var maxSize = MemorySegment.ofAddress(maxsize);
            final var pGContext = MemorySegment.ofAddress(gcontext);

            final var pJitStack = (MemorySegment) pcre2_jit_stack_create.invokeExact(
                    startSize,
                    maxSize,
                    pGContext
            );

            return pJitStack.address();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void jitStackFree(long jitStack) {
        try (var arena = Arena.ofConfined()) {
            final var pJitStack = MemorySegment.ofAddress(jitStack);

            pcre2_jit_stack_free.invokeExact(
                    pJitStack
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void jitStackAssign(long mcontext, long callback, long data) {
        try (var arena = Arena.ofConfined()) {
            final var pMContext = MemorySegment.ofAddress(mcontext);
            final var pCallback = MemorySegment.ofAddress(callback);
            final var pData = MemorySegment.ofAddress(data);

            pcre2_jit_stack_assign.invokeExact(
                    pMContext,
                    pCallback,
                    pData
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void jitFreeUnusedMemory(long gcontext) {
        try (var arena = Arena.ofConfined()) {
            final var pGContext = MemorySegment.ofAddress(gcontext);

            pcre2_jit_free_unused_memory.invokeExact(
                    pGContext
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
    public long convertContextCreate(long gcontext) {
        try (var arena = Arena.ofConfined()) {
            final var pGContext = MemorySegment.ofAddress(gcontext);

            final var pCvContext = (MemorySegment) pcre2_convert_context_create.invokeExact(
                    pGContext
            );

            return pCvContext.address();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long convertContextCopy(long cvcontext) {
        try (var arena = Arena.ofConfined()) {
            final var pCvContext = MemorySegment.ofAddress(cvcontext);

            final var pNewCvContext = (MemorySegment) pcre2_convert_context_copy.invokeExact(
                    pCvContext
            );

            return pNewCvContext.address();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void convertContextFree(long cvcontext) {
        try (var arena = Arena.ofConfined()) {
            final var pCvContext = MemorySegment.ofAddress(cvcontext);

            pcre2_convert_context_free.invokeExact(
                    pCvContext
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int patternConvert(String pattern, int options, long[] buffer, long[] blength, long cvcontext) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern must not be null");
        }
        if (buffer == null || buffer.length < 1) {
            throw new IllegalArgumentException("buffer must be an array of length 1");
        }
        if (blength == null || blength.length < 1) {
            throw new IllegalArgumentException("blength must be an array of length 1");
        }

        try (var arena = Arena.ofConfined()) {
            final var pszPattern = arena.allocateUtf8String(pattern);
            final var patternLength = MemorySegment.ofAddress(pszPattern.byteSize() - 1);
            final var pBuffer = arena.allocate(ValueLayout.ADDRESS);
            pBuffer.set(ValueLayout.ADDRESS, 0, MemorySegment.ofAddress(buffer[0]));
            final var pBlength = arena.allocate(ValueLayout.JAVA_LONG);
            pBlength.set(ValueLayout.JAVA_LONG, 0, blength[0]);
            final var pCvContext = MemorySegment.ofAddress(cvcontext);

            final var result = (int) pcre2_pattern_convert.invokeExact(
                    pszPattern,
                    patternLength,
                    options,
                    pBuffer,
                    pBlength,
                    pCvContext
            );

            buffer[0] = pBuffer.get(ValueLayout.ADDRESS, 0).address();
            blength[0] = pBlength.get(ValueLayout.JAVA_LONG, 0);

            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void convertedPatternFree(long convertedPattern) {
        try (var arena = Arena.ofConfined()) {
            final var pConvertedPattern = MemorySegment.ofAddress(convertedPattern);

            pcre2_converted_pattern_free.invokeExact(
                    pConvertedPattern
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
    public int dfaMatch(
            long code,
            String subject,
            int startoffset,
            int options,
            long matchData,
            long mcontext,
            int[] workspace,
            int wscount
    ) {
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null");
        }
        if (workspace == null) {
            throw new IllegalArgumentException("workspace must not be null");
        }
        if (wscount < 0) {
            throw new IllegalArgumentException("wscount must not be negative");
        }
        if (wscount > workspace.length) {
            throw new IllegalArgumentException("wscount must not be greater than workspace.length");
        }

        try (var arena = Arena.ofConfined()) {
            final var pCode = MemorySegment.ofAddress(code);
            final var pszSubject = arena.allocateUtf8String(subject);
            final var subjectLength = MemorySegment.ofAddress(pszSubject.byteSize() - 1);
            final var startOffset = MemorySegment.ofAddress(startoffset);
            final var pMatchData = MemorySegment.ofAddress(matchData);
            final var pMatchContext = MemorySegment.ofAddress(mcontext);
            final var pWorkspace = arena.allocateArray(ValueLayout.JAVA_INT, workspace);
            final var wsCount = MemorySegment.ofAddress(wscount);

            return (int) pcre2_dfa_match.invokeExact(
                    pCode,
                    pszSubject,
                    subjectLength,
                    startOffset,
                    options,
                    pMatchData,
                    pMatchContext,
                    pWorkspace,
                    wsCount
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
    public long getMatchDataSize(long matchData) {
        try (var arena = Arena.ofConfined()) {
            final var pMatchData = MemorySegment.ofAddress(matchData);

            final var result = (MemorySegment) pcre2_get_match_data_size.invokeExact(
                    pMatchData
            );

            return result.address();
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

    @Override
    public long getStartchar(long matchData) {
        try (var arena = Arena.ofConfined()) {
            final var pMatchData = MemorySegment.ofAddress(matchData);

            final var result = (MemorySegment) pcre2_get_startchar.invokeExact(
                    pMatchData
            );

            return result.address();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getMark(long matchData) {
        try (var arena = Arena.ofConfined()) {
            final var pMatchData = MemorySegment.ofAddress(matchData);

            final var result = (MemorySegment) pcre2_get_mark.invokeExact(
                    pMatchData
            );

            return result.address();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int setNewline(long ccontext, int newline) {
        try (var arena = Arena.ofConfined()) {
            final var pCContext = MemorySegment.ofAddress(ccontext);

            return (int) pcre2_set_newline.invokeExact(
                    pCContext,
                    newline
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int setBsr(long ccontext, int value) {
        try (var arena = Arena.ofConfined()) {
            final var pCContext = MemorySegment.ofAddress(ccontext);

            return (int) pcre2_set_bsr.invokeExact(
                    pCContext,
                    value
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int setParensNestLimit(long ccontext, int limit) {
        try (var arena = Arena.ofConfined()) {
            final var pCContext = MemorySegment.ofAddress(ccontext);

            return (int) pcre2_set_parens_nest_limit.invokeExact(
                    pCContext,
                    limit
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int setMaxPatternLength(long ccontext, long length) {
        try (var arena = Arena.ofConfined()) {
            final var pCContext = MemorySegment.ofAddress(ccontext);
            final var pLength = MemorySegment.ofAddress(length);

            return (int) pcre2_set_max_pattern_length.invokeExact(
                    pCContext,
                    pLength
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int setCompileExtraOptions(long ccontext, int extraOptions) {
        try (var arena = Arena.ofConfined()) {
            final var pCContext = MemorySegment.ofAddress(ccontext);

            return (int) pcre2_set_compile_extra_options.invokeExact(
                    pCContext,
                    extraOptions
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int setCharacterTables(long ccontext, long tables) {
        try (var arena = Arena.ofConfined()) {
            final var pCContext = MemorySegment.ofAddress(ccontext);
            final var pTables = MemorySegment.ofAddress(tables);

            return (int) pcre2_set_character_tables.invokeExact(
                    pCContext,
                    pTables
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int setCompileRecursionGuard(long ccontext, long guardFunction, long userData) {
        try {
            final var pCContext = MemorySegment.ofAddress(ccontext);
            final var pGuardFunction = MemorySegment.ofAddress(guardFunction);
            final var pUserData = MemorySegment.ofAddress(userData);

            return (int) pcre2_set_compile_recursion_guard.invokeExact(
                    pCContext,
                    pGuardFunction,
                    pUserData
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int setMatchLimit(long mcontext, int limit) {
        try (var arena = Arena.ofConfined()) {
            final var pMContext = MemorySegment.ofAddress(mcontext);

            return (int) pcre2_set_match_limit.invokeExact(
                    pMContext,
                    limit
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int setDepthLimit(long mcontext, int limit) {
        try (var arena = Arena.ofConfined()) {
            final var pMContext = MemorySegment.ofAddress(mcontext);

            return (int) pcre2_set_depth_limit.invokeExact(
                    pMContext,
                    limit
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int setHeapLimit(long mcontext, int limit) {
        try (var arena = Arena.ofConfined()) {
            final var pMContext = MemorySegment.ofAddress(mcontext);

            return (int) pcre2_set_heap_limit.invokeExact(
                    pMContext,
                    limit
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int setOffsetLimit(long mcontext, long limit) {
        try (var arena = Arena.ofConfined()) {
            final var pMContext = MemorySegment.ofAddress(mcontext);
            final var pLimit = MemorySegment.ofAddress(limit);

            return (int) pcre2_set_offset_limit.invokeExact(
                    pMContext,
                    pLimit
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int setCallout(long mcontext, long callback, long calloutData) {
        try {
            final var pMContext = MemorySegment.ofAddress(mcontext);
            final var pCallback = MemorySegment.ofAddress(callback);
            final var pCalloutData = MemorySegment.ofAddress(calloutData);

            return (int) pcre2_set_callout.invokeExact(
                    pMContext,
                    pCallback,
                    pCalloutData
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
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

        try (var arena = Arena.ofConfined()) {
            final var pCode = MemorySegment.ofAddress(code);
            final var pszSubject = arena.allocateUtf8String(subject);
            final var subjectLength = MemorySegment.ofAddress(pszSubject.byteSize() - 1);
            final var startOffset = MemorySegment.ofAddress(startoffset);
            final var pMatchData = MemorySegment.ofAddress(matchData);
            final var pMatchContext = MemorySegment.ofAddress(mcontext);
            final var pszReplacement = arena.allocateUtf8String(replacement);
            final var replacementLength = MemorySegment.ofAddress(pszReplacement.byteSize() - 1);
            final var pOutputBuffer = MemorySegment.ofBuffer(outputbuffer);
            final var pOutputLength = arena.allocateArray(ValueLayout.JAVA_LONG, 1);
            pOutputLength.set(ValueLayout.JAVA_LONG, 0, outputlength[0]);

            final var result = (int) pcre2_substitute.invokeExact(
                    pCode,
                    pszSubject,
                    subjectLength,
                    startOffset,
                    options,
                    pMatchData,
                    pMatchContext,
                    pszReplacement,
                    replacementLength,
                    pOutputBuffer,
                    pOutputLength
            );

            outputlength[0] = pOutputLength.get(ValueLayout.JAVA_LONG, 0);

            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int substringGetByNumber(long matchData, int number, long[] bufferptr, long[] bufflen) {
        if (bufferptr == null || bufferptr.length < 1) {
            throw new IllegalArgumentException("bufferptr must be an array of length 1");
        }
        if (bufflen == null || bufflen.length < 1) {
            throw new IllegalArgumentException("bufflen must be an array of length 1");
        }

        try (var arena = Arena.ofConfined()) {
            final var pMatchData = MemorySegment.ofAddress(matchData);
            final var pBufferPtr = arena.allocateArray(ValueLayout.ADDRESS, 1);
            final var pBuffLen = arena.allocateArray(ValueLayout.JAVA_LONG, 1);

            final var result = (int) pcre2_substring_get_bynumber.invokeExact(
                    pMatchData,
                    number,
                    pBufferPtr,
                    pBuffLen
            );

            bufferptr[0] = pBufferPtr.get(ValueLayout.ADDRESS, 0).address();
            bufflen[0] = pBuffLen.get(ValueLayout.JAVA_LONG, 0);

            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int substringCopyByNumber(long matchData, int number, ByteBuffer buffer, long[] bufflen) {
        if (buffer == null) {
            throw new IllegalArgumentException("buffer must not be null");
        }
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("buffer must be a direct ByteBuffer");
        }
        if (bufflen == null || bufflen.length < 1) {
            throw new IllegalArgumentException("bufflen must be an array of length 1");
        }

        try (var arena = Arena.ofConfined()) {
            final var pMatchData = MemorySegment.ofAddress(matchData);
            final var pBuffer = MemorySegment.ofBuffer(buffer);
            final var pBuffLen = arena.allocateArray(ValueLayout.JAVA_LONG, 1);
            pBuffLen.set(ValueLayout.JAVA_LONG, 0, bufflen[0]);

            final var result = (int) pcre2_substring_copy_bynumber.invokeExact(
                    pMatchData,
                    number,
                    pBuffer,
                    pBuffLen
            );

            bufflen[0] = pBuffLen.get(ValueLayout.JAVA_LONG, 0);

            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
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

        try (var arena = Arena.ofConfined()) {
            final var pMatchData = MemorySegment.ofAddress(matchData);
            final var pszName = arena.allocateUtf8String(name);
            final var pBufferPtr = arena.allocateArray(ValueLayout.ADDRESS, 1);
            final var pBuffLen = arena.allocateArray(ValueLayout.JAVA_LONG, 1);

            final var result = (int) pcre2_substring_get_byname.invokeExact(
                    pMatchData,
                    pszName,
                    pBufferPtr,
                    pBuffLen
            );

            bufferptr[0] = pBufferPtr.get(ValueLayout.ADDRESS, 0).address();
            bufflen[0] = pBuffLen.get(ValueLayout.JAVA_LONG, 0);

            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int substringCopyByName(long matchData, String name, ByteBuffer buffer, long[] bufflen) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if (buffer == null) {
            throw new IllegalArgumentException("buffer must not be null");
        }
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("buffer must be a direct ByteBuffer");
        }
        if (bufflen == null || bufflen.length < 1) {
            throw new IllegalArgumentException("bufflen must be an array of length 1");
        }

        try (var arena = Arena.ofConfined()) {
            final var pMatchData = MemorySegment.ofAddress(matchData);
            final var pszName = arena.allocateUtf8String(name);
            final var pBuffer = MemorySegment.ofBuffer(buffer);
            final var pBuffLen = arena.allocateArray(ValueLayout.JAVA_LONG, 1);
            pBuffLen.set(ValueLayout.JAVA_LONG, 0, bufflen[0]);

            final var result = (int) pcre2_substring_copy_byname.invokeExact(
                    pMatchData,
                    pszName,
                    pBuffer,
                    pBuffLen
            );

            bufflen[0] = pBuffLen.get(ValueLayout.JAVA_LONG, 0);

            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int substringLengthByName(long matchData, String name, long[] length) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }

        try (var arena = Arena.ofConfined()) {
            final var pMatchData = MemorySegment.ofAddress(matchData);
            final var pszName = arena.allocateUtf8String(name);

            if (length == null) {
                return (int) pcre2_substring_length_byname.invokeExact(
                        pMatchData,
                        pszName,
                        MemorySegment.NULL
                );
            }

            if (length.length < 1) {
                throw new IllegalArgumentException("length must be an array of length 1");
            }

            final var pLength = arena.allocateArray(ValueLayout.JAVA_LONG, 1);

            final var result = (int) pcre2_substring_length_byname.invokeExact(
                    pMatchData,
                    pszName,
                    pLength
            );

            length[0] = pLength.get(ValueLayout.JAVA_LONG, 0);
            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int substringLengthByNumber(long matchData, int number, long[] length) {
        final var pMatchData = MemorySegment.ofAddress(matchData);

        if (length == null) {
            try {
                return (int) pcre2_substring_length_bynumber.invokeExact(
                        pMatchData,
                        number,
                        MemorySegment.NULL
                );
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        if (length.length < 1) {
            throw new IllegalArgumentException("length must be an array of length 1");
        }

        try (var arena = Arena.ofConfined()) {
            final var pLength = arena.allocateArray(ValueLayout.JAVA_LONG, 1);

            final var result = (int) pcre2_substring_length_bynumber.invokeExact(
                    pMatchData,
                    number,
                    pLength
            );

            length[0] = pLength.get(ValueLayout.JAVA_LONG, 0);
            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void substringFree(long buffer) {
        try {
            final var pBuffer = MemorySegment.ofAddress(buffer);

            pcre2_substring_free.invokeExact(
                    pBuffer
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int substringListGet(long matchData, long[] listptr, long[] lengthsptr) {
        if (listptr == null) {
            throw new IllegalArgumentException("listptr must not be null");
        }
        if (listptr.length < 1) {
            throw new IllegalArgumentException("listptr must be an array of length 1");
        }
        if (lengthsptr != null && lengthsptr.length < 1) {
            throw new IllegalArgumentException("lengthsptr must be an array of length 1 or null");
        }

        try (var arena = Arena.ofConfined()) {
            final var pMatchData = MemorySegment.ofAddress(matchData);
            final var pListPtr = arena.allocateArray(ValueLayout.ADDRESS, 1);
            final var pLengthsPtr = lengthsptr != null
                    ? arena.allocateArray(ValueLayout.ADDRESS, 1)
                    : MemorySegment.NULL;

            final var result = (int) pcre2_substring_list_get.invokeExact(
                    pMatchData,
                    pListPtr,
                    pLengthsPtr
            );

            listptr[0] = pListPtr.get(ValueLayout.ADDRESS, 0).address();
            if (lengthsptr != null) {
                lengthsptr[0] = pLengthsPtr.get(ValueLayout.ADDRESS, 0).address();
            }

            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void substringListFree(long list) {
        try {
            final var pList = MemorySegment.ofAddress(list);

            pcre2_substring_list_free.invokeExact(
                    pList
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int substringNumberFromName(long code, String name) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }

        try (var arena = Arena.ofConfined()) {
            final var pCode = MemorySegment.ofAddress(code);
            final var pszName = arena.allocateUtf8String(name);

            return (int) pcre2_substring_number_from_name.invokeExact(
                    pCode,
                    pszName
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int substringNametableScan(long code, String name, long[] first, long[] last) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }

        try (var arena = Arena.ofConfined()) {
            final var pCode = MemorySegment.ofAddress(code);
            final var pszName = arena.allocateUtf8String(name);

            final var pFirst = first != null
                    ? arena.allocate(ValueLayout.ADDRESS)
                    : MemorySegment.NULL;
            final var pLast = last != null
                    ? arena.allocate(ValueLayout.ADDRESS)
                    : MemorySegment.NULL;

            final var result = (int) pcre2_substring_nametable_scan.invokeExact(
                    pCode,
                    pszName,
                    pFirst,
                    pLast
            );

            if (result >= 0) {
                if (first != null) {
                    first[0] = pFirst.get(ValueLayout.ADDRESS, 0).address();
                }
                if (last != null) {
                    last[0] = pLast.get(ValueLayout.ADDRESS, 0).address();
                }
            }

            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] readBytes(long pointer, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length must not be negative");
        }
        if (length == 0) {
            return new byte[0];
        }

        final var segment = MemorySegment.ofAddress(pointer).reinterpret(length);
        return segment.toArray(ValueLayout.JAVA_BYTE);
    }

    @Override
    public int serializeEncode(long[] codes, int numberOfCodes, long[] serializedBytes, long[] serializedSize,
            long gcontext) {
        if (codes == null) {
            throw new IllegalArgumentException("codes must not be null");
        }
        if (numberOfCodes < 1) {
            throw new IllegalArgumentException("numberOfCodes must be positive");
        }
        if (codes.length < numberOfCodes) {
            throw new IllegalArgumentException("codes array length must be at least numberOfCodes");
        }
        if (serializedBytes == null || serializedBytes.length < 1) {
            throw new IllegalArgumentException("serializedBytes must be an array of length 1");
        }
        if (serializedSize == null || serializedSize.length < 1) {
            throw new IllegalArgumentException("serializedSize must be an array of length 1");
        }

        try (var arena = Arena.ofConfined()) {
            // Create an array of pointers for the codes
            final var pCodes = arena.allocateArray(ValueLayout.ADDRESS, numberOfCodes);
            for (int i = 0; i < numberOfCodes; i++) {
                pCodes.setAtIndex(ValueLayout.ADDRESS, i, MemorySegment.ofAddress(codes[i]));
            }

            final var pSerializedBytes = arena.allocate(ValueLayout.ADDRESS);
            final var pSerializedSize = arena.allocate(ValueLayout.JAVA_LONG);
            final var pGContext = MemorySegment.ofAddress(gcontext);

            final var result = (int) pcre2_serialize_encode.invokeExact(
                    pCodes,
                    numberOfCodes,
                    pSerializedBytes,
                    pSerializedSize,
                    pGContext
            );

            if (result > 0) {
                serializedBytes[0] = pSerializedBytes.get(ValueLayout.ADDRESS, 0).address();
                serializedSize[0] = pSerializedSize.get(ValueLayout.JAVA_LONG, 0);
            }

            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int serializeDecode(long[] codes, int numberOfCodes, byte[] bytes, long gcontext) {
        if (codes == null) {
            throw new IllegalArgumentException("codes must not be null");
        }
        if (numberOfCodes < 1) {
            throw new IllegalArgumentException("numberOfCodes must be positive");
        }
        if (codes.length < numberOfCodes) {
            throw new IllegalArgumentException("codes array length must be at least numberOfCodes");
        }
        if (bytes == null) {
            throw new IllegalArgumentException("bytes must not be null");
        }

        try (var arena = Arena.ofConfined()) {
            // Allocate memory for the output array of pointers
            final var pCodes = arena.allocateArray(ValueLayout.ADDRESS, numberOfCodes);

            // Copy the input bytes to native memory
            final var pBytes = arena.allocateArray(ValueLayout.JAVA_BYTE, bytes.length);
            pBytes.copyFrom(MemorySegment.ofArray(bytes));

            final var pGContext = MemorySegment.ofAddress(gcontext);

            final var result = (int) pcre2_serialize_decode.invokeExact(
                    pCodes,
                    numberOfCodes,
                    pBytes,
                    pGContext
            );

            if (result > 0) {
                // Copy the decoded pattern handles to the output array
                for (int i = 0; i < result; i++) {
                    codes[i] = pCodes.getAtIndex(ValueLayout.ADDRESS, i).address();
                }
            }

            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void serializeFree(long bytes) {
        if (bytes == 0) {
            return;
        }

        try {
            final var pBytes = MemorySegment.ofAddress(bytes);

            pcre2_serialize_free.invokeExact(
                    pBytes
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int serializeGetNumberOfCodes(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes must not be null");
        }

        try (var arena = Arena.ofConfined()) {
            final var pBytes = arena.allocateArray(ValueLayout.JAVA_BYTE, bytes.length);
            pBytes.copyFrom(MemorySegment.ofArray(bytes));

            return (int) pcre2_serialize_get_number_of_codes.invokeExact(
                    pBytes
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
