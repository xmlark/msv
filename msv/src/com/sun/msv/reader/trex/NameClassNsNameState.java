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
import com.sun.tranquilo.grammar.NamespaceNameClass;

/**
 * parses &lt;nsName&gt; name class.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NameClassNsNameState extends NameClassWithoutChildState
{
	protected NameClass makeNameClass()
	{
		return new NamespaceNameClass( getPropagatedNamespace() );
	}
}
