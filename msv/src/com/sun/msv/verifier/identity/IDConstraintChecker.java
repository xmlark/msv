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
import com.sun.msv.verifier.DocumentDeclaration;
import com.sun.msv.verifier.VerificationErrorHandler;
import com.sun.msv.verifier.IVerifier;
import com.sun.msv.verifier.ValidityViolation;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.grammar.xmlschema.ElementDeclExp;
import com.sun.msv.grammar.xmlschema.IdentityConstraint;
import com.sun.msv.grammar.xmlschema.KeyRefConstraint;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;


/**
 * Verifier with XML Schema's identity constraint enforcement.
 * 
 * This class can be used in the same way as {@link Verifier}.
 * This class also checks XML Schema's identity constraint.
 * 
 * This class can be used with non XML Schema grammar.
 * In that case, this class does nothing.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IDConstraintChecker extends Verifier {

	public IDConstraintChecker( DocumentDeclaration documentDecl, VerificationErrorHandler errorHandler ) {
		super(documentDecl,errorHandler);
	}
	
	/** active mathcers. */
	protected final Vector matchers = new Vector();
	
	/** map from IdentityConstraint to set of keys. */
	protected final Map keyValues = new java.util.HashMap();
	
	protected void add( Matcher matcher ) {
		matchers.add(matcher);
	}
	protected void remove( Matcher matcher ) {
		matchers.remove(matcher);
	}

	public void startDocument() {
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
	
	public void startElement( String namespaceUri, String localName, String qName,
								Attributes atts ) throws SAXException {
		super.startElement(namespaceUri,localName,qName,atts);
		
		// call matchers
		int len = matchers.size();
		for( int i=0; i<len; i++ )
			((Matcher)matchers.get(i)).startElement(namespaceUri,localName,atts);
		
		// introduce newly found identity constraints.
		Object e = getCurrentElementType();
		if( e instanceof ElementDeclExp.XSElementExp ) {
			ElementDeclExp.XSElementExp exp = (ElementDeclExp.XSElementExp)e;
			if( exp.identityConstraints!=null ) {
				int m = exp.identityConstraints.size();
				for( int i=0; i<m; i++ )
					add( new SelectorMatcher( this,
							(IdentityConstraint)exp.identityConstraints.get(i), atts) );
			}
		}
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
		
		// call matchers
		int len = matchers.size();
		for( int i=len-1; i>=0; i-- )
			// Matcher may remove itself from the vector.
			// Therefore, to make it work correctly, we have to
			// enumerate Matcher in reverse direction.
			((Matcher)matchers.get(i)).endElement();
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
