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
import com.sun.tahiti.util.text.Formatter;
import java.io.PrintWriter;
import org.w3c.dom.Element;

/**
 * serializes a collection field by using a Set.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
class SetFieldSerializer extends FieldSerializer
{
	SetFieldSerializer( ClassSerializer parent, FieldUse fu ) {
		super(parent,fu);
	}

	
	void writeVariableDef( PrintWriter out, AccessModifier mod ) {
		out.println("\t"+mod+" "+getTypeStr()+" "+fu.name+
			" = new "+getTypeStr()+"();");
	}
	
	void writeGetterDef( PrintWriter out, AccessModifier mod ) {
		out.println(Formatter.format(
			"	<%0> int get<%1>Size() {\n"+
			"		return <%2>.size();\n"+
			"	}\n"+
			"	<%0> boolean contains<%1>( <%4> item ) {\n"+
			"		return <%2>.contains(item);\n"+
			"	}\n"+
			"	<%0> java.util.Iterator iterate<%1>() {\n"+
			"		return <%2>.iterator();\n"+
			"	}\n"+
			"	<%0> <%4>[] get<%1>s() {\n"+
			"		return (<%4>[])<%2>.toArray(new <%4>[<%2>.size()]);\n"+
			"	}\n",
			new Object[]{
				mod, capitalizedFieldName, fu.name, getTypeStr(),
				parent.toPrintName(fu.type)
			}));
	}
	
	void writeSetterDef( PrintWriter out, AccessModifier mod ) {
		out.println(Formatter.format(
			"	<%0> void clear<%1>() {\n"+
			"		<%2>.clear();\n"+
			"	}\n"+
			"	<%0> boolean remove( <%4> item ) {\n"+
			"		return <%2>.remove(item);\n"+
			"	}\n"+
			"	<%0> boolean add<%1>( <%4> item ) {\n"+
			"		return <%2>.add(item);\n"+
			"	}\n",
			new Object[]{
				mod, capitalizedFieldName, fu.name, getTypeStr(),
				parent.toPrintName(fu.type)
			}));
	}
	
	
	String getTypeStr() {
		return "java.util.Vector /* of " + parent.toPrintName(fu.type) +" */";
	}
	String setField( String objName ) {
		return format("this.{0}.add({1});",fu.name,objName);
	}
	String marshallerInitializer() {
		return format("int idx_{0}=0; int len_{0}={0}.size();", fu.name );
	}
	String hasMoreToken() {
		// TODO: this code becomes wrong if we allow primitive types
		// as the type.
		return format("idx_{0}!=len_{0}", fu.name );
	}
	String marshall( Element e ) {
		if( fu.type instanceof ClassItem || fu.type instanceof InterfaceItem )
			return format("(({0}){1}.get(idx_{1}++)).marshall(out);",
				parent.toPrintName(fu.type), fu.name );
		else
			return format("out.data(({0}){1}.get(idx_{1}++), {2}.{3});",
				parent.toPrintName(fu.type), fu.name,
				parent.grammarShortClassName, e.getAttribute("dataSymbol") );
	}
}
