/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.compiler.java;

import org.w3c.dom.Element;

/**
 * produces field-access related java soure code.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
interface FieldSerializer {
	
	/** gets the string that represents the type of the field. */
	String getTypeStr();
	
	/** gets the initializer of the field. */
	String getInitializer();
	
	/**
	 * gets the string that will be used to add (or store) an object
	 * to the field.
	 */
	String setField( String objName );
	
//	
// marshaller related methods
//
	/**
	 * produces a code fragment that creates an iterator, if necessary.
	 * @return null
	 *		if this field doesn't need an initializer.
	 */
	String marshallerInitializer();
	
	/**
	 * produces a code fragment that checks the availability of the next token
	 * of this field.
	 */
	String hasMoreToken();
	
	/**
	 * produces a code to marshall the next token of this field, and consumes
	 * that token.
	 * 
	 * @param marshallElement
	 *		
	 */
	String marshall( Element marshallElement );
}
