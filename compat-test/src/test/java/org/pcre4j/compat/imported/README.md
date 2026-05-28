# Imported OpenJDK test sources

All files in this directory and in `../../../../../resources/imported/` are
copied from https://github.com/openjdk/jdk21u/tree/51118e0da8e2945f1f6fc367b1124b671b798e33/test/jdk/java/util/regex
under GPLv2 + Classpath Exception.

**Upstream commit SHA:** `51118e0da8e2945f1f6fc367b1124b671b798e33`
**Date imported:** 2026-05-28

## Files

| File | Modifications |
| --- | --- |
| `TestCases.txt` | none |
| `BMPTestCases.txt` | none |
| `SupplementaryTestCases.txt` | none |
| `GraphemeTestCases.txt` | none |
| `RegExTest.java` | Package + import switch (`Pattern`/`Matcher` → `org.pcre4j.regex`; `MatchResult`/`PatternSyntaxException` kept as JDK); `import jdk.test.lib.RandomFactory` removed, usage replaced with `new java.util.Random(0xC0FFEEL)`; TestNG imports replaced with JUnit 5 + local `Assert.java` shim; method bodies of `grapheme()` (needs UCDFiles / Scanner type incompatibility), `patternAsPredicate()` and `patternAsMatchPredicate()` (Predicate&lt;CharSequence&gt; vs Predicate&lt;String&gt;) replaced with `throw new RuntimeException`; `serializeTest` skipped via `RegExTestRunner.SKIP` |
| `NamedGroupsTests.java` | Package + import switch (`Pattern`/`Matcher` → `org.pcre4j.regex`; `MatchResult` kept as JDK) |
| `SplitWithDelimitersTest.java` | Package + import switch (`Pattern` → `org.pcre4j.regex`) |
| `POSIX_ASCII.java` / `POSIX_Unicode.java` | Package declaration added; no other modifications |
| `ImmutableMatchResultTest.java` | Package + import switch (`Pattern`/`Matcher` → `org.pcre4j.regex`; `MatchResult` kept as JDK); `import jdk.test.lib.RandomFactory` removed, usage replaced with `new java.util.Random(0xC0FFEEL)` |
| `NegativeArraySize.java` | Package + import switch (`Pattern` → `org.pcre4j.regex`); TestNG `@Test`/`assertThrows` replaced with JUnit 5 equivalents |

See `../../../../../../LICENSE.NOTICE` for the full license notice.
