package com.sun.tranquilo.grammar.xmlschema;

import com.sun.tranquilo.datatype.BadTypeException;
import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.datatype.TypeIncubator;
import com.sun.tranquilo.datatype.ValidationContextProvider;
import com.sun.tranquilo.datatype.QnameType;
import com.sun.tranquilo.grammar.ReferenceExp;
import com.sun.tranquilo.grammar.ElementExp;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.SimpleNameClass;
import com.sun.tranquilo.grammar.ChoiceNameClass;
import com.sun.tranquilo.grammar.trex.ElementPattern;

/**
 * ComplexType definition.
 * 
 * ComplexTypeExp holds an expression (as a ReferenceExp) that matches to
 * this type itself and all substitutable types.
 * 
 * <code>self</code> contains the expression that exactly matches
 * to the declared content model (without any substitutable types).
 * 
 * <code>selfWType</code> field matches to " @xsi:type<'typeQName'>,self ".
 * This should be referenced from base type.
 * 
 * <code>extensions</code> field matches to selfWType of those types which
 * are derived by extention from this type.
 * 
 * <code>restrictions</code> field contains choices of selfWType of those
 * types which are derived by restriction from this type.
 */
public class ComplexTypeExp extends RedefinableExp {
	
	public ComplexTypeExp( XMLSchemaSchema schema, String localName ) {
		super(localName);
		this.parent = schema;
		this.self = new ReferenceExp( localName + ":self" );
		this.extensions = new ReferenceExp( localName + ":extensions" );
		this.extensions.exp = Expression.nullSet;
		this.restrictions = new ReferenceExp( localName + ":restrictions" );
		this.restrictions.exp = Expression.nullSet;
		
		this.selfWType = schema.pool.createSequence(
							schema.pool.createAttribute(
								new ChoiceNameClass(
									new SimpleNameClass(
										schema.XMLSchemaInstanceNamespace,
										"type"
									),
									new SimpleNameClass(
										schema.XMLSchemaInstanceNamespace_old,
										"type"
									)
								),
								schema.pool.createTypedString(
									getQNameType(schema.targetNamespace,localName)
								)
							),
							self );
		
		// self will be added later if this complex type turns out to be non-abstract.
		this.exp = schema.pool.createChoice( extensions, restrictions );
	}
	
	/** derives a QName type that only accepts this type name. */
	private static DataType getQNameType( final String namespaceURI, final String localName ) {
		try {
			TypeIncubator ti = new TypeIncubator( QnameType.theInstance );
			ti.add( "enumeration", "foo:"+localName, true,
				new ValidationContextProvider() {
					public String resolveNamespacePrefix( String prefix ) {
						if( "foo".equals(prefix) )	return namespaceURI;
						return null;
					}
					public boolean isUnparsedEntity( String entityName ) {
						throw new Error();	// shall never be called.
					}
				} );
		
			return ti.derive(null);
		} catch( BadTypeException e ) {
			// assertion failed. this can't happen.
			throw new Error();
		}
	}

	
	public final ReferenceExp self;
	public final ReferenceExp extensions;
	public final ReferenceExp restrictions;
	
	public final Expression selfWType;
	
	/** parent XMLSchemaSchema object to which this object belongs. */
	public final XMLSchemaSchema parent;
	
		
	/** clone this object. */
	public RedefinableExp getClone() {
		ComplexTypeExp exp = new ComplexTypeExp(parent,super.name);
		exp.redefine(this);
		return exp;
	}

	public void redefine( RedefinableExp _rhs ) {
		super.redefine(_rhs);
		
		ComplexTypeExp rhs = (ComplexTypeExp)_rhs;
		self.exp = rhs.self.exp;
		extensions.exp = rhs.extensions.exp;
		restrictions.exp = rhs.restrictions.exp;
		if( this.parent != rhs.parent )
			// those two must share the parent.
			throw new IllegalArgumentException();
	}

}
