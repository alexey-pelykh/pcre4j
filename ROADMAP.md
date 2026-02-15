# Roadmap

PCRE4J is currently at **v0.7.x** (pre-1.0). This document describes what the project provides
today and what remains for a stable 1.0 release.

## Current State

The 0.x release series has built out the core functionality:

- **Three API layers**: low-level (`api`), mid-level (`lib`), and `java.util.regex`-compatible
  (`regex`)
- **Two backends**: JNA and FFM (Foreign Function & Memory API)
- **Platform-specific native library bundles** for Linux, macOS, and Windows
- **GraalVM native-image** support
- **ServiceLoader-based backend discovery** for zero-configuration setup
- **High-level API coverage**: pattern serialization, DFA matching, callout support, glob/POSIX
  conversion
- **Thread-scoped backend API** for multi-backend support (`Pcre4j.withBackend()`)
- **JPMS module descriptors** across all modules
- **Architecture Decision Records** documenting key design choices

## v1.0

The first stable release, with an API stability commitment.

Remaining work is tracked in the
[v1.0 milestone](https://github.com/alexey-pelykh/pcre4j/milestone/5).
