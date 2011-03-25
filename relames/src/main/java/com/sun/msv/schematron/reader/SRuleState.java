package com.sun.msv.schematron.reader;

import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPath;

import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.schematron.grammar.SAction;
import com.sun.msv.schematron.grammar.SRule;
import com.sun.msv.util.StartTagInfo;

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
			try {
                XPath xpath = new XPath(context,null,new PrefixResolverImpl(this), XPath.MATCH);
                
                ((SRuleReceiver)parentState).onRule(new SRule(xpath,asserts,reports));
			} catch( TransformerException e ) {
				reader.reportError( SRELAXNGReader.ERR_INVALID_XPATH, context, e.getMessage() );
			}
		} else {
			reader.reportError( SRELAXNGReader.ERR_MISSING_ATTRIBUTE, startTag.qName, "context" );
		}
		
		super.endSelf();
	}
}
