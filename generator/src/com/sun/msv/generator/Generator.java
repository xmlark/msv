package com.sun.tranquilo.generator;

import org.w3c.dom.*;
import org.xml.sax.ContentHandler;
import com.sun.tranquilo.datatype.NmtokenType;
import com.sun.tranquilo.datatype.StringType;
import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.grammar.trex.*;
import com.sun.tranquilo.util.StringPair;
import java.util.*;

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
	
	/** returns true if generator should cut back. */
	protected boolean cutBack() { return depth>5; }
	
	private final Set ids = new HashSet();
	private final Set idrefs = new HashSet();
	
	public static void generate( Expression exp, Document emptyDoc )
	{
		generate( exp, emptyDoc, new GeneratorOption() );
	}
	public static void generate( Expression exp, Document emptyDoc, GeneratorOption opts )
	{
		Generator g = new Generator(emptyDoc,opts);
		exp.visit(g);
		
		Object[] ids = g.ids.toArray();
		
		// patch IDREF.
		Iterator itr = g.idrefs.iterator();
		while( itr.hasNext() )
		{
			if( ids.length==0 )	throw new Error("no ID");
			
			Text node = (Text)itr.next();
			node.setData( (String)ids[opts.random.nextInt(ids.length)] );
		}
//		emptyDoc.normalize();
	}
	
	private Generator( Document emptyDoc, GeneratorOption opts )
	{
		opts.fillInByDefault();
		this.opts = opts;
		this.pool = opts.pool;
		node = domDoc = emptyDoc;
	}
	
	
	public void onEpsilon() {}
	public void onNullSet() { throw new Error(); }	// assertion failed
	
	public void onSequence( SequenceExp exp )
	{
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
		// generate attribute name
		StringPair name;
		do
		{
			name = getName(exp.nameClass);
		}while( ((Element)node).getAttributeNodeNS(name.namespaceURI,name.localName)!=null );

		Attr a = domDoc.createAttributeNS( name.namespaceURI, name.localName );
		((Element)node).setAttributeNodeNS(a);

		Node old = node;
		node = a;
		exp.exp.visit(this);	// generate attribute value
		node = old;
	}
	
	public void onElement( ElementExp exp )
	{
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
