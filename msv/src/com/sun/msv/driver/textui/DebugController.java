package com.sun.tranquilo.driver.textui;

import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import com.sun.tranquilo.reader.GrammarReaderController;

public class DebugController implements GrammarReaderController
{
	public void warning( Locator[] loc, String errorMessage )
	{
		System.out.println(errorMessage);
	}
	
	public void error( Locator[] loc, String errorMessage, Exception nestedException )
	{
		if( nestedException instanceof SAXException )
		{
			System.out.println("SAXException");
			nestedException.printStackTrace(System.err);
			
			SAXException se = (SAXException)nestedException;
			if(se.getException()!=null)
			{
				System.out.println("nested exception");
				se.getException().printStackTrace(System.err);
			}
		}
		else
		{
			System.out.println(errorMessage);
		}
		
		for( int i=0; i<loc.length; i++ )
			System.out.println( "  "+
				(loc[i].getLineNumber()+1)+":"+
				loc[i].getColumnNumber()+"@"+
				loc[i].getSystemId() );
	}

	public InputSource resolveInclude( String url ) { return null; }
}
