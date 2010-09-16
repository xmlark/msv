package com.sun.msv.reader.dtd;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import util.Checker;
import util.ResourceChecker;

public class DTDReaderTest extends TestCase
{
    public DTDReaderTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(DTDReaderTest.class);
    }
    
    /** tests the existence of all messages */
    public void testMessages() throws Exception {
        ResourceChecker.check(
            DTDReader.class,
            "",
            new Checker(){
                public void check( String propertyName ) {
                    // if the specified property doesn't exist, this will throw an error
                    System.out.println(
                        Localizer.localize(propertyName,new Object[]{"@@@","@@@","@@@","@@@","@@@"}));
                }
            });
    }
}
