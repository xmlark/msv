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

import org.w3c.dom.Element;
import java.io.PrintWriter;
import java.util.Map;
import java.text.MessageFormat;
import com.sun.tahiti.grammar.*;
import com.sun.tahiti.reader.NameUtil;

/**
 * produces field-access related java soure code.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class FieldSerializer {
	
	/**
	 * gets the appropriate field serializer for the specified FieldUse.
	 */
	public static FieldSerializer get( ClassSerializer parent, FieldUse fu ) {
		if( fu.multiplicity.isUnique() && boxTypes.containsKey(fu.type.getTypeName()) )
			// use the unboxed type.
			return new PrimitiveTypeFieldSerializer(parent,fu);
		
		if( fu.multiplicity.isAtMostOnce() )
			// use item type itself.
			return new AtmostOneFieldSerializer(parent,fu);
		
		// otherwise use Vector
		return new VectorFieldSerializer(parent,fu);
	}
	

	public FieldSerializer( ClassSerializer parent, FieldUse fu ) {
		this.fu = fu;
		this.parent = parent;
		capitalizedFieldName = NameUtil.xmlNameToJavaName( "class", fu.name );
	}
	
	/** this serializer works on this object. */
	protected final FieldUse fu;
	
	/** the parent ClassSerializer object which owns this object. */
	protected final ClassSerializer parent;
	
	protected final String capitalizedFieldName;
	
	
	
	/** gets the string that represents the type of the field. */
	abstract String getTypeStr();
	
//	/** gets the initializer of the field. */
//	String getInitializer();
	
	/**
	 * gets the string that will be used to add (or store) an object
	 * to the field.
	 */
	abstract String setField( String objName );
	
	/**
	 * writes the field definition and accessors to the specified stream.
	 */
	final void writeFieldDef( PrintWriter out ) {
		
		Accessor acc = fu.getAccessor();
		AccessModifier mod = fu.getAccessModifier();
		
		// if the accessor methods are going to be produced,
		// produce a private field.
		if(acc==acc.field)		writeVariableDef(out,mod);
		else					writeVariableDef(out,mod._private);
		
		if(acc!=acc.field) {
			writeGetterDef(out,mod);
			if(acc==acc.readWrite)
				writeSetterDef(out,mod);
		}
	}
	
	/**
	 * writes the bare field definition to the specified output object
	 * by using the specified access modifier.
	 */
	abstract void writeVariableDef( PrintWriter out, AccessModifier mod );
	
	/**
	 * writes the getter method definition by using the specified access modifier.
	 */
	abstract void writeGetterDef( PrintWriter out, AccessModifier mod );
	
	/**
	 * writes the setter method definition by using the specified access modifier.
	 */
	abstract void writeSetterDef( PrintWriter out, AccessModifier mod );
	
	
//	
// marshaller related methods
//
	/**
	 * produces a code fragment that creates an iterator, if necessary.
	 * @return null
	 *		if this field doesn't need an initializer.
	 */
	abstract String marshallerInitializer();
	
	/**
	 * produces a code fragment that checks the availability of the next token
	 * of this field.
	 */
	abstract String hasMoreToken();
	
	/**
	 * produces a code to marshall the next token of this field, and consumes
	 * that token.
	 * 
	 * @param marshallElement
	 *		
	 */
	abstract String marshall( Element marshallElement );



	/**
	 * Java objects which has the corresponding built-in type.
	 */
	protected static final Map boxTypes;
	static {
		boxTypes = new java.util.HashMap();
		boxTypes.put( "java.lang.Integer",	"int" );
		boxTypes.put( "java.lang.Boolean",	"boolean" );
		boxTypes.put( "java.lang.Double",	"double" );
		boxTypes.put( "java.lang.Float",	"float" );
		boxTypes.put( "java.lang.Char",		"char" );
		boxTypes.put( "java.lang.Long",		"long" );
		boxTypes.put( "java.lang.Byte",		"byte" );
		boxTypes.put( "java.lang.Short",	"short" );
	}
	
	


	protected static String format( String fmt, Object[] args ) {
		return MessageFormat.format(fmt,args);
	}
	protected static String format( String fmt, Object arg1 ) {
		return MessageFormat.format(fmt,new Object[]{arg1});
	}
	protected static String format( String fmt, Object arg1, Object arg2 ) {
		return MessageFormat.format(fmt,new Object[]{arg1,arg2});
	}
	protected static String format( String fmt, Object arg1, Object arg2, Object arg3 ) {
		return MessageFormat.format(fmt,new Object[]{arg1,arg2,arg3});
	}
	protected static String format( String fmt, Object arg1, Object arg2, Object arg3, Object arg4 ) {
		return MessageFormat.format(fmt,new Object[]{arg1,arg2,arg3,arg4});
	}
}
