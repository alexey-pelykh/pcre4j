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

import java.util.Arrays;

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
        final int[] a = this.ranges;
        final int[] b = other.ranges;
        final int[] merged = new int[a.length + b.length];
        int i = 0;
        int j = 0;
        int k = 0;
        while (i < a.length && j < b.length) {
            if (a[i] <= b[j]) {
                merged[k++] = a[i];
                merged[k++] = a[i + 1];
                i += 2;
            } else {
                merged[k++] = b[j];
                merged[k++] = b[j + 1];
                j += 2;
            }
        }
        while (i < a.length) {
            merged[k++] = a[i];
            merged[k++] = a[i + 1];
            i += 2;
        }
        while (j < b.length) {
            merged[k++] = b[j];
            merged[k++] = b[j + 1];
            j += 2;
        }
        return normalise(merged, k);
    }

    /** Returns the intersection of this set and {@code other}. */
    public RangeSet intersect(final RangeSet other) {
        if (this.isEmpty() || other.isEmpty()) {
            return EMPTY;
        }
        final int[] a = this.ranges;
        final int[] b = other.ranges;
        final int[] tmp = new int[a.length + b.length];
        int i = 0;
        int j = 0;
        int k = 0;
        while (i < a.length && j < b.length) {
            final int lo = Math.max(a[i], b[j]);
            final int hi = Math.min(a[i + 1], b[j + 1]);
            if (lo <= hi) {
                tmp[k++] = lo;
                tmp[k++] = hi;
            }
            if (a[i + 1] < b[j + 1]) {
                i += 2;
            } else {
                j += 2;
            }
        }
        return finish(tmp, k);
    }

    /** Returns the complement of this set within [0, MAX_CP]. */
    public RangeSet complement() {
        if (this.isEmpty()) {
            return ALL;
        }
        final int[] tmp = new int[ranges.length + 2];
        int k = 0;
        int prev = 0;
        for (int i = 0; i < ranges.length; i += 2) {
            if (prev < ranges[i]) {
                tmp[k++] = prev;
                tmp[k++] = ranges[i] - 1;
            }
            prev = ranges[i + 1] + 1;
        }
        if (prev <= MAX_CP) {
            tmp[k++] = prev;
            tmp[k++] = MAX_CP;
        }
        return finish(tmp, k);
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
            ClassRenderer.emitLiteralInClass(lo, sb);
            if (lo != hi) {
                sb.append('-');
                ClassRenderer.emitLiteralInClass(hi, sb);
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

    /** Merges overlapping/adjacent ranges in {@code raw[0..len)} (pairs). */
    private static RangeSet normalise(final int[] raw, final int len) {
        if (len == 0) {
            return EMPTY;
        }
        final int[] out = new int[len];
        int n = 0;
        int curLo = raw[0];
        int curHi = raw[1];
        for (int i = 2; i < len; i += 2) {
            final int lo = raw[i];
            final int hi = raw[i + 1];
            if (lo <= curHi + 1) {
                curHi = Math.max(curHi, hi);
            } else {
                out[n++] = curLo;
                out[n++] = curHi;
                curLo = lo;
                curHi = hi;
            }
        }
        out[n++] = curLo;
        out[n++] = curHi;
        return finish(out, n);
    }

    /** Wraps the prefix {@code tmp[0..len)} (already normalised, possibly empty) as a {@code RangeSet}. */
    private static RangeSet finish(final int[] tmp, final int len) {
        if (len == 0) {
            return EMPTY;
        }
        return new RangeSet(len == tmp.length ? tmp : Arrays.copyOf(tmp, len));
    }
}
