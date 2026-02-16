# PCRE4J: PCRE for Java

[![GitHub Repo stars](https://img.shields.io/github/stars/alexey-pelykh/pcre4j?style=flat&logo=github)](https://github.com/alexey-pelykh/pcre4j)
[![License](https://img.shields.io/github/license/alexey-pelykh/pcre4j)](https://www.gnu.org/licenses/lgpl-3.0.txt)
[![CI](https://img.shields.io/github/check-runs/alexey-pelykh/pcre4j/main)](https://github.com/alexey-pelykh/pcre4j/actions/workflows/ci.yaml)
[![codecov](https://codecov.io/gh/alexey-pelykh/pcre4j/graph/badge.svg?token=7UJZ501GWT)](https://codecov.io/gh/alexey-pelykh/pcre4j)
[![Maven Central Version](https://img.shields.io/maven-central/v/org.pcre4j/lib)](https://mvnrepository.com/artifact/org.pcre4j/lib)
[![javadoc](https://javadoc.io/badge2/org.pcre4j/lib/javadoc.svg)](https://javadoc.io/doc/org.pcre4j)
![PCRE2 API](https://img.shields.io/badge/PCRE2_API-100%25-brightgreen)

The PCRE4J project's goal is to bring the power of the [PCRE](https://www.pcre.org) library to Java.

PCRE4J provides **100% coverage of the PCRE2 API**, giving you access to every feature of the PCRE2 library from Java.

## Why PCRE4J?

Java's built-in `java.util.regex` covers many use cases, but PCRE2 offers capabilities that go
beyond what the standard library provides:

- **Richer regex syntax** — PCRE2 supports features absent from `java.util.regex`, including
  `\K` (match reset), recursive patterns, callouts, and the DFA matching
  algorithm.
- **JIT compilation** — PCRE2's JIT compiler translates patterns into native machine code,
  delivering significant speedups for pattern-heavy workloads.
- **Built-in ReDoS protection** — configurable match, depth, and heap limits let you cap
  resource usage per match, guarding against catastrophic backtracking on untrusted input.
- **Cross-system consistency** — when your regex behavior must match other PCRE-based systems
  (Nginx, PHP, R, grep -P, and many more), using the same engine eliminates subtle
  dialect differences.
- **Drop-in `java.util.regex` API** — the `regex` module mirrors `Pattern` and `Matcher`, so
  you can switch engines without rewriting application code.
- **Full PCRE2 API access** — 100% of the PCRE2 C API is exposed, so advanced users are never
  limited by the binding layer.

### Feature Comparison

| Feature | `java.util.regex` | PCRE4J |
|---------|-------------------|--------|
| Recursive patterns (`(?R)`, `(?1)`) | No | Yes |
| `\K` match reset | No | Yes |
| Callouts | No | Yes |
| DFA matching | No | Yes |
| JIT compilation | No | Yes (default) |
| ReDoS protection (match/depth/heap limits) | No | Yes |
| Compiled pattern serialization to bytes | No | Yes |
| Glob/POSIX pattern conversion | No | Yes |
| Native library bundles (no system install) | N/A | Yes |
| GraalVM native-image | Yes | Yes |

## Quick Start

Add the dependency and start matching — the API mirrors `java.util.regex`:

```java
import org.pcre4j.regex.Pattern;

var matcher = Pattern.compile("(?<year>\\d{4})-(?<month>\\d{2})").matcher("2026-02");
if (matcher.find()) {
    System.out.println(matcher.group("year"));  // "2026"
}
```

**Maven** (`pom.xml`):

```xml
<properties>
    <pcre4j.version>1.0.0</pcre4j.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.pcre4j</groupId>
        <artifactId>regex</artifactId>
        <version>${pcre4j.version}</version>
    </dependency>
    <dependency>
        <groupId>org.pcre4j</groupId>
        <!-- TODO: Select one of the following artifacts corresponding to the backend you want to use -->
        <artifactId>jna</artifactId>
        <!-- <artifactId>ffm</artifactId> -->
        <version>${pcre4j.version}</version>
    </dependency>
</dependencies>
```

**Gradle** (`build.gradle.kts`):

```kotlin
val pcre4jVersion = "1.0.0"

dependencies {
    implementation("org.pcre4j:regex:$pcre4jVersion")
    // TODO: Select one of the following artifacts corresponding to the backend you want to use
    implementation("org.pcre4j:jna:$pcre4jVersion")
    // implementation("org.pcre4j:ffm:$pcre4jVersion")
}
```

By default, the JIT compilation is used in cases the platform and the library support it. To override this behavior, you
can set the `pcre2.regex.jit` system property with the value `false` to the JVM.

## Prerequisites

- **Java 21 or later**
- **PCRE2 native library** — choose one of these approaches:

### Option A: Native Library Bundles (no system install)

Add a platform-specific bundle to your dependencies and PCRE4J loads the library automatically:

| Artifact | Platform |
|----------|----------|
| `org.pcre4j:pcre4j-native-linux-x86_64` | Linux x86_64 |
| `org.pcre4j:pcre4j-native-linux-aarch64` | Linux aarch64 |
| `org.pcre4j:pcre4j-native-macos-x86_64` | macOS x86_64 (Intel) |
| `org.pcre4j:pcre4j-native-macos-aarch64` | macOS aarch64 (Apple Silicon) |
| `org.pcre4j:pcre4j-native-windows-x86_64` | Windows x86_64 |
| `org.pcre4j:pcre4j-native-all` | All supported platforms |

Example (Gradle): `implementation("org.pcre4j:pcre4j-native-linux-x86_64:1.0.0")`

### Option B: System-Installed PCRE2

Install the PCRE2 library on your system:
- **Ubuntu/Debian**: `sudo apt install libpcre2-8-0`
- **macOS** (Homebrew): `brew install pcre2`
- **Windows**: Download the PCRE2 DLL and add its location to `PATH`

The library is located automatically via `pcre2-config`, `pkg-config`, or well-known platform
paths. You can also set the path explicitly with `-Dpcre2.library.path=/path/to/lib`.

> Automatic library discovery can be disabled with `-Dpcre2.library.discovery=false`.

## Module Architecture

PCRE4J is organized into layered modules published as separate Maven artifacts under `org.pcre4j`:

```
regex  ──→  lib  ──→  api  ←──  jna
                       ↑
                       └──────  ffm
```

| Artifact | Description |
|----------|-------------|
| `api` | Backend interface contract (`IPcre2`) with PCRE2 constants |
| `lib` | Core wrapper (`Pcre2Code`, match data, compile/match options, utilities). Depends on `api` |
| `jna` | [JNA](https://github.com/java-native-access/jna) backend. Depends on `api` |
| `ffm` | [FFM](https://docs.oracle.com/en/java/javase/22/core/foreign-function-and-memory-api.html) backend. Depends on `api` |
| `regex` | `java.util.regex`-compatible API (`Pattern`, `Matcher`). Depends on `api` and `lib` |

### Dependency Chain for Consumers

Each API tier requires a different set of artifacts. The `regex` and `lib` artifacts declare their
upstream dependencies as transitive, so your dependency manager pulls them automatically:

| API Tier | You Declare | Resolved Transitively |
|----------|-------------|----------------------|
| `java.util.regex`-compatible | `regex` + `jna` or `ffm` | `api`, `lib` |
| PCRE4J wrapper | `lib` + `jna` or `ffm` | `api` |
| Direct PCRE2 | `jna` or `ffm` | `api` |

A backend (`jna` or `ffm`) is always required at runtime but is intentionally not a transitive
dependency of `regex` or `lib`, letting consumers choose which native access mechanism to use.

## Library Initialization

The `regex` and `lib` convenience APIs use a global backend held by `Pcre4j`. The backend is
initialized automatically — just add a backend artifact (`jna` or `ffm`) to your classpath and
start using PCRE4J. No explicit setup call is required.

On first use, `Pcre4j.api()` discovers available backends via `ServiceLoader`. When both the
FFM and JNA backends are present, the FFM backend is preferred for its better performance.

For explicit control, you can still call `Pcre4j.setup()` to install a specific backend:

```java
import org.pcre4j.Pcre4j;
import org.pcre4j.jna.Pcre2;   // or org.pcre4j.ffm.Pcre2

static {
    Pcre4j.setup(new Pcre2());
}
```

`setup()` takes priority over auto-discovery and may be called again to replace the backend;
existing compiled patterns are unaffected because each `Pcre2Code` instance captures the
backend it was created with.

Every convenience constructor and factory method (e.g. `Pcre2Code(String)`,
`Pattern.compile(String)`) has an explicit-API overload that accepts an `IPcre2` parameter
directly, bypassing the global singleton entirely.

> **Multi-classloader note:** `Pcre4j` stores the backend in a `static` field, so each
> classloader that loads PCRE4J gets its own independent singleton. In application servers or
> plugin frameworks, either place the PCRE4J JARs in a shared classloader, or use the
> explicit-API overloads to avoid relying on the global state.

## Advanced Usage via PCRE4J API

Add `lib` and a backend to your dependencies:

**Maven** (`pom.xml`):

```xml
<dependencies>
    <dependency>
        <groupId>org.pcre4j</groupId>
        <artifactId>lib</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>org.pcre4j</groupId>
        <!-- TODO: Select one of the following artifacts corresponding to the backend you want to use -->
        <artifactId>jna</artifactId>
        <!-- <artifactId>ffm</artifactId> -->
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

**Gradle** (`build.gradle.kts`):

```kotlin
dependencies {
    implementation("org.pcre4j:lib:1.0.0")
    // TODO: Select one of the following artifacts corresponding to the backend you want to use
    implementation("org.pcre4j:jna:1.0.0")
    // implementation("org.pcre4j:ffm:1.0.0")
}
```

```java
import org.pcre4j.*;
import org.pcre4j.option.*;

import java.util.EnumSet;

public class Usage {
    public static String[] example(String pattern, String subject) {
        final Pcre2Code code;
        if (Pcre4jUtils.isJitSupported(Pcre4j.api())) {
            code = new Pcre2JitCode(
                    pattern,
                    EnumSet.noneOf(Pcre2CompileOption.class),
                    null,
                    null
            );
        } else {
            code = new Pcre2Code(
                    pattern,
                    EnumSet.noneOf(Pcre2CompileOption.class),
                    null
            );
        }
        final var matchData = new Pcre2MatchData(code);
        code.match(
                subject,
                0,
                EnumSet.noneOf(Pcre2MatchOption.class),
                matchData,
                null
        );
        return Pcre4jUtils.getMatchGroups(code, subject, matchData);
    }
}
```

## Low-Level Usage

Add a backend artifact directly:

**Maven** (`pom.xml`):

```xml
<dependencies>
    <dependency>
        <groupId>org.pcre4j</groupId>
        <!-- TODO: Select one of the following artifacts corresponding to the backend you want to use -->
        <artifactId>jna</artifactId>
        <!-- <artifactId>ffm</artifactId> -->
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

**Gradle** (`build.gradle.kts`):

```kotlin
dependencies {
    // TODO: Select one of the following artifacts corresponding to the backend you want to use
    implementation("org.pcre4j:jna:1.0.0")
    // implementation("org.pcre4j:ffm:1.0.0")
}
```

```java
// TODO: Select one of the following imports for the backend you want to use:
import org.pcre4j.jna.Pcre2;
// import org.pcre4j.ffm.Pcre2;

public class Usage {
    public static void example() {
        final var pcre2 = new Pcre2();

        final var errorcode = new int[1];
        final var erroroffset = new long[1];
        final var code = pcre2.compile("pattern", 0, errorcode, erroroffset, 0);
        if (code == 0) {
            throw new RuntimeException(
                    "PCRE2 compilation failed with error code " + errorcode[0] + " at offset " + erroroffset[0]
            );
        }

        pcre2.codeFree(code);
    }
}
```

See the complete list of exposed PCRE2 API functions [here](./PCRE2_API.md).

## `java.util.regex` API Compatibility

The `regex` module provides a complete implementation of the `java.util.regex` API backed by PCRE2.
All `Pattern` and `Matcher` methods are supported.

### `Pattern` Flags

| Flag | PCRE2 Mapping | Notes |
|------|---------------|-------|
| `CASE_INSENSITIVE` | `PCRE2_CASELESS` | |
| `COMMENTS` | `PCRE2_EXTENDED` | |
| `DOTALL` | `PCRE2_DOTALL` | |
| `LITERAL` | `PCRE2_LITERAL` | |
| `MULTILINE` | `PCRE2_MULTILINE` | |
| `UNICODE_CHARACTER_CLASS` | `PCRE2_UCP` | |
| `UNICODE_CASE` | — | PCRE2 with UTF mode already performs Unicode-aware case folding |
| `UNIX_LINES` | `PCRE2_NEWLINE_LF` | |
| `CANON_EQ` | — | Via NFD normalization; see `Pattern.CANON_EQ` Javadoc for limitations |

## Security: ReDoS Protection

Regular Expression Denial of Service (ReDoS) occurs when a crafted input causes catastrophic
backtracking in a regex engine, leading to excessive CPU usage. PCRE4J provides several layers
of protection against ReDoS attacks.

For reporting security vulnerabilities, please see the [Security Policy](./SECURITY.md).

### JIT Compilation (Enabled by Default)

The `regex` module enables PCRE2 JIT compilation by default when the platform supports it.
JIT-compiled patterns use a fixed-size machine stack, which can mitigate some forms of
catastrophic backtracking and runaway recursion, but explicit match limits (see below) should
still be used for stronger guarantees on CPU and memory usage.

To disable JIT: `-Dpcre2.regex.jit=false`

### Match Limits

PCRE2 provides configurable limits that terminate match operations exceeding resource thresholds.
The `regex` module exposes these via system properties:

| System Property | Description | PCRE2 Default |
|----------------|-------------|---------------|
| `pcre2.regex.match.limit` | Maximum number of internal match function calls | ~10,000,000 |
| `pcre2.regex.depth.limit` | Maximum backtracking depth | ~250 |
| `pcre2.regex.heap.limit` | Maximum heap memory in KiB | ~20,000 |

When a limit is exceeded, a `MatchLimitException` is thrown (a `RuntimeException` subclass)
with the specific PCRE2 error code indicating which limit was hit.

**Example: Tightening limits for untrusted input:**

```bash
java -Dpcre2.regex.match.limit=1000000 -Dpcre2.regex.depth.limit=100 -jar myapp.jar
```

**Note:** The PCRE2 library's compiled-in defaults already provide baseline protection. The system
properties allow applications to tighten these limits further for security-sensitive use cases.
When not set, the library defaults are used.

### Low-Level API

For fine-grained control, the `lib` module provides `Pcre2MatchContext` with `setMatchLimit()`,
`setDepthLimit()`, and `setHeapLimit()` methods that can be applied on a per-match basis.

## Backends

The PCRE4J library supports several backends to invoke the `pcre2` API.

### `jna`

The `jna` backend uses the [Java Native Access](https://github.com/java-native-access/jna) library to invoke the `pcre2`
shared library. For this backend to work, the `pcre2` shared library must be installed on the system. The library is
located via `jna.library.path`, or automatically discovered using `pcre2-config`, `pkg-config`, or well-known platform
paths as a fallback.

### `ffm`

The `ffm` backend uses
the [Foreign Functions and Memory API](https://docs.oracle.com/en/java/javase/22/core/foreign-function-and-memory-api.html)
to invoke the `pcre2` shared library. For this backend to work, the `pcre2` shared library must be installed on the
system. The library is located via `java.library.path`, or automatically discovered using `pcre2-config`, `pkg-config`,
or well-known platform paths as a fallback.

The `ffm` module is packaged as a Multi-Release JAR supporting both:
- **Java 21**: Requires `--enable-preview` flag (FFM was a preview feature)
- **Java 22+**: No special flags required (FFM is finalized)

> **Note:** The `ffm` backend is incompatible with OpenJ9-based JVMs (including IBM Semeru) on Java
> 21 due to a JVM bug in the preview FFM implementation that causes memory corruption assertions.
> OpenJ9 Java 22+, where FFM is finalized, works correctly. Use the `jna` backend on OpenJ9 Java 21.

## GraalVM Native Image

PCRE4J supports GraalVM native-image compilation using the FFM backend. All modules ship with
GraalVM reachability metadata, so no additional configuration is required.

Requirements:
- **GraalVM JDK 25** or later (for finalized FFM and `reachability-metadata.json` foreign section support)
- **FFM backend** (`ffm` artifact) — the JNA backend is not supported for native-image
- **PCRE2 native library** must be available at runtime (via system install or library path)

## What's New in 1.0.0

The 1.0.0 release marks PCRE4J's first stable API with a comprehensive set of new features:

- **Platform-specific native library bundles** — add a dependency, skip the system install
- **GraalVM native-image support** — compile PCRE4J applications to native executables
- **ServiceLoader-based backend discovery** — zero-configuration setup
- **Callout support** in the high-level API
- **DFA matching** through the high-level API
- **Pattern serialization** — serialize compiled patterns to bytes and back
- **Glob and POSIX pattern conversion** through the high-level API
- **Scoped backend API** for multi-backend support (`Pcre4j.withBackend()`)
- **Unified exception hierarchy** with specific exception types
- **JPMS module descriptors** across all modules

See the [Changelog](./CHANGELOG.md) for the full list of changes.

## Roadmap

See the [Roadmap](./ROADMAP.md) for planned features and project direction.

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](./CONTRIBUTING.md) for guidelines.

All commits must be signed off to certify the [Developer Certificate of Origin (DCO)](https://developercertificate.org/). Use `git commit -s` to add the required `Signed-off-by` line.

This project is brought to you by [Alexey Pelykh](https://github.com/alexey-pelykh) with a great gratitude to the PCRE
library author [Philip Hazel](https://github.com/PhilipHazel) and its contributors.

The source code is hosted on [GitHub](https://github.com/alexey-pelykh/pcre4j).

## Javadoc

Please see [the Javadoc Index](https://alexey-pelykh.com/pcre4j/javadoc/) for the detailed API documentation.
