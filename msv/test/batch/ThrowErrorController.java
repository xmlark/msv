/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package batch;

import com.sun.msv.reader.GrammarReaderController;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;

/**
 * GrammarReaderController implementation that throws an Error
 * when an error is found.
 * 
 * useful for debug.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ThrowErrorController implements GrammarReaderController
{
	public InputSource resolveEntity(String s,String r){return null;}
	public void error( Locator[] locs, String msg, Exception nested )
	{
		throw new Error(msg);
	}
	public void warning( Locator[] locs, String msg ) {}
}
