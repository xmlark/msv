/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.regexp;

import com.sun.msv.util.StartTagInfo;
import com.sun.msv.grammar.ExpressionPool;

/**
 * StartTagInfo plus AttributeTokens.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class StartTagInfoEx extends StartTagInfo
{
	public final AttributeToken[] attTokens;
	
	public StartTagInfoEx( StartTagInfo base, REDocumentDeclaration docDecl )
	{
		super( base.namespaceURI, base.localName, base.qName,
			   base.attributes, base.context );
		
		attTokens = new AttributeToken[attributes.getLength()];
		for( int i=0; i<attTokens.length; i++ )
			attTokens[i] = new AttributeToken(
				docDecl.getPool(),
				attributes.getURI(i),
				attributes.getLocalName(i),
				attributes.getValue(i),
				context, docDecl.getResidualCalculator() );
	}
}
