package com.sun.tranquilo.reader.relax;

import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.*;

public class GrammarState extends SimpleState
{
	/** gets reader in type-safe fashion */
	protected RELAXReader getReader() { return (RELAXReader)reader; }

	protected State createChildState( StartTagInfo tag )
	{
		if(!RELAXReader.RELAXNamespaceNamespace.equals(tag.namespaceURI) )	return null;

// TODO: no div for grammar?
//		if(tag.localName.equals("div"))			return new DivInGrammarState();
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
				reader.reportWarning( RELAXReader.ERR_MISSING_ATTRIBUTE, "module", "relaxNamespaceVersion" );
			else
			if(!"1.0".equals(nsVersion))
				reader.reportWarning( RELAXReader.WRN_ILLEGAL_RELAXNAMESPACE_VERSION, nsVersion );
		}
	}
}
