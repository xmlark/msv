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
import com.sun.msv.grammar.OtherExp;
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
 * this type itself.
 * 
 * <p>
 * the {@link #body} field contains the expression that exactly matches
 * to the declared content model (without any substitutable types).
 * 
 * <p>
 * the <code>exp</code> field contains the reference to the body field,
 * if this complex type is not abstract. If abstract, then nullSet is set.
 * You shouldn't directly manipulate the exp field. Instead, you should use
 * the {@link #setAbstract} method to do it.
 * 
 * <p>
 * Note: The runtime type substitution
 * (the use of <code>xsi:type</code> attribute)
 * is implemented at the VGM layer. Therefore, AGMs of XML Schema does <b>NOT</b>
 * precisely represent what are actually allowed and what are not.
 * 
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
 *    the children of the {@link #body} field.
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
 * 
 * 
 * <h2>ComplexTypeExp anatomy</h2>
 * <p>
 * A ComplexTypeExp is composed roughly as in the following figure:
 * <img src="doc-files/ComplexTypeExp.png">
 * <p>
 * A box indicates an instance of ReferenceExp. As shown in this figure,
 * {@link #exp} field is the choice of all subfields by default.
 * A ComplexTypeExp is constructed in this way when it's not abstract and 
 * no block attribute was specified. The type can appear by itself (in this case
 * it'll match "self" pattern), or can be substituted by itself (in this case,
 * "selfWType" pattern), or can be substituted by any of the derived types (three
 * other patterns).
 * <p>
 * <font color="red">(1)</font> through <font color="red">(4)</font> are the marks
 * that are used later.
 * 
 * <h3>derivation by restriction</h3>
 * <p>
 * When a complex type D is derived from B by restriction, two ComplexTypeExps
 * are connected as follows:
 * <img src="doc-files/ComplexTypeDeriveByRestriction.png">
 * 
 * <p>
 * What is done here is to register the derived type D (and types derived from D)
 * to the base type so that they can correctly substitute the base type (and
 * ancestor types of B).
 * 
 * <p>
 * In this way, invariants of {@link #restrictions},{@link #extensions}, and {@link #hybrids}
 * are preserved.
 * 
 * <h3>derivation by extension</h3>
 * <p>
 * Derivation by extension is almost equal to the derivation by restriction.
 * The only difference is that the content model of the derived type uses the self
 * field of the base type.
 * <img src="doc-files/ComplexTypeDeriveByExtension.png">
 * 
 * 
 * 
 * <h3>abstract="true"</h3>
 * <p>
 * When the complex type is abstract, <font color="red">(4)</font> is cut.
 * This makes the "self" field unreachable from the "exp" field, and also makes
 * the "selfWType" field unusable (by setting nullSet to its exp field).
 * Thus achieves the effect of abstract, which
 * prevents the type definition from appearing in instance.
 * 
 * <p>
 * Note that the self field can be still referenced from the derived types
 * (as we see in the "derivation by extension" section),
 * and those derived types can appear in the document.
 * Therefore, we cannot set nullSet to the self field. Instead, we need to
 * cut <font color="red">(4)</font> so that it cannot be reached from the exp field
 * of this ComplexTypeExp.
 * 
 * 
 * 
 * <h3>block="***"</h3>
 * <p>
 * The block attribute is arbitrary combination of "restriction" or "extension".
 * If "restriction" is specified,
 * <font color="red">(2)</font> and <font color="red">(3)</font>
 * are cut. In this way, any derived type that uses restriction becomes unreachable
 * from the exp field of the ComplexTypeExp.
 * 
 * <p>
 * The point of cut is very carefully designed.
 * According to
 * <a href="http://www.w3.org/TR/xmlschema-1/#cvc-elt">
 * "Validation Rule: Element Locally Valid (Element)"</a> clause 4.3, A derived type
 * which uses "restriction" in the derivation chain cannot substitute this type.
 * Therefore, if type B has block="restriction", and De is derived from B by extension,
 * and Der is derived from De by restriction, then Der cannot substitute B.
 * 
 * <p>
 * However, things are not that easy. Now say B has the base complex type R.
 * According to <a href="http://www.w3.org/TR/xmlschema-1/#cos-ct-derived-ok">
 * "Type Derivation OK (Complex)"</a>, even if block="restriction" is specified
 * for type B, R can be still substitutable by De and Der.
 * 
 * <p>
 * As you see, cutting <font color="red">(1,2,3)</font> still allow R to be
 * substituted by De or Der.
 * 
 * <p>
 * By the similar reasoning, when "extension" is specified in the block attribute,
 * <font color="red">(1)</font> and <font color="red">(3)</font> are cut.
 * 
 * If both "extension" and "restriction" are specified, then 
 * <font color="red">(1)</font>, <font color="red">(2)</font>, and
 * <font color="red">(3)</font> are cut.
 * 
 * <p>
 * The author belives that the above design would correctly express
 * the complicated constraint of complex types.
 * 
 * 
 * 
 * <h3>Interaction with ElementDeclExp</h3>
 * 
 * <p>
 * Due to the way the block attribute of &lt;xsd:element> works, ElementDeclExp
 * and ComplexTypeExp have a rather complex interaction (sigh). See javadoc of
 * {@link ElementDeclExp} for details.
 * 
 * @see		ElementDeclExp
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ComplexTypeExp extends XMLSchemaTypeExp {
	
	public ComplexTypeExp( XMLSchemaSchema schema, String localName ) {
		super(localName);
		this.parent = schema;
		this.body = new ReferenceExp(null,null);
		this.exp = this.body;
	}
	
	
	/**
	 * actual content model definition.
	 */
	public final ReferenceExp body;
	
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
		return exp==Expression.nullSet;
	}
	public void setAbstract( boolean isAbstract ) {
		if( isAbstract )		exp=Expression.nullSet;
		else					exp=body;
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
	public boolean isDerivedTypeOf( XMLSchemaTypeExp exp, int constraint ) {
		if( exp instanceof ComplexTypeExp )
			return isDerivedTypeOf( (ComplexTypeExp)exp, constraint );
		else
			return isDerivedTypeOf( ((SimpleTypeExp)exp).getType(), constraint );
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
	
	/**
	 * gets the value of the block constraint.
	 * SimpleTypeExp always returns 0 because it doesn't have the block constraint.
	 */
	public int getBlock() { return block; }

	
	
	
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
		body.exp = rhs.body.exp;
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
		return body.isDefined();
	}
}
