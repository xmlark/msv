package com.sun.msv.schematron.reader;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPath;

import com.sun.msv.reader.ChildlessState;
import com.sun.msv.schematron.grammar.SAction;

public abstract class SActionState extends ChildlessState {
	
	public void endSelf() {
		SRELAXNGReader reader = (SRELAXNGReader)this.reader;
		
		String test = startTag.getAttribute("test");
		
		if( test!=null ) {
			try {
				onActionReady(new SAction(
                    new XPath(test,null,new PrefixResolverImpl(this), XPath.SELECT),
                    document.toString().trim()
                ));
			} catch( TransformerException e ) {
				reader.reportError( SRELAXNGReader.ERR_INVALID_XPATH, test, e.getMessage() );
			}
		} else {
			reader.reportError( SRELAXNGReader.ERR_MISSING_ATTRIBUTE, startTag.qName, "context" );
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
