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

import java.io.PrintStream;

import org.xml.sax.EntityResolver;
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
    
    /** entity resolution is delegated to this object. can be null. */
    public EntityResolver externalEntityResolver;
    
    /** messages are sent to this object. */
    protected PrintStream out;
    
    public DebugController( boolean displayWarning ) {
        // for backward compatibility. Can be removed later.
        this( displayWarning, false );
    }
    
    public DebugController( boolean displayWarning, boolean quiet ) {
        this( displayWarning, quiet, System.out );
    }
    public DebugController( boolean displayWarning, boolean quiet, EntityResolver externalEntityResolver ) {
        this( displayWarning, quiet, System.out, externalEntityResolver );
    }
    public DebugController( boolean displayWarning, boolean quiet, PrintStream outDevice ) {
        this( displayWarning, quiet, outDevice, null );
    }
    public DebugController( boolean displayWarning, boolean quiet, PrintStream outDevice, EntityResolver externalEntityResolver ) {
        this.out = outDevice;
        this.displayWarning = displayWarning;
        this.warningReported = quiet;
        this.externalEntityResolver = externalEntityResolver;
    }
    
    public void warning( Locator[] loc, String errorMessage ) {
        if(!displayWarning)    {
            if( !warningReported )
                out.println( Driver.localize(Driver.MSG_WARNING_FOUND) );
            warningReported = true;
            return;
        }
        
        out.println(errorMessage);
        
        if(loc==null || loc.length==0)
            out.println("  location unknown");
        else
            for( int i=0; i<loc.length; i++ )
                printLocation(loc[i]);
    }
    
    public void error( Locator[] loc, String errorMessage, Exception nestedException ) {
        if( nestedException instanceof SAXException ) {
            out.println("SAXException: " + nestedException.getLocalizedMessage() );
            SAXException se = (SAXException)nestedException;
            if(se.getException()!=null) {
                out.println("  nested exception: " + se.getException().getLocalizedMessage() );
                se.getException().printStackTrace(System.out);
            }
        } else {
            out.println(errorMessage);
//            Thread.currentThread().dumpStack();
//            nestedException.printStackTrace();
            if(nestedException!=null)
                System.out.println(nestedException);
        }
        
        if(loc==null || loc.length==0)
            out.println("  location unknown");
        else
            for( int i=0; i<loc.length; i++ )
                printLocation(loc[i]);
    }
    
    private void printLocation( Locator loc ) {
        String col="";
        if(loc.getColumnNumber()>=0)
            col = ":"+loc.getColumnNumber();
        
        out.println( "  "+
            loc.getLineNumber()+
            col+
            "@"+
            loc.getSystemId() );
    }

    public InputSource resolveEntity( String publicId, String systemId ) throws java.io.IOException, SAXException {
        if(externalEntityResolver!=null) {
//            System.out.println("using external resolver");
            return externalEntityResolver.resolveEntity(publicId,systemId);
        }
        return null;
    }
}
