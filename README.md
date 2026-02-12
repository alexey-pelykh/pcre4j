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

This project is brought to you by [Alexey Pelykh](https://github.com/alexey-pelykh) with a great gratitude to the PCRE
library author [Philip Hazel](https://github.com/PhilipHazel) and its contributors.

The source code is hosted on [GitHub](https://github.com/alexey-pelykh/pcre4j).

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

## Prerequisites

- **Java 21 or later**
- **PCRE2 native library** installed on your system:
  - **Ubuntu/Debian**: `sudo apt install libpcre2-8-0`
  - **macOS** (Homebrew): `brew install pcre2`
  - **Windows**: Download the PCRE2 DLL and add its location to `PATH`

The library is located automatically via `pcre2-config`, `pkg-config`, or well-known platform
paths. You can also set the path explicitly with `-Dpcre2.library.path=/path/to/lib`.

## Usage

The PCRE4J library provides several APIs to interact with the PCRE library:

- `java.util.regex`-compatible API via `org.pcre4j.regex.Pattern` and `org.pcre4j.regex.Matcher`
- The PCRE4J API via `org.pcre4j.Pcre2Code` and related classes
- The `libpcre2` direct API via backends that implement `org.pcre4j.api.IPcre2`

### Quick Start with `java.util.regex`-compatible API

Add the following dependencies to your `pom.xml` file (replace `${pcre4j.version}` with the
[latest release version](https://mvnrepository.com/artifact/org.pcre4j/lib)):

```xml
<properties>
    <pcre4j.version><!-- latest version --></pcre4j.version>
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

Proceed using the PCRE4J library in your Java code similarly like if you were using the `java.util.regex` package:

```java
import org.pcre4j.Pcre4j;
// TODO: Select one of the following imports for the backend you want to use:
import org.pcre4j.jna.Pcre2;
// import org.pcre4j.ffm.Pcre2;
import org.pcre4j.regex.Pattern;

public class Usage {
    static {
        Pcre4j.setup(new Pcre2());
    }

    public static String[] example(String pattern, String subject) {
        final var matcher = Pattern.compile(pattern).matcher(subject);
        if (matcher.find()) {
            final var groups = new String[matcher.groupCount() + 1];
            for (var i = 0; i < groups.length; i++) {
                groups[i] = matcher.group(i);
            }
            return groups;
        }
        return null;
    }
}
```

By default, the JIT compilation is used in cases the platform and the library support it. To override this behavior, you
can set the `pcre2.regex.jit` system property with the value `false` to the JVM.

### Advanced Usage via PCRE4J API

Add the following dependencies to your `pom.xml` file (replace `${pcre4j.version}` with the
[latest release version](https://mvnrepository.com/artifact/org.pcre4j/lib)):

```xml
<properties>
    <pcre4j.version><!-- latest version --></pcre4j.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.pcre4j</groupId>
        <artifactId>lib</artifactId>
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

Proceed using the PCRE4J library in your Java code:

```java
import org.pcre4j.*;
// TODO: Select one of the following imports for the backend you want to use:
import org.pcre4j.jna.Pcre2;
// import org.pcre4j.ffm.Pcre2;

public class Usage {
    static {
        Pcre4j.setup(new Pcre2());
    }

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

### Low-Level Usage

Add the following dependencies to your `pom.xml` file (replace `${pcre4j.version}` with the
[latest release version](https://mvnrepository.com/artifact/org.pcre4j/lib)):

```xml
<properties>
    <pcre4j.version><!-- latest version --></pcre4j.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.pcre4j</groupId>
        <!-- TODO: Select one of the following artifacts corresponding to the backend you want to use -->
        <artifactId>jna</artifactId>
        <!-- <artifactId>ffm</artifactId> -->
        <version>${pcre4j.version}</version>
    </dependency>
</dependencies>
```

Proceed using the `libpcre2` API in your Java code:

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

> **Note:** Automatic library discovery can be disabled by setting `-Dpcre2.library.discovery=false`.

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](./CONTRIBUTING.md) for guidelines.

All commits must be signed off to certify the [Developer Certificate of Origin (DCO)](https://developercertificate.org/). Use `git commit -s` to add the required `Signed-off-by` line.

## Javadoc

Please see [the Javadoc Index](https://alexey-pelykh.com/pcre4j/javadoc/) for the detailed API documentation.
