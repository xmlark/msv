/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.grammar.trex;

import com.sun.tranquilo.grammar.ExpressionVisitor;

/**
 * Visitor interface for TREX pattern.
 * 
 * @see ExpressionVisitor
 * 
 * <p>
 * By implementing this interface, your visitor class can safely walk
 * any AGM (including AGM created from RELAX).
 * 
 * <p>
 * As long as you depend on the core part and TREX extension of AGM
 * and not depend on the stub parts, your visitor shall work with any AGM.
 * So implementing this interface does NOT mean that your visitor is dependent
 * to TREX.
 */
public interface TREXPatternVisitor extends ExpressionVisitor
{
	Object onConcur( ConcurPattern p );
	Object onInterleave( InterleavePattern p );
}
