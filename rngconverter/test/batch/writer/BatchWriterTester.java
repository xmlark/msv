/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package batch.writer;

import junit.framework.*;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.writer.GrammarWriter;

/**
 * tests converter by using JUnit.
 * 
 * <p>
 * This class first loads the grammar, then converts it to a specific
 * language by using a GrammarWriter. Next, GrammarReader is used to
 * parse the converted grammar. Finally, test instances are validated
 * against re-parsed Grammar to make sure that the conversion was in fact
 * successful.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class BatchWriterTester extends batch.BatchTester {
	
	/** gets a TestSuite that loads and verifies all test instances in the test directory. */
	protected void populateSuite( TestSuite suite, String[] schemas ) {
		// each schema will have its own suite.
		if( schemas!=null ) {
			for( int i=0; i<schemas.length; i++ ) {
				if( !schemas[i].endsWith(".e"+ext)
				&&  schemas[i].indexOf(".nogen.")<0 )
					suite.addTest( new SchemaWriterSuite(this,schemas[i]).suite() );
				
				// ignore bad schemata
				// ignore schemata with id constraint
			}
		}
	}
	
	protected abstract GrammarReader createReader();
	protected abstract GrammarWriter getWriter();
}
