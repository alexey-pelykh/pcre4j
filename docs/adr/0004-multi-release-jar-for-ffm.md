# ADR-0004: Multi-Release JAR for FFM Preview/GA

## Status

Accepted

## Context

The Foreign Function & Memory (FFM) API is the preferred approach for calling native code from Java
without JNI. However, the FFM API went through breaking changes between Java versions:

- **Java 21 (LTS)**: FFM is a third preview (JEP 442). Methods like `Arena.allocateArray()` and
  `MemorySegment.getUtf8String()` are used for allocation and string handling. Code must be compiled
  and run with `--enable-preview`.
- **Java 22+**: FFM is finalized (JEP 454). The API changed: `Arena.allocate()` replaces
  `allocateArray()`, `Arena.allocateFrom()` replaces `allocateArray()` for initialized data, and
  `MemorySegment.getString()` replaces `getUtf8String()`.

PCRE4J targets Java 21 as its minimum version (current LTS), but must also work correctly on
Java 22+ without requiring `--enable-preview`.

## Decision

We use a **Multi-Release JAR** (JEP 238) for the `ffm` module:

- **Base (Java 21)**: The main source set contains `Pcre2.java` (the full `IPcre2` implementation
  using FFM `MethodHandle` invocations) and `ArenaHelper.java` (a thin adapter that wraps the
  preview API methods). Compiled with `--enable-preview`.
- **Java 22 overlay**: A separate `java22` source set at `src/main/java22/` contains only
  `ArenaHelper.java`, rewritten to use the finalized FFM API. Compiled with a Java 22 toolchain
  and no preview flags. Packaged into `META-INF/versions/22/` in the JAR.
- **Shared logic**: `Pcre2.java` is identical across both versions. All version-specific API
  differences are isolated in `ArenaHelper`, which provides static methods for allocation and
  string operations. The Java 22 source set compiles against a copy of `Pcre2.java` from the main
  source to satisfy compile-time references.

The JAR manifest includes `Multi-Release: true`, and the JVM automatically selects the Java 22
`ArenaHelper` when running on Java 22+.

Testing runs on both Java versions:
- The default `test` task runs with Java 21 and `--enable-preview`.
- A separate `testJava22` task uses a Java 22 toolchain and runs against the packaged MRJAR to
  verify that the Java 22 overlay is selected correctly.

## Consequences

- A single `pcre4j-ffm` artifact works on both Java 21 (preview) and Java 22+ (GA) without any
  user configuration.
- The API difference surface is small — five methods in `ArenaHelper` — making the MRJAR overlay
  easy to maintain.
- `Pcre2.java` is never duplicated; only the thin `ArenaHelper` adapter varies.
- Users on Java 21 must add `--enable-preview` to their JVM flags; users on Java 22+ do not.
- When Java 21 reaches end of life, the base source set can be updated to the GA API and the
  `java22` overlay removed.
