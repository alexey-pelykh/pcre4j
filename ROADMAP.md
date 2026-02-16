# Roadmap

PCRE4J **v1.0** is the first stable release, with an API stability commitment.

## What's Included

- **Three API layers**: low-level (`api`), mid-level (`lib`), and `java.util.regex`-compatible
  (`regex`)
- **Two backends**: JNA and FFM (Foreign Function & Memory API)
- **100% PCRE2 API coverage** across both backends
- **Platform-specific native library bundles** for Linux, macOS, and Windows
- **GraalVM native-image** support
- **ServiceLoader-based backend discovery** for zero-configuration setup
- **High-level API coverage**: pattern serialization, DFA matching, callout support, glob/POSIX
  conversion
- **Thread-scoped backend API** for multi-backend support (`Pcre4j.withBackend()`)
- **JPMS module descriptors** across all modules
- **Built-in ReDoS protection** via configurable match, depth, and heap limits

## Future Direction

Post-1.0 work is tracked in the
[GitHub Issues](https://github.com/alexey-pelykh/pcre4j/issues).
