package com.sun.tranquilo.datatype;

import java.util.StringTokenizer;

public class ListType extends DataTypeImpl
{
	/** atomic base type */
	final private DataType itemType;
	final private LengthFacet lengths;
	final private EnumerationFacet enumeration;

	// list type is not an atom type.
	public boolean isAtomType() { return false; }
	
	public boolean verify( String content )
	{
		// performs whitespace pre-processing
		// whiteSpace is collapse and cannot be changed by schema author
		content = WhiteSpaceProcessor.theCollapse.process(content);
		
		// TODO : make sure that separators are correctly handled.
		// Are #x9, #xD, and #xA allowed as a separator, or not?
		StringTokenizer tokens = new StringTokenizer(content);
		
		final int length = tokens.countTokens();
		while( tokens.hasMoreTokens() )
			if(!itemType.verify(tokens.nextToken()))		return false;
		
		if( lengths!=null && !lengths.verify(length) )		return false;
		
		if( enumeration!=null )
		{// if enumeration facet is present, we have to convert to value object.
			ListValueType value;
			try
			{
				value = (ListValueType)convertValue(content);
			}
			catch( ConvertionException e ) { return false; }	// this is not possible
			
			if( !enumeration.verify(value) )	return false;
		}
		
		return true;
	}
	
	/**
	 * computes the reason of error
	 * 
	 * Application can call this method to provide detailed error message to user.
	 * This method is kept separate from verify method to achieve higher performance
	 * if no such message is necessary at all.
	 * 
	 * @return null
	 *		if 'content' is accepted by this pattern, or 
	 *		if the derived class doesn't support this operation
	 */
	public DataTypeErrorDiagnosis diagnose( String content )
		throws java.lang.UnsupportedOperationException
	{
		// TODO : implement 
		throw new UnsupportedOperationException();
	}
	
	/**
	 * derives a new datatype from this datatype, by adding facets
	 * 
	 * It is completely legal to use null as the newTypeName paratmer,
	 * which means deriving anonymous datatype.
	 */
	public DataType derive( String newTypeName, Facets facets )
		throws BadTypeException
	{
		if( facets.isEmpty() )	return this;	// no need for derivation
		
		return new ListType( newTypeName, itemType,
			LengthFacet.merge(this.lengths,facets),
			EnumerationFacet.merge(this,this.enumeration,facets) );
	}

	/**
	 * derives a new datatype from any datatype by list
	 */
	static ListType deriveByList( String newTypeName, DataType itemType )
		throws BadTypeException
	{
		// derivation by list is only applicable to AtomType
		if(!itemType.isAtomType())
			throw new BadTypeException( BadTypeException.ERR_INVALID_ITEMTYPE );
		
		return new ListType( newTypeName, itemType, null, null );
	}
	
	/**
	 * converts lexcial value to the corresponding value object of the value space
	 * 
	 * @exception	ConvertionException
	 *		when the given lexical value is not valid lexical value for this type.
	 */
	public Object convertValue( String lexicalValue )
		throws ConvertionException
	{
		// TODO : make sure that separators are correctly handled.
		// StringTokenizer correctly implements the semantics of whiteSpace="collapse"
		StringTokenizer tokens = new StringTokenizer(lexicalValue);
		
		Object[] values = new Object[tokens.countTokens()];
		int i=0;
		
		while( tokens.hasMoreTokens() )
			values[i++] = itemType.convertValue(tokens.nextToken());
			
		return new ListValueType(values);
	}
	
	/** used for derivation by restriction from list type */
	protected ListType( String typeName, DataType itemType,
		LengthFacet lengths, EnumerationFacet enumeration )
	{
		super(typeName);
		this.lengths	= lengths;
		this.enumeration= enumeration;
		this.itemType	= itemType;
	}
}