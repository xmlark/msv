/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.datatype.xsd;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import com.sun.msv.reader.State;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.datatype.TypeOwner;
import com.sun.msv.datatype.DataTypeImpl;
import com.sun.msv.datatype.BadTypeException;
import com.sun.msv.datatype.StringType;
import com.sun.msv.util.StartTagInfo;

/**
 * Base implementation for those states which produce DataType object
 * as its parsing result.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class TypeState extends SimpleState
{
	public void endSelf()
	{
		super.endSelf();
		
		if( parentState instanceof TypeOwner ) {
			// if the parent can understand what we are creating,
			// then pass the result.
			((TypeOwner)parentState).onEndChild( _makeType() );
			return;
		} else
		if( parentState instanceof ExpressionOwner ) {
			// if the parent expects Expression, convert type into Expression
			((ExpressionOwner)parentState).onEndChild(
				reader.pool.createTypedString( _makeType() ) );
			return;
		}
		
		// we have no option to let the parent state know our result.
		throw new Error();
	}
	
	/** makeType method with protection against possible exception. */
	DataTypeImpl _makeType() {
		try {
			return makeType();
		} catch( BadTypeException be ) {
			reader.reportError( be, reader.ERR_BAD_TYPE );
			return StringType.theInstance;	// recover by assuming a valid type.
		}
	}
		
	/**
	 * This method is called from endElement method.
	 * Implementation has to provide DataType object that represents the content of
	 * this element.
	 */
	protected abstract DataTypeImpl makeType() throws BadTypeException;


	public final void startElement( String namespaceURI, String localName, String qName, Attributes atts )
	{// within the island of XSD, foreign namespaces are prohibited.
		final StartTagInfo tag = new StartTagInfo(
			namespaceURI,localName,qName,new AttributesImpl(atts),null);
		// we have to copy Attributes, otherwise it will be mutated by SAX parser
			
		State nextState = createChildState(tag);
		if(nextState!=null)
		{
			reader.pushState(nextState,this,tag);
			return;
		}
				
		// unacceptable element
		reader.reportError(reader.ERR_MALPLACED_ELEMENT, tag.qName );
		// try to recover from error by just ignoring it.
		// element of a foreign namespace. skip subtree
		reader.pushState(new IgnoreState(),this,tag);
	}
}
	
