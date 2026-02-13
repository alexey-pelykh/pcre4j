# ADR-0006: Three-Pattern Compilation in Regex Module

## Status

Accepted

## Context

The `regex` module provides a `java.util.regex`-compatible API (`Pattern` and `Matcher`). The
`java.util.regex.Matcher` class defines three matching operations with distinct semantics:

- **`find()`**: Searches for the next occurrence of the pattern anywhere in the input.
- **`matches()`**: Tests whether the entire input matches the pattern.
- **`lookingAt()`**: Tests whether the input matches the pattern starting from the beginning.

PCRE2 does not have direct equivalents for `matches()` and `lookingAt()`. It provides compile-time
options `ANCHORED` (pattern must match at the start) and `ENDANCHORED` (match must extend to the
end of the subject) that can be combined to achieve the same effect. However, these options must be
set at pattern compile time, not at match time.

When JIT compilation is enabled, PCRE2 compiles a pattern into optimized machine code for the
specific combination of options it was compiled with. A pattern compiled without `ANCHORED` cannot
benefit from JIT when used for anchored matching at match time — the JIT code path is bypassed
entirely.

## Decision

The `Pattern` class maintains up to three independently compiled `Pcre2Code` instances:

1. **`code`** (primary): Compiled during construction with the user-specified options. Used for
   `find()` operations.
2. **`matchingCode`**: Compiled lazily with the original options plus `ANCHORED` and `ENDANCHORED`.
   Used for `matches()` operations. Only created when JIT is enabled.
3. **`lookingAtCode`**: Compiled lazily with the original options plus `ANCHORED`. Used for
   `lookingAt()` operations. Only created when JIT is enabled.

Lazy compilation uses double-checked locking with `volatile` fields to ensure thread safety without
synchronizing on every access.

When JIT is not enabled, the specialized patterns are not created. Instead, `matches()` and
`lookingAt()` pass the anchoring options at match time via `Pcre2MatchOption`, which is sufficient
for the non-JIT interpreter path.

## Consequences

- `matches()` and `lookingAt()` achieve full JIT optimization by using patterns compiled with the
  correct anchoring options upfront.
- Memory overhead is deferred — the specialized patterns are only compiled when first needed.
  Applications that only use `find()` never allocate the extra patterns.
- Each `Pattern` instance may hold up to three native PCRE2 compiled patterns, tripling the native
  memory cost in the worst case. In practice, the compiled pattern size is small relative to
  application memory.
- The lazy compilation pattern adds some code complexity but is a well-understood Java idiom.
- Non-JIT mode avoids the extra compilations entirely by passing options at match time.
