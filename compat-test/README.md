# compat-test — pcre4j ↔ java.util.regex compatibility harness

> **Internal regression tool. NOT published.** See `LICENSE.NOTICE` for imported-source provenance.

## What

Runs OpenJDK 21u's own `java/util/regex` tests and `.txt` data files against `org.pcre4j.regex`,
recording oracle-vs-SUT discrepancies into `build/reports/compat/raw.jsonl` and rendering a
categorized summary into `build/reports/compat/report.md`.

## Running

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 \
PATH=/usr/lib/jvm/java-21-openjdk-amd64/bin:$PATH \
./gradlew :compat-test:compatReport \
  -Dpcre2.library.path=/usr/lib/x86_64-linux-gnu
```

To switch backend to JNA (default if Java 22 unavailable for the FFM MRJAR overlay):

```bash
./gradlew :compat-test:compatReport -Pcompat.backend=jna ...
```

## Outputs

| Path | Purpose |
| --- | --- |
| `build/reports/compat/raw.jsonl` | One JSON line per probe (oracle + SUT MatchProbe). The fact record. |
| `build/reports/compat/report.md` | Categorized summary. Re-generable from raw.jsonl by re-running `:compatReport`. |
| `build/reports/tests/test/index.html` | Standard Gradle test report (Pass/Fail per imported JUnit test). |

## Imported sources

See `src/test/java/org/pcre4j/compat/imported/README.md` for upstream SHA + per-file
modifications.

## Limitations

- Reflective driver for `RegExTest.java` only detects per-method failure when `report()` throws —
  methods that silently increment `failCount` without throwing may appear as PASS. The class-level
  final `report()` will still surface aggregate failure.
- `PatternStreamTest.java` (TestNG + JDK test-lib deps) is **not** imported; would require porting
  to JUnit 5.
- The `whitebox/` directory is **not** imported (tests JDK Pattern internal IR — not engine
  behavior).
