/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;

import java.io.Serializable;
import java.io.InvalidObjectException;

/**
 * processes white space normalization
 * 
 * @author Kohsuke KAWAGUCHI
 */
public abstract class WhiteSpaceProcessor implements Serializable {
	
	/**
	 * returns whitespace normalized text.
	 * behavior varies on what normalization mode is used.
	 */
	public abstract String process( String text );
	
	/** higher return value indicates tigher constraint */
	abstract int tightness();
	
	/**
	 * gets the name of the white space processing mode.
	 * It is one of "preserve","collapse", or "replace".
	 */
	public abstract String getName();
	
	/**
	 * returns a WhiteSpaceProcessor object if "whiteSpace" facet is specified.
	 * Otherwise returns null.
	 */
	protected static WhiteSpaceProcessor get( String name )
		throws BadTypeException {
		name = theCollapse.process(name);
		if( name.equals("preserve") )		return thePreserve;
		if( name.equals("collapse") )		return theCollapse;
		if( name.equals("replace") )		return theReplace;
		
		throw new BadTypeException( BadTypeException.ERR_INVALID_WHITESPACE_VALUE, name );
	}
	
	/** returns true if the specified char is a white space character. */
	protected static final boolean isWhiteSpace( char ch ) {
		return ch==0x9 || ch==0xA || ch==0xD || ch==0x20;
	}
	
	
	protected Object readResolve() throws InvalidObjectException {
		// return the singleton instead of deserialized object.
		try {
			return get(getName());
		} catch( BadTypeException bte ) {
			throw new InvalidObjectException("Unknown Processing Mode");
		}
	}
	
/*
	Actual processor implementation
*/
	
	public final static WhiteSpaceProcessor thePreserve = new WhiteSpaceProcessor() {
		public String process( String text )	{ return text; }
		int tightness() { return 0; }
		public String getName() { return "preserve"; }
	};
	
	public final static WhiteSpaceProcessor theReplace = new WhiteSpaceProcessor() {
		public String process( String text ) {
			final int len = text.length();
			StringBuffer result = new StringBuffer(len);
			
			for( int i=0; i<len; i++ )
				if( super.isWhiteSpace(text.charAt(i)) )
					result.append(' ');
				else
					result.append(text.charAt(i));
			
			return result.toString();		
		}
		int tightness() { return 1; }
		public String getName() { return "replace"; }
	};

	public final static WhiteSpaceProcessor theCollapse= new WhiteSpaceProcessor() {
		public String process( String text ) {
			char[] chars = text.toCharArray();
			int len = text.length();
			StringBuffer result = new StringBuffer(len /**enough size*/ );
			
			boolean inStripMode = true;
			
			for( int i=0; i<len; i++ ) {
				boolean b = WhiteSpaceProcessor.isWhiteSpace(chars[i]);
				if( inStripMode && b )
					continue;	// skip this character
				
				inStripMode = b;
				if( inStripMode )	result.append(' ');
				else				result.append(chars[i]);
			}
			
			// remove trailing whitespaces
			len = result.length();
			if( len>0 && result.charAt(len-1)==' ' )
				result.setLength(len-1);
			// whitespaces are already collapsed,
			// so all we have to do is to remove the last one character
			// if it's a whitespace.
			
			return result.toString();
		}
		int tightness() { return 2; }
		public String getName() { return "collapse"; }
	};
}

