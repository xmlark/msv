package com.sun.tranquilo.datatype;

public abstract class WhiteSpaceProcessor
{
	abstract String process( String text );
	
	/**
	 * returns a WhiteSpaceProcessor object if "whiteSpace" facet is specified.
	 * Otherwise returns null.
	 */
	protected static WhiteSpaceProcessor create( Facets facets )
		throws BadTypeException
	{
		if( !facets.contains("whiteSpace") )	return null;	// no whiteSpace facet
		
		String o = facets.getFacet("whiteSpace");
		
		if( o.equals("preserve") )		return null;
		if( o.equals("collapse") )		return theCollapse;
		if( o.equals("replace") )		return theReplace;
		
		throw new BadTypeException( BadTypeException.ERR_INVALID_WHITESPACE_VALUE, o );
	}
	
	private static boolean isWhiteSpace( char ch )
	{
		return ch==0x9 || ch==0xA || ch==0xD || ch==0x20;
	}
	
	protected static Replace		theReplace = new Replace();
	protected static Collapse		theCollapse= new Collapse();
			
	
	private static class Replace extends WhiteSpaceProcessor
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
	}
	
	private static class Collapse extends WhiteSpaceProcessor
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
			while( len>0 )
				if( result.charAt(len-1)==' ' )	len--;
			
			return result.substring(0,len);
		}
	}
}

