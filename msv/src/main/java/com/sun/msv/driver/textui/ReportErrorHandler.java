/*
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
                Integer.valueOf(spe.getLineNumber()),
                Integer.valueOf(spe.getColumnNumber()),
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
