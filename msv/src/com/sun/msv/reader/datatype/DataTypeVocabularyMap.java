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

import java.util.Map;

public class DataTypeVocabularyMap
{
	/** map from namespace URI to DataTypeVocabulary */
	private final Map impl = new java.util.HashMap();
	
	/**
	 * obtains an DataTypeVocabulary associated to the namespace.
	 * 
	 * If necessary, Vocabulary is located and instanciated.
	 */
	public DataTypeVocabulary get( String namespaceURI )
	{
		DataTypeVocabulary v = (DataTypeVocabulary)impl.get(namespaceURI);
		if(v!=null)		return v;
		
		// TODO: generic way to load a vocabulary
		if( namespaceURI.equals( com.sun.tranquilo.reader.datatype.xsd.XSDVocabulary.XMLSchemaNamespace ) )
		{
			v = new com.sun.tranquilo.reader.datatype.xsd.XSDVocabulary();
			impl.put( com.sun.tranquilo.reader.datatype.xsd.XSDVocabulary.XMLSchemaNamespace, v );
			impl.put( com.sun.tranquilo.reader.datatype.xsd.XSDVocabulary.XMLSchemaNamespace2, v );
		}
		
		return v;
	}
	
	/** manually adds DataTypeVocabulary into this map. */
	public void put( String namespaceURI, DataTypeVocabulary voc )
	{
		impl.put( namespaceURI, voc );
	}
}
