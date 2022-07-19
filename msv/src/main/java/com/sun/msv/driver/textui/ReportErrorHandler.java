/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and  use in  source and binary  forms, with  or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions  of  source code  must  retain  the above  copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution  in binary  form must  reproduct the  above copyright
 *   notice, this list of conditions  and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither  the  name   of  Sun  Microsystems,  Inc.  or   the  names  of
 * contributors may be  used to endorse or promote  products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS  OR   IMPLIED  CONDITIONS,  REPRESENTATIONS   AND  WARRANTIES,
 * INCLUDING  ANY  IMPLIED WARRANTY  OF  MERCHANTABILITY,  FITNESS FOR  A
 * PARTICULAR PURPOSE  OR NON-INFRINGEMENT, ARE HEREBY  EXCLUDED. SUN AND
 * ITS  LICENSORS SHALL  NOT BE  LIABLE  FOR ANY  DAMAGES OR  LIABILITIES
 * SUFFERED BY LICENSEE  AS A RESULT OF OR  RELATING TO USE, MODIFICATION
 * OR DISTRIBUTION OF  THE SOFTWARE OR ITS DERIVATIVES.  IN NO EVENT WILL
 * SUN OR ITS  LICENSORS BE LIABLE FOR ANY LOST  REVENUE, PROFIT OR DATA,
 * OR  FOR  DIRECT,   INDIRECT,  SPECIAL,  CONSEQUENTIAL,  INCIDENTAL  OR
 * PUNITIVE  DAMAGES, HOWEVER  CAUSED  AND REGARDLESS  OF  THE THEORY  OF
 * LIABILITY, ARISING  OUT OF  THE USE OF  OR INABILITY TO  USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
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
