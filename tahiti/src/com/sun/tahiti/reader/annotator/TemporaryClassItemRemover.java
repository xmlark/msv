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

import com.sun.msv.grammar.*;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.tahiti.grammar.*;
import com.sun.tahiti.grammar.util.Multiplicity;
import com.sun.tahiti.grammar.util.MultiplicityCounter;
import java.util.Set;
import java.util.Iterator;

/**
 * removes temporarily added ClassItems (those ClassItems whose isTemporary field
 * is true) if they are unnecessary.
 * 
 * <p>
 * The current implementation employs the following criteria to find "unnecessary"
 * class items.
 * 
 * <ol>
 *  <li>Any ClassItem that are referenced from InterfaceItem or SuperClassItem
 *		cannot be removed. InterfaceItem requires its children to be a ClassItem,
 *		so does SuperClassItem. Thus we cannot remove them.
 * 
 *  <li>Any ClassItem that has more than one child ClassItem/InterfaceItem/
 *		PrimitiveItem cannot be removed.
 *		Those items are considered too complex to be removed.
 * 
 *  <li>Any ClassItem that can be reached from more than one ClassItem (or the top
 *		level expression) cannot be	removed. Counting a reference from the top
 *		level expression is very important. Otherwise a ClassItem will be removed
 *		if it is the only one ClassItem in the entire grammar.
 * </ol>
 */
class TemporaryClassItemRemover {
	
	public static void remove( AnnotatedGrammar grammar ) {
		// run the first pass and determine which class items can be removed.
		Pass1 p1 = new Pass1();
		grammar.topLevel.visit(p1);
		
		Set cs = new java.util.HashSet(grammar.classes);
		cs.removeAll( p1.notRemovableClasses );
		
		// run the second pass and remove unnecessary class items.
		grammar.topLevel = grammar.topLevel.visit(
			new Pass2( grammar.getPool(), cs ) );
		grammar.removeClassItems(cs);
	}
	
	/**
	 * computes exact ClassItems to be removed.
	 */
	private static class Pass1 extends ExpressionWalker implements JavaItemVisitor {
		
		/** this set stores all examined ClassItems. */
		private final Set checkedClasses = new java.util.HashSet();
		
		/** this set stores ClassItems that are determined not to be removed. */
		final Set notRemovableClasses = new java.util.HashSet();
		
		/** this set stores all the children of the current ClassItem. */
		private Set childItems = new java.util.HashSet();
		
		private JavaItem parentItem;
		
		public void onOther( OtherExp exp ) {
			
			if(!(exp instanceof JavaItem )) {
				// we don't know what this OtherExp is.
				super.onOther(exp);
				return;
			}
			
			((JavaItem)exp).visitJI(this);
		}
			
		public Object onIgnore( IgnoreItem item ) {
			// since IgnoreItem is completely ignored, don't perform recursion.
			return null;
		}
		
		public Object onField( FieldItem item ) {
			// we are not interested in FieldItems now.
			// just perform recursion.
			super.onOther(item);
			return null;
		}
		
		public Object onSuper( SuperClassItem item ) { updateAndVisit(item); return null; }
		public Object onInterface( InterfaceItem item ) { updateAndVisit(item); return null; }
		
		private void updateAndVisit( JavaItem item ) {
			childItems.add(item);
			// update the parentItem field, and check the body.
			JavaItem old = parentItem;
			parentItem=item;
			super.onOther(item);
			parentItem=old;
		}
		
		public Object onPrimitive( PrimitiveItem item ) {
			// we don't need to check the body of a PrimitiveItem.
			// just store it and return.
			childItems.add(item);
			return null;
		}
		
		public Object onClass( ClassItem item ) {
			
			childItems.add(item);	// this has to be done before the checkedClasses field is examined.
			
			if((parentItem instanceof SuperClassItem)
			|| (parentItem instanceof InterfaceItem))
				// if a ClassItem is referenced from SuperClassItem or InterfaceItem,
				// then it can't be removed.
				notRemovableClasses.add(item);
			
			if(!checkedClasses.add(item)) {
				// if this ClassItem is already checked don't check it again.
				// This also means that this ClassItem is referenced more than once.
				// so this ClassItem cannot be removed.
				notRemovableClasses.add(item);
				return null;
			}
			
			if( !item.isTemporary )
				// if this ClassItem is not a temporary one,
				// of course it cannot be removed.
				notRemovableClasses.add(item);
			
			// prepare a fresh set to collect child JavaItems.
			Set oldChildItems = childItems;
			childItems = new java.util.HashSet();
			JavaItem oldParent = parentItem;
			parentItem = item;
			
			// visit the children and see what are children of this ClassItem.
			super.onOther(item);
			
			if( childItems.size()==0 ) {
				// if there is no child item, then probably
				// the existance of this element is significant.
				// so keep it.
				notRemovableClasses.add(item);
			} else
			if( childItems.size()>1 ) {
				// if a ClassItem has more than one child items,
				// then it cannot be removed.
				notRemovableClasses.add(item);
			} else {
				// if its sole child is not a primitive item, then it cannot
				// be removed either.
				if(!(childItems.iterator().next() instanceof PrimitiveItem ))
					notRemovableClasses.add(item);
			}
			
			childItems = oldChildItems;
			parentItem = oldParent;
			
			return null;
		}
	};
	
	/**
	 * removes specified ClassItems.
	 * <p>
	 * In this implementation, we don't propagate any information from the ancestor nodes
	 * to child nodes. Therefore it is safe to rewrite the body of ReferenceExp (and
	 * the content model field of ElementExps).
	 */
	private static class Pass2 extends ExpressionCloner {
		
		Pass2( ExpressionPool pool, Set targets ) {
			super(pool);
			this.targets = targets;
		}
		
		/** ClassItems contained in this set will be removed by this procedure. */
		private final Set targets;
		
	// assertions. these method may never be called.
		public Expression onNullSet()							{ throw new Error(); }
		public Expression onConcur( ConcurExp exp )				{ throw new Error(); }

	// attribute/element.
		public Expression onAttribute( AttributeExp exp ) {
			Expression body = exp.exp.visit(this);
			if( body==exp.exp )	return exp;
			else	return pool.createAttribute( exp.nameClass, body );
		}
	
		private final Set visitedExps = new java.util.HashSet();
	
		public Expression onElement( ElementExp exp ) {
			if( !visitedExps.add(exp) )
				// this exp is already processed. this check will prevent infinite recursion.
				return exp;
			exp.contentModel = exp.contentModel.visit(this);
			return exp;
		}
	
		public Expression onRef( ReferenceExp exp ) {
			if( !visitedExps.add(exp) )
				// this exp is already processed. this check will prevent infinite recursion.
				return exp;
			// update the definition and return self.
			exp.exp = exp.exp.visit(this);
			return exp;
		}
	
		public Expression onOther( OtherExp exp ) {
			if( targets.contains(exp) ) {
				// this temporary class item is unnecessary. remove it.
				// but don't forget to recurse its descendants.
				return exp.exp.visit(this);
			}
			
			// update the definition and return self.
			exp.exp = exp.exp.visit(this);
			return exp;
		}
	};
}
