/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package errorinfo;

import java.io.File;
import java.io.FileInputStream;
import javax.xml.parsers.SAXParserFactory;
import com.sun.msv.verifier.ErrorInfo;
import com.sun.msv.verifier.ValidityViolation;
import org.iso_relax.verifier.*;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Uses {@link ErrorInfo} information to get detailed information about errors
 * and its source.
 * 
 * <p>
 * This example takes a schema and a document, then dumps the document.
 * When an error is found, the error is highlighted.
 * 
 * <p>
 * The purpose of this example is to illustrate how you can access
 * {@link ErrorInfo}, which will give you detailed information of
 * the error.
 * 
 * <p>
 * {@link ValidityViolation} is derived from SAXParseException, so you can
 * always access line/column number information at least.
 * However, since this information is about the textual representation of
 * XML, it is sometimes not much useful.
 * 
 * <p>
 * <code>ErrorInfo</code> provides high-level error information,
 * and therefore it is often useful to provide an
 * application-specific error messages to the user.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ErrorReporter
{
	public static void main( String args[] ) throws Exception {
		
		if( args.length!=2 ) {
			System.out.println("ErrorReporter <schema schema> <instance file>");
			return;
		};
		
		// control MSV through JARV.
		 // For more information about JARV, see the jarv examples.
		VerifierFactory factory = new com.sun.msv.verifier.jarv.TheFactoryImpl();
		
		// compile a schema and gets a verifier.
		final Verifier verifier = factory.newVerifier(new File(args[0]));
		
		// create a SAX parser
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		XMLReader reader = parserFactory.newSAXParser().getXMLReader();
		
		// then setup the SAX pipe line as follows:
		//
		//  parser ==> interceptor ==> verifier
		//
		// "interceptor" works as a SAX filter.
		
		Interceptor interceptor = new Interceptor();
		interceptor.setParent(reader);
		interceptor.setContentHandler(verifier.getVerifierHandler());
		
		// set an error handler that throws any error.
		verifier.setErrorHandler(
			com.sun.msv.verifier.util.ErrorHandlerImpl.theInstance);
		
		// parse the file.
		interceptor.parse( new InputSource( new FileInputStream(args[1]) ) );
	}
	
	/**
	 * Interceptor works in this way:
	 * 
	 * <ul><li>
	 * First, it receives a SAX event from the parser.
	 * 
	 * <li>
	 * It then passes a SAX event to the verifier, to see if it's correct.
	 * 
	 * <ul><li>
	 * If there is no error, then the verifier will return without
	 * reporting any error. So the interceptor will print it.
	 * 
	 * <li>
	 * If there is an error, the verfieier will report an error to ErrorHandler.
	 * The error handler we set throws it, so Interceptor will catch it.
	 * If the error is catched, the interceptor will print it accordingly.
	 * 
	 */
	private static class Interceptor extends XMLFilterImpl {
		
		/**
		 * characters has to be buffered because of the way MSV works.
		 * For more information, see the javadoc of
		 * {@link ErrorInfo.BadText}.
		 */
		private StringBuffer buffer = new StringBuffer();
		
		public void startElement(
			String ns, String local, String qname, Attributes atts )
			throws SAXException {
			
			ErrorInfo ei = null;
			boolean unknownError = false;
			
			try {
				super.startElement(ns,local,qname,atts);
				// there is no error.
			} catch( ValidityViolation vv ) {
				// there was an error.
				ei = vv.getErrorInfo();
				
				if(!(ei instanceof ErrorInfo.BadText)
				&& !(ei instanceof ErrorInfo.BadTagName)
				&& !(ei instanceof ErrorInfo.BadAttribute)
				&& !(ei instanceof ErrorInfo.MissingAttribute) )
					// if the type of information is unknown to us,
					// or ei equals to null
					unknownError = true;
			}
			
			// print the text
			printText( ei instanceof ErrorInfo.BadText );
			
			printIndent();
			print("<");
			print( qname,
				unknownError ||	// if the error is unknown, highlight the tag name
				(ei instanceof ErrorInfo.BadTagName) );
			
			for( int i=0; i<atts.getLength(); i++ ) {
				boolean fError = false;
				
				if(ei instanceof ErrorInfo.BadAttribute)
					fError = ((ErrorInfo.BadAttribute)ei).
						attQName.equals(atts.getQName(i));
				
				print(" ");
				print(atts.getQName(i)+"='"+atts.getValue(i)+"'", fError );
			}
			if( ei instanceof ErrorInfo.MissingAttribute ) {
				print(" ");
				print("[missing attributes]",true);
			}
			print(">\n");
			indent++;
		}
		
		public void endElement( String ns, String local, String qname ) throws SAXException {
			
			ErrorInfo ei = null;
			boolean unknownError = false;
			
			try {
				super.endElement(ns,local,qname);
				// there is no error.
			} catch( ValidityViolation vv ) {
				// there was an error.
				ei = vv.getErrorInfo();
				
				if(!(ei instanceof ErrorInfo.BadText)
				&& !(ei instanceof ErrorInfo.BadTagName)
				&& !(ei instanceof ErrorInfo.BadAttribute)
				&& !(ei instanceof ErrorInfo.MissingAttribute) )
					// if the type of information is unknown to us,
					// or ei equals to null
					unknownError = true;
			}

			// print the text
			printText(ei instanceof ErrorInfo.BadText);

			indent--;
			
			printIndent();
			print("</");
			print( qname, ei instanceof ErrorInfo.IncompleteContentModel);
			print(">\n");
		}
		
		public void characters( char[] buf, int start, int len ) throws SAXException {
			super.characters(buf,start,len);
			// just collect characters in this callback
			buffer.append(buf,start,len);
		}
		
		private void printText( boolean fError ) {
			
			String str = buffer.toString().trim();
			
			if(str.length()!=0 || fError) {
				printIndent();
				print(str, fError );
				print("\n");
			}
			
			// update the text buffer
			buffer = new StringBuffer();
		}
		
		
		
		
	//
	// pretty print engine
	// 
		
		/** indent depth. */
		private int indent = 0;
		
		/** Prints whitespaces. */
		private void printIndent() {
			for( int i=0; i<indent; i++ )
				System.out.print("  ");
		}
		
		/** Prints the specified string (possibly with  highlighting.) */
		private void print( String str, boolean highlight ) {
			if(!highlight) {
				System.out.print(str);
			} else {
				System.out.print("***");
				System.out.print(str);
				System.out.print("***");
			}
		}
		
		private void print( String str ) {
			print(str,false);
		}
	}
}
