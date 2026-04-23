# ADR-0010: Native Bundle Release Procedure

## Status

Proposed

## Context

PCRE4J 1.0.0 introduced per-platform native-bundle artifacts (`pcre4j-native-linux-x86_64`,
`pcre4j-native-linux-aarch64`, `pcre4j-native-macos-x86_64`, `pcre4j-native-macos-aarch64`,
`pcre4j-native-windows-x86_64`, and the aggregate `pcre4j-native-all`) so consumers could depend on
PCRE4J without installing PCRE2 system-wide. The intent was that each artifact would carry a
pre-built `libpcre2-8.{so|dylib|dll}` under `META-INF/native/<platform>/`, which
`Pcre2NativeLoader` would extract at runtime.

The 1.0.0 release shipped these artifacts empty. Each `pcre4j-native-<platform>-1.0.0.jar` on
Maven Central is 659–667 bytes and contains only `META-INF/MANIFEST.MF` and an empty `.gitkeep`
placeholder; `pcre4j-native-all-1.0.0.jar` is 261 bytes (manifest only). Consumers relying on the
bundled natives hit `UnsatisfiedLinkError` with no diagnostic path back to the bundle being empty
(see [#556](https://github.com/alexey-pelykh/pcre4j/issues/556)).

The regression was the product of several compounding gaps:

1. **Release pipeline never built natives.** `.github/workflows/release.yaml` ran on a single
   `ubuntu-24.04` runner and built PCRE2 once into `/opt/pcre2/lib`, but nothing copied the built
   library into any `native/<platform>/src/main/resources/META-INF/native/<platform>/` directory
   before `./gradlew publishAllPublicationsToStagingDeployRepository`. Each `native-<platform>`
   module's `src/main/resources/META-INF/native/<platform>/` directory was present but empty
   except for a committed `.gitkeep` placeholder.
2. **Gradle packaged empty resource directories silently.** The `java-library` plugin treats
   empty resource directories (containing only `.gitkeep`) as valid. The build produced a 659-byte
   JAR, passed all checks, and published without warning.
3. **No build-time verification.** There was no task that asserted "this native JAR contains a
   shared library of non-trivial size." The release gate was "Gradle produced a JAR" — not "Gradle
   produced a usable JAR."
4. **No runtime-level integration test.** `Pcre2NativeLoaderTest` covered OS/arch detection, input
   validation, and the ignore-bypass, but did not exercise the extraction path end-to-end against
   a real bundled resource. The tests passed whether the bundle was populated or empty.
5. **No post-deploy smoke test.** Nothing pulled the freshly-published artifacts back from Maven
   Central and executed PCRE4J end-to-end on each supported platform.
6. **An unused workflow masked the problem.** `.github/workflows/build-natives.yaml` already
   contained the correct 5-platform matrix build logic, but was `workflow_dispatch`-only, had
   never been dispatched (`total_count: 0`), and used 7-day artifact retention — so even if
   triggered manually, its outputs could not feed `release.yaml`.

The pre-1.0.0 release path (source-built PCRE2 only) remained functional in 1.0.0 via
`-Dpcre2.library.path`, `jna.library.path`, or `Pcre2LibraryFinder` auto-discovery. The regression
is scoped exclusively to the newly-introduced native-bundle artifacts shipped for the first time
in 1.0.0 (PR #515, merged 2026-02-14).

A durable fix requires more than restoring the missing copy step. Each gap above eliminated an
opportunity to catch the defect; the release procedure must establish multiple independent checks
so that recovery of the native bundle does not depend on any single point of correctness.

## Decision

The release procedure for native bundles is codified as **five compounding checks**, each
independently capable of catching a regression that the others missed:

### 1. Matrix-build natives within `release.yaml`

Native libraries are built on a 5-platform GitHub Actions matrix (ubuntu-24.04 x86_64,
ubuntu-24.04-arm aarch64, macos-13 x86_64, macos-14 aarch64, windows-2022 x86_64) **within the
same workflow run that publishes to Maven Central**. Each matrix job:

- Builds PCRE2 from source using the shared `.github/actions/build-pcre2` composite action.
- Copies the produced `libpcre2-8.{so|dylib|dll}` into
  `native/<platform>/src/main/resources/META-INF/native/<platform>/`.
- Uploads the populated directory as a workflow artifact.

A downstream `package` job downloads all five artifacts into the expected resource directories
before running Gradle publish. The `build-natives.yaml` workflow retains its `workflow_dispatch`
entry point for on-demand diagnostic builds but is no longer the source of truth for release
artifacts — the matrix-build-then-publish path in `release.yaml` is.

### 2. Build-time verification task (`verifyNativeBundles`)

A Gradle verification task (`verifyNativeBundles`, per [#557](https://github.com/alexey-pelykh/pcre4j/issues/557))
opens each `native/<platform>/build/libs/native-<platform>-*.jar`, asserts the presence of
`META-INF/native/<platform>/libpcre2-8.{so|dylib|dll}` (or `pcre2-8.dll` on Windows), and fails
the build if the entry is missing or below a minimum size threshold (10 KB; a real PCRE2 shared
library is ~500 KB to ~1 MB depending on platform, so 10 KB is a generous lower bound that still
catches empty placeholders, zero-size files, and manifest-only JARs). The task runs **before**
`publishAllPublicationsToStagingDeployRepository` in `release.yaml`, so a missing native on any
of the five platforms fails the release before anything is staged.

### 3. Fixture-based `Pcre2NativeLoader` integration test

A JUnit integration test (per [#558](https://github.com/alexey-pelykh/pcre4j/issues/558)) places
a synthetic native resource under `META-INF/native/<test-platform>/` in the test classpath and
asserts that `Pcre2NativeLoader.load()` extracts it, returns a non-empty `Optional<Path>`, and
that the extracted file's bytes match the fixture. Negative scenarios verify that a missing
resource returns empty and that a `.gitkeep`-only directory is treated as the "empty bundle"
failure mode with a WARNING log — the signal that would have surfaced #556 at runtime during
normal CI even if all other checks were bypassed.

### 4. Post-deploy smoke test of Maven Central bundles

A post-release GitHub Actions workflow (per [#559](https://github.com/alexey-pelykh/pcre4j/issues/559))
runs on ubuntu, macOS, and Windows runners with **no** `pcre2.library.path` set, resolves
`org.pcre4j:pcre4j-native-<runner-platform>:<version>` from Maven Central (not the local build),
compiles a trivial pattern through PCRE4J, and executes a match. The test fails the workflow and
opens a tracking issue if resolution or execution fails on any platform. This check crosses every
trust boundary (local build → JReleaser staging → Sonatype Central → CDN) and is the only check
that exercises the artifact that consumers will actually download.

### 5. `.gitkeep` placeholder policy

The `.gitkeep` files in `native/<platform>/src/main/resources/META-INF/native/<platform>/` exist
to keep the empty directories under version control for clean checkouts. They are retained under
the following policy:

- The matrix-build step in `release.yaml` does **not** remove `.gitkeep` before copying the
  library — the library and the placeholder coexist in the final directory.
- `verifyNativeBundles` asserts the presence of the library, not the absence of `.gitkeep`.
- The fixture-based loader test explicitly covers the "placeholder-only" scenario (a directory
  containing `.gitkeep` but no library) and treats it as a failure mode with a WARNING log.

The combination ensures that a developer clone has the directory structure preserved without
making `.gitkeep` load-bearing for the release pipeline or runtime behaviour.

### Relationship between the five checks

Each check guards a different failure surface:

| Check | Catches | Would have caught #556? |
|-------|---------|-------------------------|
| Matrix-build in `release.yaml` | Missing cross-platform build step | Yes (by construction) |
| `verifyNativeBundles` | Missing library in the local JAR | Yes |
| Fixture-based loader test | Logic bugs in extraction / placeholder handling | Yes (via placeholder scenario) |
| Post-deploy smoke test | Artifact corruption across the publish pipeline | Yes |
| `.gitkeep` policy | Placeholder leaking into final JAR | Yes (flagged by loader test) |

The checks are intentionally redundant. No single check is load-bearing for correctness — any
one of them alone would have caught #556.

## Consequences

- **Release pipeline duration increases.** A single-runner release becomes a 6-runner release:
  5 matrix jobs build PCRE2 and copy libraries in parallel, then a packaging job waits for all
  five artifacts before running Gradle publish. Wall-clock time for a release grows by roughly
  the duration of one matrix PCRE2 build plus the artifact-download overhead — acceptable given
  releases are infrequent and the trust cost of shipping empty JARs outweighs the added latency.
- **GitHub Actions runner consumption increases.** The release workflow now consumes six runner
  slots (five matrix + one packaging) instead of one. On a public repository this stays within
  GitHub's free-minute allowance for standard Linux runners; macOS runners carry a higher
  per-minute cost multiplier on private-repo billing, which is relevant if the project ever moves
  to a private fork for release staging. At the current release cadence, runner cost is not a
  binding constraint, but it is the single largest variable cost of the new procedure.
- **Staging artifact complexity.** The packaging job depends on five artifact uploads succeeding
  before it runs. An artifact upload failure on any platform fails the release — which is the
  desired safety behaviour (better a failed release than a partial publish) but requires release
  engineers to debug upload failures as a first-class concern. Artifact retention for the
  populated `META-INF/native/` directories is set to the default 90 days, shared across the
  matrix; this is sufficient for post-release forensics.
- **Platform coverage is frozen at 5 platforms.** Adding a new platform (e.g., linux-riscv64,
  freebsd-x86_64) requires adding a row to the matrix, a corresponding `native-<platform>` Gradle
  module, and a new fixture for the loader test. Platform additions become a schema change, not a
  configuration tweak.
- **`build-natives.yaml` becomes secondary.** Its ongoing purpose is diagnostic — reproducing a
  single-platform build off-cycle without triggering a full release. It remains
  `workflow_dispatch`-only to avoid being mistaken for a release path.
- **Release engineers need not manually verify bundle contents.** The five checks guarantee that
  any empty, undersized, or misnamed bundle fails the release before Maven Central staging. A
  release that completes successfully is a release where all five platforms have verified native
  bundles in Maven Central.
- **Downstream consumers gain two guarantees that 1.0.0 lacked**: (1) every future release has
  populated native bundles for each supported platform, and (2) regressions introduced in the
  release pipeline are caught by at least one of the five independent checks rather than
  discovered by consumers in production.
