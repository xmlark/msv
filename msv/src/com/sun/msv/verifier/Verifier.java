/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.verifier;

import org.xml.sax.*;
import org.xml.sax.helpers.NamespaceSupport;
import java.util.Set;
import java.util.Iterator;
import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.datatype.StringType;
import com.sun.tranquilo.grammar.IDContextProvider;
import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.util.StringRef;
import com.sun.tranquilo.util.DataTypeRef;

/**
 * SAX ContentHandler that verifies incoming SAX event stream.
 * 
 * This object can be reused to validate multiple documents.
 * Just be careful NOT to use the same object to validate more than one
 * documents <b>at the same time</b>.
 */
public class Verifier implements
	ContentHandler,
	DTDHandler,
	IDContextProvider
{
	private Acceptor current;
	
	private static final class Context
	{
		final Context	previous;
		final Acceptor	acceptor;
		final int		stringCareLevel;
		Context( Context prev, Acceptor acc, int scl )
		{ previous=prev; acceptor=acc; stringCareLevel=scl; }
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
	private final VerificationErrorHandler errorHandler;
	
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
	private final DocumentDeclaration docDecl;
	
	/** panic level.
	 * 
	 * If the level is non-zero, createChildAcceptors will silently recover
	 * from error. This effectively suppresses spurious error messages.
	 * 
	 * This value is set to INITIAL_PANIC_LEVEL when first an error is encountered,
	 * and is decreased by successful stepForward and createChildAcceptor.
	 * This value is also propagated to child acceptors.
	 */
	private int panicLevel = 0;
	
	private final static int INITIAL_PANIC_LEVEL = 5;

	public Verifier( DocumentDeclaration documentDecl, VerificationErrorHandler errorHandler )
	{
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
	
	private void verifyText()
		throws SAXException
	{
		if(text.length()!=0)
		{
			characterType.type=null;
			switch( stringCareLevel )
			{
			case Acceptor.STRING_PROHIBITED:
				// only whitespace is allowed.
				final int len = text.length();
				for( int i=0; i<len; i++ )
				{
					final char ch = text.charAt(i);
					if( ch!=' ' && ch!='\t' && ch!='\r' && ch!='\n' )
					{// error
						hadError = true;
						
						if( panicLevel==0 && errorHandler!=null )
						{
							errorHandler.onError( new ValidityViolation(
								locator, localizeMessage( ERR_UNEXPECTED_TEXT, null ) ) );
							panicLevel = INITIAL_PANIC_LEVEL;
						}
						break;// recover by ignoring this token
					}
				}
				break;	
				
			case Acceptor.STRING_STRICT:
				final String txt = new String(text);
				if(!current.stepForward( txt, this, null, characterType ))
				{// error
					hadError = true;
					
					// diagnose error, if possible
					StringRef err = new StringRef();
					characterType.type=null;
					current.stepForward( txt, this, err, characterType );
					
					// report an error
					if( panicLevel==0 )
					{
						if( err.str==null )
							errorHandler.onError( new ValidityViolation(
								locator, localizeMessage( ERR_UNEXPECTED_TEXT, null ) ) );
						else
							errorHandler.onError( new ValidityViolation(
								locator, err.str ) );
						panicLevel = INITIAL_PANIC_LEVEL;
					}
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
		throws SAXException
	{
		if( com.sun.tranquilo.driver.textui.Debug.debug )
			System.out.println("\n-- startElement("+qName+")" + locator.getLineNumber()+":"+locator.getColumnNumber() );
		
		namespaceSupport.pushContext();
		verifyText();		// verify PCDATA first.
		

		// push context
		stack = new Context( stack, current, stringCareLevel );
		
		StartTagInfo sti = new StartTagInfo(namespaceUri, localName, qName, atts, this );

		// get Acceptor that will be used to validate the contents of this element.
		Acceptor next = current.createChildAcceptor(sti,null);
		
		if( next==null )
		{// no child element matchs this one
			
			if( com.sun.tranquilo.driver.textui.Debug.debug )
				System.out.println("-- no children accepted: error recovery");

			hadError = true;

			// let acceptor recover from this error.
			StringRef ref = new StringRef();
			next = current.createChildAcceptor(sti,ref);
			
			ValidityViolation vv;
			if( ref.str!=null )
			{// error message is available
				vv = new ValidityViolation(locator,ref.str);
			}
			else
			{// no error message is avaiable, use default.
				vv = new ValidityViolation( locator,
					localizeMessage( ERR_UNEXPECTED_STARTTAG, new Object[]{qName} ) );
			}
			
			if( errorHandler!=null && panicLevel==0 )
				errorHandler.onError(vv);
			
			if( errorHandler==null || next==null )
				throw new ValidationUnrecoverableException(vv);
			
			panicLevel = INITIAL_PANIC_LEVEL;
		}
		else
			panicLevel = Math.max( panicLevel-1, 0 );
		
		stringCareLevel = next.getStringCareLevel();
		if( stringCareLevel==Acceptor.STRING_IGNORE )
			characterType.type = StringType.theInstance;
		current = next;
	}
	
	public void endElement( String namespaceUri, String localName, String qName )
		throws SAXException
	{
		if( com.sun.tranquilo.driver.textui.Debug.debug )
			System.out.println("\n-- endElement("+qName+")" + locator.getLineNumber()+":"+locator.getColumnNumber() );
		
		namespaceSupport.popContext();
		verifyText();

		if( !current.isAcceptState() && panicLevel==0 )
		{
			// TODO: diagnosis?
			hadError = true;
			panicLevel = INITIAL_PANIC_LEVEL;
			errorHandler.onError( new ValidityViolation(
				locator,
				localizeMessage( ERR_UNCOMPLETED_CONTENT,
				new Object[]{qName} ) ) );	// report an error
			
			// error recovery: pretend as if this state is satisfied
			// fall through is enough
		}
		Acceptor child = current;
		
		// pop context
		current = stack.acceptor;
		stringCareLevel = stack.stringCareLevel;
		stack = stack.previous;
		
		if(!current.stepForward( child, null ))
		{// error
			hadError = true;
			StringRef ref = new StringRef();
			current.stepForward( child, ref );	// force recovery
			ValidityViolation vv;
			
			if( ref.str!=null )
			{// error message is available
				vv = new ValidityViolation(locator,ref.str);
			}
			else
			{// no error message is avaiable, use default.
				vv = new ValidityViolation( locator,
					localizeMessage( ERR_UNCOMPLETED_CONTENT, new Object[]{qName} ) );
			}
			
			if( errorHandler!=null && panicLevel==0 )
				errorHandler.onError(vv);
			
			panicLevel = INITIAL_PANIC_LEVEL;
		}
		else
			panicLevel = Math.max( panicLevel-1, 0 );
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
	public Object getCurrentElementType()
	{
		return current.getOwnerType();
	}
	
	public void characters( char[] buf, int start, int len )
	{
		if( stringCareLevel!=Acceptor.STRING_IGNORE )
			text.append(buf,start,len);
	}
	public void ignorableWhitespace( char[] buf, int start, int len )
	{
		if( stringCareLevel!=Acceptor.STRING_IGNORE )
			text.append(buf,start,len);
	}
	public void setDocumentLocator( Locator loc )
	{
		this.locator = loc;
	}
	public void skippedEntity(String p) {}
	public void processingInstruction(String name,String data) {}
	
	public void startPrefixMapping( String prefix, String uri )
	{
		namespaceSupport.declarePrefix( prefix, uri );
	}
	public void endPrefixMapping( String prefix )	{}
	
	public void startDocument()
	{
		// reset everything
		current = docDecl.createAcceptor();
		hadError=false;
		isFinished=false;
		ids.clear();
		idrefs.clear();
	}
	public void endDocument() throws SAXException
	{
		// ID/IDREF check
		if(!ids.containsAll(idrefs))
		{
			hadError = true;
			Iterator itr = idrefs.iterator();
			while( itr.hasNext() )
			{
				String idref = (String)itr.next();
				if(!ids.contains(idref))
					errorHandler.onError( new ValidityViolation(
						locator, localizeMessage( ERR_UNSOLD_IDREF, new Object[]{idref} ) ) );
			}
		}
		isFinished=true;
	}

	public void notationDecl( String name, String publicId, String systemId ) {}
	public void unparsedEntityDecl( String name, String publicId, String systemId, String notationName )
	{// store name of unparsed entities to implement ValidationContextProvider
		unparsedEntities.add(name);
	}
									
	
	/**
	 * namespace prefix to namespace URI resolver.
	 * 
	 * this object memorizes mapping information.
	 */
	private final NamespaceSupport namespaceSupport = new NamespaceSupport();

	/** unparsed entities found in the document */
	private final Set unparsedEntities = new java.util.HashSet();
	
	// methods of ValidationContextProvider
	public String resolveNamespacePrefix( String prefix )
	{
		return namespaceSupport.getURI(prefix);
	}
	public boolean isUnparsedEntity( String entityName )
	{
		return unparsedEntities.contains(entityName);
	}
	
	public void onIDREF( String token )	{ idrefs.add(token); }
	public boolean onID( String token )
	{
		if( ids.contains(token) )	return false;	// not unique.
		ids.add(token);
		return true;	// they are unique, at least now.
	}

	
	public String localizeMessage( String propertyName, Object[] args )
	{
		String format = java.util.ResourceBundle.getBundle(
			"com.sun.tranquilo.verifier.Messages").getString(propertyName);
		
	    return java.text.MessageFormat.format(format, args );
	}

	public static final String ERR_UNEXPECTED_TEXT = // arg:0
		"Verifier.Error.UnexpectedText";
	public static final String ERR_UNEXPECTED_STARTTAG = // arg:1
		"Verifier.Error.UnexpectedStartTag";
	public static final String ERR_UNCOMPLETED_CONTENT = // arg:1
		"Verifier.Error.UncompletedContent";
	public static final String ERR_UNSOLD_IDREF = // arg:1
		"Verifier.Error.UnsoldIDREF";
}
