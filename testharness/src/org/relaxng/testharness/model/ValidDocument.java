/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package org.relaxng.testharness.model;

import java.util.Vector;
import java.util.Iterator;

/**
 * A valid document and its soundness information.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ValidDocument
{
	/** The body of the document. */
	public XMLDocument document;

	/** Soundness of this document with the ID/IDREF feature. */
	public boolean isIdIdrefSound = true;
	
	public ValidDocument( XMLDocument _document ) {
		this.document = _document;
	}
}
