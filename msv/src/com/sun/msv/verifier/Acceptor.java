package com.sun.tranquilo.verifier;

import java.util.Collection;
import java.util.List;
import com.sun.tranquilo.datatype.ValidationContextProvider;
import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.util.StringRef;

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
	 * 
	 * @return false
	 *		if the literal at this position is not allowed.
	 */
	boolean stepForward( String literal, ValidationContextProvider context, StringRef refErr );
	
	/** eat accepted ElementDeclarations. */
//	boolean stepForward( ElementDeclaration[] accepted );
	
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
	
	/**
	 * gets accepted ElementDeclarations by this Acceptor.
	 * these ElementDeclarations can be considered as owners of this Acceptor.
	 */
//	void getSatisfiedElements( Collection resultReceiver );
	
	/** checks if this Acceptor is satisifed */
	boolean isAcceptState();
	
	
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
