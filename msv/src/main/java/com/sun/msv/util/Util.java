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
