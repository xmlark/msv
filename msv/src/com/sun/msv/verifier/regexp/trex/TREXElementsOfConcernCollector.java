/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.verifier.regexp.trex;

import com.sun.tranquilo.verifier.regexp.ElementsOfConcernCollector;
import com.sun.tranquilo.grammar.trex.*;
import java.util.Collection;

/**
 * Collects "elements of concern".
 * 
 * "Elements of concern" are element declarations that are possibly applicable to
 * the next element. These gathered element declarations are then tested against
 * next XML element.
 */
class TREXElementsOfConcernCollector
	extends ElementsOfConcernCollector
	implements TREXPatternVisitor
{
	public final Object onConcur( ConcurPattern exp )
	{
		exp.exp1.visit(this);
		exp.exp2.visit(this);
		return null;
	}
	
	public final Object onInterleave( InterleavePattern exp )
	{
		exp.exp1.visit(this);
		exp.exp2.visit(this);
		return null;
	}
	
}
