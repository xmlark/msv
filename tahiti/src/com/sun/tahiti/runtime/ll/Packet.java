package com.sun.tahiti.runtime.ll;

//import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.ValidationContext;
import com.sun.msv.datatype.DatabindableDatatype;
import com.sun.msv.grammar.NameClassAndExpression;

/**
 * packet is a pair of symbol and payload.
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
			LLParser.FieldReceiver, LLParser.ObjectReceiver {
		
		private static class Named {
			public final NamedSymbol name;
			public final Object obj;
			Named( NamedSymbol name, Object obj ) {
				this.name=name; this.obj=obj;
			}
		}
			
		public Named[] payload = new Named[4];
		public int payloadSize = 0;
			
		/**
		 * symbol is usually either LLAttributeExp or LLElementExp.
		 */
		ItemPacket( Object symbol ) {
			super(symbol);
		}
		
		public void dispatch( LLParser.Receiver rcv, ValidationContext contest ) throws Exception {
			for( int i=0; i<payloadSize; i++ ) {
				Named p = (Named)payload[i];
				if( p.name==null )
					((LLParser.ObjectReceiver)rcv).action( p.obj );
				else
					((LLParser.FieldReceiver)rcv).action( p.obj, p.name );
			}
		}
		
		// ignore the empty action.
		// if this item receives truly nothing,
		// then the dispatch method will call the empty action method.
		public void start() throws Exception {}
		public void end() throws Exception {}
			
		public void action( Object obj, NamedSymbol fieldName ) {
			if( payloadSize==payload.length ) {
				// expand a buffer
				Named[] old = payload;
				payload = new Named[old.length*2];
				System.arraycopy(old,0,payload,0,old.length);
			}
			payload[payloadSize++] = new Named(fieldName,obj);
		}
		public void action( Object obj ) {
			action(obj,null);
		}
	}
}