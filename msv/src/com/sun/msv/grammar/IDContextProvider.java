/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.grammar;

import com.sun.tranquilo.datatype.ValidationContextProvider;

/**
 * ValidationContextProvider that supports limited ID/IDREF implementation.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface IDContextProvider extends ValidationContextProvider
{
	/**
	 * this method is called when another ID is found to
	 * check whether this ID is already used or not.
	 * 
	 * It is the callee's responsibility that stores
	 * ID and checks doubly defined ID.
	 * 
	 * @return
	 *	true
	 *		if there is no preceding ID of the same name;
	 *	false
	 *		if this name is already declared as ID.
	 */
	boolean onID( String newIDToken );
	
	/**
	 * this method is called when an IDREF is found.
	 * 
	 * It is the callee's responsibility to store it
	 * and checks the existance of corresponding IDs later.
	 * 
	 * Note that due to the forward reference, it is not
	 * possible to perform this check when IDREF is found.
	 * It must be done separately after parsing the entire document.
	 */
	void onIDREF( String idrefToken );
}
