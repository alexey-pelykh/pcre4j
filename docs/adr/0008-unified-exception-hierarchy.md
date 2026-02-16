# ADR-0008: Unified Exception Hierarchy

## Status

Accepted

## Context

PCRE4J's exception classes lack a common hierarchy. Each error type independently extends
`RuntimeException` or `IllegalArgumentException`, making it impossible to catch "all PCRE4J errors"
with a single catch clause:

```
RuntimeException
├── Pcre2SubstituteError
├── Pcre2NoSubstringError
├── Pcre2NoUniqueSubstringError
├── Pcre2PatternInfoSizeError
└── MatchLimitException (regex module)

IllegalArgumentException
└── Pcre2CompileError
```

Additionally, the naming convention uses `*Error` which in Java conventionally refers to
unrecoverable JVM errors (e.g., `OutOfMemoryError`), not application-level exceptions.

Not all exceptions carry the PCRE2 native error code, preventing programmatic error handling based
on error codes.

## Decision

### Common Base Class

A `Pcre2Exception` base class in the `lib` module extends `RuntimeException` and provides:
- A `message` (inherited from `RuntimeException`)
- An `errorCode` field for the PCRE2 native error code (0 when not applicable)

### Semantic Subcategories

Intermediate classes group exceptions by PCRE2 operation phase:

```
RuntimeException
└── Pcre2Exception (common base)
    ├── Pcre2CompileException (compile-phase errors)
    ├── Pcre2ConvertException (conversion-phase errors)
    ├── Pcre2MatchException (match-phase errors)
    │   └── Pcre2MatchLimitException (resource limit violations)
    ├── Pcre2SubstituteException (substitute-phase errors)
    ├── Pcre2SubstringException (substring/capture group errors)
    │   ├── Pcre2NoSubstringException (named group does not exist)
    │   └── Pcre2NoUniqueSubstringException (named group is not unique)
    └── Pcre2InternalException (unexpected internal errors)
        └── Pcre2PatternInfoSizeException (unexpected pattern info size)
```

### Naming Convention

All exceptions are renamed from `*Error` to `*Exception` to follow Java conventions.

### Compile Exception Lineage

`Pcre2CompileException` extends `Pcre2Exception` (not `IllegalArgumentException`). This is a
breaking change but semantically correct — compile errors are PCRE4J-specific, not generic argument
validation errors.

### Match Limit Migration

`Pcre2MatchLimitException` is created in the `lib` module. The `regex` module's
`MatchLimitException` is updated to extend it, preserving backwards compatibility for existing
catch clauses.

### Backwards Compatibility

Old class names (`Pcre2CompileError`, `Pcre2SubstituteError`, etc.) are kept as deprecated type
aliases that extend the new classes. This provides a one-release-cycle migration path.

### Error Code Access

The base `Pcre2Exception` class provides `errorCode()` returning the PCRE2 native error code (or 0
if not applicable), enabling programmatic error handling across all exception types.

## Consequences

- `catch (Pcre2Exception e)` catches all PCRE4J-specific errors.
- Semantic subcategories allow catching by operation phase (e.g., `catch (Pcre2MatchException e)`).
- The PCRE2 error code is accessible on all exceptions via `errorCode()`.
- **Breaking change**: `Pcre2CompileError` no longer extends `IllegalArgumentException`. Code that
  catches `IllegalArgumentException` expecting compile errors will need to be updated.
- Deprecated aliases ensure existing code that references old class names continues to compile,
  with deprecation warnings guiding migration.
- The `MatchLimitException` in the `regex` module extends `Pcre2MatchLimitException`, so existing
  code catching `MatchLimitException` continues to work, but code can also catch at the broader
  `Pcre2MatchException` or `Pcre2Exception` level.
