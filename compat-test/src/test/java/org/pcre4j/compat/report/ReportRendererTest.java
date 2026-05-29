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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportRendererTest {
    @Test
    void rendersSummaryAndFailures(@TempDir Path dir) throws Exception {
        Path raw = dir.resolve("raw.jsonl");
        Files.writeString(raw, String.join("\n",
                "{\"source\":\"TestCases.txt\",\"caseIndex\":0,\"flags\":0,\"pattern\":\"\\\\d+\","
                        + "\"input\":\"a1\",\"oracle\":{\"compile\":\"ok\",\"matches\":false,"
                        + "\"lookingAt\":false,\"findAll\":[{\"start\":1,\"end\":2,\"text\":\"1\","
                        + "\"groups\":[]}]},\"sut\":{\"compile\":\"ok\",\"matches\":false,"
                        + "\"lookingAt\":false,\"findAll\":[{\"start\":1,\"end\":2,\"text\":\"1\","
                        + "\"groups\":[]}]}}",
                "{\"source\":\"TestCases.txt\",\"caseIndex\":1,\"flags\":0,"
                        + "\"pattern\":\"\\\\p{InGreek}\",\"input\":\"a\","
                        + "\"oracle\":{\"compile\":\"ok\",\"matches\":false,\"lookingAt\":false,"
                        + "\"findAll\":[]},\"sut\":{\"compile\":\"err\",\"err\":\"unknown property\","
                        + "\"matches\":null,\"lookingAt\":null,\"findAll\":[]}}"
        ));
        Path report = dir.resolve("report.md");
        ReportRenderer.render(raw, report);
        String body = Files.readString(report);
        assertTrue(body.contains("Total"));
        assertTrue(body.contains("block-property"));
        assertTrue(body.contains("sut-compile-error"));
        assertTrue(body.contains("both-rejected"));
    }

    @Test
    void findAllDifferenceCountsAsBehaviorDiff(@TempDir Path dir) throws Exception {
        // Same matches() result but different findAll hit positions: previous classify()
        // counted this as PASS; now must surface as behavior-diff.
        Path raw = dir.resolve("raw.jsonl");
        Files.writeString(raw, String.join("\n",
                "{\"source\":\"T.txt\",\"caseIndex\":0,\"flags\":0,\"pattern\":\"a\","
                        + "\"input\":\"aa\",\"oracle\":{\"compile\":\"ok\",\"matches\":false,"
                        + "\"lookingAt\":true,\"findAll\":[{\"start\":0,\"end\":1,\"text\":\"a\","
                        + "\"groups\":[]},{\"start\":1,\"end\":2,\"text\":\"a\",\"groups\":[]}]},"
                        + "\"sut\":{\"compile\":\"ok\",\"matches\":false,\"lookingAt\":true,"
                        + "\"findAll\":[{\"start\":0,\"end\":1,\"text\":\"a\",\"groups\":[]}]}}"
        ));
        Path report = dir.resolve("report.md");
        ReportRenderer.render(raw, report);
        String body = Files.readString(report);
        // 1 total, 0 pass, 1 fail under behavior-diff
        assertTrue(body.contains("| T.txt | 1 | 0 | 1 |"), body);
    }

    @Test
    void bothRejectedHasItsOwnColumnAndIsNotCountedAsPass(@TempDir Path dir) throws Exception {
        Path raw = dir.resolve("raw.jsonl");
        Files.writeString(raw, String.join("\n",
                "{\"source\":\"B.txt\",\"caseIndex\":0,\"flags\":0,\"pattern\":\"???\","
                        + "\"input\":\"x\",\"oracle\":{\"compile\":\"err\",\"err\":\"bad\","
                        + "\"matches\":null,\"lookingAt\":null,\"findAll\":[]},"
                        + "\"sut\":{\"compile\":\"err\",\"err\":\"bad\","
                        + "\"matches\":null,\"lookingAt\":null,\"findAll\":[]}}"
        ));
        Path report = dir.resolve("report.md");
        ReportRenderer.render(raw, report);
        String body = Files.readString(report);
        // 1 total, 0 pass, 0 fail, 1 both-rejected.
        assertTrue(body.contains("| B.txt | 1 | 0 | 0 | 1 |"), body);
    }

    @Test
    void timeoutOutcomeAppearsInTimeoutColumn(@TempDir Path dir) throws Exception {
        Path raw = dir.resolve("raw.jsonl");
        Files.writeString(raw, "{\"source\":\"S.txt\",\"caseIndex\":0,\"flags\":0,"
                + "\"pattern\":\"(a+)+\",\"input\":\"aaa\",\"outcome\":\"timeout\",\"timeoutMs\":10000,"
                + "\"oracle\":{\"compile\":\"timeout\",\"matches\":null,\"lookingAt\":null,\"findAll\":[]},"
                + "\"sut\":{\"compile\":\"timeout\",\"matches\":null,\"lookingAt\":null,\"findAll\":[]}}");
        Path report = dir.resolve("report.md");
        ReportRenderer.render(raw, report);
        String body = Files.readString(report);
        // Total 1, Pass 0, Fail 1, both-rejected 0, sut-compile 0, sut-runtime 0, behavior-diff 0, timeout 1
        assertTrue(body.contains("| S.txt | 1 | 0 | 1 | 0 | 0 | 0 | 0 | 1 |"), body);
    }
}
