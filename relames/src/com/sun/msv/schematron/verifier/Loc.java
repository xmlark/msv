/*
 * @(#)$Id$
 *
 * Copyright 2002 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.schematron.verifier;
import org.xml.sax.Locator;

/**
 * Poor man's locator object.
 */
class Loc {
	Loc( Locator src ) {
		this.line = src.getLineNumber();
		this.col = src.getColumnNumber();
	}
	final int line;
	final int col;
}