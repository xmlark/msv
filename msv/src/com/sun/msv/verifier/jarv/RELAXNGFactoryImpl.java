/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.jarv;

import org.iso_relax.verifier.*;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.trex.ng.RELAXNGReader;
import com.sun.msv.reader.GrammarReaderController;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * VerifierFactory implementation of RELAX NG.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXNGFactoryImpl extends FactoryImpl {
	
	protected Grammar parse( InputSource is, GrammarReaderController controller ) {
		return RELAXNGReader.parse(is,factory,controller);
	}
	protected Grammar parse( String source, GrammarReaderController controller ) {
		return RELAXNGReader.parse(source,factory,controller);
	}
}
