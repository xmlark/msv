package com.sun.tranquilo.reader.xmlschema;

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
	protected void onTargetNamespaceResolved( String targetNs ) {}
	
	protected void endSelf() {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		reader.elementFormDefault = previousElementFormDefault;
		reader.attributeFormDefault = previousAttributeFormDefault;

		super.endSelf();
	}
}
