/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ReferenceExp;
import com.sun.tranquilo.grammar.trex.TREXPatternPool;
import com.sun.tranquilo.reader.SequenceState;
import org.xml.sax.Locator;

/**
 * parses &lt;define&gt; element.
 */
public class DefineState extends SequenceState
{
	protected Expression annealExpression( Expression exp )
	{
		if(!startTag.containsAttribute("name"))
		{// name attribute is required.
			reader.reportError( TREXGrammarReader.ERR_MISSING_ATTRIBUTE,
				"ref","name");
			// recover by returning something that can be interpreted as Pattern
			return Expression.nullSet;
		}
		
		final TREXGrammarReader reader = (TREXGrammarReader)this.reader;
		final String name = startTag.getAttribute("name");
		final ReferenceExp ref = reader.grammar.namedPatterns.getOrCreate(name);
		final String combine = startTag.getAttribute("combine");
		
		if( ref.exp==null )
		{// this is the first time definition
			if( combine!=null )
				// "combine" attribute will be ignored
				reader.reportWarning( TREXGrammarReader.WRN_COMBINE_IGNORED, name );
			reader.setDeclaredLocationOf(ref);
			ref.exp = exp;
		}
		else
		{// some pattern is already defined under this name.
			if( reader.getDeclaredLocationOf(ref).getSystemId().equals(
					reader.locator.getSystemId() ) )
			{
				reader.reportError( reader.ERR_DUPLICATE_DEFINITION, name );
				// recovery by ignoring this definition
				return ref;
			}
			
			reader.setDeclaredLocationOf(ref);
			
			if( combine==null )
			{
				reader.reportError( new Locator[]{location, reader.getDeclaredLocationOf(ref)},
									TREXGrammarReader.ERR_COMBINE_MISSING, new Object[]{name} );
				// recover by ignoring this definition
			}
			else
			{// combine two patterns
				if( combine.equals("group") )
					ref.exp = reader.pool.createSequence( ref.exp, exp );
				else
				if( combine.equals("choice") )
					ref.exp = reader.pool.createChoice( ref.exp, exp );
				else
				if( combine.equals("replace") )
					ref.exp = exp;
				else
				if( combine.equals("interleave") )
					ref.exp = ((TREXPatternPool)reader.pool).createInterleave( ref.exp, exp );
				else
				if( combine.equals("concur") )
					ref.exp = ((TREXPatternPool)reader.pool).createConcur( ref.exp, exp );
				else
					reader.reportError( TREXGrammarReader.ERR_BAD_COMBINE, combine );
					// recover by ignoring this definition
			}
		}
		
		return ref;
	}
}
