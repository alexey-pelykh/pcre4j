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

/**
 * Converts a {@link ClassNode} AST to a {@link RangeSet} for algebraic manipulation.
 *
 * <p>If any {@link ClassNode.PropertyLeaf} in the tree cannot be expanded to a concrete
 * range set, an {@link EvaluationFailedException} is thrown and the caller should fall
 * back to literal emission.
 *
 * <p>Note: the expansions of {@code \d}, {@code \w}, {@code \s} here use the
 * <em>ASCII-only</em> definitions consistent with Java's default (non-UNICODE_CHARACTER_CLASS)
 * mode.  Phase 3 may extend these to full Unicode tables.
 */
public final class Evaluator {

    private Evaluator() {
    }

    /**
     * Evaluates the given node to a {@link RangeSet}.
     *
     * @param node the AST node to evaluate
     * @return the corresponding range set
     * @throws EvaluationFailedException if the node (or a descendant) contains a
     *                                   {@link ClassNode.PropertyLeaf} that cannot be expanded
     */
    public static RangeSet toRangeSet(final ClassNode node) throws EvaluationFailedException {
        if (node instanceof ClassNode.Literal lit) {
            return RangeSet.single(lit.cp());
        }
        if (node instanceof ClassNode.Range r) {
            return RangeSet.range(r.lo(), r.hi());
        }
        if (node instanceof ClassNode.Negated neg) {
            return toRangeSet(neg.child()).complement();
        }
        if (node instanceof ClassNode.Union union) {
            RangeSet result = RangeSet.EMPTY;
            for (final ClassNode child : union.children()) {
                result = result.union(toRangeSet(child));
            }
            return result;
        }
        if (node instanceof ClassNode.Intersection inter) {
            RangeSet result = RangeSet.ALL;
            for (final ClassNode operand : inter.operands()) {
                result = result.intersect(toRangeSet(operand));
            }
            return result;
        }
        if (node instanceof ClassNode.PropertyLeaf leaf) {
            return expandProperty(leaf);
        }
        throw new EvaluationFailedException("Unknown node type: " + node.getClass());
    }

    // -----------------------------------------------------------------------
    // Property expansion table (ASCII-only approximations)
    // -----------------------------------------------------------------------

    private static RangeSet expandProperty(final ClassNode.PropertyLeaf leaf)
            throws EvaluationFailedException {
        final String token = leaf.pcre2Token();
        return switch (token) {
            case "\\d"  -> DIGIT;
            case "\\D"  -> DIGIT.complement();
            case "\\w"  -> WORD;
            case "\\W"  -> WORD.complement();
            case "\\s"  -> SPACE;
            case "\\S"  -> SPACE.complement();
            case "\\p{ASCII}" -> ASCII;
            case "\\p{Alpha}" -> ALPHA;
            case "\\p{Alnum}" -> ALNUM;
            case "\\p{Lower}" -> LOWER;
            case "\\p{Upper}" -> UPPER;
            case "\\p{Digit}" -> DIGIT;
            case "\\p{XDigit}" -> HEX_DIGIT;
            case "\\p{Space}" -> POSIX_SPACE;
            case "\\p{Blank}" -> BLANK;
            case "\\p{Cntrl}" -> CNTRL;
            case "\\p{Graph}" -> GRAPH;
            case "\\p{Print}" -> PRINT;
            case "\\p{Punct}" -> PUNCT;
            default -> throw new EvaluationFailedException("Cannot expand property: " + token);
        };
    }

    // ASCII digit: 0-9
    private static final RangeSet DIGIT = RangeSet.range('0', '9');

    // ASCII word: A-Za-z0-9_
    private static final RangeSet WORD =
            RangeSet.range('A', 'Z')
                    .union(RangeSet.range('a', 'z'))
                    .union(RangeSet.range('0', '9'))
                    .union(RangeSet.single('_'));

    // ASCII whitespace: \t \n \x0B \f \r space
    private static final RangeSet SPACE =
            RangeSet.single('\t')
                    .union(RangeSet.single('\n'))
                    .union(RangeSet.single(0x0B))
                    .union(RangeSet.single('\f'))
                    .union(RangeSet.single('\r'))
                    .union(RangeSet.single(' '));

    // ASCII: 0x00-0x7F
    private static final RangeSet ASCII = RangeSet.range(0x00, 0x7F);

    // POSIX [:alpha:]: A-Za-z
    private static final RangeSet ALPHA =
            RangeSet.range('A', 'Z').union(RangeSet.range('a', 'z'));

    // POSIX [:alnum:]: A-Za-z0-9
    private static final RangeSet ALNUM = ALPHA.union(DIGIT);

    // POSIX [:lower:]: a-z
    private static final RangeSet LOWER = RangeSet.range('a', 'z');

    // POSIX [:upper:]: A-Z
    private static final RangeSet UPPER = RangeSet.range('A', 'Z');

    // POSIX [:xdigit:]: 0-9A-Fa-f
    private static final RangeSet HEX_DIGIT =
            DIGIT.union(RangeSet.range('A', 'F')).union(RangeSet.range('a', 'f'));

    // POSIX [:space:]: \t \n \x0B \f \r space (same as \s for ASCII)
    private static final RangeSet POSIX_SPACE = SPACE;

    // POSIX [:blank:]: space \t
    private static final RangeSet BLANK =
            RangeSet.single(' ').union(RangeSet.single('\t'));

    // POSIX [:cntrl:]: 0x00-0x1F, 0x7F
    private static final RangeSet CNTRL =
            RangeSet.range(0x00, 0x1F).union(RangeSet.single(0x7F));

    // POSIX [:graph:]: 0x21-0x7E
    private static final RangeSet GRAPH = RangeSet.range(0x21, 0x7E);

    // POSIX [:print:]: 0x20-0x7E
    private static final RangeSet PRINT = RangeSet.range(0x20, 0x7E);

    // POSIX [:punct:]: all printable ASCII except letters, digits and space
    private static final RangeSet PUNCT =
            PRINT.subtract(ALNUM).subtract(RangeSet.single(' '));
}
