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
import com.sun.msv.grammar.util.ExpressionPrinter;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.xmlschema.SimpleTypeExp;
import com.sun.tahiti.grammar.*;
import com.sun.tahiti.grammar.util.Multiplicity;
import com.sun.tahiti.grammar.util.MultiplicityCounter;
import com.sun.tahiti.reader.NameUtil;
import java.util.Set;
import java.util.Map;
import java.util.Stack;
import java.util.Iterator;

/**
 * annotates &lt;choice&gt; with ClassItem/InterfaceItem
 * so that it can be handled easily.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
class ChoiceAnnotator
{
	private static java.io.PrintStream debug = null;
	
	public static void annotate( AnnotatedGrammar g ) {
		
		ChoiceAnnotator ann = new ChoiceAnnotator(g);
		
		// process all class items.
		ClassItem[] classes = g.getClasses();
		for( int i=0; i<classes.length; i++ )
			classes[i].exp = classes[i].exp.visit(ann.new Annotator(classes[i]));
	}
	
	private ChoiceAnnotator( AnnotatedGrammar g ) {
		this.grammar = g;
	}
	
	/**
	 * this map will keep the annotated ReferenceExps. ReferenceExp is the key
	 * and annotated result is the value. This map is very important to minimize
	 * the number of generated classes/interfaces.
	 */
	private final Map annotatedRefs = new java.util.HashMap();

	/** the grammar object to which we are adding annotation. */
	private final AnnotatedGrammar grammar;
	
	/**
	 * Annotate the body of one ClassItem.
	 */
	private class Annotator extends ExpressionCloner {
		
		private Annotator( ClassItem owner ) {
			super(grammar.getPool());
			this.owner = owner;
		}
		
		/** the current ClassItem object. Its body is what we are dealing with now. */
		private final ClassItem owner;
		
		/**
		 * A counter. this value is used to create unique names for added class items.
		 */
		private int iota = 0;
		
		public Expression onRef( ReferenceExp exp ) {
			
			// If this ReferenceExp has already visited and annotated. reuse it.
			// This will prevent annotating the same ChoiceExp, etc again and again.
			Expression r = (Expression)annotatedRefs.get(exp);
			if(r!=null)			return r;

			return exp.exp.visit(this);
		}
		
		public Expression onOther( OtherExp exp ) {
			// expands C-C,C-P,C-I relationship.
			if( exp instanceof PrimitiveItem
			||  exp instanceof InterfaceItem
			||  exp instanceof ClassItem
			||  exp instanceof IgnoreItem
			||  exp instanceof SuperClassItem
			||	exp instanceof FieldItem )
				return exp;
			
			return exp.exp.visit(this);
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

		/**
		 * annotate ChoiceExp with FieldItem.
		 * 
		 * <p>
		 * children of a ChoiceExp is called "branches". In this method,
		 * branches of the entire choice group is considered.
		 * 
		 * <p>
		 * We consider a branch is "alive" if there is some JavaItem in that branch.
		 * For example, &lt;empty&gt; is not a live branch. IgnoreItem is another
		 * example of non-live branch.
		 * 
		 * <p>
		 * If only one branch out of the entire branches is alive, then this choice
		 * is not treated at all, and the live branch is recursively processed.
		 * This handles &lt;optional> p &lt;/optional>.
		 * 
		 * <p>
		 * Otherwise, the following algorithm is applied:
		 * 
		 * <p>
		 * A branch is said to be "complex", if the multiplicity of child JavaItem is
		 * more than one. For example,
		 * 
		 * <PRE><XMP>
		 * <oneOrMore>
		 *   <tahiti:classItem>
		 *     <element name="..."/>
		 *       ...
		 *     </element>
		 *   </tahiti:classItem>
		 * </oneOrMore>
		 * </XMP></PRE>
		 * 
		 * <p>
		 * is a complex branch. If a branch is complex, then it is wrapped by a 
		 * ClassItem. Wrapping by a ClassItem makes its
		 * multiplicity (1,1). This ensures that every branch has the multiplicity
		 * of at-most-one.
		 */
		public Expression onChoice( ChoiceExp exp ) {
			
			// check whether there is only one meaningul branch, or more than one of them.
			Expression[] b = exp.getChildren();
			boolean[] complexBranch = new boolean[b.length];
			boolean[] fieldlessBranch = new boolean[b.length];
			int numLiveBranch = 0;
			
			boolean bBranchWithField = false;
			final boolean[] bBranchWithPrimitive = new boolean[1];

			if(debug!=null) {
				debug.println( "Processing Choice: " + ExpressionPrinter.printContentModel(exp) );
				debug.println("checking each branch");
			}
			
			for( int i=0; i<b.length; i++ ) {
				final boolean[] hasChildFieldItem = new boolean[1];

				// compute the multiplicity of the all child JavaItems and 
				// also compute whether this branch has FieldItem in it.
				Multiplicity m = Multiplicity.calc( b[i],
					new MultiplicityCounter(){
						protected Multiplicity isChild( Expression exp ) {
							if(exp instanceof FieldItem)	hasChildFieldItem[0] = true;
							if(exp instanceof PrimitiveItem)	bBranchWithPrimitive[0] = true;
							
							if(exp instanceof IgnoreItem)	return Multiplicity.zero;
							if(exp instanceof JavaItem)		return Multiplicity.one;
							else						return null;
						}
					});

				if(debug!=null) {
					debug.println( "  Branch: " + ExpressionPrinter.printContentModel(b[i]) );
					debug.println( "    multiplicity:"+m+"  hasChildFieldItem:"+hasChildFieldItem[0] );
				}
				
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
				
				bBranchWithField = true;
				
				// this branch has a FieldItem. perform recursion.
				b[i] = b[i].visit(this);
			}
			
			if( numLiveBranch<=1 ) {
				// there is only one meaningful branch.
				// this happens for patterns like <optional>.
				
				// visit all unvisited branch
				for( int i=0; i<b.length; i++ )
					if( fieldlessBranch[i] || complexBranch[i] )
						b[i] = b[i].visit(this);
				
				Expression r = Expression.nullSet;
				for( int i=0; i<b.length; i++ )
					r = pool.createChoice( r, b[i] );
			
				return r;
				
			} else {
				
				/*
				if we don't have any branch with FieldItem, then we just need
				one FieldItem to cover the entire branches.
				
				TODO:(?) actually this would be done better. Even if there are 
				branches with FieldItems, one created FieldItem can cover all
				FieldItem-less branches, and then that FieldItem and other
				branches can be combined. But is it an improvement?
				*/
				
				for( int i=0; i<b.length; i++ ) {
					if( complexBranch[i] ) {
						if(debug!=null)
							debug.println("  Insert a wrapper class on: "+ExpressionPrinter.printContentModel(exp));
						
						// insert a new class item here.
						String className = owner.getTypeName()+"Subordinate"+(++iota);
						
						ClassItem ci = grammar.createClassItem( className, b[i].visit(this) );
						ci.isTemporary = true;
						b[i] = ci;
					}
				}
			
				Expression r = Expression.nullSet;
				for( int i=0; i<b.length; i++ )
					r = pool.createChoice( r, b[i] );

				if( !bBranchWithField ) {
					// there was no branch with FieldItem.
					
					if( !bBranchWithPrimitive[0] ) {
						// if there is no branch with a PrimitiveItem,
						// add an interface item automatically.
						
						// compute the interface name
						String packagePrefix = owner.getPackageName();
						if(packagePrefix==null)	packagePrefix="";
						else					packagePrefix+=".";
						
						String intfName = "I"+owner.getBareName()+"Content";
						
						if(grammar.interfaces.containsKey(packagePrefix+intfName)) {
							// the last resort
							int cnt = 2;
							while( grammar.interfaces.containsKey(packagePrefix+intfName+cnt) )
								cnt++;
							intfName = intfName + cnt;
						}
					
						if(debug!=null) {
							debug.println("  Wrap it by an interface iem: "+packagePrefix+intfName);
							debug.println("  "+ ExpressionPrinter.printContentModel(r) );
						}
						r = grammar.createInterfaceItem( packagePrefix+intfName, r );
					}
				}
				return r;
			}
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
						new PrimitiveItem( StringType.theInstance,
							pool.createTypedString(StringType.theInstance) )),
					exp.exp );
			
			try {
				// see if "exp" contains FieldItem.
				exp.exp.visit( new ExpressionWalker(){
					public void onOther( OtherExp exp ) {
						if(exp instanceof FieldItem)	throw eureka;
						if(exp instanceof JavaItem)		return;
						super.onOther(exp);
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
			
			return expanded;
		}
		
	}
		
	private static void assert( boolean b ) {
		if(!b)	throw new Error();
	}
}
