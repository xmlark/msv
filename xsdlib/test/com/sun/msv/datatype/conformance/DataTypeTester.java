/*
 * $Id$
*/
package com.sun.tranquilo.datatype.conformance;

import org.jdom.*;
import java.util.*;
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
		
		{
			List lst = testCase.getChildren("answer");
			for( int i=0; i<lst.size(); i++ )
			{
				Element item = (Element)lst.get(i);
				
				// perform test as a single type
				DataType t = DataTypeFactory.getTypeByName(item.getAttributeValue("for"));
				if(t==null)
				{
					System.out.println(item.getAttributeValue("for") + " is undefined type");
				}
				testDataType(
					t,
					values, wrongValues,
					TestPatternGenerator.trimAnswer(item.getText()),
					pattern,
						// wrap it by intrisic restriction due to this datatype
					false
					);

// TODO : we need more systematic approach here
				// perform test as an union type (completeness only)
//				DataType u = DataTypeFactory.deriveByUnion(null,
//					new DataType[]{ t, getRandomType(), getRandomType() } );
//				testDataType(
//					u, values, new String[0]/*no explicitly wrong values to test*/,
//					new BaseAnswerWrapper(item.getText(), pattern, true ),
//					true );
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
	 * @param completenessOnly
	 *		true indicates test is only performed for completeness; that is,
	 *		make sure that answer is 'o' for those which is marked as 'o'.
	 *		this flag is used to test union types.
	 */
	public void testDataType(
		DataType baseType,
		String[] values, String[] wrongs,
		String baseAnswer, TestPattern pattern,
		boolean completenessOnly )
		throws Exception
	{
		out.println("  testing " + baseType.getName() +
			" (total "+pattern.totalCases()+" patterns)" );
		
		pattern.reset();
		
		long cnt=0;
		
		while(true)
		{
			if((cnt%500)==0 && cnt!=0 )	out.print("\r"+cnt+"    ");
			
			if( !pattern.hasMore() )	break;
			final TypeIncubator ti = new TypeIncubator(baseType);
			String answer;
			
			try
			{
				answer = TestPatternGenerator.merge( baseAnswer, pattern.get(ti), true );
			}
			catch( BadTypeException bte )
			{// ignore this error
				pattern.next();
				continue;
			} 
			
			// derive a type with test facets.
			DataType typeObj=null;
			try
			{
				typeObj = ti.derive("anonymous");
			}
			catch( BadTypeException bte )
			{
				err.reportTestCaseError(baseType,ti,bte);
			}
			
			if(typeObj!=null)
			{
				if( answer.length()!=values.length )
					throw new IllegalStateException("answer and values have different length");

//				if( testCase.facets.isEmpty())	out.println("nofacet");
//				else	testCase.facets.dump(out);

				// test each value and see what happens
				for( int i=0; i<values.length; i++ )
				{
					boolean v = typeObj.verify(values[i],DummyContextProvider.theInstance);
					boolean d;
					
					try
					{
						d = (typeObj.diagnose(values[i],DummyContextProvider.theInstance)==null);
					}
					catch( UnsupportedOperationException uoe )
					{
						d = v;
					}
					
					if(v && d && answer.charAt(i)=='o')
						continue;	// as predicted
					if(!v && !d && answer.charAt(i)=='.')
						continue;	// as predicted
					
					if(completenessOnly && answer.charAt(i)=='.' && v==d )
						continue;	// do not report error if
									// the validator accepts things that
									// may not be accepted.
					
					// dump error messages
					if( !err.report( new UnexpectedResultException(
							typeObj, baseType.getName(),
							values[i], answer.charAt(i)=='o',
							ti ) ) )
					{
						out.println("test aborted");
						return;
					}
				}

				// test each wrong values and makes sure that they are rejected.
				for( int i=0; i<wrongs.length; i++ )
					if( typeObj.verify(wrongs[i],DummyContextProvider.theInstance)
					||  typeObj.diagnose(wrongs[i],DummyContextProvider.theInstance)==null )
					{
						if( !err.report( new UnexpectedResultException(
							typeObj, baseType.getName(),
							wrongs[i], false, ti ) ) )
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
	
	public static final String[] builtinTypesList =
	new String[]{
		"string",
		"boolean",
		"decimal",
		"float",
		"double",
		"duration",
		"dateTime",
		"time",
		"date",
		"yearMonth",
		"year",
		"monthDay",
		"day",
		"month",
		"hexBinary",
		"base64Binary",
		"uriReference",
		"ID",
		"IDREF",
		"ENTITY",
		"QName",
		"normalizedString",
		"token",
		"language",
		"IDREFS",
		"ENTITIES",
		"NMTOKEN",
		"NMTOKENS",
		"Name",
		"NCName",
		"NOTATION",
		"integer",
		"nonPositiveInteger",
		"negativeInteger",
		"long",
		"int",
		"short",
		"byte",
		"nonNegativeInteger",
		"unsignedLong",
		"unsignedInt",
		"unsignedShort",
		"unsignedByte",
		"positiveInteger"
	};
	
	/** gets some built-in type randomly. */
	private DataType getRandomType()
	{
		return DataTypeFactory.getTypeByName(
			builtinTypesList[ (int)(Math.random()*builtinTypesList.length) ] );
	}
}
