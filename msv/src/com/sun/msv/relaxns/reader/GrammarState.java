/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.relaxns.reader;

import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.*;

/**
 * parses &lt;grammar&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GrammarState extends SimpleState
{
	/** gets reader in type-safe fashion */
	protected RELAXNSReader getReader() { return (RELAXNSReader)reader; }

	protected State createChildState( StartTagInfo tag )
	{
		if(!RELAXNSReader.RELAXNamespaceNamespace.equals(tag.namespaceURI) )	return null;

		if(tag.localName.equals("namespace"))	return new NamespaceState();
		if(tag.localName.equals("topLevel"))	return new TopLevelState();
		if(tag.localName.equals("include"))		return new IncludeGrammarState();
		
		return null;
	}
	
	protected void startSelf()
	{
		super.startSelf();
		
		{// check relaxNamespaceVersion
			final String nsVersion = startTag.getAttribute("relaxNamespaceVersion");
			if( nsVersion==null )
				reader.reportWarning( reader.ERR_MISSING_ATTRIBUTE, "module", "relaxNamespaceVersion" );
			else
			if(!"1.0".equals(nsVersion))
				reader.reportWarning( RELAXNSReader.WRN_ILLEGAL_RELAXNAMESPACE_VERSION, nsVersion );
		}
	}
}
