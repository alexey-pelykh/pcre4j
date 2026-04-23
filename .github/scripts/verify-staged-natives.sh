#!/usr/bin/env bash
#
# Copyright (C) 2026 Oleksii PELYKH
#
# This file is a part of the PCRE4J. The PCRE4J is free software: you can redistribute it and/or modify it under the
# terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
# warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
# details.
#
# You should have received a copy of the GNU Lesser General Public License along with this program. If not, see
# <https://www.gnu.org/licenses/>.
#

# Pre-deploy staging inspection for PCRE4J native bundles.
#
# Walks `<root>/**/build/staging-deploy/**/*.jar`, picks out the platform-
# specific `pcre4j-native-<platform>-<version>.jar` main artifacts (ignoring
# `-sources.jar` / `-javadoc.jar` and the POM-only `pcre4j-native-all` JAR),
# and fails if any main artifact is smaller than the threshold OR lacks a
# non-trivial entry under `META-INF/native/<platform>/`.
#
# This is the last line of defense before `jreleaserDeploy`. Its sole job is
# to stop a release that would ship empty native JARs such as those published
# in 1.0.0.

set -euo pipefail

# -----------------------------------------------------------------------------
# Configuration
# -----------------------------------------------------------------------------
MIN_JAR_SIZE_BYTES="${MIN_JAR_SIZE_BYTES:-10240}"
MIN_ENTRY_SIZE_BYTES="${MIN_ENTRY_SIZE_BYTES:-10240}"
ROOT="${1:-.}"

PLATFORMS=(
  linux-x86_64
  linux-aarch64
  macos-x86_64
  macos-aarch64
  windows-x86_64
)

# -----------------------------------------------------------------------------
# Pretty messages — mirror GitHub Actions conventions so warnings/errors show up
# inline in the workflow run when invoked from CI.
# -----------------------------------------------------------------------------
log()   { printf '%s\n' "$*"; }
warn()  { printf '::warning::%s\n' "$*"; }
error() { printf '::error::%s\n' "$*" >&2; }

is_known_platform() {
  local candidate="$1" p
  for p in "${PLATFORMS[@]}"; do
    [[ "$p" == "$candidate" ]] && return 0
  done
  return 1
}

# Classify a JAR filename.
# Prints one of: main:<platform> | main-all | sources | javadoc | non-native
classify_jar() {
  local base="$1"
  local stem="${base%.jar}"
  case "$stem" in
    *-sources) echo "sources"; return ;;
    *-javadoc) echo "javadoc"; return ;;
  esac
  if [[ "$stem" != pcre4j-native-* ]]; then
    echo "non-native"
    return
  fi
  # Strip the trailing -<version>. The version may be `X.Y.Z`, `X.Y.Z-SNAPSHOT`,
  # `X.Y.Z-RC1`, `main-SNAPSHOT`, or `PR-<n>-SNAPSHOT`; all are ASCII and end
  # after the final `-` that introduces them. Our leaf is `pcre4j-native-<platform>-<version>`
  # with platforms limited to `<os>-<arch>` (one hyphen) or the literal `all`.
  #
  # Strategy: try progressively-longer platform candidates against PLATFORMS
  # and `all`, picking the first that matches `pcre4j-native-<candidate>-`.
  local stripped="${stem#pcre4j-native-}"  # e.g. linux-x86_64-1.0.1 or all-1.0.1
  # Try 2-segment platform names first (os-arch), then 1-segment ("all").
  local pfx
  for pfx in "${PLATFORMS[@]}" "all"; do
    if [[ "$stripped" == "$pfx-"* ]]; then
      if [[ "$pfx" == "all" ]]; then
        echo "main-all"
      else
        echo "main:$pfx"
      fi
      return
    fi
  done
  echo "non-native"
}

# Portable byte size of a regular file.
file_size() {
  local path="$1"
  if [[ "$(uname -s)" == "Darwin" ]]; then
    stat -f '%z' "$path"
  else
    stat -c '%s' "$path"
  fi
}

# Largest uncompressed entry under a given prefix inside a JAR, printed as bytes.
# Prints 0 if no entry matches.
#
# Uses `unzip -l` (available on every GitHub-hosted runner). `unzip -l` emits:
#     Length      Date    Time    Name
#   ---------  ---------- -----   ----
#        123  2024-01-01 00:00   META-INF/native/linux-x86_64/libpcre2-8.so
#         0  2024-01-01 00:00   META-INF/native/linux-x86_64/
#   ---------                     -------
#        123                     1 file
# We want the maximum numeric Length among data entries (exclude directory
# entries, whose size is 0 and whose Name ends with `/`, and exclude the
# summary separator rows).
max_entry_size_under_prefix() {
  local jar="$1" prefix="$2"
  unzip -l "$jar" 2>/dev/null \
    | awk -v pfx="$prefix" '
        $1 ~ /^[0-9]+$/ && $NF ~ ("^" pfx) && $NF !~ /\/$/ {
          if ($1 + 0 > max) max = $1 + 0
        }
        END { print max + 0 }
      '
}

# -----------------------------------------------------------------------------
# Walk staging-deploy directories.
# -----------------------------------------------------------------------------
if [[ ! -d "$ROOT" ]]; then
  error "staging root does not exist: $ROOT"
  exit 2
fi

log "Inspecting staged native bundles under: $ROOT"
log "  MIN_JAR_SIZE_BYTES=$MIN_JAR_SIZE_BYTES"
log "  MIN_ENTRY_SIZE_BYTES=$MIN_ENTRY_SIZE_BYTES"
log ""

checked=0
failed=0

# `find` is null-safe and avoids shell-glob recursion limits. -path matches
# Gradle's staging layout (any depth under `{module}/build/staging-deploy/`).
while IFS= read -r -d '' jar; do
  base="$(basename "$jar")"
  kind="$(classify_jar "$base")"

  case "$kind" in
    sources|javadoc|non-native|main-all)
      # sources/javadoc/main-all: not in scope for library-presence check.
      # native-all is a POM-only aggregator that ships no library of its own.
      continue
      ;;
    main:*)
      platform="${kind#main:}"
      ;;
    *)
      warn "unknown JAR classification '$kind' for $base; skipping"
      continue
      ;;
  esac

  if ! is_known_platform "$platform"; then
    warn "unknown platform '$platform' for $base; skipping"
    continue
  fi

  checked=$((checked+1))
  log "Checking $jar (platform=$platform)"

  jar_size="$(file_size "$jar")"
  if (( jar_size < MIN_JAR_SIZE_BYTES )); then
    error "$base is below threshold: $jar_size bytes (expected >= $MIN_JAR_SIZE_BYTES); native library appears to be missing"
    failed=$((failed+1))
    continue
  fi

  prefix="META-INF/native/$platform/"
  entry_size="$(max_entry_size_under_prefix "$jar" "$prefix")"
  if (( entry_size < MIN_ENTRY_SIZE_BYTES )); then
    error "$base is missing or has too small an entry under $prefix (largest entry is $entry_size bytes, expected >= $MIN_ENTRY_SIZE_BYTES)"
    failed=$((failed+1))
    continue
  fi

  log "  OK: jar=$jar_size bytes, largest $prefix entry=$entry_size bytes"
done < <(find "$ROOT" -type f -name '*.jar' -path '*/build/staging-deploy/*' -print0)

log ""
log "Summary: checked $checked native bundle(s); $failed failing"

if (( failed > 0 )); then
  error "staging inspection FAILED: $failed native bundle(s) are not deployable"
  exit 1
fi

log "Staging inspection PASSED"
