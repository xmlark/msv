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
import java.util.Stack;
import java.util.Vector;

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
		/**
		 * creates (or retrieves, whatever) the actual, concrete, real
		 * XSDatatype object.
		 * 
		 * <p>
		 * This method is typically called from the wrapUp method of the GrammarReader.
		 * 
		 * @return
		 *		the XSDatatype object which this LateBindDatatype object is representing.
		 *		It shall not return an instance of LateBindDatatype object.
		 * 
		 * @param context
		 *		If this renderer calls the getBody method of the other
		 *		LateBindDatatype objects, then this context should be passed
		 *		to the getBody method. This context object is responsible for
		 *		detecting recursive references.
		 * 
		 * @exception
		 *		If an error occurs during rendering, the renderer should throw
		 *		a DatatypeException instead of trying to report an error by itself.
		 *		The caller of this method will report an error message to the appropriate
		 *		handler.
		 */
		XSDatatype render( RenderingContext context ) throws DatatypeException;
	}
	
	/**
	 * this object is used to keep the information about
	 * the dependency between late-bind datatype objects.
	 * 
	 * <p>
	 * Consider the following schema:
	 * 
	 * <PRE><XMP>
	 * <xs:simpleType name="foo">
	 *   <xs:restriction base="bar">
	 *     <xs:minLength value="3"/>
	 *   </xs:restriction>
	 * </xs:simpleType>
	 * <xs:simpleType name="bar">
	 *   <xs:restriction base="foo">
	 *     <xs:minLength value="3"/>
	 *   </xs:restriction>
	 * </xs:simpleType>
	 * </XMP></PRE>
	 * 
	 * Since two types are depending on each other, if you call the
	 * getBody method of "foo" type, it will call the getBody method of "bar" type.
	 * Then in turn it will call "foo" again. So this will result in the
	 * infinite recursion.
	 * 
	 * <p>
	 * This context object is used to detect such condition and reports the
	 * dependency to the user.
	 * 
	 * <p>
	 * No method is publicly accessible.
	 */
	public static class RenderingContext {
		RenderingContext() {}
		
		private final Stack callStack = new Stack();
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
	public XSDatatype getBody( RenderingContext context ) {
		
		if(context==null)	// create a new context.
			context = new RenderingContext();
		
		if(body==null) {
			if( context.callStack.contains(this) ) {
				// a recursive definition is detected.
				Vector locs = new Vector();
				for( int i=0; i<context.callStack.size(); i++ )
					locs.add( ((LateBindDatatype)context.callStack.get(i)).ownerState.getLocation() );
				
				ownerState.reader.reportError(
					(Locator[])locs.toArray(new Locator[0]),
					ownerState.reader.ERR_RECURSIVE_DATATYPE, null );
				return StringType.theInstance;
			}
			context.callStack.push(this);
			
			try {
				body = renderer.render(context);
			} catch( DatatypeException e ) {
				ownerState.reader.reportError( ownerState.reader.ERR_BAD_TYPE,
					new Object[]{e}, e, new Locator[]{ownerState.getLocation()} );
				body = StringType.theInstance;	// recover by assuming a valid type.
			}
			
			context.callStack.pop();
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
