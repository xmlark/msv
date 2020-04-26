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

//import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.ValidationContext;
import com.sun.msv.datatype.DatabindableDatatype;
import com.sun.msv.grammar.NameClassAndExpression;

/**
 * packet is a pair of symbol and payload.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class Packet {
	public final Object	symbol;
		
	Packet( Object symbol ) {
		this.symbol = symbol;
	}

	/** dispatches the content of this packet to the specified receiver. */
	public abstract void dispatch(  LLParser.Receiver rcv, ValidationContext context ) throws Exception;
	
	/**
	 * packet for datatype.
	 */
	static class DataPacket extends Packet {
			
		private final String contents;
			
		DataPacket( DatabindableDatatype symbol, String _contents ) {
			super(symbol);
			this.contents = _contents;
		}

		public void dispatch( LLParser.Receiver rcv, ValidationContext context ) throws Exception {
			((LLParser.CharacterReceiver)rcv).action( (DatabindableDatatype)super.symbol, contents, context );
		}
	}
		
	/**
	 * packet for element and attribute.
	 * 
	 * this type of packet have multiple items as the payload, and there are
	 * two types of the payload: the first is the "named" item, which should
	 * be received by a FieldReceiver; the second is the "unnamed" item, which
	 * should be received by a ObjectReceiver.
	 */
	static class ItemPacket extends Packet implements
			LLParser.FieldReceiver, LLParser.ObjectReceiver, LLParser.CharacterReceiver {
		
		private static abstract class Payload {
			abstract void dispatch( LLParser.Receiver rcv ) throws Exception;
		}
			
		private Payload[] payload = new Payload[4];
		private int payloadSize = 0;
		public int getPayloadSize() { return payloadSize; }
			
		/**
		 * symbol is usually either LLAttributeExp or LLElementExp.
		 */
		ItemPacket( Object symbol ) {
			super(symbol);
		}
		
		public void dispatch( LLParser.Receiver rcv, ValidationContext contest ) throws Exception {
			for( int i=0; i<payloadSize; i++ )
				payload[i].dispatch(rcv);
		}
		
		// ignore the empty action.
		// if this item receives truly nothing,
		// then the dispatch method will call the empty action method.
		public void start() throws Exception {}
		public void end() throws Exception {}
			
		public void action( final Object obj, final NamedSymbol fieldName ) {
			addPayload(
				new Payload(){
					void dispatch( LLParser.Receiver rcv ) throws Exception {
						((LLParser.FieldReceiver)rcv).action( obj, fieldName );
					}
				});
		}
		public void action( final Object obj ) {
			addPayload(
				new Payload(){
					void dispatch( LLParser.Receiver rcv ) throws Exception {
						((LLParser.ObjectReceiver)rcv).action( obj );
					}
				});
		}
		public void action( DatabindableDatatype dt, String literal, ValidationContext ctxt ) {
			;	// ignorable.
			/*
				ItemPacket will receive characters only when IgnoreItem is in its parent.
				So there is no need to store the value. Just ignore it.
			*/
		}
		
		private void addPayload( Payload p ) {
			if( payloadSize==payload.length ) {
				// expand a buffer
				Payload[] old = payload;
				payload = new Payload[old.length*2];
				System.arraycopy(old,0,payload,0,old.length);
			}
			payload[payloadSize++] = p;
		}
	}
}