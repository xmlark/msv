package com.sun.tranquilo.verifier;

/**
 * Represents a kind of "constraint" over XML document.
 * 
 * Usually, this is what people call schema.
 * Conceptually, this object is a collection of ElementDeclaration.
 */
public interface DocumentDeclaration
{
	/** creates a new Acceptor that will verify the document element.
	 * 
	 * 
	 * In RELAX, this concept is equivalent to &lt;topLevel&gt;
	 * In TREX, this concept is equivalent to &lt;start&gt;
	 * 
	 * @return
	 *		The implementation cannot return null.
	 *		Apparently, it is impossible to fail in this early stage.
	 */
	Acceptor createAcceptor();
}
