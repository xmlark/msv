package com.sun.tranquilo.datatype.datetime;

interface ITimeDurationValueType
{
	BigTimeDurationValueType getBigValue();

	/** compare two ITimeDurationValueType as defined in
	 *  com/sun/tranquilo/datatype/Comparator
	 */
	int compare( ITimeDurationValueType rhs );
}
