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

import com.sun.msv.datatype.xsd.TypeIncubator;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.datatype.xsd.XSDatatypeImpl;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.DatatypeException;
import java.util.List;
import java.util.Iterator;

/**
 * Late-bind version of the TypeIncubator class.
 * 
 * <p>
 * This incubator is used to add facets to LateBindDatatype object.
 * Since the actual Datatype object is not available when facets are parsed,
 * this object merely stores all facets when the addFacet method is called.
 * 
 * <p>
 * Once the actual Datatype is provided, this class uses ordinary TypeIncubator
 * and build a real type object.
 * 
 * <p>
 * Note that many methods of TypeIncubator is not correctly implemented.
 * So it's much like a quick-hack.
 *
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class LateBindTypeIncubator extends TypeIncubator {
	
	public LateBindTypeIncubator( LateBindDatatype base ) {
		// set a dummy object. We won't use it anyway.
		super(StringType.theInstance);
		this.baseType = base;
	}
	
	/** base Datatype object. */
	private final LateBindDatatype baseType;
	
	/**
	 * applied facets.
	 * Order between facets are possibly significant.
	 */
	private final List facets = new java.util.LinkedList();
	
	public void addFacet( String name, String strValue, boolean fixed,
					 ValidationContext context ) {
		facets.add( new Facet(name,strValue,fixed,context) );
	}

	public XSDatatypeImpl derive( String name ) throws DatatypeException {
		TypeIncubator ti = new TypeIncubator( baseType.getBody() );
		Iterator itr = facets.iterator();
		while( itr.hasNext() ) {
			Facet f = (Facet)itr.next();
			ti.addFacet( f.name, f.value, f.fixed, f.context );
		}
		return ti.derive(name);
	}
	
	/** store the information about one added facet. */
	private class Facet {
		String name;
		String value;
		boolean fixed;
		ValidationContext context;
		public Facet( String name, String value, boolean fixed, ValidationContext context ) {
			this.name=name; this.value=value; this.fixed=fixed; this.context=context;
		}
	}
}
