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

import org.xml.sax.SAXException;
import org.xml.sax.Locator;

public class SchemaParseException extends SAXException
{
	/**
	 * throw an SchemaParseException
	 * 
	 * this method is solely for a short-hand purpose
	 */
	protected static void raise( XMLElement element, String errorName, Object[] args )
		throws SchemaParseException
	{
		throw new SchemaParseException( element, errorName, args );
	}

	protected static void raise( Locator locator, String errorName, Object[] args )
		throws SchemaParseException
	{
		throw new SchemaParseException( locator, errorName, args );
	}

	protected static void raise( XMLElement element, String errorName, Object arg1 )
		throws SchemaParseException
	{
		throw new SchemaParseException( element, errorName, new Object[]{arg1} );
	}
	
	protected static void raise( XMLElement element, String errorName, Object arg1, Object arg2 )
		throws SchemaParseException
	{
		throw new SchemaParseException( element, errorName, new Object[]{arg1,arg2} );
	}
	
	protected static void raise( XMLElement element, Exception nestedException )
		throws SchemaParseException
	{
		throw new SchemaParseException( element, nestedException );
	}
	
	
	
	private SchemaParseException( XMLElement element, String errorName, Object[] args )
	{
		super( java.text.MessageFormat.format(
			java.util.ResourceBundle.getBundle("com.sun.tranquilo.schema.Messages").getString(errorName),
			args ) );
		this.XMLElement = element;
		this.locator = element.locator;
	}
	
	private SchemaParseException( Locator locator, String errorName, Object[] args )
	{
		super( java.text.MessageFormat.format(
			java.util.ResourceBundle.getBundle("com.sun.tranquilo.schema.Messages").getString(errorName),
			args ) );
		this.XMLElement = null;
		this.locator = locator;
	}
	
	private SchemaParseException( XMLElement element, Exception nestedException )
	{
		super(nestedException);
		this.XMLElement = element;
		this.locator	= element.locator;
	}
	
	/** source location of error */
	public final Locator locator;
	
	/** information about error source.
	 * 
	 * Can be null if the error source is not an XML element
	 */
	public final XMLElement XMLElement;
	
	
	
	// error messages
	public static final String ERR_UNRECOGNIZED_ELEMENT
		= "SchemaParseException.UnrecognizedElement";
	public static final String ERR_MISSING_ATTRIBUTE
		= "SchemaParseException.MissingAttribute";
	public static final String ERR_LABEL_AND_ROLE_ARE_MISSING
		= "SchemaParseException.LabelAndRoleAreMissing";
	public static final String ERR_UNDEFINED_ROLE
		= "SchemaParseException.UndefinedRole";
	public static final String ERR_UNDEFINED_LABEL
		= "SchemaParseException.UndefinedLabel";
	public static final String ERR_UNRECOGNIZED_CORE_VERSION
		= "SchemaParseException.UnrecognizedCoreVersion";
	public static final String ERR_NO_PARTICLE
		= "SchemaParseException.NoParticle";
	public static final String ERR_MORE_THAN_ONE_PARTICLE
		= "SchemaParseException.MoreThanOneParticle";
	public static final String ERR_EXPORT_HAS_NO_RECOGNIZABLE_ATTRIBUTE
		= "SchemaParseException.ExportHasNoRecognizableAttribute";
	public static final String ERR_UNDEFINED_FACET
		= "SchemaParseException.UndefinedFacet";
	public static final String ERR_ILLEGAL_MIXED
		= "SchemaParseException.IllegalMixed";
	public static final String ERR_LOCAL_TAG_NOT_ALLOWED
		= "SchemaParseException.LocalTagNotAllowed";
	public static final String ERR_PCDATA_NOT_ALLOWED
		= null;
	public static final String ERR_NON_PARTICLE_IN_MIXED
		= null;	// content of "mixed" is invalid. (or, mixed cannot be a child of mixed)
	public static final String ERR_NON_PARTICLE_IN_HEDGERULE
		= null; // content of "hedgeRule" is invalid.
	public static final String ERR_CANNOT_HAVE_CONTENT
		= null;	// element {0} cannot have any child element
	public static final String ERR_UNDEFINED_DATATYPE
		= null; // datatype {0} is either wrong or not implemented.
	public static final String ERR_MALPLACED_ELEMENT
		= null; // element {0} cannot appear here.
	public static final String ERR_ILLEGAL_OCCURS
		= null;	// occurs attribute has invalid value '{0}'
	public static final String ERR_IO_EXCEPTION
		= null; // error while parsing schema: {0}
	public static final String ERR_SAX_EXCEPTION
		= null; // SAX error while parsing schema: {0}
	public static final String ERR_EXPORT_WITHOUT_LABEL_NOR_ROLE
		= null; // 'export' element must have "role" or "label" attribute.
	public static final String ERR_UNDEFINED_NAMESPACE
		= null;	// namespace URI '{0}' is not defined.
}
