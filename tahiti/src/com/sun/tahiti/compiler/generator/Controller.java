/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.compiler.generator;

import com.sun.tahiti.grammar.TypeItem;
import com.sun.msv.reader.GrammarReaderController;
import java.io.OutputStream;
import java.io.IOException;

/**
 * this interface will be implemented by the caller of the model generator
 * to control the process.
 */
public interface Controller extends GrammarReaderController {
	/**
	 * the contents of the specified {@link TypeItem} will be sent
	 * to the returned DocumentHandler.
	 */
	OutputStream getOutput( TypeItem type ) throws IOException;
}
