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
	
	/** gets appropriate ReferenceContainer to store this declaration. */
	protected abstract ReferenceContainer getContainer();
	
	protected void startSelf() {
		super.startSelf();
		
		if( isRedefine() ) {
			final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
			
			String name = startTag.getAttribute("name");
			if( name==null )
				// ignore this error just for now.
				// this error will be reported in annealExpression method.
				return;
			
			oldDecl = (RedefinableExp)getContainer()._get(name);
			if(oldDecl==null) {
				reader.reportError( reader.ERR_REDEFINE_UNDEFINED, name );
				return;
			}
			
			getContainer().redefine( name, oldDecl.getClone() );
		}
	}
	
	protected void endSelf() {
		if( oldDecl!=null )
			getContainer().redefine( oldDecl.name, oldDecl );
		
		super.endSelf();
	}
}
