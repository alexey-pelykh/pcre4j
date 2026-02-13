# ADR-0007: ReDoS Protection via Match Limits

## Status

Accepted

## Context

Regular Expression Denial of Service (ReDoS) occurs when a crafted input causes a regex engine to
backtrack exponentially, consuming CPU time and memory. PCRE2 provides three resource limits that
can be set on a match context to terminate runaway matches early:

- **Match limit**: Caps the total number of calls to the internal `match()` function during
  backtracking.
- **Depth limit**: Caps the maximum recursion/backtracking depth.
- **Heap limit**: Caps the amount of heap memory (in kibibytes) that PCRE2 may allocate during
  a match.

Without explicit limits, PCRE2 uses compile-time defaults that may be too generous for
security-sensitive applications.

## Decision

Resource limits are exposed at two levels:

**Mid-level API (`lib` module)**: `Pcre2MatchContext` provides `setMatchLimit(int)`,
`setDepthLimit(int)`, and `setHeapLimit(int)` methods that delegate directly to the corresponding
PCRE2 API calls. This gives callers fine-grained, per-match-context control over limits.

**High-level API (`regex` module)**: The `Matcher` class reads default limits from system
properties at match context creation time:

- `pcre2.regex.match.limit` — maximum number of match function calls
- `pcre2.regex.depth.limit` — maximum backtracking depth
- `pcre2.regex.heap.limit` — maximum heap memory in kibibytes

When any limit is exceeded, PCRE2 returns a specific error code. The `regex` module translates
these into a `MatchLimitException` (a `RuntimeException`) that carries the PCRE2 error code,
allowing callers to distinguish which limit was exceeded via `getErrorCode()`:

- `IPcre2.ERROR_MATCHLIMIT`
- `IPcre2.ERROR_DEPTHLIMIT`
- `IPcre2.ERROR_HEAPLIMIT`

System properties are optional. When not set, PCRE2's compiled-in defaults apply.

## Consequences

- Applications can protect against ReDoS at the JVM level by setting system properties, without
  any code changes.
- The mid-level API allows per-context limit tuning for applications that need different limits for
  different patterns or use cases.
- `MatchLimitException` is an unchecked exception, consistent with `java.util.regex`'s approach of
  using unchecked exceptions for regex errors. Callers who need to handle limit violations can
  catch it; others get a clear error message.
- The system property approach means limits are set globally for the `regex` module. Applications
  needing per-pattern limits should use the mid-level `Pcre2MatchContext` API directly.
- When limits are set too aggressively, legitimate complex patterns may fail to match. The error
  code in `MatchLimitException` helps diagnose which limit needs adjustment.
