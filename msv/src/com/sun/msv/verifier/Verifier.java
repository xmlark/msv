/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier;

import org.xml.sax.*;
import org.xml.sax.helpers.NamespaceSupport;
import java.util.Set;
import java.util.Iterator;
import com.sun.msv.datatype.DataType;
import com.sun.msv.datatype.StringType;
import com.sun.msv.grammar.IDContextProvider;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringRef;
import com.sun.msv.util.DataTypeRef;

/**
 * SAX ContentHandler that verifies incoming SAX event stream.
 * 
 * This object can be reused to validate multiple documents.
 * Just be careful NOT to use the same object to validate more than one
 * documents <b>at the same time</b>.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Verifier implements
	ContentHandler,
	DTDHandler,
	IDContextProvider {
	
	protected Acceptor current;
	
	private static final class Context {
		final Context	previous;
		final Acceptor	acceptor;
		final int		stringCareLevel;
		int				panicLevel;
		Context( Context prev, Acceptor acc, int scl, int plv )
		{ previous=prev; acceptor=acc; stringCareLevel=scl; panicLevel=plv; }
	};
	
	/** context stack */
	Context		stack = null;
	
	/** current string care level. See Acceptor.getStringCareLevel */
	private int stringCareLevel = Acceptor.STRING_STRICT;
	
	/** characters that were read (but not processed)  */
	private StringBuffer text = new StringBuffer();
	
	/** document Locator that is given by XML reader */
	private Locator locator;
	
	/** error handler */
	protected VerificationErrorHandler errorHandler;
	
	/** this flag will be set to true if an error is found */
	private boolean hadError;
	
	/** this flag will be set to true after endDocument method is called. */
	private boolean isFinished;
	
	/** this set remembers every ID token encountered in this document */
	private final Set ids = new java.util.HashSet();
	/** this map remembers every IDREF token encountered in this document */
	private final Set idrefs = new java.util.HashSet();
	
	/**
	 * checks if the document was valid.
	 * 
	 * This method may not be called before verification was completed.
	 */
	public final boolean isValid() { return !hadError && isFinished; }
	
	/** Schema object against which the validation will be done */
	protected final DocumentDeclaration docDecl;
	
	/** panic level.
	 * 
	 * If the level is non-zero, createChildAcceptors will silently recover
	 * from error. This effectively suppresses spurious error messages.
	 * 
	 * This value is set to INITIAL_PANIC_LEVEL when first an error is encountered,
	 * and is decreased by successful stepForward and createChildAcceptor.
	 * This value is also propagated to child acceptors.
	 */
	protected int panicLevel = 0;
	
	private final static int INITIAL_PANIC_LEVEL = 3;

	public Verifier( DocumentDeclaration documentDecl, VerificationErrorHandler errorHandler ) {
		this.docDecl = documentDecl;
		this.errorHandler = errorHandler;
	}
	
	/** this field is used to receive type information of character literals. */
	private final DataTypeRef characterType = new DataTypeRef();
	/**
	 * gets DataType that validated the last characters.
	 * 
	 * <p>
	 * This method works correctly only when called immediately
	 * after startElement and endElement method. When called, this method
	 * returns DataType object that validated the last character literals.
	 * 
	 * <p>
	 * So when you are using VerifierFilter, you can call this method only
	 * in your startElement and endElement method.
	 * 
	 * @return null
	 *		if type-assignment was not possible.
	 */
	public DataType getLastCharacterType() { return characterType.type; }
	
	private void verifyText() throws SAXException {
		if(text.length()!=0) {
			characterType.type=null;
			switch( stringCareLevel ) {
			case Acceptor.STRING_PROHIBITED:
				// only whitespace is allowed.
				final int len = text.length();
				for( int i=0; i<len; i++ ) {
					final char ch = text.charAt(i);
					if( ch!=' ' && ch!='\t' && ch!='\r' && ch!='\n' ) {
						// error
						onError( null, localizeMessage( ERR_UNEXPECTED_TEXT, null ) );
						break;// recover by ignoring this token
					}
				}
				break;	
				
			case Acceptor.STRING_STRICT:
				final String txt = new String(text);
				if(!current.stepForward( txt, this, null, characterType )) {
					// error
					// diagnose error, if possible
					StringRef err = new StringRef();
					characterType.type=null;
					current.stepForward( txt, this, err, characterType );
					
					// report an error
					onError( err, localizeMessage( ERR_UNEXPECTED_TEXT, null ) );
				}
				break;
				
			case Acceptor.STRING_IGNORE:
				// if STRING_IGNORE, no text should be appended.
			default:
				throw new Error();	//assertion failed
			}
			
			text = new StringBuffer();
		}
	}
	
	public void startElement( String namespaceUri, String localName, String qName, Attributes atts )
		throws SAXException {
		
		if( com.sun.msv.driver.textui.Debug.debug )
			System.out.println("\n-- startElement("+qName+")" + locator.getLineNumber()+":"+locator.getColumnNumber() );
		
		namespaceSupport.pushContext();
		verifyText();		// verify PCDATA first.
		

		// push context
		stack = new Context( stack, current, stringCareLevel, panicLevel );
		
		StartTagInfo sti = new StartTagInfo(namespaceUri, localName, qName, atts, this );

		// get Acceptor that will be used to validate the contents of this element.
		Acceptor next = current.createChildAcceptor(sti,null);
		
		if( next==null ) {
			// no child element matchs this one
			if( com.sun.msv.driver.textui.Debug.debug )
				System.out.println("-- no children accepted: error recovery");

			// let acceptor recover from this error.
			StringRef ref = new StringRef();
			next = current.createChildAcceptor(sti,ref);
			
			ValidityViolation vv = onError( ref, localizeMessage( ERR_UNEXPECTED_STARTTAG, new Object[]{qName} ) );
			
			if( next==null ) {
				if( com.sun.msv.driver.textui.Debug.debug )
					System.out.println("-- unable to recover");
				throw new ValidationUnrecoverableException(vv);
			}
		}
		else
			panicLevel = Math.max( panicLevel-1, 0 );
		
		stack.panicLevel = panicLevel;	// back-patching.
		
		stringCareLevel = next.getStringCareLevel();
		if( stringCareLevel==Acceptor.STRING_IGNORE )
			characterType.type = StringType.theInstance;
		current = next;
	}
	
	public void endElement( String namespaceUri, String localName, String qName )
		throws SAXException {
		
		if( com.sun.msv.driver.textui.Debug.debug )
			System.out.println("\n-- endElement("+qName+")" + locator.getLineNumber()+":"+locator.getColumnNumber() );
		
		namespaceSupport.popContext();
		verifyText();

		if( !current.isAcceptState(null) && panicLevel==0 ) {
			// error diagnosis
			StringRef errRef = new StringRef();
			current.isAcceptState(errRef);
			onError( errRef, localizeMessage( ERR_UNCOMPLETED_CONTENT,new Object[]{qName} ) );
			// error recovery: pretend as if this state is satisfied
			// fall through is enough
		}
		Acceptor child = current;
		
		// pop context
		current = stack.acceptor;
		stringCareLevel = stack.stringCareLevel;
		panicLevel = Math.max( panicLevel, stack.panicLevel );
		stack = stack.previous;
		
		if(!current.stepForward( child, null ))
		{// error
			StringRef ref = new StringRef();
			current.stepForward( child, ref );	// force recovery
			
			onError( ref, localizeMessage( ERR_UNEXPECTED_ELEMENT, new Object[]{qName} ) );
		}
		else
			panicLevel = Math.max( panicLevel-1, 0 );
	}
	
	/**
	 * signals an error.
	 */
	protected ValidityViolation onError( StringRef ref, String defaultMsg ) throws SAXException {
		ValidityViolation vv;
		hadError = true;
			
		if( ref!=null && ref.str!=null )
			// error message is available
			vv = new ValidityViolation(locator,ref.str);
		else
			// no error message is avaiable, use default.
			vv = new ValidityViolation( locator, defaultMsg );
			
		if( errorHandler!=null && panicLevel==0 )
			errorHandler.onError(vv);
			
		panicLevel = INITIAL_PANIC_LEVEL;
		return vv;
	}
	
	/**
	 * returns current element type.
	 * 
	 * Actual java type depends on the implementation.
	 * This method works correctly only when called immediately
	 * after handling startElement event.
	 * 
	 * @return null
	 *		this method returns null when it doesn't support
	 *		type-assignment feature, or type-assignment is impossible
	 *		for the current element (for example due to the ambiguous grammar).
	 */
	public Object getCurrentElementType() {
		return current.getOwnerType();
	}
	
	public void characters( char[] buf, int start, int len ) {
		if( stringCareLevel!=Acceptor.STRING_IGNORE )
			text.append(buf,start,len);
	}
	public void ignorableWhitespace( char[] buf, int start, int len ) {
		if( stringCareLevel!=Acceptor.STRING_IGNORE )
			text.append(buf,start,len);
	}
	public void setDocumentLocator( Locator loc ) {
		this.locator = loc;
	}
	public void skippedEntity(String p) {}
	public void processingInstruction(String name,String data) {}
	
	public void startPrefixMapping( String prefix, String uri ) {
		namespaceSupport.declarePrefix( prefix, uri );
	}
	public void endPrefixMapping( String prefix )	{}
	
	protected void init() {
		hadError=false;
		isFinished=false;
		ids.clear();
		idrefs.clear();
	}
	
	public void startDocument() {
		// reset everything.
		// since Verifier maybe reused, initialization is better done here
		// rather than constructor.
		init();
		// if Verifier is used without "divide&validate", 
		// this method is called and the initial acceptor
		// is set by this method.
		// When Verifier is used in IslandVerifierImpl,
		// then initial acceptor is set at the constructor
		// and this method is not called.
		current = docDecl.createAcceptor();
	}
	
	public void endDocument() throws SAXException {
		// ID/IDREF check
		if(!ids.containsAll(idrefs)) {
			hadError = true;
			Iterator itr = idrefs.iterator();
			while( itr.hasNext() ) {
				String idref = (String)itr.next();
				if(!ids.contains(idref))
					errorHandler.onError( new ValidityViolation(
						locator, localizeMessage( ERR_UNSOLD_IDREF, new Object[]{idref} ) ) );
			}
		}
		isFinished=true;
	}

	public void notationDecl( String name, String publicId, String systemId ) {}
	public void unparsedEntityDecl( String name, String publicId, String systemId, String notationName ) {
		// store name of unparsed entities to implement ValidationContextProvider
		unparsedEntities.add(name);
	}
									
	
	/**
	 * namespace prefix to namespace URI resolver.
	 * 
	 * this object memorizes mapping information.
	 */
	protected final NamespaceSupport namespaceSupport = new NamespaceSupport();

	/** unparsed entities found in the document */
	private final Set unparsedEntities = new java.util.HashSet();
	
	// methods of ValidationContextProvider
	public String resolveNamespacePrefix( String prefix ) {
		return namespaceSupport.getURI(prefix);
	}
	public boolean isUnparsedEntity( String entityName ) {
		return unparsedEntities.contains(entityName);
	}
	
	public void onIDREF( String token )	{ idrefs.add(token); }
	public boolean onID( String token ) {
		if( ids.contains(token) )	return false;	// not unique.
		ids.add(token);
		return true;	// they are unique, at least now.
	}

	
	public static String localizeMessage( String propertyName, Object[] args ) {
		String format = java.util.ResourceBundle.getBundle(
			"com.sun.msv.verifier.Messages").getString(propertyName);
		
	    return java.text.MessageFormat.format(format, args );
	}

	public static final String ERR_UNEXPECTED_TEXT = // arg:0
		"Verifier.Error.UnexpectedText";
	public static final String ERR_UNEXPECTED_STARTTAG = // arg:1
		"Verifier.Error.UnexpectedStartTag";
	public static final String ERR_UNCOMPLETED_CONTENT = // arg:1
		"Verifier.Error.UncompletedContent";
	public static final String ERR_UNEXPECTED_ELEMENT = // arg:1
		"Verifier.Error.UnexpectedElement";
	public static final String ERR_UNSOLD_IDREF = // arg:1
		"Verifier.Error.UnsoldIDREF";
}
