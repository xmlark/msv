package com.sun.msv.relaxns.reader;

import javax.xml.parsers.SAXParserFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import util.Checker;
import util.ResourceChecker;

import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.relax.RELAXReader;

public class RELAXNSReaderTest extends TestCase
{
    public RELAXNSReaderTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(RELAXNSReaderTest.class);
    }
    
    /** tests the existence of all messages */
    public void testMessages() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        
        final RELAXNSReader reader = new RELAXNSReader(null,factory,null);
        
        Checker checker = new Checker(){
            public void check( String propertyName ) {
                // if the specified property doesn't exist, this will throw an error
                System.out.println(
                    reader.localizeMessage(propertyName,new Object[]{"@@@","@@@","@@@","@@@","@@@"}));
            }
        };
        
        String prefixes[] = new String[]{"ERR_","WRN_"};
        
        for( int i=0; i<prefixes.length; i++ ) {
            ResourceChecker.check( RELAXNSReader.class, prefixes[i], checker );
            ResourceChecker.check( RELAXReader.class, prefixes[i], checker );
            ResourceChecker.check( GrammarReader.class, prefixes[i], checker );
        }
    }
}
