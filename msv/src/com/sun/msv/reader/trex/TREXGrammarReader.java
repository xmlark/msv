package com.sun.tranquilo.reader.trex;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Map;
import java.util.Stack;
import java.io.IOException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.datatype.StringType;
import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.grammar.trex.*;
import com.sun.tranquilo.reader.*;
import com.sun.tranquilo.reader.datatype.DataTypeVocabulary;
import com.sun.tranquilo.util.StartTagInfo;

public class TREXGrammarReader extends GrammarReader
{
	/** loads TREX pattern */
	public static TREXGrammar parse( String grammarURL,
		SAXParserFactory factory, GrammarReaderController controller )
	{
		TREXGrammarReader reader = new TREXGrammarReader(controller,factory,new TREXPatternPool());
		reader._parse(grammarURL,new RootState());
		if(reader.hadError)	return null;
		else				return reader.grammar;
	}
	
	/** loads TREX pattern */
	public static TREXGrammar parse( InputSource grammar,
		SAXParserFactory factory, GrammarReaderController controller )
	{
		TREXGrammarReader reader = new TREXGrammarReader(controller,factory,new TREXPatternPool());
		reader._parse(grammar,new RootState());
		if(reader.hadError)	return null;
		else				return reader.grammar;
	}
	
	protected TREXGrammarReader(
		GrammarReaderController controller,
		SAXParserFactory parserFactory,
		TREXPatternPool pool )
	{
		super(controller,parserFactory,pool);
	}
	
	protected String localizeMessage( String propertyName, Object[] args )
	{
		String format;
		
		try
		{
			format = ResourceBundle.getBundle("com.sun.tranquilo.reader.trex.Messages").getString(propertyName);
		}
		catch( Exception e )
		{
			format = ResourceBundle.getBundle("com.sun.tranquilo.reader.Messages").getString(propertyName);
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
	
	/** stack that stores value of ancestor 'ns' attribute. */
	private Stack nsStack = new Stack();
	/** target namespace: currently active 'ns' attribute */
	protected String targetNamespace ="";
	
	
	// to make them accesible from inner classes
	public void pushState( State newState, StartTagInfo startTag )
	{
		// handle 'ns' attribute propagation
		nsStack.push(targetNamespace);
		if( startTag!=null && startTag.containsAttribute("ns") )
			targetNamespace = startTag.getAttribute("ns");
		// if nothing specified, targetNamespace stays the same.
		// for root state, startTag is null.
		
		super.pushState(newState,startTag);
	}
	public void popState()
	{
		super.popState();
		
		targetNamespace = (String)nsStack.pop();
	}
	protected TREXPatternPool getPool()
	{ return (TREXPatternPool)super.pool; }
	
	
	static NameClassState createNameClassChildState( StartTagInfo tag )
	{
		if(tag.localName.equals("name"))		return new NameClassNameState();
		if(tag.localName.equals("anyName"))		return new NameClassAnyNameState();
		if(tag.localName.equals("nsName"))		return new NameClassNsNameState();
		if(tag.localName.equals("not"))			return new NameClassNotState();
		if(tag.localName.equals("difference"))	return new NameClassDifferenceState();
		if(tag.localName.equals("choice"))		return new NameClassChoiceState();
		
		return null;		// unknown element. let the default error be thrown.
	}
	
	private boolean issueObsoletedXMLSchemaNamespace = false;
	/**
	 * maps obsoleted XML Schema namespace to the current one.
	 */
	private String mapNamespace( String namespace )
	{
		if(namespace.equals("http://www.w3.org/2000/10/XMLSchema")
		|| namespace.equals("http://www.w3.org/2000/10/XMLSchema-datatypes"))
		{// namespace of CR version.
			if( !issueObsoletedXMLSchemaNamespace )
				// report warning only once.
				reportWarning(WRN_OBSOLETED_XMLSCHEMA_NAMSPACE,namespace);
			issueObsoletedXMLSchemaNamespace = true;
			return com.sun.tranquilo.reader.datatype.xsd.XSDVocabulary.XMLSchemaNamespace;
		}
		return namespace;
	}
	
	public State createExpressionChildState( StartTagInfo tag )
	{
		if(tag.localName.equals("element"))		return new ElementState();
		if(tag.localName.equals("attribute"))	return new AttributeState();
		if(tag.localName.equals("group"))		return new SequenceState();
		if(tag.localName.equals("interleave"))	return new InterleaveState();
		if(tag.localName.equals("choice"))		return new ChoiceState();
		if(tag.localName.equals("concur"))		return new ConcurState();
		if(tag.localName.equals("optional"))	return new OptionalState();
		if(tag.localName.equals("zeroOrMore"))	return new ZeroOrMoreState();
		if(tag.localName.equals("oneOrMore"))	return new OneOrMoreState();
		if(tag.localName.equals("mixed"))		return new MixedState();
		if(tag.localName.equals("ref"))			return new RefState();
		if(tag.localName.equals("empty"))		return new EmptyState();
		if(tag.localName.equals("anyString"))	return new AnyStringState();
		if(tag.localName.equals("string"))		return new StringState();
		if(tag.localName.equals("data"))		return new DataState();
		if(tag.localName.equals("notAllowed"))	return new NullSetState();
		if(tag.localName.equals("include"))		return new IncludePatternState();
		if(tag.localName.equals("grammar"))		return new GrammarState();

		final String role = tag.getAttribute(TREXNamespace,"role");
		if("datatype".equals(role))
		{
			String namespaceURI = mapNamespace(tag.namespaceURI);
			
			DataTypeVocabulary v = grammar.dataTypes.get(namespaceURI);
		
			if(v==null)
			{
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
		String[] s = splitNamespacePrefix(qName);
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
}
