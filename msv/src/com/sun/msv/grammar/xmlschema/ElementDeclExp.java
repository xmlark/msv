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
 * Element declaration.
 * 
 * the inherited exp field holds an expression that
 * also matches to substituted element declarations.
 * 
 * <code>self</code> field contains an expression that matches
 * only to this element declaration without no substituted element decls.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ElementDeclExp extends ReferenceExp
{
	public ElementDeclExp( String typeLocalName ) {
		super(typeLocalName);
		this.exp = Expression.nullSet;
	}

	public boolean isDefined() {
		return self!=null;
	}
	
	/**
	 * The substitution group affiliation of this declaration, if any.
	 * Otherwise null.
	 * 
	 * This field may not be modified.
	 * 
	 * @see http://www.w3.org/TR/xmlschema-1/#class_exemplar
	 */
	public ElementDeclExp substitutionAffiliation;
	
	/**
	 * those who set the value to this field is also responsible to
	 * add self into this#exp.
	 */
	public XSElementExp self;
	
	/**
	 * gets the content model of this element declaration. It could be
	 * a ComplexTypeExp, or something else.
	 * 
	 * This method is just a short cut for <code>self.contentModel</code>.
	 */
	public Expression getContentModel() {
		return self.contentModel;
	}
	
	
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
		 */
		public final Vector identityConstraints = new Vector();
		
		/**
		 * owner ElementDeclExp. If this element declaration is a global one,
		 * then this field is non-null. If this is a local one, then this field
		 * is null.
		 */
		public ElementDeclExp parent;
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
}
