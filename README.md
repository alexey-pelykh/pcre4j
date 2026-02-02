# PCRE4J: PCRE for Java

[![GitHub Repo stars](https://img.shields.io/github/stars/alexey-pelykh/pcre4j?style=flat&logo=github)](https://github.com/alexey-pelykh/pcre4j)
[![License](https://img.shields.io/github/license/alexey-pelykh/pcre4j)](https://www.gnu.org/licenses/lgpl-3.0.txt)
[![CI](https://img.shields.io/github/check-runs/alexey-pelykh/pcre4j/main)](https://github.com/alexey-pelykh/pcre4j/actions/workflows/ci.yaml)
[![codecov](https://codecov.io/gh/alexey-pelykh/pcre4j/graph/badge.svg?token=7UJZ501GWT)](https://codecov.io/gh/alexey-pelykh/pcre4j)
[![Maven Central Version](https://img.shields.io/maven-central/v/org.pcre4j/lib)](https://mvnrepository.com/artifact/org.pcre4j/lib)
[![javadoc](https://javadoc.io/badge2/org.pcre4j/lib/javadoc.svg)](https://javadoc.io/doc/org.pcre4j)

The PCRE4J project's goal is to bring the power of the [PCRE](https://www.pcre.org) library to Java.

This project is brought to you by [Alexey Pelykh](https://github.com/alexey-pelykh) with a great gratitude to the PCRE
library author [Philip Hazel](https://github.com/PhilipHazel) and its contributors.

The source code is hosted on [GitHub](https://github.com/alexey-pelykh/pcre4j).

## Usage

The PCRE4J library provides several APIs to interact with the PCRE library:

- `java.util.regex`-alike API via `org.pcre4j.regex.Pattern` and `org.pcre4j.regex.Matcher`
- The PCRE4J API via `org.pcre4j.Pcre2Code` and related classes
- The `libpcre2` direct API via backends that implement `org.pcre4j.api.IPcre2`

### Quick Start with `java.util.regex`-alike API

Add the following dependencies to your `pom.xml` file:

```xml
<dependencies>
    <dependency>
        <groupId>org.pcre4j</groupId>
        <artifactId>regex</artifactId>
        <version>0.7.0</version>
    </dependency>
    <dependency>
        <groupId>org.pcre4j</groupId>
        <!-- TODO: Select one of the following artifacts corresponding to the backend you want to use -->
        <artifactId>jna</artifactId>
        <!-- <artifactId>ffm</artifactId> -->
        <version>0.7.0</version>
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

Add the following dependencies to your `pom.xml` file:

```xml
<dependencies>
    <dependency>
        <groupId>org.pcre4j</groupId>
        <artifactId>lib</artifactId>
        <version>0.7.0</version>
    </dependency>
    <dependency>
        <groupId>org.pcre4j</groupId>
        <!-- TODO: Select one of the following artifacts corresponding to the backend you want to use -->
        <artifactId>jna</artifactId>
        <!-- <artifactId>ffm</artifactId> -->
        <version>0.7.0</version>
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

Add the following dependencies to your `pom.xml` file:

```xml
<dependencies>
    <dependency>
        <groupId>org.pcre4j</groupId>
        <!-- TODO: Select one of the following artifacts corresponding to the backend you want to use -->
        <artifactId>jna</artifactId>
        <!-- <artifactId>ffm</artifactId> -->
        <version>0.7.0</version>
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

See the exposed PCRE2 API functions list [here](./PCRE2_API.md).

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

## Javadoc

Please see [the Javadoc Index](./javadoc/index.md) for the detailed API documentation.
