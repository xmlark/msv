/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype;

import java.util.Set;
import java.util.Vector;

/**
 * "enumeration" facets validator.
 * 
 * @author	Kohsuke Kawaguchi
 */
public class EnumerationFacet extends DataTypeWithValueConstraintFacet {
	protected EnumerationFacet( String typeName, DataTypeImpl baseType, TypeIncubator facets )
		throws BadTypeException {
		super(typeName,baseType,FACET_ENUMERATION,facets);
		values = new java.util.HashSet( facets.getVector(FACET_ENUMERATION) );
	}
	
	/** set of valid values */
	public final Set values;

	public Object convertToValue( String literal, ValidationContextProvider context ) {
		Object o = baseType.convertToValue(literal,context);
		if(o==null || !values.contains(o))		return null;
		return o;
	}
	
	protected DataTypeErrorDiagnosis diagnoseByFacet(String content, ValidationContextProvider context) {
		if( convertToValue(content,context)!=null )	return null;
		
		// TODO: guess which item the user was trying to specify
		
		if( values.size()<=4 ) {
			// if choices are small in number, include them into error messages.
			Object[] members = values.toArray();
			String r="";
			
			if( members[0] instanceof String
			||  members[0] instanceof Number ) {
				// this will cover 80% of the use case.
				r += "\""+members[0].toString()+"\"";
				for( int i=1; i<members.length; i++ )
					r+= "/\""+members[i].toString()+"\"";
				
				r = "("+r+")";	// oh, don't tell me I should use StringBuffer.
				
				return new DataTypeErrorDiagnosis(this, content, -1,
					localize(ERR_ENUMERATION_WITH_ARG, r) );
			}
		}
		return new DataTypeErrorDiagnosis(this, content, -1,
			localize(ERR_ENUMERATION) );
	}

}
