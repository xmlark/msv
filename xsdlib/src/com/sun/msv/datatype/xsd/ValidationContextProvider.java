package com.sun.tranquilo.datatype;

/**
 * an interface that must be implemented by caller to
 * provide context information that is necessary to 
 * perform validation of some datatypes.
 */
public interface ValidationContextProvider
{
	/**
	 * resolved namespace prefix to namespace URI.
	 * 
	 * this method is used for validating QName. 
	 *
	 * if the specified prefix is not declared,
	 * the implementation must return null.
	 * 
	 * If prefix is "" (empty string), it indicates
	 * unprefixed value. The implementation
	 * should resolved it as if you see an unprefixed
	 * element, rather than unprefix attribute
	 */
	String resolveNamespacePrefix( String prefix );
	
	/**
	 * checks if the unparsed entity is declared with the
	 * specified name.
	 * this method is used to validate ENTITY type.
	 *
	 * @return
	 *  true
	 *		if DTD has unparsed entity declaration for
	 *		the specified name.
	 *	false
	 *		if otherwise.
	 */
	boolean isUnparsedEntity( String entityName );
}