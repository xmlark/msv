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
