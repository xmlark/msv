package com.sun.tahiti.runtime.ll;

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
