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
import com.sun.msv.grammar.NameClassAndExpression;
import org.relaxng.datatype.ValidationContext;
import java.util.Vector;
import java.util.Set;
import java.util.Map;

/**
 * Generic LL parser.
 * 
 * parses tokens according to the specified LL grammar.
 * 
 * <h2>Parser symbols</h2>
 * 
 * Terminals
 * ---------
 * ElementSymbol		--	ElementExp
 * AttributeSymbol		--	AttributeExp
 * DataSymbol			--	org.relaxng.datatype.DataType
 * 
 * Non-terminals (extends NonTerminalSymbol)
 * -----------------------------------------
 * ClassSymbol			--	ClassSymbol
 * NamedSymbol			--	NamedSymbol
 * IntermediateSymbol	--	IntermediateSymbol
 * 
 * ???: It may be possible that we want to have two distinct ClassSymbols whose
 * Java type is equal.
 * 
 * <PRE><XMP>
 * C<Person>	= N<firstName> N<lastName>
 * N<firstName>	= C<String>1
 * N<lastName>	= C<String>2
 * C<String>1	= $string(maxLength=10)
 * C<String>2	= $string(maxLength=12)
 * </XMP></PRE>
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class LLParser {

	/** logger. */
	private static java.io.PrintStream debug = System.out;
	
	/**
	 * a special symbol that instructs InputReader to remove the last-added filter.
	 */
	private static final Object removeInterleaveSymbol = new Object();
	
	public interface Receiver {
//		// used to kick the production rule which is expanded to an empty sequence.
//		// (e.g., A->$epsilon)
//		void action() throws Exception;
		void start() throws Exception;
		void end() throws Exception;
	};
	
	public interface FieldReceiver extends Receiver {
		void action( Object object, NamedSymbol fieldName ) throws Exception;
	}
	public interface ObjectReceiver extends Receiver {
		void action( Object item ) throws Exception;
	}
	public interface CharacterReceiver extends Receiver {
		void action( DatabindableDatatype datatype, String characterContents, ValidationContext context ) throws Exception;
	}
	
	/**
	 * do-nothing receiver.
	 * 
	 * This object is used as an action of IgnoreSymbol.
	 */
	public static final Receiver ignoreReceiver = new CharacterReceiver() {
		public void start() {}
		public void end() {}
		// IgnoreSymbol can be expanded to DataPacket. That is, there are rules
		// like "Ignore1->Dstring". So ignoreReceiver has to implement CharacterReceiver.
		public void action( DatabindableDatatype datatype, String characterContents, ValidationContext context ) {}
	};
	
	
	class StackItem {// must be immutable
		/** previous StackItem. StackItems form a stack by using this field. */
		public final StackItem previous;
		
		/**
		 * actual payload.
		 * Usually, this is either a terminal symbol or a non-terminal symbol.
		 * 
		 * Sometimes, this payload is used for out-of-band transmission
		 * by storing a special token.
		 */
		public final Object		symbol;
		
		/** This object receives the payload of the matched input token. */
		public final Receiver	receiver;
		
		public StackItem( Object symbol, Receiver receiver ) {
			this.symbol=symbol; this.receiver=receiver;
			this.previous=stackTop;
		}
	}
	
	/** ���̓g�[�N���� */
	Packet[] inputs;
	
	/** �g�[�N���ǂݎ��� */
	class InputReader {
		
		InputReader(Packet[] attributes,int len) {
			this.used = new boolean[inputs.length];
			this.attributes = new Packet[len];
			System.arraycopy(attributes,0,this.attributes,0,len);
			this.attLen = len;
			this.base = this.idx = 0;
		}
		
		private boolean[]	used;	// ������g�[�N����true�ɂȂ��Ă���
		private int			base;		// �܂��������g�[�N���̐擪�ʒu
		private int			idx;		// ���݂̃g�[�N���ʒu
		public int getCurrentIndex() { return idx; }
		// �t�B���^���������Ă���ƁA���҂��H���Ⴄ���Ƃ�����
		
		public Packet[]		attributes;	// unconsumed attributes
		public int			attLen;		// length of attributes.
		
		private final class FilterChain {
			// must be immutable because of the backtracking 
			final Filter filter;
			final FilterChain previous;
			FilterChain( Filter filter, FilterChain previous ) {
				this.previous = previous;
				this.filter = filter;
			}
			boolean rejects( Object sym ) {
				for( FilterChain f = this; f!=null; f=f.previous )
					if(f.filter.rejects(sym))	return true;
				return false;
			}
		}
		/** filters that are currently active. */
		private FilterChain	filter = null;
		
		/** gets the current token. */
		public Packet current() {
			if(idx==inputs.length)	return null;	// no more token
			return inputs[idx];
		}
		/** consumes a token and returns a new token. */
		public Packet consume() {
			used[idx++] = true;
			skipFilteredToken();
			return current();
		}
		
		/** skips currently filtered tokens and find an available one. */
		private void skipFilteredToken() {
			// look for an unused token.
			while( idx<inputs.length && (used[idx] || (filter!=null && filter.rejects(inputs[idx].symbol))) )
				idx++;
			
			while( base<inputs.length && used[base] )	base++;
		}
		/** consumes an attribute token. */
		public void consumeAttribute( int idx ) {
			assert( idx<attLen );
			attributes[idx] = attributes[--attLen];
		}
		/** adds a new filter. */
		public void applyFilter( Filter f ) {
			filter = new FilterChain(f,filter);
		}
		/** removes the last-added filter. */
		public void removeFilter() {
			idx=base;
			filter = filter.previous;
			skipFilteredToken();
		}
		
		/** creates a clone. */
		public InputReader copy() {
			InputReader n = new InputReader(this.attributes,this.attLen);
			n.base = this.base;
			n.idx = this.idx;
			n.filter = this.filter;
			n.attLen = this.attLen;
			
			// deep copy the array.
			System.arraycopy( this.used, 0, n.used, 0, this.used.length );
			
			return n;
		}
	}
	
	class BackTrackRecord {
		private final BackTrackRecord	previous;
		
		// back-up-ed states.
		public final StackItem		_stackTop;
		public InputReader			_reader;
		public final int			_actionPos;
		/** which rule shall we try next? */
		public int					nextRuleIdx;
		/** possible rules. */
		public final Rule[]			rules;
		
		public BackTrackRecord( Rule[] rules, int index ) {
			this._stackTop = stackTop;
			this._reader = reader.copy();
			this._actionPos = actionPos;
			this.nextRuleIdx = index;
			this.rules = rules;
			this.previous = backTrackLog;
		}
		/**
		 * performs the back track and gets back to the point of choice.
		 */
		public void doBackTrack() {
			
			Rule r = rules[nextRuleIdx++];
			stackTop = _stackTop;
			reader = _reader;
			actionPos = _actionPos;
			
			if(nextRuleIdx==rules.length)
				// no more is left. discard this BackTrackRecord.
				backTrackLog = previous;
			else
				// we have more options.
				// so leave this BackTrackRecord
				_reader = reader.copy();

			if(debug!=null) {
				debug.println("-- backtrack -------");
				StackItem s = stackTop;
				String str="";
				while( s!=null ) {
					str = " "+symbolToStr(s.symbol)+str;
					s = s.previous;
				}
				debug.println("token stack:"+str);
				debug.println("next token : ("+ reader.getCurrentIndex()
					+") " + symbolToStr(reader.current().symbol) );
				debug.println("--------------------");
			}
			
			applyRule(r);
		}
	}
	
// State	
//===============================================
	/** top of the stack. */
	protected StackItem stackTop;
	
	/** input token reader. */
	protected InputReader reader;
//===============================================
	
	
	/** back track history. */
	protected BackTrackRecord backTrackLog;
	
//	/** action table for each input packet */
//	protected Receiver[] inputTokenReceiver;
	
	
	// typical actions
	private interface Action {
		public void run() throws Exception;
	}
	private class InvokeReceiverAction implements Action {
		InvokeReceiverAction( Receiver rcv, Packet p ) {
			this.rcv=rcv; this.p=p;
		}
		private final Receiver rcv;
		private final Packet p;
		public void run() throws Exception {
			p.dispatch(rcv,context);
		}
	}
	private class StartNonTerminalAction implements Action {
		StartNonTerminalAction( Receiver rcv ) {
			this.rcv=rcv;
		}
		private final Receiver rcv;
		public void run() throws Exception {
			rcv.start();
		}
	}
	private class EndNonTerminalAction implements Action {
		EndNonTerminalAction( Receiver rcv ) {
			this.rcv=rcv;
		}
		private final Receiver rcv;
		public void run() throws Exception {
			rcv.end();
		}
	}
	
	/** actions in order of the execution. */
	protected Action[]	actions = new Action[16];
	/** number of actions in the queue. */
	protected int			actionPos=0;
	
	private void addAction( Action act ) {
		if(actions.length==actionPos) {
			// expand buffer
			Action[] n = new Action[actionPos*2];
			System.arraycopy(actions,0,n,0,actionPos);
			actions = n;
		}
		actions[actionPos++] = act;
	}
	
	/**
	 *	the datatype validation context object that could possibly be used
	 *	to instanciate objects from character contents.
	 */
	protected ValidationContext context;
	
	
	// local variable for the unmarshall method.
	private final Set applicableRules = new java.util.HashSet();
	
	/**
	 * unmarshal object(s).
	 * 
	 * @param startSymbol
	 *		the goal symbol to which input tokens are reduced. In CS terminology,
	 *		it is called "the start symbol", usually denoted by S.
	 * @param inputs
	 *		input packet sequences. Each packet must be either an element packet or
	 *		data packet. The order of items is siginificant.
	 * @param attributes
	 *		input attribute packets. All packets must be attribute packets.
	 *		The order of items is not significant.
	 * @param context
	 *		the datatype validation context object that could possibly be used
	 *		to instanciate objects from character contents.
	 * @param table
	 *		parsing table object that determines the behavior of this engine.
	 * 
	 * @return
	 *		a packet whose symbol is the start symbol.
	 */
	public Packet.ItemPacket unmarshall(
			Object startSymbol, Packet[] _inputs, Packet[] _attributes,
			ValidationContext context, LLParserTable table ) throws Exception {
		
		assert( table!=null );
		
		// initialize variables
		this.context = context;
		this.inputs = _inputs;
		reader = new InputReader(_attributes,_attributes.length);
		stackTop = null;
		actionPos = 0;
//		inputTokenReceiver = new Receiver[inputs.length];	// TODO:�ė��p
//		Map attributeListener = new java.util.HashMap();	// AttributePacket -> Action map
			
		// the result of unmarshalling is stored to this root object.
		final Packet.ItemPacket root = new Packet.ItemPacket(startSymbol);
		final StackItem startRule = new StackItem(startSymbol,root);
		stackTop = startRule;
		
		
LLparser:
		while(true) {
			Packet current = reader.current();
			
			if( stackTop==null ) {
				if( reader.attLen!=0 || current!=null ) {
					if(debug!=null) {
						debug.println("stack is empty but there are unconsumed tokens");
						if(reader.attLen!=0)
							debug.println("attribute is left");
						if(current!=null)
							debug.println("input token "+symbolToStr(current)+" is left");
					}
					// unconsumed attributes are left, or
					// unconsumed tokens are left.
					// That means we have to back track.
					doBackTrack();
					continue;
				}
				break;	// LR parser accepts the input
			}
			
			if( stackTop.symbol==removeInterleaveSymbol ) {
				if(debug!=null)
					debug.println("removing an interleave filter");
				reader.removeFilter();
				popStack();
				continue;
			}
			if( stackTop.symbol instanceof EndNonTerminalAction ) {
				addAction( (Action)stackTop.symbol );
				popStack();
				continue;
			}
			
			if( isNonTerminal(stackTop.symbol) || stackTop==startRule) {
				// the start rule is either ElementSymbol or AttributeSymbol,
				// but it must be considered non-terminals.

				// obtain the production rules that are applicable now.
				
				Rule[] rules=null;
				Object currentSymbol = (current!=null)?current.symbol:null;
				applicableRules.clear();
					
				// usually, it is better to follow the lead of attributes.
				for(int i=0; i<reader.attLen; i++) {
					Rule[] more = table.get( stackTop.symbol, reader.attributes[i].symbol );
					if(more==null)	continue;
					if(rules==null)	rules=more;
					else {
						for( int j=0; j<more.length; j++ )
							applicableRules.add(more[j]);
					}
				}
				
				{
					Rule[] more = table.get( stackTop.symbol, currentSymbol );
					if(more!=null) {
						if(rules==null)	rules=more;
						else {
							for( int j=0; j<more.length; j++ )
								applicableRules.add(more[j]);
						}
					}
				}
				
				if(rules==null) {
					if(debug!=null) {
						debug.println("error: top("+
							symbolToStr(stackTop.symbol)+") input("+
							symbolToStr(currentSymbol)+")");
						String r = "";
						for( int i=0; i<reader.attLen; i++ )
							r += " "+symbolToStr(reader.attributes[i].symbol);
						debug.println("  available attributes:"+r );
					}
					// error; no rule is available. try back track.
					doBackTrack();
				} else {
					if( rules.length>1 ) {
						// we can backtrack to this point later.
						// so store necessary information.
						for( int j=1/*0 is the applied rule*/; j<rules.length; j++ )
							applicableRules.add( rules[j] );
					}
					// then try the first rule.
					applyRule( rules[0] );
				}

				// if there are other applicable rules,
				// memorize them so that we can backtrack later if necessary.
				if( applicableRules.size()!=0 )
					backTrackLog = new BackTrackRecord(
						(Rule[])applicableRules.toArray(new Rule[0]),0);
				
				continue;
			}
			
			if( stackTop.symbol instanceof LLAttributeExp ) {
				for( int i=0; i<reader.attLen; i++ )
					if( reader.attributes[i].symbol==stackTop.symbol ) {
//						attributeListener.put( reader.attributes[i], stackTop.receiver );
						addAction( new InvokeReceiverAction( stackTop.receiver, reader.attributes[i] ) );
						
						// found a match.
						if(debug!=null) {
							debug.println("consuming an att token: "
								+symbolToStr(reader.attributes[i].symbol));
						}
						reader.consumeAttribute(i);
						popStack();
						continue LLparser;
					}
			}
			
			// now the stack top must be a terminal symbol.
			// so it must match the input symbol.
			if( current.symbol!=stackTop.symbol ) {
				// if it's not, it's an error. so perform back track.
				doBackTrack();
				continue;
			}
			
			if(debug!=null) {
				debug.println("consuming a token: "+symbolToStr(stackTop.symbol));
			}
			// stackTop.symbol is ElementSymbol or DataSymbol
//			inputTokenReceiver[reader.getCurrentIndex()] = stackTop.receiver;
			addAction( new InvokeReceiverAction( stackTop.receiver, current ) );
			current = reader.consume();
			popStack();
		}
		
		
		// now LL parser is matched its content.

		// execute the associated action.
		for( int i=0; i<actionPos; i++ )
			actions[i].run();
/*
		for( int i=0; i<inputTokenReceiver.length; i++ ) {
			// input token must be either Element/Attribute/DataSymbol.
			assert( !isNonTerminal(inputs[i].symbol) );
			inputs[i].dispatch( inputTokenReceiver[i], context );
		}
		for( int i=0; i<_attributes.length; i++ ) {
			Receiver a = (Receiver)attributeListener.get(_attributes[i]);
			assert(a!=null);		// entry must exist.
			_attributes[i].dispatch( a, context );
		}
		for( int i=0; i<epsilonActionPos; i++ ) {
			epsilonActions[i].action();
		}
*/
		
		return root;
	}
	
	private void doBackTrack() {
		/*
			It is guaranteed by the type detecter that the token sequence
			passed to the marshaller always has the parsing tree.
			So it is never possible for the marshaller to fail in constructing
			parsing tree.
		
			That means when we need a backtrack, there must be a log.
		*/
		assert( backTrackLog!=null );
		backTrackLog.doBackTrack();
	}

	/** apply a rule: A->abc, and manipulate the stack and the tokens accordingly. */
	private void applyRule( Rule rule ) {
		
		// since we are trying to expand the non-terminal which is
		// currently at the top of the stack,
		// the non-terminal of the rule must be equal to the stack top.
//		assert( rule.left==stackTop.symbol );
		Object left = stackTop.symbol;
		
		// compute the action when this rule is "reduced".
		// (when information are propagated from right to left).
		// this object will receive the value of the right hand side
		Receiver receiver;
		
		if( left instanceof NameClassAndExpression ) {
			// LLAttributeExp and LLElementExp are pseudo non-terminals
			// and therefore don't have an action.
			receiver = stackTop.receiver;
		} else {
			// other true non-terminals may have their own actions.
			assert( isNonTerminal(left) );
			receiver = ((NonTerminalSymbol)left).createReceiver(stackTop.receiver);
		}
		
		if(debug!=null) {
			StringBuffer buf = new StringBuffer();
			buf.append("expanding a rule : ");
			buf.append( symbolToStr(stackTop.symbol) );
			buf.append(" --> ");
			for( int i=0; i<rule.right.length; i++ ) {
				if(i!=0) {
					if( rule.isInterleave )	buf.append(" & ");
					else	buf.append(' ');
				}
				buf.append(symbolToStr(rule.right[i]) );
			}
			debug.println(buf);
		}
				
		popStack();
		
		if(!(left instanceof IntermediateSymbol)) {
			// We shouldn't insert Start/EndNonTerminalAction
			// when we are expanding intermediate rules.
			addAction( new StartNonTerminalAction(receiver) );
		
			// this special token will be processed to insert EndNonTerminalAction
			stackTop = new StackItem( new EndNonTerminalAction(receiver), null );
		}
		
		if( !rule.isInterleave ) {
			for( int j=rule.right.length-1; j>=0; j-- )	// in the reverse order
				stackTop = new StackItem(rule.right[j],receiver);
		} else {
			// if it's an interleave, push the  special token, too.
			for( int j=rule.right.length-1; j>=0; j-- )	{
				if(j!=rule.right.length-1)
					stackTop = new StackItem(removeInterleaveSymbol,null);
				stackTop = new StackItem(rule.right[j],receiver);
			}
			
			// apply filters. Note that the number of filters are one less than
			// the number of right hand side branches because we don't need a filter
			// for the first one branch.
			for( int j=rule.right.length-2; j>=0; j-- )
				reader.applyFilter(rule.filters[j]);
		}
	}
	
	/**
	 * checks if a symbol is a non-terminal symbol or not.
	 */
	private static boolean isNonTerminal( Object symbol ) {
		return	symbol instanceof NonTerminalSymbol;
	}
	
	public static String symbolToStr( Object symbol ) {
		if( symbol instanceof NonTerminalSymbol )
			return symbol.toString();
		
		if( symbol instanceof com.sun.msv.datatype.xsd.XSDatatype )
			return "D<"+((com.sun.msv.datatype.xsd.XSDatatype)symbol).displayName()+">";
		if( symbol instanceof DatabindableDatatype )
			return "D<"+symbol.getClass().getName()+">";
		if( symbol instanceof LLElementExp )
			return "E<"+((LLElementExp)symbol).getNameClass()+">";
		if( symbol instanceof LLAttributeExp )
			return "A<"+((LLAttributeExp)symbol).getNameClass()+">";
		
		if( symbol==null )
			return "$$$";	// terminal symbol
		
		throw new Error(symbol.toString());
	}
	
	private void popStack() {
		stackTop = stackTop.previous;
	}
}
