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

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.*;
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.tahiti.compiler.XMLWriter;
import com.sun.tahiti.compiler.Symbolizer;
import com.sun.tahiti.grammar.*;
import com.sun.tahiti.reader.TypeUtil;
import org.relaxng.datatype.Datatype;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.text.MessageFormat;

public class RuleFileGenerator implements Symbolizer {
	
	private RuleFileGenerator() {}
	
	/**
	 * 
	 * @param grammar
	 *		Grammar object to be generated as a Java source code.
	 * @param rules
	 *		production rules of the grammar. Actual type must be
	 *		non-terminal -> Rule[].
	 * @param grammarClassName
	 *		the fully-qualified name of the class that is going to be generated.
	 * @param outHandler
	 *		Generated source code will be sent to this handler.
	 * 
	 * @return
	 *		return a symbolizer that is necessary to serialize class definitions.
	 */
	public static Symbolizer generate( Grammar grammar, Map rules, String grammarClassName, DocumentHandler outHandler ) throws SAXException {
		RuleFileGenerator gen = new RuleFileGenerator();
		gen._generate( grammar, rules, grammarClassName, outHandler );
		return gen;
	}
	
	
	/**
	 * this map serves as a dictionary to lookup the name of the given symbol.
	 */
	final Map allNames = new java.util.HashMap();
	public String getId( Object symbol ) {
		if(symbol==null)	return "null";
		String s = (String)allNames.get(symbol);
		assert(s!=null);
		return s;
	}
	
	private void _generate( Grammar grammar, Map rules, String grammarClassName, DocumentHandler outHandler ) throws SAXException {
		
		// add pre-defined special symbols.
		allNames.put( Expression.epsilon, "epsilon" );
		
		try {
			final XMLWriter out = new XMLWriter(outHandler);
			outHandler.setDocumentLocator(new org.xml.sax.helpers.LocatorImpl());
			outHandler.startDocument();
			outHandler.processingInstruction("xml-stylesheet","type='text/xsl' href='../grammarDebug.xslt'");
			out.start("grammar");
			
			{
				int idx = grammarClassName.lastIndexOf('.');
				if(idx<0) {
					out.start("name");
					out.characters(grammarClassName);
					out.end("name");
				} else {
					out.start("package");
					out.characters(grammarClassName.substring(0,idx));
					out.end("package");
					out.start("name");
					out.characters(grammarClassName.substring(idx+1));
					out.end("name");
				}
			}
			
		// collect various primitives (a map to its name)
		//====================================================================
			final Map elements = new java.util.HashMap();	// ElementExps
			final Map attributes = new java.util.HashMap();	// AttributeExps
			final Map datatypes = new java.util.HashMap();	// Datatypes
			final Map classes = new java.util.HashMap();	// ClassItems
			final Map fields = new java.util.HashMap();		// FieldItems
			final Map primitives = new java.util.HashMap();	// PrimitiveItems
			final Map ignores = new java.util.HashMap();	// IgnoreItems
			
			grammar.getTopLevel().visit( new ExpressionWalker(){
				public void onElement( ElementExp exp ) {
					if(!elements.containsKey(exp)) {
						elements.put(exp,computeName(exp.getNameClass(),elements));
						super.onElement(exp);
					}
				}
				public void onAttribute( AttributeExp exp ) {
					if(!attributes.containsKey(exp)) {
						attributes.put(exp,computeName(exp.nameClass,attributes));
						super.onAttribute(exp);
					}
				}
				public void onTypedString( TypedStringExp exp ) {
					if(!datatypes.containsKey(exp)) {
						datatypes.put(exp,computeName(exp.dt,datatypes));
						super.onTypedString(exp);
					}
				}
				public void onOther( OtherExp exp ) {
					if(exp instanceof ClassItem) {
						if(!classes.containsKey(exp)) {
							classes.put(exp,computeName((ClassItem)exp,classes));
							super.onOther(exp);
						}
						return;
					}
					if(exp instanceof PrimitiveItem) {
						if(!primitives.containsKey(exp)) {
							primitives.put(exp,computeName((PrimitiveItem)exp,primitives));
							super.onOther(exp);
						}
						return;
					}
					if(exp instanceof FieldItem) {
						if(!fields.containsKey(exp)) {
							fields.put(exp,computeName((FieldItem)exp,fields));
							super.onOther(exp);
						}
						return;
					}
					if(exp instanceof IgnoreItem) {
						if(!ignores.containsKey(exp)) {
							ignores.put(exp,computeName((IgnoreItem)exp,ignores));
							super.onOther(exp);
						}
						return;
					}
					
					super.onOther(exp);
				}
			});
			
		// assign names to intermediate non-terminals.
		//====================================================================
			
			
			copyAll( elements, "E", allNames );
			copyAll( attributes, "A", allNames );
			copyAll( datatypes, "D", allNames );
			copyAll( classes, "C", allNames );
			copyAll( fields, "N", allNames );
			copyAll( primitives, "P", allNames );
			copyAll( ignores, "Ignore", allNames );
			
			
			final ElementExp[] elms = (ElementExp[])elements.keySet().toArray(new ElementExp[0]);
			final AttributeExp[] atts = (AttributeExp[])attributes.keySet().toArray(new AttributeExp[0]);
			final TypedStringExp[] dts = (TypedStringExp[])datatypes.keySet().toArray(new TypedStringExp[0]);
			final ClassItem[] cis = (ClassItem[])classes.keySet().toArray(new ClassItem[0]);
			final FieldItem[] fis = (FieldItem[])fields.keySet().toArray(new FieldItem[0]);
			final PrimitiveItem[] pis = (PrimitiveItem[])primitives.keySet().toArray(new PrimitiveItem[0]);
			final IgnoreItem[] iis = (IgnoreItem[])ignores.keySet().toArray(new IgnoreItem[0]);
			
			for( int i=0; i<dts.length; i++ ) {
				out.element( "dataSymbol", new String[]{
					"id",(String)allNames.get(dts[i]),
					"type", ((XSDatatype)dts[i].dt).getConcreteType().getName()
					} );
			}
			
			for( int i=0; i<cis.length; i++ ) {
				out.element( "classSymbol", new String[]{
					"id",(String)allNames.get(cis[i]),
					"type",(String)cis[i].getTypeName()
				} );
			}
			
			for( int i=0; i<pis.length; i++ )
				out.element( "primitiveSymbol", new String[]{"id",(String)allNames.get(pis[i])} );
			
			for( int i=0; i<fis.length; i++ )
				out.element( "namedSymbol", new String[]{"id",(String)allNames.get(fis[i])} );
			
			for( int i=0; i<iis.length; i++ )
				out.element( "ignoreSymbol", new String[]{"id",(String)allNames.get(iis[i])} );
			
			{// generate intermediate symbols.
				int cnt=1;
				for( Iterator itr = rules.keySet().iterator(); itr.hasNext(); ) {
					Expression symbol = (Expression)itr.next();
					if(!allNames.containsKey(symbol)) {
						out.element( "intermediateSymbol", new String[]{"id","T"+cnt});
						allNames.put( symbol, "T"+cnt );
						cnt++;
					}
				}
			}
			
			{// write all rules
				int rcounter = 0;
				Iterator itr = rules.keySet().iterator();
				while(itr.hasNext()) {
					Expression nonTerminal = (Expression)itr.next();
					out.start("rules",
						new String[]{"nonTerminal",getId(nonTerminal)});
					
					Rule[] rs = (Rule[])rules.get(nonTerminal);
					for( int j=0; j<rs.length; j++ ) {
						// name this rule.
						allNames.put( rs[j], "r"+(rcounter++) );
						// write this rule
						rs[j].write(out,this);
					}
					
					out.end("rules");
				}
			}
	
		// generate a source code that constructs the grammar.
		//==============================================================
			
			ExpressionSerializer eser = new ExpressionSerializer(this,out);
			
			/*
			visit all elements and attributes to compute the dependency between expressions.
			*/
			for( int i=0; i<atts.length; i++ ) {
				atts[i].exp.visit(eser.sequencer);
				eser.assignId( atts[i] );
				// attributes are serialized just like other particles.
			}
			for( int i=0; i<elms.length; i++ )
				elms[i].contentModel.visit(eser.sequencer);
			
			// ... and don't forget to visit top level expression.
			grammar.getTopLevel().visit(eser.sequencer);
			
			// then obtain the serialization order by creating a map from id to expr.
			java.util.TreeMap id2expr = new java.util.TreeMap();
			for( Iterator itr=eser.sharedExps.iterator(); itr.hasNext(); ) {
				Expression exp = (Expression)itr.next();
				id2expr.put( eser.expr2id.get(exp), exp );
			}
			
			// then serialize shared expressions
			for( Iterator itr=id2expr.keySet().iterator(); itr.hasNext(); ) {
				Integer n = (Integer)itr.next();
				
				Expression exp = (Expression)id2expr.get(n);
				
				if( exp instanceof AttributeExp ) {
					AttributeExp aexp = (AttributeExp)exp;
					out.start( "attributeSymbol", new String[]{"id",(String)allNames.get(aexp)} );
				
					ExpressionSerializer.serializeNameClass(aexp.getNameClass(),out);
					out.start("content");
					aexp.visit(eser.serializer);
					out.end("content");
					
					LLTableCalculator.calc( aexp, rules, grammar.getPool(),this ).write(out,this);
					
					out.end("attributeSymbol");
				} else {
					// other normal particles.
					out.start("particle",new String[]{"id","o"+n});
					exp.visit(eser.serializer);
					out.end("particle");
				}
			}
			
			// elements are serialized at last.
			for( int i=0; i<elms.length; i++ ) {
				out.start("elementSymbol", new String[]{"id",(String)allNames.get(elms[i])} );
			
				ExpressionSerializer.serializeNameClass(elms[i].getNameClass(),out);
				out.start("content");
				elms[i].visit(eser.serializer);
				out.end("content");
				
				LLTableCalculator.calc( elms[i], rules, grammar.getPool(),this ).write(out,this);
				
				out.end("elementSymbol");
			}
			
			if( elements.containsKey(grammar.getTopLevel()) ) {
				// if the top-level expression is element symbol,
				// then we don't need the root grammar.
				out.start("topLevel");
				out.start("content");
				eser.serialize(grammar.getTopLevel());
				out.end("content");
				out.end("topLevel");
			} else {
				// serialize top-level expression
				out.start("topLevel",new String[]{"id",getId(grammar.getTopLevel())});
				out.start("content");
				grammar.getTopLevel().visit(eser.serializer);
				out.end("content");
					LLTableCalculator.calc( grammar.getTopLevel(), rules, grammar.getPool(),this ).write(out,this);
				out.end("topLevel");
			}
			
			
			{// compute the base type of possible top-level classes
				final Set rootClasses = new java.util.HashSet();
				grammar.getTopLevel().visit( new ExpressionWalker(){
					private Set visitedExps = new java.util.HashSet();
					public void onOther( OtherExp exp ) {
						if( exp instanceof TypeItem )
							rootClasses.add(exp);
						// we don't need to parse inside a JavaItem.
					}
					public void onElement( ElementExp exp ) {
						if( visitedExps.add(exp) )
							exp.contentModel.visit(this);
					}
				});
				
				Type rootType = TypeUtil.getCommonBaseType(rootClasses);
				out.element("rootType",
					new String[]{"name", rootType.getTypeName()});
			}
			
			out.end("grammar");
			outHandler.endDocument();
		} catch( XMLWriter.SAXWrapper w ) {
			throw w.e;
		};
	}
	
	/**
	 * copy the source map to the destination map, while adding 
	 * the specified prefix to values.
	 */
	private static void copyAll( Map src, String prefix, Map dst ) {
		for( Iterator itr=src.keySet().iterator(); itr.hasNext(); ) {
			Object symbol = itr.next();
			dst.put( symbol, prefix+src.get(symbol) );
		}
	}
	
	/**
	 * computes a unique name that is used as a field name for
	 * the ElementExp/AttributeExp.
	 * 
	 * @param nc
	 *		name class of that ElementExp/AttributeExp.
	 * @param m
	 *		values of this map are the names of other ElementExp/AttributeExps.
	 *		This method has to return the unique name.
	 */
	private static String computeName( NameClass nc, Map m ) {
		if( nc instanceof SimpleNameClass ) {
			SimpleNameClass snc = (SimpleNameClass)nc;
			if(!m.containsValue(snc.localName))
				return snc.localName;
			
			return getNumberedName( snc.localName, 2, m );
		} else {
			return getNumberedName( "", 0, m );
		}
	}
	
	/**
	 * generate an unique name by concatenating a number to its tail.
	 */
	private static String getNumberedName( String prefix, int count, Map m ) {
		while(m.containsValue( prefix+Integer.toString(count) ))	count++;
			
		return prefix+Integer.toString(count);
	}

	/**
	 * computes a unique name that is used as a field name for
	 * the Datatype.
	 * 
	 * @param dt
	 *		Datatype object.
	 * @param m
	 *		values of this map are the names of other ElementExp/AttributeExps.
	 *		This method has to return the unique name.
	 */
	private static String computeName( Datatype dt, Map m ) {
		if( dt instanceof XSDatatype ) {
			XSDatatype xsdt = (XSDatatype)dt;
			if( xsdt.getName()!=null && !m.containsValue(xsdt.getName()) )
				return xsdt.getName();
			
			return getNumberedName( xsdt.getConcreteType().getName(), 2, m );
		}
		
		return getNumberedName( "", 0, m );
	}
	
	private static String computeName( ClassItem cls, Map m ) {
		String name = cls.getBareName();
		
		if(!m.containsValue(name))		return name;
		return getNumberedName( name, 2, m );
	}

	private static String computeName( PrimitiveItem pitm, Map m ) {
		String name = pitm.type.getBareName();
		
		if(!m.containsValue(name))		return name;
		return getNumberedName( name, 2, m );
	}

	private static String computeName( FieldItem field, Map m ) {
		if(!m.containsValue(field.name))		return field.name;
		return getNumberedName( field.name, 2, m );
	}
	
	private static String computeName( IgnoreItem iim, Map m ) {
		return getNumberedName( "", 1, m );
	}

	
	/**
	 * gets the source code representation of the specified string.
	 */
	private static String javaStringEscape( String s ) {
//		throw new Error();
		return "\""+s+"\"";
	}
	
	
	private static void assert( boolean b ) {
		if(!b)	throw new Error();
	}
}
