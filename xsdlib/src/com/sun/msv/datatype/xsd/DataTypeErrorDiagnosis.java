/*
 * Tranquilo : RELAX Verifier           written by Kohsuke Kawaguchi
 *                                           k-kawa@bigfoot.com
 *
 * Copyright 2000 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.sun.tranquilo.datatype;

/**
 * diagnostic information of datatype validation errror
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
	
	protected DataTypeErrorDiagnosis( DataType type, String content, int column,
		String resourcePropertyName, Object[] args )
	{
		this.content = content;
		this.type = type;
		this.column = column;
		this.message = java.text.MessageFormat.format(
			java.util.ResourceBundle.getBundle("com.sun.tranquilo.resources.datatype").getString(resourcePropertyName),
			args );
	}
	
	
}
