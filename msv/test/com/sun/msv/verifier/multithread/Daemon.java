/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.multithread;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.util.GrammarLoader;
import com.sun.msv.verifier.ValidityViolation;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.util.ErrorHandlerImpl;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.driver.textui.DebugController;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParserFactory;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * multi-thread tester.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Daemon implements Runnable
{
    public static void main( String[] args ) throws Exception
    {
        new Daemon()._main(args);
    }
    
    Grammar grammar;
    /** file names that have to be validated. */
    private final Stack jobs = new Stack();
    
    private void _main( String[] args ) throws Exception
    {
        if( args.length!=4 )
        {
            System.out.println(
                "Usage: Daemon (relax|trex) <fileprefix> <suffix> <# of threads>\n"+
                "  <prefix>.trex or <prefix>.rlx is used as a schema\n"+
                "  <prefix>.v100<suffix> to <prefix>.v999<suffix> are used as instances");
            return;
        }
        
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        
        String schemaName;

        if( args[0].equals("relax") )    schemaName = args[1]+".rlx";
        else                            schemaName = args[1]+".trex";

        grammar = GrammarLoader.loadSchema(
                schemaName,
                new DebugController(false,false),
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
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
//            ExpressionPool localPool = new ExpressionPool(grammar.getPool());
//            ExpressionPool localPool = new ExpressionPool();
            
            while(true)
            {
                String fileName;
                try
                {
                    fileName = (String)jobs.pop();    // stack is synchronized
                }
                catch( EmptyStackException e )
                {
                    System.out.println( name + " completed" );
                    return;
                }
                
                XMLReader r = factory.newSAXParser().getXMLReader();
                Verifier v = new Verifier(
                    new REDocumentDeclaration(grammar),
//                    new REDocumentDeclaration(grammar.getTopLevel(),localPool),
                    new ErrorHandlerImpl() );
                r.setContentHandler(v);
                try
                {
                    r.parse(fileName);
                    if(!v.isValid()) throw new Error(); 
                    System.out.print('.');
                }
                catch( ValidityViolation vv )
                {
                    System.out.println( name + ':' + fileName + " invalid  " + vv.getMessage() );
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
