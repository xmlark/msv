/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.relax;

import org.xml.sax.Locator;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.relax.AttPoolClause;

public class AttPoolState extends ClauseState
{
	protected void endSelf( )
	{
		super.endSelf();
		
		final String role = startTag.getAttribute("role");
		if(role==null)
		{
			reader.reportError(RELAXReader.ERR_MISSING_ATTRIBUTE, "attPool","role");
			return;	// recover by ignoring this declaration
		}
		
		AttPoolClause c = getReader().currentModule.attPools.getOrCreate(role);
		
		if(c.exp!=null)
		{
			// someone has already initialized this clause.
			// this happens when more than one attPool element declares the same role.
			reader.reportError(
				new Locator[]{getReader().getDeclaredLocationOf(c),location},
				RELAXReader.ERR_MULTIPLE_ATTPOOL_DECLARATIONS, new Object[]{role} );
			// recover from error by ignoring previous tag declaration
		}
		
		c.exp = exp;	// exp holds a sequence of AttributeExp
		getReader().setDeclaredLocationOf(c);	// remember where this AttPool is declared
		
		return;
	}
}
