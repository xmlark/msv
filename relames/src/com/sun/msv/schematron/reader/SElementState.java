package com.sun.msv.schematron.reader;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.trex.ElementState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.schematron.grammar.SElementExp;
import com.sun.msv.schematron.grammar.SAction;
import com.sun.msv.schematron.grammar.SRule;
import org.apache.xpath.XPath;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class SElementState extends ElementState implements SActionReceiver, SRuleReceiver {

	protected State createChildState( StartTagInfo tag ) {
		
		if(!tag.namespaceURI.equals(SRELAXNGReader.SchematronURI))
			return super.createChildState(tag);

		/*
		TODO:
			The context attribute of the rule element specifies "match".
			For example, <rule context="CCC"> matches to any elements whose
			name is CCC, even if it is not a direct child of the node.
			
			I couldn't find how to achieve this by using Xalan. So just for now,
			"rule" element is disabled.
		*/
//		if( tag.localName.equals("rule") )
//			return new SRuleState();
		if( tag.localName.equals("assert") )
			return new SActionState.SAssertState();
		if( tag.localName.equals("report") )
			return new SActionState.SReportState();
		return null;
	}
	
	private final Vector asserts = new Vector();
	public void onAssert( SAction action ) {
		asserts.add(action);
	}
	private final Vector reports = new Vector();
	public void onReport( SAction action ) {
		reports.add(action);
	}
	private final Vector rules = new Vector();
	public void onRule( SRule rule ) {
		rules.add(rule);
	}
	
	protected Expression annealExpression( Expression contentModel ) {
		SElementExp exp = new SElementExp( nameClass, contentModel );

		// set prefix resolver.
		exp.prefixResolver = new PrefixResolverImpl(this);
		
		// put <assert> and <report> into a rule object.
		if( asserts.size()!=0 || reports.size()!=0 ) {
			SRule rule = new SRule();
			rule.asserts = (SAction[])asserts.toArray(new SAction[asserts.size()]);
			rule.reports = (SAction[])reports.toArray(new SAction[reports.size()]);
			try {
				rule.xpath = new XPath(".",null,exp.prefixResolver,XPath.SELECT);
			} catch( TransformerException e ) {
				// impossible.
				throw new Error();
			}
			rules.add(rule);
		}
		exp.rules = (SRule[])rules.toArray(new SRule[rules.size()]);
		
		
		return exp;
	}
}
