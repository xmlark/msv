package com.sun.tranquilo.driver.textui;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import com.sun.tranquilo.reader.GrammarReaderController;

public class DebugController implements GrammarReaderController
{
	public void warning( Locator[] loc, String errorMessage )
	{
		System.err.println(errorMessage);
	}
	
	public void error( Locator[] loc, String errorMessage, Exception nestedException )
	{
		if( nestedException instanceof SAXException )
		{
			System.err.println("SAXException");
			nestedException.printStackTrace(System.err);
			
			SAXException se = (SAXException)nestedException;
			if(se.getException()!=null)
			{
				System.err.println("nested exception");
				se.getException().printStackTrace(System.err);
			}
		}
		else
		{
			System.err.println(errorMessage);
		}
		
		for( int i=0; i<loc.length; i++ )
			System.err.println( "  "+
				(loc[i].getLineNumber()+1)+":"+
				loc[i].getColumnNumber()+"@"+
				loc[i].getSystemId() );
	}
}
