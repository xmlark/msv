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
import org.relaxng.datatype.Datatype;
import java.util.Set;
import java.util.Map;
import java.util.Iterator;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.grammar.IDContextProvider;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringRef;
import com.sun.msv.util.DatatypeRef;

/**
 * SAX ContentHandler that verifies incoming SAX event stream.
 * 
 * This object can be reused to validate multiple documents.
 * Just be careful NOT to use the same object to validate more than one
 * documents <b>at the same time</b>.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Verifier extends AbstractVerifier implements IVerifier {
	
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
	
	/** error handler */
	protected VerificationErrorHandler errorHandler;
	public final VerificationErrorHandler getVErrorHandler() { return errorHandler; }
	
	/** this flag will be set to true if an error is found */
	protected boolean hadError;
	
	/** this flag will be set to true after endDocument method is called. */
	private boolean isFinished;
	
	/** an object used to store start tag information.
	 * the same object is reused. */
	private final StartTagInfo sti = new StartTagInfo(null,null,null,null,null);
	
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
	private final DatatypeRef characterType = new DatatypeRef();
	public Datatype[] getLastCharacterType() { return characterType.types; }
	
	private void verifyText() throws SAXException {
		if(text.length()!=0) {
			characterType.types=null;
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
					characterType.types=null;
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
		
		sti.reinit(namespaceUri, localName, qName, atts, this );

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
			characterType.types = new Datatype[]{StringType.theInstance};
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
	 * 
	 * This method can be overrided by the derived class to provide different behavior.
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
	
	public Object getCurrentElementType() {
		return current.getOwnerType();
	}
	
	public void characters( char[] buf, int start, int len ) throws SAXException {
		if( stringCareLevel!=Acceptor.STRING_IGNORE )
			text.append(buf,start,len);
	}
	public void ignorableWhitespace( char[] buf, int start, int len ) throws SAXException {
		if( stringCareLevel!=Acceptor.STRING_IGNORE
		&&  stringCareLevel!=Acceptor.STRING_PROHIBITED )
			// white space is allowed even if the current mode is STRING_PROHIBITED.
			text.append(buf,start,len);
	}
	
	protected void init() {
		super.init();
		hadError=false;
		isFinished=false;
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
		Iterator itr = idrefs.keySet().iterator();
		while( itr.hasNext() ) {
			String symbolSpace = (String)itr.next();
			
			Set refs = (Set)idrefs.get(symbolSpace);
			Set keys = (Set)ids.get(symbolSpace);
			
			if(keys==null || !keys.containsAll(refs)) {
				hadError = true;
				Iterator jtr = refs.iterator();
				while( jtr.hasNext() ) {
					Object idref = jtr.next();
					if(keys==null || !keys.contains(idref)) {
						if( symbolSpace.length()==0 )
							onError( null, localizeMessage( ERR_UNSOLD_IDREF, new Object[]{idref} ) );
						else
							onError( null, localizeMessage( ERR_UNSOLD_KEYREF, new Object[]{idref,symbolSpace} ) );
					}
				}
			}
		}
		
		isFinished=true;
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
	public static final String ERR_UNSOLD_KEYREF = // arg:1
		"Verifier.Error.UnsoldKeyref";
}
