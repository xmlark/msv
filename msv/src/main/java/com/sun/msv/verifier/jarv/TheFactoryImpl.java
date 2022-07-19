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

import javax.xml.parsers.SAXParserFactory;

import org.iso_relax.verifier.VerifierConfigurationException;
import org.xml.sax.InputSource;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.util.GrammarLoader;

/**
 * VerifierFactory implementation that automatically detects the schema language.
 * 
 * To use this class, see 
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TheFactoryImpl extends FactoryImpl {
    public TheFactoryImpl(SAXParserFactory factory) {
        super(factory);
    }

    /**
     * use the default SAXParser.
     */
    public TheFactoryImpl() {
        super();
    }

    protected Grammar parse(InputSource is, GrammarReaderController controller) throws VerifierConfigurationException {
        try {
            return GrammarLoader.loadSchema(is, controller, factory);
        } catch (Exception e) {
            throw new VerifierConfigurationException(e);
        }
    }

    protected Grammar parse(String source, GrammarReaderController controller) throws VerifierConfigurationException {
        try {
            return GrammarLoader.loadSchema(source, controller, factory);
        } catch (Exception e) {
            throw new VerifierConfigurationException(e);
        }
    }
}
