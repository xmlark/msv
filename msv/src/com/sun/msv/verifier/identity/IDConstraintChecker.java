/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.identity;

import java.util.Vector;
import java.util.Map;
import java.util.Set;
//import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.verifier.VerificationErrorHandler;
import com.sun.msv.verifier.IVerifier;
import com.sun.msv.verifier.ValidityViolation;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.Acceptor;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.verifier.regexp.xmlschema.XSREDocDecl;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.xmlschema.ElementDeclExp;
import com.sun.msv.grammar.xmlschema.IdentityConstraint;
import com.sun.msv.grammar.xmlschema.KeyRefConstraint;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.grammar.xmlschema.XMLSchemaTypeExp;
import com.sun.msv.util.StartTagInfo;
import org.relaxng.datatype.Datatype;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

/**
 * Verifier with XML Schema-related enforcement.
 * 
 * <p>
 * This class can be used in the same way as {@link Verifier}.
 * This class also checks XML Schema's identity constraint.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IDConstraintChecker extends Verifier {

	public IDConstraintChecker( XMLSchemaGrammar grammar, VerificationErrorHandler errorHandler ) {
		super(new XSREDocDecl(grammar),errorHandler);
		this.grammar = grammar;
	}
	
	/** the grammar object against which we are validating. */
	protected final XMLSchemaGrammar grammar;
	
	/** active mathcers. */
	protected final Vector matchers = new Vector();
	
	protected void add( Matcher matcher ) {
		matchers.add(matcher);
	}
	protected void remove( Matcher matcher ) {
		matchers.remove(matcher);
	}
	
	/** map from IdentityConstraint to set of keys. */
	protected final Map keyValues = new java.util.HashMap();

	
	
	
	public void startDocument() throws SAXException {
		super.startDocument();
		keyValues.clear();
	}
	
	public void endDocument() throws SAXException {
		super.endDocument();
		
		// keyref check
		IdentityConstraint[] constraints = 
			(IdentityConstraint[])keyValues.keySet().toArray(new IdentityConstraint[0]);
		for( int i=0; i<constraints.length; i++ )
			if( constraints[i] instanceof KeyRefConstraint ) {
				// obtain the two sets.
				Set keys = (Set)keyValues.get( ((KeyRefConstraint)constraints[i]).key );
				KeyValue[] keyrefs = (KeyValue[])
					((Set)keyValues.get(constraints[i])).toArray(new KeyValue[0]);
				
				for( int j=0; j<keyrefs.length; j++ ) {
					if( keys==null || !keys.contains(keyrefs[j]) )
						// this keyref doesn't have a corresponding key.
						reportError( keyrefs[j].locator, ERR_UNDEFINED_KEY,
							new Object[]{
								constraints[i].namespaceURI,
								constraints[i].localName} );
				}
			}
	}
	
	protected void onNextAcceptorReady( StartTagInfo sti, Acceptor next ) throws SAXException {
		
		// call matchers
		int len = matchers.size();
		for( int i=0; i<len; i++ ) {
			Matcher m = (Matcher)matchers.get(i);
			m.startElement(sti.namespaceURI,sti.localName);
		}
		
		// introduce newly found identity constraints.
		Object e = next.getOwnerType();
		if( e instanceof ElementDeclExp.XSElementExp ) {
			ElementDeclExp.XSElementExp exp = (ElementDeclExp.XSElementExp)e;
			if( exp.identityConstraints!=null ) {
				int m = exp.identityConstraints.size();
				for( int i=0; i<m; i++ )
					add( new SelectorMatcher( this,
							(IdentityConstraint)exp.identityConstraints.get(i),
							sti.namespaceURI, sti.localName ) );
			}
		}
	}

	protected Datatype[] feedAttribute( Acceptor child, String uri, String localName, String qName, String value ) throws SAXException {
		Datatype[] result = super.feedAttribute( child, uri, localName, qName, value );
		
		final int len = matchers.size();
		// call matchers for attributes.
		for( int i=0; i<len; i++ ) {
			Matcher m = (Matcher)matchers.get(i);
			m.onAttribute( uri, localName, value, 
				(result==null)?null:result[0] );
		}
		
		return result;
	}

	
	
	public void characters( char[] buf, int start, int len ) throws SAXException {
		super.characters(buf,start,len);
		
		int m = matchers.size();
		for( int i=0; i<m; i++ )
			((Matcher)matchers.get(i)).characters(buf,start,len);
	}


	public void endElement( String namespaceUri, String localName, String qName )
								throws SAXException {
		super.endElement(namespaceUri,localName,qName);
		
		// getLastCharacterType may sometimes return null. For example,
		// 1) this element should be empty and there was only whitespace characters.
		Datatype dt;
		Datatype[] lastType = getLastCharacterType();
		if( lastType==null || lastType.length==0 )	dt = null;
		else										dt = getLastCharacterType()[0];
			
		// call matchers
		int len = matchers.size();
		for( int i=len-1; i>=0; i-- ) {
			// Matcher may remove itself from the vector.
			// Therefore, to make it work correctly, we have to
			// enumerate Matcher in reverse direction.
			((Matcher)matchers.get(i)).endElement( dt );
		}
	}
	

	
	/** reports an error. */
	protected void reportError( String propKey, Object[] args ) throws SAXException {
		// use the current location.
		reportError( getLocator(), propKey, args );
	}
	
	protected void reportError( Locator loc, String propKey, Object[] args ) throws SAXException {
		hadError = true;
		errorHandler.onError(
			new ValidityViolation( loc,
				localizeMessage(propKey,args) ) );
	}
	
	public static String localizeMessage( String propertyName, Object arg ) {
		return localizeMessage( propertyName, new Object[]{arg} );
	}

	public static String localizeMessage( String propertyName, Object[] args ) {
		String format = java.util.ResourceBundle.getBundle(
			"com.sun.msv.verifier.identity.Messages").getString(propertyName);
		
	    return java.text.MessageFormat.format(format, args );
	}
	
	public static final String ERR_UNMATCHED_KEY_FIELD =
		"IdentityConstraint.UnmatchedKeyField";	// arg :3
	public static final String ERR_NOT_UNIQUE =
		"IdentityConstraint.NotUnique"; // arg:2
	public static final String ERR_NOT_UNIQUE_DIAG =
		"IdentityConstraint.NotUnique.Diag";	// arg:2
	public static final String ERR_DOUBLE_MATCH =
		"IdentityConstraint.DoubleMatch"; // arg:3
	public static final String ERR_UNDEFINED_KEY =
		"IdentityConstraint.UndefinedKey"; // arg:2 
	
}
