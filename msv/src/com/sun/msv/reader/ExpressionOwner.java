/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader;

import com.sun.tranquilo.grammar.Expression;

/**
 * interface that must be implemented by the parent state of ExpressionState.
 * 
 * ExpressionState notifies its parent by using this interface.
 */
public interface ExpressionOwner
{
	void onEndChild( Expression exp );
}
