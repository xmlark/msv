/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package org.relaxng.testharness.validator;

import org.relaxng.testharness.model.XMLDocument;
import org.relaxng.testharness.model.RNGHeader;

/**
 * A validator has to implement this interface to be tested with this harness.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface IValidator
{
	/**
	 * compiles the specified RELAX NG pattern.
	 * 
	 * @param	header
	 *		header information of the test case.
	 * 
	 * @return
	 * If the validator accepts the pattern, it should return a compiled
	 * schema object. This object will be then used to validate XML instances.
	 * If the validator rejects the pattern, it should return null.
	 * 
	 * @exception
	 *		Any exception is considered as an conformance violation.
	 */
	ISchema parseSchema( XMLDocument pattern, RNGHeader header ) throws Exception;
	
	/**
	 * validates the specified instance with the schema.
	 * 
	 * @return
	 * If the validator judges that the document is valid, return true.
	 * If the validator judges otherwise, return false.
	 * 
	 * @exception
	 *		Any exception is considered as an conformance violation.
	 */
	boolean validate( ISchema schema, XMLDocument instance ) throws Exception;
}
