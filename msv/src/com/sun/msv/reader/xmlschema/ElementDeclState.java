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
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.trex.TypedString;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.xmlschema.ElementDeclExp;
import com.sun.msv.grammar.xmlschema.ComplexTypeExp;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.grammar.xmlschema.IdentityConstraint;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.State;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.ExpressionWithChildState;
import org.xml.sax.Locator;
import java.util.StringTokenizer;
import java.util.Vector;

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
		if( tag.localName.equals("unique") )	return reader.sfactory.unique(this,tag);
		if( tag.localName.equals("key") )		return reader.sfactory.key(this,tag);
		if( tag.localName.equals("keyref") )	return reader.sfactory.keyref(this,tag);
		
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
		
		final String[] s = reader.splitQName(typeQName);
		if(s==null) {
			reader.reportError( reader.ERR_UNDECLARED_PREFIX, typeQName );
			ref.exp = Expression.nullSet;	// recover by setting a dummy definition.
			return ref;
		}
		
		reader.addBackPatchJob( new GrammarReader.BackPatch(){
			public State getOwnerState() { return ElementDeclState.this; }
			public void patch() {
				
				Expression e;
				
				if( reader.isSchemaNamespace(s[0]) ) {
					// datatypes of XML Schema part 2
					e = reader.pool.createTypedString(reader.resolveBuiltinDataType(s[1]),s[1]);
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
		if( startTag.containsAttribute("substitutionGroup") )
			reader.reportError( XMLSchemaReader.ERR_UNIMPLEMENTED_FEATURE,
				"omitting type attribute in <element> element with substitutionGroup attribute");
			// recover by assuming ur-type.
			
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
/*		
		String block = startTag.getDefaultedAttribute("block", reader.blockDefault );
		if( block!=null ) {
			StringTokenizer tokens = new StringTokenizer(block);
			boolean blockExtension = false;
			boolean blockRestriction = false;
			boolean blockSubstitution = false;
			
			while( tokens.hasMoreTokens() ) {
				String token = tokens.nextToken();
				if( token.equals("#all") )
					blockExtension = blockRestriction = blockSubstitution = true;
				if( token.equals("extension") )
					blockExtension = true;
				if( token.equals("restriction") )
					blockRestriction = true;
				if( token.equals("substitution") )
					blockSubstitution = true;
			}
		}
*/		
		String targetNamespace;
		if( isGlobal() )
			// TODO: form attribute is prohibited at toplevel.
			targetNamespace = reader.currentSchema.targetNamespace;
		else
			// in local attribute declaration,
			// targetNamespace is affected by @form and schema's @attributeFormDefault.
			targetNamespace = ((XMLSchemaReader)reader).resolveNamespaceOfElementDecl(
				startTag.getAttribute("form") );
		
		String fixed = startTag.getAttribute("fixed");
		if( fixed!=null )
			// TODO: is this 'fixed' value should be added through enumeration facet?
			// TODO: check if content model is a simpleType.
			contentType = reader.pool.createTypedString( new TypedString(fixed,false),"" );
		
		String nillable = startTag.getAttribute("nillable");
		if( nillable!=null ) {
			if( !nillable.equals("true") )
				reader.reportError( XMLSchemaReader.ERR_BAD_ATTRIBUTE_VALUE, "nillable", nillable );
				// recovery by assuming "true".
			
			// allow nil expression as well as original content model.
			contentType = reader.pool.createChoice(
				contentType, reader.nilExpression );
		}

		// allow xsi:schemaLocation and xsi:noNamespaceSchemaLocation
		contentType = reader.pool.createSequence( reader.xsiSchemaLocationExp, contentType );
		
		ElementDeclExp.XSElementExp exp = new ElementDeclExp.XSElementExp(
			new SimpleNameClass(targetNamespace,name), contentType );
		
		// set identity constraints
		exp.identityConstraints.addAll(idcs);
		
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
		exp.parent = decl;

		String abstract_ = startTag.getAttribute("abstract");
		if( !"true".equals(abstract_) )
			// prohibit this element declaration from appearing.
			// by not adding self as a member of choice.
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
				
				decl.substitutionAffiliation = head;
					
				// TODO: where to insert it?
				head.exp = reader.pool.createChoice( head.exp, decl );
			}
		}
			
		// TODO: @block
		if( startTag.containsAttribute("block") )
			reader.reportWarning( reader.ERR_UNIMPLEMENTED_FEATURE,
				"block attribute for <element>" );
		// TODO: @final
		if( startTag.containsAttribute("final") )
			reader.reportWarning( reader.ERR_UNIMPLEMENTED_FEATURE,
				"final attribute for <element>" );
		
		return decl;
	}

	protected boolean isGlobal() {
		return parentState instanceof GlobalDeclState;
	}

	
	/** identity constraints found in this element. */
	protected final Vector idcs = new Vector();
		
	/** this method is called when an identity constraint declaration is found.
	 */
	protected void onIdentityConstraint( IdentityConstraint idc ) {
		idcs.add(idc);
	}
}
