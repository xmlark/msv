/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.verifier;

import java.util.Collection;
import java.util.List;
import com.sun.tranquilo.datatype.ValidationContextProvider;
import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.util.StringRef;
import com.sun.tranquilo.util.DataTypeRef;

/**
 * represents a pseudo-automaton acceptor.
 * 
 * this interface is used to verify content models.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface Acceptor
{
	/**
	 * creates an Acceptor that will eat 
	 * combined content model of all the possible children at this moment.
	 * 
	 * @param refErr
	 *		if this parameter is non-null, the implementation should
	 *		try to detect the reason of error and recover from it.
	 *		and this object should have the error message as its str field.
	 * 
	 * @return null
	 *		If refErr is null, return null if the given start tag is not accepted.
	 *		If refErr is non-null, return null only if the recovery is impossible.
	 */
	Acceptor createChildAcceptor( StartTagInfo sti, StringRef refErr );
	
	/** eats string literal.
	 * 
	 * @param context
	 *		an object that provides context information necessary to validate
	 *		some datatypes.
	 * @param refErr
	 *		if this parameter is non-null, the implementation should
	 *		try to detect the reason of error and recover from it.
	 *		and this object should have the error message as its str field.
	 * @param refType
	 *		if this parameter is non-null and the callee supports
	 *		type-assignment, the callee will assign the DataType object
	 *		to this variable.
	 *		Caller must initialize refType.type to null before calling this method.
	 *		If the callee doesn't support type-assignment or type-assignment
	 *		is impossible for this literal (possibly by ambiguous grammar),
	 *		this variable must kept null.
	 * 
	 * @return false
	 *		if the literal at this position is not allowed.
	 */
	boolean stepForward( String literal, ValidationContextProvider context, StringRef refErr, DataTypeRef refType );
	
	/** eats a child element
	 * 
	 * It is the caller's responsibility to make sure that child acceptor
	 * is in the accept state. It is the callee's responsibility to recover
	 * from error of unsatisified child acceptor.
	 * 
	 * @return false
	 *		if this child element is not allowed.
	 */
	boolean stepForward( Acceptor child, StringRef errRef );
	
	/** checks if this Acceptor is satisifed.
	 * 
	 * Acceptor is said to be satisfied when given sequence of elements/strings
	 * is accepted by the content model.
	 */
	boolean isAcceptState();
	
	/** gets the "type" object for which this acceptor is working.
	 * 
	 * This method is used for type assignment. Actual Java type of
	 * return value depends on the implementation.
	 * 
	 * @return null
	 *		the callee should return null when it doesn't support
	 *		type-assignment feature, or type-assignment is impossible
	 *		for this acceptor (for example by ambiguous grammar).
	 */
	Object getOwnerType();
	
	
	/** gets how this acceptor take care of characters.
	 * 
	 * one of the constant value shown below.
	 * Although this method can be called anytime, it is intended to be called
	 * only once when the acceptor is first created.
	 */
	int getStringCareLevel();

	/**
	 * only whitespaces are allowed.
	 * 
	 * for example, &lt;elementRule&gt; of RELAX doesn't allow
	 * characters (except whitespaces) at all.
	 */
	static final int STRING_PROHIBITED	= 0x00;
	/**
	 * character literals are allowed, but Acceptor doesn't care
	 * its contents and where it is appeared.
	 * 
	 * Verifier doesn't need to call stepForward for literal.
	 * This mode is used for mixed contents.
	 */
	static final int STRING_IGNORE		= 0x01;
	/**
	 * attentive handling of characters is required.
	 * 
	 * Verifier has to keep track of exact contents of string and
	 * it must call stepForward for string accordingly.
	 */
	static final int STRING_STRICT		= 0x02;
	
	// TODO: possible 4th class, STRING_SLOPPY,
	// which requires stepForward invocation but don't care about its content.
}
