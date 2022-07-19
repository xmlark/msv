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

package com.sun.msv.verifier.jarv;

import org.relaxng.datatype.DatatypeLibraryFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.trex.ng.RELAXNGReader;

/**
 * VerifierFactory implementation of RELAX NG.
 * 
 * This implementation supports the "datatypeLibraryFactory" property
 * which configures RELAX NG parser with a datatype library factory.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXNGFactoryImpl extends FactoryImpl {
    
    private DatatypeLibraryFactory datatypeLibraryFactory = null;
    
    private static final String PROP_NAME = "datatypeLibraryFactory";
    
    protected Grammar parse( InputSource is, GrammarReaderController controller ) {
        RELAXNGReader reader = new RELAXNGReader(controller,factory);
        if( datatypeLibraryFactory!=null )
            reader.setDatatypeLibraryFactory(datatypeLibraryFactory);
        reader.parse(is);
        
        return reader.getResult();
    }
    
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if( name.equals(PROP_NAME) )
            return datatypeLibraryFactory;
        return super.getProperty(name);
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if( name.equals(PROP_NAME) ) {
            datatypeLibraryFactory = (DatatypeLibraryFactory)value;
            return;
        }
        super.setProperty(name, value);
    }

}
