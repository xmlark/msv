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
			String result="";
			int len = text.length();
			for( int i=0; i<len; i++ )
				if( this.isWhiteSpace(text.charAt(i)) )
					result += ' ';
				else
					result += text.charAt(i);
			
			return result;		
		}
	};

	protected static WhiteSpaceProcessor theCollapse= new WhiteSpaceProcessor()
	{
		public String process( String text )
		{
			String result="";
			char[] chars = text.toCharArray();
			int len = text.length();
			
			boolean inStripMode = true;
			
			for( int i=0; i<len; i++ )
			{
				if( inStripMode && this.isWhiteSpace(chars[i]) )
					continue;	// skip this character
				
				inStripMode = this.isWhiteSpace(chars[i]);
				if( inStripMode )	result += ' ';
				else				result += chars[i];
			}
			
			// remove trailing whitespaces
			len = result.length();
			if( len>0 && result.charAt(len-1)==' ' )
				len--;
			// whitespaces are already collapsed,
			// so all we have to do is to remove the last one character
			// if it's a whitespace.
			
			return result.substring(0,len);
		}
	};
}

