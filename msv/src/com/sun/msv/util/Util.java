/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.util;

import org.xml.sax.InputSource;
import java.io.File;
import java.net.URL;

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
			// try it as a file
			String path = new File(fileOrURL).getAbsolutePath();
			if (File.separatorChar != '/')
				path = path.replace(File.separatorChar, '/');
			if (!path.startsWith("/"))
				path = "/" + path;
//			if (!path.endsWith("/") && isDirectory())
//				path = path + "/";
			return new InputSource( new URL("file", "", path).toExternalForm() );
		} catch( Exception e ) {
			// try it as an URL
			return new InputSource(fileOrURL);
		}
	}
}
