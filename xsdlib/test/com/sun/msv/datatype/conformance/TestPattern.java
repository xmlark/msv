package com.sun.tranquilo.datatype.conformance;
import com.sun.tranquilo.datatype.*;

public interface TestPattern
{
	/** returns the number of test cases to be generated */
	long totalCases();
	
	/** restart generating test cases */
	void reset();
	
	/** decorate the given TestCase.
	 * 
	 * @return answer
	 */
	String get(TypeIncubator ti) throws BadTypeException;
	
	/** generate next test case */
	void next();
	
	/** true indicates get method can be safely called */
	boolean hasMore();
}