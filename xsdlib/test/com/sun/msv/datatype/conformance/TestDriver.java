/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.datatype.conformance;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import java.io.FileInputStream;
import java.util.Iterator;
import com.sun.tranquilo.datatype.*;

class TestDriver implements ErrorReceiver
{
	public static void main (String args[]) throws Exception
	{
		try
		{
			// reads test case file
			Document doc = new SAXBuilder("org.apache.xerces.parsers.SAXParser").build(
				TestDriver.class.getResourceAsStream("DataTypeTest.xml") );

			DataTypeTester tester = new DataTypeTester(System.out,new TestDriver());
			// perform test for each "case" item
			Iterator itr = doc.getRootElement().getChildren("case").iterator();
			while(itr.hasNext())
				tester.run( (Element)itr.next() );
		}
		catch(JDOMException e)
		{
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
    }
	
	public boolean report( UnexpectedResultException exp )
	{
		System.err.println("************* error *************");
		System.err.println("type name            : "+exp.baseTypeName);
		System.err.println("tested instance      : \""+exp.testInstance+"\"");
		System.err.println("supposed to be valid : "+exp.supposedToBeValid);
		System.err.println("verify method        : "+exp.type.verify(exp.testInstance,DummyContextProvider.theInstance) );
		System.err.println("diagnose method      : "+(exp.type.diagnose(exp.testInstance,DummyContextProvider.theInstance)==null) );
		
		if( exp.incubator.isEmpty() )
			System.err.println("facets: none");
		else
			exp.incubator.dump(System.err);

		DataTypeErrorDiagnosis err = exp.type.diagnose(exp.testInstance,DummyContextProvider.theInstance);
		
		if( err!=null && err.message!=null )
			System.err.println("diagnosis: " + err.message);
		else
			System.err.println("diagnosis: N/A");
		
		// do it again (for trace purpose)
		exp.type.verify(exp.testInstance,DummyContextProvider.theInstance);
		
		return false;
	}

	public boolean reportTestCaseError( DataType baseType, TypeIncubator incubator, BadTypeException e )
	{
/*
		System.err.println("---- warning ----");
		System.err.println("test case error");
		facets.dump(System.err);
		System.err.println();
		
		try
		{// do it again (for debug)
			baseType.derive("anonymous",facets);
		}
		catch( Exception ee ) { ; }
*/
		
		return false;
	}

}