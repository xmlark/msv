/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.grammar.relax;

import java.util.Map;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ExpressionPool;

/**
 * "Grammar" of RELAX Namespace.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXGrammar
{
	/** map from namespace URI to RELAX module.
	 * 
	 * All modules are stored in this map.
	 */
	public final Map moduleMap = new java.util.HashMap();
	
	/** top-level expression */
	public Expression topLevel;
	
	/** expression pool that was used to create these objects */
	public final ExpressionPool pool;
	
	public RELAXGrammar( ExpressionPool pool ) { this.pool = pool; }
}
