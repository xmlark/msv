package com.sun.tranquilo.verifier.regexp.trex;

import com.sun.tranquilo.datatype.ValidationContextProvider;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ElementExp;
import com.sun.tranquilo.verifier.regexp.StringToken;
import com.sun.tranquilo.verifier.regexp.ElementToken;
import com.sun.tranquilo.verifier.regexp.CombinedChildContentExpCreator;
import com.sun.tranquilo.util.StringRef;
import com.sun.tranquilo.util.DataTypeRef;

final class ComplexAcceptor extends ContentModelAcceptor
{
	protected final Expression[]	contents;
	protected final ElementExp[]	owners;
	
	public ComplexAcceptor(
		TREXDocumentDeclaration docDecl,
		Expression combined,
		CombinedChildContentExpCreator.OwnerAndContent primitives )
	{
		super( docDecl, combined );
		
		int i=0;
		for( CombinedChildContentExpCreator.OwnerAndContent o = primitives;
			 o!=null; o=o.next )	i++;
		
		contents = new Expression[i];
		owners = new ElementExp[i];
		
		i=0;
		for( CombinedChildContentExpCreator.OwnerAndContent o = primitives;
			 o!=null; o=o.next )
		{
			contents[i] = o.content;
			owners[i] = o.owner;
			i++;
		}
	}
	
	/** eats string literal */
	public boolean stepForward( String literal, ValidationContextProvider context, StringRef refErr, DataTypeRef refType )
	{
		if(!super.stepForward(literal,context,refErr,refType))	return false;

		final StringToken token = new StringToken(literal,context);

		// some may become invalid, but at least one always remain valid
		for( int i=0; i<contents.length; i++ )
			contents[i] = docDecl.getResidualCalculator().calcResidual( contents[i], token );
		
		return true;
	}
}
