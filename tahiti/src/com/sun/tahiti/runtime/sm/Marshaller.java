/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.runtime.sm;

import com.sun.msv.datatype.DatabindableDatatype;

/**
 * interface of marshaller.
 * 
 * A marshaller should implement this interface and perform actual
 * marshalling.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface Marshaller {
	void startElement( String namespaceURI, String localName );
	void endElement( String namespaceURI, String localName );

	void startAttribute( String namespaceURI, String localName );
	void endAttribute( String namespaceURI, String localName );
	
	void data( Object data, DatabindableDatatype type );
}
