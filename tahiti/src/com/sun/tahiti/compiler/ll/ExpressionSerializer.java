/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.compiler.ll;

import org.relaxng.datatype.Datatype;
import com.sun.msv.grammar.*;
import com.sun.tahiti.compiler.XMLWriter;
import com.sun.tahiti.compiler.Symbolizer;
import java.util.Map;
import java.util.Set;
import java.text.MessageFormat;

/**
 * serializes {@link Expression}s to Java source code.
 */
class ExpressionSerializer {
	
	/**
	 * this map will receive the order of serialization.
	 * expressions with the younger numbers should be serialized before
	 * the expressions with the older numbers.
	 */
	public final Map	expr2id = new java.util.HashMap();
	/**
	 * this set will receive the shared expressions.
	 */
	public final Set	sharedExps = new java.util.HashSet();
	/**
	 * generated XML representation should be sent to this object.
	 */
	public final XMLWriter out;
	/**
	 * this object is used to resolve the symbol to its name.
	 */
	public final Symbolizer symbolizer;
	
	ExpressionSerializer( Symbolizer symbolizer, XMLWriter out ) {
		this.symbolizer = symbolizer;
		this.out = out;
	}
	
	/** This visitor assigns serialization numbers to each expression. */
	public final ExpressionVisitorVoid sequencer = new ExpressionVisitorVoid() {
		/*
			elements and attributes are not considered as particles.
			their instances are created before anything else,
			so we don't need to track dependency between them.
		*/
		public void onElement( ElementExp exp )		{ return; }
		public void onAttribute( AttributeExp exp ) { onExpr(exp); }
	
		// not supported
		public void onConcur( ConcurExp exp )		{ throw new Error(); }
	
		// never be possible. because <notAllowed/> is removed.
		public void onNullSet()						{ throw new Error(); }
	
		public void onTypedString( TypedStringExp exp )	{
			if(onExpr(exp))		assignId(exp);
		}
	
		public void onMixed( MixedExp exp ) {
			if(onExpr(exp)) {
				exp.exp.visit(this);
				assignId(exp);
			}
		}
	
		public void onList( ListExp exp ) {
			if(onExpr(exp)) {
				exp.exp.visit(this);
				assignId(exp);
			}
		}

		public void onOneOrMore( OneOrMoreExp exp ) {
			if(onExpr(exp)) {
				exp.exp.visit(this);
				assignId(exp);
			}
		}

		// TODO: ReferenceExps/OtherExps are unnecessary.
		// we should be able to ignore them.
		public void onRef( ReferenceExp exp ) {
			if(onExpr(exp)) {
				exp.exp.visit(this);
				assignId(exp);
			}
		}
		
		public void onOther( OtherExp exp ) {
			if(onExpr(exp)) {
				exp.exp.visit(this);
				assignId(exp);
			}
		}
	
		// epsilon is always available through Expression.epsilon,
		// so we don't need to compute dependency.
		public void onEpsilon()						{ return; }
		public void onAnyString()					{ return; }
	
		public void onSequence( SequenceExp exp )	{ onBinExp(exp); }
		public void onChoice( ChoiceExp exp )		{ onBinExp(exp); }
		public void onInterleave( InterleaveExp exp){ onBinExp(exp); }
	
		private void onBinExp( BinaryExp exp ) {
			if(onExpr(exp)) {
				exp.exp1.visit(this);
				exp.exp2.visit(this);
				assignId(exp);
			}
		}
	
	
		/**
		 * this method is called for every particles.
		 * 
		 * @return true
		 *		if the caller should traverse its descendants.
		 */
		private boolean onExpr( Expression exp ) {
			if( expr2id.containsKey(exp) ) {
				// this expression is already visited.
				sharedExps.add( exp );
				// since this expression is already visited,
				// there is no need to traverse its children.
				return false;
			} else {
				// this is the first time to visit this expression.
				// let the caller traverse the children.
				return true;
				
				// we will number this expression in post-order fashion.
			}
		}
	};

	/**
	 * assigns an unique ID for an expression.
	 */
	public void assignId( Expression exp ) {
		if( expr2id.containsKey(exp) )	throw new Error("assertion failed");
			
		expr2id.put( exp, new Integer(expr2id.size()) );
	}
	
	
	
	/** then this visitor will serialize them into Java source code.
	 */
	public final ExpressionVisitorVoid serializer = new ExpressionVisitorVoid() {
		
		public void onElement( ElementExp exp ) {
			serialize(exp.contentModel);
		}
		public void onAttribute( AttributeExp exp ) {
			serialize(exp.exp);
		}

		// not supported
		public void onConcur( ConcurExp exp )		{ throw new Error(); }
		// never be possible. because <notAllowed/> is removed.
		public void onNullSet()					{ throw new Error(); }
	
		public void onTypedString( TypedStringExp exp )	{
			out.element("typedString",
				new String[]{"dataSymbolRef",symbolizer.getId(exp)});
		}
	
		public void onMixed( MixedExp exp ) {
			out.start("mixed");
			serialize(exp.exp);
			out.end("mixed");
		}
	
		public void onList( ListExp exp ) {
			out.start("list");
			serialize(exp.exp);
			out.end("list");
		}

		public void onOneOrMore( OneOrMoreExp exp ) {
			out.start("oneOrMore");
			serialize(exp.exp);
			out.end("oneOrMore");
		}

		public void onRef( ReferenceExp exp ) {
			serialize(exp.exp);
		}
	
		public void onOther( OtherExp exp ) {
			serialize(exp.exp);
		}
	
		public void onEpsilon() {
			out.element("epsilon");
		}
		public void onAnyString() {
			out.element("text");
		}
	
		public void onSequence( SequenceExp exp )		{ onBinExp("group",exp); }
		public void onChoice( ChoiceExp exp )			{ onBinExp("choice",exp); }
		public void onInterleave( InterleaveExp exp)	{ onBinExp("interleave",exp); }
	
		private void onBinExp( String name, BinaryExp exp ) {
			out.start(name);
			serialize(exp.exp1);
			serialize(exp.exp2);
			out.end(name);
		}
	};
	/**
	 * serializes an Expression. If the specified "exp" is shared,
	 * this method writes a reference.
	 */
	public void serialize( Expression exp ) {
		if(exp instanceof ElementExp) {
			out.element("element",
				new String[]{"symbolRef",symbolizer.getId(exp)});
		} else
		if(exp instanceof AttributeExp) {
			out.element("attribute",
				new String[]{"symbolRef",symbolizer.getId(exp)});
		} else
		if(sharedExps.contains(exp)) {
			// if this expression is shared, spit the reference.
			out.element("ref",
				new String[]{"particle","o"+((Integer)expr2id.get(exp)).toString()});
		} else {
			// otherwise perform recursion.
			exp.visit(serializer);
		}
	}

	
	/**
	 * generates canonical XML representation of the name class.
	 */
	public static void serializeNameClass( NameClass nc, final XMLWriter out ) {
		out.start("name");
		nc.visit( new NameClassVisitor(){
			public Object onChoice( ChoiceNameClass nc ) {
				out.start("choice");
				nc.nc1.visit(this);
				nc.nc2.visit(this);
				out.end("choice");
				return null;
			}
			public Object onAnyName( AnyNameClass nc ) {
				out.element("anyName");
				return null;
			}
			public Object onNsName( NamespaceNameClass nc ) {
				out.element("nsName",new String[]{"ns",nc.namespaceURI});
				return null;
			}
			public Object onNot( NotNameClass nc ) {
				out.start("not");
				nc.child.visit(this);
				out.end("not");
				return null;
			}
			public Object onDifference(DifferenceNameClass nc) {
				out.start("difference");
				nc.nc1.visit(this);
				nc.nc2.visit(this);
				out.end("difference");
				return null;
			}
			public Object onSimple(SimpleNameClass nc) {
				out.element("name",
					new String[]{"ns",nc.namespaceURI,"local",nc.localName});
				return null;
			}
		});
		out.end("name");
	}
}
