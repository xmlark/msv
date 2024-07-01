/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of Sun Microsystems, Inc.
 * Use is subject to license terms.
 *
 */
/*
 * $Id$
*/
package com.sun.msv.datatype.xsd.conformance;

import com.sun.msv.datatype.xsd.DatatypeFactory;
import com.sun.msv.datatype.xsd.TypeIncubator;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.XSDatatypeImpl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.List;
import org.jdom2.Element;
import org.junit.Assert;
import org.relaxng.datatype.DatatypeException;

/**
 * tests DataType.verify method with various types and various lexical values.
 *
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
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
            if(wrongs==null)    throw new Exception("no <wrongs>");

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
                XSDatatypeImpl t = (XSDatatypeImpl)DatatypeFactory.getTypeByName(item.getAttributeValue("for"));
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
//                DataType u = DataTypeFactory.deriveByUnion(null,
//                    new DataType[]{ t, getRandomType(), getRandomType() } );
//                testDataType(
//                    u, values, new String[0]/*no explicitly wrong values to test*/,
//                    new BaseAnswerWrapper(item.getText(), pattern, true ),
//                    true );
            }
        }
    }

    /**
     * tests one datatype
     *
     * @param typeObj
     *        DataType object to be tested
     * @param answer
     *        default answer sheet for values parameter.
     *        each "o" indicates valid, whereas "." indicates invalid.
     *        answer.charAt(i) indicates validity of values[i],
     * @param values
     *        possibly valid values. These values are tested against
     *        DataType object.
     * @param wrongs
     *        values which are always wrong. But they are tested anyway.
     * @param pattern
     *        test pattern generator that iterates a combination of facets
     *        and its predicted result.
     * @param completenessOnly
     *        true indicates test is only performed for completeness; that is,
     *        make sure that answer is 'o' for those which is marked as 'o'.
     *        this flag is used to test union types.
     */
    public void testDataType(
        XSDatatypeImpl baseType,
        String[] values, String[] wrongs,
        String baseAnswer, TestPattern pattern,
        boolean completenessOnly )
        throws Exception
    {
        long startTime = System.currentTimeMillis();
        out.println("  testing " + baseType.getName() +
            " (total "+pattern.totalCases()+" patterns)" );

        pattern.reset();

        long cnt=0;

        while(true)
        {
            if((cnt%500)==0 && cnt!=0 )    out.print("\r"+cnt+"    ");

            if( !pattern.hasMore() )    break;
            final TypeIncubator ti = new TypeIncubator(baseType);
            String answer;

            try
            {
                answer = TestPatternGenerator.merge( baseAnswer, pattern.get(ti), true );
            }
            catch( DatatypeException bte )
            {// ignore this error
                pattern.next();
                continue;
            }

            // derive a type with test facets.
            XSDatatype typeObj=null;
            try
            {
                typeObj = ti.derive("anonymousURI","anonymousLocal");
            }
            catch( DatatypeException bte )
            {
                err.reportTestCaseError(baseType,ti,bte);
            }

            {// make sure that the serialization works.
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);

                // serialize it
                oos.writeObject( typeObj );
                oos.flush();

                // obtain byte array (just in case)
                ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(bos.toByteArray()));
                typeObj = (XSDatatype)ois.readObject();
                ois.close();
            }

            if(typeObj!=null)
            {
                if( answer.length()!=values.length )
                    throw new IllegalStateException("answer and values have different length");

//                if( testCase.facets.isEmpty())    out.println("nofacet");
//                else    testCase.facets.dump(out);

                // test each value and see what happens
                for( int i=0; i<values.length; i++ )
                {
                    boolean v = typeObj.isValid(values[i],DummyContextProvider.theInstance);
                    boolean d;

                    boolean roundTripError = false;

                    Object o = typeObj.createValue(
                        values[i],DummyContextProvider.theInstance);

                    try {
                        if(o!=null) {
                            // should be able to convert it back.
                            String s = typeObj.convertToLexicalValue(o,DummyContextProvider.theInstance);
                            // try round trip conversion.
                            Object o2 = typeObj.createValue(s,DummyContextProvider.theInstance);
                            if( o2==null || !o.equals(o2) )
                            {
                                System.out.println("equals error: \n\"" + o.toString() + "\"\n\"" + s + "\"\n\"" + o2.toString() + "\"");
                                roundTripError = true;
                            }
                        }
                    } catch( UnsupportedOperationException uoe ) {
                        // ignore this exception
                    } catch( IllegalArgumentException iae ) {
                        System.out.println("roundtrip IllegalArgumentException");
                        roundTripError = true;
                    }

                    Object jo = typeObj.createJavaObject( values[i], DummyContextProvider.theInstance );

                    if( jo!=null ) {
                        if( !typeObj.getJavaObjectType().isAssignableFrom(jo.getClass()) ) {
                            System.out.println("type error");
                            roundTripError = true;
                        }

                        String s = typeObj.serializeJavaObject( jo,
                                        DummyContextProvider.theInstance );
                        if(s==null) {
                            System.out.println("serializeJavaObject failed");
                            roundTripError = true;
                        } else {
                            Object o2 = typeObj.createJavaObject(s,DummyContextProvider.theInstance);
                            if( o2==null ) {
                                System.out.println("round-trip conversion failed");
                                roundTripError = true;                                
                            }
                        }
                    }

                    try {
                        typeObj.checkValid(values[i],DummyContextProvider.theInstance);
                        d = true;
                    } catch( DatatypeException de ) {
                        d = false;
                    }

                    // if convertToValueObject and verify method differs,
                    // it's always an error.
                    // roundTripError is always an error.
                    if(!roundTripError && (o!=null)==v && (jo!=null)==v ) {

                        if(v && d && answer.charAt(i)=='o')
                            continue;    // as predicted
                        if(!v && !d && answer.charAt(i)=='.')
                            continue;    // as predicted

                        if(completenessOnly && answer.charAt(i)=='.' && v==d )
                            continue;    // do not report error if
                                        // the validator accepts things that
                                        // may not be accepted.
                    }else if(roundTripError){
                        Assert.fail("RoundtripError!");
                    }

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
                for( int i=0; i<wrongs.length; i++ ) {
                    boolean err = false;

                    err = typeObj.isValid(wrongs[i],DummyContextProvider.theInstance);
                    try {
                        typeObj.checkValid(wrongs[i],DummyContextProvider.theInstance);
                        err = true;
                    } catch (DatatypeException de) {
                        ;    // it should throw an exception
                    }

                    if( typeObj.createJavaObject(wrongs[i],DummyContextProvider.theInstance)!=null )
                        err = true;
                        
                    if( err ) {
                        if( !this.err.report( new UnexpectedResultException(
                            typeObj, baseType.getName(),
                            wrongs[i], false, ti ) ) )
                        {
                            out.println("test aborted");
                            Assert.fail("Test aborted!");
                            return;
                        }
                    }
                }
            }

            cnt++;
            pattern.next();
        }

        out.println();
        // test done
        out.println("  " + cnt + " cases tested ("+(System.currentTimeMillis()-startTime)+"ms)");
    }

    public static final String[] builtinTypesList =
    new String[]{
        "string",
        "boolean",
        "number",
        "float",
        "double",
        "duration",
        "dateTime",
        "time",
        "date",
        "gYearMonth",
        "gYear",
        "gMonthDay",
        "gDay",
        "gMonth",
        "hexBinary",
        "base64Binary",
        "anyURI",
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
}
