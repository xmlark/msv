package com.sun.tahiti.compiler.ll;

import com.sun.msv.grammar.*;
import com.sun.msv.grammar.util.ExpressionWalker;
import java.util.Set;

/**
 * computes the filter that will be used to parse &lt;interleave&gt;.
 * 
 * <p>
 * See the document of LL marshaller for why such a filter is necessary.
 * 
 * <p>
 * This class traverses the child elements and creates a name class that
 * contains all possible element names.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class FilterCalculator {
	
	// use the calc method.
	private FilterCalculator() {}
	
	/**
	 * computes the set of all element symbols in this branch.
	 */
	public static Set calc( Expression exp ) {
		// compute the possible names.
		ElementNameCollector col = new ElementNameCollector();
		exp.visit(col);
		
		// simplify it
		return col.symbols;
	}
	
	private static class ElementNameCollector extends ExpressionWalker {
		
		Set symbols = new java.util.HashSet();
		
		public void onElement( ElementExp exp ) {
			symbols.add(exp);
			// don't visit the content model.
		}
		public void onAttribute( AttributeExp exp ) {
			// we don't need to traverse the body of attribute.
		}
	}
}
