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

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.SequenceState;

/**
 * parses &lt;optional&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class OptionalState extends SequenceState
{
	protected Expression annealExpression( Expression exp )
	{
		return reader.pool.createOptional(exp);
	}
}
