package com.sun.tahiti.reader.annotator;

import com.sun.msv.grammar.*;
import com.sun.msv.datatype.DatabindableDatatype;
import com.sun.msv.util.StringPair;
import com.sun.tahiti.grammar.PrimitiveItem;
import java.util.Set;
import java.util.Map;


/**
 * add PrimitiveItems to an AGM.
 * 
 * <ul>
 *  <li>
 *   replace Expression.anyString by &lt;data type="string"/>
 *  </li>
 *  <li>
 *   wrap TypedStringExp by PrimitiveItem.
 *  </li>
 * </ul>
 */
class PrimitiveTypeAnnotator extends ExpressionCloner {
	
	PrimitiveTypeAnnotator( ExpressionPool pool ) {
		super(pool);
	}
	
	/**
	 * processed Expressions. used to prevent infinite recursion.
	 */
	private final Set visitedExps = new java.util.HashSet();
	
	/**
	 * a map from TypedStringExp to the PrimitiveItem which wraps it.
	 * used to unify PrimitiveItems.
	 */
	private final Map primitiveItems = new java.util.HashMap();
	
	public Expression onRef( ReferenceExp exp ) {
		if( visitedExps.add(exp) )
			exp.exp = exp.exp.visit(this);
		return exp;
	}
	
	public Expression onOther( OtherExp exp ) {
		if( visitedExps.add(exp) )
			exp.exp = exp.exp.visit(this);
		return exp;
	}
	
	public Expression onElement( ElementExp exp ) {
		if( visitedExps.add(exp) )
			exp.contentModel = exp.contentModel.visit(this);
		return exp;
	}
	
	public Expression onAttribute( AttributeExp exp ) {
		if( visitedExps.contains(exp) )	return exp;
		
		Expression e = pool.createAttribute( exp.nameClass, exp.exp.visit(this) );
		visitedExps.add(e);
		return e;
	}
	
	public Expression onAnyString() {
		return new PrimitiveItem( String.class,
			pool.createTypedString(
				com.sun.msv.datatype.xsd.StringType.theInstance,
				new StringPair("","string") ) );
	}
	
	public Expression onTypedString( TypedStringExp exp ) {
			
		if( primitiveItems.containsKey(exp) )
			// if this exp is already wrapped, use it instead of creating another one.
			// this will reduce the size of the LL grammar for data-binding.
			return (Expression)primitiveItems.get(exp);
		else {
			// if this is the first time, wrap it and memorize it.
			PrimitiveItem p = new PrimitiveItem(getJavaType((TypedStringExp)exp),exp);
			primitiveItems.put( exp, p );
			return p;
		}
	}

	/** gets Type of Java object from TypedStringExp. */
	protected Class getJavaType( TypedStringExp texp ) {
		if( texp.dt instanceof DatabindableDatatype ) {
			return ((DatabindableDatatype)texp.dt).getJavaObjectType();
		}
		
		// if any other attempt fails, use String.
		return String.class;
	}
}
