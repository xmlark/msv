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
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;

import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.extensions.XSLProcessorContext;
import org.apache.xalan.templates.ElemExtensionCall;
import org.apache.xpath.NodeSet;
import org.w3c.dom.Attr;
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
    private final XalanNodeAssociationManager psvi =
        XalanNodeAssociationManager.createInstance();
    
    /**
     * Stores {@link PSVIRecorder} for each document.
     */
    private final XalanNodeAssociationManager schema =
        XalanNodeAssociationManager.createInstance();
    
    /** Used as a node factory. */
    private final Document document;
    
    
    public XalanExtension(Document _document) {
        this.document = _document;
    }
    
    public XalanExtension() throws ParserConfigurationException {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         dbf.setNamespaceAware(true);
         this.document = dbf.newDocumentBuilder().newDocument();
     }
    
    
    /**
     * <code>useSchema</code> extension element that
     * associates a schema to the input.
     */
    public void useSchema( XSLProcessorContext context, ElemExtensionCall call ) throws TransformerException {
        try {
            // obtain the schema location
            String href = call.getAttribute("href", context.getContextNode(), 
                                                      context.getTransformer());

            // determine the root node
            Element root;
//            if( call.hasAttribute("name") ) {
//                // TODO
//            } else {
                root = getFirstElement(context.getContextNode().getOwnerDocument());
//            }
            
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
                root );
        } catch( RuntimeException re ) {
            re.printStackTrace();
            throw re;
        }
    }
    
    /**
     * Equivalent of the getDocumentElement method.
     */
    private Element getFirstElement( Document document ) {
        for( Node n = document.getFirstChild(); n!=null; n=n.getNextSibling() )
            if( n.getNodeType()==Node.ELEMENT_NODE )
                return (Element)n;
        return null;
    }

    public NodeSet annotation( ExpressionContext context ) throws ParserConfigurationException {
        Element e = document.createElement("dummy");
        buildResult( context.getContextNode(), e );
        return new NodeSet(e);
    }

    public NodeSet annotation( ExpressionContext context, NodeList list ) throws ParserConfigurationException {
        // put all the nodes under a dummy element so that the stylesheets
         // can be written as "schmit:annotation(.)/@test" etc.
        Element e = document.createElement("dummy");
                
        for( int i=0; i<list.getLength(); i++ )
            buildResult( list.item(i), e );

        return new NodeSet(e);
    }
    
    private void buildResult( Node n, Element result ) {
        AnnotatedPattern p = (AnnotatedPattern)psvi.get(n);
            
        if( p==null )   return;
            
        for( Iterator itr=p.getAnnotations().iterator(); itr.hasNext(); ) {
            Node o = document.importNode( (Node)itr.next(), true );
            if( o instanceof Attr )
                result.setAttributeNodeNS((Attr)o);
            else
                result.appendChild( o );
        }
    }
}
