# Post-Deploy Smoke Test

Driven by [`.github/workflows/post-deploy-smoke.yaml`](../workflows/post-deploy-smoke.yaml).

## Purpose

Resolves the freshly-published `org.pcre4j:pcre4j-native-<platform>:<version>` from
Maven Central on a 3-OS matrix (Linux/macOS/Windows) and exercises PCRE4J end-to-end
with no `-Dpcre2.library.path` set. This is Decision 4 in ADR-0010's defense-in-depth
release procedure (#559), catching regressions that the build-time and pre-deploy
checks cannot reach because they cross trust boundaries (JReleaser staging, Sonatype
Central, the CDN).

## Files

| File | Purpose |
|---|---|
| `SmokeTest.java` | The minimal Java program: bundle assertion + JNA backend init + `a(b\|c)+` match |
| `pom.xml.template` | Maven project template, rendered by the workflow with `@VERSION@` and `@PLATFORM@` substituted |

## Local validation

Once `<version>` is published to Maven Central (e.g. `1.0.1`), reproduce a single
platform locally without waiting for a release:

```bash
cd .github/post-deploy-smoke
sed -e "s/@VERSION@/1.0.1/g" -e "s/@PLATFORM@/linux-x86_64/g" pom.xml.template > pom.xml
mvn -B -ntp -q dependency:build-classpath -Dmdep.outputFile=cp.txt
mkdir -p build && javac -cp "$(cat cp.txt)" -d build SmokeTest.java
java \
  -Dpcre2.library.discovery=false \
  -Djava.util.logging.SimpleFormatter.format='%4$s: %5$s%n' \
  -cp "build:$(cat cp.txt)" \
  SmokeTest
```

Replace `linux-x86_64` with `macos-aarch64`, `macos-x86_64`, `linux-aarch64`, or
`windows-x86_64` to test other bundles. On Windows, replace the `:` classpath
separator with `;`.

Expected output on success (3 `OK` lines, then the summary):

```
OK: bundled native extracted to /tmp/pcre4j-native<random>/
OK: JNA backend initialized
OK: Pattern "a(b|c)+" matched "abbc"

Smoke test PASSED
```

(The first line shows the extraction **directory**; the actual
`libpcre2-8.so`/`.dylib`/`.dll` sits inside it.)

Pinning the smoke test against `1.0.0` is a useful negative check: that release
shipped empty native bundles (the #556 regression), so the first assertion fires
and the program exits 1 with a clear `FAIL: pcre4j-native-* bundle did not provide
an extractable native library` message.

## Why JNA and not FFM

JNA works on Java 21+ with no preview flags or `--enable-native-access` opt-ins, which
keeps the smoke test dependency surface minimal. Adding an FFM variant in the future
is reasonable; this PR keeps the footprint tight to what the #559 acceptance criteria
require.

## Why discovery is disabled

`-Dpcre2.library.discovery=false` disables `Pcre2LibraryFinder` so a system-installed
PCRE2 (Homebrew on macOS, distro packages on Linux) cannot mask an empty bundle by
satisfying the JNA fallback path. The bundle assertion in `SmokeTest.java` is the
primary safety net; disabling discovery makes the negative path observable in CI.
