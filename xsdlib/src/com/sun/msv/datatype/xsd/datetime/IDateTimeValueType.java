package com.sun.tranquilo.datatype.datetime;

public interface IDateTimeValueType
{
	BigDateTimeValueType getBigValue();
	
	/** returns the result of addition of this object and TimeDuration.
	 * 
	 * this object will not be mutated.
	 */
	IDateTimeValueType add( ITimeDurationValueType duration );
	
	/** gets the normalized IDateTimeValueType.
	 * 
	 * normalized value always has GMT timezone or no timezone
	 */
	IDateTimeValueType normalize();
	
	/** compare two DateTimeValueType as defined in
	 *  com/sun/tranquilo/datatype/Comparator
	 */
	int compare( IDateTimeValueType rhs );
}
