/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype;

/**
 * diagnostic information of datatype validation errror.
 * 
 * @author	Kohsuke Kawaguchi
 */
public class DataTypeErrorDiagnosis
{
	/** lexcial representation of data that caused an error */
	public final String content;
	
	/** against which datatype validation was performed? */
	public final DataType type;
	
	/**	diagnosis message */
	public final String message;
	
	/** column number of the error source
	 *
	 * if this value is UNKNOWN, it means no column number information is available.
	 */
	public final int column;
	
	public static int UNKNOWN = -1;
	
	public DataTypeErrorDiagnosis( DataType type, String content, int column,
		String msg )
	{
		this.content = content;
		this.type = type;
		this.column = column;
		this.message = msg;
	}
}
