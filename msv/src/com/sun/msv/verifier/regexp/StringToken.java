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
	
	public final String literal;
	public final IDContextProvider context;
	protected final REDocumentDeclaration docDecl;
	protected final boolean ignorable;
	
	/**
	 * if this field is non-null,
	 * this field will receive assigned DataType object.
	 */
	public final DataTypeRef refType;
	protected boolean saturated = false;
	
	private static final DataType[] ignoredType = new DataType[0];
	
	public StringToken( REDocumentDeclaration docDecl, String literal, IDContextProvider context ) {
		this(docDecl,literal,context,null);
	}
	
	public StringToken( REDocumentDeclaration docDecl, String literal, IDContextProvider context, DataTypeRef refType ) {
		this.docDecl = docDecl;
		this.literal = literal;
		this.context = context;
		this.refType = refType;
		this.ignorable = literal.trim().length()==0;
		
		if( ignorable )	refType.types = ignoredType;
	}
	
	/** TypedStringExp can consume this token if its datatype can accept this string */
	boolean match( TypedStringExp exp ) {
		
		if(!exp.dt.allows( literal, context )) return false; // not accepted.
		
		// this type accepts me.
		if(refType!=null)		assignType(exp.dt);
		
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
		
		// if the application needs type information,
		// collect them from children.
		DataTypeRef dtRef = null;
		DataType[] childTypes = null;
		int cnt=0;
		
		if( this.refType!=null ) {
			dtRef = new DataTypeRef();
			childTypes = new DataType[tokens.countTokens()];
		}
		
		while( tokens.hasMoreTokens() ) {
			StringToken child = createChildStringToken(tokens.nextToken(),dtRef);
			residual = docDecl.resCalc.calcResidual( residual, child );
			
			if( residual==Expression.nullSet )
				// the expression is failed to accept this item.
				return false;
			
			if( dtRef!=null ) {
				if( dtRef.types==null ) {
					// failed to assign type. bail out.
					saturated = true;
					refType.types = null;
					dtRef = null;
				} else {
					// type is successfully assigned for this child.
					if( dtRef.types.length!=1 )
						// the current RELAX NG prohibits to nest <list> patterns.
						// Thus it's not possible for this child to return more than one type.
						throw new Error();
					
					childTypes[cnt++] = dtRef.types[0];
				}
			}
		}
		
		if(!residual.isEpsilonReducible())
			// some expressions are still left. failed to accept this string.
			return false;
		
		// this <list> accepts this string.
		
		if( childTypes!=null ) {
			// assign datatype
			if(saturated)
				// a type is already assigned. That means this string has more than one type.
				// so bail out.
				refType.types=null;
			else
				refType.types = childTypes;
			saturated = true;
		}
		
		return true;
	}
	
	protected StringToken createChildStringToken( String literal, DataTypeRef dtRef ) {
		return new StringToken( docDecl, literal, context, dtRef );
	}

	// anyString can match any string
	boolean matchAnyString() {
		if(refType!=null)	assignType(StringType.theInstance);
		return true;
	}

	private void assignType( DataType dt ) {
		if(saturated) {
			if(refType.types[0]!=dt || refType.types.length!=1)
				// different types are assigned. roll back to null
				refType.types=null;
		} else {
			// this is the first assignment. remember this value.
			refType.types=new DataType[]{dt};
			saturated=true;
		}
	}
		
	/** checks if this token is ignorable.
	 * 
	 * StringToken is ignorable when it matches [ \t\r\n]*
	 */
	boolean isIgnorable() { return ignorable; }
}
