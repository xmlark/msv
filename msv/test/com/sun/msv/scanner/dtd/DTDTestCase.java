package com.sun.msv.scanner.dtd;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.iso_relax.dispatcher.Dispatcher;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

public class DTDTestCase extends TestCase {
    
    public DTDTestCase( String name ) {
        super(name);
    }

    public class TestHandler implements DTDEventListener {

        private boolean started = false;
        private boolean ended = false;
        
        public final Map pi = new HashMap();
        
        public void processingInstruction(String target, String data) {
            pi.put(target,data);
        }

        public final Map notations = new HashMap();
        
        public void notationDecl(String name, String publicId, String systemId) {
            notations.put( name, new Dispatcher.NotationDecl(name,publicId,systemId) );
        }

        public final Map unparsedEntities = new HashMap();
        
        public void unparsedEntityDecl(String name, String publicId, 
            String systemId, String notationName) {
            
            unparsedEntities.put( name,
                new Dispatcher.UnparsedEntityDecl(name,publicId,systemId,notationName) );
        }

        public final Map internalEntities = new HashMap();
        public void internalGeneralEntityDecl( String name, String value ) {
            internalEntities.put(name,value);
        }

        public final Map externalEntities = new HashMap();
        public void externalGeneralEntityDecl(String name, String publicId, String systemId) {
            externalEntities.put( name, new Dispatcher.NotationDecl(name,publicId,systemId) );
        }

        public void internalParameterEntityDecl (String name, String value) {
            internalGeneralEntityDecl( "%"+name, value );
        }

        public void externalParameterEntityDecl (String name, String publicId, String systemId) {
            externalGeneralEntityDecl( "%"+name, publicId, systemId );
        }

        public void startDTD(InputEntity in) {
            assertTrue(!started && !ended );
            started = true;
        }

        public void endDTD() {
            assertTrue(started && !ended);
            ended = true;
        }

        public void comment (String text) {}
        public void characters (char ch[], int start, int length) {}
        public void ignorableWhitespace (char ch[], int start, int length) {}
        public void startCDATA () {}
        public void endCDATA () {}


        public void fatalError(SAXParseException e) throws SAXParseException { throw e; }
        public void error(SAXParseException e) throws SAXParseException { throw e; }
        public void warning(SAXParseException err) {}

        private String elementName;
        private short contentModelType;
        private boolean inContentModel;
        private String model;
        
        private final Map contentModels = new HashMap();
        
        public void startContentModel( String elementName, short contentModelType ) {
            assertTrue(!inContentModel);
            inContentModel = true;
            this.elementName = elementName;
            this.contentModelType = contentModelType;
            model = "";
        }
        
        public void endContentModel( String elementName, short contentModelType ) {
            assertTrue(inContentModel);
            inContentModel = false;
            assertEquals( elementName, this.elementName );
            assertEquals( contentModelType, this.contentModelType );
            
            contentModels.put( elementName, model );
            model = null;
        }

        public void attributeDecl(
            String elementName, String attributeName, String attributeType,
            String[] enumeration, short attributeUse, String defaultValue ) {
            // TODO: @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        }
        
        public void childElement( String elementName, short occurence ) {
            model += elementName + getOccurs(occurence);
        }
        
        public void mixedElement( String elementName ) {
            model += "<"+elementName+">";
        }
        
        public void startModelGroup() {
            model += "(";
        }
        public void endModelGroup( short occurence ) {
            model += ")" + getOccurs(occurence);
        }
        
        public void connector( short connectorType ) {
            model += getConnector(connectorType);
        }
        
        String getConnector( short type ) {
            switch(type) {
            case CHOICE:        return "|";
            case SEQUENCE:        return ",";
            default:            throw new Error();
            }
        }
    
        String getOccurs( short type ) {
            switch(type) {
            case OCCURENCE_ONE_OR_MORE:        return "+";
            case OCCURENCE_ZERO_OR_MORE:    return "*";
            case OCCURENCE_ZERO_OR_ONE:        return "?";
            case OCCURENCE_ONCE:            return "";
            default:            throw new Error();
            }
        }
        
        public void setDocumentLocator( Locator loc ) {}
    }
}
