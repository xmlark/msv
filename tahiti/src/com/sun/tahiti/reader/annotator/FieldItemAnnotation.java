/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.reader.annotator;

import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.grammar.*;
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.tahiti.grammar.*;
import com.sun.tahiti.grammar.util.ClassCollector;
import com.sun.tahiti.grammar.util.Multiplicity;
import com.sun.tahiti.grammar.util.MultiplicityCounter;
import java.util.Set;
import java.util.Map;
import java.util.Stack;
import java.util.Iterator;

/**
 * adds missing FieldItems to the grammar.
 * 
 * This algorithm expands C-C, C-I, and C-P relationships to
 * C-F/F-C, C-F/F-I, and C-F/F-P relationships.
 */
class FieldItemAnnotation
{
	public static Expression annotate( Expression exp, ExpressionPool pool ) {
		ClassCollector col = new ClassCollector();
		exp.visit(col);
		
		FieldItemAnnotation ann = new FieldItemAnnotation();
		
		// process all class items.
		ClassItem[] classes = (ClassItem[])col.classItems.toArray(new ClassItem[0]);
		for( int i=0; i<classes.length; i++ )
			classes[i].exp = classes[i].exp.visit(ann.new Annotator(pool,classes[i]));
		
		return exp;
	}
	
	private FieldItemAnnotation() {}
	
	/**
	 * this map will keep the annotated ReferenceExps. ReferenceExp is the key
	 * and annotated result is the value. This map is very important to minimize
	 * the number of generated classes/interfaces.
	 */
	private final Map annotatedRefs = new java.util.HashMap();

	private class Annotator extends ExpressionCloner {
		
		private Annotator( ExpressionPool pool, ClassItem owner ) {
			super(pool);
			this.owner = owner;
			names.push("value");	// if no other name is available, this name will be used.
		}
		
		/**
		 * the top of this stack is the name of the nearest enclosing named item.
		 * 
		 * This information is used to determine the name of newly added FieldItem.
		 */
		private final Stack names = new Stack();
		
		private final ClassItem owner;
		
		/**
		 * A counter. this value is used to create unique names for added class items.
		 */
		private int iota = 0;
		
		public Expression onRef( ReferenceExp exp ) {
			// expands C-C,C-P,C-I relationship.
			if( exp instanceof PrimitiveItem
			||  exp instanceof InterfaceItem
			||  exp instanceof ClassItem )
				return new FieldItem(
					typeNameToFieldName(((Type)exp).getBareName()),
					exp );
			
			if( exp instanceof IgnoreItem
			||  exp instanceof SuperClassItem
			||	exp instanceof FieldItem )
				return exp;
			
			// otherwise this is a normal ReferenceExp
			assert(!(exp instanceof JavaItem));
			
			Expression r = (Expression)annotatedRefs.get(exp);
			if(r!=null)		return r;	// this ReferenceExp has already visited and annotated. reuse it.
			
			// store the name information
			boolean pushed = false;
			if(exp.name!=null) {
				names.push(exp.name);
				pushed = true;
			}
			r = exp.exp.visit(this);
			annotatedRefs.put(exp,r);	// store the annotated result.
			
			if(pushed)	names.pop();
			return r;
		}

		public Expression onAttribute( AttributeExp exp ) {
			Expression body = exp.exp.visit(this);
			if( body==exp.exp )	return exp;
			else	return pool.createAttribute( exp.nameClass, body );
		}
	
		public Expression onElement( ElementExp exp ) {
			/*
			although we will lose any additional information
			added to this ElementExp,
			we have to create a copy of ElementExp. Otherwise
			we cannot correctly process things like:
			
			<define name="X">
				<ref name="Z" t:role="class"/>
			</define>
			<define name="Y">
				<ref name="Z" t:role="field"/>
			</define>
			<define name="Z">
				<element name="foo">
					<data type="string" t:role="primitive"/>
				</element>
			</define>
			
			When processing X->Z, we want to add extra FieldItem.
			When processing Y->Z, we don't want that.
			*/
			
			Expression body = exp.contentModel.visit(this);
			if(body==exp.contentModel)	return exp;
			else	return new ElementPattern( exp.getNameClass(), body );
		}

		public Expression onChoice( ChoiceExp exp ) {
			// check whether there is only one meaningul branch, or more than one of them.
			Expression[] b = exp.getChildren();
			boolean[] complexBranch = new boolean[b.length];
			boolean[] fieldlessBranch = new boolean[b.length];
			int numLiveBranch = 0;
			
			for( int i=0; i<b.length; i++ ) {
				final boolean[] hasChildFieldItem = new boolean[1];
				
				// compute the multiplicity of the all child JavaItems and 
				// also compute whether this branch has FieldItem in it.
				Multiplicity m = Multiplicity.calc( b[i],
					new MultiplicityCounter(){
						protected Multiplicity isChild( Expression exp ) {
							if(exp instanceof FieldItem)	hasChildFieldItem[0] = true;
							
							if(exp instanceof IgnoreItem)	return Multiplicity.zero;
							if(exp instanceof JavaItem)		return Multiplicity.one;
							else						return null;
						}
					});
				
				if(m.isZero())
					continue;		// do nothing for this branch.
				
				numLiveBranch++;
				
				if(!m.isAtMostOnce()) {
					// memorize that this branch is complex.
					complexBranch[i] = true;
					continue;
				}
				
				if( !hasChildFieldItem[0] ) {
					// memorize that this branch is fieldless.
					fieldlessBranch[i] = true;
					continue;
				}
				
				// this branch has a FieldItem. perform recursion.
				b[i] = b[i].visit(this);
			}
			
			if( numLiveBranch<=1 ) {
				// there are only one meaningful branch.
				// this happens for patterns like <optional>.
				
				// visit all unvisited branch
				for( int i=0; i<b.length; i++ )
					if( fieldlessBranch[i] || complexBranch[i] )
						b[i] = b[i].visit(this);
			} else {
				String fieldName = decideName();
				
				for( int i=0; i<b.length; i++ ) {
					if( fieldlessBranch[i] )
						// do not perform recursion because we've added FieldItem.
						b[i] = new FieldItem( fieldName, b[i] );
					if( complexBranch[i] ) {
						// insert a new class item here.
						String className = owner.getTypeName()+"Subordinate"+(++iota);
						
						b[i] = new FieldItem( fieldName,
							new ClassItem( className, b[i].visit(this) ) );
					}
				}
			}
			
			Expression r = Expression.nullSet;
			for( int i=b.length-1; i>=0; i-- )
				r = pool.createChoice( b[i], r );
			return r;
		}
	
		
		private RuntimeException eureka = new RuntimeException();
		
		public Expression onMixed( MixedExp exp ) {
			
			/*
			expand this <mixed> as
			<interleave>
				<zeroOrMore>
					<primitiveItem>
						<data type="string"/>
					</primitiveItem>
				</zeroOrMore>
				exp.exp
			</interleave>
			*/
			Expression expanded = pool.createInterleave(
					pool.createZeroOrMore(
						new PrimitiveItem( String.class,
							pool.createTypedString(StringType.theInstance) )),
					exp.exp );
			
			try {
				// see if "exp" contains FieldItem.
				exp.exp.visit( new ExpressionWalker(){
					public void onRef( ReferenceExp exp ) {
						if(exp instanceof FieldItem)	throw eureka;
						if(exp instanceof JavaItem)		return;
						super.onRef(exp);
					}
				});
			} catch(RuntimeException e) {
				// if there is a field item in it,
				// then visit "expanded".
				// typically this will loose the order between characters and 
				// elements, but this seems to be a reasonable compromise.
				assert(e==eureka);
				return expanded.visit(this);
			}
			
			/*
			if there is no FieldItem in it, treat everything as a big sequence
			by enclosing everything into one big FieldItem.
			*/
			return new FieldItem( decideName(), expanded );
		}
		
		/**
		 * decides a name to be used as the field name.
		 * 
		 * Use the name at the stack top. As a side effect,
		 * this method modifies the stack top so that fields that
		 * are added later will have different names.
		 */
		private String decideName() {

			final String name = (String)names.pop();
			
			// the value of name must be the form of "xxxxxNNN" where 
			// NNN is digits.
			int idx = name.length()-1;
			while( Character.isDigit(name.charAt(idx)) && idx>=0 )	idx--;
			
			names.push( name.substring(0,idx)+(Integer.parseInt(name.substring(idx))+1) );
			
			return name;
		}
	}


	/**
	 * generates a field name suitable to hold a reference for the specified class.
	 */
	private static String typeNameToFieldName( String bareName ) {
		return Character.toLowerCase(bareName.charAt(0))+bareName.substring(1);
	}
		
	private static void assert( boolean b ) {
		if(!b)	throw new Error();
	}
}
