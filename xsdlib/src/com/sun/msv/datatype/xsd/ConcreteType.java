/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;

import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.DatatypeException;

/**
 * base class for those types which can be used by itself
 * (int,uriReference,string, etc) .
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ConcreteType extends XSDatatypeImpl {
	
	protected ConcreteType( String typeName, WhiteSpaceProcessor whiteSpace ) {
		super( typeName, whiteSpace );
	}
	
	protected ConcreteType( String typeName ) {
		this( typeName, WhiteSpaceProcessor.theCollapse );
	}
	
	final public ConcreteType getConcreteType() {
		return this;
	}

	// as a default implementation, this method returns VARIETY_ATOMIC.
	public int getVariety() {
		return VARIETY_ATOMIC;
	}
	
	public boolean isFinal( int derivationType ) {
		// allow derivation by default.
		return false;
	}
	
	public final String displayName() {
		return getName();
	}
	
	protected Object readResolve() throws java.io.ObjectStreamException {
		// return the sigleton object, if any.
		String name = getName();
		if(name!=null) {
			XSDatatype dt = DatatypeFactory.getTypeByName(name);
			if(dt!=null)
				return dt;
		}
		
		return this;
	}

	// default implementation for concrete type. somewhat shabby.
	protected void diagnoseValue(String content, ValidationContext context) throws DatatypeException {
		if(checkFormat(content,context))	return;
		
		throw new DatatypeException(DatatypeException.UNKNOWN,
			localize(ERR_INAPPROPRIATE_FOR_TYPE, content, getName()) );
	}
}
