package com.sun.tranquilo.grammar.xmlschema;

import com.sun.tranquilo.grammar.Grammar;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ExpressionPool;
import com.sun.tranquilo.grammar.trex.TREXPatternPool;
import com.sun.tranquilo.grammar.ReferenceContainer;
import com.sun.tranquilo.grammar.ReferenceExp;

public class XMLSchemaSchema {

	public static final String XMLSchemaInstanceNamespace =
		"http://www.w3.org/2001/XMLSchema-instance";

	public XMLSchemaSchema( String targetNamespace, XMLSchemaGrammar parent ) {
		this.pool = parent.pool;
		this.targetNamespace = targetNamespace;
	}
	
	/** target namespace URI of this schema. */
	public final String targetNamespace;
	
	/** pool object which was used to construct this grammar. */
	protected final TREXPatternPool pool;
	public final ExpressionPool getPool() {
		return pool;
	}
	
	/** choice of all global element declarations. */
	public Expression topLevel;
	
	
	final public class SimpleTypeContainer extends ReferenceContainer {
		public SimpleTypeExp getOrCreate( String name ) {
			return (SimpleTypeExp)super._getOrCreate(name); }

		public SimpleTypeExp get( String name )
		{ return (SimpleTypeExp)super._get(name); }

		protected ReferenceExp createReference( String name )
		{ return new SimpleTypeExp(name); }
	}
	/** map from simple type name to SimpleTypeExp object */
	public final SimpleTypeContainer simpleTypes = new SimpleTypeContainer();
	
	
	final public class ComplexTypeContainer extends ReferenceContainer {
		public ComplexTypeExp getOrCreate( String name ) {
			return (ComplexTypeExp)super._getOrCreate(name); }

		public ComplexTypeExp get( String name )
		{ return (ComplexTypeExp)super._get(name); }

		protected ReferenceExp createReference( String name )
		{ return new ComplexTypeExp(XMLSchemaSchema.this,name); }
	}
	/** map from simple type name to SimpleTypeExp object */
	public final ComplexTypeContainer complexTypes = new ComplexTypeContainer();

	
	final public class AttributeGroupContainer extends ReferenceContainer {
		public AttributeGroupExp getOrCreate( String name ) {
			return (AttributeGroupExp)super._getOrCreate(name); }

		public AttributeGroupExp get( String name )
		{ return (AttributeGroupExp)super._get(name); }

		protected ReferenceExp createReference( String name )
		{ return new AttributeGroupExp(name); }
	}
	/** map from attribute group name to AttributeGroupExp object */
	public final AttributeGroupContainer attributeGroups = new AttributeGroupContainer();
	
	
	final public class AttributeDeclContainer extends ReferenceContainer {
		public AttributeDeclExp getOrCreate( String name ) {
			return (AttributeDeclExp)super._getOrCreate(name); }

		public AttributeDeclExp get( String name )
		{ return (AttributeDeclExp)super._get(name); }

		protected ReferenceExp createReference( String name )
		{ return new AttributeDeclExp(name); }
	}
	/** map from attribute declaration name to AttributeDeclExp object */
	public final AttributeDeclContainer attributeDecls = new AttributeDeclContainer();
	
	
	final public class ElementDeclContainer extends ReferenceContainer {
		public ElementDeclExp getOrCreate( String name ) {
			return (ElementDeclExp)super._getOrCreate(name); }

		public ElementDeclExp get( String name )
		{ return (ElementDeclExp)super._get(name); }

		protected ReferenceExp createReference( String name )
		{ return new ElementDeclExp(name); }
	}
	/** map from attribute declaration name to AttributeDeclExp object */
	public final ElementDeclContainer elementDecls = new ElementDeclContainer();
	
	
	final public class GroupDeclContainer extends ReferenceContainer {
		public GroupDeclExp getOrCreate( String name ) {
			return (GroupDeclExp)super._getOrCreate(name); }

		public GroupDeclExp get( String name )
		{ return (GroupDeclExp)super._get(name); }

		protected ReferenceExp createReference( String name )
		{ return new GroupDeclExp(name); }
	}
	/** map from attribute declaration name to AttributeDeclExp object */
	public final GroupDeclContainer groupDecls = new GroupDeclContainer();
	
}
