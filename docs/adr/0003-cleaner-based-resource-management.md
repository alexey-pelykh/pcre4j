# ADR-0003: Cleaner-based Resource Management

## Status

Accepted

## Context

PCRE4J wraps native PCRE2 resources — compiled patterns (`pcre2_code`), match data blocks
(`pcre2_match_data`), and various contexts (`pcre2_compile_context`, `pcre2_match_context`,
`pcre2_general_context`, `pcre2_convert_context`). Each of these is allocated by PCRE2 and must be
explicitly freed via the corresponding `*_free()` function. Failing to free them leaks native memory
that the JVM garbage collector cannot reclaim.

Java offers two standard patterns for deterministic resource cleanup:

1. **`Closeable`/`AutoCloseable` with try-with-resources**: Caller explicitly manages lifecycle.
   Straightforward, but forces every usage site into a try-with-resources block and makes it easy to
   leak resources if the caller forgets to close.
2. **`java.lang.ref.Cleaner`**: GC-driven cleanup. Resources are freed automatically when the
   wrapper object becomes unreachable. No caller action required, but cleanup timing is
   non-deterministic.

## Decision

We use `java.lang.ref.Cleaner` for all native resource wrappers. A single shared `Cleaner` instance
(`Pcre4jCleaner.INSTANCE`) serves the entire library, reducing daemon thread overhead to one thread
total.

Each wrapper class (e.g. `Pcre2Code`, `Pcre2MatchData`, `Pcre2MatchContext`) follows the same
pattern:

1. The constructor allocates the native resource via `IPcre2` and stores the returned `long` handle.
2. It registers a `Cleaner.Cleanable` with a static inner `Clean` record that captures only the
   `IPcre2` reference and the `long` handle — never the wrapper object itself (to avoid preventing
   GC).
3. The `Clean` record implements `Runnable.run()` to call the appropriate `*_free()` method.

The wrapper classes do **not** implement `Closeable` or `AutoCloseable`.

## Consequences

- Callers never need to manage native resource lifecycle explicitly — no try-with-resources blocks
  required for PCRE4J objects.
- The `regex` module's `Pattern` and `Matcher` classes can hold `Pcre2Code` and `Pcre2MatchData`
  references with the same lifecycle semantics as `java.util.regex`, where patterns and matchers are
  not closeable either.
- Native memory cleanup is non-deterministic. Under heavy allocation without GC, native memory
  pressure may build up before the cleaner runs. In practice, PCRE2 resource sizes are small enough
  that this is not a concern.
- All `Clean` records are static inner types that hold only primitive handles and the `IPcre2`
  reference — they never capture a reference to the enclosing wrapper, ensuring the cleaner does not
  prevent garbage collection.
- A single daemon thread handles all cleanup, rather than one per wrapper class.
