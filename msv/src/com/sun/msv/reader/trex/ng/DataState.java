/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.ng;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.relaxng.NGTypedStringExp;
import com.sun.msv.reader.State;
import com.sun.msv.reader.ExpressionState;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringPair;
import org.relaxng.datatype.*;

/**
 * parses &lt;data&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DataState extends ExpressionState {
	
	protected State createChildState( StartTagInfo tag ) {
		final RELAXNGReader reader = (RELAXNGReader)this.reader;
		
		if( tag.localName.equals("param") )
			return reader.getStateFactory().dataParam(this,tag);
		
		return null;
	}
	
	/** type incubator object to be used to create a type. */
	protected DataTypeBuilder typeBuilder;
	
	/** the name of the base type. */
	protected StringPair baseTypeName;
	
	protected void startSelf() {
		final RELAXNGReader reader = (RELAXNGReader)this.reader;
		super.startSelf();
		
		final String localName = startTag.getAttribute("type");
		if( localName==null ) {
			reader.reportError( reader.ERR_MISSING_ATTRIBUTE, "data", "type" );
		} else {
			// create a type incubator
			if( reader.datatypeLib!=null ) {// an error is already reported if lib==null.
				baseTypeName = new StringPair( reader.datatypeLibURI, localName );
				 try {
					typeBuilder = reader.datatypeLib.createDataTypeBuilder(localName);
					if( typeBuilder==null )
						 reader.reportError( reader.ERR_UNDEFINED_DATATYPE, localName );
				 } catch( DataTypeException dte ) {
					 reader.reportError( reader.ERR_UNDEFINED_DATATYPE_1, localName, dte.getMessage() );
				 }
			}
		}
		
		if( typeBuilder==null ) {
			// if an error is encountered, then typeIncubator field is left null.
			// In that case, set a dummy implementation so that the successive param
			// statements are happy.
			typeBuilder = new DataTypeBuilder(){
				public DataType derive() {
					return com.sun.msv.datatype.StringType.theInstance;
				}
				public void add( String name, String value, ValidationContext context ) {
					// do nothing.
					// thereby accepts anything as a valid facet.
				}
			};
		}
	}
	
	protected Expression makeExpression() {
		final RELAXNGReader reader = (RELAXNGReader)this.reader;
		
		try {
			String key = startTag.getAttribute("key");
			String keyref = startTag.getAttribute("keyref");
			if( key==null && keyref==null )
				// avoid NGTypedStringExp if possible.
				// it's good for other applications,
				// and TypedStringExps are sharable.
				return reader.pool.createTypedString( typeBuilder.derive() );
			else {
				NGTypedStringExp exp = new NGTypedStringExp(
					typeBuilder.derive(), key, keyref, baseTypeName );
				reader.keyKeyrefs.add(exp);
				reader.setDeclaredLocationOf(exp);	// memorize the location.
				return exp;
			}
		} catch( DataTypeException dte ) {
			reader.reportError( reader.ERR_INVALID_PARAMETERS, dte.getMessage() );
			// recover by returning something.
			return Expression.nullSet;
		}
	}
}
