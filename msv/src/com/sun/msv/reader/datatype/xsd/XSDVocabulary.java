/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.datatype.xsd;

import com.sun.tranquilo.grammar.IDType;
import com.sun.tranquilo.grammar.IDREFType;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.reader.datatype.DataTypeVocabulary;
import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.datatype.BadTypeException;
import com.sun.tranquilo.datatype.DataTypeFactory;
import com.sun.tranquilo.util.StartTagInfo;
import org.xml.sax.ContentHandler;
import java.util.Map;

/**
 * XSD implementation of {@link DataTypeVocabulary}.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class XSDVocabulary implements DataTypeVocabulary {
	
	/** namespace URI of XML Schema */
	public static final String XMLSchemaNamespace = "http://www.w3.org/2001/XMLSchema-datatypes";
	public static final String XMLSchemaNamespace2= "http://www.w3.org/2001/XMLSchema";
	
	public State createTopLevelReaderState( StartTagInfo tag ) {
		if( tag.localName.equals("simpleType") )	return new SimpleTypeState();
		else	return null;
	}
	
	public DataType getType( String localTypeName ) {
		DataType dt = (DataType)userDefinedTypes.get(localTypeName);
		if(dt==null)	dt = DataTypeFactory.getTypeByName(localTypeName);
		return dt;
	}

	/** user-defined named types */
	private final Map userDefinedTypes = new java.util.HashMap();
	
	public void addType( DataType type ) {
		userDefinedTypes.put( type.getName(), type );
	}
	
	public XSDVocabulary() {
		// ID and IDREF are implemented in a different package.
		try {
			addType( IDType.theInstance );
			addType( IDREFType.theInstance );
			addType( DataTypeFactory.deriveByList("IDREFS",IDREFType.theInstance ) );
		} catch( BadTypeException bte ) {
			throw new Error();	// this is not possible
		}
	}
}
