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
		// reads test case file
		Document doc = new SAXBuilder().build(
			TestDriver.class.getResourceAsStream("DataTypeTest.xml") );

		DataTypeTester tester = new DataTypeTester(System.out,new TestDriver());
		// perform test for each "case" item
		Iterator itr = doc.getRootElement().getChildren("case").iterator();
		while(itr.hasNext())
			tester.run( (Element)itr.next() );
    }
	
	public boolean report( UnexpectedResultException exp )
	{
		System.err.println("************* error *************");
		System.err.println("type name            : "+exp.baseTypeName);
		System.err.println("tested instance      : \""+exp.testInstance+"\"");
		System.err.println("supposed to be valid : "+exp.supposedToBeValid);
		System.err.println("verify method        : "+exp.type.verify(exp.testInstance,null) );
		System.err.println("diagnose method      : "+(exp.type.diagnose(exp.testInstance,null)==null) );
		
		if( exp.testCase.facets.isEmpty() )
			System.err.println("facets: none");
		else
			exp.testCase.facets.dump(System.err);

		DataTypeErrorDiagnosis err = exp.type.diagnose(exp.testInstance,null);
		
		if( err!=null && err.message!=null )
			System.err.println("diagnosis: " + err.message);
		else
			System.err.println("diagnosis: N/A");
		
		// do it again (for trace purpose)
		exp.type.verify(exp.testInstance,null);
		
		return false;
	}

	public boolean reportTestCaseError( DataType baseType, Facets facets, BadTypeException e )
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