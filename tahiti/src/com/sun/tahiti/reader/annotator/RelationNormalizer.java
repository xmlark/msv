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
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.reader.GrammarReader;
import com.sun.tahiti.grammar.*;
import com.sun.tahiti.grammar.util.Multiplicity;
import com.sun.tahiti.grammar.util.MultiplicityCounter;
import com.sun.tahiti.reader.TypeUtil;
import java.util.Set;
import org.xml.sax.Locator;

/**
 * Normalizes the relationships between JavaItems.
 * 
 * <h2>1st pass</h2>
 * 
 * <p>
 * Its first job is to check that prohibited relationships are not used.
 * For example, super-super relationship is prohibited. See the design document
 * for the complete list of the prohibited relationships.
 * 
 * <p>
 * Its second job is to find the actual ClassItem for every SuperClassItem.
 * There has to be one and only one ClassItem for each SuperClassItem,
 * and its multiplicity must be '1'.
 * 
 * <p>
 * Its third job is to make sure that a ClassItem has at most one SuperClassItem,
 * and its multiplicity must be '1' or '?'.
 * 
 * <p>
 * Its fourth job is to create a FieldUse object for each class-field relationship and
 * connects a class and its fields. It is possible and allowed for one ClassItem object
 * to have multiple FieldItem objects that share the same field name.
 * 
 * <p>
 * Its fifth job is to process "interface-class" relationship. Whenever this is 
 * relationship is found, the fact that this class 
 * implements that interface is recorded.
 * Its multiplicity must be (1,1). (It can be relaxed to allow (0,1))
 * 
 * 
 * <p>
 * It also strips any tahiti declarations found under an IgnoreItem.
 * For the unmarshaller to work correctly, IgnoreItem cannot have any tahiti
 * items.
 * 
 * 
 * <h2>2nd pass</h2>
 * <p>
 * In the 2nd pass, our first job is to compute the total multiplicity of each field.
 * One ClassItem can have multiple FieldItem with the same name, and one FieldItem
 * can have multiple TypeItem as its children.
 * 
 * <p>
 * In the 1st pass, we've computed the multiplicity for every FieldItem. So before
 * the 2nd pass, we are in the following situation:
 * 
 * <PRE><XMP>
 *   <group t:role="class">
 *     <element name="abc" t:role="field"> <!-- multiplicity (1,1) -->
 *       <ref name="abc.model"/>
 *     </element>
 *     <oneOrMore t:role="field"> <!-- multiplicity (1,unbounded) -->
 *       <element name="abc">
 *         <ref name="abc.model"/>
 *       </element>
 *     </oneOrMore>
 *   </group>
 * </XMP></PRE>
 * 
 * <p>
 * We'd like to know the "total" multiplicity of the field "abc". In this case,
 * it will be (2,unbounded).
 * 
 * <p>
 * Its next job is to compute the type of the field. Field values may have
 * different types, and we need to compute the common base type.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
class RelationNormalizer {
	
	private RelationNormalizer( GrammarReader reader ) {
		this.reader = reader;
	}
	
	private final GrammarReader reader;
	
	/**
	 * performs the normalization.
	 * 
	 * @param reader
	 *		GrammarReader object which was responsible to parse the grammar.
	 *		This object is used to report errors and obtain the source location
	 *		for error messages.
	 * @param exp
	 *		The top-level expression of the parsed grammar.
	 * 
	 * @return
	 *		The top-level expression of the normalized grammar.
	 */
	public static void normalize( GrammarReader reader, AnnotatedGrammar grammar ) {
		
		RelationNormalizer n = new RelationNormalizer(reader);
		ClassItem[] classItems = grammar.getClasses();
		
		Pass1 pass1 = n.new Pass1();
		grammar.topLevel = grammar.topLevel.visit(pass1);
		for( int i=0; i<classItems.length; i++ ) {
			// explicitly visit each children since some of them
			// may be unreachable from the top level expression.
			classItems[i].visit(pass1);
		}

		
		
		// for each field use in each class item,
		// compute the total multiplicity.
		// also, compute the type of the field.
		for( int i=0; i<classItems.length; i++ ) {
			FieldUse[] fieldUses = (FieldUse[])classItems[i].fields.values().toArray(new FieldUse[0]);
			for( int j=0; j<fieldUses.length; j++ ) {
				
				fieldUses[j].multiplicity = (Multiplicity)
					classItems[i].exp.visit(n.new Pass2(fieldUses[j]));
				
				// collect all possible ClassItems for this type.
				Set possibleTypes = new java.util.HashSet();
				FieldItem[] fields = (FieldItem[])fieldUses[j].items.toArray(new FieldItem[0]);
				for( int k=0; k<fields.length; k++ )
					possibleTypes.addAll(fields[k].types);
				
				// then compute the base type of them.
				fieldUses[j].type = TypeUtil.getCommonBaseType( (Type[])possibleTypes.toArray(new Type[0]) );
			}
		}
	}
	
	/**
	 * see the documentation of RelationNormalizer.
	 * 
	 * Pass1 walks the content models of all class items.
	 */
	private class Pass1 implements ExpressionVisitorExpression {
		
		public Expression onAttribute( AttributeExp exp ) {
			Expression newContent = exp.exp.visit(this);
			if( newContent!=exp.exp )
				// the content model is modified.
				return reader.pool.createAttribute( exp.getNameClass(), newContent );
			else
				return exp;
		}
		
		public Expression onElement( ElementExp exp ) {
			/*
			although we will lose any additional information
			added to this ElementExp,
			we have to create a copy of ElementExp. Otherwise
			we cannot correclt process things like:
			
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
			
			return new ElementPattern( exp.getNameClass(), body );
		}
		
		public Expression onMixed( MixedExp exp ) {
			// <mixed> doesn't affect the multiplicity.
			return reader.pool.createMixed(exp.exp.visit(this));
		}
		
		public Expression onList( ListExp exp ) {
			// <list> itself doesn't affect the multiplicity.
			return reader.pool.createList(exp.exp.visit(this));
		}
		
		public Expression onKey( KeyExp exp ) {
			// <key> itself doesn't affect the multiplicity.
			if(exp.isKey)
				return reader.pool.createKey(exp.exp.visit(this),exp.name);
			else
				return reader.pool.createKeyref(exp.exp.visit(this),exp.name);
		}

		public Expression onConcur( ConcurExp exp ) {
			// possibly, it can be served by ignoring all but one branch.
			throw new Error("concur is not supported");
		}
		
		public Expression onChoice( ChoiceExp exp ) {
			Expression lhs = exp.exp1.visit(this);
			Multiplicity lhc = multiplicity;
			Expression rhs = exp.exp2.visit(this);
			Multiplicity rhc = multiplicity;
			
			multiplicity = Multiplicity.choice(lhc,rhc);
			return reader.pool.createChoice( lhs, rhs );
		}
		
		public Expression onSequence( SequenceExp exp ) {
			Expression lhs = exp.exp1.visit(this);
			Multiplicity lhc = multiplicity;
			Expression rhs = exp.exp2.visit(this);
			Multiplicity rhc = multiplicity;
			
			multiplicity = Multiplicity.group(lhc,rhc);
			return reader.pool.createSequence( lhs, rhs );
		}
		
		public Expression onInterleave( InterleaveExp exp ) {
			Expression lhs = exp.exp1.visit(this);
			Multiplicity lhc = multiplicity;
			Expression rhs = exp.exp2.visit(this);
			Multiplicity rhc = multiplicity;
			
			multiplicity = Multiplicity.group(lhc,rhc);
			return reader.pool.createInterleave( lhs, rhs );
		}
		
		public Expression onOneOrMore( OneOrMoreExp exp ) {
			Expression p = reader.pool.createOneOrMore( exp.exp.visit(this) );
			multiplicity = Multiplicity.oneOrMore(multiplicity);
			
			return p;
		}

// terminal items. starts with multiplicity (0,0)
		public Expression onEpsilon() {
			multiplicity = Multiplicity.zero;
			return Expression.epsilon;
		}
		public Expression onNullSet() {
			// nullSet should have been completely removed.
			throw new Error();
		}
		public Expression onAnyString() {
			// anyString should have been completely removed.
			throw new Error();
		}
		public Expression onTypedString( TypedStringExp exp ) {
			multiplicity = Multiplicity.zero;
			return exp;
		}
		
		public Expression onRef( ReferenceExp exp ) {
			return exp.exp.visit(this);
		}
		
	// Java items
	//=======================================
		
		public Expression onOther( OtherExp exp ) {
			
			// if it's not a java item,
			// simply recurse its contents.
			// note that we can't update exp.exp by the modified expressions.
			if(!(exp instanceof JavaItem))
				return exp.exp.visit(this);
			
			// skip any JavaItem if it is in an ignored item.
			// this will effectively clone the entire descendants of the 
			// IgnoreItem.
			if( isIgnore(parentItem) )
				return exp.exp.visit(this);
			
			
			
			JavaItem old = parentItem;
			if( exp instanceof JavaItem ) {
				// this is a java item.
				
				// check if this relation is allowed.
				// several relationships are prohibited (like S-S).
				sanityCheck( parentItem, (JavaItem)exp );
				
				if( isSuperClass(parentItem) && isClass(exp) )
					setSuperClassBody( (SuperClassItem)parentItem, (ClassItem)exp );
				
				if( isInterface(parentItem) && isType(exp) )
					setImplementedInterface( (TypeItem)exp, (InterfaceItem)parentItem );
				
				if( isClass(parentItem) && isSuperClass(exp) )
					// this is a super class to the parent class item.
					setSuperClassForClass( (ClassItem)parentItem, (SuperClassItem)exp );
			
				if( isClass(parentItem) && isField(exp) ) {
					// this is a field to the parent class item.
					FieldItem fi = (FieldItem)exp;
					((ClassItem)parentItem).getFieldUse(fi.name).items.add(fi);
				}
				
				if( isField(parentItem) && (exp instanceof Type) )
					((FieldItem)parentItem).types.add(exp);
				
				if( !visitedClasses.add(exp) ) {
					multiplicity = getJavaItemMultiplicity(exp);
					// this one is a java item and already processed.
					// so there is no need to traverse it again.
					// to prevent infinite recursion, return immediately.
					return exp;
				}
				
				// then change the parent item to this object.
				parentItem = (JavaItem)exp;
			}

			
			
			
			// visit children
			exp.exp = exp.exp.visit(this);
			
			parentItem = old;
			
			// make sure that this class item is defined properly.
			// this part of the code is executed only once per each JavaItem.
			
			if( isSuperClass(exp) ) {
				// super class item must have its definition.
				SuperClassItem sci = (SuperClassItem)exp;
				if( sci.definition==null ) {
					reader.reportError(
						new Locator[]{reader.getDeclaredLocationOf(exp)},
						ERR_MISSING_SUPERCLASS_BODY,
						null );
				}
				else {
					// if we couldn't find the definition, do not report this error.
					// S-C multiplicity must be (1,1)
					if( !multiplicity.isUnique() ) {
						reader.reportError(
							new Locator[]{
								reader.getDeclaredLocationOf(exp),
								reader.getDeclaredLocationOf(sci.definition)},
							ERR_BAD_SUPERCLASS_BODY_MULTIPLICITY,
							new Object[]{sci.definition.name} );
					}
				}
			}

			if( isField(exp) ) {
				// store the multiplicity of this field.
				((FieldItem)exp).multiplicity = multiplicity;
			}
			
			if( isInterface(exp) ) {
				// I-I/I-C multiplicity must be (1,1)
				InterfaceItem ii = (InterfaceItem)exp;
				if( !multiplicity.isAtMostOnce() ) {
					reader.reportError(
						new Locator[]{reader.getDeclaredLocationOf(ii)},
						ERR_BAD_INTERFACE_CLASS_MULTIPLICITY,
						new Object[]{ ii.name } );
				}
				
				// InterfaceItem returns the multiplicity of its children.
				// so don't touch the multiplicity field
				return exp;
			}
			
			multiplicity = getJavaItemMultiplicity(exp);
			return exp;
		}
		
		private Multiplicity getJavaItemMultiplicity( OtherExp item ) {
			if( item instanceof IgnoreItem )	return Multiplicity.zero;
			else								return Multiplicity.one;
		}
		
		/**
		 * performs sanity check for the use of roles.
		 */
		private void sanityCheck( JavaItem parent, JavaItem child ) {
			if( isSuperClass(parent) && !isClass(child) ) {
				// super-field, super-super, super-interface.
				reader.reportError(
					new Locator[]{
						reader.getDeclaredLocationOf(parent),
						reader.getDeclaredLocationOf(child)},
					ERR_BAD_SUPERCLASS_USE, null );
				return;
			}
			
			if( isPrimitive(parent) )
				// primitive-*.
				// since PrimitiveItems are not specified by the user,
				// it must be an internal error.
				throw new Error("internal error: use of primitive-* relation.");
			
			if(( isField(parent) && ( isSuperClass(child) || isField(child)) )
				|| ( isInterface(parent) && ( isSuperClass(child) || isField(child) || isPrimitive(child) ) ) ) {
				// I-S, I-F, F-S, F-F, F-P relationship.
				// TODO: diagnose better
				reader.reportError(
					new Locator[]{
						reader.getDeclaredLocationOf(parent),
						reader.getDeclaredLocationOf(child)},
					ERR_BAD_ITEM_USE, null );
				return;
			}

			
			if( isClass(parent)	&& (child instanceof Type) ) {
				// class-class, class-interface, or class-primitive relation.
				// FieldItemAnnotator should run before this process
				// to prevent such situations from happening in the normalizer.
				throw new Error("internal error. C-C/C-I/C-P relation "
					+ ((ClassItem)parent).getTypeName()+" "+
					child.toString()+
					" "+ com.sun.msv.grammar.util.ExpressionPrinter.printContentModel(parent) );
			}
		}
		
		/**
		 * this method is called when class-super relationship is found, and after
		 * all the descendants of "super" is processed.
		 */
		protected void setSuperClassForClass( ClassItem p, SuperClassItem c ) {
			// C-S multiplicity check has to be done in the 2nd pass.
/*
			if( multiplicity!='1' && multiplicity!='?' ) {
				reader.reportError(
					new Locator[]{
						reader.getDeclaredLocationOf(p),
						reader.getDeclaredLocationOf(c)},
					ERR_BAD_SUPERCLASS_CARDINALITY,
					new Object[]{p.name} );
				return;
			}
*/
			
			if( p.superClass!=null ) {
				// this parent item already has a super class.
				reader.reportError(
					new Locator[]{
						reader.getDeclaredLocationOf(p),	// parent class item
						reader.getDeclaredLocationOf(p.superClass),	// previous super class definition.
						reader.getDeclaredLocationOf(c)},	// newly found super class definition.
					ERR_MULTIPLE_INHERITANCE,
					new Object[]{p.name} );
				return;
			}
			
			p.superClass = c;
		}
		
		/**
		 * this method is called when super-class relationship is found, and before
		 * the descendants of "class" is processed.
		 */
		protected void setSuperClassBody( SuperClassItem parent, ClassItem child ) {
			// set the definition field of SuperClassItem.
			if( parent.definition!=null ) {
				/* two definitions are found. This happens for patterns like
					<group t:role="superClass">
						<group t:role="class">
							....
						</group>
						<group t:role="class">
							....
						</group>
					</group>
				*/
				reader.reportError(
					new Locator[]{
						reader.getDeclaredLocationOf(parent),
						reader.getDeclaredLocationOf(child),
						reader.getDeclaredLocationOf(parent.definition)
					},
					ERR_MULTIPLE_SUPERCLASS_BODY, null );
				// recover by ignoring this definition.
				return;
			}

			parent.definition = child;
		}
		
		/**
		 * this method is called when interface-class/interface-interface relationship
		 * is found, and before the descendants of "class" is processed.
		 */
		protected void setImplementedInterface( TypeItem c, InterfaceItem i ) {
			c.interfaces.add(i);
		}
		
		/**
		 * this set contains all visited ClassItem objects.
		 * used to prevent infinite recursion.
		 */
		private final Set visitedClasses = new java.util.HashSet();
		
		/**
		 * this field holds the parent item object.
		 */
		private JavaItem parentItem = null;
		
		/**
		 * multiplicity from the current parent (either '1', '?', '+', or '*').
		 * 
		 * '1' means "exactly once", '?' means "zero or one", '+' means
		 * "one or more", and '*' means "zero or more".
		 * This value represents how many times this expression can be matched
		 * for one parent item.
		 * 
		 * <p>
		 * For example, consider the following expression:
		 * <XMP>
		 * <group> <-- parent
		 *   <oneOrMore>
		 *     <choice>
		 *       <element/>  <-- child
		 *       <element/>
		 *     </choice>
		 *   </oneOrMore>
		 * </group>
		 * </XMP>
		 * 
		 * for one parent item, child item can appear '*' times.
		 */
		private Multiplicity multiplicity = null;
	}
	
	
	
	/**
	 * computes the total multiplicity of a FieldUse.
	 */
	private class Pass2 extends MultiplicityCounter {
		
		Pass2( FieldUse fieldUse ) {
			this.fieldUse = fieldUse;
		}
		
		private final FieldUse fieldUse;
		
		protected Multiplicity isChild( Expression exp ) {
			// if this is a FieldItem and it counts, then
			// return its multiplicity.
			if( fieldUse.items.contains(exp) ) {
				if( ((FieldItem)exp).multiplicity==null)
					throw new Error("internal error");
				return ((FieldItem)exp).multiplicity;
			}
			
			// if it is a JavaItem, return (0,0).
			if( exp instanceof JavaItem )
				return Multiplicity.zero;
			
			//  otherwise recurse to the children
			return null;
		}
	}
	
	
	
	
	
// type check utility methods.
//=================================================
	private static boolean isClass( Object exp ) {
		return exp instanceof ClassItem;
	}
	private static boolean isSuperClass( Object exp ) {
		return exp instanceof SuperClassItem;
	}
	private static boolean isInterface( Object exp ) {
		return exp instanceof InterfaceItem;
	}
	private static boolean isField( Object exp ) {
		return exp instanceof FieldItem;
	}
	private static boolean isType( Object exp ) {
		return exp instanceof TypeItem;
	}
	private static boolean isPrimitive( Object exp ) {
		return exp instanceof PrimitiveItem;
	}
	private static boolean isIgnore( Object exp ) {
		return exp instanceof IgnoreItem;
	}


// Normalizer error messages.
	public static final String ERR_BAD_SUPERCLASS_USE = // arg:0
		"Normalizer.BadSuperClassUse";
	public static final String ERR_BAD_ITEM_USE = // arg:0
		"Normalizer.BadItemUse";
	public static final String ERR_MULTIPLE_SUPERCLASS_BODY = // arg:0
		"Normalizer.MultipleSuperClassBody";	// more than one class items match a superClass item.
	public static final String ERR_MULTIPLE_INHERITANCE = // arg:1
		"Normalizer.MultipleInheritance";	// more than one super class items are found for a class item "{0}".
	public static final String ERR_MISSING_SUPERCLASS_BODY = // arg:1
		"Normalizer.MissingSuperClassBody";	// super class item "{0}" doesn't have a child class item.
	public static final String ERR_BAD_SUPERCLASS_MULTIPLICITY  = // arg:1
		"Normalizer.BadSuperClassMultiplicity";	// class item "{0}" can possibly match its super class several times.
	public static final String ERR_BAD_SUPERCLASS_BODY_MULTIPLICITY = // arg:1
		"Normalizer.BadSuperClassBodyMultiplicity";	// a super class item can reach this class item "{0}" more than once, or maybe zero.
	public static final String ERR_BAD_INTERFACE_CLASS_MULTIPLICITY = // arg:1
		"Normalizer.BadInterfaceToClassMultiplicity";	// the interface item "{1}" may have repeated children or is epsilon-reducible.
}
