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

import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.xmlschema.XPath;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Base implementation of XPath matching engine.
 * 
 * It only supports the subset defined in XML Schema Part 1. Extra care
 * must be taken to call the testInitialMatch method after the creation of an object.
 * 
 * Match to an attribute is not supported. It is implemented in FieldPathMatcher
 * class.
 * 
 * The onMatched method is called when the specified XPath matches the current element.
 * Derived classes should implement this method to do something useful.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class PathMatcher extends Matcher {
	
	/**
	 * stores matched steps.
	 * first dimension is expanded as the depth goes deep.
	 * second dimension is always equal to the size of steps.
	 */
	protected boolean[][]		activeSteps;
	protected short				currentDepth=0;
	protected final XPath		path;
	
	protected PathMatcher( IDConstraintChecker owner, XPath path ) {
		super(owner);
		this.path = path;
		activeSteps = new boolean[4][];
		/*
			activeSteps[i][0] is used to represent an implicit "root".
			For example, when XPath is "//A/B",
		
				[0]:root		[1]:A		[2]:B
			
			(initial state)
					1			0			0		(1 indicates "active")
								[0] is initialized to 1.
			
			(startElement(X))
			(step:1 shift to right)
					1(*1)		1			0
							*1	[0] will be populated by isAnyDescendant field.
								In this case, since isAnyDescendant ("//") is true,
								[0] is set to true after shift. This indicates that
								new element X can possibly be used as the implicit root.
			(step:2 perform name test)
					1			0			0
								root is excluded from the test. Since A doesn't match
								X, the corresponding field is set to false.
			
			(startElement(A))
			(step:1 shift to right)
					1			1			0
			(step:2 perform name test)
					1			1			0
			
			(startElement(B))
			(step:1 shift to right)
					1			1			1
			(step:2 perform name test)
					1			0			1 (*2)
							*2	Now that the right most slot is true,
								this element B matches XPath.
		*/
		activeSteps[0] = new boolean[path.steps.length+1];
		activeSteps[0][0] = true;	// initialization
		// we only need an empty buffer for activeStep[0].
		// other slots are filled on demand.
	}
	
	/** this method should be called immediately after the constructor. */
	protected void testInitialMatch( Attributes atts ) throws SAXException {
		if(path.steps.length==0)
			// if the step is length 0, (that is, ".")
			// it is an immediate match.
			onMatched(null,null,atts);
		
	}
	
	protected void startElement( String namespaceURI, String localName, Attributes attributes )
													throws SAXException {
		if(currentDepth==activeSteps.length-1) {
			// if the buffer is used up, expand buffer
			boolean[][] newBuf = new boolean[currentDepth*2][];
			System.arraycopy( activeSteps, 0, newBuf, 0, activeSteps.length );
			activeSteps = newBuf;
		}
		currentDepth++;
		int len = path.steps.length;
		
		boolean[] prvBuf = activeSteps[currentDepth-1];
		boolean[] curBuf = activeSteps[currentDepth];
		if(curBuf==null)	activeSteps[currentDepth]=curBuf=new boolean[len+1/*implicit root*/];
		
		// shift to right
		if(len!=0) {
			System.arraycopy(
				prvBuf, 0, curBuf, 1, len );
			curBuf[0] = path.isAnyDescendant;
		}
		
		// perform name test and deactivate unmatched steps
		for( int i=1; i<=len; i++ )
			// exclude root from test.
			if( curBuf[i] && !path.steps[i-1].accepts(namespaceURI,localName) )
				curBuf[i] = false;
		
		if( curBuf[len] ) {
			// this element matched this path
			onMatched(namespaceURI,localName,attributes);
		}
	}
	
	protected abstract void onMatched(
		String namespaceURI, String localName, Attributes attributes )
		throws SAXException;
	
	protected void endElement() {
		currentDepth--;
	}
}
