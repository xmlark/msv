/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.datatype;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * command-line tester of datatype library.
 * 
 * @author Kohsuke KAWAGUCHI
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
					DataType dt = DataTypeFactory.getTypeByName(typeName);
					if(dt==null)
					{
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
					incubator.add( facetName, facetValue, false, null );
					continue;
				}
				if( cmd.equals("test") )
				{
					String value = tokens.nextToken();
					DataType dt = incubator.derive("anonymous");
					if( dt.verify(value,null) )
						System.out.println("valid value");
					else
					{
						DataTypeErrorDiagnosis diag = dt.diagnose(value,null);
						System.out.println("invalid: "+diag.message );
					}
					continue;
				}
				if( cmd.equals("quit") )
					return;
			
				help();
			}
			catch( BadTypeException bte )
			{
				System.out.println("BadTypeException: " +bte.getMessage() );
			}
			catch( java.util.NoSuchElementException nse )
			{
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
