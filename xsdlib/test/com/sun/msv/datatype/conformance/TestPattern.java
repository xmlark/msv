/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.datatype.conformance;
import com.sun.tranquilo.datatype.*;

/**
 * test pattern interface.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
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