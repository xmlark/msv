package com.sun.tranquilo.reader.xmlschema;

import com.sun.tranquilo.grammar.ReferenceContainer;
import com.sun.tranquilo.grammar.xmlschema.RedefinableExp;
import com.sun.tranquilo.reader.ExpressionWithChildState;

/** state that parses declaration.
 * 
 * "Declarations" are attribute, element, complexType, group, and attributeGroup.
 * simpleType is treated differently.
 */
public abstract class RedefinableDeclState extends ExpressionWithChildState {

	protected boolean isGlobal() {
		return parentState instanceof GlobalDeclState;
	}
	protected boolean isRedefine() {
		return parentState instanceof RedefineState;
	}

	/**
	 * keeps a reference to previous declaration.
	 * 
	 * this field is used only when in redefine mode. Derived class should use
	 * this declaration instead of getting one from ReferenceContainer through
	 * XMLSchemaSchema.
	 */
	protected RedefinableExp oldDecl;
	
	private ReferenceContainer container;
	
	protected void startSelf(ReferenceContainer con) {
		super.startSelf();
		
		if( isRedefine() ) {
			final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
			this.container = con;
			
			String name = startTag.getAttribute("name");
			if( name==null )
				// ignore this error just for now.
				// this error will be reported in annealExpression method.
				return;
			
			oldDecl = (XMLSchemaExp)con._get(name);
			if(exp==null) {
				reader.reportError( reader.ERR_REDEFINE_UNDEFINED, name );
				return;
			}
			
			con.redefine( name, oldDecl.getClone() );
		}
	}
	
	protected void endSelf() {
		container.redefine( oldDecl.name, oldDecl );
		
		super.endSelf();
	}
}
