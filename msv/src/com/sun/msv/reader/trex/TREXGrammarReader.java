/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Map;
import java.util.Stack;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import com.sun.msv.datatype.DataType;
import com.sun.msv.datatype.StringType;
import com.sun.msv.grammar.*;
import com.sun.msv.grammar.trex.*;
import com.sun.msv.reader.*;
import com.sun.msv.reader.datatype.DataTypeVocabulary;
import com.sun.msv.util.StartTagInfo;
import org.iso_relax.dispatcher.IslandSchema;

/**
 * reads TREX grammar from SAX2 and constructs abstract grammar model.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TREXGrammarReader
	extends GrammarReader
{
	/** loads TREX pattern */
	public static TREXGrammar parse( String grammarURL,
		SAXParserFactory factory, GrammarReaderController controller )
	{
		TREXGrammarReader reader = new TREXGrammarReader(controller,factory);
		reader.parse(grammarURL);
		
		return reader.getResult();
	}
	
	/** loads TREX pattern */
	public static TREXGrammar parse( InputSource grammar,
		SAXParserFactory factory, GrammarReaderController controller )
	{
		TREXGrammarReader reader = new TREXGrammarReader(controller,factory);
		reader.parse(grammar);
		
		return reader.getResult();
	}

	/** easy-to-use constructor. */
	public TREXGrammarReader(
		GrammarReaderController controller,
		SAXParserFactory parserFactory) {
		this(controller,parserFactory,new StateFactory(),new TREXPatternPool());
	}
	
	/** full constructor */
	public TREXGrammarReader(
		GrammarReaderController controller,
		SAXParserFactory parserFactory,
		StateFactory stateFactory,
		TREXPatternPool pool ) {
		
		super(controller,parserFactory,pool,new RootState());
		this.sfactory = stateFactory;
	}
	
	protected String localizeMessage( String propertyName, Object[] args )
	{
		String format;
		
		try
		{
			format = ResourceBundle.getBundle("com.sun.msv.reader.trex.Messages").getString(propertyName);
		}
		catch( Exception e )
		{
			format = ResourceBundle.getBundle("com.sun.msv.reader.Messages").getString(propertyName);
		}
		
	    return MessageFormat.format(format, args );
	}
	
	/** TREX allows either
	 *    (1) the predefined namespace for TREX or
	 *    (2) default namespace ""
	 * 
	 *  as its namespace. This variable holds which namespace is currently in use.
	 */
	protected String currentGrammarURI;
	
	/** Namespace URI of TREX */
	public static final String TREXNamespace = "http://www.thaiopensource.com/trex";

	protected boolean isGrammarElement( StartTagInfo tag )
	{
		if( currentGrammarURI==null )
		{// first time.
			if( tag.namespaceURI.equals(TREXNamespace) )
			{
				currentGrammarURI = TREXNamespace;
				return true;
			}
			if( tag.namespaceURI.equals("") )
			{
				currentGrammarURI = "";
				return true;
			}
			return false;
		}
		else
		{
			if(currentGrammarURI.equals(tag.namespaceURI))	return true;
			if(tag.containsAttribute(TREXNamespace,"role"))	return true;
			
			return false;
		}
	}
	
	/** grammar object currently being loaded. */
	protected TREXGrammar grammar;
	/** obtains parsed grammar object only if parsing was successful. */
	public final TREXGrammar getResult()
	{
		if(hadError)	return null;
		else			return grammar;
	}
	
	/** stack that stores value of ancestor 'ns' attribute. */
	private Stack nsStack = new Stack();
	/** target namespace: currently active 'ns' attribute */
	protected String targetNamespace ="";
	
	
	protected TREXPatternPool getPool() {
		return (TREXPatternPool)super.pool;
	}
	
	
	/**
	 * creates various State object, which in turn parses grammar.
	 * parsing behavior can be customized by implementing custom StateFactory.
	 */
	public static class StateFactory {
		protected State nsName		( State parent, StartTagInfo tag ) { return new NameClassNameState(); }
		protected State nsAnyName	( State parent, StartTagInfo tag ) { return new NameClassAnyNameState(); }
		protected State nsNsName	( State parent, StartTagInfo tag ) { return new NameClassNsNameState(); }
		protected State nsNot		( State parent, StartTagInfo tag ) { return new NameClassNotState(); }
		protected State nsDifference( State parent, StartTagInfo tag ) { return new NameClassDifferenceState(); }
		protected State nsChoice	( State parent, StartTagInfo tag ) { return new NameClassChoiceState(); }

		protected State element		( State parent, StartTagInfo tag ) { return new ElementState(); }
		protected State attribute	( State parent, StartTagInfo tag ) { return new AttributeState(); }
		protected State group		( State parent, StartTagInfo tag ) { return new SequenceState(); }
		protected State interleave	( State parent, StartTagInfo tag ) { return new InterleaveState(); }
		protected State choice		( State parent, StartTagInfo tag ) { return new ChoiceState(); }
		protected State concur		( State parent, StartTagInfo tag ) { return new ConcurState(); }
		protected State optional	( State parent, StartTagInfo tag ) { return new OptionalState(); }
		protected State zeroOrMore	( State parent, StartTagInfo tag ) { return new ZeroOrMoreState(); }
		protected State oneOrMore	( State parent, StartTagInfo tag ) { return new OneOrMoreState(); }
		protected State mixed		( State parent, StartTagInfo tag ) { return new MixedState(); }
		protected State ref			( State parent, StartTagInfo tag ) { return new RefState(); }
		protected State empty		( State parent, StartTagInfo tag ) { return new EmptyState(); }
		protected State anyString	( State parent, StartTagInfo tag ) { return new AnyStringState(); }
		protected State string		( State parent, StartTagInfo tag ) { return new StringState(); }
		protected State data		( State parent, StartTagInfo tag ) { return new DataState(); }
		protected State notAllowed	( State parent, StartTagInfo tag ) { return new NullSetState(); }
		protected State includePattern( State parent, StartTagInfo tag ) { return new IncludePatternState(); }
		protected State includeGrammar( State parent, StartTagInfo tag ) { return new IncludeMergeState(); }
		protected State grammar		( State parent, StartTagInfo tag ) { return new GrammarState(); }
		protected State start		( State parent, StartTagInfo tag ) { return new StartState(); }
		protected State define		( State parent, StartTagInfo tag ) { return new DefineState(); }
	}
	public final StateFactory sfactory;
	
	State createNameClassChildState( State parent, StartTagInfo tag )
	{
		if(tag.localName.equals("name"))		return sfactory.nsName(parent,tag);
		if(tag.localName.equals("anyName"))		return sfactory.nsAnyName(parent,tag);
		if(tag.localName.equals("nsName"))		return sfactory.nsNsName(parent,tag);
		if(tag.localName.equals("not"))			return sfactory.nsNot(parent,tag);
		if(tag.localName.equals("difference"))	return sfactory.nsDifference(parent,tag);
		if(tag.localName.equals("choice"))		return sfactory.nsChoice(parent,tag);
		
		return null;		// unknown element. let the default error be thrown.
	}
	
	private boolean issueObsoletedXMLSchemaNamespace = false;
	/**
	 * maps obsoleted XML Schema namespace to the current one.
	 */
	private String mapNamespace( String namespace ) {
		if(namespace.equals("http://www.w3.org/2000/10/XMLSchema")
		|| namespace.equals("http://www.w3.org/2000/10/XMLSchema-datatypes")) {
			// namespace of CR version.
			if( !issueObsoletedXMLSchemaNamespace )
				// report warning only once.
				reportWarning(WRN_OBSOLETED_XMLSCHEMA_NAMSPACE,namespace);
			issueObsoletedXMLSchemaNamespace = true;
			return com.sun.msv.reader.datatype.xsd.XSDVocabulary.XMLSchemaNamespace;
		}
		return namespace;
	}
	
	public State createExpressionChildState( State parent, StartTagInfo tag )
	{
		if(tag.localName.equals("element"))		return sfactory.element(parent,tag);
		if(tag.localName.equals("attribute"))	return sfactory.attribute(parent,tag);
		if(tag.localName.equals("group"))		return sfactory.group(parent,tag);
		if(tag.localName.equals("interleave"))	return sfactory.interleave(parent,tag);
		if(tag.localName.equals("choice"))		return sfactory.choice(parent,tag);
		if(tag.localName.equals("concur"))		return sfactory.concur(parent,tag);
		if(tag.localName.equals("optional"))	return sfactory.optional(parent,tag);
		if(tag.localName.equals("zeroOrMore"))	return sfactory.zeroOrMore(parent,tag);
		if(tag.localName.equals("oneOrMore"))	return sfactory.oneOrMore(parent,tag);
		if(tag.localName.equals("mixed"))		return sfactory.mixed(parent,tag);
		if(tag.localName.equals("ref"))			return sfactory.ref(parent,tag);
		if(tag.localName.equals("empty"))		return sfactory.empty(parent,tag);
		if(tag.localName.equals("anyString"))	return sfactory.anyString(parent,tag);
		if(tag.localName.equals("string"))		return sfactory.string(parent,tag);
		if(tag.localName.equals("data"))		return sfactory.data(parent,tag);
		if(tag.localName.equals("notAllowed"))	return sfactory.notAllowed(parent,tag);
		if(tag.localName.equals("include"))		return sfactory.includePattern(parent,tag);
		if(tag.localName.equals("grammar"))		return sfactory.grammar(parent,tag);

		final String role = tag.getAttribute(TREXNamespace,"role");
		if("datatype".equals(role)) {
			String namespaceURI = mapNamespace(tag.namespaceURI);
			DataTypeVocabulary v = grammar.dataTypes.get(namespaceURI);
		
			if(v==null) {
				reportError( ERR_UNKNOWN_DATATYPE_VOCABULARY, tag.namespaceURI );
				// put a dummy vocabulary into the map
				// so that user will never receive the same error again.
				grammar.dataTypes.put( tag.namespaceURI, new UndefinedDataTypeVocabulary() );
				return new IgnoreState();	// recover by ignoring this element.
			}			
			
			return v.createTopLevelReaderState(tag);
		}
			
		
		return null;		// unknown element. let the default error be thrown.
	}
	
	/** obtains a named DataType object referenced by a QName.
	 */
	public DataType resolveDataType( String qName )
	{
		String[] s = splitQName(qName);
		if(s==null)
		{
			reportError( ERR_UNDECLEARED_PREFIX, qName );
			// recover by using a dummy DataType
			return StringType.theInstance;
		}
		
		s[0] = mapNamespace(s[0]);	// s[0] == namespace URI
		
		DataTypeVocabulary v = grammar.dataTypes.get(s[0]);
		if(v==null)
		{
			reportError( ERR_UNKNOWN_DATATYPE_VOCABULARY, s[0] );
			// put a dummy vocabulary into the map
			// so that user will never receive the same error again.
			grammar.dataTypes.put( s[0], new UndefinedDataTypeVocabulary() );
		}
		else
		{
			DataType dt = v.getType( s[1] );	// s[1] == local name
			if(dt!=null)	return dt;
			
			reportError( ERR_UNDEFINED_DATATYPE, qName );
		}
		// recover by using a dummy DataType
		return StringType.theInstance;
	}


// SAX event interception
//--------------------------------
	public void startElement( String a, String b, String c, Attributes d ) throws SAXException
	{
		// handle 'ns' attribute propagation
		nsStack.push(targetNamespace);
		if( d.getIndex("ns")!=-1 )
			targetNamespace = d.getValue("ns");
		// if nothing specified, targetNamespace stays the same.
		// for root state, startTag is null.
		
		super.startElement(a,b,c,d);
	}
	public void endElement( String a, String b, String c ) throws SAXException
	{
		super.endElement(a,b,c);
		targetNamespace = (String)nsStack.pop();
	}

	
	
	
	/**
	 * Dummy DataTypeVocabulary for better error recovery.
	 * 
	 * If DataTypeVocabulary is not found, the error is reported
	 * and this class is used to prevent further repetitive error messages.
	 */
	private static class UndefinedDataTypeVocabulary implements DataTypeVocabulary
	{
		public State createTopLevelReaderState( StartTagInfo tag )
		{ return new IgnoreState(); }	// ignore everything
		public DataType getType( String localTypeName )
		{ return StringType.theInstance; }	// accepts any type name

	}

	// error messages
	
	public static final String ERR_MISSING_CHILD_NAMECLASS = // arg:0
		"TREXGrammarReader.MissingChildNameClass";
	public static final String ERR_MORE_THAN_ONE_NAMECLASS = // arg:0
		"TREXGrammarReader.MoreThanOneNameClass";
	public static final String ERR_UNDECLEARED_PREFIX = // arg:1
		"TREXGrammarReader.UndeclaredPrefix";
	public static final String ERR_UNDEFINED_PATTERN = // arg:1
		"TREXGrammarReader.UndefinedPattern";
	public static final String ERR_UNKNOWN_DATATYPE_VOCABULARY = // arg:1
		"TREXGrammarReader.UnknownDataTypeVocabulary";
	public static final String ERR_BAD_COMBINE = // arg:1
		"TREXGrammarReader.BadCombine";
	public static final String ERR_COMBINE_MISSING = // arg:1
		"TREXGrammarReader.CombineMissing";
	public static final String WRN_COMBINE_IGNORED =
		"TREXGrammarReader.Warning.CombineIgnored";
	public static final String WRN_OBSOLETED_XMLSCHEMA_NAMSPACE =
		"TREXGrammarReader.Warning.ObsoletedXMLSchemaNamespace";
	public static final String ERR_DUPLICATE_DEFINITION =
		"TREXGrammarReader.DuplicateDefinition";
	public static final String ERR_NONEXISTENT_PARENT_GRAMMAR =
		"TREXGrammarReader.NonExistentParentGrammar";
	public static final String ERR_INTERLEAVED_STRING =
		"TREXGrammarReader.InterleavedString";
	public static final String ERR_SEQUENCED_STRING =
		"TREXGrammarReader.SequencedString";
	public static final String ERR_REPEATED_STRING =
		"TREXGrammarReader.RepeatedString";
}
