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

import com.sun.msv.grammar.xmlschema.IdentityConstraint;
import com.sun.msv.grammar.xmlschema.XPath;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * XPath matcher that tests the selector of an identity constraint.
 * 
 * This object is created whenever an element with identity constraints is found.
 * XML Schema guarantees that we can see if an element has id constraints at the
 * startElement method.
 * 
 * This mathcer then monitor startElement/endElement and find matches to the
 * specified XPath. Every time it finds a match ("target node" in XML Schema
 * terminology), it creates a FieldsMatcher.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SelectorMatcher extends MatcherBundle {
	
	protected IdentityConstraint idConst;

	SelectorMatcher(
				IDConstraintChecker owner, IdentityConstraint idConst,
				Attributes atts ) throws SAXException {
		super(owner);
		this.idConst = idConst;

		boolean signal = false;
		
		children = new SelectorPathMatcher[idConst.selectors.length];
		for( int i=0; i<children.length; i++ ) {
			SelectorPathMatcher m = new SelectorPathMatcher( idConst.selectors[i] );
			m.testInitialMatch(atts);
			children[i] = m;
		}
		
		seeIfMatched(atts);
	}
	
	/**
	 * checks children to see if anyone matches.
	 * If it finds a match, then it invokes a FieldsMatcher.
	 */
	private void seeIfMatched( Attributes atts ) throws SAXException {
		boolean signal = false;
		
		for( int i=0; i<children.length; i++ ) {
			if( ((SelectorPathMatcher)children[i]).signalTargetNode ) {
				signal = true;
				((SelectorPathMatcher)children[i]).signalTargetNode = false;
			}
		}
		
		if( signal ) {
			if( com.sun.msv.driver.textui.Debug.debug )
				System.out.println("find a match for a selector: "+idConst.localName);
			
			// this element matches the path.
			owner.add( new FieldsMatcher(owner,idConst,atts) );
		}
	}
	
	protected void startElement( String namespaceURI, String localName, Attributes attributes )
													throws SAXException {
		super.startElement(namespaceURI,localName,attributes);
		seeIfMatched(attributes);
	}
	
	private class SelectorPathMatcher extends PathMatcher {
		SelectorPathMatcher( XPath path ) {
			super( SelectorMatcher.this.owner, path );
		}
		protected void onMatched( String namespaceURI, String localName, Attributes attributes ) throws SAXException {
			// this element is a target node.
			signalTargetNode = true;
		}
	
		/**
		 * a flag that indicates this element is a target node.
		 * Sometimes more than one SelectorPathMatchers match the same element.
		 * Therefore this field is necessary.
		 */
		protected boolean signalTargetNode;
	}
	
}
