# ADR-0009: Thread-Scoped Backend API

## Status

Accepted

## Context

ADR-0002 established the two-tier backend system: a global singleton (`Pcre4j.setup()` / `Pcre4j.api()`)
for convenience, and explicit-API overloads (`Pcre2Code(IPcre2, ...)`) for flexibility. This works well
for applications that use a single backend, but leaves a usability gap for multi-backend scenarios:

- **Performance comparison**: Running JNA and FFM side-by-side in benchmarks.
- **Fallback strategy**: Trying FFM, falling back to JNA if unavailable.
- **Testing**: Verifying application code against multiple backends.
- **Library authors**: Libraries using PCRE4J should not interfere with the application's backend choice.

The explicit-API overloads handle all of these, but they require threading an `IPcre2` reference
through every call site — significant boilerplate when many PCRE4J objects are created within a scope.

## Decision

We add a **thread-scoped backend** tier between the global singleton and the explicit-API overloads,
using `ThreadLocal`:

- `Pcre4j.withBackend(IPcre2)` sets a thread-local backend override and returns an `AutoCloseable`
  that restores the previous state when closed.
- `Pcre4j.api()` checks the thread-local first, then falls through to the global backend.
- Scopes can be nested; each close restores exactly the state that existed before the corresponding
  `withBackend` call.

The resolution order becomes:

1. Thread-scoped backend (via `withBackend`) — if set for the current thread.
2. Global backend (via `setup` or auto-discovery) — the existing behavior.
3. Explicit-API overloads — bypass all resolution, unchanged.

### Why ThreadLocal

- **Try-with-resources**: Natural fit for Java's resource management pattern.
- **Nesting**: Handled automatically by saving/restoring previous values.
- **No API breakage**: Existing code using `Pcre4j.api()` or `Pcre4j.setup()` is completely
  unaffected.
- **Virtual threads**: Each virtual thread has its own `ThreadLocal` state. Scoped backends work
  correctly with virtual threads (the scope is per-virtual-thread, not per-carrier-thread).

### Alternatives Considered

- **Backend Registry** (name → backend mapping): Less ergonomic, introduces naming concerns, and
  doesn't compose as naturally with try-with-resources.
- **Context Object** (explicit context parameter): Essentially what the explicit-API overloads
  already provide. Adding another explicit parameter mechanism doesn't reduce boilerplate.
- **ScopedValue** (Java 25 GA): More appropriate long-term, but not yet GA in Java 21 (the project's
  minimum version). `ThreadLocal` can be replaced with `ScopedValue` in a future major version.

## Consequences

- Applications can temporarily override the backend with minimal boilerplate:
  ```java
  try (var scope = Pcre4j.withBackend(ffmBackend)) {
      // all PCRE4J operations here use ffmBackend
  }
  ```
- The three tiers (thread-scoped, global, explicit) provide a clear escalation path: use the
  simplest tier that meets your needs.
- If `withBackend` is used without try-with-resources, the thread-local persists until the thread
  dies. The `AutoCloseable` pattern makes correct usage the natural path.
- The scoped backend is not inherited by child threads. For structured concurrency scenarios, the
  explicit-API overloads remain the correct choice.
- Performance impact is negligible: `ThreadLocal.get()` adds ~1ns per `api()` call, which is
  insignificant compared to native PCRE2 operations.
