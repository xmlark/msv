/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.NameClass;
import com.sun.tranquilo.grammar.AnyNameClass;
import com.sun.tranquilo.datatype.WhiteSpaceProcessor;

public class NameClassAnyNameState extends NameClassWithoutChildState
{
	protected NameClass makeNameClass()
	{
		return AnyNameClass.theInstance;
	}
}
