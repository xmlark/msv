package com.sun.msv.schematron.reader;

import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.schematron.grammar.SRule;
import com.sun.msv.schematron.grammar.SAction;
import org.apache.xpath.XPath;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class SRuleState extends SimpleState implements SActionReceiver {
	
	protected State createChildState( StartTagInfo tag ) {
		
		if(!tag.namespaceURI.equals(SRELAXNGReader.SchematronURI))
			return null;

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
	
	public void endSelf() {
		SRELAXNGReader reader = (SRELAXNGReader)this.reader;
		
		String context = startTag.getAttribute("context");
		
		if(context!=null) {
			SRule rule = new SRule();
			rule.asserts = (SAction[])asserts.toArray(new SAction[asserts.size()]);
			rule.reports = (SAction[])reports.toArray(new SAction[reports.size()]);
			try {
				rule.xpath = new XPath(context,null,new PrefixResolverImpl(this), XPath.MATCH);
				((SRuleReceiver)parentState).onRule(rule);
			} catch( TransformerException e ) {
				reader.reportError( reader.ERR_INVALID_XPATH, context, e.getMessage() );
			}
		} else {
			reader.reportError( reader.ERR_MISSING_ATTRIBUTE, startTag.qName, "context" );
		}
		
		super.endSelf();
	}
}
