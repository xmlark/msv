/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.runtime.ll;

import com.sun.msv.datatype.DatabindableDatatype;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.NameClassAndExpression;
import com.sun.msv.grammar.Expression;
import com.sun.msv.verifier.psvi.TypedContentHandler;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.ValidationContext;
import org.xml.sax.SAXException;

/**
 * perform data-binding.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class Binder implements TypedContentHandler {
	
	private static java.io.PrintStream debug = System.out;
	
	Binder() {
		this.rootSymbol = null;
		this.rootTable = null;
	}

	/**
	 * constructor with the root parser table.
	 */
	Binder( BindableGrammar grammar ) {
		this.rootSymbol = grammar.getRootSymbol();
		this.rootTable = grammar.getRootTable();
	}
	
	/**
	 * intermediate packet buffer.
	 * The buffer is expanded on demand.
	 */
	private Packet[]	buffer = new Packet[16];
	/** number of packets currently placed in the buffer. */
	private int			bufLen;
	/** index on the buffer that indicates where the current buffer starts. */
	private int			currentStartPos = 0;
	/** index on the buffer that indicates where the content part starts. */
	private int			currentBodyPos;
	
	// currentStartPos/currentBodyPos stack.
	private int[]		cpStack = new int[8];
	private int			depth;
	
	private LLParser	parser = new LLParser();
	
	/** validation context object. */
	private ValidationContext context;
	
	/**
	 * the root parser table.
	 * 
	 * If this field is present, then the LLParser has to be invoked with
	 * this table and the rootSymbol int the endDocument method.
	 */
	private final LLParserTable rootTable;
	private final Object rootSymbol;

	private void initialize() {
		depth = 0;
		bufLen = 0;
		currentStartPos = 0;
		currentBodyPos = 0;
	}
	
	
	/** place a packet in the buffer. */
	private void pushPacket( Packet p ) {
		if( buffer.length==bufLen ) {
			// expand buffer
			Packet[] newBuf = new Packet[buffer.length*2];
			System.arraycopy(buffer,0,newBuf,0,buffer.length);
			buffer = newBuf;
		}
		buffer[bufLen++] = p;
		
		if(debug!=null)	debug.println("pushPacket()");
	}
	
	public void characterChunk( String literal, Datatype type ) throws SAXException {
		System.out.println("characterChunk()");
		assert( type instanceof DatabindableDatatype );
		pushPacket( new Packet.DataPacket((DatabindableDatatype)type,literal) );
	}

	private void processItem( Object targetSymbol, LLParserTable table ) throws SAXException {
		
		// TODO: avoid unnecessary copy.
		Packet[] tokens = new Packet[bufLen-currentBodyPos];
		System.arraycopy(buffer,currentBodyPos,tokens,0,tokens.length);
		Packet[] attributes = new Packet[currentBodyPos-currentStartPos];
		System.arraycopy(buffer,currentStartPos,attributes,0,attributes.length);
		
		if( debug!=null ) {
			debug.println("target symbol - " + LLParser.symbolToStr(targetSymbol) );
			debug.println("input packets (body:"+(bufLen-currentBodyPos)+") (atts:"+(currentBodyPos-currentStartPos)+")");
		}
		
		popContext();

		try {
			// unmarshall this content model and put the result to the buffer.
			pushPacket( parser.unmarshall(
				targetSymbol, tokens, attributes, context, table ) );
		} catch( Exception e ) {
			e.printStackTrace();
			throw new UnmarshallingException(e);
		}
		
		if( debug!=null )
			debug.println("item processed ("+
				((Packet.ItemPacket)buffer[bufLen-1]).getPayloadSize()
				+")\n");
		
	}


	/** push the current pointers. */
	private void pushContext() {
		if( depth==cpStack.length ) {
			// expand buffer
			int[] newcp = new int[cpStack.length*2];
			System.arraycopy(cpStack,0,newcp,0,cpStack.length);
			cpStack = newcp;
		}
		cpStack[depth++] = currentStartPos;
		cpStack[depth++] = currentBodyPos;
		currentStartPos = bufLen;
		currentBodyPos = 0;
	}
	/** pop the current pointers. */
	private void popContext() {
		bufLen = currentStartPos;
		currentBodyPos = cpStack[--depth];
		currentStartPos = cpStack[--depth];
	}
	
	public void startElement( String namespaceURI, String localName, String qName ) throws SAXException {
		if(debug!=null)
			debug.println("startElement("+namespaceURI+","+localName+")");
		pushContext();
	}
	
	public void startAttribute( String namespaceURI, String localName, String qName ) throws SAXException {
		if(debug!=null)
			debug.println("startAttribute("+namespaceURI+","+localName+")");
		pushContext();
		currentBodyPos = bufLen;
		if(debug!=null)
			debug.println("startAttribute(): currentBodyPos="+currentBodyPos);
	}
	
	public void endAttribute( String namespaceURI, String localName, String qName, AttributeExp type ) throws SAXException {
		assert( type instanceof LLAttributeExp );
		processItem( type, ((LLAttributeExp)type).parserTable );
	}

	public void endAttributePart() throws SAXException {
		currentBodyPos = bufLen;
		if(debug!=null) {
			debug.println("endAttributePart()   (currentBodyPos="+currentBodyPos+")");
		}
	}
	
	public void endElement( String namespaceURI, String localName, String qName, ElementExp type ) throws SAXException {
		assert( type instanceof LLElementExp );
		processItem( type, ((LLElementExp)type).parserTable );
	}
	

	public void startDocument( ValidationContext _context ) throws SAXException {
		initialize();
		if( rootSymbol!=null )
			pushContext();
		this.context = _context;
	}
	
	public void endDocument() throws SAXException {
		
		if( rootSymbol!=null )
			// we have a root parser table.
			// the last invokation.
			processItem( rootSymbol, rootTable );
		
		assert( bufLen==1 );
	}
	
	public Object getResult() {
		
		// after the completion of a parsing,
		// buffer must have one and only one packet, and it must be an ItemPacket.
		Packet.ItemPacket top = (Packet.ItemPacket)buffer[0];
		
		final Object[] theResult = new Object[1];
		
		try {
			top.dispatch(
				new LLParser.ObjectReceiver(){
					public void start() {}
					public void end() {}
					public void action(Object item) {
						if(theResult[0]!=null)	// the top-level class must be unique.
							throw new Error();
						theResult[0] = item;
					}
			}, null );
		}catch(Exception e) {
			e.printStackTrace();
			assert(false);	// impossible. because we don't throw any exception from the action.
		}
		
		return theResult[0];
	}
	
	private static void assert( boolean b ) {
		if(!b)	throw new Error();
	}
}
