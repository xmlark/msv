/*
 * $Id$
*/
package com.sun.tranquilo.datatype.test;

import org.jdom.*;
import java.util.List;
import java.io.PrintStream;
import com.sun.tranquilo.datatype.*;

/**
 * tests DataType.verify method with various types and various lexical values.
 */
public class DataTypeTester
{
	/** progress indication will be sent to this object */
	private final PrintStream out;
	
	/** error will be passed to this object */
	private final ErrorReceiver err;

	public DataTypeTester( PrintStream out, ErrorReceiver err )
	{
		this.out = out;
		this.err = err;
	}
	
	public void run( Element testCase )
		throws Exception
	{
		out.println( testCase.getAttributeValue("name") );
		
		String[] values;
		{// read values
			List lst = testCase.getChildren("value");
			values = new String[lst.size()];
			for( int i=0; i<values.length; i++ )
				values[i] = ((Element)lst.get(i)).getText();
		}
		
		String[] wrongValues;
		{// read wrongValues, which is always wrong with any types tested
			Element wrongs = testCase.getChild("wrongs");
			if(wrongs==null)	throw new Exception("no <wrongs>");
			
			List lst = wrongs.getChildren("value");
			wrongValues = new String[lst.size()];
			for( int i=0; i<wrongValues.length; i++ )
				wrongValues[i] = ((Element)lst.get(i)).getText();
		}
		
		// parses a test pattern, which basically enumerates
		// possible test pattern and its expected result
		Element facetElement = testCase.getChild("facets");
		TestPattern pattern =
			TestPatternGenerator.parse(
				(Element)facetElement.getChildren().get(0));
		
		{// perform test for each type specified
			List lst = testCase.getChildren("answer");
			for( int i=0; i<lst.size(); i++ )
			{
				Element item = (Element)lst.get(i);
				pattern.reset();
				
				DataType t = DataTypeFactory.getTypeByName(item.getAttributeValue("for"));
				if(t==null)
				{
					System.out.println(item.getAttributeValue("for") + " is undefined type");
				}
				testDataType(
					t,
					values, wrongValues,
					new BaseAnswerWrapper(item.getText(),pattern)
						// wrap it by intrisic restriction of this datatype
					);
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
		String[] values, String[] wrongs, TestPattern pattern )
		throws Exception
	{
		out.println("  testing " + baseType.getName() +
			" (total "+pattern.totalCases()+" patterns)" );
		
		long cnt=0;
		
		while(true)
		{
			if((cnt%500)==0 && cnt!=0 )	out.print("\r"+cnt+"    ");
			
			if( !pattern.hasMore() )	break;
			final TestCase testCase = pattern.get();
			
			// derive a type with test facets.
			DataType typeObj=null;
			try
			{
				typeObj = baseType.derive("anonymous",  testCase.facets );
			}
			catch( BadTypeException bte )
			{
				err.reportTestCaseError(baseType,testCase.facets,bte);
			}
			
			if(typeObj!=null)
			{
				final String answer = testCase.answer;
				
				if( answer.length()!=values.length )
					throw new IllegalStateException("answer and values have different length");

//				if( testCase.facets.isEmpty())	out.println("nofacet");
//				else	testCase.facets.dump(out);

				// test each value and see what happens
				for( int i=0; i<values.length; i++ )
				{
					boolean v = typeObj.verify(values[i]);
					boolean d = (typeObj.diagnose(values[i])==null);
					
					if(v && d && answer.charAt(i)=='o')
						continue;	// as predicted
					if(!v && !d && answer.charAt(i)=='.')
						continue;	// as predicted
					
					// dump error messages
					if( !err.report( new UnexpectedResultException(
							typeObj, baseType.getName(),
							values[i], answer.charAt(i)=='o',
							testCase ) ) )
					{
						out.println("test aborted");
						return;
					}
				}

				// test each wrong values and makes sure that they are rejected.
				for( int i=0; i<wrongs.length; i++ )
					if( typeObj.verify(wrongs[i])
					||  typeObj.diagnose(wrongs[i])==null )
					{
						if( !err.report( new UnexpectedResultException(
							typeObj, baseType.getName(),
							wrongs[i], false, TestCase.theEmptyCase ) ) )
						{
							out.println("test aborted");
							return;
						}
					}
			}
			
			cnt++;
			pattern.next();
		}
		
		out.println();
		// test done
		out.println("  " + cnt + " cases tested");
	}
	
	static class BaseAnswerWrapper implements TestPattern
	{
		private final TestPattern core;
		private final String baseAnswer;
		
		/** returns the number of test cases to be generated */
		public long totalCases() { return core.totalCases(); }

		/** restart generating test cases */
		public void reset() { core.reset(); }

		/** get the current test case */
		public TestCase get()
		{
			// merge two answer in AND mode
			TestCase tc = new TestCase(baseAnswer);
			try
			{
				tc.merge(core.get(),true);
			}catch(BadTypeException bte) { throw new IllegalStateException(); } // not possible
			return tc;
		}

		/** generate next test case */
		public void next() { core.next(); }

		public boolean hasMore() { return core.hasMore(); }
		
		BaseAnswerWrapper( String baseAnswer, TestPattern base )
		{
			this.core = base;
			this.baseAnswer = TestPatternGenerator.trimAnswer(baseAnswer);
		}
	}
}
