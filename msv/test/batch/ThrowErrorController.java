/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package batch;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.sun.msv.reader.GrammarReaderController;

/**
 * GrammarReaderController implementation that throws an Error
 * when an error is found.
 * 
 * useful for debug.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ThrowErrorController implements GrammarReaderController {
	
	private final EntityResolver resolver;
	public ThrowErrorController( EntityResolver resolver ) {
		this.resolver = resolver;
	}
	public ThrowErrorController() {
		this(null);
	}
	
	public InputSource resolveEntity(String s,String r) throws SAXException, java.io.IOException {
		if(resolver!=null)
			return resolver.resolveEntity(s,r);
		return null;
	}
	public void error( Locator[] locs, String msg, Exception nested ) {
		if( nested instanceof SAXException ) {
			if(((SAXException)nested).getException()!=null)
				((SAXException)nested).getException().printStackTrace();
		}
		throw new Error(msg);
	}
	public void warning( Locator[] locs, String msg ) {}
}
