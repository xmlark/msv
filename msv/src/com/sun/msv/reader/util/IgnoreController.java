/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.util;

import com.sun.tranquilo.reader.GrammarReaderController;
import org.xml.sax.Locator;
import org.xml.sax.InputSource;

/**
 * Default implementation of GrammarReaderController.
 * 
 * This class ignores every errors and warnings.
 */
public class IgnoreController implements GrammarReaderController
{
	public void warning( Locator[] locs, String errorMessage ) {}
	public void error( Locator[] locs, String errorMessage, Exception nestedException ) {}
	public InputSource resolveInclude( String url ) { return null; }
}
