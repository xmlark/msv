/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.ng;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.relaxng.datatype.*;
import com.sun.msv.grammar.*;
import com.sun.msv.grammar.trex.*;
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.msv.grammar.relaxng.datatype.BuiltinDatatypeLibrary;
import com.sun.msv.grammar.relaxng.datatype.CompatibilityDatatypeLibrary;
import com.sun.msv.reader.*;
import com.sun.msv.reader.datatype.xsd.XSDVocabulary;
import com.sun.msv.reader.trex.TREXBaseReader;
import com.sun.msv.reader.trex.AnyStringState;
import com.sun.msv.reader.trex.RootState;
import com.sun.msv.reader.trex.RefState;
import com.sun.msv.reader.trex.NameClassChoiceState;
import com.sun.msv.reader.trex.DivInGrammarState;
import com.sun.msv.reader.trex.IncludePatternState;
import com.sun.msv.reader.datatype.DataTypeVocabulary;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.LightStack;

/**
 * reads RELAX NG grammar from SAX2 and constructs abstract grammar model.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXNGReader extends TREXBaseReader {
	
	/** loads RELAX NG pattern */
	public static TREXGrammar parse( String grammarURL,
		SAXParserFactory factory, GrammarReaderController controller )
	{
		RELAXNGReader reader = new RELAXNGReader(controller,factory);
		reader.parse(grammarURL);
		
		return reader.getResult();
	}
	
	/** loads RELAX NG pattern */
	public static TREXGrammar parse( InputSource grammar,
		SAXParserFactory factory, GrammarReaderController controller )
	{
		RELAXNGReader reader = new RELAXNGReader(controller,factory);
		reader.parse(grammar);
		
		return reader.getResult();
	}

	/** easy-to-use constructor. */
	public RELAXNGReader(
		GrammarReaderController controller,
		SAXParserFactory parserFactory) {
		this(controller,parserFactory,new StateFactory(),new ExpressionPool());
	}
	
	/** full constructor */
	public RELAXNGReader(
		GrammarReaderController controller,
		SAXParserFactory parserFactory,
		StateFactory stateFactory,
		ExpressionPool pool ) {
		
		super( controller, parserFactory, pool, stateFactory, new RootState() );
	}
	
	protected String localizeMessage( String propertyName, Object[] args ) {
		String format;
		
		try {
			format = ResourceBundle.getBundle("com.sun.msv.reader.trex.ng.Messages").getString(propertyName);
		} catch( Exception e ) {
			return super.localizeMessage(propertyName,args);
		}
		
	    return MessageFormat.format(format, args );
	}
	
	protected TREXGrammar getGrammar() {
		return grammar;
	}
	
	/** map from ReferenceExps to RefExpParseInfos. */
	private final Map refExpParseInfos = new java.util.HashMap();
	
	/** gets RefExpParseInfo object for the specified ReferenceExp. */
	protected RefExpParseInfo getRefExpParseInfo( ReferenceExp exp ) {
		RefExpParseInfo r = (RefExpParseInfo)refExpParseInfos.get(exp);
		if(r==null)
			refExpParseInfos.put(exp, r = new RefExpParseInfo());
		return r;
	}
	
	/**
	 * information necessary to correctly parse pattern definitions.
	 */
	protected static class RefExpParseInfo {
		/**
		 * this field is set to true once the head declaration is found.
		 * A head declaration is a define element without the combine attribute.
		 * It is an error that two head declarations share the same name.
		 */
		public boolean haveHead = false;
		
		/**
		 * the combine method which is used to combine this pattern.
		 * this field is set to null if combine attribute is not yet used.
		 */
		public String combineMethod;
		
		public static abstract class RedefinitionStatus {}
		public static RedefinitionStatus notBeingRedefined = new RedefinitionStatus(){};
		public static RedefinitionStatus originalNotFoundYet = new RedefinitionStatus(){};
		public static RedefinitionStatus originalFound = new RedefinitionStatus(){};
		
		/**
		 * current redefinition status.
		 */
		public RedefinitionStatus redefinition = notBeingRedefined;
		
		/**
		 * copy the contents of rhs into this object.
		 */
		public void set( RefExpParseInfo rhs ) {
			this.haveHead = rhs.haveHead;
			this.combineMethod = rhs.combineMethod;
			this.redefinition = rhs.redefinition;
		}
	}
	
	/** Namespace URI of RELAX NG */
	public static final String RELAXNGNamespace = "http://relaxng.org/ns/structure/0.9";

	protected boolean isGrammarElement( StartTagInfo tag ) {
		return RELAXNGNamespace.equals(tag.namespaceURI);
	}
	
	/**
	 * creates various State object, which in turn parses grammar.
	 * parsing behavior can be customized by implementing custom StateFactory.
	 */
	public static class StateFactory extends TREXBaseReader.StateFactory {
		public State nsAnyName	( State parent, StartTagInfo tag ) { return new NGNameState.AnyNameState(); }
		public State nsNsName	( State parent, StartTagInfo tag ) { return new NGNameState.NsNameState(); }
		public State nsExcept	( State parent, StartTagInfo tag ) { return new NameClassChoiceState(); }
		
		public State text			( State parent, StartTagInfo tag ) { return new AnyStringState(); }
		public State data			( State parent, StartTagInfo tag ) { return new DataState(); }
		public State dataParam		( State parent, StartTagInfo tag ) { return new DataParamState(); }
		public State value			( State parent, StartTagInfo tag ) { return new ValueState(); }
		public State list			( State parent, StartTagInfo tag ) { return new ListState(); }
		public State define			( State parent, StartTagInfo tag ) { return new DefineState(); }
		public State start			( State parent, StartTagInfo tag ) { return new StartState(); }
		public State redefine		( State parent, StartTagInfo tag ) { return new DefineState(); }
		public State includeGrammar	( State parent, StartTagInfo tag ) { return new IncludeMergeState(); }
		public State externalRef	( State parent, StartTagInfo tag ) { return new IncludePatternState(); }
		public State divInGrammar	( State parent, StartTagInfo tag ) { return new DivInGrammarState(); }
		public State dataExcept		( State parent, StartTagInfo tag ) { return new ChoiceState(); }
		
		public State ref		( State parent, StartTagInfo tag ) {
			if( tag.containsAttribute("parent") ) {
				// this might be a TREX user...
				// provide an error message.
				parent.reader.reportError( ERR_DISALLOWED_ATTRIBUTE, "ref", "parent");
				return new NullSetState();	// recovery
			}
			return new RefState(false);	// always local reference
		}
		public State parentRef	( State parent, StartTagInfo tag ) {
			return new RefState(true);	// parent reference
		}

		/**
		 * gets DataTypeLibrary object that is specified by the namespace URI.
		 * 
		 * If no vocabulary is known to have that namespace URI, then simply
		 * return null without issuing an error message.
		 * 
		 * It is also possible to throw an exception to indicate
		 * that the resolution was failed.
		 */
		public DatatypeLibrary getDatatypeLibrary( String namespaceURI ) throws Exception {
			// We have the built-in support for XML Schema Part 2.
			if( namespaceURI.equals(XSDVocabulary.XMLSchemaNamespace)
			||  namespaceURI.equals(XSDVocabulary.XMLSchemaNamespace2) ) {
				if(xsdlib==null)
					xsdlib = new com.sun.msv.datatype.xsd.ngimpl.DataTypeLibraryImpl();
				return xsdlib;
			}
			
			// RELAX NG compatibiltiy datatypes library is also supported
			if( namespaceURI.equals(CompatibilityDatatypeLibrary.namespaceURI) ) {
				if( compatibilityLib==null )
					compatibilityLib = new CompatibilityDatatypeLibrary();
				return compatibilityLib;
			}
			
			// check the property file.
			String className;
			try {
				className = ResourceBundle.getBundle("RELAXNGDataTypeLibrary").getString(namespaceURI);
			} catch( java.util.MissingResourceException e ) {
				// our property file doesn't have a field corresponding to the URI.
				return null;
			}
			
			return (DatatypeLibrary)Class.forName(className).newInstance();
		}
		private DatatypeLibrary xsdlib;
		private DatatypeLibrary compatibilityLib;
	}
	protected StateFactory getStateFactory() {
		return (StateFactory)super.sfactory;
	}
	
	State createNameClassChildState( State parent, StartTagInfo tag ) {
		if(tag.localName.equals("name"))		return sfactory.nsName(parent,tag);
		if(tag.localName.equals("anyName"))		return sfactory.nsAnyName(parent,tag);
		if(tag.localName.equals("nsName"))		return sfactory.nsNsName(parent,tag);
		if(tag.localName.equals("choice"))		return sfactory.nsChoice(parent,tag);
		
		return null;		// unknown element. let the default error be thrown.
	}
	
	public State createExpressionChildState( State parent, StartTagInfo tag ) {
		
		if(tag.localName.equals("text"))		return getStateFactory().text(parent,tag);
		if(tag.localName.equals("data"))		return getStateFactory().data(parent,tag);
		if(tag.localName.equals("value"))		return getStateFactory().value(parent,tag);
		if(tag.localName.equals("list"))		return getStateFactory().list(parent,tag);
		if(tag.localName.equals("externalRef"))	return getStateFactory().externalRef(parent,tag);
		if(tag.localName.equals("parentRef"))	return getStateFactory().parentRef(parent,tag);
		
		return super.createExpressionChildState(parent,tag);
	}
	
	/** obtains a named DataType object referenced by a local name. */
	public Datatype resolveDataType( String localName ) {
		
		if(datatypeLib==null)
			// silently recover from an error
			return com.sun.msv.datatype.xsd.StringType.theInstance;
		
		try {
			return datatypeLib.createDatatype(localName);
		} catch( DatatypeException dte ) {
			reportError( ERR_UNDEFINED_DATATYPE_1, localName, dte.getMessage() );
			return com.sun.msv.datatype.xsd.StringType.theInstance;
		}
	}
	
	/**
	 * obtains the DataTypeLibrary that represents the specified namespace URI.
	 * 
	 * If the specified URI is undefined, then this method issues an error to
	 * the user and returns null.
	 */
	public DatatypeLibrary resolveDataTypeLibrary( String uri ) {
		try {
			DatatypeLibrary lib = getStateFactory().getDatatypeLibrary(uri);
			if(lib!=null)		return lib;
		
			// issue an error
			reportError( ERR_UNKNOWN_DATATYPE_VOCABULARY, uri );
		} catch( Throwable e ) {
			reportError( ERR_UNKNOWN_DATATYPE_VOCABULARY_1, uri, e.toString() );
		}
		return null;
	}

	
	
	
// propagatable attributes
//--------------------------------
	/**
	 * currently active datatype library.
	 * This field is maintained by this class.
	 * In case of an error, this field may be set to null after issuing an error.
	 * So care should be taken not to report the same error again.
	 */
	protected DatatypeLibrary datatypeLib = new BuiltinDatatypeLibrary();
	/**
	 * the namespace URI of the currently active datatype library.
	 * The empty string indicates the built-in datatype library.
	 */
	protected String datatypeLibURI = "";
	
	private final LightStack dtLibStack = new LightStack();
	private final LightStack dtLibURIStack = new LightStack();

	public String resolveNamespacePrefix( String prefix ) {
		// In RELAX NG grammar, the default namespace should be resolved 
		// to the current value of the ns attribute.
		if(prefix.equals(""))	return targetNamespace;
		else					return super.resolveNamespacePrefix(prefix);
	}
	
	public void startDocument() throws SAXException {
		// the datatypeLibrary attribute does not do chameleon
		dtLibStack.push(datatypeLib);
		dtLibURIStack.push(datatypeLibURI);
		datatypeLib = new BuiltinDatatypeLibrary();
		datatypeLibURI = "";

		super.startDocument();
	}
	public void endDocument() throws SAXException {
		super.endDocument();
		datatypeLib = (DatatypeLibrary)dtLibStack.pop();
		datatypeLibURI = (String)dtLibURIStack.pop();
	}
	
	public void startElement( String a, String b, String c, Attributes d ) throws SAXException {
		// handle 'datatypeLibrary' attribute propagation
		dtLibStack.push(datatypeLib);
		dtLibURIStack.push(datatypeLibURI);
		if( d.getIndex("datatypeLibrary")!=-1 ) {
			datatypeLibURI = d.getValue("datatypeLibrary");
			datatypeLib = resolveDataTypeLibrary(datatypeLibURI);
		}
		// if nothing specified, datatype library stays the same.
		super.startElement(a,b,c,d);
	}
	public void endElement( String a, String b, String c ) throws SAXException {
		super.endElement(a,b,c);
		datatypeLib = (DatatypeLibrary)dtLibStack.pop();
		datatypeLibURI = (String)dtLibURIStack.pop();
	}
	
	
	
	
	// error messages
	public static final String ERR_BAD_FACET = // arg:2
		"RELAXNGReader.BadFacet";
	public static final String ERR_INVALID_PARAMETERS = // arg:1
		"RELAXNGReader.InvalidParameters";
	public static final String ERR_BAD_DATA_VALUE = // arg:2
		"RELAXNGReader.BadDataValue";
	public static final String ERR_UNDEFINED_KEY = // arg:1
		"RELAXNGReader.UndefinedKey";
	public static final String ERR_UNDEFINED_DATATYPE_1 = // arg:2
		"RELAXNGReader.UndefinedDataType1";
	public static final String ERR_INCONSISTENT_KEY_TYPE = // arg:1
		"RELAXNGReader.InconsistentKeyType";
	public static final String ERR_INCONSISTENT_COMBINE = // arg:1
		"RELAXNGReader.InconsistentCombine";
	public static final String ERR_REDEFINING_UNDEFINED = // arg:1
		"RELAXNGReader.RedefiningUndefined";
	public static final String ERR_UNKNOWN_DATATYPE_VOCABULARY_1 = // arg:2
		"RELAXNGReader.UnknownDatatypeVocabulary1";
	public static final String ERR_MULTIPLE_EXCEPT = // arg:0
		"RELAXNGReader.MultipleExcept";
}
