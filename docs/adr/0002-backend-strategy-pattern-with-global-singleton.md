# ADR-0002: Backend Strategy Pattern with Global Singleton

## Status

Accepted

## Context

PCRE4J needs to support multiple native library backends (JNA and FFM) through a single API. The
`IPcre2` interface in the `api` module defines the backend contract — a set of ~60 methods that map
directly to PCRE2 C functions, operating on opaque `long` handles for native resources.

Applications need a way to select which backend to use. Two competing concerns arise:

1. **Convenience**: Most applications use a single backend throughout their lifecycle. Requiring
   an `IPcre2` parameter on every API call adds unnecessary boilerplate.
2. **Flexibility**: Some environments (tests, multi-classloader containers) need to work with
   multiple backends simultaneously or swap them at runtime.

## Decision

We use a **global singleton with explicit-API escape hatches**:

- `Pcre4j` holds a single `IPcre2` instance, set once via `Pcre4j.setup(IPcre2)` and retrieved via
  `Pcre4j.api()`. Both methods are synchronized.
- Convenience constructors (e.g. `Pcre2Code(String)`, `Pattern.compile(String)`) delegate to
  `Pcre4j.api()` internally.
- Every convenience constructor has an explicit-API overload that accepts an `IPcre2` parameter
  directly (e.g. `Pcre2Code(IPcre2, String)`, `Pattern.compile(IPcre2, String)`), bypassing the
  global singleton entirely.
- Each `Pcre2Code` (and other wrapper objects) captures the `IPcre2` reference at construction
  time, so replacing the global backend later does not affect existing instances.

The singleton validates at setup time that the backend supports UTF-8 via
`Pcre4jUtils.getCompiledWidths()`.

## Consequences

- Applications call `Pcre4j.setup(new org.pcre4j.jna.Pcre2())` once at startup and then use the
  convenience API without passing backend references.
- Calling `Pcre4j.api()` before `setup()` throws `IllegalStateException`, making misconfiguration
  obvious.
- Tests and multi-backend scenarios use the explicit-API overloads and never depend on global state.
- In multi-classloader environments (application servers, OSGi), each classloader gets its own
  `Pcre4j` class and must call `setup()` independently.
- The `api` module has no dependency on `lib` — backends implement `IPcre2` without knowing about
  the singleton.
- [ADR-0009](0009-thread-scoped-backend-api.md) extends this pattern with a thread-scoped tier
  between the global singleton and the explicit-API overloads.
