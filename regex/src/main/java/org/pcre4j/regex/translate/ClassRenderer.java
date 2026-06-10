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
 * Renders a {@link ClassNode} AST as a PCRE2-compatible character class string
 * (including the surrounding {@code [} and {@code ]}).
 *
 * <h3>Strategy</h3>
 * <ol>
 *   <li><b>No intersection present:</b> walk the AST emitting a single flat {@code [...]}
 *       class.  Nested {@link ClassNode.Union} nodes are flattened inline;
 *       {@link ClassNode.PropertyLeaf} tokens are emitted verbatim; ranges and literals are
 *       emitted normally.</li>
 *   <li><b>Intersection present:</b> attempt {@link Evaluator#toRangeSet(ClassNode)}.  If
 *       evaluation succeeds, emit {@code [<body>]} (or {@code [^<body>]} for a top-level
 *       {@link ClassNode.Negated}).  If evaluation fails (unknown property), fall back to
 *       literal emission with {@code &&} preserved — PCRE2 will reject it, but no worse
 *       than before translation.</li>
 * </ol>
 */
public final class ClassRenderer {

    /** PCRE2 class representing the empty set (matches nothing). */
    private static final String EMPTY_CLASS = "[^\\x{0}-\\x{10FFFF}]";

    private ClassRenderer() {
    }

    /**
     * Outcome of {@link #renderWithSignal(ClassNode)}: the rendered PCRE2 string and a flag
     * indicating whether any intersection in the AST had to be left as a literal {@code &&}
     * fallback (i.e. could not be evaluated to a concrete {@link RangeSet}).
     */
    public record RenderResult(String text, boolean intersectionUnresolved) {
    }

    /**
     * Renders the given {@link ClassNode} as a PCRE2 character class string.
     *
     * @param node the root AST node (typically the result of
     *             {@link ClassBodyParser#parseClass(String, int[])})
     * @return PCRE2 character class string including surrounding {@code [} and {@code ]}
     */
    public static String render(final ClassNode node) {
        return renderWithSignal(node).text();
    }

    /**
     * Like {@link #render(ClassNode)}, but also reports whether any intersection subtree was left
     * unresolved (i.e. {@code &&} still appears in the output). Callers that previously inferred
     * this by scanning the rendered text for {@code "&&"} should use this typed result instead —
     * it cannot be fooled by literal ampersands appearing for unrelated reasons.
     */
    public static RenderResult renderWithSignal(final ClassNode node) {
        // Determine negation at top level
        final boolean negated = node instanceof ClassNode.Negated;
        final ClassNode inner = negated ? ((ClassNode.Negated) node).child() : node;

        if (containsIntersection(inner)) {
            // renderWithIntersection now reports its own intersection-unresolved status
            // (true iff Strategy 3 fallback fires and at least one operand left a literal
            // {@code &&} in the output). Threading the boolean directly removes the prior
            // {@code rendered.contains("&&")} string-scan, which would have misfired if any
            // future code path emitted a literal {@code &&} from a resolved subtree.
            return renderWithIntersection(inner, negated);
        }
        // Simple path: no intersection — try flat emission, fall back to original-style
        // if any nested negated subtree can't be evaluated (so we don't silently drop '^').
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        if (negated) {
            sb.append('^');
        }
        try {
            emitFlat(inner, sb);
        } catch (EvaluationFailedException e) {
            // Nested [^...] that can't be evaluated to a RangeSet — preserve the wrapper by
            // re-emitting in original style so PCRE2 sees identical structure to Phase-1 output.
            final StringBuilder fallback = new StringBuilder();
            fallback.append('[');
            if (negated) {
                fallback.append('^');
            }
            emitOriginalStyle(inner, fallback);
            fallback.append(']');
            return new RenderResult(fallback.toString(), false);
        }
        sb.append(']');
        return new RenderResult(sb.toString(), false);
    }

    // -----------------------------------------------------------------------
    // Flat emission (no intersection in this subtree)
    // -----------------------------------------------------------------------

    /**
     * Emits the body of {@code node} into {@code sb} without surrounding {@code [...]}.
     * Nested unions are flattened inline.
     *
     * @throws EvaluationFailedException if a nested {@link ClassNode.Negated} subtree cannot be
     *         evaluated to a {@link RangeSet}; the caller should fall back to original-style
     *         rendering so the {@code ^} is not silently lost.
     */
    private static void emitFlat(final ClassNode node, final StringBuilder sb)
            throws EvaluationFailedException {
        switch (node) {
            case ClassNode.Literal lit -> emitLiteralInClass(lit.cp(), sb);
            case ClassNode.Range r -> {
                emitLiteralInClass(r.lo(), sb);
                sb.append('-');
                emitLiteralInClass(r.hi(), sb);
            }
            case ClassNode.PropertyLeaf leaf -> sb.append(renderPropertyToken(leaf));
            case ClassNode.Negated neg -> {
                // A negated subtree inside a non-negated flat class.
                // Evaluate + complement so we can inline the ranges. Use toRangeSet directly so
                // the underlying EvaluationFailedException propagates with its full cause chain
                // (root cause preserved for support/diagnostics); render() falls back to
                // original-style emission on failure.
                final RangeSet rs;
                try {
                    rs = Evaluator.toRangeSet(neg.child());
                } catch (EvaluationFailedException e) {
                    throw new EvaluationFailedException(
                            "Cannot flatten nested [^...]; caller must fall back", e);
                }
                sb.append(rs.complement().toPcre2ClassBody());
            }
            case ClassNode.Union union -> {
                for (final ClassNode child : union.children()) {
                    emitFlat(child, sb);
                }
            }
            case ClassNode.Intersection inter -> {
                // Unreachable: render() routes intersections through renderWithIntersection.
                throw new AssertionError(
                        "emitFlat must not be called on Intersection nodes: " + inter);
            }
        }
    }

    /**
     * Emits the body of {@code node} in "original style" — keeping nested {@code [^...]} sub-classes
     * intact (not expanding them) so that PCRE2 parses the result the same way the pre-Phase-2
     * output was parsed. Called by both the intersection fallback path
     * ({@link #renderWithIntersection}) and the simple-path catch in {@link #render} when a nested
     * negated subtree cannot be evaluated.
     */
    private static void emitOriginalStyle(final ClassNode node, final StringBuilder sb) {
        switch (node) {
            case ClassNode.Literal lit -> emitLiteralInClass(lit.cp(), sb);
            case ClassNode.Range r -> {
                emitLiteralInClass(r.lo(), sb);
                sb.append('-');
                emitLiteralInClass(r.hi(), sb);
            }
            case ClassNode.PropertyLeaf leaf -> sb.append(renderPropertyToken(leaf));
            case ClassNode.Negated neg -> {
                // Keep the [^...] wrapper intact so PCRE2 parses it identically to Phase 1 output
                sb.append("[^");
                emitOriginalStyle(neg.child(), sb);
                sb.append(']');
            }
            case ClassNode.Union union -> {
                for (final ClassNode child : union.children()) {
                    emitOriginalStyle(child, sb);
                }
            }
            case ClassNode.Intersection inter -> emitIntersectionFallbackOriginal(inter, sb);
        }
    }

    // -----------------------------------------------------------------------
    // Intersection-aware rendering
    // -----------------------------------------------------------------------

    private static RenderResult renderWithIntersection(final ClassNode inner, final boolean negated) {
        // Strategy 1: Try to evaluate the entire subtree to a RangeSet
        RangeSet rs = Evaluator.tryToRangeSet(inner);
        if (rs != null) {
            if (negated) {
                rs = rs.complement();
            }
            if (rs.isEmpty()) {
                return new RenderResult(EMPTY_CLASS, false);
            }
            return new RenderResult("[" + rs.toPcre2ClassBody() + "]", false);
        }

        // Strategy 2: If the top node is Intersection, try each operand individually
        if (inner instanceof ClassNode.Intersection inter) {
            final RangeSet operandResult = tryEvaluateIntersectionRangeSet(inter);
            if (operandResult != null) {
                final RangeSet effective = negated ? operandResult.complement() : operandResult;
                if (effective.isEmpty()) {
                    return new RenderResult(EMPTY_CLASS, false);
                }
                return new RenderResult("[" + effective.toPcre2ClassBody() + "]", false);
            }
        }

        // Strategy 3: Fallback — emit in "original style" preserving [^...] structure
        // so that PCRE2 parses identically to the Phase-1-only output (no new regressions).
        // This is the only branch that can leave a literal {@code &&} in the rendered string;
        // we set intersectionUnresolved=true unconditionally here so callers know without
        // having to re-scan the text.
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        if (negated) {
            sb.append('^');
        }
        emitOriginalStyle(inner, sb);
        sb.append(']');
        return new RenderResult(sb.toString(), true);
    }

    /**
     * Tries to evaluate each operand of the intersection independently and intersects them.
     * Returns the intersected {@link RangeSet}, or {@code null} if any operand cannot be evaluated.
     */
    private static RangeSet tryEvaluateIntersectionRangeSet(final ClassNode.Intersection inter) {
        final List<ClassNode> operands = inter.operands();
        final List<RangeSet> sets = new ArrayList<>(operands.size());
        for (final ClassNode op : operands) {
            final RangeSet rs = Evaluator.tryToRangeSet(op);
            if (rs == null) {
                return null;
            }
            sets.add(rs);
        }
        RangeSet result = RangeSet.ALL;
        for (final RangeSet rs : sets) {
            result = result.intersect(rs);
        }
        return result;
    }

    /**
     * Emits the intersection's operands with {@code &&} separators using original-style rendering.
     * Used in the fallback path to avoid changing PCRE2 parse behaviour.
     */
    private static void emitIntersectionFallbackOriginal(
            final ClassNode.Intersection inter, final StringBuilder sb) {
        final List<ClassNode> operands = inter.operands();
        for (int i = 0; i < operands.size(); i++) {
            if (i > 0) {
                sb.append("&&");
            }
            emitOriginalStyle(operands.get(i), sb);
        }
    }

    // -----------------------------------------------------------------------
    // Literal emission inside a class body
    // -----------------------------------------------------------------------

    /**
     * Renders a {@link ClassNode.PropertyLeaf}'s class-body form. For most leaves this is just the
     * stored {@code pcre2Token}, but for negated JDK-specific properties such as
     * {@code \P{InGreek}} or {@code \P{javaLowerCase}} — which PCRE2 cannot evaluate inside a
     * class body — we resolve the positive set via {@link JdkPropertyExpander}, complement it,
     * and inline the resulting ranges. The leaf's stored token stays opaque so that
     * {@link Evaluator} can still expand it for intersection algebra.
     */
    private static String renderPropertyToken(final ClassNode.PropertyLeaf leaf) {
        final String token = leaf.pcre2Token();
        if (token.startsWith("\\P{") && token.endsWith("}")) {
            final String propName = token.substring(3, token.length() - 1);
            final RangeSet positive = JdkPropertyExpander.expand("\\p{" + propName + "}");
            if (positive != null) {
                final RangeSet complement = positive.complement();
                if (!complement.isEmpty()) {
                    return complement.toPcre2ClassBody();
                }
            }
        }
        return token;
    }

    static void emitLiteralInClass(final int cp, final StringBuilder sb) {
        if (cp >= 0x20 && cp <= 0x7E) {
            switch ((char) cp) {
                case '\\' -> sb.append("\\\\");
                case ']'  -> sb.append("\\]");
                case '^'  -> sb.append("\\^");
                case '-'  -> sb.append("\\-");
                default   -> sb.append((char) cp);
            }
        } else if (cp >= 0xD800 && cp <= 0xDFFF) {
            // Lone surrogate inside a class body. PCRE2 in UTF mode rejects both \x{D800} and
            // a raw surrogate byte, so there is no escape we can emit that compiles cleanly.
            // JDK's java.util.regex accepts \uD800 in a class (it just never matches against
            // any decoded UTF-16 input). Throw a clear translator error so callers see a
            // PatternSyntaxException with context rather than a cryptic PCRE2 compile failure
            // produced after the rendered string is already discarded.
            throw new IllegalArgumentException(
                    "Lone surrogate U+" + String.format("%04X", cp)
                            + " is not representable in PCRE2 UTF mode");
        } else {
            sb.append(String.format("\\x{%X}", cp));
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /** Returns {@code true} if the node tree contains any {@link ClassNode.Intersection}. */
    static boolean containsIntersection(final ClassNode node) {
        return switch (node) {
            case ClassNode.Intersection ignored -> true;
            case ClassNode.Negated neg -> containsIntersection(neg.child());
            case ClassNode.Union union ->
                    union.children().stream().anyMatch(ClassRenderer::containsIntersection);
            default -> false;
        };
    }
}
