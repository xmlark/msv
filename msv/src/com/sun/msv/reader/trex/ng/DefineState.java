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
		RELAXNGReader.RefExpParseInfo info = reader.getRefExpParseInfo(baseExp);
		
		if( baseExp.exp!=null ) {
			// make sure that the previous definition was in a different file.
			if( reader.getDeclaredLocationOf(baseExp).getSystemId().equals(
					reader.locator.getSystemId() ) ) {
				reader.reportError( reader.ERR_DUPLICATE_DEFINITION, baseExp.name );
				// recovery by ignoring this definition
				return baseExp.exp;
			}
		}
		
		
		if( combine==null ) {
			// this is a head declaration
			if( info.haveHead ) {
				// two head declarations: an error.
				reader.reportError( reader.ERR_COMBINE_MISSING, baseExp.name );
				return baseExp.exp;
			}
			info.haveHead = true;
		} else {
			// check the consistency of the combine method.
			
			if( info.combineMethod==null ) {
				// If this is the first time @combine is used for this pattern...
				info.combineMethod = combine.trim();
				// make sure that the value is ok.
				if( !info.combineMethod.equals("choice")
				&&	!info.combineMethod.equals("interleave") )
					reader.reportError( reader.ERR_BAD_COMBINE, info.combineMethod );
			} else {
				if( !info.combineMethod.equals(combine) ) {
					// different combine method.
					reader.reportError( new Locator[]{location, reader.getDeclaredLocationOf(baseExp)},
								reader.ERR_INCONSISTENT_COMBINE, new Object[]{baseExp.name} );
					return baseExp.exp;
				}
			}
		}
			
		if( baseExp.exp==null )	// the first definition
			return newExp;
		
		if( info.redefinition!=info.notBeingRedefined ) {
			// ignore the new definition
			// because this definition is currently being redefined by
			// the caller.
			
			// the original definition was found.
			info.redefinition = info.originalFound;
			return baseExp.exp;
		}
		
		if( info.combineMethod.equals("choice") )
			return reader.pool.createChoice( baseExp.exp, newExp );
		
		if( info.combineMethod.equals("interleave") )
			return reader.pool.createInterleave( baseExp.exp, newExp );
		
		// some kind of error.
		return null;
	}
}
