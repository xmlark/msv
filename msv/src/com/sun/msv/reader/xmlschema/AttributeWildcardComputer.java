package com.sun.msv.reader.xmlschema;

import com.sun.msv.grammar.*;
import com.sun.msv.grammar.xmlschema.*;
import com.sun.msv.grammar.util.ExpressionWalker;
import java.util.Set;
import java.util.HashSet;

public class AttributeWildcardComputer extends ExpressionWalker {
	
	public AttributeWildcardComputer( XMLSchemaReader _reader ) {
		this.reader = _reader;
	}
	
	private final XMLSchemaReader reader;
	
	/**
	 * Visited ElementExps and ReferenceExps to prevent infinite recursion.
	 */
	private final Set visitedExps = new HashSet();
	
	/**
	 * Used to collect AttributeWildcards of children.
	 */
	private Set wildcards = null;
	
	public void onElement( ElementExp exp ) {
		if( !visitedExps.add(exp) )
			return;		// this element has already been processed
		super.onElement(exp);
	}
	
	public void onRef( ReferenceExp exp ) {
		if( visitedExps.add(exp) ) {
			if( exp instanceof AttributeGroupExp ) {
				AttributeGroupExp aexp = (AttributeGroupExp)exp;
				
				final Set o = wildcards;
				{
					// process children and collect their wildcards.
					wildcards = new HashSet();
					exp.exp.visit(this);
					// compute the attribute wildcard
					aexp.wildcard = calcCompleteWildcard( aexp.wildcard, wildcards );
				}
				wildcards = o;
			}
			else
			if( exp instanceof ComplexTypeExp ) {
				ComplexTypeExp cexp = (ComplexTypeExp)exp;
				
				final Set o = wildcards;
				{
					// process children and collect their wildcards.
					wildcards = new HashSet();
					exp.exp.visit(this);
					// compute the attribute wildcard
					cexp.wildcard = calcCompleteWildcard( cexp.wildcard, wildcards );
					
//					if(cexp.wildcard==null)
//						System.out.println("complete wildcard is: none");
//					else
//						System.out.println("complete wildcard is: "+cexp.wildcard.getName());
					
					// if the base type is a complex type and the extension is chosen,
					// then we need one last step. Sigh.
					
					if(cexp.complexBaseType!=null) {
//						System.out.println("check the base type");
						
						// process the base type first.
						cexp.complexBaseType.visit(this);
						if(cexp.derivationMethod==cexp.EXTENSION)
							cexp.wildcard = calcComplexTypeWildcard(
								cexp.wildcard,
								cexp.complexBaseType.wildcard );
					}
					
					// create the expression for this complex type.
					if( cexp.wildcard!=null )
						cexp.attWildcard.exp = cexp.wildcard.createExpression(reader.grammar);
				}
				wildcards = o;
			} else
				// otherwise process it normally.
				super.onRef(exp);
		}
		
		if( wildcards!=null ) {
			// add the complete att wildcard of this component.
			if( exp instanceof AttWildcardExp ) {
				AttributeWildcard w = ((AttWildcardExp)exp).getAttributeWildcard();
				if(w!=null)	wildcards.add(w);
			}
		}
	}
	
	/**
	 * Computes the "complete attribute wildcard"
	 */
	private AttributeWildcard calcCompleteWildcard( AttributeWildcard local, Set s ) {
		final AttributeWildcard[] children =
			(AttributeWildcard[])s.toArray(new AttributeWildcard[s.size()]);
		
		// 1st step is to compute the complete wildcard.
		if( children.length==0 )
			return local;
		
		// assert(children.length>0)
			
		// compute the intersection of wildcard.
		NameClass target = children[0].getName();
		for( int i=1; i<children.length; i++ )
			target = NameClass.intersection(target,children[i].getName());
			
		if( local!=null )
			return new AttributeWildcard(
				NameClass.intersection(local.getName(),target),
				local.getProcessMode() );
		else
			return new AttributeWildcard(
				target, children[0].getProcessMode() );
	}

	private AttributeWildcard calcComplexTypeWildcard(
		AttributeWildcard complete, AttributeWildcard base ) {

		if(base!=null) {
			if(complete==null)
				return base;
			else
				return new AttributeWildcard(
					NameClass.union( complete.getName(), base.getName() ),
					complete.getProcessMode() );
		} else {
			// the spec does not have a description for this case.
			// this is my guess.
			return complete;
		}
	}
}
