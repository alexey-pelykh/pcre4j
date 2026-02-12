# ADR-0001: Test-Time Circular Awareness in lib Module

## Status

Accepted

## Context

PCRE4J uses a layered module architecture:

```
api  ←  lib  ←  jna (backend)
                ffm (backend)
```

At compile time, `lib` depends only on `api`. The backend modules (`jna`, `ffm`) depend on `lib`
at compile time and use `lib`'s test fixtures to run shared contract tests.

However, at **test time**, `lib` needs concrete backend implementations to verify its own
functionality against real PCRE2 operations. This creates a test-time circular awareness:

- `lib` declares `jna` and `ffm` as `testRuntimeOnly` dependencies (`lib/build.gradle.kts`,
  lines 33-34)
- `jna` and `ffm` declare `testFixtures(project(":lib"))` as `testImplementation` dependencies

## Decision

We accept this test-time circular awareness as intentional. The `lib` module loads backends
**reflectively** at test time via `BackendProvider`, which discovers backend classes by name
(`org.pcre4j.jna.Pcre2`, `org.pcre4j.ffm.Pcre2`) using `Class.forName()`. This avoids any
compile-time coupling from `lib` to the backends.

The `testRuntimeOnly` scope ensures that:

1. **No compile-time dependency exists** from `lib` to any backend — the circular awareness is
   strictly a runtime testing concern
2. **Gradle's dependency resolution is satisfied** — `testRuntimeOnly` does not create a true
   circular dependency in the build graph since it does not affect compilation
3. **Contract tests run in both directions** — `lib` tests verify its abstractions work with real
   backends, while backend tests reuse `lib`'s test fixtures to verify backend compliance

## Consequences

- `lib` module tests require both `jna` and `ffm` modules to be built before they can run
- Adding a new backend requires adding it as a `testRuntimeOnly` dependency in `lib` and
  registering it in `BackendProvider`
- The reflective loading in `BackendProvider` means backend class name changes will cause
  runtime test failures rather than compile-time errors
