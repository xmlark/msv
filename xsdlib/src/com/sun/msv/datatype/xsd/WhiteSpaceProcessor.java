package com.sun.tranquilo.datatype;

public abstract class WhiteSpaceProcessor
{
	abstract String process( String text );
	
	/**
	 * returns a WhiteSpaceProcessor object if "whiteSpace" facet is specified.
	 * Otherwise returns null.
	 */
	protected static WhiteSpaceProcessor get( String name )
		throws BadTypeException
	{
		if( name.equals("preserve") )		return theReplace;
		if( name.equals("collapse") )		return theCollapse;
		if( name.equals("replace") )		return theReplace;
		
		throw new BadTypeException( BadTypeException.ERR_INVALID_WHITESPACE_VALUE, name );
	}
	
	private static boolean isWhiteSpace( char ch )
	{
		return ch==0x9 || ch==0xA || ch==0xD || ch==0x20;
	}
	
	protected static WhiteSpaceProcessor thePreserve = new WhiteSpaceProcessor()
	{
		public String process( String text )	{ return text; }
	};
	
	protected static WhiteSpaceProcessor theReplace = new WhiteSpaceProcessor()
	{
		public String process( String text )
		{
			final int len = text.length();
			StringBuffer result = new StringBuffer(len);
			
			for( int i=0; i<len; i++ )
				if( this.isWhiteSpace(text.charAt(i)) )
					result.append(' ');
				else
					result.append(text.charAt(i));
			
			return result.toString();		
		}
	};

	protected static WhiteSpaceProcessor theCollapse= new WhiteSpaceProcessor()
	{
		public String process( String text )
		{
			char[] chars = text.toCharArray();
			int len = text.length();
			StringBuffer result = new StringBuffer(len/2 /**rough estimation*/ );
			
			boolean inStripMode = true;
			
			for( int i=0; i<len; i++ )
			{
				if( inStripMode && this.isWhiteSpace(chars[i]) )
					continue;	// skip this character
				
				inStripMode = this.isWhiteSpace(chars[i]);
				if( inStripMode )	result.append(' ');
				else				result.append(chars[i]);
			}
			
			// remove trailing whitespaces
			len = result.length();
			if( len>0 && result.charAt(len-1)==' ' )
				len--;
			// whitespaces are already collapsed,
			// so all we have to do is to remove the last one character
			// if it's a whitespace.
			
			result.setLength(len);
			
			return result.toString();
		}
	};
}

