/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.driver.textui;

import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import com.sun.tranquilo.reader.GrammarReaderController;

/**
 * GrammarReaderController that prints all errors and warnings.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DebugController implements GrammarReaderController
{
	public void warning( Locator[] loc, String errorMessage )
	{
		System.out.println(errorMessage);
		
		for( int i=0; i<loc.length; i++ )
			printLocation(loc[i]);
	}
	
	public void error( Locator[] loc, String errorMessage, Exception nestedException )
	{
		if( nestedException instanceof SAXException )
		{
			System.out.println("SAXException: " + nestedException.getLocalizedMessage() );
			SAXException se = (SAXException)nestedException;
			if(se.getException()!=null)
				System.out.println("  nested exception: " + se.getException().getLocalizedMessage() );
		}
		else
		{
			System.out.println(errorMessage);
		}
		
		for( int i=0; i<loc.length; i++ )
			printLocation(loc[i]);
	}
	
	private void printLocation( Locator loc )
	{
		System.out.println( "  "+
			(loc.getLineNumber()+1)+":"+
			loc.getColumnNumber()+"@"+
			loc.getSystemId() );
	}

	public InputSource resolveInclude( String url ) { return null; }
}
