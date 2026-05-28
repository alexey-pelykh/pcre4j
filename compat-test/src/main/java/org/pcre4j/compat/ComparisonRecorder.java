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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class ComparisonRecorder implements AutoCloseable {
    private final BufferedWriter writer;
    private final Object lock = new Object();

    public ComparisonRecorder(Path out) throws IOException {
        Files.createDirectories(out.getParent());
        this.writer = Files.newBufferedWriter(out, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public void record(String source, int caseIndex, String pattern, String input, int flags,
                       MatchProbe oracle, MatchProbe sut) {
        String json = "{"
                + "\"source\":" + jsonString(source) + ","
                + "\"caseIndex\":" + caseIndex + ","
                + "\"flags\":" + flags + ","
                + "\"pattern\":" + jsonString(pattern) + ","
                + "\"input\":" + jsonString(input) + ","
                + "\"oracle\":" + probeToJson(oracle) + ","
                + "\"sut\":" + probeToJson(sut)
                + "}";
        synchronized (lock) {
            try {
                writer.write(json);
                writer.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    static String probeToJson(MatchProbe p) {
        var sb = new StringBuilder("{");
        if (p.compile() instanceof Outcome.Ok) {
            sb.append("\"compile\":\"ok\"");
        } else if (p.compile() instanceof Outcome.SyntaxError se) {
            sb.append("\"compile\":\"err\",\"err\":").append(jsonString(se.message()));
        }
        sb.append(",\"matches\":").append(p.matchesFull());
        sb.append(",\"lookingAt\":").append(p.lookingAt());
        sb.append(",\"findAll\":[");
        for (int i = 0; i < p.findAll().size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(hitToJson(p.findAll().get(i)));
        }
        sb.append("]}");
        return sb.toString();
    }

    static String hitToJson(Hit h) {
        var sb = new StringBuilder("{");
        sb.append("\"start\":").append(h.start());
        sb.append(",\"end\":").append(h.end());
        sb.append(",\"text\":").append(jsonString(h.text()));
        sb.append(",\"groups\":[");
        for (int i = 0; i < h.groups().size(); i++) {
            if (i > 0) sb.append(",");
            var g = h.groups().get(i);
            sb.append("{\"start\":").append(g.start())
                    .append(",\"end\":").append(g.end())
                    .append(",\"text\":").append(jsonString(g.text())).append("}");
        }
        sb.append("]}");
        return sb.toString();
    }

    static String jsonString(String s) {
        if (s == null) return "null";
        var sb = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '"' -> sb.append("\\\"");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                default -> {
                    if (c < 0x20 || Character.isSurrogate(c)) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append("\"");
        return sb.toString();
    }
}
