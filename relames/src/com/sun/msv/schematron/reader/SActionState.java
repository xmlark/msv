package com.sun.msv.schematron.reader;

import com.sun.msv.reader.ChildlessState;
import com.sun.msv.schematron.grammar.SAction;
import org.apache.xpath.XPath;
import javax.xml.transform.TransformerException;

public abstract class SActionState extends ChildlessState {
	
	public void endSelf() {
		SRELAXNGReader reader = (SRELAXNGReader)this.reader;
		
		String test = startTag.getAttribute("test");
		
		if( test!=null ) {
			SAction a = new SAction();
			try {
				a.xpath = new XPath(test,null,new PrefixResolverImpl(this), XPath.SELECT);
				a.document = document.toString().trim();
				onActionReady(a);
			} catch( TransformerException e ) {
				reader.reportError( reader.ERR_INVALID_XPATH, test, e.getMessage() );
			}
		} else {
			reader.reportError( reader.ERR_MISSING_ATTRIBUTE, startTag.qName, "context" );
		}
		
		super.endSelf();
	}
	
	private StringBuffer document = new StringBuffer();
	public void characters( char[] buf, int start, int len ) {
		document.append( buf, start, len );
	}
	
	protected abstract void onActionReady( SAction action );
	
	public static class SAssertState extends SActionState {
		protected void onActionReady( SAction action ) {
			((SActionReceiver)parentState).onAssert(action);
		}
	}
	public static class SReportState extends SActionState {
		protected void onActionReady( SAction action ) {
			((SActionReceiver)parentState).onReport(action);
		}
	}
		
}
