/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.xmlschema;

import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.datatype.BadTypeException;
import com.sun.tranquilo.datatype.TypeIncubator;
import com.sun.tranquilo.datatype.StringType;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.reader.SequenceState;
import com.sun.tranquilo.reader.datatype.TypeOwner;
import com.sun.tranquilo.reader.datatype.xsd.FacetStateParent;
import com.sun.tranquilo.util.StartTagInfo;

public class SimpleContentBodyState extends SequenceState
	implements FacetStateParent,TypeOwner {
	
	protected final boolean extension;
	
	protected SimpleContentBodyState( boolean extension ) {
		this.extension = extension;
	}
	
	/** used to restrict simpleType */
	protected TypeIncubator incubator;
	public TypeIncubator getIncubator() { return incubator; }
	
	public void onEndChild( DataType child ) {
		if( incubator!=null )
			// assertion failed.
			// createChildState should reject 2nd <simpleType> element.
			throw new Error();
		incubator = new TypeIncubator(child);
	}
	
	protected State createChildState( StartTagInfo tag ) {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
		if( incubator==null && !extension && tag.localName.equals("simpleType") )
			return reader.sfactory.simpleType(this,tag);
		
		State s = reader.createAttributeState(this,tag);
		if(s!=null )	return s;
		
		return reader.createFacetState(this,tag);	// facets
	}
	
	protected void startSelf() {
		super.startSelf();
		
		String base	= startTag.getAttribute("base");
		if(base!=null)
			incubator = new TypeIncubator( reader.resolveDataType(base) );
		else {
			if(extension) {
				// in extension, base attribute must is mandatory.
				reader.reportError( reader.ERR_MISSING_ATTRIBUTE, startTag.localName, "base");
				// recover by pretending base="string"
				incubator = new TypeIncubator( StringType.theInstance );
			}
			// in case of restriction, child <simpleType> may be present.
		}
	}
	
	protected Expression initialExpression() {
		// without this statement,
		// <extension>/<restriction> without any attribute will be prohibited.
		return Expression.epsilon;
	}
	
	protected Expression annealExpression( Expression exp ) {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		try	{
			exp = super.annealExpression(exp);
			
			Expression typedStr;
			if( incubator==null ) {
				// neither @base nor <simpleType> was present.
				reader.reportError( reader.ERR_MISSING_ATTRIBUTE, startTag.qName, "base" );
				// recover by pretending some expression
				typedStr = Expression.nullSet;
			} else {
				typedStr = reader.pool.createTypedString( incubator.derive(null) );
			}

			return reader.pool.createSequence( typedStr, exp );
		} catch( BadTypeException e ) {
			// derivation failed
			reader.reportError( e, reader.ERR_BAD_TYPE, e.getMessage() );
			// recover by using harmless expression. anything will do.
			return Expression.nullSet;
		}
	}
}
