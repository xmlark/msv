/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.xmlschema;

import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.util.Util;
import java.io.IOException;
import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * An utility class that
 * reads multiple XML Schema documents and combines them into one schema object.
 * 
 * <h2>Usage</h2>
 * <p>
 * Creates a new instance of {@link XMLSchemaReader}, then pass it to the
 * constructor of this class. Then call the parse method as many times as you want.
 * Finally, call the finish method.
 * </p><p>
 * The parsed grammar can be obtained from the underlying XMLSchemaReader object.
 * </p>
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class MultiSchemaReader
{
    public MultiSchemaReader( XMLSchemaReader _reader ) {
        this.reader = _reader;
        reader.setDocumentLocator(new LocatorImpl());
    }
    
    private final XMLSchemaReader reader;
    
    public final XMLSchemaReader getReader() { return reader; }
    
    
    
    public final void parse( String source ) {
        parse(Util.getInputSource(source));
    }
    
    /**
     * Parses an additional schema.
     * 
     * The caller can call this method many times to parse
     * multiple schemas.
     * 
     * If possible, the caller should set the system Id to the InputSource.
     */
    public void parse( InputSource is ) {
        
        reader.switchSource( is,
            new RootIncludedSchemaState(
                reader.sfactory.schemaHead(null)) );
    }
    
    /**
     * This method should be called when all the schemas are parsed.
     */
    public void finish() {
        // do the final wrap-up work.
        reader.wrapUp();
    }
}
