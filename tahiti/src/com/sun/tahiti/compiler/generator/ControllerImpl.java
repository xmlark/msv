/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.compiler.generator;

import com.sun.msv.reader.GrammarReaderController;
import org.xml.sax.Locator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.IOException;

/**
 * an implementation of the Controller interface.
 * 
 * This implementation delegates GrammarReaderController events to
 * another object.
 */
public abstract class ControllerImpl implements Controller {
	
	protected ControllerImpl( GrammarReaderController core ) {
		this.core = core;
	}
	
	protected GrammarReaderController core;
	
	public void error( Locator[] locs, String msg, Exception e ) {
		core.error( locs, msg, e );
	}
	public void warning( Locator[] locs, String msg ) {
		core.warning( locs, msg );
	}
	public InputSource resolveEntity( String a, String b )
				throws SAXException, IOException {
		return core.resolveEntity(a,b);
	}
}
