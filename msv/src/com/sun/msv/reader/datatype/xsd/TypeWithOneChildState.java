/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.datatype.xsd;

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.State;
import com.sun.msv.reader.ExpressionWithChildState;
import com.sun.msv.reader.datatype.TypeOwner;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;

/**
 * State which has at most one TypeState as its child.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class TypeWithOneChildState extends TypeState implements TypeOwner
{
	protected XSDatatype type;

	/** receives a Pattern object that is contained in this element. */
	public void onEndChild( XSDatatype child ) {
		if( type!=null )
			reader.reportError( reader.ERR_MORE_THAN_ONE_CHILD_TYPE );
			// recover by ignoring this child
		else
			type = child;
	}
	
	protected final XSDatatype makeType() throws DatatypeException {
		if( type==null ) {
			reader.reportError( reader.ERR_MISSING_CHILD_TYPE );
			return StringType.theInstance;	// recover by supplying a dummy DataType
		}
		return annealType(type);
	}

	/**
	 * performs final wrap-up and returns a fully created DataType object
	 * that represents this element.
	 */
	protected XSDatatype annealType( XSDatatype dt ) throws DatatypeException
	{
		// default implementation do nothing.
		return dt;
	}
}
