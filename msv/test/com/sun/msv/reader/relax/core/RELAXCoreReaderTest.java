package com.sun.msv.reader.relax.core;

import javax.xml.parsers.SAXParserFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import util.Checker;
import util.ResourceChecker;

import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.relax.RELAXReader;

public class RELAXCoreReaderTest extends TestCase
{
    public RELAXCoreReaderTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(RELAXCoreReaderTest.class);
    }
    
    /** tests the existence of all messages */
    public void testMessages() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        
        final RELAXCoreReader reader = new RELAXCoreReader(null,factory,null);
        
        Checker checker = new Checker(){
            public void check( String propertyName ) {
                // if the specified property doesn't exist, this will throw an error
                System.out.println(
                    reader.localizeMessage(propertyName,new Object[]{"@@@","@@@","@@@","@@@","@@@"}));
            }
        };
        
        String prefixes[] = new String[]{"ERR_","WRN_"};
        
        for( int i=0; i<prefixes.length; i++ ) {
            ResourceChecker.check( RELAXCoreReader.class, prefixes[i], checker );
            ResourceChecker.check( RELAXReader.class, prefixes[i], checker );
            ResourceChecker.check( GrammarReader.class, prefixes[i], checker );
        }
    }
}
