/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.reader;

/**
 * conveys additional information about the result of the grammar parsing.
 * 
 * <p>
 * The body of this object must be filled by the reader.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ReaderResult {
	
	/**
	 * the grammar file will be generated as this name.
	 * This field would be something like "com.example.abc.FileName".
	 * It shouldn't have a file extension.
	 */
	public String grammarName;
}
