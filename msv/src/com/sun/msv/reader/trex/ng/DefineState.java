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
 * parses &lt;define&gt; declaration.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DefineState extends com.sun.msv.reader.trex.DefineState {
	
	/**
	 * combines two expressions into one as specified by the combine parameter,
	 * and returns a new expression.
	 * 
	 * If the combine parameter is invalid, then return null.
	 */
	protected Expression doCombine( ReferenceExp baseExp, Expression newExp, String combine ) {
		
		final RELAXNGReader reader = (RELAXNGReader)this.reader;
		
		
		if( combine==null ) {
			// this is a head declaration
			if( reader.headRefExps.contains(baseExp) ) {
				// two head declarations: an error.
				reader.reportError( reader.ERR_COMBINE_MISSING, baseExp.name );
				return baseExp.exp;
			}
			reader.headRefExps.add(baseExp);
		}
		
		if( combine!=null ) {
			// check the consistency of the combine method.
			String prevCombine = (String)reader.combineMethodMap.get(baseExp);
			
			if( prevCombine==null )
				reader.combineMethodMap.put(baseExp,combine);
			else
				if( !combine.equals(prevCombine) ) {
					// different combine method.
					reader.reportError( new Locator[]{location, reader.getDeclaredLocationOf(baseExp)},
								reader.ERR_INCONSISTENT_COMBINE, new Object[]{baseExp.name} );
					return baseExp.exp;
				}
		} else {
			// get the combine method.
			combine = (String)reader.combineMethodMap.get(baseExp);
		}
		
		if( baseExp.exp!=null ) {
			// make sure that the previous definition was in a different file.
			if( reader.getDeclaredLocationOf(baseExp).getSystemId().equals(
					reader.locator.getSystemId() ) ) {
				reader.reportError( reader.ERR_DUPLICATE_DEFINITION, baseExp.name );
				// recovery by ignoring this definition
				return baseExp.exp;
			}
		}
		
		
		if( baseExp.exp==null )	// the first definition
			return newExp;
		else
		if( combine.equals("choice") )
			return reader.pool.createChoice( baseExp.exp, newExp );
		else
		if( combine.equals("interleave") )
			return reader.pool.createInterleave( baseExp.exp, newExp );
		else
			return null;
	}
}
