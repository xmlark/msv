package com.sun.tahiti.compiler.java;

import com.sun.tahiti.util.xml.DOMVisitor;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

class MarshallerSerializer extends DOMVisitor {
	
	/**
	 * produces the marshaller method.
	 */
	public static void write( Map fieldSerializers, PrintWriter out, Document dom ) {
		
		out.println("\tpublic void marshall( Marshaller out ) {");
		
		Iterator itr = fieldSerializers.values().iterator();
		while( itr.hasNext() ) {
			FieldSerializer fs = (FieldSerializer)itr.next();
			String s = fs.marshallerInitializer();
			if(s!=null)
				out.println("\t\t"+s);
		}
		new MarshallerSerializer(fieldSerializers,out).visit(dom);
		
		out.println("\t}");
		out.println("");
	}
	
	
	
	private MarshallerSerializer( Map fieldSerializers, PrintWriter out ) {
		this.fieldSerializers = fieldSerializers;
		this.out = out;
	}
	
	/** a map from field name to FieldSerializer. */
	private final Map fieldSerializers;
	
	/** output will be sent to this object. */
	private final PrintWriter out;
	
	
	
	public void visit( Element e ) {
		final String tagName = e.getTagName();
						
		if(tagName.equals("group")) {
			super.visit(e);
			return;
		}
		if(tagName.equals("oneOrMore")) {
			String exp = e.getAttribute("while");
			println("do {");
			indent++;
			super.visit(e);
			indent--;
			println("} while( "+
				getSerializer(exp).hasMoreToken()+" );");
			return;
		}
		if(tagName.equals("choice")) {
			super.visit(e);
			return;
		}
		if(tagName.equals("option")) {
			String exp = e.getAttribute("if");
			println("if( "+ getSerializer(exp).hasMoreToken() +" ) {");
			indent++;
			super.visit(e);
			indent--;
			println("} else");
			return;
		}
		if(tagName.equals("otherwise")) {
			println("{");
			indent++;
			super.visit(e);
			indent--;
			println("}");
			return;
		}
		if(tagName.equals("epsilon")) {
			println(";");
			return;
		}
		if(tagName.equals("notPossible")) {
			println("// assertion failed.");
			println("throw new Error();");
			return;
		}
		if(tagName.equals("marshall")) {
			String exp = e.getAttribute("fieldName");
			println( getSerializer(exp).marshall() );
			return;
		}
		if(tagName.equals("element") || tagName.equals("attribute")) {
			String uri = e.getAttribute("uri");
			String local = e.getAttribute("name");
			String methodName = tagName.equals("element")?"Element":"Attribute";
			println( "out.start"+methodName+"("+quote(uri)+","+quote(local)+");" );
			super.visit(e);
			println( "out.end"+methodName+"("+quote(uri)+","+quote(local)+");" );
			return;
		}
		throw new Error("unknown tag name:"+tagName);
	}
	
	private static String quote( String body ) {
		return "\""+body+"\"";
	}
					
	/** gets the FieldSerializer for the specified field. */
	private FieldSerializer getSerializer( String fieldName ) {
		return (FieldSerializer)fieldSerializers.get(fieldName);
	}
																					  
	private int indent = 2;
	/** print a string with indentation. */
	private void println( String s) {
		for( int i=0; i<indent; i++ )
			out.print('\t');
		out.println(s);
	}

}
