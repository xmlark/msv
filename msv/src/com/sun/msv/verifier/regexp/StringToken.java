/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.regexp;

import com.sun.msv.grammar.*;
import com.sun.msv.grammar.relaxng.NGTypedStringExp;
import com.sun.msv.datatype.StringType;
import com.sun.msv.util.DataTypeRef;
import org.relaxng.datatype.DataType;
import java.util.StringTokenizer;

/**
 * chunk of string.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class StringToken extends Token {
	
	protected final String literal;
	protected final IDContextProvider context;
	protected final REDocumentDeclaration docDecl;
	protected final boolean ignorable;
	/**
	 * if this field is non-null,
	 * this field will receive assigned DataType object.
	 */
	protected final DataTypeRef refType;
	protected boolean saturated = false;
	
	public StringToken( REDocumentDeclaration docDecl, String literal, IDContextProvider context ) {
		this(docDecl,literal,context,null);
	}
	
	public StringToken( REDocumentDeclaration docDecl, String literal, IDContextProvider context, DataTypeRef refType ) {
		this.docDecl = docDecl;
		this.literal = literal;
		this.context = context;
		this.refType = refType;
		this.ignorable = literal.trim().length()==0;
	}
	
	/** TypedStringExp can consume this token if its datatype can accept this string */
	boolean match( TypedStringExp exp ) {
		if(!exp.dt.allows( literal, context ))	return false;
		if(refType!=null)	assignType(exp.dt);
		
		boolean ret = true;
		
		// ID/IDREF constraint check.
		if( exp instanceof NGTypedStringExp ) {
			// if this expression is key/keyref of RELAX NG,
			NGTypedStringExp texp = (NGTypedStringExp)exp;
			
			// then report detected key/keyrefs.
			if( texp.keyName!=null )
				ret = context.onID( texp.keyName,
					exp.dt.createValue(literal,context) );
			
			if( texp.keyrefName!=null )
				context.onIDREF( texp.keyrefName,
					exp.dt.createValue(literal,context) );
		}
		return ret;
	}

	/** ListExp can consume this token if its pattern accepts this string */
	boolean match( ListExp exp ) {
		StringTokenizer tokens = new StringTokenizer(literal);
		Expression residual = exp.exp;
	
		while( residual!=Expression.nullSet && tokens.hasMoreTokens() ) {
			residual = docDecl.resCalc.calcResidual( residual,
				createChildStringToken(tokens.nextToken()) );
		}
		
		return residual.isEpsilonReducible();
	}
	
	protected Token createChildStringToken( String literal ) {
		return new StringToken( docDecl, literal, context );
	}

	// anyString can match any string
	boolean matchAnyString() {
		if(refType!=null)	assignType(StringType.theInstance);
		return true;
	}

	private void assignType( DataType dt ) {
		if(saturated)
			// more than one types are assigned. roll back to null
			refType.type=null;
		else {
			// this is the first assignment. remember this value.
			refType.type=dt;
			saturated=true;
		}
	}
		
	/** checks if this token is ignorable.
	 * 
	 * StringToken is ignorable when it matches [ \t\r\n]*
	 */
	boolean isIgnorable() { return ignorable; }
}
