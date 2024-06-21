# PCRE2 API

Here's the list of the PCRE2 API functions exposed via `org.pcre4j.api.IPcre2` and the backends:

| ✅ | API | Description |
| -- | --- | ----------- |
| [ ] | [pcre2_callout_enumerate](https://www.pcre.org/current/doc/html/pcre2_callout_enumerate.html) | Enumerate callouts in a compiled pattern |
| [ ] | [pcre2_code_copy](https://www.pcre.org/current/doc/html/pcre2_code_copy.html) | Copy a compiled pattern |
| [ ] | [pcre2_code_copy_with_tables](https://www.pcre.org/current/doc/html/pcre2_code_copy_with_tables.html) | Copy a compiled pattern and its character tables |
| [ ] | [pcre2_code_free](https://www.pcre.org/current/doc/html/pcre2_code_free.html) | Free a compiled pattern |
| [ ] | [pcre2_compile](https://www.pcre.org/current/doc/html/pcre2_compile.html) | Compile a regular expression pattern |
| [ ] | [pcre2_compile_context_copy](https://www.pcre.org/current/doc/html/pcre2_compile_context_copy.html) | Copy a compile context |
| [ ] | [pcre2_compile_context_create](https://www.pcre.org/current/doc/html/pcre2_compile_context_create.html) | Create a compile context |
| [ ] | [pcre2_compile_context_free](https://www.pcre.org/current/doc/html/pcre2_compile_context_free.html) | Free a compile context |
| [ ] | [pcre2_config](https://www.pcre.org/current/doc/html/pcre2_config.html) | Show build-time configuration options |
| [ ] | [pcre2_convert_context_copy](https://www.pcre.org/current/doc/html/pcre2_convert_context_copy.html) | Copy a convert context |
| [ ] | [pcre2_convert_context_create](https://www.pcre.org/current/doc/html/pcre2_convert_context_create.html) | Create a convert context |
| [ ] | [pcre2_convert_context_free](https://www.pcre.org/current/doc/html/pcre2_convert_context_free.html) | Free a convert context |
| [ ] | [pcre2_converted_pattern_free](https://www.pcre.org/current/doc/html/pcre2_converted_pattern_free.html) | Free converted foreign pattern |
| [ ] | [pcre2_dfa_match](https://www.pcre.org/current/doc/html/pcre2_dfa_match.html) | Match a compiled pattern to a subject string (DFA algorithm; not Perl compatible) |
| [ ] | [pcre2_general_context_copy](https://www.pcre.org/current/doc/html/pcre2_general_context_copy.html) | Copy a general context |
| [ ] | [pcre2_general_context_create](https://www.pcre.org/current/doc/html/pcre2_general_context_create.html) | Create a general context |
| [ ] | [pcre2_general_context_free](https://www.pcre.org/current/doc/html/pcre2_general_context_free.html) | Free a general context |
| [ ] | [pcre2_get_error_message](https://www.pcre.org/current/doc/html/pcre2_get_error_message.html) | Get textual error message for error number |
| [ ] | [pcre2_get_mark](https://www.pcre.org/current/doc/html/pcre2_get_mark.html) | Get a (*MARK) name |
| [ ] | [pcre2_get_match_data_size](https://www.pcre.org/current/doc/html/pcre2_get_match_data_size.html) | Get the size of a match data block |
| [ ] | [pcre2_get_ovector_count](https://www.pcre.org/current/doc/html/pcre2_get_ovector_count.html) | Get the ovector count |
| [ ] | [pcre2_get_ovector_pointer](https://www.pcre.org/current/doc/html/pcre2_get_ovector_pointer.html) | Get a pointer to the ovector |
| [ ] | [pcre2_get_startchar](https://www.pcre.org/current/doc/html/pcre2_get_startchar.html) | Get the starting character offset |
| [ ] | [pcre2_jit_compile](https://www.pcre.org/current/doc/html/pcre2_jit_compile.html) | Process a compiled pattern with the JIT compiler |
| [ ] | [pcre2_jit_free_unused_memory](https://www.pcre.org/current/doc/html/pcre2_jit_free_unused_memory.html) | Free unused JIT memory |
| [ ] | [pcre2_jit_match](https://www.pcre.org/current/doc/html/pcre2_jit_match.html) | Fast path interface to JIT matching |
| [ ] | [pcre2_jit_stack_assign](https://www.pcre.org/current/doc/html/pcre2_jit_stack_assign.html) | Assign stack for JIT matching |
| [ ] | [pcre2_jit_stack_create](https://www.pcre.org/current/doc/html/pcre2_jit_stack_create.html) | Create a stack for JIT matching |
| [ ] | [pcre2_jit_stack_free](https://www.pcre.org/current/doc/html/pcre2_jit_stack_free.html) | Free a JIT matching stack |
| [ ] | [pcre2_maketables](https://www.pcre.org/current/doc/html/pcre2_maketables.html) | Build character tables in current locale |
| [ ] | [pcre2_maketables_free](https://www.pcre.org/current/doc/html/pcre2_maketables_free.html) | Free character tables |
| [ ] | [pcre2_match](https://www.pcre.org/current/doc/html/pcre2_match.html) | Match a compiled pattern to a subject string (Perl compatible) |
| [ ] | [pcre2_match_context_copy](https://www.pcre.org/current/doc/html/pcre2_match_context_copy.html) | Copy a match context |
| [ ] | [pcre2_match_context_create](https://www.pcre.org/current/doc/html/pcre2_match_context_create.html) | Create a match context |
| [ ] | [pcre2_match_context_free](https://www.pcre.org/current/doc/html/pcre2_match_context_free.html) | Free a match context |
| [ ] | [pcre2_match_data_create](https://www.pcre.org/current/doc/html/pcre2_match_data_create.html) | Create a match data block |
| [ ] | [pcre2_match_data_create_from_pattern](https://www.pcre.org/current/doc/html/pcre2_match_data_create_from_pattern.html) | Create a match data block getting size from pattern |
| [ ] | [pcre2_match_data_free](https://www.pcre.org/current/doc/html/pcre2_match_data_free.html) | Free a match data block |
| [ ] | [pcre2_pattern_convert](https://www.pcre.org/current/doc/html/pcre2_pattern_convert.html) | Experimental foreign pattern converter |
| [ ] | [pcre2_pattern_info](https://www.pcre.org/current/doc/html/pcre2_pattern_info.html) | Extract information about a pattern |
| [ ] | [pcre2_serialize_decode](https://www.pcre.org/current/doc/html/pcre2_serialize_decode.html) | Decode serialized compiled patterns |
| [ ] | [pcre2_serialize_encode](https://www.pcre.org/current/doc/html/pcre2_serialize_encode.html) | Serialize compiled patterns for save/restore |
| [ ] | [pcre2_serialize_free](https://www.pcre.org/current/doc/html/pcre2_serialize_free.html) | Free serialized compiled patterns |
| [ ] | [pcre2_serialize_get_number_of_codes](https://www.pcre.org/current/doc/html/pcre2_serialize_get_number_of_codes.html) | Get number of serialized compiled patterns |
| [ ] | [pcre2_set_bsr](https://www.pcre.org/current/doc/html/pcre2_set_bsr.html) | Set \R convention |
| [ ] | [pcre2_set_callout](https://www.pcre.org/current/doc/html/pcre2_set_callout.html) | Set up a callout function |
| [ ] | [pcre2_set_character_tables](https://www.pcre.org/current/doc/html/pcre2_set_character_tables.html) | Set character tables |
| [ ] | [pcre2_set_compile_extra_options](https://www.pcre.org/current/doc/html/pcre2_set_compile_extra_options.html) | Set compile time extra options |
| [ ] | [pcre2_set_compile_recursion_guard](https://www.pcre.org/current/doc/html/pcre2_set_compile_recursion_guard.html) | Set up a compile recursion guard function |
| [ ] | [pcre2_set_depth_limit](https://www.pcre.org/current/doc/html/pcre2_set_depth_limit.html) | Set the match backtracking depth limit |
| [ ] | [pcre2_set_glob_escape](https://www.pcre.org/current/doc/html/pcre2_set_glob_escape.html) | Set glob escape character |
| [ ] | [pcre2_set_glob_separator](https://www.pcre.org/current/doc/html/pcre2_set_glob_separator.html) | Set glob separator character |
| [ ] | [pcre2_set_heap_limit](https://www.pcre.org/current/doc/html/pcre2_set_heap_limit.html) | Set the match backtracking heap limit |
| [ ] | [pcre2_set_match_limit](https://www.pcre.org/current/doc/html/pcre2_set_match_limit.html) | Set the match limit |
| [ ] | [pcre2_set_max_pattern_length](https://www.pcre.org/current/doc/html/pcre2_set_max_pattern_length.html) | Set the maximum length of pattern |
| [ ] | [pcre2_set_newline](https://www.pcre.org/current/doc/html/pcre2_set_newline.html) | Set the newline convention |
| [ ] | [pcre2_set_offset_limit](https://www.pcre.org/current/doc/html/pcre2_set_offset_limit.html) | Set the offset limit |
| [ ] | [pcre2_set_parens_nest_limit](https://www.pcre.org/current/doc/html/pcre2_set_parens_nest_limit.html) | Set the parentheses nesting limit |
| [ ] | [pcre2_set_recursion_limit](https://www.pcre.org/current/doc/html/pcre2_set_recursion_limit.html) | Obsolete: use pcre2_set_depth_limit |
| [ ] | [pcre2_set_recursion_memory_management](https://www.pcre.org/current/doc/html/pcre2_set_recursion_memory_management.html) | Obsolete function that (from 10.30 onwards) does nothing |
| [ ] | [pcre2_substitute](https://www.pcre.org/current/doc/html/pcre2_substitute.html) | Match a compiled pattern to a subject string and do substitutions |
| [ ] | [pcre2_substring_copy_byname](https://www.pcre.org/current/doc/html/pcre2_substring_copy_byname.html) | Extract named substring into given buffer |
| [ ] | [pcre2_substring_copy_bynumber](https://www.pcre.org/current/doc/html/pcre2_substring_copy_bynumber.html) | Extract numbered substring into given buffer |
| [ ] | [pcre2_substring_free](https://www.pcre.org/current/doc/html/pcre2_substring_free.html) | Free extracted substring |
| [ ] | [pcre2_substring_get_byname](https://www.pcre.org/current/doc/html/pcre2_substring_get_byname.html) | Extract named substring into new memory |
| [ ] | [pcre2_substring_get_bynumber](https://www.pcre.org/current/doc/html/pcre2_substring_get_bynumber.html) | Extract numbered substring into new memory |
| [ ] | [pcre2_substring_length_byname](https://www.pcre.org/current/doc/html/pcre2_substring_length_byname.html) | Find length of named substring |
| [ ] | [pcre2_substring_length_bynumber](https://www.pcre.org/current/doc/html/pcre2_substring_length_bynumber.html) | Find length of numbered substring |
| [ ] | [pcre2_substring_list_free](https://www.pcre.org/current/doc/html/pcre2_substring_list_free.html) | Free list of extracted substrings |
| [ ] | [pcre2_substring_list_get](https://www.pcre.org/current/doc/html/pcre2_substring_list_get.html) | Extract all substrings into new memory |
| [ ] | [pcre2_substring_nametable_scan](https://www.pcre.org/current/doc/html/pcre2_substring_nametable_scan.html) | Find table entries for given string name |
| [ ] | [pcre2_substring_number_from_name](https://www.pcre.org/current/doc/html/pcre2_substring_number_from_name.html) | Convert captured string name to number |