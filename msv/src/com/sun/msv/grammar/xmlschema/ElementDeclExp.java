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

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.util.ExpressionWalker;
import java.util.Vector;

/**
 * Global Element declaration.
 * 
 * <p>
 * the inherited exp field holds an expression that
 * also matches to substituted element declarations.
 * 
 * <p>
 * <code>self</code> field contains an expression that matches
 * only to this element declaration without no substituted element decls.
 * 
 * <p>
 * This object is not created for local element declaration.
 * 
 * <h2>Element Declaration Schema Component Properties</h2>
 * <p>
 * This table shows the mapping between
 * <a href="http://www.w3.org/TR/xmlschema-1/#Element_Declaration_details">
 * "element declaration schema component properties"</a>
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
 *    type definition
 *   </td><td>
 *    <b>To be implemented</b>. Accessible through the {@link self} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    scope
 *   </td><td>
 *    Always global. A local element declaration does not have the corresponding
 *    ElementDeclExp object.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    value constraint
 *   </td><td>
 *    <b>To be implemented</b>.  Accessible through the {@link self} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    nillable
 *   </td><td>
 *    <b>To be implemented</b>. Accessible through the {@link self} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    identity constraints
 *   </td><td>
 *    The <code>identityConstraints</code> field of the {@link XSElementExp},
 *	  which in turn can be obtained throught the {@link self} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    substitution group affiliation
 *   </td><td>
 *    The {@link #substitutionAffiliation} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    substitution group exclusion
 *   </td><td>
 *    The {@link #finalValue} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    disallowed substitution
 *   </td><td>
 *    The {@link #block} field.
 *   </td>
 *  </tr><tr>
 *   <td>
 *    abstract
 *   </td><td>
 *    the {@link #isAbstract} method.
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
public class ElementDeclExp extends ReferenceExp
{
	public ElementDeclExp( XMLSchemaSchema schema, String typeLocalName ) {
		super(typeLocalName);
		this.parent = schema;
		this.exp = Expression.nullSet;
	}

	/**
	 * <a href="http://www.w3.org/TR/xmlschema-1/#class_exemplar">
	 * The substitution group affiliation property</a>
	 * of this component, if any.
	 * Otherwise null.
	 */
	public ElementDeclExp substitutionAffiliation;
	
	/**
	 * those who set the value to this field is also responsible to
	 * add self into this#exp.
	 */
	public XSElementExp self;
	
	/**
	 * gets the pattern that represents the content model of
	 * this element declaration.
	 * 
	 * This method is just a short cut for <code>self.contentModel</code>.
	 */
	public Expression getContentModel() {
		return self.contentModel;
	}
	
	/** parent XMLSchemaSchema object to which this object belongs. */
	public final XMLSchemaSchema parent;
	
	
	/**
	 * XML Schema version of {@link ElementExp}.
	 * 
	 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
	 */
	public static class XSElementExp extends ElementExp {
		public final SimpleNameClass elementName;
		public final NameClass getNameClass() { return elementName; }
		
		public XSElementExp( SimpleNameClass elementName, Expression contentModel ) {
			super(contentModel,false);
			this.elementName = elementName;
		}
		
		/**
		 * identity constraints associated to this declaration.
		 * When no constraint exists, this field may be null (or empty vector).
		 * Items are of derived types of {@link IdentityConstraint} class.
		 */
		public final Vector identityConstraints = new Vector();
		
		/**
		 * owner ElementDeclExp. If this element declaration is a global one,
		 * then this field is non-null. If this is a local one, then this field
		 * is null.
		 */
		public ElementDeclExp parent;
	}

	
	
	
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
	 * checks if this element declaration is abstract.
	 * 
	 * @return
	 *		true if this method is abstract.
	 */
	public boolean isAbstract() {
		// if it is abstract, then XSElementExp pointed by the self field
		// is not reachable from the exp field.
		final RuntimeException eureka = new RuntimeException();
		try {
			exp.visit( new ExpressionWalker() {
				public void onElement( ElementExp exp ) {
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

	
	public static final int RESTRICTION	= 0x1;
	public static final int EXTENSION	= 0x2;
	public static final int SUBSTITUTION= 0x4;

	/**
	 * The <a href="http://www.w3.org/TR/xmlschema-1/#e-final">
	 * substitution group exclusions property</a> of this schema component,
	 * implemented as a bit field.
	 * 
	 * <p>
	 * a bit-wise OR of RESTRICTION and EXTENSION.
	 */
	public int finalValue =0;
	
	/**
	 * The <a href="http://www.w3.org/TR/xmlschema-1/#e-exact">
	 * disallowed substitution property</a> of this schema component,
	 * implemented as a bit field.
	 * 
	 * <p>
	 * a bit-wise OR of RESTRICTION, EXTENSION, and SUBSTITUTION.
	 */
	public int block =0;
	
	
	
//
// Implementation details
//=========================================
	public boolean isDefined() {
		return self!=null;
	}
	
}
