# Roadmap

This document outlines the planned direction for PCRE4J. Each section corresponds to a
[GitHub Milestone](https://github.com/alexey-pelykh/pcre4j/milestones) where you can track
detailed progress.

## v1.0 — Production Ready

**Focus**: Core stability, code quality, and documentation.

Key items include unified exception handling, comprehensive test coverage, JPMS module
descriptors, build convention plugins, and architecture decision records.

[See milestone](https://github.com/alexey-pelykh/pcre4j/milestone/1)

## v1.1 — Adoption

**Focus**: Zero-friction onboarding for new users.

Key items include platform-specific native library bundles, GraalVM native-image support,
ServiceLoader-based backend discovery, and published performance benchmarks.

[See milestone](https://github.com/alexey-pelykh/pcre4j/milestone/2)

## v1.2 — Feature Completeness

**Focus**: Full PCRE2 feature coverage through the high-level API.

Key items include pattern serialization/deserialization, DFA matching, callout support, and
glob/POSIX pattern conversion.

[See milestone](https://github.com/alexey-pelykh/pcre4j/milestone/3)

## v2.0 — Platform

**Focus**: Architecture evolution.

Key items include a scoped backend API for multi-backend support, enabling applications to use
different PCRE2 backends concurrently without relying on the global singleton.

[See milestone](https://github.com/alexey-pelykh/pcre4j/milestone/4)
