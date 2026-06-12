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

import java.util.ArrayList;
import java.util.List;

public final class Probes {

    static {
        String backend = System.getProperty("pcre4j.test.backends", "ffm");
        if ("jna".equals(backend)) {
            org.pcre4j.Pcre4j.setup(new org.pcre4j.jna.Pcre2());
        } else {
            org.pcre4j.Pcre4j.setup(new org.pcre4j.ffm.Pcre2());
        }
    }

    private Probes() {}

    public static MatchProbe oracle(String pattern, String input, int flags) {
        java.util.regex.Pattern p;
        try {
            p = java.util.regex.Pattern.compile(pattern, flags);
        } catch (java.util.regex.PatternSyntaxException e) {
            return new MatchProbe(new Outcome.SyntaxError(e.getMessage()), null, null, List.of());
        } catch (RuntimeException e) {
            return new MatchProbe(
                    new Outcome.SyntaxError("runtime:" + e.getClass().getSimpleName() + ":" + e.getMessage()),
                    null, null, List.of());
        }
        return runWithMatcher(input, p.matcher(input));
    }

    public static MatchProbe sut(String pattern, String input, int flags) {
        org.pcre4j.regex.Pattern p;
        try {
            p = org.pcre4j.regex.Pattern.compile(pattern, flags);
        } catch (java.util.regex.PatternSyntaxException e) {
            return new MatchProbe(new Outcome.SyntaxError(e.getMessage()), null, null, List.of());
        } catch (RuntimeException e) {
            return new MatchProbe(
                    new Outcome.SyntaxError("runtime:" + e.getClass().getSimpleName() + ":" + e.getMessage()),
                    null, null, List.of());
        }
        return runWithMatcher(input, p.matcher(input));
    }

    private static MatchProbe runWithMatcher(String input, Object m) {
        try {
            Boolean matchesFull = (Boolean) m.getClass().getMethod("matches").invoke(m);
            m.getClass().getMethod("reset").invoke(m);
            Boolean lookingAt = (Boolean) m.getClass().getMethod("lookingAt").invoke(m);
            m.getClass().getMethod("reset").invoke(m);
            List<Hit> hits = new ArrayList<>();
            while ((Boolean) m.getClass().getMethod("find").invoke(m)) {
                int start = (int) m.getClass().getMethod("start").invoke(m);
                int end = (int) m.getClass().getMethod("end").invoke(m);
                String text = (String) m.getClass().getMethod("group").invoke(m);
                int gc = (int) m.getClass().getMethod("groupCount").invoke(m);
                List<Group> groups = new ArrayList<>(gc);
                for (int i = 1; i <= gc; i++) {
                    Object gStart = m.getClass().getMethod("start", int.class).invoke(m, i);
                    Object gEnd = m.getClass().getMethod("end", int.class).invoke(m, i);
                    Object gText = m.getClass().getMethod("group", int.class).invoke(m, i);
                    groups.add(new Group(null, (int) gStart, (int) gEnd, (String) gText));
                }
                hits.add(new Hit(start, end, text, groups));
                if (end == start) {
                    if (end >= input.length()) break;
                    m.getClass().getMethod("region", int.class, int.class).invoke(m, end + 1, input.length());
                }
            }
            return new MatchProbe(new Outcome.Ok(), matchesFull, lookingAt, hits);
        } catch (RuntimeException e) {
            return new MatchProbe(
                    new Outcome.SyntaxError("runtime-match:" + e.getClass().getSimpleName() + ":" + e.getMessage()),
                    null, null, List.of());
        } catch (Exception e) {
            Throwable c = e.getCause() != null ? e.getCause() : e;
            return new MatchProbe(
                    new Outcome.SyntaxError(
                            "runtime-match:" + c.getClass().getSimpleName() + ":" + c.getMessage()),
                    null, null, List.of());
        }
    }
}
