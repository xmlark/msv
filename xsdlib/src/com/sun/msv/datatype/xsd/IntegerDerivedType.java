package com.sun.tranquilo.datatype;

/**
 * base class for types derived from integer.
 */
abstract class IntegerDerivedType extends ConcreteType implements Comparator
{
	protected IntegerDerivedType( String typeName )
	{ super(typeName); }
	
	public final int isFacetApplicable( String facetName )
	{
		// TODO : should we allow scale facet, or not?
		if( facetName.equals(FACET_PRECISION)
		||	facetName.equals(FACET_PATTERN)
		||	facetName.equals(FACET_ENUMERATION)
		||	facetName.equals(FACET_MAXINCLUSIVE)
		||	facetName.equals(FACET_MININCLUSIVE)
		||	facetName.equals(FACET_MAXEXCLUSIVE)
		||	facetName.equals(FACET_MINEXCLUSIVE) )
			return APPLICABLE;
		else
			return NOT_ALLOWED;
	}
	
	protected final boolean checkFormat( String content, ValidationContextProvider context )
	{// integer-derived types always checks lexical format by trying to convert it to value object
		return convertToValue(content,context)!=null;
	}
	
	public final int compare( Object o1, Object o2 )
	{// integer-derived type always uses Comparable object as its value type
		final int r = ((Comparable)o1).compareTo(o2);
		if(r<0)	return LESS;
		if(r>0)	return GREATER;
		return EQUAL;
	}
}
