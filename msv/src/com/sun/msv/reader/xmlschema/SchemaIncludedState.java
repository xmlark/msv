/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.xmlschema;

import java.util.Set;
import com.sun.msv.reader.State;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.util.StartTagInfo;

/**
 * used to parse &lt;schema&gt; element of included schema.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SchemaIncludedState extends GlobalDeclState {
	
	/**
	 * target namespace that the caller expects.
	 * 
	 * If this field is null, that indicates caller doesn't
	 * expect particular target namespace, therefore schema element
	 * must have targetNamespace attribute.
	 * 
	 * If this field is non-null and schema element has different
	 * value as targetNamespace, then error will be signaled.
	 */
	protected String expectedTargetNamespace;
	
	protected SchemaIncludedState( String expectedTargetNamespace ) {
		this.expectedTargetNamespace = expectedTargetNamespace;
	}
	
	private String previousElementFormDefault;
	private String previousAttributeFormDefault;
	
	/**
	 * this flag is set to true to indicate all the contents of this element
	 * will be skipped (due to the double inclusion).
	 */
	private boolean ignoreContents = false;
	
	protected State createChildState( StartTagInfo tag ) {
		if( ignoreContents	)		return new IgnoreState();
		else						return super.createChildState(tag);
	}

	
	protected void startSelf() {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		super.startSelf();
		
		String targetNs = startTag.getAttribute("targetNamespace");
		if( targetNs==null ) {
			if( expectedTargetNamespace==null ) {
				// this is not an error. It just means target namespace is absent.
				// reader.reportError( reader.ERR_MISSING_ATTRIBUTE, "schema", "targetNamespace" );
				targetNs = "";	// recover by assuming "" namespace.
			}
			else
				targetNs = expectedTargetNamespace;
		} else {
			if( expectedTargetNamespace!=null
			&& !expectedTargetNamespace.equals(targetNs) )
				reader.reportError( reader.ERR_INCONSISTENT_TARGETNAMESPACE, targetNs, expectedTargetNamespace );
				// recover by adopting the one specified in the schema.
		}

		onTargetNamespaceResolved(targetNs);

		// check double inclusion.
		Set s = (Set)reader.parsedFiles.get(targetNs);
		if(s==null)
			reader.parsedFiles.put( targetNs, s = new java.util.HashSet() );
		
		if( s.contains(this.location.getSystemId()) )
			// this file is already included. So skip processing it.
			ignoreContents = true;
		else
			s.add(this.location.getSystemId());

		// process other attributes.
		previousElementFormDefault = reader.elementFormDefault;
		previousAttributeFormDefault = reader.attributeFormDefault;
		
		String form;
		form = startTag.getDefaultedAttribute("elementFormDefault","unqualified");
		if( form.equals("qualified") )
			reader.elementFormDefault = targetNs;
		else {
			reader.elementFormDefault = "";
			if( !form.equals("unqualified") )
				reader.reportError( reader.ERR_BAD_ATTRIBUTE_VALUE, "elementFormDefault", form );
		}
		
		form = startTag.getDefaultedAttribute("attributeFormDefault","unqualified");
		if( form.equals("qualified") )
			reader.attributeFormDefault = targetNs;
		else {
			reader.attributeFormDefault = "";
			if( !form.equals("unqualified") )
				reader.reportError( reader.ERR_BAD_ATTRIBUTE_VALUE, "attributeFormDefault", form );
		}
		
		
	}

	/** does something useful with determined target namespace. */
	protected void onTargetNamespaceResolved( String targetNs ) {
	}
	
	protected void endSelf() {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		reader.elementFormDefault = previousElementFormDefault;
		reader.attributeFormDefault = previousAttributeFormDefault;

		super.endSelf();
	}
}
