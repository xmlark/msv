/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.relax;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.datatype.DataTypeFactory;
import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.grammar.relax.RELAXModule;
import com.sun.tranquilo.grammar.relax.RELAXGrammar;
import com.sun.tranquilo.grammar.relax.EmptyStringType;
import com.sun.tranquilo.grammar.relax.NoneType;
import com.sun.tranquilo.reader.*;
import com.sun.tranquilo.reader.datatype.xsd.FacetState;
import com.sun.tranquilo.reader.datatype.xsd.XSDVocabulary;
import com.sun.tranquilo.util.StartTagInfo;

/**
 * reads RELAX grammar/module by SAX2 and constructs abstract grammar model.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXReader extends GrammarReader
{
	/** loads RELAX grammar */
	public static RELAXGrammar parse( String moduleURL,
		SAXParserFactory factory, GrammarReaderController controller, ExpressionPool pool )
		throws SAXException,ParserConfigurationException
	{
		RELAXReader reader = new RELAXReader(controller,factory,pool);
		reader.guardedParse(moduleURL);
		
		if(reader.hadError)	return null;
		else				return reader.grammar;
	}

	/** loads RELAX grammar */
	public static RELAXGrammar parse( InputSource module,
		SAXParserFactory factory, GrammarReaderController controller, ExpressionPool pool )
		throws SAXException,ParserConfigurationException
	{
		RELAXReader reader = new RELAXReader(controller,factory,pool);
		reader.guardedParse(module);
		
		if(reader.hadError)	return null;
		else				return reader.grammar;
	}
	
	
	protected RELAXReader(
		GrammarReaderController controller,
		SAXParserFactory parserFactory,
		ExpressionPool pool )
		throws SAXException,ParserConfigurationException
	{
		super(controller,parserFactory,pool,new RootState());
		grammar = new RELAXGrammar(pool);
	}
	
	/** Namespace URI of RELAX Core */
	public static final String RELAXCoreNamespace = "http://www.xml.gr.jp/xmlns/relaxCore";
	/** Namespace URI of RELAX Namespace */
	public static final String RELAXNamespaceNamespace = "http://www.xml.gr.jp/xmlns/relaxNamespace";

	protected boolean isGrammarElement( StartTagInfo tag )
	{
		if( !RELAXCoreNamespace.equals(tag.namespaceURI)
		&&  !RELAXNamespaceNamespace.equals(tag.namespaceURI) )
			return false;
		
		// annotation is ignored at this level.
		// by returning false, the entire subtree will be simply ignored.
		if(tag.localName.equals("annotation"))	return false;
		
		return true;
	}
	
	
	public State createExpressionChildState( StartTagInfo tag )
	{
		if(! RELAXCoreNamespace.equals(tag.namespaceURI) )	return null;
		
		if(tag.localName.equals("mixed"))			return new MixedState();
		if(tag.localName.equals("none"))			return new NullSetState();
		if(tag.localName.equals("empty"))			return new EmptyState();
		if(tag.localName.equals("ref"))				return new ElementRefState();
		if(tag.localName.equals("hedgeRef"))		return new HedgeRefState();
		if(tag.localName.equals("choice"))			return new ChoiceState();
		if(tag.localName.equals("sequence"))		return new SequenceState();
		if(tag.localName.equals("element"))			return new InlineElementState();
		if(tag.localName.equals("anyOtherElement"))	return new AnyOtherElementState();
		return null;		// unknown element. let the default error be thrown.
	}
	
	public static FacetState createFacetState( StartTagInfo tag )
	{
		if(! RELAXCoreNamespace.equals(tag.namespaceURI) )	return null;
		
		if( FacetState.facetNames.contains(tag.localName) )	return new FacetState();
		else	return null;
	}
	

	protected Expression interceptExpression( ExpressionState state, Expression exp )
	{
		// handle occurs attribute here.
		final String occurs= state.getStartTag().getAttribute("occurs");
		
		if( state instanceof SequenceState
		||	state instanceof ElementRefState
		||	state instanceof HedgeRefState
		||	state instanceof ChoiceState
		||	state instanceof InlineElementState
		||  state instanceof AnyOtherElementState )
		{// these are the repeatable expressions
			if( occurs!=null )
			{
				if( occurs.equals("?") )	exp = pool.createOptional(exp);
				else
				if( occurs.equals("+") )	exp = pool.createOneOrMore(exp);
				else
				if( occurs.equals("*") )	exp = pool.createZeroOrMore(exp);
				else
					reportError( ERR_ILLEGAL_OCCURS, occurs );
					// recover from error by ignoring this occurs attribute
			}
		}
		else
		{
			if( occurs!=null )
				reportError( ERR_MISPLACED_OCCURS, state.getStartTag().localName );
		}
		return exp;
	}

	protected String localizeMessage( String propertyName, Object[] args )
	{
		String format;
		
		try
		{
			format = ResourceBundle.getBundle("com.sun.tranquilo.reader.relax.Messages").getString(propertyName);
		}
		catch( Exception e )
		{
			format = ResourceBundle.getBundle("com.sun.tranquilo.reader.Messages").getString(propertyName);
		}
		
	    return MessageFormat.format(format, args );
	}
	
	/** RELAX grammar that is currentlt being loaded */
	protected final RELAXGrammar grammar;
	
	/** currently active RELAX module.
	 * 
	 * reference without namespace will be resolved to this module.
	 * declarations will be added to this module.
	 */
	protected RELAXModule currentModule;
	
	/**
	 * adds new module to the currently loading grammar.
	 * 
	 * If the given namespace is already used, error will be issued.
	 */
	protected RELAXModule getOrCreateModule( String namespace )
	{
		// how to detect two modules specifying the same namespace?
		if( grammar.moduleMap.containsKey(namespace) )
			return (RELAXModule)grammar.moduleMap.get(namespace);
		else
		{
			RELAXModule m = new RELAXModule(namespace);
			grammar.moduleMap.put( namespace, m );
			return m;
		}
	}
	
	/**
	 * set of AnyOtherElementState that has to be wraped up after
	 * parsing the entire grammar.
	 * 
	 * AnyOtherElementState adds itself to this set, and RootState will call them.
	 */
	protected final Set pendingAnyOtherElements = new java.util.HashSet();
	
	
	
	/**
	 * resolves reference to a RELAX module.
	 * 
	 * RELAX elements use namespace attribute to designate module.
	 * This method handles this attribute correctly.
	 */
	public RELAXModule resolveModuleReference( StartTagInfo tag )
	{
		String namespace = tag.getAttribute("namespace");
		
		if( namespace!=null )	// reference to the other module
		{
			RELAXModule module = getOrCreateModule( namespace );
			backwardReference.memorizeLink(module,true);	// memorize this reference
			return module;
		}
		else
			return currentModule;
	}
	
	/**
	 * Modules in this set have already been (or is currently being) parsed.
	 * 
	 * RELAXReader has to memorize actually parsed modules so as to
	 * detect references to undeclared namespace.
	 */
	private final Set initializedModules = new java.util.HashSet();
	
	protected void markAsInitialized( RELAXModule module )
	{
		if( initializedModules.contains(module) )
		{	// this module is already initialized
			// that is, two different module elements declare the same target namespace
			reportError( ERR_NAMESPACE_COLLISION, module.targetNamespace );
			
			// declarations in two modules are merged and thus nothing has to be done
			// to recover from error.
			return;
		}
		initializedModules.add(module);
	}
	/** checks if the specified module is marked as initialized. */
	protected boolean isInitializedModule( RELAXModule module )
	{
		return initializedModules.contains(module);
	}
	
	
	
	
	
	
	/** Modules in this set are 'stub' modules.
	 * 
	 * 'stub' module is used to implement those modules with validation='false'.
	 * It doesn't have any definition by itself, but any reference to it is
	 * considered valid.
	 */
	private final Set stubModules = new java.util.HashSet();
														  
	protected void markAsStub( RELAXModule module )
	{
		stubModules.add(module);
	}
	protected boolean isStubModule( RELAXModule module )
	{
		return stubModules.contains(module);
	}
	
	/**
	 * map from type name of Candidate Recommendation to the current type.
	 */
	private static final Map deprecatedTypes = initDeprecatedTypes();
	private static Map initDeprecatedTypes()
	{
		Map m = new java.util.HashMap();
		m.put("uriReference",		com.sun.tranquilo.datatype.AnyURIType.theInstance );
		m.put("decimal",			com.sun.tranquilo.datatype.NumberType.theInstance );
		m.put("timeDuration",		com.sun.tranquilo.datatype.DurationType.theInstance );
		m.put("CDATA",				com.sun.tranquilo.datatype.NormalizedStringType.theInstance );
		return m;
	}
	public DataType getBackwardCompatibleType( String typeName )
	{
		DataType dt = (DataType)deprecatedTypes.get(typeName);
		if( dt!=null )
			reportWarning( WRN_DEPRECATED_TYPENAME, typeName, dt.getName() );
		return dt;
	}
	
	/**
	 * gets DataType object from type name.
	 * 
	 * If undefined type name is specified, this method is responsible
	 * to report an error, and recovers.
	 */
	public DataType resolveDataType( String typeName )
	{
		// look up user defined types first
		DataType dt = (DataType)currentModule.userDefinedTypes.getType(typeName);
		
		if(dt==null)
		{
			dt = getBackwardCompatibleType(typeName);
			if(dt!=null)	return dt;
			
			reportError( ERR_UNDEFINED_DATATYPE, typeName );
			return NoneType.theInstance;	// recover by assuming a valid DataType
		}
		else
			return dt;
	}

	
	protected ExpressionPool getPool()	{ return super.pool; }

	// error message
	public static final String ERR_UNDEFINED_NAMESPACE	// arg:1
		= "RELAXReader.UndefinedNamespace";
	public static final String ERR_NAMESPACE_COLLISION	// arg:1
		= "RELAXReader.NamespaceCollision";
	public static final String ERR_INCONSISTENT_TARGET_NAMESPACE	// arg:2
		= "RELAXReader.InconsistentTargetNamespace";
	public static final String ERR_MISSING_TARGET_NAMESPACE	// arg:0
		= "RELAXReader.MissingTargetNamespace";
	public static final String ERR_ILLEGAL_OCCURS	// arg:1
		= "RELAXReader.IllegalOccurs";
	public static final String ERR_MISPLACED_OCCURS	// arg:1
		= "RELAXReader.MisplacedOccurs";
	public static final String ERR_MULTIPLE_TAG_DECLARATIONS	// arg:1
		= "RELAXReader.MultipleTagDeclarations";
	public static final String ERR_MORE_THAN_ONE_INLINE_TAG	// arg:0
		= "RELAXReader.MoreThanOneInlineTag";
	public static final String ERR_MULTIPLE_ATTPOOL_DECLARATIONS	// arg:1
		= "RELAXReader.MultipleAttPoolDeclarations";
	public static final String WRN_ILLEGAL_RELAXCORE_VERSION	// arg:1
		= "RELAXReader.Warning.IllegalRelaxCoreVersion";
	public static final String WRN_ILLEGAL_RELAXNAMESPACE_VERSION	// arg:1
		= "RELAXReader.Warning.IllegalRelaxNamespaceVersion";
	public static final String LANGUAGE_NOT_SUPPORTED	// arg:0
		= "RELAXReader.LanguageNotSupported";
	public static final String ERR_INLINEMODULE_NOT_FOUND	// arg:0
		= "RELAXReader.InlineModuleNotFound";
	public static final String ERR_UNDEFINED_ELEMENTRULE	// arg:1
		= "RELAXReader.UndefinedElementRule";
	public static final String ERR_UNEXPORTED_ELEMENTRULE	// arg:1
		= "RELAXReader.UnexportedElementRule";
	public static final String ERR_UNDEFINED_HEDGERULE	// arg:1
		= "RELAXReader.UndefinedHedgeRule";
	public static final String ERR_UNEXPORTED_HEDGERULE	// arg:1
		= "RELAXReader.UnexportedHedgeRule";
	public static final String ERR_UNDEFINED_TAG	// arg:1
		= "RELAXReader.UndefinedTag";
	public static final String ERR_UNDEFINED_ATTPOOL	// arg:1
		= "RELAXReader.UndefinedAttPool";
	public static final String ERR_UNEXPORTED_ATTPOOL	// arg:1
		= "RELAXReader.UnexportedAttPool";
	public static final String ERR_LABEL_COLLISION	// arg:1
		= "RELAXReader.LabelCollision";
	public static final String ERR_ROLE_COLLISION	// arg:1
		= "RELAXReader.RoleCollision";
	public static final String ERR_TOPLEVEL_PARTICLE_MUST_BE_RELAX_CORE	// arg:0
		= "RELAXReader.TopLevelParticleMustBeRelaxCore";
	public static final String ERR_NO_EXPROTED_LABEL	// arg:0
		= "RELAXReader.NoExportedLabel";
	public static final String ERR_EXPROTED_HEDGERULE_CONSTRAINT
		= "RELAXReader.ExportedHedgeRuleConstraint";	// arg:1
	public static final String WRN_ANYOTHER_NAMESPACE_IGNORED // arg:1
		= "RELAXReader.Warning.AnyOtherNamespaceIgnored";
	public static final String ERR_MULTIPLE_ATTRIBUTE_CONSTRAINT // arg:1
		= "RELAXReader.MultipleAttributeConstraint";
	public static final String ERR_ID_ABUSE // arg:0
		= "RELAXReader.IdAbuse";
	public static final String ERR_ID_ABUSE_1 // arg:1
		= "RELAXReader.IdAbuse.1";
	public static final String WRN_DEPRECATED_TYPENAME = // arg:2
		"RELAXReader.Warning.DeprecatedTypeName";
}
