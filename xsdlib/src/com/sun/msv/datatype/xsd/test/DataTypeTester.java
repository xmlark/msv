/*
 * $Id$
*/
package com.sun.tranquilo.datatype.test;

import org.jdom.*;
import java.util.List;
import com.sun.tranquilo.datatype.*;

public class DataTypeTester
{
    public static void main (String args[]) throws Exception
	{
		// reads test case file
		Document doc = new DOMBuilder().build(new FileInputStream("testcase.xml"));

		// perform test for each "case" item
		Iterator itr = doc.getRootElement().getChildren("case").iterator();
		while(itr.hasNext())
			runTestCase( (Element)itr.next() );
    }
	
	public static void runTestCase( Element testCase )
	{
		String[] values;
		{// read values
			List lst = testCase.getChildren("value");
			values = new String[lst.size()];
			for( int i=0; i<len; i++ )
				values[i] = ((Element)lst.get(i)).getText();
		}
		
		String[] wrongValues;
		{// read wrongValues, which is always wrong with any types tested
			List lst = testCase.getChild("wrongValues").getChildren("value");
			wrongValues = new String[lst.size()];
			for( int i=0; i<len; i++ )
				wrongValues[i] = ((Element)lst.get(i)).getText();
		}
		
		// creates a test pattern generator, which basically enumerates
		// possible test pattern and its expected result
		TestPatternGenerator pattern =
			new TestPatternGenerator(testCase.getChild("facets"));
		
		{// perform test for each type specified
			List lst = testCase.getChildren("answer");
			for( int i=0; i<lst.size(); i++ )
			{
				Element item = (Element)lst.get(i);
				
				pattern.setBaseAnswer(item.getText());
				pattern.reset();
				
				testDataType(
					DataTypeFactory.get(item.getAttributeValue("for")),
					values, wrongValues, pattern );
			}
		}
	}
	
	/**
	 * tests one datatype
	 *
	 * @param typeObj
	 *		DataType object to be tested
	 * @param answer
	 *		default answer sheet for values parameter.
	 *		each "o" indicates valid, whereas "." indicates invalid.
	 *		answer.charAt(i) indicates validity of values[i],
	 * @param values
	 *		possibly valid values. These values are tested against
	 *		DataType object.
	 * @param wrongs
	 *		values which are always wrong. But they are tested anyway.
	 * @param pattern
	 *		test pattern generator that iterates a combination of facets
	 *		and its predicted result.
	 */
	public void testDataType(
		DataType baseType,
		String[] values, String[] wrongs, TestPatternGenerator pattern )
	{
		while(pattern.hasMore())
		{
			// derive a type with test facets.
			DataType typeObj = typeObj.derive( pattern.getFacets() );
			String answer = pattern.getAnswer();
			
			// test each value and see what happens
			for( int i=0; i<values.length; i++ )
			{
				if(typeObj.verify(value[i]))
				{
					if(answer.charAt(i)=='o')	continue;	// as predicted
				}
				else
				{
					if(answer.charAt(i)=='.')	continue;	// as predicted
				}
				// dump error messages
				throw new Exception("unexpected result");
			}
			
			// test each wrong values and makes sure that they are rejected.
			for( int i=0; i<wrongs.length; i++ )
				if(typeObj.verify(wrongs[i]))
					throw new Exception("unexpected result");
			
			pattern.next();
		}
		
		// test done
	}
}
