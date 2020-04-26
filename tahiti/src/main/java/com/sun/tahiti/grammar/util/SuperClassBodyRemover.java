/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.grammar.util;

import com.sun.msv.grammar.*;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.tahiti.grammar.*;
import java.util.Set;

/**
 * removes ClassItem inside SuperClassItem as a preparation
 * of the marshaller generation.
 * 
 * <p>
 * Care has to be taken not to remove ClassItems directly referenced
 * from other part of the grammar. For example,
 * 
 * <PRE><XMP>
 * <start>
 *   <choice>
 *     <element name="derived" t:role="class">
 *       <ref name="body" t:role="superClass"/>
 *     </element>
 *     <element name="base">
 *       <ref name="body"/>
 *     </element>
 *   </choice>
 * </start>
 * 
 * <define name="body" t:role="class">
 *   <data type="string"/>
 * </define>
 * </XMP></PRE>
 * 
 * We can't simply remove ClassItem from "body". Intuitively, we are
 * to create the following pattern from the above pattern in this process.
 * 
 * <PRE><XMP>
 * <start>
 *   <choice>
 *     <element name="derived" t:role="class">
 *       <ref name="body1" t:role="superClass"/>
 *     </element>
 *     <element name="base">
 *       <ref name="body2"/>
 *     </element>
 *   </choice>
 * </start>
 * 
 * <define name="body1">
 *   <data type="string"/>
 * </define>
 * 
 * <define name="body2" t:role="class">
 *   <data type="string"/>
 * </define>
 * </XMP></PRE>
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SuperClassBodyRemover extends ExpressionCloner {
	
	private final Set visitedRefs = new java.util.HashSet();
	
	public static void remove( AnnotatedGrammar g ) {
		SuperClassBodyRemover su = new SuperClassBodyRemover(g.getPool());
		
		ClassItem[] cls = g.getClasses();
		for( int i=0; i<cls.length; i++ )
			cls[i].exp = cls[i].exp.visit(su);
	}
	
	public Expression onAttribute( AttributeExp exp ) {
		return pool.createAttribute( exp.nameClass, exp.exp.visit(this), exp.getDefaultValue());
	}
	
	public Expression onElement( ElementExp exp ) {
		if(visitedRefs.add(exp))
			exp.contentModel = exp.contentModel;
		return exp;
	}
	
	public Expression onRef( ReferenceExp exp ) {
		if(visitedRefs.add(exp))
			exp.exp = exp.exp.visit(this);
		return exp;		// recurse children if this is the first visit.
	}
	
	public Expression onOther( OtherExp exp ) {
		if( exp instanceof SuperClassItem ) {
			return exp.exp.visit(remover);
		}
		if(visitedRefs.add(exp))
			exp.exp=exp.exp.visit(this);
		return exp;
	}
	
	private ExpressionCloner remover;

	
	private SuperClassBodyRemover( ExpressionPool pool ) {
		super(pool);
		remover = new ExpressionCloner(pool){
			
			public Expression onRef( ReferenceExp exp ) {
				return exp.exp.visit(this);
			}
			
			public Expression onOther( OtherExp exp ) {
				if( exp instanceof ClassItem ) {
					// this is the definition of this super class item.
					// remove it.
					return exp.exp;
				}
				
				// it must not be a JavaItem.
				// this check should have already been done by the RelationNormalizer
				if( exp instanceof JavaItem )
					throw new Error("internal error");
				
				// other unknown exps
				return exp.exp.visit(this);
			}
			
			/*
			we have to copy ElementExp/AttributeExp if that is necessary.
			consider the following pattern:
			<start>
				<choice>
					<group t:role="class">
						<ref name="body" t:role="superClass"/>
						<element name="ext"/>
					</group>
					<ref name="body">
				</choice>
			</start>
			<define name="body">
				<element name="body">
					<group t:role="class" id="base">
						<data />
					</group>
				</element>
			</define>
			
			We cannot strip the ClassItem of the "base" correctly unless
			we copy the definition of the "body" elment.
			*/
            @Override
			public Expression onAttribute( AttributeExp exp ) {
				return super.pool.createAttribute(exp.nameClass,exp.exp.visit(this), exp.getDefaultValue());
			}
			
			public Expression onElement( ElementExp exp ) {
				Expression body = exp.contentModel.visit(this);
				if(body==exp.contentModel)	return exp;	// this item is not modified.
				else
					// body of this element is modified.
					// since this ElementExp might be shared,
					// we need to create a fresh ElementExp.
					return new ElementPattern(exp.getNameClass(),body);
			}
		};
	}
	
}
