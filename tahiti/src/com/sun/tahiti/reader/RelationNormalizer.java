package com.sun.tahiti.reader;

import com.sun.msv.grammar.*;
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.reader.GrammarReader;
import com.sun.tahiti.grammar.*;
import java.util.Set;
import org.xml.sax.Locator;

/**
 * Normalizes JavaItem relationShip.
 * 
 * <h2>1st pass</h2>
 * <p>
 * Its first job is to find all class-class or class-interface relationships
 * and change them to class-field-class or class-field-interface respectively.
 * 
 * If class-class relationship is found, a new FieldItem object is created and
 * inserted immediately above the child class item. The same thing haapens
 * for class-interface relationship.
 * 
 * <p>
 * Its second job is to check that prohibited relationships are not used.
 * For example, super-super relationship is prohibited. See the design document
 * for the complete list of the prohibited relationships.
 * 
 * <p>
 * Its third job is to find the actual ClassItem for every SuperClassItem.
 * There has to be one and only one ClassItem for each SuperClassItem,
 * and its cardinality must be '1'.
 * 
 * <p>
 * Its fourth job is to make sure that a ClassItem has at most one SuperClassItem,
 * and its cardinality must be '1' or '?'.
 * 
 * <p>
 * Its fifth job is to create a FieldUse object for each class-field relationship and
 * connects a class and its fields. It is possible and allowed for one ClassItem object
 * to have multiple FieldUse objects that point the same Field object.
 * Those cardinality computation is done at the 2nd pass.
 * 
 * <p>
 * Its sixth job is to process "interface-class" relationship. Whenever this is 
 * relationship is found, the class is recorded to implement the specified interface.
 * Its cardinality must be (1,1). (It can be relaxed to allow (0,1))
 * 
 * ++++++++++++ interface-class and interface-interface needs a special cardinality check
 * to prevent things like:
 * <XMP>
 *   <group t:role="interface">
 *     <element t:role="class"/>
 *     <element t:role="class"/>
 *   </group>
 * </XMP>
 * 
 * 
 * 
 * <h2>2nd pass</h2>
 * <p>
 * In the 2nd pass, our first job is to compute the total cardinality of each field.
 * One ClassItem can have multiple FieldItem with the same name, and one FieldItem
 * can have multiple TypeItem as its children.
 * 
 * <p>
 * In the 1st pass, we've computed the cardinality for every FieldItem. So before
 * the 2nd pass, we are in the following situation:
 * 
 * <PRE><XMP>
 *   <group t:role="class">
 *     <element name="abc" t:role="field"> <!-- cardinality (1,1) -->
 *       <ref name="abc.model"/>
 *     </element>
 *     <oneOrMore t:role="field"> <!-- cardinality (1,unbounded) -->
 *       <element name="abc">
 *         <ref name="abc.model"/>
 *       </element>
 *     </oneOrMore>
 *   </group>
 * </XMP></PRE>
 * 
 * <p>
 * We'd like to know the "total" cardinality of the field "abc". In this case,
 * it will be (2,unbounded).
 * 
 * <p>
 * Its next job is to compute the type of the field. Field values may have
 * different types, and we need to compute the common base type.
 */
public class RelationNormalizer {
	
	private RelationNormalizer( GrammarReader reader ) {
		this.reader = reader;
	}
	
	private final GrammarReader reader;
	
	public static Expression normalize( GrammarReader reader, Expression exp ) {
		RelationNormalizer n = new RelationNormalizer(reader);
		exp = exp.visit(n.new Pass1());
		
		// for each field use in each class item,
		// compute the total cardinality.
		// also, compute the type of the field.
		ClassItem[] classItems = (ClassItem[])n.classes.toArray(new ClassItem[0]);
		for( int i=0; i<classItems.length; i++ ) {
			FieldUse[] fieldUses = (FieldUse[])classItems[i].fields.values().toArray(new FieldUse[0]);
			for( int j=0; j<fieldUses.length; j++ ) {
				
				fieldUses[j].cardinality = (Cardinality)
					classItems[i].exp.visit(n.new Pass2(fieldUses[j]));
				
				// collect all possible ClassItems for this type.
				Set possibleTypes = new java.util.HashSet();
				FieldItem[] fields = (FieldItem[])fieldUses[j].items.toArray(new FieldItem[0]);
				for( int k=0; k<fields.length; k++ )
					possibleTypes.addAll(fields[k].types);
				
				// then compute the base type of them.
				fieldUses[j].type = getCommonBaseType( (Type[])possibleTypes.toArray(new Type[0]) );
			}
		}
		
		return exp;
	}
	
	/**
	 * set of all ClassItems found in this expression.
	 * This field is computed during the 1st pass.
	 */
	protected final Set classes = new java.util.HashSet();
	
	
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
			unfortunately, we will lose any additional information
			added to this ElementExp.
			however, we have to create a copy of ElementExp. Otherwise
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
			return new ElementPattern( exp.getNameClass(),
				exp.contentModel.visit(this) );
		}
		
		public Expression onMixed( MixedExp exp ) {
			// <mixed> doesn't affect the cardinality.
			return reader.pool.createMixed(exp.exp.visit(this));
		}
		
		public Expression onList( ListExp exp ) {
			// <list> itself doesn't affect the cardinality.
			return reader.pool.createList(exp.exp.visit(this));
		}

		public Expression onConcur( ConcurExp exp ) {
			// possibly, it can be served by ignoring all but one branch.
			throw new Error("concur is not supported");
		}
		
		public Expression onChoice( ChoiceExp exp ) {
			Expression lhs = exp.exp1.visit(this);
			Cardinality lhc = cardinality;
			Expression rhs = exp.exp2.visit(this);
			Cardinality rhc = cardinality;
			
			cardinality = Cardinality.choice(lhc,rhc);
			return reader.pool.createChoice( lhs, rhs );
		}
		
		public Expression onSequence( SequenceExp exp ) {
			Expression lhs = exp.exp1.visit(this);
			Cardinality lhc = cardinality;
			Expression rhs = exp.exp2.visit(this);
			Cardinality rhc = cardinality;
			
			cardinality = Cardinality.group(lhc,rhc);
			return reader.pool.createSequence( lhs, rhs );
		}
		
		public Expression onInterleave( InterleaveExp exp ) {
			Expression lhs = exp.exp1.visit(this);
			Cardinality lhc = cardinality;
			Expression rhs = exp.exp2.visit(this);
			Cardinality rhc = cardinality;
			
			cardinality = Cardinality.group(lhc,rhc);
			return reader.pool.createInterleave( lhs, rhs );
		}
		
		public Expression onOneOrMore( OneOrMoreExp exp ) {
			Expression p = reader.pool.createOneOrMore( exp.exp.visit(this) );
			cardinality = Cardinality.oneOrMore(cardinality);
			
			return p;
		}

// terminal items. starts with cardinality (0,0)
		public Expression onEpsilon() {
			cardinality = new Cardinality(0,0);
			return Expression.epsilon;
		}
		public Expression onNullSet() {
			cardinality = new Cardinality(0,0);
			return Expression.nullSet;
		}
		public Expression onAnyString() {
			cardinality = new Cardinality(0,0);
			return Expression.anyString;
		}
		public Expression onTypedString( TypedStringExp exp ) {
			cardinality = new Cardinality(0,0);
			return exp;
		}
		
		
	// Java items
	//=======================================
		
		public Expression onRef( ReferenceExp exp ) {
			
			if( isClass(parentItem)	&& (exp instanceof Type) ) {
				// class-class, class-interface, or class-primitive relation.
				// this should be converted to
				// C-F-C, C-F-I, or C-F-P respectively
				// before anything else.
				
				// create a tag and insert it between tag and its parent.
				ReferenceExp tag = new FieldItem(typeNameToJavaName(exp.name));
				tag.exp = exp;
				// then process tag. so that we can process both C-F and F-C/I/P.
				return onRef(tag);
			}
			
			
			
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
				
				if( isClass(exp) )
					classes.add(exp);	// collect classes.
					
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
				
				// then change the parent item to this object.
				parentItem = (JavaItem)exp;

				if( !visitedClasses.add(exp) ) {
					cardinality = new Cardinality(1,1);
					// this one is a java item and already processed.
					// so there is no need to traverse it again.
					// to prevent infinite recursion, return immediately.
					return exp;
				}
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
						ERR_SUPERCLASS_BODY_NOTFOUND,
						null );
				}
				else {
					// if we couldn't find the definition, do not report this error.
					// S-C cardinality must be (1,1)
					if( !cardinality.isUnique() ) {
						reader.reportError(
							new Locator[]{
								reader.getDeclaredLocationOf(exp),
								reader.getDeclaredLocationOf(sci.definition)},
							ERR_BAD_SUPERCLASS_BODY_CARDINALITY,
							new Object[]{sci.definition.name} );
					}
				}
			}

			if( isInterface(exp) ) {
				// I-I/I-C cardinality must be (1,1)
				InterfaceItem ii = (InterfaceItem)exp;
				if( !cardinality.isUnique() ) {
					reader.reportError(
						new Locator[]{reader.getDeclaredLocationOf(ii)},
						ERR_BAD_INTERFACE_CLASS_CARDINALITY,
						new Object[]{ ii.name } );
				}
			}
			
			if( isField(exp) ) {
				// store the cardinality of this field.
				((FieldItem)exp).cardinality = cardinality;
			}
			
			cardinality = new Cardinality(1,1);
			return exp;
		}
		
		/**
		 * changes the first character to the lower case
		 */
		protected String typeNameToJavaName( String s ) {
			return Character.toLowerCase(s.charAt(0))+s.substring(1);
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
			
			if(( isField(parent) || isInterface(parent) )
			&& ( isSuperClass(child) || isField(child) )) {
				// TODO: diagnose better
				reader.reportError(
					new Locator[]{
						reader.getDeclaredLocationOf(parent),
						reader.getDeclaredLocationOf(child)},
					ERR_BAD_ITEM_USE, null );
				return;
			}
		}
		
		/**
		 * this method is called when class-super relationship is found, and after
		 * all the descendants of "super" is processed.
		 */
		protected void setSuperClassForClass( ClassItem p, SuperClassItem c ) {
			// C-S cardinality check has to be done in the 2nd pass.
/*
			if( cardinality!='1' && cardinality!='?' ) {
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
					ERR_MULTIPLE_SUPERCLASS,
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
					ERR_MULTIPLE_DEFINITIONS_FOR_SUPERCLASS, null );
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
		 * cardinality from the current parent (either '1', '?', '+', or '*').
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
		private Cardinality cardinality = null;
	}
	
	
	
	/**
	 * computes the total cardinality of a FieldUse.
	 */
	private class Pass2 implements ExpressionVisitor {
		
		Pass2( FieldUse fieldUse ) {
			this.fieldUse = fieldUse;
		}
		
		private final FieldUse fieldUse;
		
		public Object onEpsilon()	{ return new Cardinality(0,0); }
		public Object onNullSet()	{ return new Cardinality(0,0); }
		public Object onAnyString()	{ return new Cardinality(0,0); }
		public Object onTypedString( TypedStringExp exp ) { return new Cardinality(0,0); }
		
		public Object onSequence( SequenceExp exp ) {
			return Cardinality.group(
				(Cardinality)exp.exp1.visit(this),
				(Cardinality)exp.exp2.visit(this) );
		}
		
		public Object onInterleave( InterleaveExp exp ) {
			return Cardinality.group(
				(Cardinality)exp.exp1.visit(this),
				(Cardinality)exp.exp2.visit(this) );
		}
		
		public Object onChoice( ChoiceExp exp ) {
			return Cardinality.choice(
				(Cardinality)exp.exp1.visit(this),
				(Cardinality)exp.exp2.visit(this) );
		}
		
		public Object onList( ListExp exp )				{ return exp.exp.visit(this); }
		public Object onMixed( MixedExp exp )			{ return exp.exp.visit(this); }
		public Object onAttribute( AttributeExp exp )	{ return exp.exp.visit(this); }
		public Object onElement( ElementExp exp )		{ return exp.contentModel.visit(this); }
		
		public Object onConcur( ConcurExp exp ) {
			// concur can be supported at least for this method.
			throw new Error();
		}
		
		public Object onOneOrMore( OneOrMoreExp exp ) {
			return Cardinality.oneOrMore( (Cardinality)exp.exp.visit(this) );
		}
		
		
		public Object onRef( ReferenceExp exp ) {
			// if this is a FieldItem and it counts, then
			// return its cardinality.
			if( fieldUse.items.contains(exp) ) {
				if( ((FieldItem)exp).cardinality==null)
					throw new Error("internal error");
				return ((FieldItem)exp).cardinality;
			}
			
			// otherwise if it is a JavaItem, return (0,0).
			if( exp instanceof JavaItem )
				return new Cardinality(0,0);
			
			// if this is just a reference, then resolve the reference.
			return exp.exp.visit(this);
		}
	}
	
	/**
	 * compute the common base type of two types.
	 * 
	 * TODO: this is a very interesting problem. Since one type has possibly
	 * multiple base types, it's not an easy problem.
	 * The current implementation is very naive.
	 */
	protected static Type getCommonBaseType( Type[] t ) {
		// TODO:
		
		for( int i=1; i<t.length; i++ )
			if(t[0]!=t[i])
				return SystemType.get(Object.class);
		
		return t[0];
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



// Normalizer error messages.
	public static final String ERR_BAD_SUPERCLASS_USE = // arg:0
		null;
	public static final String ERR_BAD_ITEM_USE = // arg:0
		null;
	public static final String ERR_MULTIPLE_DEFINITIONS_FOR_SUPERCLASS = // arg:0
		null;	// more than one class items match a superClass item.
	public static final String ERR_MULTIPLE_SUPERCLASS = // arg:1
		null;	// more than one super class items are found for a class item "{0}".
	public static final String ERR_NO_DEFINITION_FOR_SUPERCLASS = // arg:1
		null;	// super class item "{0}" doesn't have a child class item.
	public static final String ERR_BAD_SUPERCLASS_CARDINALITY  = // arg:1
		null;	// class item "{0}" can possibly match its super class several times.
	public static final String ERR_BAD_SUPERCLASS_BODY_CARDINALITY = // arg:1
		null;	// a super class item can reach this class item "{0}" more than once, or maybe zero.
	public static final String ERR_BAD_INTERFACE_CLASS_CARDINALITY = // arg:1
		null;	// the interface item "{1}" may have repeated children or is epsilon-reducible.
	public static final String ERR_SUPERCLASS_BODY_NOTFOUND = // arg:0
		null;	// a super class item without child class item.
}
