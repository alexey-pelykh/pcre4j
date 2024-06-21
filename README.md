# PCRE4J: PCRE for Java

![GitHub License](https://img.shields.io/github/license/alexey-pelykh/pcre4j)
![GitHub branch check runs](https://img.shields.io/github/check-runs/alexey-pelykh/pcre4j/main)
[![codecov](https://codecov.io/gh/alexey-pelykh/pcre4j/graph/badge.svg?token=7UJZ501GWT)](https://codecov.io/gh/alexey-pelykh/pcre4j)
![Maven Central Version](https://img.shields.io/maven-central/v/org.pcre4j/lib)

The PCRE4J project's goal is to bring the power of the [PCRE](https://www.pcre.org) library to Java.

This project is brought to you by [Alexey Pelykh](https://github.com/alexey-pelykh) with a great gratitude to the PCRE
library author [Philip Hazel](https://github.com/PhilipHazel) and its contributors.

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
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>org.pcre4j</groupId>
        <!-- TODO: Select one of the following artifacts corresponding to the backend you want to use -->
        <artifactId>jna</artifactId>
        <!-- <artifactId>ffm</artifactId> -->
        <version>0.1.0</version>
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

### Advanced Usage via PCRE4J API

Add the following dependencies to your `pom.xml` file:

```xml
<dependencies>
    <dependency>
        <groupId>org.pcre4j</groupId>
        <artifactId>lib</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>org.pcre4j</groupId>
        <!-- TODO: Select one of the following artifacts corresponding to the backend you want to use -->
        <artifactId>jna</artifactId>
        <!-- <artifactId>ffm</artifactId> -->
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

Proceed using the PCRE4J library in your Java code:

```java
import org.pcre4j.Pcre2Code;
import org.pcre4j.Pcre2CompileOption;
import org.pcre4j.Pcre2MatchData;
import org.pcre4j.Pcre2MatchOption;
import org.pcre4j.Pcre4j;
import org.pcre4j.Pcre4jUtils;
// TODO: Select one of the following imports for the backend you want to use:
import org.pcre4j.jna.Pcre2;
// import org.pcre4j.ffm.Pcre2;

public class Usage {
    static {
        Pcre4j.setup(new Pcre2());
    }

    public static String[] example(String pattern, String subject) {
        final var code = new Pcre2Code(
                pattern,
                EnumSet.noneOf(Pcre2CompileOption.class),
                null
        );
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
        <version>0.1.0</version>
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

        api.codeFree(code);
    }
}
```

See the exposed PCRE2 API functions list [here](./PCRE2_API.md).

## Backends

The PCRE4J library supports several backends to invoke the `pcre2` API.

### `jna`

The `jna` backend uses the [Java Native Access](https://github.com/java-native-access/jna) library to invoke the `pcre2`
shared library. For this backend to work, the `pcre2` shared library must be installed on the system and be visible to
the JNA.

### `ffm`

The `ffm` backend uses
the [Foreign Functions and Memory API](https://docs.oracle.com/en/java/javase/21/core/foreign-function-and-memory-api.html)
to invoke the `pcre2` shared library. For this backend to work, the `pcre2` shared library must be installed on the
system and be visible via `java.library.path`.

Note that `--enable-preview` must be passed to the Java compiler to enable the preview features for this backend to be
used.

### Javadoc

Please see [the Javadoc Index](./javadoc/index.md) for the detailed API documentation.
