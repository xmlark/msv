package com.sun.tranquilo.datatype.conformance;

import com.sun.tranquilo.datatype.DataType;
	
public class UnexpectedResultException extends Exception
{
	public final DataType type;
	public final String baseTypeName;
	public final String testInstance;
	public final boolean supposedToBeValid;
	public final TestCase testCase;

	UnexpectedResultException( DataType typeObj, String baseTypeName,
		String testInstance, boolean supposedToBeValid, TestCase testCase )
	{
		this.type			= typeObj;
		this.baseTypeName		= baseTypeName;
		this.testInstance		= testInstance;
		this.supposedToBeValid	= supposedToBeValid;
		this.testCase			= testCase;
	}
}
