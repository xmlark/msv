package com.sun.msv.scanner.dtd;

import java.text.MessageFormat;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DumpHandler implements DTDEventListener {

    private int indent = 0;
    private void printIndent() {
        for( int i=0; i<indent; i++ )
            System.out.print("  ");
    }
    
    protected void printFormat( String format, Object[] args ) {
        printIndent();
        System.out.println(MessageFormat.format(format,args));
    }
    
    protected void printFormat( String format ) {
        printFormat( format, new Object[]{} );
    }
    
    protected void printFormat( String format, Object o1 ) {
        printFormat( format, new Object[]{o1} );
    }
    
    protected void printFormat( String format, Object o1, Object o2 ) {
        printFormat( format, new Object[]{o1,o2} );
    }
    
    protected void printFormat( String format, Object o1, Object o2, Object o3 ) {
        printFormat( format, new Object[]{o1,o2,o3} );
    }
    
    protected void printFormat( String format, Object o1, Object o2, Object o3, Object o4 ) {
        printFormat( format, new Object[]{o1,o2,o3,o4} );
    }
    
    public void processingInstruction(String target, String data) {
        printFormat("pi({0},{1})",target,data);
    }
    
    public void fatalError(SAXParseException e) throws SAXException {
        throw e;
    }

    public void error(SAXParseException e) throws SAXException {
        throw e;
    }
    
    public void warning(SAXParseException err) {
        printFormat("warning({0})",err.getMessage());
    }
    
    public void notationDecl(String name, String publicId, String systemId) {
        printFormat("notation({0},{1},{2})", name, publicId, systemId );
    }
    
    public void unparsedEntityDecl(String name, String publicId, 
                                    String systemId, String notationName) {
        printFormat("unparsedEntity({0},{1},{2},{3})", name, publicId, systemId, notationName );
    }
    
    public void startDTD (InputEntity in) {
        printFormat("startDTD()");
        indent++;
    }
    
    public void endDTD() throws SAXException {
        indent--;
        printFormat("endDTD()");
    }
    
    public void externalGeneralEntityDecl(String n, String p, String s) {
        printFormat("externalGeneralEntityDecl({0},{1},{2})", n, p, s );
    }
    
    public void internalGeneralEntityDecl (String n, String v) {
        printFormat("internalGeneralEntityDecl({0},{1})", n, v );
    }
    
    public void externalParameterEntityDecl (String n, String p, String s) {
        printFormat("externalParameterEntityDecl({0},{1},{2})", n, p, s );
    }
    
    public void internalParameterEntityDecl (String n, String v) {
        printFormat("internalParameterEntityDecl({0},{1})", n, v );
    }
    
    public void comment (String n) {
        printFormat("comment({0})",n);
    }
    
    public void characters (char ch[], int start, int length) {
    }
    
    public void ignorableWhitespace (char ch[], int start, int length) {
    }
    
    public void startCDATA () {
    }

    public void endCDATA () {
    }
    
    protected String getModelTypeString( short type ) {
        switch(type) {
        case CONTENT_MODEL_ANY:            return "ANY";
        case CONTENT_MODEL_CHILDREN:    return "CHILDREN";
        case CONTENT_MODEL_EMPTY:        return "EMPTY";
        case CONTENT_MODEL_MIXED:        return "MIXED";
        default:                        throw new Error();
        }
    }
    
    public void startContentModel( String elementName, short t ) {
        printFormat("startContentModel({0},{1})",elementName,getModelTypeString(t) );
        indent++;
    }
    
    public void endContentModel( String elementName, short t ) {
        indent--;
        printFormat("endContentModel({0},{1})",elementName,getModelTypeString(t));
    }
    
    protected String use( short useType ) {
        switch(useType) {
        case USE_FIXED:        return "#FIXED";
        case USE_IMPLIED:    return "#IMPLIED";
        case USE_NORMAL:    return "(normal use)";
        case USE_REQUIRED:    return "#REQUIRED";
        default:            throw new Error();
        }
    }
    
    public void attributeDecl(
        String elementName, String attributeName, String attributeType,
        String[] enumeration, short attributeUse, String defaultValue ){
        printFormat("attributeDecl({0},{1},{2},{3})",
            elementName, attributeName, attributeType, use(attributeUse) );
        
        indent++;
        if( enumeration!=null )
            for( int i=0; i<enumeration.length; i++ )
                printFormat("enum({0})",enumeration[i]);
        if( defaultValue!=null )
            printFormat("default({0})",defaultValue);
        
        indent--;
        
    }
    
    public void childElement( String elementName, short occurence ) {
        printFormat("childElement({0}{1})",elementName,occurType(occurence));
    }
    
    public void mixedElement( String elementName ) {
        printFormat("mixedElement({0})",elementName);
    }
    
    public void startModelGroup() {
        printFormat("startModelGroup()");
        indent++;
    }
    
    public void endModelGroup( short occurence ) {
        indent--;
        printFormat("endModelGroup({0})",occurType(occurence));
    }
    
    String combType( short type ) {
        switch(type) {
        case CHOICE:        return "|";
        case SEQUENCE:        return ",";
        default:            throw new Error();
        }
    }
    
    public void connector( short combinatorType ) {
        printFormat("connector({0})",combType(combinatorType) );
    }
    
    String occurType( short type ) {
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
