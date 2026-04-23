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

# Tests for verify-staged-natives.sh.
#
# Each test builds a fixture tree that mirrors the layout
# `{module}/build/staging-deploy/**/*.jar`, invokes the script, and asserts on
# exit status plus the error-output fingerprint.
#
# Requires: bash >= 4, zip (both available on ubuntu-24.04 runners).

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SCRIPT="$SCRIPT_DIR/verify-staged-natives.sh"
VERSION="1.0.1"
PLATFORMS=(linux-x86_64 linux-aarch64 macos-x86_64 macos-aarch64 windows-x86_64)

if [[ ! -x "$SCRIPT" ]]; then
  echo "FATAL: $SCRIPT is not executable" >&2
  exit 2
fi

WORK_ROOT="$(mktemp -d -t verify-staged-natives.XXXXXX)"
trap 'rm -rf "$WORK_ROOT"' EXIT

PASS=0
FAIL=0
FAILED_CASES=()

# lib_filename <platform> -> filename of the bundled native library for the platform
lib_filename() {
  case "$1" in
    linux-*) echo "libpcre2-8.so.0.13.0" ;;
    macos-*) echo "libpcre2-8.0.13.0.dylib" ;;
    windows-*) echo "pcre2-8.dll" ;;
    *) echo "unknown" ;;
  esac
}

# make_jar <output_path> <entry_path>=<content_size_or_file>...
# Creates a JAR with the given entries. Each entry is:
#   path=SIZE     -> zero-filled content of SIZE bytes
#   path=@file    -> copy of an existing file
# A default MANIFEST.MF is always included.
make_jar() {
  local jar_path="$1"; shift
  local staging
  staging="$(mktemp -d -p "$WORK_ROOT")"
  mkdir -p "$staging/META-INF"
  printf 'Manifest-Version: 1.0\r\n\r\n' > "$staging/META-INF/MANIFEST.MF"

  local entry path spec
  for entry in "$@"; do
    path="${entry%%=*}"
    spec="${entry#*=}"
    mkdir -p "$staging/$(dirname "$path")"
    if [[ "$spec" == @* ]]; then
      cp "${spec:1}" "$staging/$path"
    else
      # Random bytes so the JAR doesn't shrink to near-zero under zip's DEFLATE
      # (real shared libraries contain entropic binary content).
      head -c "$spec" /dev/urandom > "$staging/$path"
    fi
  done

  mkdir -p "$(dirname "$jar_path")"
  rm -f "$jar_path"
  ( cd "$staging" && zip -q -r "$jar_path" . )
  rm -rf "$staging"
}

# empty_jar_with_gitkeep <output_path>
# Crafts the pathological 1.0.0 release shape: MANIFEST.MF + .gitkeep, no content.
empty_jar_with_gitkeep() {
  local jar_path="$1"
  local staging
  staging="$(mktemp -d -p "$WORK_ROOT")"
  mkdir -p "$staging/META-INF"
  printf 'Manifest-Version: 1.0\r\n\r\n' > "$staging/META-INF/MANIFEST.MF"
  : > "$staging/.gitkeep"
  mkdir -p "$(dirname "$jar_path")"
  rm -f "$jar_path"
  ( cd "$staging" && zip -q -r "$jar_path" . )
  rm -rf "$staging"
}

# Build a complete, valid staging tree.
# Args: <root_dir> [<version>]
build_good_tree() {
  local root="$1"
  local version="${2:-$VERSION}"
  local platform
  for platform in "${PLATFORMS[@]}"; do
    local module_dir="$root/native/$platform/build/staging-deploy/org/pcre4j/pcre4j-native-$platform/$version"
    local main_jar="$module_dir/pcre4j-native-$platform-$version.jar"
    local lib
    lib="$(lib_filename "$platform")"
    make_jar "$main_jar" "META-INF/native/$platform/$lib=32768"
    make_jar "$module_dir/pcre4j-native-$platform-$version-sources.jar" "README.txt=128"
    make_jar "$module_dir/pcre4j-native-$platform-$version-javadoc.jar" "index.html=256"
  done
  local all_dir="$root/native/all/build/staging-deploy/org/pcre4j/pcre4j-native-all/$version"
  # pcre4j-native-all is a POM-only aggregator; its main JAR has no resources.
  make_jar "$all_dir/pcre4j-native-all-$version.jar"
  make_jar "$all_dir/pcre4j-native-all-$version-sources.jar" "README.txt=128"
  make_jar "$all_dir/pcre4j-native-all-$version-javadoc.jar" "index.html=256"
  # Non-native siblings that must be ignored.
  local api_dir="$root/api/build/staging-deploy/org/pcre4j/pcre4j-api/$version"
  make_jar "$api_dir/pcre4j-api-$version.jar" "org/pcre4j/api/IPcre2.class=1024"
  make_jar "$api_dir/pcre4j-api-$version-sources.jar" "org/pcre4j/api/IPcre2.java=512"
  make_jar "$api_dir/pcre4j-api-$version-javadoc.jar" "index.html=256"
}

run_case() {
  local name="$1"
  local expected_status="$2"
  shift 2
  local tree="$1"; shift
  local stdout_file stderr_file status=0
  stdout_file="$(mktemp -p "$WORK_ROOT")"
  stderr_file="$(mktemp -p "$WORK_ROOT")"
  set +e
  "$SCRIPT" "$tree" > "$stdout_file" 2> "$stderr_file"
  status=$?
  set -e

  local ok=1
  if [[ "$expected_status" == "0" && "$status" -ne 0 ]]; then
    ok=0
  fi
  if [[ "$expected_status" == "nonzero" && "$status" -eq 0 ]]; then
    ok=0
  fi

  # Remaining positional args are literal substrings that MUST appear in combined output.
  local combined="$(cat "$stdout_file" "$stderr_file")"
  local needle
  for needle in "$@"; do
    if ! grep -qF -- "$needle" <<<"$combined"; then
      ok=0
      echo "   MISSING substring: $needle" >&2
    fi
  done

  if [[ "$ok" -eq 1 ]]; then
    PASS=$((PASS+1))
    echo "ok  - $name (exit=$status)"
  else
    FAIL=$((FAIL+1))
    FAILED_CASES+=("$name")
    echo "FAIL - $name (exit=$status, expected=$expected_status)"
    echo "   stdout:"; sed 's/^/     /' "$stdout_file" >&2
    echo "   stderr:"; sed 's/^/     /' "$stderr_file" >&2
  fi
}

# ----------------------------------------------------------------------------
# Case 1: PASS — good release, all 5 platforms + native-all + unrelated modules
# ----------------------------------------------------------------------------
tree1="$WORK_ROOT/tree1"
build_good_tree "$tree1"
run_case "pass_good_release" "0" "$tree1"

# ----------------------------------------------------------------------------
# Case 2: FAIL — empty JAR shape from 1.0.0 (AC scenario: fail case)
# ----------------------------------------------------------------------------
tree2="$WORK_ROOT/tree2"
build_good_tree "$tree2"
empty_jar_with_gitkeep \
  "$tree2/native/linux-x86_64/build/staging-deploy/org/pcre4j/pcre4j-native-linux-x86_64/$VERSION/pcre4j-native-linux-x86_64-$VERSION.jar"
run_case "fail_empty_jar_shape_from_1_0_0" "nonzero" "$tree2" \
  "pcre4j-native-linux-x86_64-$VERSION.jar" \
  "below threshold"

# ----------------------------------------------------------------------------
# Case 3: FAIL — JAR large enough but missing META-INF/native/<platform>/ content
# ----------------------------------------------------------------------------
tree3="$WORK_ROOT/tree3"
build_good_tree "$tree3"
jar3="$tree3/native/macos-aarch64/build/staging-deploy/org/pcre4j/pcre4j-native-macos-aarch64/$VERSION/pcre4j-native-macos-aarch64-$VERSION.jar"
rm -f "$jar3"
# JAR is >10KB because of bulk filler, but has NO META-INF/native/macos-aarch64/ entry
make_jar "$jar3" "org/pcre4j/filler/irrelevant.bin=32768"
run_case "fail_jar_lacks_meta_inf_native_entry" "nonzero" "$tree3" \
  "pcre4j-native-macos-aarch64-$VERSION.jar" \
  "META-INF/native/macos-aarch64/"

# ----------------------------------------------------------------------------
# Case 4: FAIL — entry under META-INF/native/<platform>/ exists but is too small
# ----------------------------------------------------------------------------
tree4="$WORK_ROOT/tree4"
build_good_tree "$tree4"
jar4="$tree4/native/windows-x86_64/build/staging-deploy/org/pcre4j/pcre4j-native-windows-x86_64/$VERSION/pcre4j-native-windows-x86_64-$VERSION.jar"
rm -f "$jar4"
# JAR itself is >10KB thanks to the filler, but the library entry is only 2KB,
# so the entry-size check is what must fail here (not the JAR-size check).
make_jar "$jar4" \
  "org/pcre4j/filler/big.bin=32768" \
  "META-INF/native/windows-x86_64/pcre2-8.dll=2048"
run_case "fail_entry_present_but_too_small" "nonzero" "$tree4" \
  "pcre4j-native-windows-x86_64-$VERSION.jar" \
  "META-INF/native/windows-x86_64/"

# ----------------------------------------------------------------------------
# Case 5: PASS — native-all JAR is tiny (POM-only aggregator by design)
# ----------------------------------------------------------------------------
tree5="$WORK_ROOT/tree5"
build_good_tree "$tree5"
# native-all is already tiny in build_good_tree; re-stamp as explicitly empty to be sure
all_jar5="$tree5/native/all/build/staging-deploy/org/pcre4j/pcre4j-native-all/$VERSION/pcre4j-native-all-$VERSION.jar"
rm -f "$all_jar5"
empty_jar_with_gitkeep "$all_jar5"
run_case "pass_native_all_tiny_by_design" "0" "$tree5"

# ----------------------------------------------------------------------------
# Case 6: PASS — sources/javadoc siblings are small but must be ignored
# ----------------------------------------------------------------------------
tree6="$WORK_ROOT/tree6"
build_good_tree "$tree6"
# Make the sources/javadoc JARs trivially tiny for one platform
src_dir6="$tree6/native/linux-aarch64/build/staging-deploy/org/pcre4j/pcre4j-native-linux-aarch64/$VERSION"
rm -f "$src_dir6/pcre4j-native-linux-aarch64-$VERSION-sources.jar" \
      "$src_dir6/pcre4j-native-linux-aarch64-$VERSION-javadoc.jar"
empty_jar_with_gitkeep "$src_dir6/pcre4j-native-linux-aarch64-$VERSION-sources.jar"
empty_jar_with_gitkeep "$src_dir6/pcre4j-native-linux-aarch64-$VERSION-javadoc.jar"
run_case "pass_sources_and_javadoc_ignored" "0" "$tree6"

# ----------------------------------------------------------------------------
# Case 7: PASS — non-native staged JARs (pcre4j-api etc.) are small/ignored
# ----------------------------------------------------------------------------
tree7="$WORK_ROOT/tree7"
build_good_tree "$tree7"
api_dir7="$tree7/api/build/staging-deploy/org/pcre4j/pcre4j-api/$VERSION"
rm -f "$api_dir7/pcre4j-api-$VERSION.jar"
# Deliberately tiny — must not be checked by this script.
empty_jar_with_gitkeep "$api_dir7/pcre4j-api-$VERSION.jar"
run_case "pass_non_native_modules_ignored" "0" "$tree7"

# ----------------------------------------------------------------------------
# Summary
# ----------------------------------------------------------------------------
echo
echo "Results: $PASS passed, $FAIL failed"
if [[ "$FAIL" -gt 0 ]]; then
  echo "Failed cases:"
  for c in "${FAILED_CASES[@]}"; do
    echo "  - $c"
  done
  exit 1
fi
