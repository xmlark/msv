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

import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

/**
 * {@link ErrorHandler} that reports only the first error.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class WordlessErrorReporter implements ErrorHandler {
	
	private boolean first = true;
	private SAXParseException error = null;
	
	public SAXParseException getError() { return error; }
	
	public void fatalError( SAXParseException e ) throws SAXParseException {
		error(e);
		throw e;
	}
	public void error( SAXParseException error ) {
		if( first ) {
			System.out.println(error.getMessage());
			this.error = error;
		}
		first = false;
	}
		
	public void warning( SAXParseException warning ) {}
}
