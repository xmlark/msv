package com.sun.tranquilo.datatype;

import java.util.StringTokenizer;

final public class ListType extends ConcreteType implements Discrete
{
	/**
	 * derives a new datatype from atomic datatype by list
	 */
	public ListType( String newTypeName, DataTypeImpl itemType )
		throws BadTypeException
	{
		super(newTypeName);
		
		// derivation by list is only applicable to AtomType
		if(!itemType.isAtomType())
			throw new BadTypeException( BadTypeException.ERR_INVALID_ITEMTYPE );
		
		this.itemType = itemType;
	}
	
	/** atomic base type */
	final private DataTypeImpl itemType;

	// list type is not an atom type.
	public final boolean isAtomType() { return false; }
	
	public final int isFacetApplicable( String facetName )
	{
		if( facetName.equals(FACET_LENGTH)
		||	facetName.equals(FACET_MINLENGTH)
		||	facetName.equals(FACET_MAXLENGTH)
		||	facetName.equals(FACET_ENUMERATION) )
			return APPLICABLE;
		else
			return NOT_ALLOWED;
	}
	
	protected final boolean checkFormat( String content, ValidationContextProvider context )
	{
		// Are #x9, #xD, and #xA allowed as a separator, or not?
		StringTokenizer tokens = new StringTokenizer(content);
		
		while( tokens.hasMoreTokens() )
			if(!itemType.checkFormat(tokens.nextToken(),context))	return false;
		
		return true;
	}
	
	public Object convertToValue( String content, ValidationContextProvider context )
	{
		// StringTokenizer correctly implements the semantics of whiteSpace="collapse"
		StringTokenizer tokens = new StringTokenizer(content);
		
		Object[] values = new Object[tokens.countTokens()];
		int i=0;
		
		while( tokens.hasMoreTokens() )
		{
			if( ( values[i++] = itemType.convertToValue(tokens.nextToken(),context) )==null )
				return null;
		}
			
		return new ListValueType(values);
	}
	
	public final int countLength( Object value )
	{// for list type, length is a number of items.
		return ((ListValueType)value).values.length;
	}
	
	/** The current implementation detects which list item is considered wrong. */
	protected DataTypeErrorDiagnosis diagnoseValue(String content, ValidationContextProvider context)
	{
		// StringTokenizer correctly implements the semantics of whiteSpace="collapse"
		StringTokenizer tokens = new StringTokenizer(content);
		
		while( tokens.hasMoreTokens() )
		{
			String token = tokens.nextToken();
			DataTypeErrorDiagnosis err = itemType.diagnose(token,context);
			if(err!=null) return err;
		}
		
		return null;	// accepted
	}

}