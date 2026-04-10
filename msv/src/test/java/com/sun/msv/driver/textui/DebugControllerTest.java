package com.sun.msv.driver.textui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

/**
 * Tests for {@link DebugController} — exposes NPE when a null Locator
 * element is present inside the Locator array passed to error() / warning().
 */
public class DebugControllerTest extends TestCase {

    public DebugControllerTest(String name) { super(name); }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(DebugControllerTest.class);
    }

    private DebugController createController(PrintStream out) {
        return new DebugController(true, false, out);
    }

    private LocatorImpl createLocator(String systemId, int line, int col) {
        LocatorImpl loc = new LocatorImpl();
        loc.setSystemId(systemId);
        loc.setLineNumber(line);
        loc.setColumnNumber(col);
        return loc;
    }

    /**
     * error() with a Locator array containing a single null element
     * must not throw NullPointerException.
     */
    public void testErrorWithNullLocatorElement() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        DebugController ctrl = createController(ps);

        ctrl.error(new Locator[]{null}, "test error message", null);

        String output = baos.toString();
        assertTrue("output should contain the error message",
                output.contains("test error message"));
    }

    /**
     * warning() with a Locator array containing a single null element
     * must not throw NullPointerException.
     */
    public void testWarningWithNullLocatorElement() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        DebugController ctrl = createController(ps);

        ctrl.warning(new Locator[]{null}, "test warning message");

        String output = baos.toString();
        assertTrue("output should contain the warning message",
                output.contains("test warning message"));
    }

    /**
     * error() with a mix of a valid Locator and a null element
     * must not throw NullPointerException.
     */
    public void testErrorWithMixedNullLocators() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        DebugController ctrl = createController(ps);

        LocatorImpl valid = createLocator("file:///test.xsd", 10, 5);

        ctrl.error(new Locator[]{valid, null}, "mixed locator error", null);

        String output = baos.toString();
        assertTrue("output should contain the error message",
                output.contains("mixed locator error"));
        assertTrue("output should contain the valid locator info",
                output.contains("test.xsd"));
    }
}
