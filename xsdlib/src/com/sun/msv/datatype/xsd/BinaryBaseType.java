package com.sun.tranquilo.datatype;

abstract class BinaryBaseType extends DataTypeImpl implements Discrete
{
	BinaryBaseType( String typeName ) { super(typeName); }
	
	final public int isFacetApplicable( String facetName )
	{
		if( facetName.equals( FACET_LENGTH )
		||	facetName.equals( FACET_MAXLENGTH )
		||	facetName.equals( FACET_MINLENGTH )
		||	facetName.equals( FACET_PATTERN )
		||	facetName.equals( FACET_ENUMERATION ) )
			return APPLICABLE;
		else
			return NOT_ALLOWED;
	}
	
	final public int countLength( Object value )
	{// for binary types, length is the number of bytes
		return ((BinaryValueType)value).rawData.length;
	}
}
