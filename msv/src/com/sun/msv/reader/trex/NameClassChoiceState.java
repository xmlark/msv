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
import com.sun.tranquilo.grammar.ChoiceNameClass;
import java.util.ArrayList;
import java.util.List;

public class NameClassChoiceState extends NameClassWithChildState
{
	protected NameClass castNameClass( NameClass halfCasted, NameClass newChild )
	{
		if( halfCasted==null )	return newChild;	// first item
		
		else return new ChoiceNameClass( halfCasted, newChild );
	}
}
