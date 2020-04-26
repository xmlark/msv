package com.sun.tahiti.runtime.ll;

import java.util.Set;

/**
 * Input token filter for parsing interleave content models.
 * 
 * This class filters out tokens so that "interleave" can be treated like a sequence.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface Filter {
	boolean rejects( Object symbol );
	
	/**
	 * Filter class that rejects only one token.
	 */
	public static final class SimpleFilter implements Filter {
		public SimpleFilter( Object symbol ) { this.symbol=symbol; }
		
		private Object symbol;
		
		public boolean rejects( Object symbol ) {
			return symbol==this.symbol;
		}
	}
	
	/**
	 * Filter class that rejects tokens by using a set.
	 */
	public static final class SetFilter implements Filter {
		public SetFilter( Object[] _symbols ) {
			symbols = new java.util.HashSet();
			for( int i=0; i<_symbols.length; i++ )
				symbols.add(_symbols[i]);
		}
		
		private final Set symbols;
		
		public boolean rejects( Object symbol ) {
			return symbols.contains(symbol);
		}
	}
	
	/** a filter object that rejects nothing. */
	public static final Filter emptyFilter = new Filter() {
		public boolean rejects( Object symbol ) { return false; }
	};
}
