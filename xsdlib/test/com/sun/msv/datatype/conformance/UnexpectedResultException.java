package com.sun.tranquilo.datatype.conformance;

import com.sun.tranquilo.datatype.*;
	
public class UnexpectedResultException extends Exception
{
	public final DataType type;
	public final String baseTypeName;
	public final String testInstance;
	public final boolean supposedToBeValid;
	public final TypeIncubator incubator;

	UnexpectedResultException( DataType typeObj, String baseTypeName,
		String testInstance, boolean supposedToBeValid, TypeIncubator ti )
	{
		this.type			= typeObj;
		this.baseTypeName		= baseTypeName;
		this.testInstance		= testInstance;
		this.supposedToBeValid	= supposedToBeValid;
		this.incubator			= ti;
	}
}
