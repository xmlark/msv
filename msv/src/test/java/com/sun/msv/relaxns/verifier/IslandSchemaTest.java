package com.sun.msv.relaxns.verifier;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import util.Checker;
import util.ResourceChecker;

public class IslandSchemaTest extends TestCase
{
    public IslandSchemaTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(IslandSchemaTest.class);
    }
    
    /** tests the existence of all messages */
    public void testMessages() throws Exception {
        final IslandSchemaImpl.Binder impl = new IslandSchemaImpl.Binder(null,null,null);/*{
            // dummy implementation
            public void bind( SchemaProvider p, ErrorHandler e ) {}
            public Grammar getGrammar() { return null; }
        };*/
        
        Checker checker = new Checker(){
            public void check( String propertyName ) {
                // if the specified property doesn't exist, this will throw an error
                System.out.println(
                    impl.localize(propertyName,new Object[]{"@@@","@@@","@@@","@@@","@@@"}));
            }
        };
        
        ResourceChecker.check( IslandSchemaImpl.Binder.class, "", checker );
    }
}
