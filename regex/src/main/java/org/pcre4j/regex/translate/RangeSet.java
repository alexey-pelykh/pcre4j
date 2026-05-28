/*
 * Copyright (C) 2024-2026 Oleksii PELYKH
 *
 * This file is a part of the PCRE4J. The PCRE4J is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see
 * <https://www.gnu.org/licenses/>.
 */
package org.pcre4j.regex.translate;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable, sorted, disjoint set of Unicode code-point ranges over [0, 0x10FFFF].
 *
 * <p>Internally stored as a flat {@code int[]} of the form
 * {@code [lo0, hi0, lo1, hi1, ...]} where {@code lo0 <= hi0 < lo1 <= hi1 < ...}.
 * All endpoints are inclusive.
 */
public final class RangeSet {

    /** The full Unicode code-point space [0, 0x10FFFF]. */
    private static final int MAX_CP = 0x10FFFF;

    /** Empty set. */
    public static final RangeSet EMPTY = new RangeSet(new int[0]);

    /** Set containing every code point [0, MAX_CP]. */
    public static final RangeSet ALL = new RangeSet(new int[]{0, MAX_CP});

    /** Sorted, non-overlapping pairs [lo, hi] (inclusive at both ends). */
    private final int[] ranges;

    private RangeSet(final int[] ranges) {
        this.ranges = ranges;
    }

    /** Creates a set containing the single code point {@code cp}. */
    public static RangeSet single(final int cp) {
        if (cp < 0 || cp > MAX_CP) {
            throw new IllegalArgumentException("Code point out of range: " + cp);
        }
        return new RangeSet(new int[]{cp, cp});
    }

    /** Creates a set containing the range [lo, hi] (inclusive). */
    public static RangeSet range(final int lo, final int hi) {
        if (lo < 0 || hi > MAX_CP || lo > hi) {
            throw new IllegalArgumentException("Invalid range: [" + lo + ", " + hi + "]");
        }
        return new RangeSet(new int[]{lo, hi});
    }

    /** Creates a {@code RangeSet} from a flat {@code int[lo, hi, ...]} array (already normalised). */
    static RangeSet fromSortedPairs(final int[] pairs) {
        return new RangeSet(pairs.clone());
    }

    /** Returns the union of this set and {@code other}. */
    public RangeSet union(final RangeSet other) {
        if (this.isEmpty()) {
            return other;
        }
        if (other.isEmpty()) {
            return this;
        }
        // Merge two sorted range lists
        final List<Integer> merged = new ArrayList<>(this.ranges.length + other.ranges.length);
        int i = 0;
        int j = 0;
        while (i < this.ranges.length && j < other.ranges.length) {
            if (this.ranges[i] <= other.ranges[j]) {
                merged.add(this.ranges[i]);
                merged.add(this.ranges[i + 1]);
                i += 2;
            } else {
                merged.add(other.ranges[j]);
                merged.add(other.ranges[j + 1]);
                j += 2;
            }
        }
        while (i < this.ranges.length) {
            merged.add(this.ranges[i]);
            merged.add(this.ranges[i + 1]);
            i += 2;
        }
        while (j < other.ranges.length) {
            merged.add(other.ranges[j]);
            merged.add(other.ranges[j + 1]);
            j += 2;
        }
        return normalise(merged);
    }

    /** Returns the intersection of this set and {@code other}. */
    public RangeSet intersect(final RangeSet other) {
        if (this.isEmpty() || other.isEmpty()) {
            return EMPTY;
        }
        final List<Integer> result = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (i < this.ranges.length && j < other.ranges.length) {
            final int lo = Math.max(this.ranges[i], other.ranges[j]);
            final int hi = Math.min(this.ranges[i + 1], other.ranges[j + 1]);
            if (lo <= hi) {
                result.add(lo);
                result.add(hi);
            }
            // advance the one that ends first
            if (this.ranges[i + 1] < other.ranges[j + 1]) {
                i += 2;
            } else {
                j += 2;
            }
        }
        return fromList(result);
    }

    /** Returns the complement of this set within [0, MAX_CP]. */
    public RangeSet complement() {
        if (this.isEmpty()) {
            return ALL;
        }
        final List<Integer> result = new ArrayList<>();
        int prev = 0;
        for (int i = 0; i < ranges.length; i += 2) {
            final int lo = ranges[i];
            final int hi = ranges[i + 1];
            if (prev < lo) {
                result.add(prev);
                result.add(lo - 1);
            }
            prev = hi + 1;
        }
        if (prev <= MAX_CP) {
            result.add(prev);
            result.add(MAX_CP);
        }
        return fromList(result);
    }

    /** Returns {@code this - other} (set difference). */
    public RangeSet subtract(final RangeSet other) {
        return this.intersect(other.complement());
    }

    /** Returns {@code true} if this set contains no code points. */
    public boolean isEmpty() {
        return ranges.length == 0;
    }

    /** Returns {@code true} if this set contains the code point {@code cp}. */
    public boolean contains(final int cp) {
        for (int i = 0; i < ranges.length; i += 2) {
            if (cp >= ranges[i] && cp <= ranges[i + 1]) {
                return true;
            }
            if (cp < ranges[i]) {
                break;
            }
        }
        return false;
    }

    /**
     * Emits the content of this set as a PCRE2 character class body (NOT wrapped in {@code [...]}).
     *
     * <p>Printable ASCII code points in the range 0x20–0x7E are emitted literally, EXCEPT for
     * {@code \}, {@code ]}, {@code ^}, {@code -} which are backslash-escaped.  All other code
     * points are emitted as {@code \x{HH...}}.
     *
     * <p>Contiguous ranges of two or more code points are emitted as {@code lo-hi} when both
     * endpoints are printable ASCII, or as {@code \x{LO}-\x{HI}} otherwise.
     */
    public String toPcre2ClassBody() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ranges.length; i += 2) {
            final int lo = ranges[i];
            final int hi = ranges[i + 1];
            if (lo == hi) {
                appendCp(sb, lo);
            } else {
                appendCp(sb, lo);
                sb.append('-');
                appendCp(sb, hi);
            }
        }
        return sb.toString();
    }

    /** Returns the number of ranges in this set (for testing). */
    int rangeCount() {
        return ranges.length / 2;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "RangeSet{}";
        }
        final StringBuilder sb = new StringBuilder("RangeSet{");
        for (int i = 0; i < ranges.length; i += 2) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(String.format("U+%04X-U+%04X", ranges[i], ranges[i + 1]));
        }
        sb.append('}');
        return sb.toString();
    }

    // -----------------------------------------------------------------------
    // private helpers
    // -----------------------------------------------------------------------

    /** Emits a single code point in the class-body representation. */
    private static void appendCp(final StringBuilder sb, final int cp) {
        if (cp >= 0x20 && cp <= 0x7E) {
            // Printable ASCII — emit raw except for chars with special meaning inside [...]
            switch ((char) cp) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case ']':
                    sb.append("\\]");
                    break;
                case '^':
                    sb.append("\\^");
                    break;
                case '-':
                    sb.append("\\-");
                    break;
                default:
                    sb.append((char) cp);
                    break;
            }
        } else if (cp >= 0xD800 && cp <= 0xDFFF) {
            // Lone surrogate — emit as raw char (same reason as in ClassRenderer)
            sb.append((char) cp);
        } else {
            sb.append(String.format("\\x{%X}", cp));
        }
    }

    /** Normalises a list of [lo, hi] pairs by merging overlapping/adjacent ranges. */
    private static RangeSet normalise(final List<Integer> raw) {
        if (raw.isEmpty()) {
            return EMPTY;
        }
        final List<Integer> result = new ArrayList<>(raw.size());
        int curLo = raw.get(0);
        int curHi = raw.get(1);
        for (int i = 2; i < raw.size(); i += 2) {
            final int lo = raw.get(i);
            final int hi = raw.get(i + 1);
            if (lo <= curHi + 1) {
                // Overlap or adjacent — merge
                curHi = Math.max(curHi, hi);
            } else {
                result.add(curLo);
                result.add(curHi);
                curLo = lo;
                curHi = hi;
            }
        }
        result.add(curLo);
        result.add(curHi);
        return fromList(result);
    }

    private static RangeSet fromList(final List<Integer> list) {
        final int[] arr = new int[list.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = list.get(i);
        }
        return new RangeSet(arr);
    }
}
