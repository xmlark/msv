package com.sun.tranquilo.generator;

import com.sun.tranquilo.datatype.DataType;

/**
 * generates an text value that matchs to a datatype.
 */
public interface DataTypeGenerator
{
	String generate( DataType dt );
}
