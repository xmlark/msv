package com.sun.tranquilo.datatype.test;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import java.io.FileInputStream;
import java.util.Iterator;

class TestDriver implements ErrorReceiver
{
	public static void main (String args[]) throws Exception
	{
		// reads test case file
		Document doc = new SAXBuilder().build(new FileInputStream( //"testcase.txt"));
		"C:\\Documents and Settings\\Bear\\My Documents\\Sun\\tranquilo\\src\\com\\sun\\tranquilo\\datatype\\test\\testcase.txt" ));

		DataTypeTester tester = new DataTypeTester(System.out,new TestDriver());
		// perform test for each "case" item
		Iterator itr = doc.getRootElement().getChildren("case").iterator();
		while(itr.hasNext())
			tester.run( (Element)itr.next() );
    }
	
	public boolean report( UnexpectedResultException exp )
	{
		System.err.println("************* error *************");
		System.err.println("type name:"+exp.baseTypeName);
		System.err.println("tested instance: \""+exp.testInstance+"\"");
		System.err.println("supposed to be valid?"+exp.supposedToBeValid);
		if( exp.testCase.facets.isEmpty() )
			System.err.println("facets: none");
		else
			exp.testCase.facets.dump(System.err);
		
		// do it again (for tracing)
		exp.type.verify(exp.testInstance);
		
		return false;
	}

}