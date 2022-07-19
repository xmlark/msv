/*
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
