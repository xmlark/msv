package com.sun.tranquilo.grammar;

import com.sun.tranquilo.datatype.ValidationContextProvider;

/**
 * provides support for limited ID/IDREF.
 */
public interface IDContextProvider extends ValidationContextProvider
{
	/**
	 * this method is called when another ID is found to
	 * check whether this ID is already used or not.
	 * 
	 * It is the callee's responsibility that stores
	 * ID and checks doubly defined ID.
	 * 
	 * @return
	 *	true
	 *		if there is no preceding ID of the same name;
	 *	false
	 *		if this name is already declared as ID.
	 */
	boolean onID( String newIDToken );
	
	/**
	 * this method is called when an IDREF is found.
	 * 
	 * It is the callee's responsibility to store it
	 * and checks the existance of corresponding IDs later.
	 * 
	 * Note that due to the forward reference, it is not
	 * possible to perform this check when IDREF is found.
	 * It must be done separately after parsing the entire document.
	 */
	void onIDREF( String idrefToken );
}
