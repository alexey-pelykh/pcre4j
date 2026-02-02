# Contributing to PCRE4J

Thank you for your interest in contributing to PCRE4J! This document provides guidelines and instructions for contributing.

## Reporting Bugs

Please use the [bug report template](https://github.com/alexey-pelykh/pcre4j/issues/new?template=bug_report.yml) to report bugs. Include as much detail as possible, including your Java version, operating system, and PCRE2 library version.

## Requesting Features

Use the [feature request template](https://github.com/alexey-pelykh/pcre4j/issues/new?template=feature_request.yml) to suggest new features or enhancements.

## Submitting Pull Requests

1. Fork the repository and create a branch from `main`
2. Make your changes
3. Ensure the build passes (see [Build Instructions](#build-instructions) below)
4. Submit a pull request against `main`

## Developer Certificate of Origin (DCO)

All commits must be signed off to certify that you have the right to submit the contribution under the project's license. Add a `Signed-off-by` line to every commit message:

```
(feat) add new feature

Signed-off-by: Your Name <your.email@example.com>
```

Use `git commit -s` to automatically add this line. If you forget, you can amend the last commit with `git commit --amend -s`.

## Code Conventions

- **Line length**: 120 characters (enforced by Checkstyle)
- **Indentation**: 4 spaces (no tabs)
- **Charset**: UTF-8 with LF line endings
- **Copyright header**: Required on all source files (LGPL-3.0 notice)
- **JavaDoc**: Required on all public APIs

## Commit Message Format

Use the format `(type) brief description`:

- `(feat)` — new feature
- `(fix)` — bug fix
- `(chore)` — maintenance (build, dependencies, CI)
- `(docs)` — documentation changes

Examples:
- `(feat) regex: implement CANON_EQ flag support`
- `(fix) matcher: handle empty region anchor semantics`
- `(chore) gradle: upgrade to 9.3.0`

## Build Instructions

### Prerequisites

PCRE2 must be installed on your system:

- **Ubuntu/Debian**: `sudo apt install libpcre2-8-0`
- **macOS**: `brew install pcre2`
- **Windows**: Download PCRE2 DLL and add to PATH

### Building and Testing

```bash
# Full build with tests
./gradlew build -Dpcre2.library.path=/usr/lib/x86_64-linux-gnu

# macOS (Apple Silicon)
./gradlew build -Dpcre2.library.path=/opt/homebrew/lib

# macOS (Intel)
./gradlew build -Dpcre2.library.path=/usr/local/lib

# Code style check
./gradlew checkstyleMain checkstyleTest
```

### Before Submitting

Ensure the following pass:

- `./gradlew build` — compilation and tests
- `./gradlew checkstyleMain checkstyleTest` — code style

If your changes add new PCRE2 API bindings, update `PCRE2_API.md` to mark the API as implemented.

## License

By contributing to PCRE4J, you agree that your contributions will be licensed under the [GNU Lesser General Public License v3.0](LICENSE).
