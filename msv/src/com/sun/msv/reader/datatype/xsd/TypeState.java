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
import org.relaxng.datatype.DatatypeException;
import com.sun.msv.reader.State;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.datatype.TypeOwner;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.util.StartTagInfo;

/**
 * Base implementation for those states which produce DataType object
 * as its parsing result.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class TypeState extends SimpleState
{
	public void endSelf() {
		super.endSelf();
		
		XSDatatype type = _makeType();
		
		if( parentState instanceof TypeOwner ) {
			// if the parent can understand what we are creating,
			// then pass the result.
			((TypeOwner)parentState).onEndChild(type);
			return;
		} else
		if( parentState instanceof ExpressionOwner ) {
			if( type instanceof LateBindDatatype ) {
				// if this is a late-bind datatype,/
				// return a temporary ReferenceExp now and
				// perform a back-patching later to complete AGM.
				final ReferenceExp ref = new ReferenceExp(null);
				((ExpressionOwner)parentState).onEndChild(ref);
				
				final LateBindDatatype lbdt = (LateBindDatatype)type;
				
				// register a patcher to render the real object.
				reader.addBackPatchJob(
					new GrammarReader.BackPatch() {
						public State getOwnerState() { return TypeState.this; }
						public void patch() {
							// perform binding.
							ref.exp = reader.pool.createTypedString(lbdt.getBody());
						}
				});
				
			} else {
				// if the parent expects Expression, convert type into Expression
				((ExpressionOwner)parentState).onEndChild(
					reader.pool.createTypedString(type) );
			}
			return;
		}
		
		// we have no option to let the parent state know our result.
		throw new Error();
	}
	
	/** the makeType method with protection against possible exception. */
	XSDatatype _makeType() {
		try {
			return makeType();
		} catch( DatatypeException be ) {
			reader.reportError( be, reader.ERR_BAD_TYPE );
			return StringType.theInstance;	// recover by assuming a valid type.
		}
	}
		
	/**
	 * This method is called from endElement method.
	 * Implementation has to provide DataType object that represents the content of
	 * this element.
	 */
	protected abstract XSDatatype makeType() throws DatatypeException;


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
	
