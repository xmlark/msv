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

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.msv.verifier.ValidationUnrecoverableException;

/**
 * {@link ErrorHandler} that reports all errors and warnings.
 * 
 * SAX parse errors are also handled.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ReportErrorHandler implements ErrorHandler {
    
    private int counter = 0;
    public boolean hadError = false;
    
    public void error( SAXParseException e ) throws SAXException {
        hadError = true;
        countCheck(e);
        printSAXParseException( e, MSG_ERROR );
    }
    
    public void fatalError( SAXParseException e ) throws SAXException {
        hadError = true;
        printSAXParseException( e, MSG_FATAL );
        throw new ValidationUnrecoverableException(e);
    }
    
    public void warning( SAXParseException e ) {
        printSAXParseException( e, MSG_WARNING );
    }
    
    protected static void printSAXParseException( SAXParseException spe, String prop ) {
        System.out.println(
            Driver.localize( prop, new Object[]{
                new Integer(spe.getLineNumber()), 
                new Integer(spe.getColumnNumber()),
                spe.getSystemId(),
                spe.getLocalizedMessage()} ) );
    }
    
    
    private void countCheck( SAXParseException e )
        throws ValidationUnrecoverableException    {
        if( counter++ < 20 )    return;
        
        System.out.println( Driver.localize(MSG_TOO_MANY_ERRORS) );
        throw new ValidationUnrecoverableException(e);
    }
    
    public static final String MSG_TOO_MANY_ERRORS = //arg:1
        "ReportErrorHandler.TooManyErrors";
    public static final String MSG_ERROR = // arg:4
        "ReportErrorHandler.Error";
    public static final String MSG_WARNING = // arg:4
        "ReportErrorHandler.Warning";
    public static final String MSG_FATAL = // arg:4
        "ReportErrorHandler.Fatal";
}
