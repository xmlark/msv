package com.sun.msv.driver.textui;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import util.Checker;
import util.ResourceChecker;

public class DriverTest extends TestCase
{
    public DriverTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(DriverTest.class);
    }
    
    /** tests the existence of all messages */
    public void testMessages() throws Exception {
        ResourceChecker.check(
            Driver.class,
            "",
            new Checker(){
                public void check( String propertyName ) {
                    // if the specified property doesn't exist, this will throw an error
                    System.out.println(
                        Driver.localize(propertyName,new Object[]{"@@@","@@@","@@@","@@@","@@@"}));
                }
            });
    }
}
