package com.sun.msv.reader.trex.classic;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import util.Checker;
import util.ResourceChecker;

import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.trex.TREXBaseReader;

public class TREXGrammarReaderTest extends TestCase
{
    public TREXGrammarReaderTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(TREXGrammarReaderTest.class);
    }
    
    /** tests the existence of all messages */
    public void testMessages() throws Exception {
        final TREXGrammarReader reader = new TREXGrammarReader(null);
        
        Checker checker = new Checker(){
            public void check( String propertyName ) {
                // if the specified property doesn't exist, this will throw an error
                System.out.println(
                    reader.localizeMessage(propertyName,new Object[]{"@@@","@@@","@@@","@@@","@@@"}));
            }
        };
        
        String prefixes[] = new String[]{"ERR_","WRN_"};
        
        for( int i=0; i<prefixes.length; i++ ) {
            ResourceChecker.check( TREXGrammarReader.class, prefixes[i], checker );
            ResourceChecker.check( TREXBaseReader.class, prefixes[i], checker );
            ResourceChecker.check( GrammarReader.class, prefixes[i], checker );
        }
    }
}
