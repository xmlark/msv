package com.sun.tahiti.compiler.generator;

import com.sun.msv.grammar.*;
import com.sun.tahiti.compiler.Symbolizer;
import com.sun.tahiti.grammar.*;
import com.sun.tahiti.grammar.util.Multiplicity;
import com.sun.tahiti.grammar.util.ClassCollector;
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
		for( int i=0; i<types.length; i++ )
			writeClass( types[i], new PrintWriter(outResolver.getOutput(types[i])) );
		
		InterfaceItem[] itfs = (InterfaceItem[])col.interfaceItems.toArray(new InterfaceItem[0]);
		for( int i=0; i<itfs.length; i++ )
			writeClass( itfs[i], new PrintWriter(outResolver.getOutput(itfs[i])) );
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
	private void writeClass( TypeItem type, PrintWriter out ) {

		// one 
		ClassItem citm = null;
		InterfaceItem iitm = null;
		
		if( type instanceof ClassItem )	citm = (ClassItem)type;
		else							iitm = (InterfaceItem)type;
	
		
		String packageName = type.getPackageName();
		if( packageName!=null )
			out.println(format("package {0};\n",packageName));
		
		out.println("import com.sun.tahiti.runtime.ll.NamedSymbol;");
		out.println(format("import {0};",grammarClassName));
		out.println();
		
		out.print(format("public {0} {1}",
			citm!=null?"class":"interface",	type.getBareName() ));
		
		if( citm!=null && citm.getSuperType()!=null )
			out.print(format(" extends {0}",type.getSuperType().getTypeName()));
		
		Type[] itfs = type.getInterfaces();
		if(itfs.length!=0) {
			out.print(format(" {0} {1}",
				citm!=null?"implements":"extends", itfs[0].getTypeName()));
			
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
		if( citm!=null ) {
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
			if( type.getSuperType()!=null )
				out.println("\t\tsuper.setField(name,item);");
			else
				out.println("\t\tthrow new Error();//assertion failed.this is not possible");
			out.println("\t}");
		}
		
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
		if( fu.multiplicity.isAtMostOnce() )
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
/*
		if( fu.multiplicity.max!=null ) {
			if( fu.multiplicity.max.intValue()==fu.multiplicity.min )
				// use array.
				return new Container(){
					public String getTypeStr() {
						return fu.type.getTypeName()+"[]";
					}
					public String getInitializer() {
						return format("new {0}[{1}]",
							fu.type.getTypeName(),
							new Integer(fu.multiplicity.min));
					}
				};
		}
*/		
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
