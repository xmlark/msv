package com.sun.tranquilo.reader.relax;

import org.xml.sax.Locator;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.relax.TagClause;
import com.sun.tranquilo.grammar.SimpleNameClass;

public class TagState extends ClauseState
{
	protected void endSelf( )
	{
		super.endSelf();
		
		final String name = startTag.getAttribute("name");
		String role = startTag.getAttribute("role");
		if(role==null)	role=name;	// role defaults to name
		
		if(name==null)
		{
			reader.reportError(RELAXReader.ERR_MISSING_ATTRIBUTE, "tag","name");
			return;
		}
		
		TagClause c = getReader().currentModule.tags.getOrCreate(role);
		
		if(c.nameClass!=null)
		{
			// someone has already initialized this clause.
			// this happens when more than one tag element declares the same role.
			reader.reportError(
				new Locator[]{getReader().getDeclaredLocationOf(c),location},
				RELAXReader.ERR_MULTIPLE_TAG_DECLARATIONS, new Object[]{role} );
			// recover from error by ignoring previous tag declaration
		}
		
		c.nameClass = new SimpleNameClass(
			getReader().currentModule.targetNamespace,
			name );
		
		c.exp = exp;	// exp holds a sequence of AttributeExp
		getReader().setDeclaredLocationOf(c);	// remember where this tag is declared
		
		return;
	}
}
