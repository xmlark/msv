package com.sun.msv.schematron.grammar;

import java.util.Collection;

import javax.xml.transform.TransformerException;

import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.w3c.dom.Node;

public class SRule extends SActions
{
	private final XPath match;
	
    public SRule( XPath _match, Collection _asserts, Collection _reports ) {
        super(_asserts,_reports);
        this.match = _match;
    }
    
	/**
	 * checks if the given Node matches this rule.
	 */
	public boolean matches( Node node ) throws TransformerException {
		return match.execute(
			new XPathContext(), node, new PrefixResolverDefault(node) )
			.num() != XPath.MATCH_SCORE_NONE;
	}
}
