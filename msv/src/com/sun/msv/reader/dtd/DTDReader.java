package com.sun.tranquilo.reader.dtd;

import com.sun.tranquilo.datatype.BadTypeException;
import com.sun.tranquilo.datatype.ValidationContextProvider;
import com.sun.tranquilo.datatype.TypeIncubator;
import com.sun.tranquilo.datatype.DataTypeFactory;
import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.datatype.EntityType;
import com.sun.tranquilo.datatype.NmtokenType;
import com.sun.tranquilo.datatype.NormalizedStringType;
import com.sun.tranquilo.datatype.StringType;
import com.sun.tranquilo.reader.GrammarReaderController;
import com.sun.tranquilo.scanner.dtd.DTDEventListener;
import com.sun.tranquilo.scanner.dtd.DTDParser;
import com.sun.tranquilo.scanner.dtd.InputEntity;
import com.sun.tranquilo.grammar.relax.*;
import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.grammar.dtd.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.Locator;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.LocatorImpl;
import java.util.Map;
import java.util.Iterator;
import java.util.Vector;

/**
 * constructs {@link RELAXModule} object that exactly matches to
 * the parsed DTD.
 */
public class DTDReader implements
	DTDEventListener, IDContextProvider {
	
	public DTDReader( GrammarReaderController controller, 
		String targetNamespace, ExpressionPool pool ) {
		this.controller = controller;
		module = new RELAXModule(pool,targetNamespace);
	}
	
	public static RELAXModule parse( InputSource source,
		GrammarReaderController controller ) {
		
		return parse( source, controller, "", new ExpressionPool() );
	}
	
	public static RELAXModule parse( InputSource source,
		GrammarReaderController controller,
		String targetNamespace, ExpressionPool pool ) {
		
		try {
			DTDReader reader = new DTDReader(controller,targetNamespace,pool);
			DTDParser parser = new DTDParser();
			parser.setDtdHandler(reader);
			parser.setEntityResolver(controller);
			parser.parse(source);
		
			if( reader.hadError )	return null;
			else					return reader.module;
		} catch( SAXParseException e ) {
			return null;	// this error was already handled by GrammarReaderController
		} catch( Exception e ) {
			controller.error( new Locator[0], e.getMessage(), e );
			return null;
		}
	}
	
	protected final GrammarReaderController controller;
	
	protected static final Map dtdTypes = createDTDTypes();
	
	protected boolean hadError = false;
	
	/**
	 * creates a map from DTD type name to corresponding
	 * {@link DataType} object.
	 */
	protected static Map createDTDTypes() {
		try {
			Map m = new java.util.HashMap();
		
			m.put( DTDParser.TYPE_CDATA,	NormalizedStringType.theInstance );
			m.put( DTDParser.TYPE_ID,		IDType.theInstance );
			m.put( DTDParser.TYPE_IDREF,	IDREFType.theInstance );
			m.put( DTDParser.TYPE_ENTITY,	EntityType.theInstance );
			m.put( DTDParser.TYPE_ENTITIES,	DataTypeFactory.deriveByList(DTDParser.TYPE_ENTITIES, EntityType.theInstance ) );
			m.put( DTDParser.TYPE_NMTOKEN,	NmtokenType.theInstance );
			m.put( DTDParser.TYPE_NMTOKENS,	DataTypeFactory.deriveByList(DTDParser.TYPE_NMTOKENS, NmtokenType.theInstance ) );
    
			// use string as a base type of enumeration.
			// TODO: confirm whitespace handling of string type is appropriate.
			m.put( DTDParser.TYPE_ENUMERATION, StringType.theInstance );
		
			// also use string as the base type of notation.
			// "NOTATION" type of XML Schema Part 2 validates QName, not Name!
			// so we can't use our NotationTtpe here.
			m.put( DTDParser.TYPE_NOTATION, StringType.theInstance );
		
			return m;
		} catch( BadTypeException e ) {
			// assertion failed. we know these derivations are safe.
			throw new Error();
		}
	}

	/**
	 * map from prefix to vector of possible namespace URI.
	 * default namespace (without prefix) is stored by using "" as a key.
	 */
//	protected final Map namespaces = new java.util.HashMap();
	
	/**
	 * when this value is in the above vector, that indicates
	 * we couldn't detect what URIs are going to be used with that prefix.
	 */
//	protected static final String ABANDON_URI_SNIFFING = "*";
	
	protected NameClass getNameClass( String maybeQName ) {
		return new LocalNameClass( stripPrefix(maybeQName) );
	}
	
	/**
	 * returns local part if the given string is colonalized-name.
	 * otherwise return it without any modification.
	 */
	protected String stripPrefix( String maybeQName ) {
		int idx = maybeQName.indexOf(':');
		if(idx<0)	return maybeQName;	// it wasn't a qname.
		return maybeQName.substring(idx+1);
	}
	
	
	protected final RELAXModule module;

	protected Locator locator;

	protected final Map elementDecls = new java.util.HashMap();
	protected final Map attributeDecls = new java.util.HashMap();
	
	
	
	public void startContentModel( String elementName, short type ) {
		if( contentModel!=null )
			// assertion failed.
			// this must be a bug of DTDScanner.
			throw new Error();
		
		if( type==CONTENT_MODEL_MIXED )
			contentModel = Expression.nullSet;	// initial set up.
		if( type==CONTENT_MODEL_ANY )
			// if ANY is used, then refer to the special hedgeRule.
			contentModel = module.pool.createZeroOrMore(
				module.hedgeRules.getOrCreate("all") );
		if( type==CONTENT_MODEL_EMPTY )
			contentModel = Expression.epsilon;
	}
	
	public void endContentModel( String elementName, short type ) {
		if( contentModel==null )
			// assertion failed.
			// this must be a bug of DTDScanner.
			throw new Error();
		
		switch(type) {
		case CONTENT_MODEL_CHILDREN:
		case CONTENT_MODEL_ANY:
			break;	// do nothing.
		case CONTENT_MODEL_EMPTY:
			contentModel = Expression.epsilon;
			break;
		case CONTENT_MODEL_MIXED:
			if( contentModel == Expression.nullSet )
				// this happens when mixed content model is #PCDATA only.
				contentModel = Expression.epsilon;
			contentModel = module.pool.createMixed(
				module.pool.createZeroOrMore(contentModel));
			break;
		}
		
		// memorize parsed content model.
		elementDecls.put( elementName, contentModel );
		contentModel = null;
	}
	
	/**
	 * processes occurence (?,+,*) of the given expression
	 */
	protected Expression processOccurs( Expression item, short occurence ) {
		switch( occurence ) {
		case OCCURENCE_ONCE:			return item;
		case OCCURENCE_ONE_OR_MORE:		return module.pool.createOneOrMore(item);
		case OCCURENCE_ZERO_OR_MORE:	return module.pool.createZeroOrMore(item);
		case OCCURENCE_ZERO_OR_ONE:		return module.pool.createOptional(item);
		default:		// assertion failed. this must be a bug of DTDScanner.
										throw new Error();
		}
	}
	
	protected class Context {
		final Expression	exp;
		final short		connectorType;
		final Context		previous;
		Context( Context prev, Expression exp, short connector ) {
			this.exp = exp;
			this.connectorType = connector;
			this.previous = prev;
		}
	}
	
	protected Context		contextStack;
	protected Expression	contentModel;
	protected short			connectorType;
	protected final short	CONNECTOR_UNKNOWN = -999;
	
	public void childElement( String elementName, short occurence ) {
		Expression exp = processOccurs(
				module.elementRules.getOrCreate(elementName),
				occurence);
		
		if( connectorType == CONNECTOR_UNKNOWN ) {
			// this must be the first child element within this model group.
			if( contentModel!=null )	throw new Error();
			contentModel = exp;
		} else {
			combineToContentModel(exp);
		}
	}
	
	protected void combineToContentModel( Expression exp ) {
		switch( connectorType ) {
		case CHOICE:
			contentModel = module.pool.createChoice( contentModel, exp );
			break;
		case SEQUENCE:
			contentModel = module.pool.createSequence( contentModel, exp );
			break;
		default:
			// assertion failed. no such connector.
			throw new Error();
		}
	}
	
	public void mixedElement( String elementName ) {
		if( contentModel==null )
			// assertion failed. contentModel must be prepared by startContentModel method.
			throw new Error();
		
		contentModel = module.pool.createChoice( contentModel,
			module.elementRules.getOrCreate(elementName) );
	}
	
	public void startModelGroup() {
		// push context
		contextStack = new Context( contextStack, contentModel, connectorType );
		contentModel = null;
		connectorType = CONNECTOR_UNKNOWN;
	}
	
	public void endModelGroup( short occurence ) {
		Expression exp = processOccurs( contentModel, occurence );
		// pop context
		contentModel = contextStack.exp;
		connectorType = contextStack.connectorType;
		contextStack = contextStack.previous;
		
		if( contentModel==null )
			// this model group is the first item in the parent model group.
			contentModel = exp;
		else
			combineToContentModel(exp);
	}
	
	public void connector( short type ) throws SAXException {
		if( this.connectorType==CONNECTOR_UNKNOWN )
			this.connectorType = type;
		else
		if( this.connectorType!=type )
			// assertion failed.
			// within a model group, operator must be the same.
			throw new Error();
	}
/*
	private Vector getNamespaceVector( String prefix ) {
		Vector v = (Vector)namespaces.get(prefix);
		if(v!=null)		return v;
		v = new Vector();
		namespaces.put(prefix,v);
		return v;
	}
*/	
	/**
	 * this flag is set to true after reporting WRN_ATTEMPT_TO_USE_NAMESPACE.
	 * this is used to prevent issuing the same warning more than once.
	 */
	private boolean reportedXmlnsWarning = false;
	
	public void attributeDecl(
		String elementName, String attributeName, String attributeType,
		String[] enums, short attributeUse, String defaultValue )
		throws SAXException {
		
		if( attributeName.startsWith("xmlns") ) {
			// this is namespace declaration
			
			if( !reportedXmlnsWarning )
				controller.warning( new Locator[0],
					Localizer.localize( WRN_ATTEMPT_TO_USE_NAMESPACE ) );
			reportedXmlnsWarning = true;
/*			
			if( defaultValue==null )
				// we don't have a default value, so no way to determine URI.
				defaultValue = ABANDON_URI_SNIFFING;
			
			Vector v;
			if( attributeName.equals("xmlns") )
				v = getNamespaceVector("");
			else
				v = getNamespaceVector( attributeName.substring(6) );
			
			v.add( defaultValue );
*/			
			// xmlns:* cannot be added to attr constraint expression.
			return;
		}
		
		Expression attList = (Expression)attributeDecls.get(elementName);
		if( attList==null )
			// the first attribute for this element.
			attList = Expression.epsilon;
		
		
		// create DataType that validates attribute value.
		DataType dt = (DataType)dtdTypes.get(attributeType);
		
		try {
			if(enums!=null) {
				TypeIncubator incubator = new TypeIncubator(dt);
				for( int i=0; i<enums.length; i++ )
					incubator.add( DataType.FACET_ENUMERATION, enums[i], false, this );
				dt = incubator.derive(null);
			}
		
			if( attributeUse == USE_FIXED ) {
				// in case of #FIXED, derive a datatype with default value as 
				// an enumeration value.
				TypeIncubator incubator = new TypeIncubator(dt);
				incubator.add( DataType.FACET_ENUMERATION, defaultValue, false, this );
				dt = incubator.derive(null);
			}
		} catch( BadTypeException e ) {
			throw new SAXParseException(
				e.getMessage(), locator, e );
		}
		
		Expression exp = module.pool.createTypedString(dt);
		
		// wrap it by AttributeExp.
		exp = module.pool.createAttribute( getNameClass(attributeName), exp );
		
		// apply attribute use.
		// unless USE_REQUIRED, the attribute is optional.
		if( attributeUse != USE_REQUIRED )
			exp = module.pool.createOptional(exp);
		
		// append it to attribute list.
		attList = module.pool.createSequence( exp, attList );
		attributeDecls.put(elementName,attList);
	}

    public void endDTD() throws SAXException {
		// perform final wrap-up.
		
		// this variable will be the choice of all elements.
		Expression allExp = Expression.nullSet;
		
		// create ElementRules
		Iterator itr = elementDecls.keySet().iterator();
		while( itr.hasNext() ) {
			String elementName = (String)itr.next();
			
			TagClause t = module.tags.getOrCreate(elementName);
			t.nameClass = getNameClass(elementName);
			t.exp = (Expression)attributeDecls.get(elementName);
			if( t.exp==null )
				// this element has no attribute.
				t.exp = Expression.epsilon;
			
			ElementRules er = module.elementRules.getOrCreate(elementName);
			er.addElementRule( module.pool,
				new ElementRule( module.pool, t,
					(Expression)elementDecls.get(elementName) ) );
			allExp = module.pool.createChoice( allExp, er );
		}
		
		// set this allExp as the content model of "all" hedgeRule.
		// this special hedgeRule is used to implement ANY content model,
		// and this hedgeRule is also exported.
		module.hedgeRules.getOrCreate("all").exp = allExp;
		
		// also this allExp is used as top-level expression.
		module.topLevel = allExp;
		
		// check undefined element.
		ReferenceExp[] exps = module.elementRules.getAll();
		for( int i=0; i<exps.length; i++ )
			if( exps[i].exp==null ) {
				// this element is referenced but not defined.
				hadError = true;
				controller.error( new Locator[0],
					Localizer.localize( ERR_UNDEFINED_ELEMENT, new Object[]{exps[i].name} ), null );
			}
	}

	protected Locator[] getLocation( SAXParseException e ) {
		LocatorImpl loc = new LocatorImpl();
		loc.setColumnNumber(e.getColumnNumber());
		loc.setLineNumber(e.getLineNumber());
		loc.setSystemId(e.getSystemId());
		loc.setPublicId(e.getPublicId());
		return new Locator[]{loc};
	}
	
    public void fatalError(SAXParseException e) throws SAXException {
		hadError = true;
		controller.error( getLocation(e), e.getMessage(), null );
    }

    public void error(SAXParseException e) throws SAXException {
		hadError = true;
		controller.error( getLocation(e), e.getMessage(), null );
    }

    public void warning(SAXParseException e) throws SAXException {
		controller.warning( getLocation(e), e.getMessage() );
    }
	
// validation context provider methods
//----------------------------------------
	public boolean isUnparsedEntity( String entityName ) {
		// if this method is called, then it must be from TypeIncubator
		// that adds enumeration value to EntityType.
		
		// TODO: if all DTD declares unparsed entity *before*
		// they are referenced (possibly by default value for ENTITY type),
		// then this method can check whether given name is actually 
		// declared as entity.
		
		// currently, this method always returns true to make 
		// derivation happy.
		return true;
	}
	
	public String resolveNamespacePrefix( String prefix ) {
		// types that relys on QName are not used in DTD.
		// therefore this method shall never be called.
		throw new Error();
	}
	
	// these methods may be also called when adding enumeration values to
	// IDREF types. Just allow anything.
	public void onIDREF( String token ) {}
	public boolean onID( String token ) { return true; }
	
	
// unused methods
//---------------------

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
    }

    public void unparsedEntityDecl(String name, String publicId, 
                                   String systemId, String notationName) throws SAXException {
    }

    public void externalGeneralEntityDecl(String n, String p, String s)  throws SAXException {
    }

    public void internalGeneralEntityDecl (String n, String v) throws SAXException {
    }

    public void externalParameterEntityDecl (String n, String p, String s) throws SAXException {
    }

    public void internalParameterEntityDecl (String n, String v) throws SAXException {
    }

    public void startDTD (InputEntity in) throws SAXException {
    }

    public void comment (String n) throws SAXException {
    }

    public void characters (char ch[], int start, int length) throws SAXException {
    }

    public void ignorableWhitespace (char ch[], int start, int length) throws SAXException {
    }

    public void startCDATA () throws SAXException {
    }

    public void endCDATA () throws SAXException {
    }
	
	public static final String ERR_UNDEFINED_ELEMENT = // arg:1
		"DTDReader.UndefinedElement";
	public static final String WRN_ATTEMPT_TO_USE_NAMESPACE = // arg:0
		"DTDReader.Warning.AttemptToUseNamespace";
}
