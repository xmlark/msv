/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex;

import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.datatype.xsd.WhiteSpaceProcessor;

/**
 * parses &lt;name&gt; name class.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NameClassNameState extends NameClassWithoutChildState {
	protected final StringBuffer text = new StringBuffer();
	
	public void characters( char[] buf, int from, int len ) {
		text.append(buf,from,len);
	}
	public void ignorableWhitespace( char[] buf, int from, int len ) {
		text.append(buf,from,len);
	}

	protected NameClass makeNameClass() {
		return new SimpleNameClass(
			getPropagatedNamespace(),
			WhiteSpaceProcessor.theReplace.process(new String(text)) );
			
	}
}
