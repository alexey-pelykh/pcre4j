package org.pcre4j.compat.imported;

import org.junit.jupiter.api.function.Executable;

/**
 * Minimal TestNG-compatible Assert shim for imported JDK tests.
 * Delegates to JUnit 5 Assertions; arg order is preserved as TestNG
 * (actual, expected) — error messages may show reversed labels on failure,
 * but pass/fail logic is identical.
 */
@SuppressWarnings({"unused", "overloads"})
class Assert {

    static void assertEquals(Object actual, Object expected) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }

    static void assertEquals(Object actual, Object expected, String message) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual, message);
    }

    static void assertEquals(long actual, long expected) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }

    static void assertEquals(long actual, long expected, String message) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual, message);
    }

    static void assertEquals(int actual, int expected) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }

    static void assertEquals(int actual, int expected, String message) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual, message);
    }

    static void assertEquals(boolean actual, boolean expected) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }

    static void assertNotEquals(Object actual, Object unexpected) {
        org.junit.jupiter.api.Assertions.assertNotEquals(unexpected, actual);
    }

    static void assertNotEquals(Object actual, Object unexpected, String message) {
        org.junit.jupiter.api.Assertions.assertNotEquals(unexpected, actual, message);
    }

    static void assertNotEquals(long actual, long unexpected) {
        org.junit.jupiter.api.Assertions.assertNotEquals(unexpected, actual);
    }

    static void assertNotEquals(long actual, long unexpected, String message) {
        org.junit.jupiter.api.Assertions.assertNotEquals(unexpected, actual, message);
    }

    static void assertNotEquals(int actual, int unexpected) {
        org.junit.jupiter.api.Assertions.assertNotEquals(unexpected, actual);
    }

    static void assertNotEquals(int actual, int unexpected, String message) {
        org.junit.jupiter.api.Assertions.assertNotEquals(unexpected, actual, message);
    }

    static void assertNotSame(Object actual, Object unexpected) {
        org.junit.jupiter.api.Assertions.assertNotSame(unexpected, actual);
    }

    static void assertNotSame(Object actual, Object unexpected, String message) {
        org.junit.jupiter.api.Assertions.assertNotSame(unexpected, actual, message);
    }

    static void assertTrue(boolean condition) {
        org.junit.jupiter.api.Assertions.assertTrue(condition);
    }

    static void assertTrue(boolean condition, String message) {
        org.junit.jupiter.api.Assertions.assertTrue(condition, message);
    }

    static void assertFalse(boolean condition) {
        org.junit.jupiter.api.Assertions.assertFalse(condition);
    }

    static void assertFalse(boolean condition, String message) {
        org.junit.jupiter.api.Assertions.assertFalse(condition, message);
    }

    static void assertNull(Object object) {
        org.junit.jupiter.api.Assertions.assertNull(object);
    }

    static void assertNotNull(Object object) {
        org.junit.jupiter.api.Assertions.assertNotNull(object);
    }

    static void fail(String message) {
        org.junit.jupiter.api.Assertions.fail(message);
    }

    static void fail() {
        org.junit.jupiter.api.Assertions.fail();
    }

    static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable) {
        return org.junit.jupiter.api.Assertions.assertThrows(expectedType, executable);
    }

    static <T extends Throwable> T expectThrows(Class<T> expectedType, Executable executable) {
        return org.junit.jupiter.api.Assertions.assertThrows(expectedType, executable);
    }
}
