package com.sun.tranquilo.grammar;

/**
 * ExpressionVisitor that returns Expression object
 */
public interface ExpressionVisitorExpression
{
	Expression onAttribute( AttributeExp exp );
	Expression onChoice( ChoiceExp exp );
	Expression onElement( ElementExp exp );
	Expression onOneOrMore( OneOrMoreExp exp );
	Expression onMixed( MixedExp exp );
	Expression onRef( ReferenceExp exp );
	Expression onEpsilon();
	Expression onNullSet();
	Expression onAnyString();
	Expression onSequence( SequenceExp exp );
	Expression onTypedString( TypedStringExp exp );
}
