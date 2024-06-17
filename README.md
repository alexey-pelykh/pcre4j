# PCRE4J: PCRE for Java

The PCRE4J project's goal is to bring the power of the [PCRE](https://www.pcre.org) library to Java.

This project is brought to you by [Alexey Pelykh](https://github.com/alexey-pelykh) with a great gratitude to the PCRE
library author [Philip Hazel](https://github.com/PhilipHazel) and its contributors.

## Usage

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

## Backends

The PCRE4J library supports several backends to invoke the `pcre2` API.

### `jna`

The `jna` backend uses the [Java Native Access](https://github.com/java-native-access/jna) library to invoke the `pcre2`
shared library. For this backend to work, the `pcre2` shared library must be installed on the system and be visible to
the JNA.

### `ffm`

The `ffm` backend uses the [Foreign Functions and Memory API](https://docs.oracle.com/en/java/javase/21/core/foreign-function-and-memory-api.html)
to invoke the `pcre2` shared library. For this backend to work, the `pcre2` shared library must be installed on the
system and be visible via `java.library.path`.

Note that `--enable-preview` must be passed to the Java compiler to enable the preview features for this backend to be
used.
