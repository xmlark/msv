/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package batch;

import javax.xml.parsers.*;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.io.*;
//import java.net.URL;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.relaxng.testharness.model.*;
import junit.framework.*;
import com.sun.msv.verifier.*;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.dtd.DTDReader;
import com.sun.msv.reader.util.GrammarLoader;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.resolver.tools.CatalogResolver;
import msv.*;
import batch.model.DirectoryTestReader;

/**
 * Test schemata/instances are expected to follow a naming convention.
 * 
 * <ol>
 *  <li>legal schema must have "*.rlx" or "*.trex"
 *  <li>invalid schema must have "*.e.rlx" or "*.e.trex"
 *  <li>valid test document must have "*.vNN.xml".
 *      these documents are validated against "*.rlx" or "*.trex".
 *  <li>invalid test document must have "*.nNN.xml".
 * </ol>
 * 
 * Files that follow this naming convention are all tested. If any unexpected
 * result is found, main method returns non-0 exit code.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class BatchTester {
	public SAXParserFactory factory =
		new org.apache.xerces.jaxp.SAXParserFactoryImpl();
//		new org.apache.crimson.jaxp.SAXParserFactoryImpl();

	public CatalogResolver resolver = new CatalogResolver();
	
	/** schema file extension ".rlx", ".trex", or ".dtd" */
	public String ext;
	
	public IValidatorEx validator;
	
	
	
	
	
	
	protected abstract void usage();
	
	protected abstract TestSuite suite( RNGTestSuite src );
	
	
	public RNGTest parse( String target ) throws Exception {
		File src = new File(target);
		
		if(src.isDirectory())
			return DirectoryTestReader.parseDirectory(
				src, ext, false );
		else
			return DirectoryTestReader.parseSchema(src);
	}
	
	public void init( String target ) {
		if( target.equals("relax") )		_init( ".rlx", new GenericValidator() );
		else
		if( target.equals("trex") )		_init( ".trex", new GenericValidator() );
		else
		if( target.equals("rng") )		_init( ".rng", new IValidatorImplForRNG() );
		else
		if( target.equals("xsd") )		_init( ".xsd", new IValidatorImplForXS() );
		else
		if( target.equals("dtd") )		_init( ".dtd", new DTDValidator() );
		else
			throw new Error("unrecognized language type: "+target );
	}
		
	private void _init( String _ext, IValidatorEx _validator ) {
		this.ext = _ext;
		this.validator = _validator;
	}
	
	public void run( String[] av ) throws Exception {
		
		if( av.length<2 ) {
			usage();
			return;
		}
		
		init(av[0]);
		
		// collect test cases
		RNGTestSuite s = new RNGTestSuite();
		for( int i=1; i<av.length; i++ )
			s.addTest(parse(av[i]));
		
		junit.textui.TestRunner.run( suite(s) );
	}
	
	public static void report( ValidityViolation vv ) {
		System.out.println(
			vv.getLineNumber()+":"+vv.getColumnNumber()+
			"  " + vv.getMessage());
	}

	
	
	/**
	 * Creates a JUnit test suite from a set of paths that are specified
	 * by a system property.
	 * 
	 * Mainly used to run an automated test from Ant.
	 */
	public TestSuite createFromProperty( String propertyName ) throws Exception {
		String property = System.getProperty(propertyName);
		if(property==null)
			return new TestSuite();	// return an empty test suite.
		
		StringTokenizer tokens = new StringTokenizer( property, ";" );

		// collect test cases
		RNGTestSuite s = new RNGTestSuite();
		while( tokens.hasMoreTokens() )
			s.addTest(parse(tokens.nextToken()));
				
		return suite(s);
	}
	
	/**
	 * Gets the name of the test case from a header, which can be possibly null.
	 */
	public String getName( RNGHeader header ) {
		String s=null;
		if(header!=null)	s = header.getName();
		if(s==null)			s = "";
		return s;
	}

}
