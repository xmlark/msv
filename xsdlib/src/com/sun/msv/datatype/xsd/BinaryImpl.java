package com.sun.tranquilo.datatype;

import java.util.Hashtable;

/**
 * base class for Base64BinaryType and HexBinaryType
 */
public abstract class BinaryImpl extends DataTypeImpl
{
	public boolean verify( String content )
	{
		// performs whitespace pre-processing
		content = WhiteSpaceProcessor.theCollapse.process(content);
		
		// checks additional facets
		if( pattern!=null && !pattern.verify(content) )			return false;
		
		if( lengths==null && enumeration==null )
		{// if neither length facets or enumeration facets are specified,
		//  we don't need to compute a value object.
			return checkFormat(content);
		}
		else
		{
			// checks constraints over value space
			BinaryValueType value;
			try
			{
				value = (BinaryValueType)convertValue(content);
			}
			catch( ConvertionException e ) { return false; }
		
			if( lengths!=null   && !lengths.verify(value.rawData.length) )	return false;
			if( enumeration!=null && !enumeration.verify(value) )			return false;
		
			return true;
		}
	}
	
	public DataTypeErrorDiagnosis diagnose( String content )
	{
		// TODO : implement this method
		return null;
	}
	
	/**
	 * checks that given string is properly formatted.
	 * 
	 * @return		true	if the format is proper.
	 *				false	otherwise 
	 */
	protected abstract boolean checkFormat( String lexicalValue );
	
	protected final LengthFacet lengths;
	protected final PatternFacet pattern;
	protected final EnumerationFacet enumeration;
	
	/** constructor for derived-type from binary by restriction. */
	protected BinaryImpl(String typeName,LengthFacet lengths,PatternFacet pattern,EnumerationFacet enumeration)
	{
		super( typeName );
		this.lengths	= lengths;
		this.pattern	= pattern;
		this.enumeration= enumeration;
	}
	
}
