/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and  use in  source and binary  forms, with  or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions  of  source code  must  retain  the above  copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution  in binary  form must  reproduct the  above copyright
 *   notice, this list of conditions  and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither  the  name   of  Sun  Microsystems,  Inc.  or   the  names  of
 * contributors may be  used to endorse or promote  products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS  OR   IMPLIED  CONDITIONS,  REPRESENTATIONS   AND  WARRANTIES,
 * INCLUDING  ANY  IMPLIED WARRANTY  OF  MERCHANTABILITY,  FITNESS FOR  A
 * PARTICULAR PURPOSE  OR NON-INFRINGEMENT, ARE HEREBY  EXCLUDED. SUN AND
 * ITS  LICENSORS SHALL  NOT BE  LIABLE  FOR ANY  DAMAGES OR  LIABILITIES
 * SUFFERED BY LICENSEE  AS A RESULT OF OR  RELATING TO USE, MODIFICATION
 * OR DISTRIBUTION OF  THE SOFTWARE OR ITS DERIVATIVES.  IN NO EVENT WILL
 * SUN OR ITS  LICENSORS BE LIABLE FOR ANY LOST  REVENUE, PROFIT OR DATA,
 * OR  FOR  DIRECT,   INDIRECT,  SPECIAL,  CONSEQUENTIAL,  INCIDENTAL  OR
 * PUNITIVE  DAMAGES, HOWEVER  CAUSED  AND REGARDLESS  OF  THE THEORY  OF
 * LIABILITY, ARISING  OUT OF  THE USE OF  OR INABILITY TO  USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

package com.sun.msv.generator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.relaxng.datatype.Datatype;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.sun.msv.datatype.xsd.NmtokenType;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.BinaryExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.grammar.util.ExpressionPrinter;
import com.sun.msv.util.StringPair;
import com.sun.xml.util.XmlChars;

/**
 * generates an XML DOM instance that conforms to the given schema.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Generator implements ExpressionVisitorVoid {
	
	/** generation parameters */
	private final GeneratorOption opts;
	private final ExpressionPool pool;
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
	public static void generate( Expression exp, Document emptyDoc ) {
		generate( exp, emptyDoc, new GeneratorOption() );
	}
	
	/** generates instance by custom settings. */
	public static void generate( Expression exp, Document emptyDoc, GeneratorOption opts ) {
		Generator g;
		
		for( int i=0; i<10; i++ ) {
			// make it empty.
			while( emptyDoc.hasChildNodes())
				emptyDoc.removeChild(emptyDoc.getFirstChild());
			
			do {
				while( emptyDoc.getFirstChild()!=null ) // delete any existing children
					emptyDoc.removeChild( emptyDoc.getFirstChild() );
				
				g = new Generator(exp,emptyDoc,opts);
				exp.visit(g);
				// if error ratio is specified and no error is generated, do it again.
			}while( !g.errorGenerated && opts.errorSpecified() );
		
		
			Object[] ids = g.ids.toArray();
			if( ids.length==0 && g.idrefs.size()!=0 )
				continue;	// IDREF is generated but no ID is generated.
							// try again.
		
			// patch IDREF.
			Iterator itr = g.idrefs.iterator();
			while( itr.hasNext() ) {
				Text node = (Text)itr.next();
				node.setData( (String)ids[opts.random.nextInt(ids.length)] );
			}
			return;
		}
		
		throw new Error("no ID");
	}
	
	protected Generator( Expression exp, Document emptyDoc, GeneratorOption opts ) {
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
	private void noteError( String error ) {
		errorGenerated = true;
		if( !opts.insertComment )	return;
		
		Node com = domDoc.createComment("  "+error+"  ");
		
		Node n = node;
		if( n.getNodeType()==Node.ATTRIBUTE_NODE ) {
			n = ((Attr)n).getOwnerElement();
			n.insertBefore( com, n.getFirstChild() );
		} else {
			n.appendChild(com);
		}
	}
	
	
	public void onEpsilon() {}
	public void onNullSet() { throw new Error(); }	// assertion failed
	
	public void onSequence( SequenceExp exp ) {
		if(!(exp.exp1 instanceof AttributeExp)
		&& !(exp.exp2 instanceof AttributeExp) ) {
			// sequencing error of attribute is meaningless.
			if( opts.random.nextDouble() < opts.probSeqError ) {
				// generate sequencing error
				noteError("swap sequence to "+
						  ExpressionPrinter.printSmallest(exp.exp2)+","+
						  ExpressionPrinter.printSmallest(exp.exp1) );
				exp.exp2.visit(this);
				exp.exp1.visit(this);
				return;
			}
		}
		
		// generate valid instance.
		exp.exp1.visit(this);
		exp.exp2.visit(this);
	}
	
	public void onInterleave( InterleaveExp ip ) {
		// collect children
		Vector vec = getChildren(ip);
		
		Node old = node;
		// generate XML fragment for each child.
		for( int i=0; i<vec.size(); i++ ) {
			node = domDoc.createElement("dummy");
			((Expression)vec.get(i)).visit(this);
			
			vec.set(i,node);
		}
		node = old;
		
		// interleave them.
		while( vec.size()!=0 ) {
			int idx = opts.random.nextInt(vec.size());
			Element e = (Element)vec.get(idx);
			if(!e.hasChildNodes()) {
				// this one has no more child.
				vec.remove(idx);
				
				// copy attributes
				// note that removing an attribute
				// will change the index of the rest.
				NamedNodeMap m = e.getAttributes();
				for( int i=0; i<m.getLength(); i++ ) {
					Attr a = (Attr)m.item(0);
					
					// due to the bug(?) of DOM (or Xercs), we cannot use
					// removeAttributeNode.
					e.removeAttributeNS(a.getNamespaceURI(),a.getLocalName());
					
					if( !((Element)node).hasAttribute(a.getName()) )
						// due to the error generation, two attributes may collide.
						((Element)node).setAttributeNodeNS(a);
				}
				
				continue;
			}
			node.appendChild( e.getFirstChild() );
		}
	}
	
	public void onChoice( ChoiceExp cp ) {
		// "A*" is modeled as (epsilon|A+)
		if( cp.exp1==Expression.epsilon && cp.exp2 instanceof OneOrMoreExp ) {
			onZeroOrMore( (OneOrMoreExp)cp.exp2 );
			return;
		}
		if( cp.exp2==Expression.epsilon && cp.exp1 instanceof OneOrMoreExp ) {
			onZeroOrMore( (OneOrMoreExp)cp.exp1 );
			return;
		}
		
		if( cutBack() && cp.isEpsilonReducible() )	return;	// cut back
		
		
		// gather candidates
		Vector vec = getChildren(cp);

		if( opts.random.nextDouble() < opts.probGreedyChoiceError ) {
			// greedy choice error. visit twice.
			Expression[] es = new Expression[2];
			for( int i=0; i<2; i++ )
				do {
					es[i] = (Expression)vec.get(opts.random.nextInt(vec.size()));
				}while(es[i]==Expression.epsilon);

			noteError("greedy choice "+
					  ExpressionPrinter.printSmallest(es[0])+ " & "+
					  ExpressionPrinter.printSmallest(es[1]));
			
			for( int i=0; i<2; i++ )
				es[i].visit(this);
			return;
		}
		
		// randomly select one candidate.
		((Expression)vec.get(opts.random.nextInt(vec.size()))).visit(this);
	}
	
	public void onMixed( MixedExp exp ) {
		// convert it to interleave so that we can generate some pcdata.
		pool.createInterleave(
			pool.createZeroOrMore(Expression.anyString),
			exp.exp ).visit(this);
	}
	
	public void onList( ListExp exp ) {
		Node oldNode = node;
		
		Element child = domDoc.createElement("dummy");
		node = child;
		
		// generate children
		exp.exp.visit(this);
		
		// several TextNode should have been appended to Element.
		// so copy them to the parent with separators.
		Node text;
		while( (text = node.getFirstChild())!=null ) {
			// append a delimiter
			oldNode.appendChild( domDoc.createTextNode(" ") );
			// append a token
			node.removeChild(text);
			oldNode.appendChild(text);
		}
	}
	
	public void onRef( ReferenceExp exp ) {
		exp.exp.visit(this);
	}
	
	public void onOther( OtherExp exp ) {
		exp.exp.visit(this);
	}
	
	public void onAttribute( AttributeExp exp ) {
		if( opts.random.nextDouble() < opts.probMutatedAttrError ) {
			// mutated element error. generate a random attribute and ignore this declaration.
			noteError("mutated attribute "+exp.nameClass);
			onAttribute( attributeDecls[opts.random.nextInt(attributeDecls.length)] );
			return;
		}
		
		if( opts.random.nextDouble() < opts.probMissingAttrError ) {
			// missing attribute error. skip generating this instance.
			noteError("missing attribute "+exp.nameClass);
			return;
		}
		
		if( opts.random.nextDouble() < opts.probSlipInAttrError ) {
			// slip-in error. generate random attribute.
			AttributeExp a = attributeDecls[opts.random.nextInt(attributeDecls.length)];
			noteError("slip-in attribute "+a.nameClass);
			onAttribute( a );
		}
		
		
		// generate attribute name
		StringPair name;
		int retry=0;
		do {
			name = getName(exp.nameClass);
		}while( ((Element)node).getAttributeNodeNS(name.namespaceURI,name.localName)!=null
			&&  retry++<100/*abort after several retries*/ );

		if( opts.random.nextDouble() < opts.probAttrNameTypo ) {
			noteError("attribute name typo: "+name.localName);
			name = generateTypo(name);
		}
		
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
	
	public void onElement( ElementExp exp ) {
		
		if( node instanceof Document && ((Document)node).getDocumentElement()!=null ) {
			// document has the root element. so abort.
			return;
		}
		
		if( opts.random.nextDouble() < opts.probMutatedElemError ) {
			// mutated element error. generate a random element and ignore this declaration.
			noteError("mutated element");
			onElement( elementDecls[opts.random.nextInt(elementDecls.length)] );
			return;
		}
				
		if( node.getNodeType()!=Node.DOCUMENT_NODE ) {
			// these errors cannot be generated for the document element
			if( opts.random.nextDouble() < opts.probMissingElemError ) {
				// missing element error. skip generating this instance.
				noteError("missing element: "+ExpressionPrinter.printSmallest(exp) );
				return;
			}
		
			if( opts.random.nextDouble() < opts.probSlipInElemError ) {
				// slip-in error. generate random element.
				ElementExp e = elementDecls[opts.random.nextInt(elementDecls.length)];
				noteError("slip-in element: "+ExpressionPrinter.printSmallest(e) );
				onElement( e );
			}
		}
		
		StringPair name = getName(exp.getNameClass());

		if( opts.random.nextDouble() < opts.probElemNameTypo ) {
			noteError("element name typo: "+name.localName);
			name = generateTypo(name);
		}
		
		Element child = domDoc.createElementNS( name.namespaceURI, name.localName );
		node.appendChild( child );
		node = child;
		
		// generate children
		depth++;
		exp.contentModel.visit(this);
		depth--;
		
		node = child.getParentNode();
	}
	
	public void onAnyString() {
		node.appendChild( domDoc.createTextNode(opts.dtGenerator.generate(StringType.theInstance,getContext())) );
	}
	
	public void onOneOrMore( OneOrMoreExp exp ) {
		if( opts.random.nextDouble() < opts.probMissingPlus ) {
			noteError("missing " + ExpressionPrinter.printSmallest(exp) );
			return;
		}
		
		int m = opts.width.next()+1;
		if( cutBack() )	m=1;
		for( int i=0; i<m; i++ )
			exp.exp.visit(this);
	}
	
	public void onZeroOrMore( OneOrMoreExp exp ) {
		int m = opts.width.next();
		if( cutBack() )	m=0;
		for( int i=0; i<m; i++ )
			exp.exp.visit(this);
	}
	
	public void onValue( ValueExp exp ) {
		String text;
		if( exp.dt instanceof XSDatatype ) {
			XSDatatype xsd = (XSDatatype)exp.dt;
			text = xsd.convertToLexicalValue(exp.value,getContext());
		} else {
			text = exp.value.toString();
			if(!exp.dt.sameValue( exp.value, exp.dt.createValue(text,getContext()) ) )
				throw new Error("unable to produce a value for the datatype:"+exp.name);
		}
		
		node.appendChild( domDoc.createTextNode(text) );
		return;
	}
	public void onData( DataExp exp ) {
		String value;
		if( exp.dt==com.sun.msv.datatype.xsd.IDType.theInstance ) {
			do {
				value = opts.dtGenerator.generate(NmtokenType.theInstance,getContext());
			}while( ids.contains(value) );
			ids.add(value);
		}
		else
		if( exp.dt.getIdType()==Datatype.ID_TYPE_IDREF
		||  exp.dt.getIdType()==Datatype.ID_TYPE_IDREFS ) {
			Node n = domDoc.createTextNode("{TmpIDRef}");
			node.appendChild(n);
			idrefs.add(n); // memorize this node so that we can patch it later.
			return;
		} else {
			value = opts.dtGenerator.generate(exp.dt,getContext());
		}
		
		node.appendChild( domDoc.createTextNode(value) );
	}
	
	public void onConcur( ConcurExp exp ) {
		throw new Error("concur is not supported");
	}
	
	protected ContextProviderImpl getContext() {
		Node n = node;
		while(!(n instanceof Element) && n!=null) {
			if(n instanceof Attr)
				n = ((Attr)n).getOwnerElement();
			else
				n = n.getParentNode();
		}
		if(n==null)		throw new Error();	// impossible
		return new ContextProviderImpl((Element)n);
	}
	
	/** generaets a name that satisfies given NameClass */
	private StringPair getName( NameClass nc ) {
		StringPair name = opts.nameGenerator.generate(nc);
		
		if( !nc.accepts( name.namespaceURI, name.localName ) )
			throw new Error();	// invalid
		
		return name;
	}
	
	
	/** enumerates children of BinaryExp into a vector. */
	private Vector getChildren( BinaryExp exp ) {
		final Vector vec = new Vector();
		Iterator itr = exp.children();
		while( itr.hasNext() )	vec.add( itr.next() );
		return vec;
	}

	/**
	 * generates 'typo'.
	 */
	protected StringPair generateTypo( StringPair pair ) {
		// in this implementation, typo is made only to localName.
		StringBuffer buf = new StringBuffer(pair.localName);
		
		int idx = opts.random.nextInt(buf.length());
		
		char ch = buf.charAt(idx);
		if( XmlChars.isNCNameChar((char)(ch-1)) )
			ch = (char)(ch-1);
		else
		if( XmlChars.isNCNameChar((char)(ch+1)) )
			ch = (char)(ch+1);
		else
		do
		{
			ch = (char)opts.random.nextInt();
		}while(!XmlChars.isNCNameChar(ch));
		
		buf.setCharAt( idx, ch );
		
		return new StringPair(pair.namespaceURI,buf.toString());
	}
	
}
