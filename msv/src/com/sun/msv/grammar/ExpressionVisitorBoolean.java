/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.grammar;

/**
 * ExpressionVisitor that returns boolean
 */
public interface ExpressionVisitorBoolean
{
	boolean onAttribute( AttributeExp exp );
	boolean onChoice( ChoiceExp exp );
	boolean onElement( ElementExp exp );
	boolean onOneOrMore( OneOrMoreExp exp );
	boolean onMixed( MixedExp exp );
	boolean onRef( ReferenceExp exp );
	boolean onEpsilon();
	boolean onNullSet();
	boolean onAnyString();
	boolean onSequence( SequenceExp exp );
	boolean onTypedString( TypedStringExp exp );
}
