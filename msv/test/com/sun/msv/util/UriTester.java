/*
 * @(#)$Id$
 *
 * Copyright 2002 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.xml.utils.URI;

/**
 * Tests the {@link Uri} class.
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class UriTester {
    public static void main(String[] args) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        
        String base = r.readLine();
        String rel = r.readLine();
        
//        System.out.println("Thai Open Source:");
//        System.out.println( Uri.resolve(base,rel) );
        
        System.out.println("Apache:");
        System.out.println( new URI(new URI(base),rel).toString() );
    }
}
