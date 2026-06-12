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

import java.util.List;

/**
 * Sealed AST representing a parsed Java character-class body.
 *
 * <ul>
 *   <li>{@link Literal} — a single Unicode code point</li>
 *   <li>{@link Range} — a code-point range [lo, hi]</li>
 *   <li>{@link PropertyLeaf} — a PCRE2 escape like {@code \d}, {@code \p{L}}</li>
 *   <li>{@link Negated} — {@code [^...]} wrapper</li>
 *   <li>{@link Union} — union of children (from outer class or nested union flattening)</li>
 *   <li>{@link Intersection} — {@code A&&B&&C} — PCRE2 has no native intersection</li>
 * </ul>
 */
public sealed interface ClassNode
        permits ClassNode.Literal, ClassNode.Range, ClassNode.PropertyLeaf,
                ClassNode.Negated, ClassNode.Union, ClassNode.Intersection {

    /** A single Unicode code point. */
    record Literal(int cp) implements ClassNode {}

    /** An inclusive code-point range. */
    record Range(int lo, int hi) implements ClassNode {}

    /**
     * A backslash-property or shorthand class token, kept as the PCRE2 string that will be
     * emitted literally (e.g. {@code "\\d"}, {@code "\\p{L}"}, {@code "\\p{Greek}"}).
     * The {@code negated} flag is {@code true} for {@code \D}, {@code \W}, {@code \S},
     * {@code \P{...}} variants; in simple emission the token is emitted as-is and negation
     * is already encoded in the token string.
     */
    record PropertyLeaf(String pcre2Token, boolean negated) implements ClassNode {}

    /** Represents {@code [^...]} — a negated class body. */
    record Negated(ClassNode child) implements ClassNode {}

    /** Union of two or more children (no negation at this level). */
    record Union(List<ClassNode> children) implements ClassNode {}

    /**
     * Intersection of two or more operands (each operand is itself a {@link Union}).
     * Java's {@code [A&&B&&C]} parses as {@code Intersection([Union(A), Union(B), Union(C)])}.
     */
    record Intersection(List<ClassNode> operands) implements ClassNode {}
}
