package com.sun.tahiti.runtime.ll;

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
	
	/** a filter object that rejects nothing. */
	public static final Filter emptyFilter = new Filter() {
		public boolean rejects( Object symbol ) { return false; }
	};
}
