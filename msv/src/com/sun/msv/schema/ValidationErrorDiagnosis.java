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
package com.sun.tranquilo.schema;

import com.sun.tranquilo.datatype.DataTypeErrorDiagnosis;
	
/**
 * diagnostic information of datatype validation errror
 */
public class ValidationErrorDiagnosis
{
	/**	diagnosis message */
	public final String message;
	/** underlying DataTypeErrorDiagnosis, if any (otherwise null) */
	public final DataTypeErrorDiagnosis dataTypeError;
	
	protected ValidationErrorDiagnosis( DataTypeErrorDiagnosis dtError, String resourcePropertyName, Object[] args )
	{
		this.dataTypeError = dtError;
		this.message = java.text.MessageFormat.format(
			java.util.ResourceBundle.getBundle("com.sun.tranquilo.schema.Diagnosis").getString(resourcePropertyName),
			args );
	}
	protected ValidationErrorDiagnosis( String resourcePropertyName, Object[] args )
	{
		this( null, resourcePropertyName, args );
	}
	protected ValidationErrorDiagnosis( DataTypeErrorDiagnosis dtError, String resourcePropertyName, Object arg1 )
	{
		this( dtError, resourcePropertyName, new Object[]{arg1} );
	}
	protected ValidationErrorDiagnosis( String resourcePropertyName, Object arg1 )
	{
		this( null, resourcePropertyName, new Object[]{arg1} );
	}
	
	public static final String MSG_Attribute_RequiredAttributeIsMissing
					=	"Diagnosis.Attribute.RequiredAttributeIsMissing";
	public static final String MSG_Attribute_AttributeValueIsInvalid
					=	"Diagnosis.Attribute.AttributeValueIsInvalid";
}
