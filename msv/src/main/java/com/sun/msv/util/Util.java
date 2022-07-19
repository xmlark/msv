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

package com.sun.msv.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.InputSource;

/**
 * Collection of utility methods.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Util
{
    /**
     * Gets an InputSource from a string, which contains either
     * a file name or an URL.
     */
    public static InputSource getInputSource( String fileOrURL ) {
        try {
            // try it as an URL
            new URL(fileOrURL);
            return new InputSource(fileOrURL);
        } catch( MalformedURLException e ) {
            // try it as a file
            String path = new File(fileOrURL).getAbsolutePath();
            if (File.separatorChar != '/')
                path = path.replace(File.separatorChar, '/');
            if (!path.startsWith("/"))
                path = "/" + path;
//            if (!path.endsWith("/") && isDirectory())
//                path = path + "/";
            return new InputSource("file://"+path);
        }
    }
    
    /**
     * Checks if a given string is an absolute URI if it is an URI.
     * 
     * <p>
     * This method does not check whether it is an URI.
     * 
     * <p>
     * This implementation is based on
     * <a href="http://lists.oasis-open.org/archives/relax-ng/200107/msg00211.html">
     * this post.</a>
     */
    public static boolean isAbsoluteURI( String uri ) {
        
        int len = uri.length();
        if(len==0)    return true;    // an empty string is OK.
        if(len<2)    return false;
        
        char ch = uri.charAt(0);
        if(('a'<=ch && ch<='z') || ('A'<=ch && ch<='Z')) {
            
            for( int i=1; i<len; i++ ) {
                ch = uri.charAt(i);
                
                if(ch==':')        return true;
                if(('a'<=ch && ch<='z') || ('A'<=ch && ch<='Z'))    continue;
                if(ch=='-' || ch=='+' || ch=='.')    continue;
                
                return false;    // invalid character
            }
        }
        
        return false;
    }


    public static String which( Class clazz ) {
        return which( clazz.getName(), clazz.getClassLoader() );
    }

    /**
     * Search the specified classloader for the given classname.
     *
     * @param classname the fully qualified name of the class to search for
     * @param loader the classloader to search
     * @return the source location of the resource, or null if it wasn't found
     */
    public static String which(String classname, ClassLoader loader) {

        String classnameAsResource = classname.replace('.', '/') + ".class";
        
        if( loader==null )  loader = ClassLoader.getSystemClassLoader();
        
        URL it = loader.getResource(classnameAsResource);
        if (it != null) {
            return it.toString();
        } else {
            return null;
        }
    }
}
