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

import com.sun.msv.reader.ChildlessState;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.State;
import com.sun.msv.grammar.Expression;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;include&gt; element as a child of &lt;grammar&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IncludeMergeState extends com.sun.msv.reader.trex.IncludeMergeState
			implements ExpressionOwner {
	
	protected State createChildState( StartTagInfo tag ) {
		final RELAXNGReader reader = (RELAXNGReader)this.reader;
		if(tag.localName.equals("define"))	return reader.getStateFactory().redefine(this,tag);
		return null;
	}
	
	public void onEndChild( Expression child ) {}
}
