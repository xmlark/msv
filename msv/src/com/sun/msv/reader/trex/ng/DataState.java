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
import com.sun.msv.grammar.TypedStringExp;
import com.sun.msv.reader.State;
import com.sun.msv.reader.ExpressionState;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringPair;
import org.relaxng.datatype.*;

/**
 * parses &lt;data&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DataState extends ExpressionState implements ExpressionOwner {
	
	protected State createChildState( StartTagInfo tag ) {
		final RELAXNGReader reader = (RELAXNGReader)this.reader;
		
		if( tag.localName.equals("except") )
			return reader.getStateFactory().dataExcept(this,tag);
		if( tag.localName.equals("param") )
			return reader.getStateFactory().dataParam(this,tag);
		
		return null;
	}
	
	/** type incubator object to be used to create a type. */
	protected DatatypeBuilder typeBuilder;
	
	/** the name of the base type. */
	protected StringPair baseTypeName;
	
	protected void startSelf() {
		final RELAXNGReader reader = (RELAXNGReader)this.reader;
		super.startSelf();

		if( startTag.containsAttribute("key") ) {
			// since this attribute was allowed in early RELAX NG,
			// it is useful to explicitly raise this as an error.
			reader.reportError( reader.ERR_DISALLOWED_ATTRIBUTE, startTag.qName, "key" );
		}
		
		if( startTag.containsAttribute("keyref") ) {
			// since this attribute was allowed in early RELAX NG,
			// it is useful to explicitly raise this as an error.
			reader.reportError( reader.ERR_DISALLOWED_ATTRIBUTE, startTag.qName, "keyref" );
		}
		
		final String localName = startTag.getCollapsedAttribute("type");
		if( localName==null ) {
			reader.reportError( reader.ERR_MISSING_ATTRIBUTE, "data", "type" );
		} else {
			// create a type incubator
			if( reader.datatypeLib!=null ) {// an error is already reported if lib==null.
				baseTypeName = new StringPair( reader.datatypeLibURI, localName );
				 try {
					typeBuilder = reader.datatypeLib.createDatatypeBuilder(localName);
				 } catch( DatatypeException dte ) {
					 reader.reportError( reader.ERR_UNDEFINED_DATATYPE_1, localName, dte.getMessage() );
				 }
			}
		}
		
		if( typeBuilder==null ) {
			// if an error is encountered, then typeIncubator field is left null.
			// In that case, set a dummy implementation so that the successive param
			// statements are happy.
			typeBuilder = new DatatypeBuilder(){
				public Datatype createDatatype() {
					return com.sun.msv.datatype.xsd.StringType.theInstance;
				}
				public void addParameter( String name, String value, ValidationContext context ) {
					// do nothing.
					// thereby accepts anything as a valid facet.
				}
			};
		}
	}
	
	/** the 'except' clause. Null if nothing was specified */
	protected Expression except = null;
	
	public void onEndChild( Expression child ) {
		final RELAXNGReader reader = (RELAXNGReader)this.reader;
		
		// this method receives the 'except' clause, if any.
		if( except!=null )
			reader.reportError( reader.ERR_MULTIPLE_EXCEPT );
		
		except = child;
	}
	
	protected Expression makeExpression() {
		final RELAXNGReader reader = (RELAXNGReader)this.reader;
		
		try {
			if( except==null )	except=Expression.nullSet;
			
			return reader.pool.createTypedString(
				typeBuilder.createDatatype(), baseTypeName, except );
				
		} catch( DatatypeException dte ) {
			reader.reportError( reader.ERR_INVALID_PARAMETERS, dte.getMessage() );
			// recover by returning something.
			return Expression.nullSet;
		}
	}
}
