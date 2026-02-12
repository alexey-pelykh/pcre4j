# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/),
and this project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

## [0.7.0] - 2026-02-02

### Added

- regex: CANON_EQ flag support ([#201](https://github.com/alexey-pelykh/pcre4j/pull/201))
- regex: UNICODE_CASE flag support ([#200](https://github.com/alexey-pelykh/pcre4j/pull/200))
- Automatic PCRE2 library discovery fallback ([#205](https://github.com/alexey-pelykh/pcre4j/pull/205))

### Fixed

- docs: correct variable name in Low-Level Usage example ([#199](https://github.com/alexey-pelykh/pcre4j/pull/199))

## [0.6.0] - 2026-01-29

### Added

- regex: hitEnd() and requireEnd() support ([#198](https://github.com/alexey-pelykh/pcre4j/pull/198))
- regex: transparent bounds support ([#195](https://github.com/alexey-pelykh/pcre4j/pull/195))
- regex: anchoring bounds support ([#192](https://github.com/alexey-pelykh/pcre4j/pull/192))
- regex: COMMENTS flag support ([#191](https://github.com/alexey-pelykh/pcre4j/pull/191))
- regex: Matcher.results() ([#190](https://github.com/alexey-pelykh/pcre4j/pull/190))
- regex: Pattern.quote(String) ([#127](https://github.com/alexey-pelykh/pcre4j/pull/127))
- ffm: MRJAR for Java 21-25 support ([#197](https://github.com/alexey-pelykh/pcre4j/pull/197))
- ffm: update to Java 22 finalized FFM API ([#194](https://github.com/alexey-pelykh/pcre4j/pull/194))
- Parameterized UTF width support for JNA and FFM backends ([#187](https://github.com/alexey-pelykh/pcre4j/pull/187))
- PCRE2 version matrix to CI ([#185](https://github.com/alexey-pelykh/pcre4j/pull/185))
- pcre2_set_glob_separator API binding ([#182](https://github.com/alexey-pelykh/pcre4j/pull/182))
- pcre2_set_glob_escape API binding ([#181](https://github.com/alexey-pelykh/pcre4j/pull/181))
- pcre2_pattern_convert and pcre2_converted_pattern_free API bindings ([#180](https://github.com/alexey-pelykh/pcre4j/pull/180))
- pcre2_convert_context_create/copy/free API bindings ([ac5fa0c](https://github.com/alexey-pelykh/pcre4j/commit/ac5fa0c))
- pcre2_jit_free_unused_memory API binding ([#179](https://github.com/alexey-pelykh/pcre4j/pull/179))
- pcre2_code_copy_with_tables API binding ([#178](https://github.com/alexey-pelykh/pcre4j/pull/178))
- pcre2_set_character_tables API binding ([#177](https://github.com/alexey-pelykh/pcre4j/pull/177))
- pcre2_maketables and pcre2_maketables_free API bindings ([#176](https://github.com/alexey-pelykh/pcre4j/pull/176))
- pcre2_callout_enumerate API binding ([#174](https://github.com/alexey-pelykh/pcre4j/pull/174))
- pcre2_set_callout API binding ([#173](https://github.com/alexey-pelykh/pcre4j/pull/173))
- pcre2_serialize_get_number_of_codes API binding ([#172](https://github.com/alexey-pelykh/pcre4j/pull/172))
- pcre2_serialize_free API binding ([#171](https://github.com/alexey-pelykh/pcre4j/pull/171))
- pcre2_serialize_decode API binding ([#170](https://github.com/alexey-pelykh/pcre4j/pull/170))
- pcre2_serialize_encode API binding ([#169](https://github.com/alexey-pelykh/pcre4j/pull/169))
- pcre2_code_copy API binding ([#168](https://github.com/alexey-pelykh/pcre4j/pull/168))
- pcre2_get_match_data_size API binding ([#167](https://github.com/alexey-pelykh/pcre4j/pull/167))
- pcre2_get_mark API binding ([#163](https://github.com/alexey-pelykh/pcre4j/pull/163))
- pcre2_get_startchar API binding ([#162](https://github.com/alexey-pelykh/pcre4j/pull/162))
- pcre2_set_compile_extra_options API binding ([#160](https://github.com/alexey-pelykh/pcre4j/pull/160))
- pcre2_set_max_pattern_length API binding ([#159](https://github.com/alexey-pelykh/pcre4j/pull/159))
- pcre2_set_parens_nest_limit API binding ([#158](https://github.com/alexey-pelykh/pcre4j/pull/158))
- pcre2_set_bsr API binding ([#157](https://github.com/alexey-pelykh/pcre4j/pull/157))
- pcre2_dfa_match API binding ([#156](https://github.com/alexey-pelykh/pcre4j/pull/156))
- pcre2_substring_nametable_scan API binding ([#126](https://github.com/alexey-pelykh/pcre4j/pull/126))
- pcre2_substring_list_get/free API binding ([#125](https://github.com/alexey-pelykh/pcre4j/pull/125))
- pcre2_substring_length_byname API binding ([#124](https://github.com/alexey-pelykh/pcre4j/pull/124))
- pcre2_substring_length_bynumber API binding ([#123](https://github.com/alexey-pelykh/pcre4j/pull/123))
- pcre2_substring_copy_byname API binding ([#122](https://github.com/alexey-pelykh/pcre4j/pull/122))
- pcre2_substring_copy_bynumber API binding ([#111](https://github.com/alexey-pelykh/pcre4j/pull/111))
- pcre2_set_offset_limit API binding ([#103](https://github.com/alexey-pelykh/pcre4j/pull/103))
- pcre2_set_heap_limit API binding ([#101](https://github.com/alexey-pelykh/pcre4j/pull/101))
- pcre2_set_depth_limit API binding ([#100](https://github.com/alexey-pelykh/pcre4j/pull/100))
- pcre2_set_match_limit API binding ([#99](https://github.com/alexey-pelykh/pcre4j/pull/99))

### Fixed

- Remove dead code in limit getters ([#104](https://github.com/alexey-pelykh/pcre4j/pull/104))
- Skip snapshot publishing for fork PRs ([#189](https://github.com/alexey-pelykh/pcre4j/pull/189))

## [0.5.0] - 2026-01-19

### Added

- regex: replacement APIs (replaceFirst, replaceAll, appendReplacement, appendTail) ([#98](https://github.com/alexey-pelykh/pcre4j/pull/98))
- pcre2_substitute API binding ([#93](https://github.com/alexey-pelykh/pcre4j/pull/93))
- pcre2_substring_get_bynumber and pcre2_substring_free API bindings ([#94](https://github.com/alexey-pelykh/pcre4j/pull/94))
- pcre2_substring_get_byname API binding ([#95](https://github.com/alexey-pelykh/pcre4j/pull/95))
- pcre2_substring_number_from_name API binding ([#96](https://github.com/alexey-pelykh/pcre4j/pull/96))

## [0.4.4] - 2026-01-18

> **Note:** The 18-month gap between 0.4.3 and 0.4.4 reflects a period where the project was on hold.
> Development resumed in January 2026 with renewed focus on `java.util.regex` API compatibility and
> expanded PCRE2 API coverage.

### Fixed

- Allow matching empty strings ([#68](https://github.com/alexey-pelykh/pcre4j/pull/68))
- lib: matchLimit() uses correct INFO_MATCHLIMIT constant ([#70](https://github.com/alexey-pelykh/pcre4j/pull/70))

## [0.4.3] - 2024-06-28

### Fixed

- ffm: mimic loadLibrary() check ([#56](https://github.com/alexey-pelykh/pcre4j/pull/56))
- lib: getPatternSizeInfo() ([#58](https://github.com/alexey-pelykh/pcre4j/pull/58))
- Revert "regex: Pcre2JitStack only for Pcre2JitCode" ([#57](https://github.com/alexey-pelykh/pcre4j/pull/57))

## [0.4.2] - 2024-06-26

### Fixed

- jna: use Pointer for size_t ([#52](https://github.com/alexey-pelykh/pcre4j/pull/52))
- regex: Pcre2JitStack only for Pcre2JitCode ([#53](https://github.com/alexey-pelykh/pcre4j/pull/53))

## [0.4.1] - 2024-06-26

### Added

- regex: pcre2.regex.jit flag ([#50](https://github.com/alexey-pelykh/pcre4j/pull/50))

### Fixed

- regex: JIT stack only for JIT code ([#49](https://github.com/alexey-pelykh/pcre4j/pull/49))

## [0.4.0] - 2024-06-25

### Added

- JIT stack support ([#47](https://github.com/alexey-pelykh/pcre4j/pull/47))

## [0.3.0] - 2024-06-25

### Added

- regex: Pattern.UNICODE_CHARACTER_CLASS ([#43](https://github.com/alexey-pelykh/pcre4j/pull/43))
- regex: Pattern.UNIX_LINES ([#44](https://github.com/alexey-pelykh/pcre4j/pull/44))

## [0.2.1] - 2024-06-24

### Fixed

- lib: positive look-around ([#37](https://github.com/alexey-pelykh/pcre4j/pull/37))
- Unmatched groups ([#38](https://github.com/alexey-pelykh/pcre4j/pull/38))

## [0.2.0] - 2024-06-23

### Added

- pcre2_config API binding ([#28](https://github.com/alexey-pelykh/pcre4j/pull/28))
- pcre2.library.name and pcre2.function.suffix configuration ([#35](https://github.com/alexey-pelykh/pcre4j/pull/35))
- JIT compilation support ([#29](https://github.com/alexey-pelykh/pcre4j/pull/29))
- Pass api reference directly ([#36](https://github.com/alexey-pelykh/pcre4j/pull/36))

### Fixed

- lib: Pcre2Code.match() char-index to byte offset conversion ([#31](https://github.com/alexey-pelykh/pcre4j/pull/31))
- lib: get compiled-widths and check for UTF-8 ([#33](https://github.com/alexey-pelykh/pcre4j/pull/33))

## [0.1.2] - 2024-06-22

### Fixed

- lib: unmatched groups ([#27](https://github.com/alexey-pelykh/pcre4j/pull/27))

## [0.1.1] - 2024-06-22

### Fixed

- Unicode handling ([#22](https://github.com/alexey-pelykh/pcre4j/pull/22))
- 1/2/3/4-byte unicode support ([#24](https://github.com/alexey-pelykh/pcre4j/pull/24))

## [0.1.0] - 2024-06-21

### Added

- java.util.regex-alike minimum viable API ([#6](https://github.com/alexey-pelykh/pcre4j/pull/6))
- regex: Pattern.split() ([#10](https://github.com/alexey-pelykh/pcre4j/pull/10))
- regex: Matcher.lookingAt() ([#13](https://github.com/alexey-pelykh/pcre4j/pull/13))
- regex: Matcher.toMatchResult() ([#14](https://github.com/alexey-pelykh/pcre4j/pull/14))
- lib: expose api() and handle() ([#11](https://github.com/alexey-pelykh/pcre4j/pull/11))

### Fixed

- jna: patternInfo for ByteBuffer ([#3](https://github.com/alexey-pelykh/pcre4j/pull/3))
- build.gradle.kts: implementation to api dependency ([#4](https://github.com/alexey-pelykh/pcre4j/pull/4))
- Missing OSSRH credentials ([#9](https://github.com/alexey-pelykh/pcre4j/pull/9))

## [0.0.0] - 2024-06-19

### Added

- Minimum viable implementation with JNA and FFM backends ([#1](https://github.com/alexey-pelykh/pcre4j/pull/1))

[Unreleased]: https://github.com/alexey-pelykh/pcre4j/compare/0.7.0...HEAD
[0.7.0]: https://github.com/alexey-pelykh/pcre4j/compare/0.6.0...0.7.0
[0.6.0]: https://github.com/alexey-pelykh/pcre4j/compare/0.5.0...0.6.0
[0.5.0]: https://github.com/alexey-pelykh/pcre4j/compare/0.4.4...0.5.0
[0.4.4]: https://github.com/alexey-pelykh/pcre4j/compare/0.4.3...0.4.4
[0.4.3]: https://github.com/alexey-pelykh/pcre4j/compare/0.4.2...0.4.3
[0.4.2]: https://github.com/alexey-pelykh/pcre4j/compare/0.4.1...0.4.2
[0.4.1]: https://github.com/alexey-pelykh/pcre4j/compare/0.4.0...0.4.1
[0.4.0]: https://github.com/alexey-pelykh/pcre4j/compare/0.3.0...0.4.0
[0.3.0]: https://github.com/alexey-pelykh/pcre4j/compare/0.2.1...0.3.0
[0.2.1]: https://github.com/alexey-pelykh/pcre4j/compare/0.2.0...0.2.1
[0.2.0]: https://github.com/alexey-pelykh/pcre4j/compare/0.1.2...0.2.0
[0.1.2]: https://github.com/alexey-pelykh/pcre4j/compare/0.1.1...0.1.2
[0.1.1]: https://github.com/alexey-pelykh/pcre4j/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/alexey-pelykh/pcre4j/compare/0.0.0...0.1.0
[0.0.0]: https://github.com/alexey-pelykh/pcre4j/commits/0.0.0
