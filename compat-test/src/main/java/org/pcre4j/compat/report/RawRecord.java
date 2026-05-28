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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record RawRecord(
        String source, int caseIndex, String pattern, String input,
        String oracleCompile, Boolean oracleMatches,
        String sutCompile, String sutErr, Boolean sutMatches
) {

    private static String extract(String json, String key) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\":\"((?:\\\\.|[^\"\\\\])*)\"");
        Matcher m = p.matcher(json);
        return m.find() ? unescape(m.group(1)) : null;
    }

    private static Boolean extractBool(String json, String key) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\":(true|false|null)");
        Matcher m = p.matcher(json);
        if (!m.find()) return null;
        String v = m.group(1);
        return v.equals("null") ? null : Boolean.valueOf(v);
    }

    private static String unescape(String s) {
        return s.replace("\\\"", "\"").replace("\\\\", "\\").replace("\\n", "\n").replace("\\t", "\t");
    }

    public static RawRecord parse(String json) {
        String oracle = subObject(json, "oracle");
        String sut = subObject(json, "sut");
        return new RawRecord(
                extract(json, "source"),
                Integer.parseInt(json.replaceAll(".*\"caseIndex\":(-?\\d+).*", "$1")),
                extract(json, "pattern"),
                extract(json, "input"),
                extract(oracle, "compile"),
                extractBool(oracle, "matches"),
                extract(sut, "compile"),
                extract(sut, "err"),
                extractBool(sut, "matches")
        );
    }

    private static String subObject(String json, String key) {
        int i = json.indexOf("\"" + key + "\":{");
        if (i < 0) return "{}";
        int depth = 0;
        int start = json.indexOf('{', i);
        for (int j = start; j < json.length(); j++) {
            char c = json.charAt(j);
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) return json.substring(start, j + 1);
            }
        }
        return "{}";
    }
}
