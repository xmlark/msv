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
 * "QName" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#QName for the spec.
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class QnameType extends ConcreteType implements Discrete
{
	public static final QnameType theInstance = new QnameType();
	private QnameType() { super("QName"); }

	private static String getNamespaceURI( String content, ValidationContextProvider context )
	{
		return context.resolveNamespacePrefix( content.substring(0, content.indexOf(':')) );
	}
	
	protected boolean checkFormat( String value, ValidationContextProvider context )
	{
		// [6] QName ::= (Prefix ':')? LocalPart
		// [7] Prefix ::= NCName
		// [8] LocalPart ::= NCName

		final int first = value.indexOf(':');

		// no Prefix, only check LocalPart
		if(first <= 0)		return XmlNames.isUnqualifiedName(value);

		// Prefix exists, check everything
		final int	last = value.lastIndexOf(':');
		if (last != first)	return false;

		final String prefix = value.substring (0, first);
		return XmlNames.isUnqualifiedName(prefix)
			&& XmlNames.isUnqualifiedName(value.substring (first + 1))
			&& context.resolveNamespacePrefix(prefix)!=null;
	}
	
	public Object convertToValue( String value, ValidationContextProvider context ) {
		String uri,localPart;
		// [6] QName ::= (Prefix ':')? LocalPart
		// [7] Prefix ::= NCName
		// [8] LocalPart ::= NCName

		final int first = value.indexOf(':');

		if(first <= 0)
		{// no Prefix, only check LocalPart
			if(!XmlNames.isUnqualifiedName(value))	return null;
			uri = context.resolveNamespacePrefix("");
			localPart = value;
		}
		else
		{// Prefix exists, check everything
			final int	last = value.lastIndexOf (':');
			if (last != first)	return null;
			
			final String prefix = value.substring(0, first);
			localPart = value.substring(first + 1);
			
			if(!XmlNames.isUnqualifiedName(prefix)
			|| !XmlNames.isUnqualifiedName(localPart) )
				return null;
			
			uri = context.resolveNamespacePrefix(prefix);
		}
		
		if(uri==null)	return null;
		
		return new QnameValueType(uri,localPart);
	}
	
	public String convertToLexicalValue( Object o ) {
		// there is no way to convert QName into a string.
		throw new UnsupportedOperationException();
	}
	
	public final int isFacetApplicable( String facetName )
	{
		if( facetName.equals(FACET_PATTERN)
		||	facetName.equals(FACET_ENUMERATION)
		||	facetName.equals(FACET_LENGTH)
		||	facetName.equals(FACET_MAXLENGTH)
		||	facetName.equals(FACET_MINLENGTH)
		)
			return APPLICABLE;
		else
			return NOT_ALLOWED;
	}
	
	public final int countLength( Object value )
	{
		QnameValueType v = (QnameValueType)value;
		
		// the spec does not define what is the unit of length.
		// TODO: check the update of the spec and modify this if necessary.
		return	UnicodeUtil.countLength( v.namespaceURI )+
				UnicodeUtil.countLength( v.localPart );
	}
}
