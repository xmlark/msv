package com.sun.msv.verifier.identity;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import util.Checker;
import util.ResourceChecker;

public class IDConstraintCheckerTest extends TestCase
{
    public IDConstraintCheckerTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(IDConstraintCheckerTest.class);
    }
    
    /** tests the existence of all messages */
    public void testMessages() throws Exception {

        Checker checker = new Checker(){
            public void check( String propertyName ) {
                // if the specified property doesn't exist, this will throw an error
                System.out.println(
                    IDConstraintChecker.localizeMessage(propertyName,new Object[]{"@@@","@@@","@@@","@@@","@@@"}));
            }
        };
        
        ResourceChecker.check( IDConstraintChecker.class, "", checker );
    }
}
