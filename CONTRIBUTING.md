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

## Branch Protection

The `main` branch has the following protection rules:

- **Required status checks**: The `package` CI job must pass before merging (this transitively requires `lint` and all `compatibility` matrix jobs to pass as well)
- **Branch must be up to date**: PRs must be up to date with `main` before merging
- **Required reviews**: At least 1 approving review is required
- **Stale review dismissal**: Approvals are dismissed when new commits are pushed
- **Signed commits**: All commits must be signed ([how to sign commits](https://docs.github.com/en/authentication/managing-commit-signature-verification/signing-commits))
- **Admin enforcement**: These rules apply to everyone, including administrators
- **No force pushes**: Force pushes to `main` are not allowed
- **No branch deletion**: The `main` branch cannot be deleted

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

## Testing Guide

PCRE4J supports two native backends — JNA and FFM — and tests must verify behavior across both. The test infrastructure provides several patterns for backend instantiation depending on the test's scope.

### Parameterized Tests with `BackendProvider`

Most tests in the `lib` and `regex` modules use JUnit 5 parameterized tests with a shared `BackendProvider` that supplies both backend instances. Each test method runs once per backend automatically:

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pcre4j.api.IPcre2;

@ParameterizedTest
@MethodSource("org.pcre4j.test.BackendProvider#parameters")
void myTest(IPcre2 api) {
    var code = new Pcre2Code(api, "\\d+");
    // ... assertions using api
}
```

`BackendProvider` (in `lib/src/testFixtures`) loads backends reflectively to avoid compile-time coupling:

```java
public static Stream<Arguments> parameters() {
    return Stream.of(
            Arguments.of(loadBackend("org.pcre4j.jna.Pcre2")),
            Arguments.of(loadBackend("org.pcre4j.ffm.Pcre2"))
    );
}
```

Use this pattern when testing `lib` or `regex` classes that accept an `IPcre2` parameter and should behave identically regardless of backend.

### Contract Test Interfaces

The `lib/src/testFixtures` directory defines contract test interfaces — Java interfaces with `default` test methods that act as reusable test traits:

```java
public interface Pcre2MatchingContractTest<T extends IPcre2> {
    T getApi();

    @Test
    default void plainStringMatch() {
        var code = new Pcre2Code(getApi(), "42", EnumSet.noneOf(Pcre2CompileOption.class), null);
        // ... assertions
    }
}
```

There are 12 contract interfaces covering all PCRE2 functionality areas (configuration, matching, substitution, substrings, match context, DFA matching, compile context, serialization, JIT, pattern conversion, miscellaneous, and UTF width support).

### Base Test Class (`Pcre2Tests`)

The abstract `org.pcre4j.test.Pcre2Tests` base class aggregates all 12 contract interfaces. Each backend module extends this class and provides its own backend instance:

```java
// In jna/src/test/java/org/pcre4j/jna/Pcre2Tests.java
public class Pcre2Tests extends org.pcre4j.test.Pcre2Tests {
    public Pcre2Tests() {
        super(new Pcre2());  // Direct JNA backend instantiation
    }

    @Override
    public IPcre2 createApi(Pcre2UtfWidth width) {
        return new Pcre2(width);
    }
}
```

The FFM backend follows the same structure. This pattern guarantees both backends run identical contract tests and also allows backend-specific tests (e.g., JNA callback handling via `com.sun.jna.Callback` vs FFM upcall stubs via `MethodHandle`).

### Backend-Agnostic Tests

Some tests don't need a backend at all — they test utility logic, enum constants, or bootstrap error handling:

```java
// In lib/src/test/java/org/pcre4j/Pcre4jTests.java
public class Pcre4jTests {
    @BeforeEach
    void resetSingleton() throws Exception {
        Field apiField = Pcre4j.class.getDeclaredField("api");
        apiField.setAccessible(true);
        apiField.set(null, null);
    }

    @Test
    void api_beforeSetup_throwsIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> Pcre4j.api());
    }
}
```

Use this pattern for tests that exercise backend-independent code paths.

### Choosing a Pattern

| Scenario | Pattern | Example |
|----------|---------|---------|
| Testing `lib`/`regex` classes against both backends | Parameterized with `BackendProvider` | `Pcre2CodeTests`, `PatternTests` |
| Adding low-level PCRE2 API contract tests | Contract test interface + `Pcre2Tests` base class | `Pcre2MatchingContractTest` |
| Testing backend-specific behavior (callbacks, FFI) | Backend-specific test methods in `jna`/`ffm` `Pcre2Tests` | JNA `CalloutEnumerateCallback` tests |
| Testing utilities, enums, or bootstrap logic | Backend-agnostic tests (no parameterization) | `Pcre4jTests`, `Pcre2EnumTests` |

## License

By contributing to PCRE4J, you agree that your contributions will be licensed under the [GNU Lesser General Public License v3.0](LICENSE).
