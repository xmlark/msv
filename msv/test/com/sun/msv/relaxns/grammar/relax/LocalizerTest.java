package com.sun.msv.relaxns.grammar.relax;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import util.Checker;
import util.ResourceChecker;

public class LocalizerTest extends TestCase
{
    public LocalizerTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(LocalizerTest.class);
    }
    
    /** tests the existence of all messages */
    public void testMessages() throws Exception {
        Checker checker = new Checker(){
            public void check( String propertyName ) {
                // if the specified property doesn't exist, this will throw an error
                System.out.println(
                    Localizer.localize(propertyName,new Object[]{"@@@","@@@","@@@","@@@","@@@"}));
            }
        };
        
        ResourceChecker.check( Localizer.class, "ERR_", checker );
        ResourceChecker.check( Localizer.class, "WRN_", checker );
        ResourceChecker.check( Localizer.class, "MSG_", checker );
    }
}
