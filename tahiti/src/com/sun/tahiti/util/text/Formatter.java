/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.util.text;

public class Formatter
{
	/**
	 * formats a text.
	 * 
	 * acts like java.text.MessageFormat#format, but more flexible.
	 * 
	 * Each occurence of &lt;%foo&gt; is replaced by the corresponding
	 * string supplied by a Model object.
	 */
	public static String format( String fmt, Model model ) {
		int idx=0;
		StringBuffer out = new StringBuffer();
		
		while( (idx=fmt.indexOf("<%")) >= 0 ) {
			
			out.append( fmt.substring(0,idx) );
			
			fmt = fmt.substring(idx+2);
			int last = fmt.indexOf(">");
			if(last==-1)
				throw new IllegalArgumentException("end tag not found");
			
			String replace = model.getParameter( fmt.substring(0,last) );
			out.append(replace);
			
			fmt = fmt.substring(last+1);
		}
		
		out.append(fmt);
		
		return out.toString();
	}
	
	/**
	 * simple version of the format method.
	 * 
	 * This version accepts parameters like &lt;%1> &lt;%2> and replaces
	 * them by the corresponding item in the array.
	 */
	public static String format( String fmt, final Object[] args ) {
		return format( fmt,
			new Model(){
				public String getParameter( String arg ) {
					return args[Integer.parseInt(arg)].toString();
				}
			});
	}
}
