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
		
		final String name = startTag.getAttribute("name");
		final ReferenceExp ref = ((TREXGrammarReader)reader).grammar.namedPatterns.getOrCreate(name);
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
