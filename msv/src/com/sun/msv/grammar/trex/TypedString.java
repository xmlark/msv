package com.sun.tranquilo.grammar.trex;

import com.sun.tranquilo.datatype.*;

/**
 * Datatype created by &lt;string&gt; element.
 */
public class TypedString implements DataType
{
	/** this type only matches this string */
	public final String value;
	/** true indicates that whiteSpace should be preserved. */
	protected final boolean preserveWhiteSpace;
	
	public TypedString( String value, boolean preserveWhiteSpace )
	{
		if(preserveWhiteSpace)
			this.value = value;
		else
			this.value = WhiteSpaceProcessor.theCollapse.process(value);
		
		this.preserveWhiteSpace = preserveWhiteSpace;
	}

	public int isFacetApplicable( String facetName )
	{
		// again derivation will never be required.
		throw new UnsupportedOperationException();
	}

	public Object convertToValueObject( String literal, ValidationContextProvider context )
	{
		if(!preserveWhiteSpace)
			literal = WhiteSpaceProcessor.theCollapse.process(literal);
		
		if(value.equals(literal))	return literal;
		else						return null;
	}
	
	public boolean isAtomType() { return true; }
	
	public boolean verify( String literal, ValidationContextProvider context )
	{
		return convertToValueObject(literal,context)!=null;
	}
	
	public DataTypeErrorDiagnosis diagnose( String content, ValidationContextProvider context )
	{
		// TODO: implement this
		throw new UnsupportedOperationException();
	}
	
	public String getName() { return "TREX built-in string"; }
}
