/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import org.relaxng.datatype.DatatypeException;

/**
 * command-line tester of datatype library.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class CommandLineTester
{
	public static void help()
	{
		System.out.println(
			"base <typeName>\n" +
			"  set base type name.\n" +
			"  this will reset all the facets you've added\n" +
			"add <facet name> <facet value>\n" +
			"  add facet\n" +
			"test <value>\n" +
			"  test if the value is accepted by the current base type and facets\n" +
			"quit\n"+
			"  quit this tool"
		);
	}
	public static void main( String args[] )
		throws java.io.IOException
	{
		System.out.println("XML Schema Part 2 command line tool");
		
		final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		// TypeIncubator is used to "incubate" a type by adding facets.
		// constructor accepts the base type instance.
		TypeIncubator incubator = new TypeIncubator(StringType.theInstance);
		
		while(true)
		{
			try
			{
				System.out.print("-->");
				String s = in.readLine();
				StringTokenizer tokens = new StringTokenizer(s);
			
				String cmd = tokens.nextToken();
			
				if( cmd.equals("base") )
				{
					String typeName = tokens.nextToken();
					
					// to obtain a type by name, call this method.
					XSDatatype dt = DatatypeFactory.getTypeByName(typeName);
					if(dt==null)
					{// if the name is not recognized, null is returned.
						System.out.println("no such type");
						continue;
					}
					incubator = new TypeIncubator(dt);
					continue;
				}
				if( cmd.equals("add") )
				{
					String facetName = tokens.nextToken();
					String facetValue = tokens.nextToken();
					// to add a facet, call add method.
					// you MUST supply a valid ValidationContextProvider,
					// although this example omits one.
					incubator.addFacet( facetName, facetValue, false, null );
					continue;
				}
				if( cmd.equals("test") )
				{
					String value = tokens.nextToken();
					// a type can be derived by derive method.
					// the new type contains all facets that were added.
					XSDatatype dt = incubator.derive("anonymous");
					
					// check validity.
					if( dt.isValid(value,null) )
						// verify method returns true if the value is valid.
						System.out.println("valid value");
					else
					{// it returns false otherwise,
						// call diagnose method to see what is wrong.
						try
						{
							dt.checkValid(value,null);
							System.out.println("valid");
						}
						catch( DatatypeException diag )
						{
							if( diag.getMessage()==null ) {
								// datatype object may not support diagnosis.
								// in that case, UnsupportedOperationException is thrown.
								System.out.println("invalid: no diagnosys available");
							} else {
								System.out.println("invalid: "+diag.getMessage() );
							}
						}
					}
					continue;
				}
				if( cmd.equals("quit") )
					return;
			
				help();
			}
			catch( DatatypeException bte )
			{// this exception happens in cases like:
				// 1. unapplicable facet is added ("minInclusive" for string, etc.)
				// 2. 
				System.out.println("DatatypeException: " +bte.getMessage() );
			}
			catch( java.util.NoSuchElementException nse )
			{// error in command line parsing.
				System.out.println("???");
				help();
			}
			catch( RuntimeException rte )
			{
				rte.printStackTrace();
			}
		}
	}
}
