/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier;

import com.sun.msv.datatype.DataType;
import org.xml.sax.Locator;

/**
 * Interface of verifier.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface IVerifier extends org.xml.sax.ContentHandler {

	/**
	 * checks if the document was valid.
	 * This method may not be called before verification was completed.
	 */
	boolean isValid();
	
	/**
	 * returns current element type.
	 * 
	 * Actual java type depends on the implementation.
	 * This method works correctly only when called immediately
	 * after handling startElement event.
	 * 
	 * @return null
	 *		this method returns null when it doesn't support
	 *		type-assignment feature, or type-assignment is impossible
	 *		for the current element (for example due to the ambiguous grammar).
	 */
	Object getCurrentElementType();
	
	/**
	 * gets DataType that validated the last characters.
	 * 
	 * <p>
	 * This method works correctly only when called immediately
	 * after startElement and endElement method. When called, this method
	 * returns DataType object that validated the last character literals.
	 * 
	 * <p>
	 * So when you are using VerifierFilter, you can call this method only
	 * in your startElement and endElement method.
	 * 
	 * @return null
	 *		if type-assignment was not possible.
	 */
	DataType getLastCharacterType();


	Locator getLocator();
	VerificationErrorHandler getVErrorHandler();
}
