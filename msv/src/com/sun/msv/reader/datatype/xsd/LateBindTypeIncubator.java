package com.sun.msv.reader.datatype.xsd;

import com.sun.msv.datatype.xsd.TypeIncubator;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.datatype.xsd.XSDatatypeImpl;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.DatatypeException;
import java.util.List;
import java.util.Iterator;

public class LateBindTypeIncubator extends TypeIncubator {
	
	public LateBindTypeIncubator( LateBindDatatype base ) {
		// set a dummy object. We won't use it anyway.
		super(StringType.theInstance);
		this.baseType = base;
	}
	
	/** base Datatype object. */
	private final LateBindDatatype baseType;
	
	/** applied facets. */
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
