/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and  use in  source and binary  forms, with  or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions  of  source code  must  retain  the above  copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution  in binary  form must  reproduct the  above copyright
 *   notice, this list of conditions  and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither  the  name   of  Sun  Microsystems,  Inc.  or   the  names  of
 * contributors may be  used to endorse or promote  products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS  OR   IMPLIED  CONDITIONS,  REPRESENTATIONS   AND  WARRANTIES,
 * INCLUDING  ANY  IMPLIED WARRANTY  OF  MERCHANTABILITY,  FITNESS FOR  A
 * PARTICULAR PURPOSE  OR NON-INFRINGEMENT, ARE HEREBY  EXCLUDED. SUN AND
 * ITS  LICENSORS SHALL  NOT BE  LIABLE  FOR ANY  DAMAGES OR  LIABILITIES
 * SUFFERED BY LICENSEE  AS A RESULT OF OR  RELATING TO USE, MODIFICATION
 * OR DISTRIBUTION OF  THE SOFTWARE OR ITS DERIVATIVES.  IN NO EVENT WILL
 * SUN OR ITS  LICENSORS BE LIABLE FOR ANY LOST  REVENUE, PROFIT OR DATA,
 * OR  FOR  DIRECT,   INDIRECT,  SPECIAL,  CONSEQUENTIAL,  INCIDENTAL  OR
 * PUNITIVE  DAMAGES, HOWEVER  CAUSED  AND REGARDLESS  OF  THE THEORY  OF
 * LIABILITY, ARISING  OUT OF  THE USE OF  OR INABILITY TO  USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

package com.sun.msv.reader.xmlschema;

import javax.xml.transform.Source;

import org.xml.sax.InputSource;
import org.xml.sax.helpers.LocatorImpl;

import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.util.Util;

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
    
    private boolean finalized = false;
    
    /**
     * @deprecated
     */
    public final XMLSchemaReader getReader() { return reader; }
    
    /**
     * Obtains the parsed grammar.
     */
    public final XMLSchemaGrammar getResult() {
        finish();
        return reader.getResult();
    }

    /**
     * Parse a schema supplied by a javax.xml.transform Source.
     * @param source
     */
    public void parse(Source source) {
        reader.switchSource(source,
                new RootIncludedSchemaState(
                    reader.sfactory.schemaHead(null)) );
    }
    
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
        if( !finalized ) {
            finalized = true;
            reader.wrapUp();
        }
    }
}
