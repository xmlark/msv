package com.sun.tahiti.compiler.java;

import com.sun.tahiti.grammar.*;
import java.io.PrintWriter;

abstract class SingleFieldSerializer extends FieldSerializer
{
	public SingleFieldSerializer( ClassSerializer parent, FieldUse fu ) {
		super(parent,fu);
	}


	void writeVariableDef( PrintWriter out, AccessModifier mod ) {
		out.println("\t"+mod+" "+getTypeStr()+" "+fu.name+";");
	}
	
	void writeGetterDef( PrintWriter out, AccessModifier mod ) {
		out.println("\t"+mod+" "+getTypeStr()+" get"+capitalizedFieldName+"() {");
		out.println("\t\treturn this."+fu.name+";");
		out.println("\t}");
		out.println();
	}
	
	void writeSetterDef( PrintWriter out, AccessModifier mod ) {
		out.println("\t"+mod+" void set"+capitalizedFieldName+"( "+getTypeStr()+" newVal ) {");
		out.println("\t\tthis."+fu.name+" = newVal;");
		out.println("\t}");
		out.println();
	}
}
