/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.relax.core;

import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.datatype.BadTypeException;
import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.datatype.TypeIncubator;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.datatype.xsd.FacetStateParent;
import com.sun.tranquilo.util.StartTagInfo;

/**
 * parses &lt;elementRule&gt; with 'type' attribute.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ElementRuleWithTypeState extends ElementRuleBaseState implements FacetStateParent
{
	protected TypeIncubator incubator;
	
	public TypeIncubator getIncubator()	{ return incubator; }
	
	protected void startSelf() {
		super.startSelf();
		
		// existance of type attribute has already checked before
		// this state is created.
		incubator = new TypeIncubator(
			reader.resolveDataType( startTag.getAttribute("type") ) );
	}
	
	protected Expression getContentModel() {
		try {
			return reader.pool.createTypedString( incubator.derive(null) );
		} catch( BadTypeException e ) {
			// derivation failed
			reader.reportError( e, reader.ERR_BAD_TYPE, e.getMessage() );
			// recover by using harmless expression. anything will do.
			return Expression.anyString;
		}
	}
	
	protected State createChildState( StartTagInfo tag ) {
		State next = getReader().createFacetState(this,tag);
		if(next!=null)		return next;			// facets
		
		return super.createChildState(tag);			// or delegate to the base class
	}
}
