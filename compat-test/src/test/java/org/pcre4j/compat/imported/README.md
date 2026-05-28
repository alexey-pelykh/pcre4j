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
| `RegExTest.java` | import switched from `java.util.regex` to `org.pcre4j.regex` (see RegExTestRunner.java); methods reflecting on JDK private API marked `@Disabled` |
| `NamedGroupsTests.java` | same as RegExTest |
| `SplitWithDelimitersTest.java` | same |
| `POSIX_ASCII.java` / `POSIX_Unicode.java` | unmodified (helper truth tables) |
| `ImmutableMatchResultTest.java` | replaced `jdk.test.lib.RandomFactory` with `new Random(0xC0FFEEL)`; import switch |
| `NegativeArraySize.java` | import switch |

See `../../../../../../LICENSE.NOTICE` for the full license notice.
