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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;
import com.sun.msv.grammar.xmlschema.IdentityConstraint;
import com.sun.msv.grammar.xmlschema.KeyConstraint;
import com.sun.msv.grammar.xmlschema.KeyRefConstraint;
import java.util.Set;

/**
 * Coordinator of FieldMatcher.
 * 
 * This object is created when SelectorMatcher finds a match.
 * This object then creates FieldMatcher for each field, and
 * let them find their field matchs.
 * When leaving the element that matched the selector, it collects
 * field values and registers a key value to IDConstraintChecker.
 * 
 * <p>
 * Depending on the type of the constraint, it works differently.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class FieldsMatcher extends MatcherBundle {
	
	/** identity constraint to which this object is matching. */
	protected final IdentityConstraint idConst;
	
	/**
	 * location of the start tag.
	 * It is usually preferable as a source of error.
	 */
	protected final Locator startTag;
	
	
	protected FieldsMatcher( IDConstraintChecker owner,
					IdentityConstraint idConst, String namespaceURI, String localName ) throws SAXException {
		super(owner);
		
		this.idConst = idConst;
		this.startTag = new LocatorImpl(owner.getLocator());
		
		children = new Matcher[idConst.fields.length];
		for( int i=0; i<idConst.fields.length; i++ )
			children[i] = new FieldMatcher(this,idConst.fields[i], namespaceURI,localName);
	}
	
	protected void onRemoved() throws SAXException {
		Object[] values = new Object[children.length];
			
		// copy matched values into "values" variable,
		// while checking any unmatched fields.
		for( int i=0; i<children.length; i++ )
			if( (values[i]=((FieldMatcher)children[i]).value) == null ) {
				if(!(idConst instanceof KeyConstraint))
					// some fields didn't match to anything.
					// In case of KeyRef and Unique constraints,
					// we can ignore this node.
					return;
					
				// if this is the key constraint, it is an error
				owner.reportError(
					startTag,
					owner.ERR_UNMATCHED_KEY_FIELD,
					new Object[]{
						idConst.namespaceURI,
						idConst.localName,
						new Integer(i+1)} );
				return;
			}

		if( com.sun.msv.driver.textui.Debug.debug )
			System.out.println("fields collected for "+idConst.localName);
		
		Set valueSet = (Set)owner.keyValues.get(idConst);
		if(valueSet==null)
			// create a new set.
			owner.keyValues.put(idConst, valueSet = new java.util.HashSet() );
			
		KeyValue kv = new KeyValue(values,startTag);
		
		if( !valueSet.contains(kv) ) {
			valueSet.add(kv);
			return;
		}
		
		if( idConst instanceof KeyRefConstraint )
			// multiple reference to the same key value.
			// not a problem.
			return;
		
		// find a value that collides with kv
		Object[] items = valueSet.toArray();
		int i;
		for( i=0; i<values.length; i++ )
			if( items[i].equals(kv) )
				break;
		
		// violates uniqueness constraint.
		// this set already has this value.
		owner.reportError(
			startTag,
			owner.ERR_NOT_UNIQUE,
			new Object[]{
				idConst.namespaceURI, idConst.localName} );
		owner.reportError(
			((KeyValue)items[i]).locator,
			owner.ERR_NOT_UNIQUE_DIAG,
			new Object[]{
				idConst.namespaceURI, idConst.localName} );
	}
	
}
