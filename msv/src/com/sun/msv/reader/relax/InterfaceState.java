package com.sun.tranquilo.reader.relax;

import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.reader.SimpleState;
import com.sun.tranquilo.grammar.relax.RELAXModule;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.ChildlessState;

/**
 * &lt;interface&gt; element and &lt;div&gt; in interface.
 */
class InterfaceState extends SimpleState
{
	protected State createChildState( StartTagInfo tag )
	{
		if(!tag.namespaceURI.equals(RELAXReader.RELAXCoreNamespace))	return null;
		
		if(tag.localName.equals("div"))		return new InterfaceState();
		
		RELAXModule module = getReader().currentModule;
		
		if(tag.localName.equals("export"))
		{
			final String role = tag.getAttribute("role");
			final String label = tag.getAttribute("label");
			
			if(role!=null)
			{
				// for the content expression of exported AttPoolClause, 
				// nullSet is temporarily assigned.
				// Actual expression will be created at final wrap-up
				// at GrammarState.endSelf
				
				// by assigning non-null value, it becomes possible to
				// detect references to unexported attPools.
				module.exportedAttPools.getOrCreate(role).exp
					= Expression.nullSet;
			}
			else
			if(label!=null)
			{
				module.elementRules.getOrCreate(label).exported = true;
			}
			else
				reader.reportError(RELAXReader.ERR_MISSING_ATTRIBUTE_2,
								   "export", "label","role" );
				// recover by ignoring this export
			
			return new ChildlessState();
		}
		if(tag.localName.equals("hedgeExport"))
		{
			final String label = tag.getAttribute("label");
			if(label==null)
				reader.reportError(RELAXReader.ERR_MISSING_ATTRIBUTE,"hedgeExport","label");
				// recover by ignoring this hedgeExport
			else
				module.hedgeRules.getOrCreate(label).exported = true;
			
			return new ChildlessState();
		}
		
		return null;
	}

	protected RELAXReader getReader() { return (RELAXReader)reader; }
}
