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
 * ExpressionVisitor that returns void.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface ExpressionVisitorVoid
{
	void onAttribute( AttributeExp exp );
	void onChoice( ChoiceExp exp );
	void onElement( ElementExp exp );
	void onOneOrMore( OneOrMoreExp exp );
	void onMixed( MixedExp exp );
	void onRef( ReferenceExp exp );
	void onEpsilon();
	void onNullSet();
	void onAnyString();
	void onSequence( SequenceExp exp );
	void onTypedString( TypedStringExp exp );
}
