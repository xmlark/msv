package com.sun.msv.schematron.grammar;

import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xml.utils.PrefixResolverDefault;
import org.w3c.dom.Node;
import javax.xml.transform.TransformerException;

public class SRule {
	public XPath		xpath;
	public SAction[]	asserts;
	public SAction[]	reports;
	
	/**
	 * checks if the given Node matches this rule.
	 */
	public boolean matches( Node node ) throws TransformerException {
		return xpath.execute(
			new XPathContext(), node, new PrefixResolverDefault(node) )
			.num() != XPath.MATCH_SCORE_NONE;
	}
}
