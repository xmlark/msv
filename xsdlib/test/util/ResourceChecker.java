/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package util;

import java.lang.reflect.*;
import java.util.Locale;

/**
 * checks the existance of message resource.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ResourceChecker {
	
	/**
	 * checks the existance of message resource.
	 * 
	 * this method utilizes Java reflection to read values of 
	 * "public static final String ERR_*****.
	 * 
	 * @param prefix
	 *		Fields whose name does not start with this prefix are not tested.
	 *		Can be "".
	 */
	public static void check( Class cls, String prefix, Checker checker ) throws Exception {
		Field[] fields = cls.getDeclaredFields();
		
		for( int i=0; i<fields.length; i++ ) {
			int mod = fields[i].getModifiers();
			if( Modifier.isStatic(mod)
			&&	Modifier.isPublic(mod)
			&&	Modifier.isFinal(mod)
			&&  fields[i].getType() == String.class
			&&  fields[i].getName().startsWith(prefix) ){
				
				// test English resource
				Locale.setDefault( Locale.ENGLISH );
				checker.check( (String)fields[i].get(null) );
				
				// also test Japanese resource
				Locale.setDefault( Locale.JAPANESE );
				checker.check( (String)fields[i].get(null) );
			}
		}
	}
}
