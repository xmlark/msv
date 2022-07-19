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

import org.iso_relax.verifier.VerifierFactory;
import org.iso_relax.verifier.VerifierFactoryLoader;

import com.sun.msv.reader.relax.core.RELAXCoreReader;
import com.sun.msv.reader.trex.classic.TREXGrammarReader;
import com.sun.msv.reader.trex.ng.RELAXNGReader;
import com.sun.msv.reader.xmlschema.XMLSchemaReader;
import com.sun.msv.relaxns.reader.RELAXNSReader;
    
public class FactoryLoaderImpl implements VerifierFactoryLoader {
    public FactoryLoaderImpl() {
    }

    public VerifierFactory createFactory(String language) {

        // supported language
        if (language.equals(RELAXNGReader.RELAXNGNamespace))
            return new RELAXNGFactoryImpl();
        if (language.equals(RELAXCoreReader.RELAXCoreNamespace))
            return new RELAXCoreFactoryImpl();
        if (language.equals(TREXGrammarReader.TREXNamespace))
            return new TREXFactoryImpl();
        if (language.equals(XMLSchemaReader.XMLSchemaNamespace)
            || language.equals(XMLSchemaReader.XMLSchemaNamespace_old))
            return new XSFactoryImpl();
        if(language.equals(RELAXNSReader.RELAXNamespaceNamespace))
            return new TheFactoryImpl();
        if(language.equals("http://www.w3.org/XML/1998/namespace"))
            return new DTDFactoryImpl();
        
        // backward compatibility
        if (language.equals("relax"))
            return new TheFactoryImpl();
        if (language.toUpperCase().equals("DTD"))
            return new DTDFactoryImpl();

        return null;
    }
}
