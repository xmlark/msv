package com.sun.tranquilo.grammar;

/**
 * Visitor interface for Expression and its derived types.
 * 
 * Note that traversing an expression is still a job for implementator.
 * Expression and derived types do not provide any traversal.
 */
public interface ExpressionVisitor
{
	Object onAttribute( AttributeExp exp );
	Object onChoice( ChoiceExp exp );
	Object onElement( ElementExp exp );
	Object onOneOrMore( OneOrMoreExp exp );
	Object onMixed( MixedExp exp );
	Object onRef( ReferenceExp exp );
	Object onEpsilon();
	Object onNullSet();
	Object onAnyString();
	Object onSequence( SequenceExp exp );
	Object onTypedString( TypedStringExp exp );
}
