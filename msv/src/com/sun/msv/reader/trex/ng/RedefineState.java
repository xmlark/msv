/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.ng;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.reader.SequenceState;
import org.xml.sax.Locator;

/**
 * parses &lt;define&gt; declaration under a &lt;include&gt; declaration.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RedefineState extends com.sun.msv.reader.trex.DefineState {
	
	protected Expression doCombine( ReferenceExp baseExp, Expression newExp, String combine ) {
		
		final RELAXNGReader reader = (RELAXNGReader)this.reader;
		
		if( combine!=null ) {
			// combine must be null.
			reader.reportError( reader.ERR_DISALLOWED_ATTRIBUTE, startTag.localName, "combine" );
			return newExp;
		}
		
		// make sure that this ReferenceExp is already defined.
		if( baseExp.exp==null ) {
			reader.reportError( reader.ERR_REDEFINING_UNDEFINED, baseExp.name );
			return newExp;
		}
		
		// makes it a head element, and reset the combine method.
		reader.headRefExps.remove(baseExp);
		reader.combineMethodMap.remove(baseExp);
		
		return newExp;
	}
}
