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

import com.sun.msv.reader.SequenceState;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.KeyExp;
import com.sun.msv.util.StringPair;

/**
 * state that parses &lt;key&gt; and &lt;keyref&gt; pattern of RELAX NG.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class KeyState extends SequenceState {
	
	public KeyState( boolean isKey ) {
		this.isKey = isKey;
	}
	
	private final boolean isKey;
	
	protected Expression annealExpression( Expression body ) {
		final RELAXNGReader reader = (RELAXNGReader)this.reader;

		String name = startTag.getAttribute("name");
		if(name==null) {
			reader.reportError( reader.ERR_MISSING_ATTRIBUTE, startTag.qName, "name" );
			return Expression.nullSet;
		}
		
		String[] key;
		if( name.indexOf(':')>=0 )
			key = reader.splitQName(name);
		else
			key = new String[]{reader.getTargetNamespace(),name,name};
				
		if( key==null ) {
			reader.reportError( reader.ERR_UNDECLARED_PREFIX, name );
			return Expression.nullSet;
		}
		
		Expression exp;
		if( isKey )
			exp = reader.pool.createKey( body, new StringPair( key[0], key[1] ) );
		else
			exp = reader.pool.createKeyref( body, new StringPair( key[0], key[1] ) );
		
		if( exp instanceof KeyExp )
			reader.keyKeyrefs.add(exp);
		reader.setDeclaredLocationOf(exp);
		return exp;
	}
}
