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
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.trex.TypedString;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.xmlschema.ElementDeclExp;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.reader.State;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.ExpressionWithChildState;
import org.xml.sax.Locator;

/**
 * used to parse &lt;element &gt; element without ref attribute.
 * 
 * this state uses ExpressionWithChildState to collect content model
 * of this element declaration.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ElementDeclState extends ExpressionWithChildState {

	protected State createChildState( StartTagInfo tag ) {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
		// type declaration is allowed only when we haven't seen type definition.
		if( super.exp==null ) {
			if( tag.localName.equals("simpleType") )	return reader.sfactory.simpleType(this,tag);
			if( tag.localName.equals("complexType") )	return reader.sfactory.complexTypeDecl(this,tag);
		}
		// unique/key/keyref are ignored.
		if( tag.localName.equals("unique") )	return new IgnoreState();
		if( tag.localName.equals("key") )		return new IgnoreState();
		if( tag.localName.equals("keyref") )	return new IgnoreState();
		
		return null;
	}

	protected Expression initialExpression() {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
		// if <element> element has type attribute, then
		// it shall be used as content type.
		final String typeQName = startTag.getAttribute("type");
		if( typeQName==null )	return null;
		
		// TODO: shall I memorize this as a backward reference?
		// symbol may not be defined at this moment.
		// so just return an empty ReferenceExp and back-patch the actual definition later.
		final ReferenceExp ref = new ReferenceExp("elementType("+typeQName+")");
		
		reader.addBackPatchJob( new XMLSchemaReader.BackPatch(){
			public State getOwnerState() { return ElementDeclState.this; }
			public void patch() {
				String[] s = reader.splitQName(typeQName);
				if(s==null) {
					reader.reportError( reader.ERR_UNDECLARED_PREFIX, typeQName );
					ref.exp = Expression.nullSet;	// recover by setting a dummy definition.
					return;
				}
				
				Expression e;
				
				if( reader.isSchemaNamespace(s[0]) ) {
					// datatypes of XML Schema part 2
					e = reader.pool.createTypedString(reader.resolveBuiltinDataType(s[1]));
				} else {
					XMLSchemaSchema g = reader.getOrCreateSchema(s[0]/*uri*/);
					e = g.simpleTypes.get(s[1]/*local name*/);
					if(e==null)	e = g.complexTypes.get(s[1]);
					if(e==null ) {
						// both simpleType and complexType are undefined.
						reader.reportError( reader.ERR_UNDEFINED_ELEMENTTYPE, typeQName );
						e = Expression.nullSet;	// recover by dummy definition.
					}
				}
				ref.exp = e;
			}
		});
		
		return ref;
	}

	protected Expression castExpression( Expression halfCastedExpression, Expression newChildExpression ) {
		
		if( halfCastedExpression!=null )
			// assertion failed:
			// createChildState shouldn't allow parsing child <simpleType>
			// if one is already present.
			throw new Error();
		
		return newChildExpression;
	}
																												   
	protected Expression defaultExpression() {
		// if no content model is given, then this element type is ur-type.
		// TODO: confirm it.
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		reader.reportWarning( reader.WRN_IMPLICIT_URTYPE_FOR_ELEMENT, null );
		return reader.complexUrType;
	}
	
	protected Expression annealExpression(Expression contentType) {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
		String name = startTag.getAttribute("name");
		if( name==null ) {
			reader.reportError( reader.ERR_MISSING_ATTRIBUTE, "element", "name" );
			// recover by abandoning this element.
			return Expression.nullSet;
		}
		
		String targetNamespace;
		if( isGlobal() )
			// TODO: form attribute is prohibited at toplevel.
			targetNamespace = reader.currentSchema.targetNamespace;
		else
			// in local attribute declaration,
			// targetNamespace is affected by @form and schema's @attributeFormDefault.
			targetNamespace = ((XMLSchemaReader)reader).resolveNamespaceOfElementDecl(
				startTag.getAttribute("form") );
		
		// TODO: this doesn't work: super class signals an error
		// if contentType is null at this moment.
		if( contentType==null ) {
			// type attribute is not present, and
			// no simpleType/complexType is given as a child.
			
			if( startTag.containsAttribute("substitutionGroup") )
				reader.reportError( XMLSchemaReader.ERR_UNIMPLEMENTED_FEATURE,
					"omitting type attribute in <element> element with substitutionGroup attribute");
				// recover by assuming ur-type.
			
			// so it is assumed as "ur-type".
			// TODO: is this correct?
			/* it may be
				<zeroOrMore>
					<element>
						<anyName />
						....
			*/
			contentType = Expression.anyString;
		}
		
		String fixed = startTag.getAttribute("fixed");
		if( fixed!=null )
			// TODO: is this 'fixed' value should be added through enumeration facet?
			// TODO: check if content model is a simpleType.
			contentType = reader.pool.createTypedString( new TypedString(fixed,false) );
		
		String nillable = startTag.getAttribute("nillable");
		if( nillable!=null ) {
			if( !nillable.equals("true") )
				reader.reportError( XMLSchemaReader.ERR_BAD_ATTRIBUTE_VALUE, "nillable", nillable );
				// recovery by assuming "true".
			
			// allow nil expression as well as original content model.
			contentType = reader.pool.createChoice(
				contentType, reader.nilExpression );
		}

		// TODO: abstract is prohibited in local declarations.
		String abstract_ = startTag.getAttribute("abstract");
		if( "true".equals(abstract_) )
			// prohibit this element declaration from appearing.
			contentType = Expression.nullSet;
		
		// allow xsi:schemaLocation and xsi:noNamespaceSchemaLocation
		contentType = reader.pool.createSequence( reader.xsiSchemaLocationExp, contentType );
		
		ElementPattern exp = new ElementPattern(
			new SimpleNameClass(targetNamespace,name), contentType );
		
		
		if( !isGlobal() )
			// minOccurs/maxOccurs is processed through interception
			return exp;
		
		
		// register this as global element declaration
		ElementDeclExp decl = reader.currentSchema.elementDecls.getOrCreate(name);
		if( decl.self!=null )
			reader.reportError( 
				new Locator[]{this.location,reader.getDeclaredLocationOf(decl)},
				reader.ERR_DUPLICATE_ELEMENT_DEFINITION,
				new Object[]{name} );
		
		decl.self = exp;
		decl.exp = reader.pool.createChoice( decl.exp, decl.self );
		reader.setDeclaredLocationOf(decl);
			
		String substitutionGroupQName = startTag.getAttribute("substitutionGroup");
		if( substitutionGroupQName!=null ) {
			String[] r = reader.splitQName(substitutionGroupQName);
			if(r==null) {
				reader.reportError( reader.ERR_UNDECLARED_PREFIX, substitutionGroupQName );
				// recover by ignoring substitutionGroup.
			} else {
				// register this declaration to the head elementDecl.
				ElementDeclExp head = reader.getOrCreateSchema(r[0]/*uri*/).
					elementDecls.getOrCreate(r[1]/*local name*/);
					
				// TODO: where to insert it?
				head.exp = reader.pool.createChoice( head.exp, decl );
			}
		}
			
		// TODO: @block
		// TODO: @final
		return decl;
	}

	protected boolean isGlobal() {
		return parentState instanceof GlobalDeclState;
	}
}
