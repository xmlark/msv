package com.sun.msv.verifier.regexp;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import util.Checker;
import util.ResourceChecker;

public class REDocumentDeclarationTest extends TestCase {
    
    public REDocumentDeclarationTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(REDocumentDeclarationTest.class);
    }
    
    /** tests the existence of all messages */
    public void testMessages() throws Exception {
        
        final REDocumentDeclaration decl = new REDocumentDeclaration(null,null);
        
        Checker checker = new Checker(){
            public void check( String propertyName ) {
                // if the specified property doesn't exist, this will throw an error
                System.out.println(
                    decl.localizeMessage(propertyName,new Object[]{"@@@","@@@","@@@","@@@","@@@"}));
            }
        };
        
        ResourceChecker.check( REDocumentDeclaration.class, "", checker );
    }
}
