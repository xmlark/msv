/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.schmit;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParserFactory;

import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.extensions.XSLProcessorContext;
import org.apache.xalan.templates.ElemExtensionCall;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.schmit.grammar.AnnotatedPattern;
import com.sun.msv.schmit.reader.relaxng.SchmitRELAXNGReader;
import com.sun.msv.schmit.util.ListNodeListImpl;

/**
 * Extension element/function definitions for Xalan.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class XalanExtension {
    
    /**
     * Stores {@link com.sun.msv.schmit.grammar.AnnotatedElementPattern}
     * for each Element node.
     */
    private static final XalanNodeAssociationManager psvi =
        XalanNodeAssociationManager.createInstance();
    
    /**
     * Stores {@link PSVIRecorder} for each document.
     */
    private static final XalanNodeAssociationManager schema =
        XalanNodeAssociationManager.createInstance();
    
   
    
    /**
     * <code>useSchema</code> extension element that
     * associates a schema to the input.
     */
    public static void useSchema( XSLProcessorContext context, ElemExtensionCall call ) {
        try {
            // obtain the schema location
            String href = call.getAttribute("href");
            
            try {
                href = new URL( new URL(call.getBaseIdentifier()), href ).toExternalForm();
            } catch( MalformedURLException e ) {
                ;   // failed to absolutize
            }
            
            // parse the grammar
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            GrammarReader reader = new SchmitRELAXNGReader(
                new DebugController(false),
                spf,
                new ExpressionPool() );
            
            reader.parse(href);
            Grammar grammar = reader.getResultAsGrammar();
            
    //        // remember this association
    //        schema.put( context.getSourceTree(), new PSVIRecorder(grammar) );
    
            // run PSVI annotation
            new PSVIRecorder(grammar,psvi).annotate(
                // DTM node proxy doesn't support the getDocumentElement method.
                getFirstElement(context.getContextNode().getOwnerDocument()) );
        } catch( RuntimeException re ) {
            re.printStackTrace();
            throw re;
        }
    }
    
    /**
     * Equivalent of the getDocumentElement method.
     */
    private static Element getFirstElement( Document document ) {
        for( Node n = document.getFirstChild(); n!=null; n=n.getNextSibling() )
            if( n.getNodeType()==Node.ELEMENT_NODE )
                return (Element)n;
        return null;
    }

//    public static NodeList set( ExpressionContext context, NodeList nl, String data ) {
//        for( int i=0; i<nl.getLength(); i++ ) {
//            Node n = nl.item(i);
//            XalanNodeAssociationManager.theInstance.put(n,data);
//        }
//        return new NodeList() {
//            public Node item(int index) {
//                return null;
//            }
//
//            public int getLength() {
//                return 0;
//            }
//        };
//    }
    
    public static NodeList annotation( ExpressionContext context, NodeList list ) {
        final ArrayList a = new ArrayList();
        
        for( int i=0; i<list.getLength(); i++ ) {
            Node n = list.item(i);
            AnnotatedPattern p = (AnnotatedPattern)psvi.get(n);
        
            if( p!=null )   a.addAll(p.getAnnotations());
        }
        
        return new ListNodeListImpl(a);
    }
    
//    private static Node create( String data ) {
//        try {
//            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//            dbf.setNamespaceAware(true);
//            Document d = dbf.newDocumentBuilder().newDocument();
//        
//            Node testNode = d.createElementNS("abc","def");
//            testNode.appendChild(d.createTextNode(data));
//            return testNode;
//        } catch( Exception e ) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}
