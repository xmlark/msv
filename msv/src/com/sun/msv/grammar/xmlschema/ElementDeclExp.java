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
import java.util.Vector;

/**
 * Element declaration.
 * 
 * ElementDeclExp as a ReferenceExp holds an expression that
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
	 * those who set the value to this field is also responsible to
	 * add self into this#exp.
	 */
	public XSElementExp self;
	
	/**
	 * XML Schema version of {@link ElementExp}.
	 * 
	 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
	 */
	public static class XSElementExp extends ElementExp {
		public final SimpleNameClass nameClass;
		public final NameClass getNameClass() { return nameClass; }
		
		public XSElementExp( SimpleNameClass nameClass, Expression contentModel ) {
			super(contentModel,false);
			this.nameClass = nameClass;
		}
		
		/**
		 * identity constraints associated to this declaration.
		 * When no constraint exists, this field may be null.
		 */
		public final Vector identityConstraints = new Vector();
	}
}
