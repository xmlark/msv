/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.datetime;

/**
 * interface as a value type of TimeDurationType
 * 
 * @author Kohsuke KAWAGUCHI
 */
public interface ITimeDurationValueType
{
	BigTimeDurationValueType getBigValue();

	/** compare two ITimeDurationValueType as defined in
	 *  com.sun.msv.datatype/Comparator
	 */
	int compare( ITimeDurationValueType rhs );
}
