/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.xmlschema;

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.TypeIncubator;
import com.sun.msv.datatype.xsd.QnameType;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.util.ExpressionWalker;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.DatatypeException;

/**
 * ComplexType definition.
 * 
 * ComplexTypeExp holds an expression (as a ReferenceExp) that matches to
 * this type itself and all substitutable types.
 * 
 * <p>
 * <code>self</code> contains the expression that exactly matches
 * to the declared content model (without any substitutable types).
 * 
 * <p>
 * <code>selfWType</code> field matches to " @xsi:type<'typeQName'>,self ".
 * This should be referenced from base type.
 * 
 * <p>
 * <code>extensions</code> field matches to selfWType of those types which
 * are derived by extention from this type.
 * 
 * <p>
 * <code>restrictions</code> field contains choices of selfWType of those
 * types which are derived by restriction from this type.
 * 
 * <h2>Complex Type Definition Schema Component Properties</h2>
 * <p>
 * This table shows the mapping between
 * <a href="http://www.w3.org/TR/xmlschema-1/#Complex_Type_Definition_details">
 * "complex type definition schema component properties"</a>
 * (which is defined in the spec) and corresponding method/field of this class.
 * 
 * <table border=1>
 *  <thead><tr>
 *   <td>Property of the spec</td>
 *   <td>method/field of this class</td>
 *  </tr></thead>
 *  <tbody><tr>
 *   <td>
 *    name
 *   </td><td>
 *    The {@link #name} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    target namespace
 *   </td><td>
 *    the {@link #getTargetNamespace} method.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    abstract
 *   </td><td>
 *    the {@link #isAbstract} method.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    base type definition
 *   </td><td>
 *    {@link #simpleBaseType} or {@link #complexBaseType} field,
 *    depending on whether the base type is a simple type or a complex type.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    derivation method
 *   </td><td>
 *    the {@link #derivationMethod} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    final
 *   </td><td>
 *    the {@link #finalValue} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    prohibited substitutions
 *   </td><td>
 *    the {@link #block} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    attribtue uses <br> attribute wildcard <br> content type
 *   </td><td>
 *    Not directly accessible. Can be found by walking
 *    the children of the {@link #self} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    annotation
 *   </td><td>
 *    Unaccessible. This information is removed during the parsing phase.
 *   </td>
 *  </tr></tbody>
 * </table>
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ComplexTypeExp extends RedefinableExp {
	
	public ComplexTypeExp( XMLSchemaSchema schema, String localName ) {
		super(localName);
		this.parent = schema;
		this.self = new ReferenceExp( localName + ":self" );
		this.extensions = new ReferenceExp( localName + ":extensions" );
		this.extensions.exp = Expression.nullSet;
		this.restrictions = new ReferenceExp( localName + ":restrictions" );
		this.restrictions.exp = Expression.nullSet;
		
		this.selfWType = schema.pool.createSequence(
							schema.pool.createAttribute(
								new ChoiceNameClass(
									new SimpleNameClass(
										schema.XMLSchemaInstanceNamespace,
										"type"),
									new SimpleNameClass(
										schema.XMLSchemaInstanceNamespace_old,
										"type")),
								schema.pool.createTypedString(
									getQNameType(schema.targetNamespace,localName)
								)), self );
		
		// self will be added later if this complex type turns out to be non-abstract.
		this.exp = schema.pool.createChoice( extensions, restrictions );
	}
	

	/**
	 * the pattern that represents content model of this type.
	 */
	public final ReferenceExp self;
	/**
	 * choices of the <code>selfWType</code> field of
	 * all complextypes which are derived by extension from this type.
	 */
	public final ReferenceExp extensions;
	/**
	 * choices of the <code>selfWType</code> field of
	 * all complextypes which are derived by restriction from this type.
	 */
	public final ReferenceExp restrictions;
	
	/**
	 * content model plus "xsi:type='typeName'" attribute.
	 * This pattern is used from the base type.
	 */
	public final Expression selfWType;
	
	/** parent XMLSchemaSchema object to which this object belongs. */
	public final XMLSchemaSchema parent;
	
//
// Schema component properties
//======================================
//
	
	/**
	 * gets the target namespace property of this component as
	 * <a href="http://www.w3.org/TR/xmlschema-1/#ct-target_namespace">
	 * specified in the spec</a>.
	 * 
	 * <p>
	 * If the property is <a href="http://www.w3.org/TR/xmlschema-1/#key-null">
	 * absent</a>, then this method returns the empty string.
	 * 
	 * <p>
	 * This method is just a shortcut for <code>parent.targetNamespace</code>.
	 */
	public final String getTargetNamespace() {
		return parent.targetNamespace;
	}
	
	/**
	 * base type of this complex type.
	 * 
	 * Either baseComplexType field or baseSimpleType field is set.
	 * If the base type is
	 * <a href="http://www.w3.org/TR/xmlschema-1/#section-Built-in-Complex-Type-Definition">
	 * ur-type</a>, both fields are set to null.
	 * 
	 * @see #simpleBaseType
	 */
	public ComplexTypeExp complexBaseType;
	/**
	 * base type of this complex type.
	 * 
	 * @see #complexBaseType
	 */
	public XSDatatype simpleBaseType;
	
	/**
	 * the derivation method used to derive this complex type from the base type.
	 * Either RESTRICTION or EXTENSION.
	 * 
	 * @see #complexBaseType
	 *		#simpleBaseType
	 */
	public int derivationMethod = -1;
	
	// actual values for these constants must keep in line with those values
	// defined in the ElementDeclExp.
	public static final int RESTRICTION	= 0x1;
	public static final int EXTENSION	= 0x2;
	

	/**
	 * checks if this complex type is abstract.
	 * 
	 * <p>
	 * This method corresponds to the abstract property of
	 * the complex type declaration schema component.
	 * 
	 * @return
	 *		true if this method is abstract. Flase if not.
	 */
	public boolean isAbstract() {
		// if it is abstract, then XSElementExp pointed by the self field
		// is not reachable from the exp field.
		final RuntimeException eureka = new RuntimeException();
		try {
			exp.visit( new ExpressionWalker() {
				public void onRef( ReferenceExp exp ) {
					if( exp==self )
						throw eureka;	// it's not abstract
					else
						return;
				}
			});
			// if the self field is not contained in the exp field,
			// it's abstract.
			return true;
		} catch( RuntimeException e ) {
			if( e == eureka )
				return false;
			throw e;
		}
	}
	
	/**
	 * Checks if this type is a derived type of the specified type.
	 * 
	 * <p>
	 * This method is an implementation of
	 * <a href="http://www.w3.org/TR/xmlschema-1/#cos-ct-derived-ok">
	 * "Type Derivation OK (Complex)"</a> test
	 * of the spec.
	 * 
	 * <p>
	 * If you are not familiar with the abovementioned part of the spec,
	 * <b>don't use this method</b>. This method probably won't give you
	 * what you expected.
	 * 
	 * @param	constraint
	 *		A bit field that represents the restricted derivation. This field
	 *		must consists of bitwise and of {@link #EXTENSION} or {@link #RESTRICTION}.
	 * 
	 * @return
	 *		true if the specified type is "validly derived" from this type.
	 *		false if not.
	 */
	public boolean isDerivedTypeOf( ComplexTypeExp baseType, int constraint ) {
		
		ComplexTypeExp derived = this;
		
		while( derived!=null ) {
			if( derived==baseType )		return true;
			
			if( (derived.derivationMethod&constraint)!=0 )
				return false;	// this type of derivation is prohibited.
			derived = derived.complexBaseType;
		}
		
		return false;
	}
	/**
	 * @see #isDerivedTypeOf(ComplexTypeExp,int)
	 */
	public boolean isDerivedTypeOf( XSDatatype baseType, int constraint ) {
		ComplexTypeExp derived = this;
		
		while(true) {
			if( derived.complexBaseType==null ) {
				if( derived.simpleBaseType!=null )
					return derived.simpleBaseType.isDerivedTypeOf(
						baseType, (constraint&RESTRICTION)==0 );
				else
					return false;
			}
			
			if( (derived.derivationMethod&constraint)!=0 )
				return false;	// this type of derivation is prohibited.
			
			derived = derived.complexBaseType;
		}
	}


	/**
	 * The <a href="http://www.w3.org/TR/xmlschema-1/#ct-final">
	 * final property</a> of this schema component, implemented as a bit field.
	 * 
	 * <p>
	 * 0, RESTRICTION, EXTENSION, or (RESTRICTION|EXTENSION).
	 */
	public int finalValue =0;
	
	/**
	 * The <a href="http://www.w3.org/TR/xmlschema-1/#ct-block">
	 * block property</a> of this schema component, implemented as a bit field.
	 * 
	 * <p>
	 * 0, RESTRICTION, EXTENSION, or (RESTRICTION|EXTENSION).
	 */
	public int block =0;
	
	
	
	
//
// Other implementation details
//======================================
//
	/** clone this object. */
	public RedefinableExp getClone() {
		ComplexTypeExp exp = new ComplexTypeExp(parent,super.name);
		exp.redefine(this);
		return exp;
	}

	public void redefine( RedefinableExp _rhs ) {
		super.redefine(_rhs);
		
		ComplexTypeExp rhs = (ComplexTypeExp)_rhs;
		self.exp = rhs.self.exp;
		extensions.exp = rhs.extensions.exp;
		restrictions.exp = rhs.restrictions.exp;
		if( this.parent != rhs.parent )
			// those two must share the parent.
			throw new IllegalArgumentException();
	}

	/** derives a QName type that only accepts this type name. */
	private static XSDatatype getQNameType( final String namespaceURI, final String localName ) {
		try {
			TypeIncubator ti = new TypeIncubator( QnameType.theInstance );
			ti.addFacet( "enumeration", "foo:"+localName, true,
				new ValidationContext() {
					public String resolveNamespacePrefix( String prefix ) {
						if( "foo".equals(prefix) )	return namespaceURI;
						return null;
					}
					public boolean isUnparsedEntity( String entityName ) {
						throw new Error();	// shall never be called.
					}
					public boolean isNotation( String notationName ) {
						throw new Error();	// shall never be called.
					}
				} );
		
			return ti.derive(null);
		} catch( DatatypeException e ) {
			// assertion failed. this can't happen.
			throw new Error();
		}
	}

	/**
	 * implementation detail.
	 * 
	 * A ComplexTypeDecl is properly defined if its self is defined.
	 * Note that the default implementation of the isDefined method doesn't 
	 * work for this class because the exp field is set by the constructor.
	 */
	public boolean isDefined() {
		return self.isDefined();
	}
}
