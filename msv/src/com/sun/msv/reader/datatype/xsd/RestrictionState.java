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

import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.State;
import com.sun.msv.datatype.BadTypeException;
import com.sun.msv.datatype.StringType;
import com.sun.msv.datatype.DataType;
import com.sun.msv.datatype.TypeIncubator;
import com.sun.msv.util.StartTagInfo;

/**
 * state that parses &lt;restriction&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RestrictionState extends TypeWithOneChildState implements FacetStateParent {
	
	protected final String newTypeName;
	protected RestrictionState( String newTypeName ) {
		this.newTypeName = newTypeName;
	}
	
	protected TypeIncubator incubator;
	public final TypeIncubator getIncubator() {
		return incubator;
	}

	protected DataType annealType( DataType baseType ) throws BadTypeException {
		return incubator.derive(newTypeName);
	}
	
	public void onEndChild( DataType child ) {
		super.onEndChild(child);
		incubator = new TypeIncubator(super.type);
	}

	
	protected void startSelf() {
		super.startSelf();
		
		// if base attribute is used, try to load it.
		String base = startTag.getAttribute("base");
		if(base!=null) {
			type = reader.resolveDataType(base);
			incubator = new TypeIncubator(type);
		}
	}

	protected State createChildState( StartTagInfo tag ) {
		// accepts elements from the same namespace only.
		if( !startTag.namespaceURI.equals(tag.namespaceURI) )	return null;
		
		if( tag.localName.equals("annotation") )	return new IgnoreState();
		if( tag.localName.equals("simpleType") )	return new SimpleTypeState();
		if( FacetState.facetNames.contains(tag.localName) ) {
			if( incubator==null ) {
				reader.reportError( reader.ERR_MISSING_ATTRIBUTE, "restriction", "base" );
				type = reader.resolveDataType("string");	// assume some type
				incubator = new TypeIncubator(type);
			}
			return new FacetState();
		}
		
		return null;	// unrecognized
	}
}
