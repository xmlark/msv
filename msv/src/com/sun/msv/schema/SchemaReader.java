/*
 * Tranquilo : RELAX Verifier           written by Kohsuke Kawaguchi
 *                                           k-kawa@bigfoot.com
 *
 * Copyright 2000 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.sun.tranquilo.schema;

import jp.gr.xml.formal.automaton.Automaton;
import jp.gr.xml.formal.automaton.AutomatonFactory;
import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.datatype.DataTypeFactory;
import com.sun.tranquilo.datatype.IllegalFacetException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import java.util.Hashtable;
import java.util.Vector;
import java.io.IOException;

/**
 * Base class of ModuleReader and GrammarReader
 */
abstract class SchemaReader extends org.xml.sax.helpers.DefaultHandler
{	
	/** object that will receive parsed information */
	public final Schema schema;
	
	/** namespace URI of RELAX schema.
	 *
	 * This will be "http://www.xml.gr.jp/xmlns/relaxCore" while parsing RELAX modules,
	 * and "http://www.xml.gr.jp/xmlns/relaxNamespace" while parsing RELAX grammars.
	 */
	protected final String relaxNamespace;

	protected Locator locator;
	public void setDocumentLocator( Locator loc ) { this.locator = loc; }
		
	protected SchemaReader( Schema schema, String relaxNamespace )
	{
		this.schema = schema;
		this.relaxNamespace = relaxNamespace;
	}
	
	
	
	
//
// stack of State objects and related services
//============================================================
	private XMLReader parser;
	
	public String getSystemId() { return locator.getSystemId(); }
	
	/** pushs the current state into the stack and sets new one */
	protected void pushState( State newState, XMLElement startTag )
		throws SchemaParseException
	{
		newState.init( (State)parser.getContentHandler(), startTag );
		// ASSERT : parser.getContentHandler()==newState.parentState
		parser.setContentHandler(newState);
	}
	/** pops the previous state from the stack */
	protected void popState()
	{
		State currentState = (State)parser.getContentHandler();
		parser.setContentHandler( currentState.parentState );
	}
	
	/**
	 * creates appropriate State object that'll parses children of given XML element.
	 * 
	 * For example, if tag name of XML element is "hedgeRule", implementation
	 * should provide a State object that can parse the content of hedgeRule
	 * (e.g., parses "annotation" and one hedge model).
	 * 
	 * @param parentModel
	 *		the current ModelState object. This will be the parent state for
	 *		new State object.
	 * @param startTag
	 *		information of start tag. Implementation uses this information
	 *		to determine what appropriate State object will be.
	 * 
	 * @exception SchemaParseException
	 *		the implementation should throw this exception if it cannot recognize
	 *		given element. DO NOT return null.
	 */
//	protected State createChildState( ModelState parentModel, XMLElement startTag )
//		throws SchemaParseException
//	{
//		
//	}
	
	protected Hashtable topLevelDeclarationStates = initializeTopLevelDeclarationStates();
	protected Hashtable initializeTopLevelDeclarationStates()
	{
		Hashtable table = new Hashtable();
		table.put( "hedgeRule", new HedgeRuleState() );
		table.put( "tag",		new TagState() );
		return table;
	}
	
	protected Hashtable particleDeclarationStates = initializeParticleDeclarationStates();
	protected Hashtable initializeParticleDeclarationStates()
	{
		Hashtable table = new Hashtable();
//		"mixed" shouldn't be here because it is *NOT* a particle.
//		table.put( "mixed",		new MixedState() );
		table.put( "none",		new NoneState() );
		table.put( "empty",		new EmptyState() );
		table.put( "ref",		new ElementRefState() );
		table.put( "hedgeRef",	new HedgeRefState() );
		table.put( "choice",	new ChoiceState() );
		table.put( "sequence",	new SequenceState() );
		return table;
	}
	
	
	public void parse( XMLReader parser, InputSource source )
		throws SchemaParseException
	{
		_parse(parser,source);
	}
	public void parse( XMLReader parser, String source )
		throws SchemaParseException
	{
		_parse(parser,source);
	}
	private void _parse( XMLReader parser, Object source )
		throws SchemaParseException
	{	
		this.parser = parser;
		try
		{
			if( source instanceof InputSource )		parser.parse((InputSource)source);
			if( source instanceof String )			parser.parse((String)source);
		}
		catch( IOException e )
		{
			SchemaParseException.raise( null,
				SchemaParseException.ERR_IO_EXCEPTION, e.getMessage() );
		}
		catch( SchemaParseException e )
		{
			throw e;
		}
		catch( SAXException e )
		{
			SchemaParseException.raise( null,
				SchemaParseException.ERR_SAX_EXCEPTION, e.getMessage() );
		}
	}
	
	
	public void startElement( String namespaceUri, String localName, String qName, Attributes attributes )
		throws SchemaParseException
	{// this method will be called only once when parser reads document element
		pushState(createRootState(),new XMLElement(localName,qName,attributes,locator));
	}
	
	protected abstract State createRootState();
	
	/** base interface of parsing states
	 * 
	 * this class provides:
	 * <ul>
	 *  <li>access to the parent state
	 *  <li>handling for all ContentHandler callbacks except startElement and endElement
	 * </ul>
	 * 
	 * <p>
	 * State objects are used in stack.
	 */
	protected abstract class State implements ContentHandler
	{
		/** parent state of this state  */
		protected State parentState;
				
		/** information of the start tag */
		protected XMLElement startTag;
		
		protected final void init( State parentState, XMLElement startTag )
			throws SchemaParseException
		{
			this.parentState = parentState;
			this.startTag = startTag;
			startSelf();
		}
		
		/** performs task that should be done before reading any child elements.
		 * 
		 * derived-class can safely read startTag and/or parentState values.
		 */
		void startSelf( ) throws SchemaParseException {}
		
		public void characters(char[] buffer, int from, int len )
			throws SchemaParseException
		{
			SchemaParseException.raise( locator,
				SchemaParseException.ERR_PCDATA_NOT_ALLOWED, null );
		}
		public void processingInstruction( String target, String data ) {}
		public void startDocument() {}
		public void endDocument() {}
		public void ignorableWhitespace(char[] buffer, int from, int len ) {}
		public void skippedEntity( String name ) {}
		public void startPrefixMapping(String prefix, String uri ) {}
		public void endPrefixMapping(String prefix) {}
		// TODO : make sure that we can ignore it.
		public void setDocumentLocator( Locator loc ) {}
	}
	
	/**
	 * state that ignores the entire subtree.
	 */
	protected class IgnoreState extends State
	{
		public void startElement( String uri, String localName, String qName, Attributes attributes )
		{
			nestLevel++;
		}
		
		public void endElement( String uri, String localName, String qName )
		{
			if( nestLevel==0 )
			{// all the subtree are parsed. return to the parent
				popState();
				return;
			}
			nestLevel--;
		}
		
		private int nestLevel = 0;
	}

	/**
	 * state for parsing Model-derived objects
	 * 
	 * ModelState builds Model by looking at
	 * <ol>
	 *  <li>XML element itself
	 *  <li>Model objects built by child XML element.
	 * </ol>
	 * 
	 * In default implementation of endChild, no children is allowed.
	 */
	protected abstract class ModelState extends State
	{
		/** obtain an Object that is parsed by this state
		 * 
		 * this method is accessible only from endChild method of the parent state.
		 * Two different call to getResult can return the same object or the different
		 * object.
		 */
		protected abstract Object getResult()
			throws SchemaParseException;
		
		public void startElement( String uri, String localPart, String qName, Attributes attributes )
			throws SchemaParseException
		{
			final XMLElement e = new XMLElement(localPart,qName,attributes,locator);
			// makes sure that this element belongs to the proper namespace
			if( !uri.equals(relaxNamespace) )
				SchemaParseException.raise( e,
					SchemaParseException.ERR_UNRECOGNIZED_ELEMENT, qName );
			
			// call SchemaReader.createChildState to create
			// appropriate child state for this element
			final State childState = createChildState( e );
			if( childState==null )
				SchemaParseException.raise( e,
					SchemaParseException.ERR_MALPLACED_ELEMENT, e.tagName );
			
			pushState( childState, e );	// leave the control to the child state
		}

		/**
		 * creates appropriate State object that'll parses given child element
		 * 
		 * For example, if tag name of XML element is "hedgeRule", implementation
		 * should provide a State object that can parse the content of hedgeRule.
		 * 
		 * @param startTag
		 *		information of start tag. Implementation uses this information
		 *		to determine what appropriate State object will be.
		 *		
		 *		before calling this method, call has already checked that namespace URI
		 *		is correct. Thus implementation can skip this check.
		 * 
		 * @return null
		 *		if element is not recognized. If this is the case,
		 *		caller throws an exception (ERR_MALPLACED_ELEMENT).
		 * 
		 * @throws SchemaParseException
		 *		If implementation can provide better error message,
		 *		it can throw this exception.
		 */
		protected State createChildState( XMLElement startTag ) throws SchemaParseException
		{// default implementation: just skip annotation.
			if( startTag.tagName.equals("annotation") )	return new IgnoreState();
			return null;
		}
		
		public void endElement( String uri, String localPart, String qName )
			throws SchemaParseException
		{
			endSelf();					// performs wrapping-up operation
			
			// report the completion to parent state.
			// note that parent state is always ModelState
			((ModelState)parentState).endChild(this);
			
			popState();					// leave the control to the parent state
		}
		
		/**
		 * performs final operation necessary to build Model object.
		 * 
		 * This method is called when corresponding endElement is encountered.
		 * After returning from this method, getResult method must be available.
		 * 
		 * This is a chance for implementation to instanciate actual Model-derived
		 * object from gathered information.
		 */
		protected void endSelf() throws SchemaParseException {}

		/**
		 * receives notification of the completion of child ModelState
		 */
		protected void endChild( ModelState childState )
			throws SchemaParseException
		{
			// default implementation : throw error if a child is found
			SchemaParseException.raise( childState.startTag,
				SchemaParseException.ERR_CANNOT_HAVE_CONTENT,
				startTag.tagName );
		}
	}
	
	/**
	 * State that is capable of skipping "div" elements.
	 */
	protected abstract class DivSkipState extends ModelState
	{
		public void startElement( String uri, String localPart, String qName, Attributes attributes )
			throws SchemaParseException
		{
			if( localPart.equals("div") && uri.equals(relaxNamespace) )
			{
				divNestLevel++;
				return;
			}
			// annotation will be handled within createChildModel element
			
			super.startElement( uri, localPart, qName, attributes );
		}
		
		public void endElement( String uri, String localPart, String qName )
			throws SchemaParseException
		{
			if( divNestLevel>0 )
			{// this is endElement for "div" element. so simply ignore it.
				divNestLevel--;
				return;
			}
			// otherwise delegate to the default implementation
			super.endElement( uri, localPart, qName );
		}
		
		/** counts nest level of "div" element */
		private int divNestLevel = 0;
	}
	
	/**
	 * State that parses top-level elements ("grammar" or "module").
	 * 
	 * This state is capable of skipping "div" elements.
	 */
	protected abstract class DocumentElementState extends DivSkipState
	{
		protected State createChildState( XMLElement startTag ) throws SchemaParseException
		{// default implementation: just skip annotation.
			State s = super.createChildState(startTag);
			if( s!=null )	return s;
			return (State)topLevelDeclarationStates.get(startTag.tagName);
		}
	}
	
	protected abstract class HedgeModelParentState extends ModelState
	{
		/** child HedgeModel object.
		 * 
		 * this class allows one and only one child
		 */
		private HedgeModel	childModel = null;
		
		protected HedgeModel getChildModel()
			throws SchemaParseException
		{
			if( childModel==null )	// error: no child specified
				SchemaParseException.raise( startTag,
					SchemaParseException.ERR_NO_PARTICLE, startTag.tagName );
			return childModel;
		}
		
		protected void endChild( ModelState childState )
			throws SchemaParseException
		{
			if( childModel==null )
			{// store this model as childModel
				// TODO : what if childState is not a HedgeModel?
				childModel = (HedgeModel)childState.getResult();
				return;
			}
			else
			{// error: only one child is allowed.
				SchemaParseException.raise( childState.startTag,
					SchemaParseException.ERR_MORE_THAN_ONE_PARTICLE, startTag.tagName );
			}
		}

		protected final State createChildState( XMLElement startTag )
		{
			return (State)particleDeclarationStates.get(startTag.tagName);
		}
	}
	
	/** parses 'mixed' element */
	protected class MixedState extends HedgeModelParentState implements HedgeModelState
	{
		public Object getResult()	throws SchemaParseException
		{ return getHedgeModel(); }
		
		public HedgeModel getHedgeModel()
			throws SchemaParseException
		{
			final Model child = getChildModel();
			if(!(child instanceof Particle))
				SchemaParseException.raise( startTag,
					SchemaParseException.ERR_NON_PARTICLE_IN_MIXED, null );
			
			return new Mixed( (Particle)child );
		}
	}
	
	/** parses HedgeModel */
	protected class HedgeRuleState extends HedgeModelParentState
	{
		protected Object getResult()
			throws SchemaParseException
		{
			final Model child = getChildModel();
			if(!(child instanceof Particle))
				SchemaParseException.raise( startTag,
					SchemaParseException.ERR_NON_PARTICLE_IN_HEDGERULE, null );
			
			return new HedgeRuleX(
				startTag.getRequiredAttribute( "label" ),
				(Particle)child );
		}
	}
	
	/** parses 'elementRule' : base part
	 * 
	 * This class performs basic part of parsing ElementRule.
	 * This class recognized its contents as follows.
	 * 
	 * <sequence>
	 *   <ref label="annotation" occurs="?" />	: skipped
	 *   <ref label="tag" occurs="?" />			: depends on the presence of role attribute
	 *   
	 *   <hedgeRef />	: derived-class dependent content model.
	 * </sequence>
	 */
	protected abstract class ElementRuleState extends ModelState
	{
		protected String labelName;
		protected Clauses clauses;
		protected HedgeModel content;
		
		/**
		 * datatype specified with type attribute.
		 * 
		 * For elementRule without type attribute, this variable holds null
		 */
		protected DataType baseType;
		
		protected FacetReader facets;
		
		/** indicates what is expected next. */
		private int parseState;
		
		// constant values for parseState variable
		/** next child should be "tag" */
		private final int TAG_EXPECTED		= 1;	
		/** next child should be facets (if baseType!=null) or hedge model (if baseType==null) */
		private final int CONTENT_EXPECTED	= 2;
		/** nothing expected. If any child appears, it is an error */
		private final int NOTHING_EXPECTED	= 3;
		
		protected void startSelf()
			throws SchemaParseException
		{
			super.startSelf();
			
			final String roleName = startTag.getOptionalAttribute( "role", null );
			
			if( roleName==null )
			{
				labelName = startTag.getRequiredAttribute("label");
				parseState = TAG_EXPECTED;	// local tag declaration is mandatory
			}
			else
			{// if role is present, label defaults to it.
				labelName = startTag.getOptionalAttribute("label",roleName);
				parseState = CONTENT_EXPECTED;	// local tag declaration is prohibited
				
				// clause should be a named one.
				clauses = schema.clauses.getOrCreate(roleName);
			}
			
			if( startTag.hasAttribute("type") )
			{// value of baseType is used later in endChild method.
				baseType = DataTypeFactory.getTypeByName( startTag.getAttribute("type") );
				if( baseType==null )
					SchemaParseException.raise( startTag,
						SchemaParseException.ERR_UNDEFINED_DATATYPE, startTag.getAttribute("type") );
				facets = new FacetReader();
			}
			else
			{
				baseType = null;
				facets = null;
			}
		}
		
		protected final void endChild( ModelState childState )
			throws SchemaParseException
		{
			switch( parseState )
			{
			case TAG_EXPECTED:
				if(!(childState instanceof TagState ))
				{// no local tag specified.
					// report this error as "missing role attribute for elementRule".
					SchemaParseException.raise( startTag,
						SchemaParseException.ERR_MISSING_ATTRIBUTE, "elementRule", "role" );
				}
					
				// make anonymous Clauses for this local tag
				clauses = schema.clauses.createAnonymous();
				clauses.add( (Tag)childState.getResult() );
				
				// next element should be a derived-class dependent content.
				parseState = CONTENT_EXPECTED;
				return;
				
			case CONTENT_EXPECTED:
				if( baseType==null )
				{//  expect content model
					if(!(childState instanceof HedgeModelState ))
						SchemaParseException.raise( childState.startTag,
							SchemaParseException.ERR_MALPLACED_ELEMENT, childState.startTag.qName );
					
					content = ((HedgeModelState)childState).getHedgeModel();
					parseState = NOTHING_EXPECTED;	// parsing finished. further elements are considered as errors
				}
				else
				{// expect facets
					if(!(childState instanceof FacetState ))
					{// TODO : provide better error message. @type or content model.
						SchemaParseException.raise( childState.startTag,
							SchemaParseException.ERR_MALPLACED_ELEMENT, childState.startTag.qName );
					}
					
					facets.add( (FacetState)childState );
				}
				break;
				
			case NOTHING_EXPECTED:
				// error. but examine further to provide user-friendly error message.
					
				// ASSERT : baseType==null
				
				if( childState instanceof ParticleState )
					SchemaParseException.raise( childState.startTag,
						SchemaParseException.ERR_MORE_THAN_ONE_PARTICLE, "elementRule" );
				
				SchemaParseException.raise( childState.startTag,
					SchemaParseException.ERR_MALPLACED_ELEMENT, childState.startTag.qName );
				
			default:
				// ASSERT : false
				throw new IllegalStateException();
			}
		}
		
		protected final State createChildState( XMLElement startTag )
			throws SchemaParseException
		{
			State s = super.createChildState(startTag);
			if(s!=null)		return s;
			
			if( startTag.tagName.equals("tag") )	return new TagState();
			else									return new FacetState();
			
			// other unknown element is simply assumed as a facet
		}
		
		protected Object getResult()
			throws SchemaParseException
		{
			if( baseType!=null )
			{// returns a typed element rule
				DataType dt = baseType;
				Hashtable f = facets.getFacets();
				if( f.size()!=0 )
					try
					{
						dt = baseType.derive(null,f);	// derives anonymous datatype
					}
					catch( IllegalFacetException e )
					{
						SchemaParseException.raise( startTag, e );
					}
				
				return new ElementRule( labelName, clauses, new TypedText(dt) );
			}
			else
			{// returns a element rule with content model
				return new ElementRule( labelName, clauses, content );
			}
		}
	}
	
	/** reads and stores a sequence of facets. */
	protected class FacetReader
	{
		protected void add( FacetState state )
			throws SchemaParseException
		{
			final String facetName	= state.facetName;
			final String facetValue = state.facetValue;
				
			if( facetName.equals("enumeration") )
			{// enumeration is handled differently
				Vector enums = (Vector)facets.get("enumeration");
				if( enums==null )
					facets.put("enumeration", enums = new Vector() );
					
				enums.addElement(facetValue);
			}
			else
			{
				// prohibits multiple specification
				if( facets.containsKey(facetName) )
					SchemaParseException.raise( state.startTag,
						SchemaParseException.ERR_DUPLICATE_FACET, facetName );
					
				facets.put( facetName, facetValue );
			}
		}
		
		/** stores all specified facets in the form of map from facet name to value.
		 * 
		 * enumeration facet is treated differently and stored in Vector.
		 */
		private final Hashtable facets = new Hashtable();
		
		protected Hashtable getFacets() { return facets; }
	}
	
	protected class FacetState extends ModelState
	{
		protected void startSelf() throws SchemaParseException
		{
			super.startSelf();
			
			facetName	= startTag.tagName;
			facetValue	= startTag.getRequiredAttribute("value");
		}
		
		protected String facetName;
		protected String facetValue;
		
		protected Object getResult()
		{// this state does not implement getResult
			throw new IllegalStateException();
		}
	}
	
	/**
	 * State that returns HedgeModel object as its parsing result
	 * 
	 * State classes that parses HedgeModel-derived object must implement this
	 * interface.
	 */
	protected interface HedgeModelState
	{
		HedgeModel getHedgeModel()
			throws SchemaParseException;
	}
	
	/**
	 * State that returns Particle object as its parsing result.
	 */
	protected abstract class ParticleState extends ModelState implements HedgeModelState
	{
		protected abstract Particle getParticle()	throws SchemaParseException;
		
		final public HedgeModel getHedgeModel()
			throws SchemaParseException
		{
			return getParticle();
		}
		
		final protected Object getResult() throws SchemaParseException
		{
			return getParticle();
		}
	}
	
	protected class NoneState extends ParticleState
	{
		protected Particle getParticle() { return None.theInstance; }
	}
	
	protected class EmptyState extends ParticleState
	{
		protected Particle getParticle() { return Empty.theInstance; }
	}
	
	/**
	 * State that returns RepetableParticle object as its parsing result.
	 * 
	 * this class reads occurs attribute and stores it.
	 */
	protected abstract class RepetableParticleState extends ParticleState
	{
		protected int occurence;
		
		protected void startSelf() throws SchemaParseException
		{
			super.startSelf();
			
			if( !startTag.hasAttribute("occurs") )
				occurence = RepetableParticle.NORMAL;
			else
			{
				String value = startTag.getAttribute("occurs");
				if(      value.equals("?") )	occurence = RepetableParticle.OPTIONAL;
				else if( value.equals("*") )	occurence = RepetableParticle.STAR;
				else if( value.equals("+") )	occurence = RepetableParticle.PLUS;
				else
				{
					SchemaParseException.raise( startTag,
						SchemaParseException.ERR_ILLEGAL_OCCURS, value );
					occurence = 0;	// assure the compiler that occurence will be always assigned.
									// compiler cannot know raise method never returns.
				}
			}
		}
	}
	
	/**
	 * State that parses &lt;ref label="..." /&gt; element.
	 */
	protected class ElementRefState extends RepetableParticleState
	{
		private ElementRules rules;
		protected void startSelf() throws SchemaParseException
		{
			super.startSelf();
			rules = schema.resolveElementRuleRef(startTag);
		}
		
		protected Particle getParticle() { return rules; }
	}
	
	/**
	 * State that parses &lt;hedgeRef label="..." /&gt; element.
	 */
	protected class HedgeRefState extends RepetableParticleState
	{
		private HedgeRules rules;
		protected void startSelf() throws SchemaParseException
		{
			super.startSelf();
			rules = schema.resolveHedgeRuleRef(startTag);
		}
		
		protected Particle getParticle() { return rules; }
	}
	
	/**
	 * State to parse particles that can contain multiple child particles
	 */
	protected abstract class ContainerParticleState extends RepetableParticleState
	{
		/** child particles will be added to this object */
		private final ContainerParticle container;
		
		protected ContainerParticleState( ContainerParticle container )
		{
			this.container = container;
		}
		
		protected void endChild( ModelState childState )
			throws SchemaParseException
		{
			// make sure that parsed child is in fact particle.
			if(!(childState instanceof ParticleState ))
				SchemaParseException.raise( startTag,
					SchemaParseException.ERR_MALPLACED_ELEMENT, childState.startTag.tagName );
			
			// append this Particle to container
			container.children.appendChild( ((ParticleState)childState).getParticle() );
		}
		
		protected final State createChildState( XMLElement startTag )
		{
			return (State)particleDeclarationStates.get(startTag.tagName);
		}
		
		protected Particle getParticle() { return container; }
	}
	
	protected class ChoiceState extends ContainerParticleState
	{
		protected ChoiceState()	{ super( new Choice() ); }
	}
	
	protected class SequenceState extends ContainerParticleState
	{
		protected SequenceState() { super( new Sequence() ); }
	}

	
	
	
	protected class ClauseState extends ModelState
	{
		/** derived-class should set this value in its constructor. */
		protected Clause clause;
		
		protected final void endChild( ModelState childState )
			throws SchemaParseException
		{
			if( childState instanceof AttributeState )
			{
				clause.attributes.add( (Attribute)childState.getResult() );
				return;
			}
			
			if( childState instanceof RoleRefState )
			{// reference by role to other attPool
				clause.refs.add( (Clauses)childState.getResult() );
				return;
			}
			
			// otherwise error
			SchemaParseException.raise( childState.startTag,
				SchemaParseException.ERR_MALPLACED_ELEMENT, childState.startTag.qName );
		}
		
		protected final State createChildState( XMLElement startTag ) throws SchemaParseException
		{
			State s = super.createChildState(startTag);
			if( startTag.tagName.equals("attribute") )	s = new AttributeState();
			if( startTag.tagName.equals("ref") )		s = new RoleRefState();
			return s;
		}

		
		final protected Object getResult()
		{
			return clause;
		}
	}
	
	protected class TagState extends ClauseState
	{
		protected void startSelf() throws SchemaParseException
		{
			super.startSelf();
			String tagName;
			String roleName;
			
			if( parentState instanceof DocumentElementState )
			{// stand-alone clause declaration.

				// name attribute is mandatory
				tagName = startTag.getRequiredAttribute("name");
				// role attribtue defaults to name
				roleName = startTag.getOptionalAttribute("role",tagName);
			}
			else
			{
				// embeded clause declaration.
				// ASSERT : parentState instanceof ElementRuleState
				
				// role attribute is prohibited.
				roleName = null;
				// name attribute defaults to label name of the parent
				tagName = startTag.getOptionalAttribute("name", ((ElementRuleState)parentState).labelName);
			}
		
			clause = new Tag( roleName, tagName );
		}
	}

	protected class AttPoolState extends ClauseState
	{
		protected void startSelf() throws SchemaParseException
		{
			super.startSelf();
			clause = new AttPool( startTag.getRequiredAttribute("role") );
		}
	}
	
	protected class AttributeState extends ModelState
	{
		private FacetReader facetReader = new FacetReader();
		private Attribute attribute;

		protected void startSelf() throws SchemaParseException
		{
			super.startSelf();
			String namespace;
			
			if( schema instanceof Grammar )
				namespace = startTag.getRequiredAttribute("namespace");
			else
				namespace = ((Module)schema).getTargetNamespace();
			
			attribute = new Attribute(
				namespace,
				startTag.getRequiredAttribute("name"),
				startTag.getOptionalAttribute("required",null)!=null,
				DataTypeFactory.getTypeByName( startTag.getOptionalAttribute("type","string") ) );
		}

		public void endSelf() throws SchemaParseException
		{
			try
			{
				attribute.type = attribute.type.derive( null, facetReader.getFacets() );	// anonymous datatype
			}
			catch( IllegalFacetException e )
			{
				SchemaParseException.raise( startTag, e );
			}
		}
		
		protected final State createChildState( XMLElement startTag ) throws SchemaParseException
		{
			State s = super.createChildState(startTag);
			// each facet has its unique facet name as a tag name.
			// Instead of enumerating all of them, just pass it to FacetState.
			if(s==null)	s = new FacetState();
			return s;
		}
		
		protected Object getResult()	{ return attribute;	}
	}
	
	protected class RoleRefState extends ModelState
	{
		protected Object getResult() throws SchemaParseException
		{
			return schema.resolveRoleRef(startTag);
		}
	}
}
