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

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.NameClass;
import com.sun.tranquilo.grammar.ReferenceExp;
import com.sun.tranquilo.grammar.ReferenceContainer;
import com.sun.tranquilo.grammar.xmlschema.XMLSchemaSchema;
import com.sun.tranquilo.reader.State;
import java.util.StringTokenizer;
import java.util.Iterator;

/**
 * used to parse &lt;anyAttribute &gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AnyAttributeState extends AnyState {

	protected Expression createExpression( final String namespace, final String process ) {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
		if( process.equals("skip") ) {
			// "skip" can be expanded now.
			NameClass nc = getNameClass(namespace);
			
			// TODO: make sure that <zeroOrMore> is correct semantics.
			return reader.pool.createZeroOrMore(
				reader.pool.createAttribute(nc) );
		}
		
		// "lax"/"strict" has to be back-patched later.
		final ReferenceExp exp = new ReferenceExp("anyAttribute("+process+":"+namespace+")");
		reader.addBackPatchJob( new XMLSchemaReader.BackPatch(){
			public State getOwnerState() { return AnyAttributeState.this; }
			public void patch() {
				
				if( !process.equals("lax")
				&&  !process.equals("strict") )  {
					reader.reportError( reader.ERR_BAD_ATTRIBUTE_VALUE, "processContents", process );
					exp.exp = Expression.nullSet;
					return;
				}
				
				exp.exp = Expression.nullSet;
				NameClass nc = getNameClass(namespace);
				Iterator itr = reader.grammar.schemata.values().iterator();
				while( itr.hasNext() ) {
					XMLSchemaSchema schema = (XMLSchemaSchema)itr.next();
					// nc is built by using NamespaceNameClass.
					// "strict" allows global element declarations of 
					// specified namespaces.
					if(nc.accepts( schema.targetNamespace, nc.LOCALNAME_WILDCARD )) {
							
						// gather global attributes.
						ReferenceExp[] atts = schema.attributeDecls.getAll();
						for( int i=0; i<atts.length; i++ )
							exp.exp = reader.pool.createSequence(
								reader.pool.createOptional(atts[i]),
								exp.exp );
					}
				}
				
				if( !process.equals("lax") )
					return;	// if processContents="strict", the above is fine.
				
				// if "lax", we have to add an expression to
				// match other attributes.
				NameClass laxNc = createLaxNameClass( nc,
					new XMLSchemaReader.RefResolver() {
						public ReferenceContainer get( XMLSchemaSchema schema ) {
							return schema.elementDecls;
						}
					});
				
				exp.exp = reader.pool.createSequence(
					reader.pool.createZeroOrMore(
						reader.pool.createAttribute( laxNc )
					),
					exp.exp );
			}
		});
		
		exp.exp = Expression.nullSet;	// dummy for a while.
		
		return exp;
	}
}
