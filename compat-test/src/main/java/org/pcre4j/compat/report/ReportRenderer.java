/*
 * Copyright (C) 2026 Oleksii PELYKH
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
package org.pcre4j.compat.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public final class ReportRenderer {

    private ReportRenderer() {}

    public static void render(Path rawJsonl, Path outMd) throws IOException {
        // Dedupe by (source, caseIndex), last-wins. The harness can emit two rows for the
        // same case when a TimeoutException fires `recordTimeout(...)` and the cancelled
        // worker subsequently completes (java.util.regex catastrophic backtracking doesn't
        // poll Thread.interrupt) and calls the normal `record(...)`. The worker's actual
        // outcome is the more authoritative datum, so letting the later write replace the
        // timeout placeholder gives the most accurate verdict and — critically — keeps the
        // Total/rate columns from double-counting the same case on slow CI runners.
        final java.util.LinkedHashMap<String, RawRecord> dedup = new java.util.LinkedHashMap<>();
        for (String line : Files.readAllLines(rawJsonl)) {
            if (line.isBlank()) continue;
            try {
                final RawRecord r = RawRecord.parse(line);
                dedup.put(r.source() + "#" + r.caseIndex(), r);
            } catch (RuntimeException ignored) {
                // skip malformed lines
            }
        }
        final List<RawRecord> records = new ArrayList<>(dedup.values());

        Map<String, int[]> summary = new TreeMap<>();
        for (RawRecord r : records) {
            int[] s = summary.computeIfAbsent(r.source(), k -> new int[8]);
            s[0]++;
            Verdict v = classify(r);
            switch (v) {
                case PASS -> s[1]++;
                case SUT_COMPILE_ERROR -> {
                    s[2]++;
                    s[3]++;
                }
                case SUT_RUNTIME_ERROR -> {
                    s[2]++;
                    s[4]++;
                }
                case BEHAVIOR_DIFF -> {
                    s[2]++;
                    s[5]++;
                }
                case SUT_ACCEPTS_REJECTED -> s[2]++;
                case BOTH_REJECTED -> s[6]++;
                case TIMEOUT -> {
                    s[2]++;
                    s[7]++;
                }
            }
        }

        StringBuilder out = new StringBuilder();
        out.append("# pcre4j compat report vs java.util.regex\n\n");
        out.append("## Summary\n\n");
        out.append("| Source | Total | Pass | Fail | both-rejected | sut-compile-error |")
                .append(" sut-runtime-error | behavior-diff | timeout |\n");
        out.append("| --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: |\n");
        for (var e : summary.entrySet()) {
            int[] s = e.getValue();
            out.append("| ").append(e.getKey()).append(" | ").append(s[0]).append(" | ").append(s[1])
                    .append(" | ").append(s[2]).append(" | ").append(s[6]).append(" | ")
                    .append(s[3]).append(" | ").append(s[4]).append(" | ").append(s[5])
                    .append(" | ").append(s[7]).append(" |\n");
        }

        out.append("\n> **Pass** = oracle and SUT agree on `matches()`, `lookingAt()` and `findAll`. ")
                .append("**both-rejected** = both engines refused to compile the pattern")
                .append(" (counted separately so the headline number reflects behavioural agreement only).\n");

        out.append("\n## Failures by root cause\n\n");
        out.append("| Cause | Count | Sample pattern |\n| --- | ---: | --- |\n");
        Map<String, Integer> totalByCause = new HashMap<>();
        Map<String, String> sampleByCause = new HashMap<>();
        for (RawRecord r : records) {
            Verdict v = classify(r);
            if (v == Verdict.PASS || v == Verdict.BOTH_REJECTED) continue;
            String cause = Classifier.classify(r.pattern());
            totalByCause.merge(cause, 1, Integer::sum);
            sampleByCause.putIfAbsent(cause, r.pattern());
        }
        totalByCause.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> out.append("| ").append(e.getKey()).append(" | ").append(e.getValue())
                        .append(" | `").append(sampleByCause.get(e.getKey()).replace("|", "\\|")).append("` |\n"));

        Files.createDirectories(outMd.getParent());
        Files.writeString(outMd, out.toString());
    }

    private enum Verdict {
        PASS, SUT_COMPILE_ERROR, SUT_RUNTIME_ERROR, SUT_ACCEPTS_REJECTED, BEHAVIOR_DIFF, BOTH_REJECTED, TIMEOUT
    }

    private static Verdict classify(RawRecord r) {
        if ("timeout".equals(r.outcomeTag())) return Verdict.TIMEOUT;
        boolean oOk = "ok".equals(r.oracleCompile());
        boolean sOk = "ok".equals(r.sutCompile());
        if (!oOk && !sOk) return Verdict.BOTH_REJECTED;
        if (oOk && !sOk) {
            String err = r.sutErr() == null ? "" : r.sutErr();
            if (err.startsWith("runtime") || err.startsWith("runtime-match")) return Verdict.SUT_RUNTIME_ERROR;
            return Verdict.SUT_COMPILE_ERROR;
        }
        if (!oOk && sOk) return Verdict.SUT_ACCEPTS_REJECTED;
        // Both compiled: behaviour must agree on all three probe operations.
        if (!Objects.equals(r.oracleMatches(), r.sutMatches())) return Verdict.BEHAVIOR_DIFF;
        if (!Objects.equals(r.oracleLookingAt(), r.sutLookingAt())) return Verdict.BEHAVIOR_DIFF;
        if (!Objects.equals(r.oracleFindAll(), r.sutFindAll())) return Verdict.BEHAVIOR_DIFF;
        return Verdict.PASS;
    }

    /**
     * CLI entry point so the {@code :compatReport} Gradle task can launch the renderer
     * via {@code JavaExec} on the project's Java 21 toolchain rather than via in-process
     * reflection on the Gradle daemon's JVM. The in-process path threw
     * {@code UnsupportedClassVersionError} whenever the daemon ran on an older JDK
     * (class file 65.0 vs. e.g. 61.0), silently preventing {@code report.md} from being
     * produced.
     *
     * <p>Usage: {@code ReportRenderer <raw.jsonl> <out.md>}.
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: ReportRenderer <raw.jsonl> <out.md>");
            System.exit(2);
        }
        render(Path.of(args[0]), Path.of(args[1]));
        System.out.println("Wrote " + args[1]);
    }
}
