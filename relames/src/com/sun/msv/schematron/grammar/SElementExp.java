package com.sun.msv.schematron.grammar;

import java.util.Collection;

import org.apache.xml.utils.PrefixResolver;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.trex.ElementPattern;

/**
 * ElementPattern with schematron annotation
 */
public class SElementExp extends ElementPattern {
    
    /** this object is used to resolve prefixes found in XPath. */
    public final PrefixResolver prefixResolver;
    
    /** schematron rules. */
    public final SRule[]  rules;
    
    /** actions directly apply to this element. */
    public final SActions actions;
	
	public SElementExp( NameClass nc, Expression body,
        PrefixResolver pr, Collection rules, SActions actions ) {
		
        super(nc,body);
        
        this.prefixResolver = pr;
        this.rules = (SRule[]) rules.toArray(new SRule[rules.size()]);;
        this.actions = actions;
	}
}
