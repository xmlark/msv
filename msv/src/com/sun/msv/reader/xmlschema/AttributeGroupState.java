/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.xmlschema;

import com.sun.msv.datatype.DataType;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.xmlschema.AttributeGroupExp;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.reader.State;
import org.xml.sax.Locator;

/**
 * parses &lt;attributeGroup /&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeGroupState extends RedefinableDeclState {
	
	protected State createChildState( StartTagInfo tag ) {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		return reader.createAttributeState(this,tag);
	}

	protected ReferenceContainer getContainer() {
		return ((XMLSchemaReader)reader).currentSchema.attributeGroups;
	}
	
	protected Expression initialExpression() {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
		String refQName = startTag.getAttribute("ref");
		if( refQName==null )
			// child expressions are expected. (although it's optional)
			return Expression.epsilon;

		Expression exp = reader.resolveQNameRef(
			startTag, "ref",
			new XMLSchemaReader.RefResolver() {
				public ReferenceContainer get( XMLSchemaSchema g ) {
					return g.attributeGroups;
				}
			} );
		if( exp==null )		return Expression.epsilon;	// couldn't resolve QName.
		return exp;
	}

	protected Expression castExpression( Expression halfCastedExpression, Expression newChildExpression ) {
		if( startTag.containsAttribute("ref") )
			reader.reportError( reader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION );
		if( halfCastedExpression==null )
			return newChildExpression;	// the first one.
		return reader.pool.createSequence( newChildExpression, halfCastedExpression );
	}
	
	protected Expression annealExpression(Expression contentType) {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
		if( !isGlobal() )		return contentType;
		
		// if this is a global declaration register it.
		String name = startTag.getAttribute("name");
		if( name==null ) {
			reader.reportError( reader.ERR_MISSING_ATTRIBUTE, "attributeGroup", "name" );
			return Expression.epsilon;
			// recover by returning something meaningless.
			// the parent state will ignore this.
		}
		AttributeGroupExp exp;
		if( isRedefine() )
			exp = (AttributeGroupExp)super.oldDecl;
		else {
			exp = reader.currentSchema.attributeGroups.getOrCreate(name);
			if( exp.exp!=null )
				reader.reportError( 
					new Locator[]{this.location,reader.getDeclaredLocationOf(exp)},
					reader.ERR_DUPLICATE_ATTRIBUTE_GROUP_DEFINITION,
					new Object[]{name} );
		}
		reader.setDeclaredLocationOf(exp);
		exp.exp = contentType;
		return exp;
	}
}
