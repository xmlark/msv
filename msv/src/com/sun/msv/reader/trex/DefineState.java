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

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.reader.SequenceState;
import org.xml.sax.Locator;

/**
 * parses &lt;define&gt; declaration.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class DefineState extends SequenceState {
	
	protected ReferenceExp getReference() {
		final String name = startTag.getCollapsedAttribute("name");
		
		if(name==null) {
			// name attribute is required.
			reader.reportError( reader.ERR_MISSING_ATTRIBUTE,
				"ref","name");
			// recover by returning something that can be interpreted as Pattern
			return null;
		}
		
		final TREXBaseReader reader = (TREXBaseReader)this.reader;
		return reader.grammar.namedPatterns.getOrCreate(name);
	}
	
	protected Expression annealExpression( Expression exp ) {

		final TREXBaseReader reader = (TREXBaseReader)this.reader;
		final ReferenceExp ref = getReference();
		final String combine = startTag.getCollapsedAttribute("combine");
		
		if(ref==null)	return Expression.nullSet;
		
		// combine two patterns
		Expression newexp = doCombine( ref, exp, combine );
		if( newexp==null )
			reader.reportError( reader.ERR_BAD_COMBINE, combine );
			// recover by ignoring this definition
		else
			ref.exp = newexp;
	
		reader.setDeclaredLocationOf(ref);
		
		return ref;
	}
	
	/**
	 * combines two expressions into one as specified by the combine parameter,
	 * and returns a new expression.
	 * 
	 * If the combine parameter is invalid, then return null.
	 */
	protected abstract Expression doCombine( ReferenceExp baseExp, Expression newExp, String combine );
}
