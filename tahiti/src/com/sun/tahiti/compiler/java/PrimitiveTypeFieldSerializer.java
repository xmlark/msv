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
 * serializes a field of Java built-in type with (1,1) multiplicity.
 * 
 * <p>
 * (1,1) multiplicity implies that the field is always present.
 * Therefore the field never need to be null (, which indicates
 * that the value is absent).
 * 
 * <p>
 * In this case and in this case only,
 * Java built-in types (int,float, etc.) can be used directly as the field type.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
class PrimitiveTypeFieldSerializer extends SingleFieldSerializer {
	
	public PrimitiveTypeFieldSerializer( ClassSerializer parent, FieldUse fu ) {
		super(parent,fu);
		
		boxType = parent.toPrintName(fu.type);
		primitiveType = (String)boxTypes.get(fu.type.getTypeName());
	}

	/**
	 * name of the "box" type. Things like "Integer","Float",...
	 */
	private final String boxType;
	
	/**
	 * name of the primitive type. Things like "int","float", ...
	 */
	private final String primitiveType;

	String getTypeStr() {
		return primitiveType;
	}
	String setField( String objName ) {
		return format("this.{0}=(({1}){2}).{3}Value();",
			fu.name, boxType, objName, primitiveType );
	}
	String marshallerInitializer() {
		return null;
	}
	String hasMoreToken() {
		return "true";
	}
	String marshall( Element e ) {
		return format("out.data(new {0}({1}),{2}.{3});",
			boxType, fu.name,
			parent.grammarShortClassName, e.getAttribute("dataSymbol") );
	}
}
