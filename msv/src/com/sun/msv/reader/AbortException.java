/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader;

/**
 * This exception will be thrown when the schema parsing is aborted
 * after all the errors are reported through GrammarReaderController.
 */
public class AbortException extends Exception
{
    private AbortException() {
        super("aborted. Errors should have been reported");
    }
    
    public static final AbortException theInstance = new AbortException();

/*    
    private final Exception nestedException;
    
    public Exception getNestedException() { return nestedException; }
    
    public void printStackTrace( java.io.PrintWriter out ) {
        super.printStackTrace(out);
        if(nestedException!=null) {
            out.println("nested exception:");
            nestedException.printStackTrace(out);
        }
    }
*/
}
