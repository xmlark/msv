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
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.SimpleNameClass;
import com.sun.tranquilo.grammar.ReferenceContainer;
import com.sun.tranquilo.grammar.xmlschema.ComplexTypeExp;
import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.reader.State;
import org.xml.sax.Locator;

/**
 * used to parse &lt;complexType&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ComplexTypeDeclState extends RedefinableDeclState {
	
	protected ComplexTypeExp decl;
	
	protected ReferenceContainer getContainer() {
		return ((XMLSchemaReader)reader).currentSchema.complexTypes;
	}
	
	protected void startSelf() {
		super.startSelf();
		
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
		String name = startTag.getAttribute("name");
		if( name==null ) {
			if( isGlobal() )
				reader.reportError( reader.ERR_MISSING_ATTRIBUTE, "complexType", "name" );
			decl = new ComplexTypeExp( reader.currentSchema, null );
		} else {
			if( isRedefine() )
				// in redefine mode, use temporary object.
				// parsed complexType will be copied into the original one.
				decl = new ComplexTypeExp( reader.currentSchema, name );
			else {
				decl = reader.currentSchema.complexTypes.getOrCreate(name);
				if( decl.self.exp!=null )
					reader.reportError( 
						new Locator[]{this.location,reader.getDeclaredLocationOf(decl)},
						reader.ERR_DUPLICATE_COMPLEXTYPE_DEFINITION,
						new Object[]{name} );
			}
		}
	}
	
	protected State createChildState( StartTagInfo tag ) {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
		// simpleContent, ComplexContent, group, all, choice, and sequence
		// are allowed only when we haven't seen type definition.
		if(tag.localName.equals("simpleContent") )	return reader.sfactory.simpleContent(this,tag);
		if(tag.localName.equals("complexContent") )	return reader.sfactory.complexContent(this,tag,decl);
		State s = reader.createModelGroupState(this,tag);
		if(s!=null)		return s;
		
		if( super.exp==null ) {
			// no content model was given.
			// I couldn't "decipher" what should we do in this case.
			// I assume "empty" just because it's most likely.
			exp = Expression.epsilon;
		}
		
		// TODO: attributes are prohibited after simpleContent/complexContent.
		
		// attribute, attributeGroup, and anyAttribtue can be specified
		// after content model is given.
		return reader.createAttributeState(this,tag);
	}
	
	protected Expression castExpression( Expression halfCastedExpression, Expression newChildExpression ) {
		if( halfCastedExpression==null )
			return newChildExpression;		// the first one
		
		// only the first one contains element.
		// the rest consists of attributes.
		// so this order of parameters is fine.
		return reader.pool.createSequence( newChildExpression, halfCastedExpression );
	}
																												   
	protected Expression defaultExpression() {
		// if no content model is given, then this complex type is ur-type.
		// TODO: confirm it.
		
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		reader.reportWarning( reader.WRN_IMPLICIT_URTYPE_FOR_COMPLEXTYPE, null );
		return reader.complexUrType;
	}
	
	protected Expression annealExpression(Expression contentType) {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
//		String targetNamespace = reader.grammar.targetNamespace;
		
		String abstract_ = startTag.getAttribute("abstract");
		if( "false".equals(abstract_) || abstract_==null )
			// allow content model to directly appear as this type.
			decl.exp = reader.pool.createChoice( decl.self, decl.exp );
		else
		if( !"true".equals(abstract_) )
			reader.reportError( reader.ERR_BAD_ATTRIBUTE_VALUE, "abstract", abstract_ );
			// recover by ignoring this error.
		
		// TODO: @block
		// TODO: @final
		
		String mixed = startTag.getAttribute("mixed");
		if( "true".equals(mixed) )
			contentType = reader.pool.createMixed(contentType);
		else
		if( mixed!=null && !"false".equals(mixed) )
			reader.reportError( reader.ERR_BAD_ATTRIBUTE_VALUE, "mixed", mixed );
			// recover by ignoring this error.

		decl.self.exp = contentType;

		if( isRedefine() ) {
			// copy new definition back into the original definition.
			oldDecl.redefine(decl);
			reader.setDeclaredLocationOf(oldDecl);
			return oldDecl;
		} else {
			reader.setDeclaredLocationOf(decl);
			return decl;
		}
	}
}
