package com.sun.msv.schematron.grammar;

import org.apache.xpath.XPath;

/**
 * assert or report
 */
public class SAction {
    
	public final XPath		xpath;
	public final String		document;
    
    public SAction( XPath xp, String msg ) {
        this.xpath = xp;
        this.document = msg;
    }
}
