package org.pcre4j.regex;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PatternSplitAsStreamTest {

    @Test
    void splitAsStreamJavaOnly() {
        // Call your new method in Pattern.java
        var pattern = Pattern.compile(",", 0); // no IPcre2 dependency
        var input = "a,b,c";

        var result = pattern.splitAsStream(input).toList();

        assertEquals(Arrays.asList("a", "b", "c"), result,
            "splitAsStream should discard trailing empty strings like split(input, 0)");

        // Optional: test trailing comma
        var inputWithTrailingComma = "a,b,c,";
        var result2 = pattern.splitAsStream(inputWithTrailingComma).toList();

        assertEquals(Arrays.asList("a", "b", "c"), result2,
            "splitAsStream should discard trailing empty strings");
    }
}
