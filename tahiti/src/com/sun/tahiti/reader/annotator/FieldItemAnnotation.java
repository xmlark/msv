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
 * adds missing FieldItems to the grammar.
 * 
 * This algorithm expands C-C, C-I, and C-P relationships to
 * C-F/F-C, C-F/F-I, and C-F/F-P relationships.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
class FieldItemAnnotation
{
	private static java.io.PrintStream debug = null;
	
	public static void annotate( AnnotatedGrammar g ) {
		
		FieldItemAnnotation ann = new FieldItemAnnotation();
		
		// process all class items.
		ClassItem[] classes = g.getClasses();
		for( int i=0; i<classes.length; i++ )
			classes[i].exp = classes[i].exp.visit(ann.new Annotator(g,classes[i]));
	}
	
	private FieldItemAnnotation() {}
	
	/**
	 * this map will keep the annotated ReferenceExps. ReferenceExp is the key
	 * and annotated result is the value. This map is very important to minimize
	 * the number of generated classes/interfaces.
	 */
	private final Map annotatedRefs = new java.util.HashMap();
	
	/**
	 * Annotate the body of one ClassItem.
	 */
	private class Annotator extends ExpressionCloner {
		
		private Annotator( AnnotatedGrammar g, ClassItem owner ) {
			super(g.getPool());
			this.owner = owner;
			this.grammar = g;
			names.push("content");	// if no other name is available, "content" is used.
		}
		
		/**
		 * the top of this stack is the name of the nearest enclosing named item.
		 * 
		 * This information is used to determine the name of newly added FieldItem.
		 */
		private final Stack names = new Stack();
		
		/** the grammar object to which we are adding annotation. */
		private final AnnotatedGrammar grammar;
		
		/** the current ClassItem object. Its body is what we are dealing with now. */
		private final ClassItem owner;
		
		public Expression onRef( ReferenceExp exp ) {
			
			// If this ReferenceExp has already visited and annotated. reuse it.
			// This will prevent annotating the same ChoiceExp, etc again and again.
			Expression r = (Expression)annotatedRefs.get(exp);
			if(r!=null)			return r;

			// store the name information
			boolean pushed = false;
			if(exp.name!=null ) {
				if(!(exp instanceof SimpleTypeExp)) {
					// typically we don't want to use the type name 
					// as the field name.
					names.push(exp.name);
					pushed = true;
				}
			}
			r = exp.exp.visit(this);
			
			if( pushed )
				// store the annotated result.
				// if we haven't push the name,
				// the top of the names stack is the name from ancestors.
				// in that case, visiting this node next time will
				// produce a different result.
				// So we cannot memorize the result.
				annotatedRefs.put(exp,r);
			
//	debug: assertion check
// since it is now properly annotated,
// every ClassItem, PrimitiveItem or InterfaceItem must be wrapped by FieldItem.
			r.visit( new ExpressionWalker(){
				public void onOther( OtherExp exp ) {
					if( exp instanceof FieldItem )
						return;
					if( exp instanceof IgnoreItem )
						return;
					if( exp instanceof JavaItem ) {
						System.err.println(exp);
						throw new Error();
					}
				}
			});
			
			
			if(pushed)	names.pop();
			return r;
		}
		
		public Expression onOther( OtherExp exp ) {
			// expands C-C,C-P,C-I relationship.
			if( exp instanceof PrimitiveItem
			||  exp instanceof InterfaceItem
			||  exp instanceof ClassItem )
				return  new FieldItem( decideName(exp.exp), exp );
			
			if( exp instanceof IgnoreItem
			||  exp instanceof SuperClassItem
			||	exp instanceof FieldItem )
				return exp;
			
			return exp.exp.visit(this);
		}

		public Expression onAttribute( AttributeExp exp ) {
			Expression body = visitXMLItemContent(exp);
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
			
			Expression body = visitXMLItemContent(exp);
			if(body==exp.contentModel)	return exp;
			else	return new ElementPattern( exp.getNameClass(), body );
		}
		
		private Expression visitXMLItemContent( NameClassAndExpression exp ) {
			// if this element or attribute has a simple name, then
			// push it to the name stack.
			String name=null;
			NameClass nc = exp.getNameClass();
			if( nc instanceof SimpleNameClass )
				name = ((SimpleNameClass)nc).localName;
			
			// if this is the direct child of the owner, do not push it to
			// the name stack.
			/*
			Consider the following case. Typically, a ClassItem is created on <element>.
			In this case, ClassItem's exp field is an ElementExp.
			
			<tahiti:classItem name="Foo">
			  <element name="foo">
			    <group>
			      <tahiti:classItem name="First">
			        <element name="FirstName">
			           ....
			
			In cases like this, it's silly to use "foo" as the name of children.
			So don't push it to the name stack.
			*/
			if( exp==owner.exp )
				name = null;
			
			if(name!=null)	names.push(name);
			Expression body = exp.getContentModel().visit(this);
			if(name!=null)	names.pop();
			
			return body;
		}
		
		/**
		 * decides a name to be used as the field name.
		 * 
		 * Use the name at the stack top. As a side effect,
		 * this method modifies the stack top so that fields that
		 * are added later will have different names.
		 */
		private String decideName( Expression hint ) {
			String name=null;
			
			// use hint if available.
			if( hint!=null && hint instanceof NameClassAndExpression ) {
				NameClass nc = ((NameClassAndExpression)hint).getNameClass();
				if( nc instanceof SimpleNameClass )
					return NameUtil.xmlNameToJavaName("field",((SimpleNameClass)nc).localName);
			}
			
			if( name==null )
				name = (String)names.pop();
			
			// the value of name must be the form of "xxxxxNNN" where 
			// NNN is digits.
			int idx = name.length()-1;
			while( Character.isDigit(name.charAt(idx)) && idx>=0 )	idx--;
			idx++;
			
			if(idx==name.length())	// the name doesn't have any number in its suffix.
				names.push( name+"1" );
			else // increment the number
				names.push( name.substring(0,idx)+(Integer.parseInt(name.substring(idx))+1) );
			
			return NameUtil.xmlNameToJavaName("field",name);
		}
	}


//	/**
//	 * generates a field name suitable to hold a reference for the specified class.
//	 */
//	private static String typeNameToFieldName( String bareName ) {
//		return Character.toLowerCase(bareName.charAt(0))+bareName.substring(1);
//	}
		
	private static void assert( boolean b ) {
		if(!b)	throw new Error();
	}
}
