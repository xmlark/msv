package com.sun.tranquilo.datatype;

/**
 * default implementaion for DataType interface
 */
abstract class DataTypeImpl implements DataType
{
	private final String typeName;
	
	public String getName()
	{
		return typeName;
	}
	
	// the majority is atom type
	public boolean isAtomType() { return true; }
	
	protected DataTypeImpl( String typeName )	{ this.typeName=typeName; }
}
