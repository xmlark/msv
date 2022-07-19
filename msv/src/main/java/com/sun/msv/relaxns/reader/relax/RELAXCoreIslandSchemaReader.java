/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and  use in  source and binary  forms, with  or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions  of  source code  must  retain  the above  copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution  in binary  form must  reproduct the  above copyright
 *   notice, this list of conditions  and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither  the  name   of  Sun  Microsystems,  Inc.  or   the  names  of
 * contributors may be  used to endorse or promote  products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS  OR   IMPLIED  CONDITIONS,  REPRESENTATIONS   AND  WARRANTIES,
 * INCLUDING  ANY  IMPLIED WARRANTY  OF  MERCHANTABILITY,  FITNESS FOR  A
 * PARTICULAR PURPOSE  OR NON-INFRINGEMENT, ARE HEREBY  EXCLUDED. SUN AND
 * ITS  LICENSORS SHALL  NOT BE  LIABLE  FOR ANY  DAMAGES OR  LIABILITIES
 * SUFFERED BY LICENSEE  AS A RESULT OF OR  RELATING TO USE, MODIFICATION
 * OR DISTRIBUTION OF  THE SOFTWARE OR ITS DERIVATIVES.  IN NO EVENT WILL
 * SUN OR ITS  LICENSORS BE LIABLE FOR ANY LOST  REVENUE, PROFIT OR DATA,
 * OR  FOR  DIRECT,   INDIRECT,  SPECIAL,  CONSEQUENTIAL,  INCIDENTAL  OR
 * PUNITIVE  DAMAGES, HOWEVER  CAUSED  AND REGARDLESS  OF  THE THEORY  OF
 * LIABILITY, ARISING  OUT OF  THE USE OF  OR INABILITY TO  USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

package com.sun.msv.relaxns.reader.relax;

import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.iso_relax.dispatcher.IslandSchema;
import org.iso_relax.dispatcher.IslandSchemaReader;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.relax.RELAXModule;
import com.sun.msv.reader.ExpressionState;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.State;
import com.sun.msv.reader.relax.core.RELAXCoreReader;
import com.sun.msv.relaxns.grammar.ExternalAttributeExp;
import com.sun.msv.relaxns.grammar.ExternalElementExp;
import com.sun.msv.relaxns.grammar.relax.RELAXIslandSchema;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringPair;

/**
 * reads RELAX-Namespace-extended RELAX Core.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXCoreIslandSchemaReader extends RELAXCoreReader
    implements IslandSchemaReader {
    
    public RELAXCoreIslandSchemaReader(
        GrammarReaderController controller,
        SAXParserFactory parserFactory,
        ExpressionPool pool,
        String expectedTargetnamespace )
        throws SAXException,ParserConfigurationException
    {
        super(controller,parserFactory,new StateFactory(),pool,expectedTargetnamespace);
    }
    
    private static class StateFactory extends RELAXCoreReader.StateFactory {
        public State interface_(State parent,StartTagInfo tag) { return new InterfaceStateEx(); }
    }
    
    // to allow access within this package.
    protected RELAXModule getModule() { return super.module; }

    /** returns true if the given state can have "occurs" attribute. */
    protected boolean canHaveOccurs( ExpressionState state )
    {
        return super.canHaveOccurs(state) || state instanceof AnyOtherElementState;
    }

    public final IslandSchema getSchema() {
        RELAXModule m = getResult();
        if(m==null)        return null;
        else            return new RELAXIslandSchema( m, pendingAnyOtherElements );
    }
    
    public State createExpressionChildState( State parent,StartTagInfo tag )
    {
        if(! RELAXCoreNamespace.equals(tag.namespaceURI) )    return null;

        if(tag.localName.equals("anyOtherElement"))    return new AnyOtherElementState();
        return super.createExpressionChildState(parent,tag);
    }
    
    /** map from StringPair(namespace,label) to ExternalElementExp. */
    private final Map externalElementExps = new java.util.HashMap();
    private ExternalElementExp getExtElementExp( String namespace, String label )
    {
        StringPair name = new StringPair(namespace,label);
        ExternalElementExp exp = (ExternalElementExp)externalElementExps.get(name);
        if( exp!=null )    return exp;
        
        exp = new ExternalElementExp( pool, namespace, label, new LocatorImpl(getLocator()) );
        externalElementExps.put( name, exp );
        return exp;
    }
    
    protected Expression resolveElementRef( String namespace, String label )
    {
        if( namespace!=null )
            return getExtElementExp( namespace, label );
        else
            return super.resolveElementRef(namespace,label);
    }
    protected Expression resolveHedgeRef( String namespace, String label )
    {
        if( namespace!=null )
            return getExtElementExp( namespace, label );
        else
            return super.resolveHedgeRef(namespace,label);
    }
    protected Expression resolveAttPoolRef( String namespace, String label )
    {
        if( namespace!=null )
            return new ExternalAttributeExp(pool,namespace,label,new LocatorImpl(getLocator()));
        else
            return super.resolveAttPoolRef(namespace,label);
    }

    
    /**
     * set of AnyOtherElementExp object.
     * 
     * each object will be invoked to do a wrap up by bind method of IslandSchema.
     */
    protected final Set pendingAnyOtherElements = new java.util.HashSet();
}
