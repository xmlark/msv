package com.sun.tahiti.compiler.model;

import com.sun.msv.grammar.*;
import com.sun.tahiti.compiler.Symbolizer;
import com.sun.tahiti.grammar.*;
import java.util.Iterator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;

class JavaGenerator
{
	JavaGenerator( Expression topLevel, String grammarClassName, Symbolizer symbolizer, OutputResolver resolver ) {
		this.topLevel = topLevel;
		this.symbolizer = symbolizer;
		this.outResolver = resolver;
		this.grammarClassName = grammarClassName;
		
		int idx = grammarClassName.lastIndexOf('.');
		if(idx<0)	grammarShortClassName = grammarClassName;
		else		grammarShortClassName = grammarClassName.substring(idx+1);
	}
	
	private final Expression topLevel;
	private final Symbolizer symbolizer;
	private final OutputResolver outResolver;
	private final String grammarClassName;
	private final String grammarShortClassName;
		
	void generate() throws IOException {
		// collect all ClassItems.
		ClassCollector col = new ClassCollector();
		topLevel.visit(col);
		
		ClassItem[] types = (ClassItem[])col.classItems.toArray(new ClassItem[0]);
		
		for( int i=0; i<types.length; i++ ) {
			final ClassItem type = types[i];
				
			writeClass( type, new PrintWriter(outResolver.getOutput(type)) );
		}
	}
	
	
	private static String format( String fmt, Object[] args ) {
		return MessageFormat.format(fmt,args);
	}
	private static String format( String fmt, Object arg1 ) {
		return MessageFormat.format(fmt,new Object[]{arg1});
	}
	private static String format( String fmt, Object arg1, Object arg2 ) {
		return MessageFormat.format(fmt,new Object[]{arg1,arg2});
	}
	private static String format( String fmt, Object arg1, Object arg2, Object arg3 ) {
		return MessageFormat.format(fmt,new Object[]{arg1,arg2,arg3});
	}
	
	
	/**
	 * writes body of ClassItem.
	 */
	private void writeClass( ClassItem type, PrintWriter out ) {

		out.println("import com.sun.tahiti.runtime.ll.NamedSymbol;");
		out.println(format("import {0};",grammarClassName));
		out.println();
		
		out.print(format("public class {0}",type.name));
		
		if( type.getSuperType()!=null )
			out.print(format(" extends {0}",type.getSuperType().getTypeName()));
		
		Type[] itfs = type.getInterfaces();
		if(itfs.length!=0) {
			out.print(format(" implements {0}",itfs[0].getTypeName()));
			for( int i=1;i<itfs.length;i++ )
				out.print(format(", {0}",itfs[i].getTypeName()));
		}
		
		out.println(" {");
		out.println();
		
	// generate fields
	//----------------------------------------
		Iterator itr = type.fields.keySet().iterator();
		while( itr.hasNext() ) {
			String fieldName = (String)itr.next();
			FieldUse fu = (FieldUse)type.fields.get(fieldName);
			
			Container cont = getContainer(fu);
			out.println(format("\tpublic {0} {1} = {2};\n",
				new Object[]{
					cont.getTypeStr(),
					fu.name,
					cont.getInitializer() }));
		}
		
	// generate the setField method
	//------------------------------------------
		out.println("\n\n");
		out.println("\tpublic void setField( NamedSymbol name, Object item ) throws Exception {");
		itr = type.fields.keySet().iterator();
		while( itr.hasNext() ) {
			String fieldName = (String)itr.next();
			FieldUse fu = (FieldUse)type.fields.get(fieldName);

			Container cont = getContainer(fu);
			
			out.print("\t\tif( ");
			FieldItem[] fi = fu.getItems();
			for( int i=0; i<fi.length; i++ ) {
				if(i!=0)
					out.print(" || ");
				out.print(format("name=={0}.{1}",
					grammarShortClassName,
					symbolizer.getId(fi[i])));
			}
			out.println(" ) {");
			out.println("\t\t\t"+cont.setField(fu.name,"item"));
			out.println("\t\t\treturn;");
			out.println("\t\t}");
		}
		out.println("\t\tthrow new Error();//assertion failed.this is not possible");
		out.println("\t}");
		out.println("}");
		out.flush();
		out.close();
	}
		
	private interface Container {
		String getTypeStr();
		String getInitializer();
		String setField( String fieldName, String objName );
	}
	
	private Container getContainer( final FieldUse fu ) {
		if( fu.cardinality.max!=null ) {
			if( fu.cardinality.max.intValue()==1 )
				// use item type itself.
				return new Container(){
					public String getTypeStr() {
						return fu.type.getTypeName();
					}
					public String getInitializer() {
						return "null";
					}
					public String setField( String fieldName, String objName ) {
						return format("this.{0}=({1}){2};",fieldName,getTypeStr(),objName);
					}
				};
/*			if( fu.cardinality.max.intValue()==fu.cardinality.min )
				// use array.
				return new Container(){
					public String getTypeStr() {
						return fu.type.getTypeName()+"[]";
					}
					public String getInitializer() {
						return format("new {0}[{1}]",
							fu.type.getTypeName(),
							new Integer(fu.cardinality.min));
					}
				};
*/		}
		
		// otherwise use Vector
		return new Container(){
			public String getTypeStr() {
				return "java.util.Vector";
			}
			public String getInitializer() {
				return "new java.util.Vector()";
			}
			public String setField( String fieldName, String objName ) {
				return format("this.{0}.add({1});",fieldName,objName);
			}
		};
	}
}
