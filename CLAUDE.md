# PCRE4J Project Guidelines

## Project Overview

PCRE4J is a Java binding for the PCRE2 (Perl Compatible Regular Expressions 2) library, providing three API layers:
- **High-level**: `java.util.regex`-compatible API (`regex` module)
- **Mid-level**: PCRE4J wrapper API (`lib` module)
- **Low-level**: Direct PCRE2 API access (`api` module)

**License**: LGPL-3.0
**Java Version**: 21 (LTS)

## Module Architecture

```
pcre4j/
├── api/    → IPcre2 interface (backend contract, ~180 PCRE2 constants)
├── lib/    → Core wrapper (Pcre2Code, contexts, enums, utilities)
├── jna/    → JNA backend (Java Native Access implementation)
├── ffm/    → FFM backend (Foreign Functions & Memory API, Java 21+)
├── regex/  → java.util.regex compatibility layer (Pattern, Matcher)
└── test/   → Shared test infrastructure (abstract base tests)
```

**Dependencies**: `api` ← `lib` ← (`jna` | `ffm`) ← `regex`

## Build Commands

```bash
# Full build with tests
./gradlew build -Dpcre2.library.path=/usr/lib/x86_64-linux-gnu

# Run tests only
./gradlew test -Dpcre2.library.path=/usr/lib/x86_64-linux-gnu

# Module-specific tests
./gradlew jna:test ffm:test -Dpcre2.library.path=/usr/lib/x86_64-linux-gnu

# Code style check
./gradlew checkstyleMain checkstyleTest

# Coverage report
./gradlew build jacocoAggregatedTestReport -Dpcre2.library.path=/usr/lib/x86_64-linux-gnu
```

**macOS library path**: `-Dpcre2.library.path=/opt/homebrew/lib` (Apple Silicon) or `/usr/local/lib` (Intel)

## Native Library Requirements

PCRE2 must be installed on the system:
- **Ubuntu/Debian**: `sudo apt install libpcre2-8-0`
- **macOS**: `brew install pcre2`
- **Windows**: Download PCRE2 DLL and add to PATH

Library discovery priority:
1. `pcre2.library.path` system property
2. `jna.library.path` (JNA) / `java.library.path` (FFM)
3. System library path

## Code Conventions

- **Line length**: 120 characters (Checkstyle enforced)
- **Indentation**: 4 spaces
- **Charset**: UTF-8 with LF line endings
- **Copyright header**: Required on all files (LGPL-3.0 notice)
- **JavaDoc**: Required on all public APIs
- **Naming**:
  - Classes: `Pcre2Code`, `Pcre2CompileOption`
  - Enum values: `CASE_INSENSITIVE`, `DOTALL`
  - Methods: `compile()`, `match()`, `getErrorMessage()`

## Testing Patterns

- **Framework**: JUnit 5 (Jupiter) with parameterized tests
- **Backend testing**: Shared base class `org.pcre4j.test.Pcre2Tests` extended by backend-specific tests
- **Parameterization**: Tests run against both JNA and FFM backends via `@MethodSource`

## FFM Backend Notes

The FFM module requires preview features (handled automatically by Gradle):
- Compiler: `--enable-preview`
- JVM: `--enable-preview`

## Key Classes

| Class | Module | Purpose |
|-------|--------|---------|
| `IPcre2` | api | Backend interface contract |
| `Pcre2Code` | lib | Compiled pattern wrapper |
| `Pcre2MatchData` | lib | Match result container |
| `Pcre4j` | lib | Bootstrap singleton for backend selection |
| `Pcre4jUtils` | lib | Static utility methods |
| `Pattern` | regex | java.util.regex-compatible pattern |
| `Matcher` | regex | java.util.regex-compatible matcher |

## Commit Message Format

Preferred format: `(type) brief description`
- Types: `chore`, `docs`, `feat`, `fix`
- Examples: `(chore) gradle 8.12`, `(feat) JIT stack`, `(fix) regex: handle edge case`
- Reverts use Git default: `Revert "(type) original message"`

## Release Process

Releases are created using `gh release create`, which creates both the git tag AND GitHub Release in one command:

```bash
gh release create <version> --generate-notes
```

**Workflow**:
1. `gh release create <version> --generate-notes` creates tag + GitHub Release
2. Tag push triggers `.github/workflows/release.yaml` for Maven Central publish via JReleaser
3. Update README.md version references, commit, push

**Note**: `skipRelease: true` in jreleaser.yml exists because the GitHub Release is already created by `gh release create` before JReleaser runs.

## Task Tracking

GitHub Issues: https://github.com/alexey-pelykh/pcre4j/issues

**Closing Issues**: Use PR descriptions (not commit messages) since the project uses rebase-merge.
Include `Fixes #<number>` or `Closes #<number>` in the PR description to auto-close issues on merge.
