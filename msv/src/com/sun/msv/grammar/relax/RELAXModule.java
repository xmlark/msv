/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.grammar.relax;

import com.sun.tranquilo.grammar.ReferenceExp;
import com.sun.tranquilo.grammar.ReferenceContainer;
import com.sun.tranquilo.reader.datatype.xsd.XSDVocabulary;

/**
 * "Module" of RELAX Core.
 */
public class RELAXModule
{
	final public class ElementRulesContainer extends ReferenceContainer
	{
		public ElementRules getOrCreate( String name )
		{ return (ElementRules)super._getOrCreate(name); }

		public ElementRules get( String name )
		{ return (ElementRules)super._get(name); }

		protected ReferenceExp createReference( String name )
		{ return new ElementRules(name,RELAXModule.this); }
	}
	/** map from label name to ElementRules object */
	public final ElementRulesContainer elementRules = new ElementRulesContainer();
	
	final public class HedgeRulesContainer extends ReferenceContainer
	{
		public HedgeRules getOrCreate( String name )
		{ return (HedgeRules)super._getOrCreate(name); }
	
		public HedgeRules get( String name )
		{ return (HedgeRules)super._get(name); }

		protected ReferenceExp createReference( String name )
		{ return new HedgeRules(name,RELAXModule.this); }
	}
	/** map from label name to HedgeRules object */
	public final HedgeRulesContainer hedgeRules = new HedgeRulesContainer();

	final public class TagContainer extends ReferenceContainer
	{
		public TagClause getOrCreate( String name )
		{ return (TagClause)super._getOrCreate(name); }
	
		public TagClause get( String name )
		{ return (TagClause)super._get(name); }

		protected ReferenceExp createReference( String name )
		{ return new TagClause(name); }
	}
	/** map from role name to TagClause object */
	public final TagContainer tags = new TagContainer();

	final public class AttPoolContainer extends ReferenceContainer
	{
		public AttPoolClause getOrCreate( String name )
		{ return (AttPoolClause)super._getOrCreate(name); }
	
		public AttPoolClause get( String name )
		{ return (AttPoolClause)super._get(name); }

		protected ReferenceExp createReference( String name )
		{ return new AttPoolClause(name); }
	}
	/** map from role name to AttPoolClause object */
	public final AttPoolContainer attPools = new AttPoolContainer();
	/** map from role name to exported AttPoolClause object */
	public final AttPoolContainer exportedAttPools = new AttPoolContainer();
	
	/*
		exported AttPool objects are treated differently because
		they have difference in validation semantics. Namely,
		when attPool is used from the same module, its attribute declarations
		validate themselves against default namespace(""),
		whereas when used from the external modules, they validate
		themselves against target namespace of the module.
	
		The difficult part is that we have to achieve this semantics even when
		we use the RELAX schema with com.sun.tranquilo.verifier.trex.
	
		To do this, this class has separate difinition for all exported attPools.
	*/
	
	/**
	 * creates an empty ReferenceExp that can then be used
	 * for anyOtherElement.
	 */
	public final ReferenceExp createAnyOtherElementSkelton()
	{
		return new HedgeRules("##anyOtherElement",this);
	}
	
	/** target namespace URI */
	public final String targetNamespace;
	
	/** datatypes */
	public final XSDVocabulary userDefinedTypes = new XSDVocabulary();
	
	
	public RELAXModule( String targetNamespace )
	{
		// if you don't want to namespace, specify ""
		if( targetNamespace==null )		throw new NullPointerException();
		
		this.targetNamespace = targetNamespace;
		userDefinedTypes.addType( EmptyStringType.theInstance );
		userDefinedTypes.addType( NoneType.theInstance );
	}
}
