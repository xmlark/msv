/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.xmlschema;

import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.datatype.xsd.BooleanType;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.AnyNameClass;
import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.grammar.xmlschema.ComplexTypeExp;
import com.sun.msv.reader.datatype.xsd.XSDVocabulary;
import com.sun.msv.reader.State;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.SequenceState;
import com.sun.msv.reader.ChoiceState;
import com.sun.msv.reader.InterleaveState;
import com.sun.msv.reader.ExpressionState;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.RunAwayExpressionChecker;
import com.sun.msv.reader.datatype.xsd.FacetState;
import com.sun.msv.reader.datatype.xsd.SimpleTypeState;
import com.sun.msv.reader.datatype.xsd.XSDVocabulary;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringPair;
import javax.xml.parsers.SAXParserFactory;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Map;
import java.util.Iterator;
import java.text.MessageFormat;
import org.xml.sax.Locator;
import org.xml.sax.InputSource;
import org.relaxng.datatype.Datatype;

/**
 * parses XML representation of XML Schema and constructs AGM.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class XMLSchemaReader extends GrammarReader {
	
	/** loads XML Schema */
	public static XMLSchemaGrammar parse( String grammarURL,
		SAXParserFactory factory, GrammarReaderController controller ) {
		
		XMLSchemaReader reader = new XMLSchemaReader(controller,factory);
		reader.parse(grammarURL);
		
		return reader.getResult();
	}
	
	/** loads XML Schema */
	public static XMLSchemaGrammar parse( InputSource grammar,
		SAXParserFactory factory, GrammarReaderController controller ) {
		
		XMLSchemaReader reader = new XMLSchemaReader(controller,factory);
		reader.parse(grammar);
		
		return reader.getResult();
	}
	
	public XMLSchemaReader(
		GrammarReaderController controller,
		SAXParserFactory parserFactory ) {
		this( controller, parserFactory, new StateFactory(), new ExpressionPool() );
	}
	
	public XMLSchemaReader(
		GrammarReaderController controller,
		SAXParserFactory parserFactory,
		StateFactory stateFactory,
		ExpressionPool pool ) {
		
		super(controller,parserFactory,pool,new RootState(stateFactory.schemaHead(null)));
		this.sfactory = stateFactory;
		
		nilExpression = pool.createSequence(
			// TODO: @nil preserves whiteSpace, or not?
			// @nil
			pool.createAttribute(
				new SimpleNameClass(currentSchema.XMLSchemaInstanceNamespace,"nil"),
				pool.createTypedString( BooleanType.theInstance ) ),
			// content must be empty
			Expression.epsilon );
		
		// by putting them into ReferenceExp, # of expressions usually gets smaller
		// because createSequence are right-associative and any attempt to extend
		// this sequence will end up creating new SequenceExps.
		// It's also good for writer: it can generate more compact XML representation.
		ReferenceExp exp = new ReferenceExp( XMLSchemaSchemaLocationAttributes );
		xsiSchemaLocationExp = exp;
		exp.exp =
			pool.createSequence(
			pool.createOptional(
				pool.createAttribute(
					new ChoiceNameClass(
						new SimpleNameClass(currentSchema.XMLSchemaInstanceNamespace,"schemaLocation"),
						new SimpleNameClass(currentSchema.XMLSchemaInstanceNamespace_old,"schemaLocation")
					)
				)
			),
			pool.createOptional(
				pool.createAttribute(
					new ChoiceNameClass(
						new SimpleNameClass(currentSchema.XMLSchemaInstanceNamespace,"noNamespaceSchemaLocation"),
						new SimpleNameClass(currentSchema.XMLSchemaInstanceNamespace_old,"noNamespaceSchemaLocation")
					)
				)
			)
		);
		
		ElementPattern e = new ElementPattern( AnyNameClass.theInstance, Expression.nullSet );
		e.contentModel =
			pool.createMixed(
				pool.createZeroOrMore(
					pool.createChoice(
						pool.createAttribute( AnyNameClass.theInstance ),
						e )));
		complexUrType = e.contentModel;
		
		this.grammar = new XMLSchemaGrammar(pool);
	}
	
	
	/** content model that matches to the use of "nil". */
	public final Expression nilExpression;
	
	/**
	 * content model that matches to
	 * optional xsi:schemaLocation or xsi:noNamespaceSchemaLocation.
	 */
	public final Expression xsiSchemaLocationExp;
	
	public final static String XMLSchemaSchemaLocationAttributes = 
		"____internal_XML_schema_SchemaLocation_attributes";
	
	/**
	 * expression that matches to "ur-type" when used as a complex type.
	 */
	public final Expression complexUrType;
	
	/** value of "attributeFormDefault" attribute. */
	protected String attributeFormDefault;
	/** value of "elementFormDefault" attribute. */
	protected String elementFormDefault;
	
	
	/** grammar object which is being under construction. */
	protected final XMLSchemaGrammar grammar;
	protected XMLSchemaSchema currentSchema;
	
	/**
	 * tables that store all SystemIds that we've read.
	 * 
	 * map from target namespace URI to set of system ids.
	 * This field is used to prevent double inclusion.
	 * 
	 * Strictly speaking, comparision based on system id is not enough.
	 * The spec calls for <i>"the necessity of establishing identity
	 * component by component"</i> (section 4.2.1, last note).
	 */
	public final Map parsedFiles = new java.util.HashMap();
	
	public final XMLSchemaGrammar getResult() {
		if(hadError)	return null;
		else			return grammar;
	}
	public Grammar getResultAsGrammar() {
		return getResult();
	}
	
	/**
	 * gets a reference to XMLSchemaGrammar object whose target namespace 
	 * is the specified one.
	 * 
	 * If there is no such object, this method creates a new instance and
	 * returns it.
	 */
	public XMLSchemaSchema getOrCreateSchema( String namespaceURI ) {
		
		XMLSchemaSchema g = (XMLSchemaSchema)grammar.schemata.get(namespaceURI);
		if(g!=null)		return g;
		
		// create new one.
		g = new XMLSchemaSchema(namespaceURI,grammar);
		grammar.schemata.put(namespaceURI,g);
		
		// memorize the first link so that we can report the source of error
		// if this namespace turns out to be undefined.
		backwardReference.memorizeLink(g);
		
		return g;
	}
	
	/**
	 * creates various State object, which in turn parses grammar.
	 * parsing behavior can be customized by implementing custom StateFactory.
	 */
	public static class StateFactory {
		protected State schemaHead			(String expectedNamespace )	{
			return new SchemaState(expectedNamespace);
		}
		protected State schemaIncluded		(State parent, String expectedNamespace )	{
			return new SchemaIncludedState(expectedNamespace);
		}
		
		protected State simpleType			(State parent,StartTagInfo tag)	{ return new SimpleTypeState(); }
		protected State all					(State parent,StartTagInfo tag)	{ return new InterleaveState(); }
		protected State choice				(State parent,StartTagInfo tag)	{ return new ChoiceState(true); }
		protected State sequence			(State parent,StartTagInfo tag)	{ return new SequenceState(true); }
		protected State group				(State parent,StartTagInfo tag)	{ return new GroupState(); }
		protected State complexTypeDecl		(State parent,StartTagInfo tag)	{ return new ComplexTypeDeclState(); }
		protected State attribute			(State parent,StartTagInfo tag)	{ return new AttributeState(); }
		protected State attributeGroup		(State parent,StartTagInfo tag)	{ return new AttributeGroupState(); }
		protected State elementDecl			(State parent,StartTagInfo tag)	{ return new ElementDeclState(); }
		protected State elementRef			(State parent,StartTagInfo tag)	{ return new ElementRefState(); }
		protected State any					(State parent,StartTagInfo tag)	{ return new AnyElementState(); }
		protected State anyAttribute		(State parent,StartTagInfo tag)	{ return new AnyAttributeState(); }
		protected State include				(State parent,StartTagInfo tag)	{ return new IncludeState(); }
		protected State import_				(State parent,StartTagInfo tag)	{ return new ImportState(); }
		protected State redefine			(State parent,StartTagInfo tag)	{ return new RedefineState(); }
		protected State notation			(State parent,StartTagInfo tag)	{ return new IgnoreState(); }
		protected State facets				(State parent,StartTagInfo tag)	{ return new FacetState(); }

		protected State unique				(State parent,StartTagInfo tag)	{ return new IdentityConstraintState(); }
		protected State key					(State parent,StartTagInfo tag)	{ return new IdentityConstraintState(); }
		protected State keyref				(State parent,StartTagInfo tag)	{ return new IdentityConstraintState(); }
		
		protected State complexContent		(State parent,StartTagInfo tag,ComplexTypeExp decl)	{ return new ComplexContentState(decl); }
		// complexContent/restriction
		protected State complexRst			(State parent,StartTagInfo tag,ComplexTypeExp decl)	{ return new ComplexContentBodyState(decl,false); }
		// complexContent/extension
		protected State complexExt			(State parent,StartTagInfo tag,ComplexTypeExp decl)	{ return new ComplexContentBodyState(decl,true); }

		protected State simpleContent		(State parent,StartTagInfo tag)	{ return new SimpleContentState(); }
		// simpleContent/restriction
		protected State simpleRst			(State parent,StartTagInfo tag)	{ return new SimpleContentBodyState(false); }
		// simpleContent/extension
		protected State simpleExt			(State parent,StartTagInfo tag)	{ return new SimpleContentBodyState(true); }
	}
	
	public final StateFactory sfactory;
	
	public State createExpressionChildState( State parent, StartTagInfo tag ) {
		if(tag.localName.equals("element")) {
			if(tag.containsAttribute("ref"))	return sfactory.elementRef(parent,tag);
			else								return sfactory.elementDecl(parent,tag);
		}
		if(tag.localName.equals("any"))			return sfactory.any(parent,tag);
		
		return createModelGroupState(parent,tag);
	}
	
	/**
	 * creates a state object that parses "all"/"group ref"/"choice" and "sequence".
	 */
	public State createModelGroupState( State parent, StartTagInfo tag ) {
		if(tag.localName.equals("all"))			return sfactory.all(parent,tag);
		if(tag.localName.equals("choice"))		return sfactory.choice(parent,tag);
		if(tag.localName.equals("sequence"))	return sfactory.sequence(parent,tag);
		if(tag.localName.equals("group"))		return sfactory.group(parent,tag);
	
		return null;
	}
	
	/**
	 * creates a state object that parses "attribute","attributeGroup ref", and "anyAttribute".
	 */
	public State createAttributeState( State parent, StartTagInfo tag ) {
		if(tag.localName.equals("attribute"))		return sfactory.attribute(parent,tag);
		if(tag.localName.equals("anyAttribute"))	return sfactory.anyAttribute(parent,tag);
		if(tag.localName.equals("attributeGroup"))	return sfactory.attributeGroup(parent,tag);
	
		return null;
	}

	public State createFacetState( State parent, StartTagInfo tag ) {
		if( FacetState.facetNames.contains(tag.localName) )	return sfactory.facets(parent,tag);
		else	return null;
	}
	
	
	
	/** namespace URI of XML Schema declarations. */
	public static final String XMLSchemaNamespace = "http://www.w3.org/2001/XMLSchema";
	public static final String XMLSchemaNamespace_old = "http://www.w3.org/2000/10/XMLSchema";
	
	private boolean issuedOldNamespaceWarning = false;
	
	protected boolean isGrammarElement( StartTagInfo tag ) {
		
		if(!isSchemaNamespace(tag.namespaceURI))	return false;
		
		// annotation is ignored at this level.
		// by returning false, the entire subtree will be simply ignored.
		if(tag.localName.equals("annotation"))	return false;
		
		return true;
	}

	/** set of XMLSchemaGrammar that is already defined.
	 * XMLSchemaGrammar object is created when it is first referenced or defined.
	 */
	private final Set definedSchemata = new java.util.HashSet();
	public final void markSchemaAsDefined( XMLSchemaSchema schema ) {
		definedSchemata.add( schema );
	}
	public final boolean isSchemaDefined( XMLSchemaSchema schema ) {
		return definedSchemata.contains(schema);
	}
	
	
	
	protected String resolveNamespaceOfAttributeDecl( String formValue ) {
		return resolveNamespaceOfDeclaration( formValue, attributeFormDefault );
	}
	
	protected String resolveNamespaceOfElementDecl( String formValue ) {
		return resolveNamespaceOfDeclaration( formValue, elementFormDefault );
	}
	
	private String resolveNamespaceOfDeclaration( String formValue, String defaultValue ) {
		if( "qualified".equals(formValue) )
			return currentSchema.targetNamespace;
		
		if( "unqualified".equals(formValue) )
			return "";
		
		if( formValue!=null ) {
			reportError( ERR_BAD_ATTRIBUTE_VALUE, "form", formValue );
			return "$$recover$$";	// recovery by returning whatever
		}
		
		return defaultValue;
	}
	
	private XSDVocabulary builtinTypes = new XSDVocabulary();
	
	/**
	 * resolves built-in datatypes (URI: http://www.w3.org/2001/XMLSchema)
	 */
	public Datatype resolveBuiltinDataType( String typeLocalName ) {
		// datatypes of XML Schema part 2
		Datatype dt = builtinTypes.getType(typeLocalName);
		if( dt==null )  dt = getBackwardCompatibleType(typeLocalName);
		if( dt!=null )	return dt;
		
		reportError( ERR_UNDEFINED_DATATYPE, typeLocalName );
		return StringType.theInstance;	// recover by assuming string.
	}
	
	public boolean isSchemaNamespace( String ns ) {
		if( ns.equals(XMLSchemaNamespace) ) return true;
		
		if( ns.equals(XMLSchemaNamespace_old) ) {
			// old namespace.
			// report a warning only once.
			if( !issuedOldNamespaceWarning )
				reportWarning( WRN_OBSOLETED_NAMESPACE, null );
			issuedOldNamespaceWarning = true;
			return true;
		}
		
		return false;
	}
		
	
	public Datatype resolveDataType( String typeQName ) {
		
		String[] r = splitQName(typeQName);
		if(r==null) {
			reportError( ERR_UNDECLARED_PREFIX, typeQName );
			// TODO: implement UndefinedType, that is used only when an error is encountered.
			// it should accept anything and any facets.
			return StringType.theInstance;	// recover by assuming string.
		}
		
		if( isSchemaNamespace(r[0]) )
			return resolveBuiltinDataType(r[1]);
		
		Datatype dt = getOrCreateSchema(r[0]/*uri*/).simpleTypes.
			getOrCreate(r[1]/*local name*/).getType();
		
		if( dt!=null ) return dt;
		
		reportError( ERR_UNDEFINED_OR_FORWARD_REFERENCED_TYPE, r[2]/*qName*/ );
		// TODO: implement error data type.
		return StringType.theInstance;
	}
	
	/**
	 * gets a TypedString expression for the specified datatype.
	 * this method may return a ReferenceExp whose content will be supplied later
	 * (to make forward-reference possible).
	 */
	public Expression resolveDelayedDataType( String qName ) {
		String[] r = splitQName(qName);
		if(r==null) {
			reportError( ERR_UNDECLARED_PREFIX, qName );
			// TODO: implement UndefinedType, that is used only when an error is encountered.
			// it should accept anything and any facets.
			return Expression.nullSet;	// recover by assuming some expression.
		}
		
		if( isSchemaNamespace(r[0]) )
			return pool.createTypedString(
				resolveBuiltinDataType(r[1]),
				new StringPair(r[0],r[1]) );
		
		Expression exp = getOrCreateSchema(r[0]/*uri*/).simpleTypes.getOrCreate(r[1]/*local name*/);
		backwardReference.memorizeLink(exp);
		return exp;
	}
	
	public static interface RefResolver {
		ReferenceContainer get( XMLSchemaSchema schema );
	}
	public Expression resolveQNameRef( StartTagInfo tag, String attName, RefResolver resolver ) {
		
		String refQName = tag.getAttribute(attName);
		if( refQName==null ) {
			reportError( ERR_MISSING_ATTRIBUTE, tag.qName, attName );
			return null;	// failed.
		}
		
		String[] r = splitQName(refQName);
		if(r==null) {
			reportError( ERR_UNDECLARED_PREFIX, refQName );
			return null;
		}
		
		Expression e =  resolver.get( getOrCreateSchema(r[0]/*uri*/) )._getOrCreate(r[1]/*local name*/);
		backwardReference.memorizeLink(e);
		
		return e;
	}

	protected Expression interceptExpression( ExpressionState state, Expression exp ) {
		// process minOccurs/maxOccurs
		if( state instanceof SequenceState
		||  state instanceof ChoiceState
		||  state instanceof InterleaveState
		||  state instanceof AnyElementState
		||  state instanceof ElementDeclState
		||  state instanceof ElementRefState
		||  state instanceof GroupState )
			// TODO: <all/> is limited upto 1
			return processOccurs(state.getStartTag(),exp);
		
		return exp;
	}
	
	public Expression processOccurs( StartTagInfo startTag, Expression item ) {
																				 
		String minOccurs = startTag.getAttribute("minOccurs");
		int minOccursValue=1;
		Expression exp = item;
		
		if( minOccurs!=null ) {
			try {
				minOccursValue = Integer.parseInt(minOccurs);
				if(minOccursValue<0)	throw new NumberFormatException();
			} catch( NumberFormatException e ) {
				reportError( ERR_BAD_ATTRIBUTE_VALUE, "minOccurs", minOccurs );
				minOccursValue = 1;
			}
			
			switch(minOccursValue) {
			case 0:
				exp = Expression.epsilon;
				break;
			case 1:
				break;
			default:
				for( int i=1; i<minOccursValue; i++ )
					exp = pool.createSequence( item, exp );
				break;
			}
		}
		
		String maxOccurs = startTag.getAttribute("maxOccurs");
		if( maxOccurs==null ) {
			// maxOccurs if not present. make sure that minOccurs<=1
			if( minOccursValue>1 )
				reportError( ERR_MAXOCCURS_IS_NECESSARY );
			maxOccurs = "1";
		}
		
		if( maxOccurs.equals("unbounded") ) {
			if( minOccursValue==1 )
				// this will produce a smaller expression.
				// it is also good for TREXWriter and ExpressionPrinter.
				exp = pool.createOneOrMore(item);
			else
				exp = pool.createSequence( exp, pool.createZeroOrMore(item) );
		} else {
			int v;
			try {
				v = Integer.parseInt(maxOccurs);
				if(v<0 || v<minOccursValue)
					throw new NumberFormatException();
			} catch( NumberFormatException e ) {
				reportError( ERR_BAD_ATTRIBUTE_VALUE, "maxOccurs", maxOccurs );
				v = 1;
			}
				
			// create (A,(A, ... (A?)? ... )?
			Expression tmp = Expression.epsilon;
			for( int i=minOccursValue; i<v; i++ )
				tmp = pool.createOptional( pool.createSequence( item, tmp ) );
				
			exp = pool.createSequence( exp, tmp );
		}
		
		return exp;
	}

	protected void switchSource( State sourceState, State newRootState ) {
		final String schemaLocation = sourceState.getStartTag().getAttribute("schemaLocation");

		if(schemaLocation==null) {
			// schemaLocation attribute is required.
			reportError( ERR_MISSING_ATTRIBUTE, sourceState.getStartTag().qName, "schemaLocation" );
			// recover by ignoring this element
		}
		else
			// parse specified file
			switchSource( sourceState, schemaLocation, newRootState );
	}

	
	
	/**
	 * a flag that indicates State objects should check duplicate definitions.
	 * This flag is set to false when in &lt;redefine&gt;. Otherwise this flag is true.
	 */
	public boolean doDuplicateDefinitionCheck = true;
	
	
	
	/**
	 * performs final wrap-up of parsing.
	 * this method is called by RootState after the parsing of the entire documents
	 * has completed.
	 */
	protected void wrapUp() {
		Iterator itr;
		
		// TODO: undefined grammar check.
		Expression grammarTopLevel = Expression.nullSet;
		itr = grammar.schemata.values().iterator();
		while( itr.hasNext() ) {
			XMLSchemaSchema schema = (XMLSchemaSchema)itr.next();
			
			if( !isSchemaDefined(schema) ) {
				reportError(
					backwardReference.getReferer(schema),
					ERR_UNDEFINED_SCHEMA,
					new Object[]{schema.targetNamespace} );
				return;	// surpress excessive error messages.
			}
			
			// detect undefined declarations.
			detectUndefinedOnes( schema.attributeDecls,		ERR_UNDEFINED_ATTRIBUTE_DECL );
			detectUndefinedOnes( schema.attributeGroups,	ERR_UNDEFINED_ATTRIBUTE_GROUP );
			detectUndefinedOnes( schema.complexTypes,		ERR_UNDEFINED_COMPLEX_TYPE );
			detectUndefinedOnes( schema.simpleTypes,		ERR_UNDEFINED_SIMPLE_TYPE );
			detectUndefinedOnes( schema.elementDecls,		ERR_UNDEFINED_ELEMENT_DECL );
			detectUndefinedOnes( schema.groupDecls,			ERR_UNDEFINED_GROUP );
			
			// prepare top-level expression.
			// TODO: make sure this is a correct implementation
			// any globally declared element can be a top-level element.
			Expression exp = Expression.nullSet;
			ReferenceExp[] elems = schema.elementDecls.getAll();
			for( int i=0; i<elems.length; i++ )
				exp = pool.createChoice( exp, elems[i] );
			
			schema.topLevel = exp;
			
			// toplevel of the grammar will be choices of toplevels of all modules.
			grammarTopLevel = pool.createChoice( grammarTopLevel, exp );
		}
		
		grammar.topLevel = grammarTopLevel;


		// perform all back patching.
		//------------------------------
		Locator oldLoc = locator;
		itr = backPatchJobs.iterator();
		while( itr.hasNext() ) {
			BackPatch job = ((BackPatch)itr.next());
			// so that errors reported in the patch job will have 
			// position of its start tag.
			locator = job.getOwnerState().getLocation();
			job.patch();
		}
		locator = oldLoc;
		
		
		if( hadError )	return;
		// undefined expressions may interfare with runaway expression check.
		
		// runaway expression check
		RunAwayExpressionChecker.check( this, grammar.topLevel );
		
	}
	
	
	
	protected String localizeMessage( String propertyName, Object[] args ) {
		String format;
		
		try {
			format = ResourceBundle.getBundle("com.sun.msv.reader.xmlschema.Messages").getString(propertyName);
		} catch( Exception e ) {
			format = ResourceBundle.getBundle("com.sun.msv.reader.Messages").getString(propertyName);
		}
		
	    return MessageFormat.format(format, args );
	}
	
	
	public static final String ERR_MAXOCCURS_IS_NECESSARY =	// arg:0
		"XMLSchemaReader.MaxOccursIsNecessary";
	public static final String ERR_UNIMPLEMENTED_FEATURE =	// arg:1
		"XMLSchemaReader.UnimplementedFeature";
	public static final String ERR_UNDECLARED_PREFIX =	// arg:1
		"XMLSchemaReader.UndeclaredPrefix";
	public static final String ERR_INCONSISTENT_TARGETNAMESPACE =	// arg:2
		"XMLSchemaReader.InconsistentTargetNamespace";
	public static final String ERR_IMPORTING_SAME_NAMESPACE =	// arg:1
		"XMLSchemaReader.ImportingSameNamespace";
	public static final String ERR_DUPLICATE_SCHEMA_DEFINITION =	// arg:1
		"XMLSchemaReader.DuplicateSchemaDefinition";
	public static final String ERR_UNDEFINED_ELEMENTTYPE =	// arg:1
		"XMLSchemaReader.UndefinedElementType";
	public static final String ERR_UNDEFINED_ATTRIBUTE_DECL =
		"XMLSchemaReader.UndefinedAttributeDecl";
	public static final String ERR_UNDEFINED_ATTRIBUTE_GROUP =
		"XMLSchemaReader.UndefinedAttributeGroup";
	public static final String ERR_UNDEFINED_COMPLEX_TYPE =
		"XMLSchemaReader.UndefinedComplexType";
	public static final String ERR_UNDEFINED_SIMPLE_TYPE =
		"XMLSchemaReader.UndefinedSimpleType";
	public static final String ERR_UNDEFINED_ELEMENT_DECL =
		"XMLSchemaReader.UndefinedElementDecl";
	public static final String ERR_UNDEFINED_GROUP =
		"XMLSchemaReader.UndefinedGroup";
	public static final String ERR_UNDEFINED_SCHEMA =
		"XMLSchemaReader.UndefinedSchema";
	public static final String WRN_UNSUPPORTED_ANYELEMENT = // arg:1
		"XMLSchemaReader.Warning.UnsupportedAnyElement";
	public static final String WRN_OBSOLETED_NAMESPACE = // arg:0
		"XMLSchemaReader.Warning.ObsoletedNamespace";
	public static final String ERR_UNDEFINED_OR_FORWARD_REFERENCED_TYPE = //arg:1
		"XMLSchemaReader.UndefinedOrForwardReferencedType";
	public static final String ERR_REDEFINE_UNDEFINED = // arg:1
		"XMLSchemaReader.RedefineUndefined";
	public static final String ERR_DUPLICATE_ATTRIBUTE_DEFINITION = // arg:1
		"XMLSchemaReader.DuplicateAttributeDefinition";
	public static final String ERR_DUPLICATE_COMPLEXTYPE_DEFINITION = // arg:1
		"XMLSchemaReader.DuplicateComplexTypeDefinition";
	public static final String ERR_DUPLICATE_ATTRIBUTE_GROUP_DEFINITION = // arg:1
		"XMLSchemaReader.DuplicateAttributeGroupDefinition";
	public static final String ERR_DUPLICATE_GROUP_DEFINITION = // arg:1
		"XMLSchemaReader.DuplicateGroupDefinition";
	public static final String ERR_DUPLICATE_ELEMENT_DEFINITION = // arg:1
		"XMLSchemaReader.DuplicateElementDefinition";
	public static final String ERR_DUPLICATE_IDENTITY_CONSTRAINT_DEFINITION = // arg:1
		"XMLSchemaReader.DuplicateIdentityConstraintDefinition";
	public static final String ERR_BAD_XPATH = // arg:1
		"XMLSchemaReader.BadXPath";
	public static final String ERR_UNDEFINED_KEY = // arg:1
		"XMLSchemaReader.UndefinedKey";
	public static final String ERR_KEY_FIELD_NUMBER_MISMATCH = // arg:2
		"XMLSchemaReader.KeyFieldNumberMismatch";
	public static final String ERR_KEYREF_REFERRING_NON_KEY = // arg:1
		"XMLSchemaReader.KeyrefReferringNonKey";
	public static final String WRN_IMPLICIT_URTYPE_FOR_ELEMENT = // arg:0
		"XMLSchemaReader.Warning.ImplicitUrTypeForElement";
	public static final String WRN_IMPLICIT_URTYPE_FOR_COMPLEXTYPE = // arg:0
		"XMLSchemaReader.Warning.ImplicitUrTypeForComplexType";
}
