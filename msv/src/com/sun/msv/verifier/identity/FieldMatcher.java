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
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.xmlschema.Field;

/**
 * XPath matcher that tests one field of a key.
 * 
 * This object is created by a FieldsMatcher when a SelectorMathcer
 * finds a match to its selector. This object is responsible for finding
 * a match to one field of the constraint.
 * 
 * A field XPath may consist of "A|B|C". Each sub case A,B, and C is
 * tested by a child FieldPathMatcher object. This class coordinates
 * the work of those children and collects actual text that matches
 * the given XPath.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class FieldMatcher extends MatcherBundle {
	
	protected Field field;
	
	/**
	 * the matched value. If this field is null, then it means
	 * nothing is matched yet.
	 */
	protected String	matched;
	
	/** parent FieldsMatcher object. */
	protected final FieldsMatcher parent;
	
	/**
	 * this field is set to non-null if it's found that an element
	 * is matched to this XPath. This field is then used to collect
	 * the contents of the matched element until it encounters
	 * the endElement method.
	 */
	protected StringBuffer elementText = null;
	
	FieldMatcher( FieldsMatcher parent, Field field, Attributes atts ) throws SAXException {
		super(parent.owner);
		this.parent = parent;
		
		// create children
		children = new Matcher[field.paths.length];
		for( int i=0; i<field.paths.length; i++ ) {
			FieldPathMatcher m = new FieldPathMatcher(field.paths[i]);
			children[i] = m;
			m.testInitialMatch(atts);
		}
	}

	private class FieldPathMatcher extends PathMatcher {
		FieldPathMatcher( Field.FieldPath path ) {
			super(FieldMatcher.this.owner,path);
		}
		protected void onMatched( String namespaceURI, String localName, Attributes attributes ) throws SAXException {
			if(matched!=null) {
				// not the first match.
				doubleMatchError();
				return;
			}
			
			if( com.sun.msv.driver.textui.Debug.debug )
				System.out.println("field match for "+ parent.idConst.localName );
			
			NameClass attributeStep = ((Field.FieldPath)path).attributeStep;
			
			if( attributeStep==null ) {
				// this field matches this element.
				// wait for the corresponding endElement call and
				// obtain text.
				elementText = new StringBuffer();
			} else {
				// look for the attribute match
				int len = attributes.getLength();
				for( int i=0; i<len; i++ ) {
					if( attributeStep.accepts(
						attributes.getURI(i), attributes.getLocalName(i) ) ) {
						// this field matches this attribute.
						if(matched!=null)	doubleMatchError();
						matched = attributes.getValue(i);
					}
				}
			}
		}
	}
	
	protected void startElement( String namespaceURI, String localName, Attributes attributes ) 
								throws SAXException {
		if( elementText!=null ) {
			// this situation is an error because a matched element
			// cannot contain any child element.
			// But what I don't know is how to treat this situation.
			
			// 1. to make the document invalid?
			// 2. cancel the match?
			
			// the current implementation choose the 2nd.
			elementText = null;
		}
		super.startElement(namespaceURI,localName,attributes);
	}
	
	protected void endElement() throws SAXException {
		super.endElement();
		// double match error is already checked in the corresponding
		// startElement method.
		if( elementText!=null ) {
			matched = elementText.toString();
			elementText = null;
		}
	}

	protected void characters( char[] buf, int start, int len ) throws SAXException {
		super.characters(buf,start,len);
		if( elementText!=null )
			// collect text
			elementText.append(buf,start,len);
	}
	
	/** this field matches more than once. */
	private void doubleMatchError() throws SAXException {
		int i;
		// compute the index number of this field.
		for( i=0; i<parent.children.length; i++ )
			if( parent.children[i]==this )
				break;
		
		owner.reportError( owner.ERR_DOUBLE_MATCH,
			new Object[]{
				parent.idConst.namespaceURI,
				parent.idConst.localName,
				new Integer(i+1)} );
	}
}
