# ADR-0005: Contract Testing via Shared Test Fixtures

## Status

Accepted

## Context

PCRE4J has two backend implementations (JNA and FFM) that both implement the `IPcre2` interface.
Every backend must behave identically — the same pattern compiled and matched through JNA must
produce the same results as through FFM. Without shared tests, each backend would maintain its own
copy of test cases, and behavioral differences would go undetected.

The challenge is structural: `lib` defines the wrapper API and should own the contract tests, but
`lib` has no compile-time dependency on the backends. The backends depend on `lib`, not the other
way around.

## Decision

We use **Gradle test fixtures** (`java-test-fixtures` plugin) in the `lib` module to publish shared
test infrastructure that both backend modules consume:

- `lib/src/testFixtures/` contains:
  - `BackendProvider` — a utility that reflectively loads `IPcre2` implementations by class name
    (`org.pcre4j.jna.Pcre2`, `org.pcre4j.ffm.Pcre2`) and exposes them as JUnit 5
    `@MethodSource` arguments.
  - `Pcre2Tests` — an abstract base class that implements multiple contract test interfaces (e.g.
    `Pcre2ConfigurationContractTest`, `Pcre2MatchingContractTest`, `Pcre2SubstitutionContractTest`,
    and others). Each interface covers a specific PCRE2 functional area.
  - Individual contract test interfaces containing the actual `@ParameterizedTest` methods.

- Backend modules (`jna`, `ffm`) declare `testImplementation(testFixtures(project(":lib")))` and
  extend `Pcre2Tests` with a concrete subclass that implements `createApi()` for their specific
  backend.

- `lib` itself declares `jna` and `ffm` as `testRuntimeOnly` dependencies so it can run the same
  contract tests against both backends via `BackendProvider` (see ADR-0001 for the test-time
  circular awareness this creates).

Backend discovery is reflective (`Class.forName()`) so that `lib`'s test fixtures have no
compile-time coupling to any backend implementation.

## Consequences

- A single set of contract tests verifies both backends identically. Adding a test to any contract
  interface automatically runs it against JNA and FFM.
- Adding a new backend requires: implementing `IPcre2`, extending `Pcre2Tests`, and registering the
  class name in `BackendProvider`.
- Backend class name changes cause runtime test failures rather than compile-time errors, due to
  the reflective loading strategy.
- Test fixtures are published as a separate artifact (`pcre4j-lib-test-fixtures`), making them
  available to third-party backend implementations.
- The contract test interfaces provide fine-grained test organization by PCRE2 functional area,
  while `Pcre2Tests` composes them into a single runnable suite per backend.
