/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.verifier.multithread;

import javax.xml.parsers.*;
import com.sun.tranquilo.grammar.Grammar;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ExpressionPool;
import com.sun.tranquilo.reader.util.GrammarLoader;
import com.sun.tranquilo.grammar.trex.TREXPatternPool;
import com.sun.tranquilo.verifier.regexp.trex.TREXDocumentDeclaration;
import com.sun.tranquilo.verifier.Verifier;
import com.sun.tranquilo.verifier.ValidityViolation;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * multi-thread tester.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Daemon implements Runnable
{
	public static void main( String args[] ) throws Exception
	{
		new Daemon()._main(args);
	}
	
	Grammar grammar;
	/** file names that have to be validated. */
	private final Stack jobs = new Stack();
	
	private void _main( String args[] ) throws Exception
	{
		if( args.length!=4 )
		{
			System.out.println(
				"Usage: Daemon (relax|trex) <fileprefix> <suffix> <# of threads>\n"+
				"  <prefix>.trex or <prefix>.rlx is used as a schema\n"+
				"  <prefix>.v100<suffix> to <prefix>.v999<suffix> are used as instances");
			return;
		}
		
//		SAXParserFactory factory = new org.apache.crimson.jaxp.SAXParserFactoryImpl();
		SAXParserFactory factory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		
		String schemaName;

		if( args[0].equals("relax") )	schemaName = args[1]+".rlx";
		else							schemaName = args[1]+".trex";

		grammar = GrammarLoader.loadSchema(
				schemaName,
				new com.sun.tranquilo.driver.textui.DebugController(false),
				factory );
		
		
		for( int i=100; i<=999; i++ )
			jobs.push( args[1]+".v"+i+args[2] );
		
		final long startTime = System.currentTimeMillis();
		
		final int m = Integer.parseInt( args[3] );
		System.out.println("Use " + m + " threads");
		Thread[] ts = new Thread[m];
		for( int i=0; i<m; i++ )
		{
			ts[i] = new Thread(this,"#"+i);
			ts[i].start();
		}
		
		System.out.println("launched all threads");
		
		for( int i=0; i<m; i++ )
			ts[i].join();
		
		System.out.println("Time : " + (System.currentTimeMillis()-startTime) + " ms");
	}
	
	public void run()
	{
		final String name = Thread.currentThread().getName();
		try
		{
			SAXParserFactory factory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
			factory.setNamespaceAware(true);
			TREXPatternPool localPool = new TREXPatternPool(grammar.getPool());
//			TREXPatternPool localPool = new TREXPatternPool();
			
			while(true)
			{
				String fileName;
				try
				{
					fileName = (String)jobs.pop();	// stack is synchronized
				}
				catch( EmptyStackException e )
				{
					System.out.println( name + " completed" );
					return;
				}
				
				XMLReader r = factory.newSAXParser().getXMLReader();
				Verifier v = new Verifier(
					new TREXDocumentDeclaration(grammar),
//					new TREXDocumentDeclaration(grammar.getTopLevel(),localPool),
					new com.sun.tranquilo.verifier.util.VerificationErrorHandlerImpl() );
				r.setContentHandler(v);
				try
				{
					r.parse(fileName);
					if(!v.isValid()) throw new Error(); 
					System.out.print('.');
				}
				catch( ValidityViolation vv )
				{
					System.out.println( name + ":" + fileName + " invalid  " + vv.getMessage() );
				}
			}
		}
		catch( Exception e )
		{
			System.out.println( name + " aborted" );
			e.printStackTrace();
		}
	}
}
