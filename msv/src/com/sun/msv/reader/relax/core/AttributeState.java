/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.relax.core;

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.TypeIncubator;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.reader.ExpressionState;
import com.sun.msv.reader.State;
import com.sun.msv.reader.datatype.xsd.FacetStateParent;
import com.sun.msv.util.StartTagInfo;
import org.relaxng.datatype.DatatypeException;

/**
 * parses &lt;attribute&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeState extends ExpressionState implements FacetStateParent
{
	protected TypeIncubator incubator;
	
	public TypeIncubator getIncubator() { return incubator; }
	
	protected void startSelf() {
		super.startSelf();
		String type		= startTag.getAttribute("type");
		if(type==null)	type="string";
		incubator = new TypeIncubator( (XSDatatype)reader.resolveDataType(type) );
	}
	
	protected Expression makeExpression() {
		try	{
			final String name		= startTag.getAttribute("name");
			final String required	= startTag.getAttribute("required");
			
			if( name==null ) {
				reader.reportError( reader.ERR_MISSING_ATTRIBUTE, "attribute","name" );
				// recover by ignoring this attribute.
				// since attributes are combined by sequence, so epsilon is appropriate.
				return Expression.epsilon;
			}
			
			Expression value;
			
			if( !startTag.containsAttribute("type") && incubator.isEmpty() )
				// we can use cheaper anyString
				value = Expression.anyString;
			else
				value = reader.pool.createTypedString( incubator.derive(null) );
			
			Expression exp = reader.pool.createAttribute(
				new SimpleNameClass("",name),
				value );
			
			// unless required attribute is specified, it is considered optional
			if(! "true".equals(required) )
				exp = reader.pool.createOptional(exp);
			
			return exp;
		} catch( DatatypeException e ) {
			// derivation failed
			reader.reportError( e, reader.ERR_BAD_TYPE, e.getMessage() );
			// recover by using harmless expression. anything will do.
			return Expression.anyString;
		}
	}
	
	protected State createChildState( StartTagInfo tag ) {
		return ((RELAXCoreReader)reader).createFacetState(this,tag);	// facets
	}
}
