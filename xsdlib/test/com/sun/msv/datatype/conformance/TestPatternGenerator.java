package com.sun.tranquilo.datatype.conformance;

import org.jdom.*;
import java.util.List;
import com.sun.tranquilo.datatype.Facets;

class TestPatternGenerator
{
	/**
	 * parses test pattern specification and returns it.
	 *
	 * @param patternElement
	 *		one of "combination","choice","facet".
	 */
	public static TestPattern parse( Element patternElement )
		throws Exception
	{
		final String tagName = patternElement.getName();
		if( tagName.equals("combination") || tagName.equals("choice") )
		{
			// parse children
			List lst = patternElement.getChildren();
			TestPattern[] children = new TestPattern[lst.size()];
			for( int i=0; i<lst.size(); i++ )
				children[i] = parse( (Element)lst.get(i) );
			
			if( tagName.equals("combination") )
			{
				boolean mode = true;	// default is 'AND'
				if( patternElement.getAttribute("mode")!=null
				&&  patternElement.getAttributeValue("mode").equals("or") )
					mode = false;
				
				return new FullCombinationPattern(children,mode);
			}
			else
			{
				return new ChoiceTestPattern(children);
			}
		}
		if( tagName.equals("facet") )
		{
			Facets facets = new Facets();
			facets.add(
				patternElement.getAttributeValue("name"),
				patternElement.getAttributeValue("value"),
				true/*fixed*/ );
			return new SimpleTestPattern( new TestCase( facets,
				trimAnswer( patternElement.getAttributeValue("answer") ) ) );
		}
		
		throw new Exception("unknown pattern:"+tagName);
	}
	
	public static String trimAnswer( String answer )
	{
		String r="";
		final int len = answer.length();
		for( int i=0; i<len; i++ )
		{
			final char ch = answer.charAt(i);
			if(ch=='o' || ch=='.')	r+=ch;
		}
		
		return r;
	}
}
