/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.datatype;

import com.sun.tranquilo.datatype.DataType;

/**
 * State can implement this method to be notified by DataType vocabulary
 * about the result of parsing.
 */
public interface TypeOwner
{
	void onEndChild( DataType child );
}
