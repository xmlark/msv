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

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.trex.TypedString;
import com.sun.msv.grammar.xmlschema.AttributeDeclExp;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringPair;
import com.sun.msv.reader.State;
import com.sun.msv.reader.ExpressionWithChildState;
import org.xml.sax.Locator;

/**
 * used to parse &lt;attribute &gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeState extends ExpressionWithChildState {
	
	protected State createChildState( StartTagInfo tag ) {
		if( tag.localName.equals("simpleType") )
			return ((XMLSchemaReader)reader).sfactory.simpleType(this,tag);
		
		return super.createChildState(tag);
	}
	
	protected Expression initialExpression() {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
		if( startTag.containsAttribute("ref") ) {
			if( isGlobal() ) {
				reader.reportError( reader.ERR_DISALLOWED_ATTRIBUTE,
					startTag.qName, "ref" );
				return Expression.epsilon;
			}
			
			// this tag has @ref.
			Expression exp = reader.resolveQNameRef(
				startTag, "ref",
				new XMLSchemaReader.RefResolver() {
					public ReferenceContainer get( XMLSchemaSchema g ) {
						return g.attributeDecls;
					}
				} );
			if( exp==null )		return Expression.epsilon;	// couldn't resolve QName.
			return exp;
		}
		
		final String typeAttr = startTag.getAttribute("type");
		if( typeAttr==null )
			// return null to indicate that no type definition is given.
			return null;
		
		// if <attribute> element has @type, then
		// it shall be used as content type.
		return reader.resolveDelayedDataType( typeAttr );
	}

	protected Expression defaultExpression() {
		// if no type definition is given, assume ur-type.
		return Expression.anyString;
	}
	
	protected Expression castExpression( Expression halfCastedExpression, Expression newChildExpression ) {
		if( halfCastedExpression!=null )
			// only one child is allowed.
			// recover by ignoring previously found child expressions.
			reader.reportError( reader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION );
		
		return newChildExpression;
	}
	
	protected Expression annealExpression(Expression contentType) {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		final String fixed = startTag.getAttribute("fixed");
		final String name = startTag.getAttribute("name");


		Expression exp;
		
		if( startTag.containsAttribute("ref") ) {
			if( fixed!=null )
				reader.reportError( reader.ERR_UNIMPLEMENTED_FEATURE,
					"<attribute> element with both 'ref' and 'fixed' attributes" );
			
			exp = contentType;
		} else {
			// TODO: form attribute is prohibited in several occasions.
			String targetNamespace;
		
			if( isGlobal() )	targetNamespace = reader.currentSchema.targetNamespace;
			else
				// in local attribute declaration,
				// targetNamespace is affected by @form and schema's @attributeFormDefault.
				targetNamespace = reader.resolveNamespaceOfAttributeDecl(
					startTag.getAttribute("form") );
		
			if( fixed!=null )
				// TODO: is this 'fixed' value should be added through enumeration facet?
				// in that way, we can check if this value is acceptable as the base type.
				contentType = reader.pool.createTypedString(
					new TypedString(fixed,false),
					new StringPair("$xsd","fixed") );
		
			exp = reader.pool.createAttribute(
				new SimpleNameClass( targetNamespace, name ),
				contentType );
		}
		
		if( isGlobal() ) {
			
			// register this expression as a global attribtue declaration.
			AttributeDeclExp decl = reader.currentSchema.attributeDecls.getOrCreate(name);
			if(decl.exp!=null)
				reader.reportError( 
					new Locator[]{this.location,reader.getDeclaredLocationOf(decl)},
					reader.ERR_DUPLICATE_ATTRIBUTE_DEFINITION,
					new Object[]{name} );
			reader.setDeclaredLocationOf(decl);
			if( exp instanceof AttributeExp )
				decl.set( (AttributeExp)exp );
			else {
				// sometimes, because of the error recovery,
				// exp can be something other than an AttributeExp.
				if( !reader.hadError )	throw new Error();
			}
			
			// TODO: @use is prohibited in global
			
		} else {
			// handle @use
			
			String use = startTag.getAttribute("use");
			if( "prohibited".equals(use) )
				// in case of 'prohibit', the declaraion is simply ignored.
				return Expression.epsilon;
		
			if( "optional".equals(use) || use==null )
				exp = reader.pool.createOptional(exp);
			else
			if( !"required".equals(use) )
				reader.reportError( reader.ERR_BAD_ATTRIBUTE_VALUE, "use", use );
				// recover by assuming "required" (i.e., do nothing)
		}
		
		return exp;
	}

	protected boolean isGlobal() {
		return parentState instanceof GlobalDeclState;
	}
}
