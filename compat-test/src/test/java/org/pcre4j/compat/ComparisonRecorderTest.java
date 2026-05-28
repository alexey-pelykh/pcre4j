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
package org.pcre4j.compat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComparisonRecorderTest {
    @Test
    void writesOneLinePerRecord(@TempDir Path dir) throws Exception {
        Path out = dir.resolve("raw.jsonl");
        try (var rec = new ComparisonRecorder(out)) {
            rec.record("TestCases.txt", 1, "\\d+", "abc123", 0,
                    Probes.oracle("\\d+", "abc123", 0),
                    Probes.sut("\\d+", "abc123", 0));
        }
        List<String> lines = Files.readAllLines(out);
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).startsWith("{"));
        assertTrue(lines.get(0).contains("\"source\":\"TestCases.txt\""));
        assertTrue(lines.get(0).contains("\"pattern\":\"\\\\d+\""));
    }

    @Test
    void isThreadSafe(@TempDir Path dir) throws Exception {
        Path out = dir.resolve("raw.jsonl");
        int n = 200;
        try (var rec = new ComparisonRecorder(out)) {
            ExecutorService pool = Executors.newFixedThreadPool(8);
            var futures = new ArrayList<Future<?>>();
            for (int i = 0; i < n; i++) {
                final int idx = i;
                futures.add(pool.submit(() -> rec.record("t.txt", idx, "x", "y", 0,
                        Probes.oracle("x", "y", 0), Probes.sut("x", "y", 0))));
            }
            for (var f : futures) f.get();
            pool.shutdown();
        }
        assertEquals(n, Files.readAllLines(out).size());
    }
}
