package com.sun.tahiti.grammar;

public interface JavaItemVisitor {
	Object onClass( ClassItem item );
	Object onField( FieldItem item );
	Object onIgnore( IgnoreItem item );
	Object onInterface( InterfaceItem item );
	Object onPrimitive( PrimitiveItem item );
	Object onSuper( SuperClassItem item );
}
