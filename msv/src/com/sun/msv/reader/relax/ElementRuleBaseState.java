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

import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.reader.SimpleState;
import com.sun.tranquilo.reader.ExpressionOwner;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.relax.TagClause;
import com.sun.tranquilo.grammar.relax.ElementRule;
import com.sun.tranquilo.grammar.relax.ElementRules;
import com.sun.tranquilo.util.StartTagInfo;

/**
 * Base implementation for ElementRuleWithHedgeState and ElementRuleWithTypeState.
 */
abstract class ElementRuleBaseState extends SimpleState
{
	protected TagClause clause;
	
	/** gets reader in type-safe fashion */
	protected RELAXReader getReader() { return (RELAXReader)reader; }
	
	/** gets content model of this elementRule */
	protected abstract Expression getContentModel();
	
	/** notification of inline tag element.
	 * 
	 * this method is called by InlineTagState after it is parsed
	 */
	protected void onEndInlineClause( TagClause inlineTag )
	{
		if(clause!=null)
		{// more than one inline tag was specified
			reader.reportError( RELAXReader.ERR_MORE_THAN_ONE_INLINE_TAG );
			// recover by ignoring previous local tag.
		}
		clause = inlineTag;
	}
	
	protected void endSelf()
	{
		String role = startTag.getAttribute("role");
		String label = startTag.getAttribute("label");
		
		if(role==null && label==null)
		{
			reader.reportError( RELAXReader.ERR_MISSING_ATTRIBUTE_2,
								"elementRule", "role", "label" );
			// recover from error by supplying dummy label
			label = "<undefined>";
		}
		
		if( label==null )	label=role;	// label attribute defaults to role attribute.
		
		if( clause==null )
		{
			// inline <tag> element was not found.
			// role element must point to some TagClause
			if( role==null )
			{
				reader.reportError( RELAXReader.ERR_MISSING_ATTRIBUTE,
									"elementRule","role");
				// recover by assuming a harmless Clause
				clause = new TagClause();
			}
			else
			{
				clause = getReader().currentModule.tags.getOrCreate(role);
			}
		}
		
		ElementRules er = getReader().currentModule.elementRules.getOrCreate(label);
		getReader().setDeclaredLocationOf(er);	// remember where this ElementRules is defined
		er.addElementRule( reader.pool, new ElementRule( reader.pool, clause, getContentModel() ) );
		
		super.endSelf();
	}

	
	protected State createChildState( StartTagInfo tag )
	{
//		this check is already performed by the derived class
//		if( !tag.namespaceURI.equals(RELAXReader.RELAXCoreNamespace) )	return null;
		
		if( tag.localName.equals("tag") )	return new InlineTagState();
		
		return null;	// otherwise unknown
	}
}
