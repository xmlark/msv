/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.generator;

import org.w3c.dom.*;
import org.xml.sax.ContentHandler;
import com.sun.tranquilo.datatype.NmtokenType;
import com.sun.tranquilo.datatype.StringType;
import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.grammar.trex.*;
import com.sun.tranquilo.util.StringPair;
import com.sun.tranquilo.grammar.trex.util.TREXPatternPrinter;
import java.util.*;

/**
 * generates an XML DOM instance that conforms to the given schema.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Generator implements TREXPatternVisitorVoid
{
	/** generation parameters */
	private final GeneratorOption opts;
	private final TREXPatternPool pool;
	private final Document domDoc;
	/** current generated node */
	private Node node;
	
	/** current nest level (depth of elements). */
	private int depth = 0;
	
	/** this flag is set to true once an error is generated. */
	private boolean errorGenerated = false;

	/** returns true if generator should cut back. */
	protected boolean cutBack() { return depth>5; }
	
	/** ID tokens that are used */
	private final Set ids = new HashSet();
	/** Text nodes of IDREFs that should be "patched" by IDs. */
	private final Set idrefs = new HashSet();
	
	/** all ElementExps in the grammar. */
	private final ElementExp[] elementDecls;
	/** all AttributeExps in the grammar. */
	private final AttributeExp[] attributeDecls;
	
	/** generates instance by using default settings. */
	public static void generate( Expression exp, Document emptyDoc )
	{
		generate( exp, emptyDoc, new GeneratorOption() );
	}
	
	/** generates instance by custom settings. */
	public static void generate( Expression exp, Document emptyDoc, GeneratorOption opts )
	{
		Generator g;
		
		do
		{
			while( emptyDoc.getFirstChild()!=null ) // delete any existing children
				emptyDoc.removeChild( emptyDoc.getFirstChild() );
			
			g = new Generator(exp,emptyDoc,opts);
			exp.visit(g);
			// if error ratio is specified and no error is generated, do it again.
		}while( !g.errorGenerated && opts.errorSpecified() );
		
		
		Object[] ids = g.ids.toArray();
		
		// patch IDREF.
		Iterator itr = g.idrefs.iterator();
		while( itr.hasNext() )
		{
			if( ids.length==0 )	throw new Error("no ID");
			
			Text node = (Text)itr.next();
			node.setData( (String)ids[opts.random.nextInt(ids.length)] );
		}
	}
	
	protected Generator( Expression exp, Document emptyDoc, GeneratorOption opts )
	{
		opts.fillInByDefault();
		this.opts = opts;
		this.pool = opts.pool;
		node = domDoc = emptyDoc;
		
		// collect element and attribute decls.
		Set[] s= ElementDeclCollector.collect(exp);
		elementDecls = new ElementExp[s[0].size()];
		s[0].toArray(elementDecls);
		attributeDecls = new AttributeExp[s[1].size()];
		s[1].toArray(attributeDecls);
	}
	
	/** annotate DOM by adding a comment that an error is generated. */
	private void noteError( String error )
	{
		errorGenerated = true;
		Comment com = domDoc.createComment("  "+error+"  ");
		
		Node n = node;
		if( n.getNodeType()==n.ATTRIBUTE_NODE )
		{
			n = n.getParentNode();
			n.insertBefore( com, n.getFirstChild() );
		}
		else
		{
			n.appendChild(com);
		}
	}
	
	
	public void onEpsilon() {}
	public void onNullSet() { throw new Error(); }	// assertion failed
	
	public void onSequence( SequenceExp exp )
	{
		if(!(exp.exp1 instanceof AttributeExp)
		&& !(exp.exp2 instanceof AttributeExp) )
		{// sequencing error of attribute is meaningless.
			if( opts.random.nextDouble() < opts.probSeqError )
			{// generate sequencing error
				noteError("swap sequence to "+
						  TREXPatternPrinter.printSmallest(exp.exp2)+","+
						  TREXPatternPrinter.printSmallest(exp.exp1) );
				exp.exp2.visit(this);
				exp.exp1.visit(this);
				return;
			}
		}
		
		// generate valid instance.
		exp.exp1.visit(this);
		exp.exp2.visit(this);
	}
	
	public void onInterleave( InterleavePattern ip )
	{
		// collect children
		Vector vec = getChildren(ip);
		
		Node old = node;
		// generate XML fragment for each child.
		for( int i=0; i<vec.size(); i++ )
		{
			node = domDoc.createElement("dummy");
			
			((Expression)vec.get(i)).visit(this);
			
			vec.set(i,node);
		}
		node = old;
		
		// interleave them.
		while( vec.size()!=0 )
		{
			int idx = opts.random.nextInt(vec.size());
			Element e = (Element)vec.get(idx);
			if(!e.hasChildNodes())
			{// this one has no more child.
				vec.remove(idx);
				continue;
			}
			node.appendChild( e.getFirstChild() );
		}
	}
	
	public void onChoice( ChoiceExp cp )
	{
		// "A*" is modeled as (epsilon|A+)
		if( cp.exp1==Expression.epsilon && cp.exp2 instanceof OneOrMoreExp )
		{
			onZeroOrMore( (OneOrMoreExp)cp.exp2 );
			return;
		}
		if( cp.exp2==Expression.epsilon && cp.exp1 instanceof OneOrMoreExp )
		{
			onZeroOrMore( (OneOrMoreExp)cp.exp1 );
			return;
		}
		
		if( cutBack() && cp.isEpsilonReducible() )	return;	// cut back
		
		
		// gather candidates
		Vector vec = getChildren(cp);

		if( opts.random.nextDouble() < opts.probGreedyChoiceError )
		{// greedy choice error. visit twice.
			noteError("greedy choice:"+
					  TREXPatternPrinter.printSmallest(cp));
			((Expression)vec.get(opts.random.nextInt(vec.size()))).visit(this);
			((Expression)vec.get(opts.random.nextInt(vec.size()))).visit(this);
			return;
		}
		
		// randomly select one candidate.
		((Expression)vec.get(opts.random.nextInt(vec.size()))).visit(this);
	}
	
	public void onMixed( MixedExp exp )
	{// convert it to interleave so that we can generate some pcdata.
		pool.createInterleave(
			pool.createZeroOrMore(Expression.anyString),
			exp.exp ).visit(this);
	}
	
	public void onRef( ReferenceExp exp )
	{
		exp.exp.visit(this);
	}
	
	public void onAttribute( AttributeExp exp )
	{
		if( opts.random.nextDouble() < opts.probMutatedAttrError )
		{// mutated element error. generate a random attribute and ignore this declaration.
			noteError("mutated attribute "+exp.nameClass);
			onAttribute( attributeDecls[opts.random.nextInt(attributeDecls.length)] );
			return;
		}
		
		if( opts.random.nextDouble() < opts.probMissingAttrError )
		{// missing attribute error. skip generating this instance.
			noteError("missing attribute "+exp.nameClass);
			return;
		}
		
		if( opts.random.nextDouble() < opts.probSlipInAttrError )
		{// slip-in error. generate random attribute.
			onAttribute( attributeDecls[opts.random.nextInt(attributeDecls.length)] );
			noteError("slip-in attribute "+exp.nameClass);
		}
		
		
		// generate attribute name
		StringPair name;
		int retry=0;
		do
		{
			name = getName(exp.nameClass);
		}while( ((Element)node).getAttributeNodeNS(name.namespaceURI,name.localName)!=null
			&&  retry++<100/*abort after several retries*/ );

		// It is possible
		// that this attribute is already added as a result of
		// generating an error.
		Attr a = domDoc.createAttributeNS( name.namespaceURI, name.localName );
		((Element)node).setAttributeNodeNS(a);

		Node old = node;
		node = a;
		exp.exp.visit(this);	// generate attribute value
		node = old;
	}
	
	public void onElement( ElementExp exp )
	{
		if( opts.random.nextDouble() < opts.probMutatedElemError )
		{// mutated element error. generate a random element and ignore this declaration.
			noteError("mutated element");
			onElement( elementDecls[opts.random.nextInt(elementDecls.length)] );
			return;
		}
			
		if( opts.random.nextDouble() < opts.probMissingElemError )
		{// missing element error. skip generating this instance.
			noteError("missing element: "+TREXPatternPrinter.printSmallest(exp) );
			return;
		}
		
		if( opts.random.nextDouble() < opts.probSlipInElemError )
		{// slip-in error. generate random element.
			onElement( elementDecls[opts.random.nextInt(elementDecls.length)] );
			noteError("slip-in element: "+TREXPatternPrinter.printSmallest(exp) );
		}
		
		StringPair name = getName(exp.getNameClass());
		
		Element child = domDoc.createElementNS( name.namespaceURI, name.localName );
		node.appendChild( child );
		node = child;
		
		// generate children
		depth++;
		exp.contentModel.visit(this);
		depth--;
		
		node = child.getParentNode();
	}
	
	public void onAnyString()
	{
		node.appendChild( domDoc.createTextNode(opts.dtGenerator.generate(StringType.theInstance)) );
	}
	
	public void onOneOrMore( OneOrMoreExp exp )
	{
		if( opts.random.nextDouble() < opts.probMissingPlus )
		{
			noteError("missing " + TREXPatternPrinter.printSmallest(exp) );
			return;
		}
		
		int m = opts.width.next()+1;
		if( cutBack() )	m=1;
		for( int i=0; i<m; i++ )
			exp.exp.visit(this);
	}
	
	public void onZeroOrMore( OneOrMoreExp exp )
	{
		int m = opts.width.next();
		if( cutBack() )	m=0;
		for( int i=0; i<m; i++ )
			exp.exp.visit(this);
	}
	
	public void onTypedString( TypedStringExp exp )
	{
		String value;
		if( "ID".equals(exp.dt.getName()) )
		{
			do
			{
				value = opts.dtGenerator.generate(NmtokenType.theInstance);
			}while( ids.contains(value) );
			ids.add(value);
		}
		else
		if( "IDREF".equals(exp.dt.getName()) || "IDREFS".equals(exp.dt.getName()) )
		{
			Node n = domDoc.createTextNode("{TmpIDRef}");
			node.appendChild(n);
			idrefs.add(n); // memorize this node so that we can patch it later.
			return;
		}
		else
		{
			value = opts.dtGenerator.generate(exp.dt);
		}
		
		node.appendChild( domDoc.createTextNode(value) );
	}
	
	public void onConcur( ConcurPattern exp )
	{
		throw new Error("concur is not supported");
	}
	
	
	/** generaets a name that satisfies given NameClass */
	private StringPair getName( NameClass nc )
	{
		StringPair name = (StringPair)nc.visit(opts.nameGenerator);
		
		if( !nc.accepts( name.namespaceURI, name.localName ) )
			throw new Error();	// invalid
		
		return name;
	}
	
	
	/** enumerates children of BinaryExp into a vector. */
	private Vector getChildren( BinaryExp exp )
	{
		final Vector vec = new Vector();
		Iterator itr = exp.children();
		while( itr.hasNext() )	vec.add( itr.next() );
		return vec;
	}
}
