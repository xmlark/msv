/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.driver.textui;

import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import com.sun.msv.reader.GrammarReaderController;

/**
 * GrammarReaderController that prints all errors and warnings.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DebugController implements GrammarReaderController {
	
	/** if true, warnings are reported. If false, not reported. */
	private boolean displayWarning;
	
	/** set to true after "there are warnings..." message is once printed. */
	private boolean warningReported = false;
	
	public DebugController( boolean displayWarning ) {
		// for backward compatibility. Can be removed later.
		this( displayWarning, false );
	}
	
	public DebugController( boolean displayWarning, boolean quiet ) {
		this.displayWarning = displayWarning;
		this.warningReported = quiet;
	}
	
	public void warning( Locator[] loc, String errorMessage ) {
		if(!displayWarning)	{
			if( !warningReported )
				System.out.println( Driver.localize(Driver.MSG_WARNING_FOUND) );
			warningReported = true;
			return;
		}
		
		System.out.println(errorMessage);
		
		if(loc==null || loc.length==0)
			System.out.println("  location unknown");
		else
			for( int i=0; i<loc.length; i++ )
				printLocation(loc[i]);
	}
	
	public void error( Locator[] loc, String errorMessage, Exception nestedException ) {
		if( nestedException instanceof SAXException ) {
			System.out.println("SAXException: " + nestedException.getLocalizedMessage() );
			SAXException se = (SAXException)nestedException;
			if(se.getException()!=null) {
				System.out.println("  nested exception: " + se.getException().getLocalizedMessage() );
				se.getException().printStackTrace(System.out);
			}
		} else {
			System.out.println(errorMessage);
		}
		
		if(loc==null || loc.length==0)
			System.out.println("  location unknown");
		else
			for( int i=0; i<loc.length; i++ )
				printLocation(loc[i]);
	}
	
	private void printLocation( Locator loc ) {
		System.out.println( "  "+
			(loc.getLineNumber()+1)+":"+
			loc.getColumnNumber()+"@"+
			loc.getSystemId() );
	}

	public InputSource resolveEntity( String publicId, String systemId ) {
		return null;
	}
}
