/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package batch.model;

import java.io.File;

/**
 * A validator has to implement this interface to be tested with this harness.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface IValidator
{
	/**
	 * compiles the specified schema.
	 * 
	 * @return
	 * If the validator accepts the schema, it should return a compiled
	 * schema object. This object will be then used to validate XML instances.
	 * If the validator rejects the schema, it should return null.
	 * 
	 * @exception
	 *		Any exception is considered as an conformance violation.
	 */
	ISchema parseSchema( File schema ) throws Exception;
	
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
	boolean validate( ISchema schema, File document ) throws Exception;
}
