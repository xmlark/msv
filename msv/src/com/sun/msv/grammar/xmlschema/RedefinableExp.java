package com.sun.tranquilo.grammar.xmlschema;

import com.sun.tranquilo.grammar.ReferenceExp;

public abstract class RedefinableExp extends ReferenceExp {
	
	public RedefinableExp( String typeLocalName ) {
		super(typeLocalName);
	}
	
	/** clones this object. */
	public abstract RedefinableExp getClone();
	
	/** assigns contents of rhs to this object.
	 * 
	 * rhs and this object must be the same runtime type, and
	 * they must have the same name.
	 * this method redefines this object by the given component.
	 * 
	 * derived class should override this method should it necessary.
	 */
	public void redefine( RedefinableExp rhs ) {
		if( this.getClass()!=rhs.getClass()
		|| !this.name.equals(rhs.name) )
			// two must be the same class.
			throw new IllegalArgumentException();
		
		this.exp = rhs.exp;
	}
}
