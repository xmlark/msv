/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.compiler.java;

import com.sun.tahiti.grammar.*;
import java.io.PrintWriter;
import org.w3c.dom.Element;

/**
 * serializes a field of (1,1)/(0,1) multiplicity.
 * 
 * <p>
 * We use non-collection class (String,Float,MyXYZType) as
 * the field type.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
class AtmostOneFieldSerializer extends SingleFieldSerializer
{
	AtmostOneFieldSerializer( ClassSerializer parent, FieldUse fu ) {
		super(parent,fu);
	}
	
	
	String getTypeStr() {
		return parent.toPrintName(fu.type);
	}
	String setField( String objName ) {
		return format("this.{0}=({1}){2};",fu.name,getTypeStr(),objName);
	}
	String hasMoreToken() {
		return format("{0}!=null", fu.name );
	}
	String marshall( Element e ) {
		if( fu.type instanceof ClassItem || fu.type instanceof InterfaceItem )
			return format("{0}.marshall(out);", fu.name );
		else
			return format("out.data({0},{1}.{2});", fu.name,
				parent.grammarShortClassName, e.getAttribute("dataSymbol") );
	}
	String marshallerInitializer() {
		return null;
	}
}
