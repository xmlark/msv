/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.reader.xmlschema;

import com.sun.tahiti.grammar.AnnotatedGrammar;
import com.sun.tahiti.grammar.ClassItem;
import com.sun.tahiti.reader.TahitiGrammarReader;
import com.sun.tahiti.reader.NameUtil;
import com.sun.tahiti.reader.annotator.Annotator;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.State;
import com.sun.msv.reader.xmlschema.XMLSchemaReader;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.xmlschema.ElementDeclExp;
import com.sun.msv.grammar.xmlschema.ComplexTypeExp;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.util.StartTagInfo;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.xml.parsers.SAXParserFactory;

public class TXMLSchemaReader extends XMLSchemaReader implements TahitiGrammarReader {
	
	public TXMLSchemaReader(
		GrammarReaderController controller,
		SAXParserFactory parserFactory ) {
		super( controller, parserFactory, new StateFactory(), new ExpressionPool() );
	}

	protected final AnnotatedGrammar annGrammar = new AnnotatedGrammar( null, pool );
	public AnnotatedGrammar getAnnotatedResult() {
		return annGrammar;
	}
	
	/**
	 * gets the current XMLSchemaSchema object.
	 */
	protected XMLSchemaSchema getCurrentSchema() {
		return super.currentSchema;
	}
	
	/**
	 * a map from ElementDeclExp to its ClassItem.
	 */
	private final Map elementToClass = new java.util.HashMap();
	protected void addElementClass( ElementDeclExp exp, ClassItem cls ) {
		elementToClass.put( exp, cls );
	}
	protected ClassItem getElementClass( ElementDeclExp exp ) {
		return (ClassItem)elementToClass.get(exp);
	}

	
	/**
	 * computes the name of the generated Java item.
	 */
	protected String computeTypeName( State owner, String type ) {
		
		String packageName = defaultPackageName;
		if( packageName.length()!=0 )	packageName+=".";
		
		// check the t:name attribute first.
		String name = owner.getStartTag().getAttribute(TahitiGrammarReader.TahitiNamespace,"name");
		if(name!=null)	return packageName+name;
		
		// use the name of the element.
		name = owner.getStartTag().getAttribute("name");
		if(name!=null)	return packageName+NameUtil.xmlNameToJavaName(type,name);
		
		// this must be an error.
		return null;
	}
	

	public void wrapUp() {
		// First, let the super class do its job.
		super.wrapUp();
		
		// if we already have an error, abort further processing.
		if(hadError)	return;

		// if no package name is specified, place it to the root pacakge.
		if(annGrammar.grammarName==null)
			annGrammar.grammarName = "Grammar";
		
		// add missing annotations and normalizes them.
		annGrammar.topLevel = grammar.topLevel;
		Annotator.annotate( annGrammar, this );
		grammar.topLevel = annGrammar.topLevel;
	}

	
	
	
	// several State objects are replaced to annotated AGM with Tahiti items.
	public static class StateFactory extends XMLSchemaReader.StateFactory {
		protected State complexTypeDecl		(State parent,StartTagInfo tag)	{ return new TComplexTypeDeclState(); }
		protected State elementDecl			(State parent,StartTagInfo tag)	{ return new TElementDeclState(); }
		protected State any					(State parent,StartTagInfo tag)	{ return new TAnyElementState(); }
		protected State anyAttribute		(State parent,StartTagInfo tag)	{ return new TAnyAttributeState(); }
		
		// complexContent/extension
		protected State complexExt			(State parent,StartTagInfo tag, ComplexTypeExp decl)	{ return new TComplexContentBodyState(decl,true); }
	}



	

	/**
	 * propagatable 't:package' attribute that specifies the default package 
	 * for unqualified java classes/interfaces.
	 */
	private final Stack packageNameStack = new Stack();
	private String defaultPackageName="";
	
	public void startElement( String a, String b, String c, Attributes d ) throws SAXException {
		// handle "t:package" attribute here.
		packageNameStack.push(defaultPackageName);
		if( d.getIndex(TahitiNamespace,"package")!=-1 ) {
			defaultPackageName = d.getValue(TahitiNamespace,"package");
			
			// if this is the first time the package name is specified,
			// then use it for the grammar's name.
			if(annGrammar.grammarName==null)
				annGrammar.grammarName = defaultPackageName+".Grammar";
		}
		
		super.startElement(a,b,c,d);
	}
	public void endElement( String a, String b, String c ) throws SAXException {
		super.endElement(a,b,c);
		defaultPackageName = (String)packageNameStack.pop();
	}

}
