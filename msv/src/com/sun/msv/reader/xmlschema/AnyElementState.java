/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.xmlschema;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.NameClass;
import com.sun.tranquilo.grammar.ReferenceExp;
import com.sun.tranquilo.grammar.ReferenceContainer;
import com.sun.tranquilo.grammar.trex.ElementPattern;
import com.sun.tranquilo.grammar.xmlschema.XMLSchemaSchema;
import com.sun.tranquilo.grammar.xmlschema.ElementDeclExp;
import com.sun.tranquilo.reader.State;
import java.util.Iterator;

/**
 * used to parse &lt;any &gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AnyElementState extends AnyState
{
	protected Expression createExpression( final String namespace, final String process ) {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
		if( !process.equals("skip") ) {
			if(process.equals("lax") || process.equals("strict"))
				reader.reportWarning( reader.WRN_UNSUPPORTED_ANYELEMENT, process );
			else {
				// invalid attribute value.
				reader.reportError( reader.ERR_BAD_ATTRIBUTE_VALUE, "processContents", process );
				return Expression.nullSet;
			}
		}
		
		// without divide-and-validate framework, we can't handle lax and strict correctly.
		// so just validate them as "skip".
		
		NameClass nc = getNameClass(namespace);
			
		ElementPattern ep = new ElementPattern(nc,Expression.nullSet);
				
		ep.contentModel = 
			// <mixed><zeroOrMore><choice><attribute /><element /></choice></zeroOrMore></mixed>
			reader.pool.createMixed(
				reader.pool.createZeroOrMore(
					reader.pool.createChoice(
						ep,
						reader.pool.createAttribute(nc)
					)
				)
			);
				
		// minOccurs/maxOccurs is processed through interception
		return ep;
	}
}
