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
 * {@link #extensions} field matches to selfWType of those types which
 * are derived from this type by extension only.
 * {@link #restrictions} field contains choices of selfWType of those
 * types which are derived from this type by restriction only.
 * {@link #hybrids} field contains choices of selfWType of those types
 * which are derived from this by using both restriction and extension.
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
		this.self = new ReferenceExp( localName + ":self" );
		
		this.extensions = new ReferenceExp( localName + ":extensions" );
		this.extensions.exp = Expression.nullSet;
		this.extensionsSwitch = new OtherExp(this.extensions);
			
		this.restrictions = new ReferenceExp( localName + ":restrictions" );
		this.restrictions.exp = Expression.nullSet;
		this.restrictionsSwitch = new OtherExp(this.restrictions);
		
		this.hybrids = new ReferenceExp( localName + ":hybridDerivedTypes" );
		this.hybrids.exp = Expression.nullSet;
		this.hybridsSwitch = new OtherExp(this.hybrids);
		
		this.selfWType = new ReferenceExp( localName + ":type" );
		this.selfWType.exp = schema.pool.createSequence(
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
		
		// self will be added later after the abstract attribute
		// is examined.
		this.exp = schema.pool.createChoice(selfWType,
				schema.pool.createChoice(extensionsSwitch,
					schema.pool.createChoice(restrictionsSwitch,hybridsSwitch)));
	}
	

	/**
	 * the pattern that represents the content model of this type.
	 */
	public final ReferenceExp self;
	/**
	 * choices of the <code>selfWType</code> field of
	 * all complextypes which are derived only by extension from this type.
	 * @see #hybrids
	 */
	public final ReferenceExp extensions;
	public final OtherExp extensionsSwitch;
	
	/**
	 * choices of the <code>selfWType</code> field of
	 * all complextypes which are derived only by restriction from this type.
	 * @see #hybrids
	 */
	public final ReferenceExp restrictions;
	public final OtherExp restrictionsSwitch;
	
	/**
	 * choices of the <code>selfWType</code> field of
	 * all complextypes which are derived by using both restriction and extension
	 * from this type.
	 * 
	 * <p>
	 * For example, say that this ComplexTypeExp represents B, and B has the following
	 * type hierarchy.
	 * 
	 * <pre>
	 *         B
	 *        / \
	 *       /r  \e
	 *      /     \
	 *     Dr      De
	 *    /r \e   /r \e
	 *  Drr  Dre Der  Dee
	 * </pre>
	 * 
	 * Then
	 * the <code>restrictions</code> field contains Dr and Drr, 
	 * the <code>extensions</code> field contains De and Dee, and
	 * the <code>hybrids</code> field contains Dre and Der.
	 */
	public final ReferenceExp hybrids;
	public final OtherExp hybridsSwitch;
	
	
	/**
	 * content model plus "xsi:type='typeName'" attribute.
	 * This pattern is used from the base type.
	 */
	public final ReferenceExp selfWType;
	
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
