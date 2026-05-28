package org.pcre4j.compat.imported;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

class RegExTestRunner {

    static {
        // Trigger Probes static init so Pcre4j.setup(...) runs with the configured backend
        // before any RegExTest method calls org.pcre4j.regex.Pattern.compile().
        try {
            Class.forName(org.pcre4j.compat.Probes.class.getName());
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final Set<String> SKIP = Set.of(
            "serializeTest",
            // Data-driven; already run by TxtCompatTest. Excluded here to avoid double-counting.
            "processTestCases",
            "processBMPTestCases",
            "processSupplementaryTestCases"
    );

    @TestFactory
    Stream<DynamicTest> allRegExTests() {
        List<Method> methods = new ArrayList<>();
        for (Method m : RegExTest.class.getDeclaredMethods()) {
            if (!Modifier.isStatic(m.getModifiers())) continue;
            if (m.getParameterCount() != 0) continue;
            if (m.getReturnType() != void.class) continue;
            if (!m.isAnnotationPresent(org.junit.jupiter.api.Test.class)) continue;
            methods.add(m);
        }
        methods.sort(Comparator.comparing(Method::getName));
        return methods.stream().map(m -> DynamicTest.dynamicTest(m.getName(), () -> invoke(m)));
    }

    private void invoke(Method m) throws Exception {
        if (SKIP.contains(m.getName())) {
            org.junit.jupiter.api.Assumptions.abort("pcre4j-skip: " + m.getName());
        }
        m.setAccessible(true);
        PrintStream origOut = System.out;
        PrintStream origErr = System.err;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream tee = new PrintStream(buf, true);
        System.setOut(tee);
        System.setErr(tee);
        try {
            m.invoke(null);
        } catch (Throwable t) {
            Throwable c = t.getCause() != null ? t.getCause() : t;
            if (c instanceof org.opentest4j.TestAbortedException tae) {
                throw tae;
            }
            String captured = buf.toString();
            throw new AssertionError(m.getName() + " failed: " + c + "\nCaptured output:\n" + captured, c);
        } finally {
            System.setOut(origOut);
            System.setErr(origErr);
        }
    }
}
