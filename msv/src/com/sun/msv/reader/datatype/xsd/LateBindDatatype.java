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

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.DataTypeWithFacet;
import com.sun.msv.datatype.xsd.ConcreteType;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.datatype.SerializationContext;
import com.sun.msv.reader.State;
import org.relaxng.datatype.*;
import org.xml.sax.Locator;

/**
 * Place holder datatype object.
 * 
 * <p>
 * This object is used when a datatype is referenced but not defined yet.
 * It holds a reference to the renderer, which is responsible for creating
 * actual XSDatatype object.
 * 
 * <p>
 * Forward-references between simple types can only occur in XML Schema.
 * So this quick-hack is used only in XML Schema reader.
 * 
 * <p>
 * If the schema language is forward-reference free (e.g., RELAX NG, TREX),
 * then there is no need to worry about LateBindDatatype.
 *
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class LateBindDatatype implements XSDatatype {
	
	/** this object renders the actual datatype object. */
	public interface Renderer {
		XSDatatype render() throws DatatypeException;
	}
	
	/**
	 * the real datatype object. A LateBindDatatype object
	 * works as a place-holder for this datatype object.
	 */
	private XSDatatype	body = null;
	
	/**
	 * State object that creates this late-binding object.
	 * The source location of this state is used for error message.
	 */
	private final State ownerState;

	/**
	 * Once the parsing is completed, this function object should
	 * be able to render the actual datatype object.
	 */
	private final Renderer renderer;
	
	/**
	 * @param renderer
	 *		Upon the completion of the parsing, this renderer should be
	 *		able to produce a real datatype object.
	 */
	public LateBindDatatype( Renderer renderer, State ownerState ) {
		this.renderer = renderer;
		this.ownerState = ownerState;
	}
	
	/**
	 * gets the actual definition.
	 * This method cannot be called before the "wrapUp" method.
	 */
	public XSDatatype getBody() {
		if(body==null)
			try {
				body = renderer.render();
			} catch( DatatypeException e ) {
				ownerState.reader.reportError( ownerState.reader.ERR_BAD_TYPE,
					new Object[]{e}, e, new Locator[]{ownerState.getLocation()} );
				body = StringType.theInstance;	// recover by assuming a valid type.
			}
		
		// DBG
		if( body instanceof LateBindDatatype )
			throw new Error();	// assertion failed. it must return a real type.
		if( body==null )
			throw new Error();	// renderer must render some datatype.
								// if there was an error, it must recover from it.
		return body;
	}
	
// no method works
	
	public Object createJavaObject( String value, ValidationContext context ) {
		throw new Error();
	}
	public String displayName() {
		// this method is called by ExpressionPool#createTypedString method,
		// so we cannot throw Error
		return "";
	}
	public String convertToLexicalValue( Object value, SerializationContext context ) {
		throw new Error();
	}
	public Class getJavaObjectType() {
		throw new Error();
	}
	public boolean isAtomType() {
		throw new Error();
	}
	public boolean isFinal( int derivationType ) {
		throw new Error();
	}
	public int isFacetApplicable( String facetName ) {
		throw new Error();
	}
	public DataTypeWithFacet getFacetObject( String facetName ) {
		throw new Error();
	}
	public ConcreteType getConcreteType() {
		throw new Error();
	}
	public boolean isValid( String s, ValidationContext context ) {
		throw new Error();
	}
	public void checkValid( String s, ValidationContext context ) {
		throw new Error();
	}
	public DatatypeStreamingValidator createStreamingValidator( ValidationContext context ) {
		throw new Error();
	}
	public boolean sameValue( Object o1, Object o2 ) {
		throw new Error();
	}
	public String getName() {
		throw new Error();
	}
	public Object createValue( String s, ValidationContext context ) {
		throw new Error();
	}
	public int valueHashCode( Object o ) {
		throw new Error();
	}
}
