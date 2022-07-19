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

package com.sun.msv.relaxns.grammar.relax;

import java.util.Iterator;
import java.util.StringTokenizer;

import org.iso_relax.dispatcher.ElementDecl;
import org.iso_relax.dispatcher.SchemaProvider;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;

import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.NotNameClass;

/**
 * place holder for &lt;anyOtherElement&gt; of RELAX.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AnyOtherElementExp extends ElementExp implements ElementDecl {
    
    public NameClass getNameClass() { return nameClass; }
    public String getName() { return "anyOtherElement:"+nameClass; }

    /**
     * this name class matches all the namespaces that are accepted by this anyOtherElement.
     * this field is set by bind method.
     */
    public NameClass nameClass;
    
    /**
     * where did this reference is written in the source file.
     * can be set to null (to reduce memory usage) at anytime.
     */
    public transient Locator source;
    
    public final String includeNamespace;
    public final String excludeNamespace;
    
    /**
     * creates "skelton" of AnyOtherElement.
     * 
     * pseudo content model and name class must be supplied separately.
     */
    public AnyOtherElementExp( Locator loc,
        String includeNamespace, String excludeNamespace ) {
        // set content model to nullSet
        // to make this elementExp accept absolutely nothing.
        
        // "ignoreUndeclaredAttributes" parameter is meaningless here
        // because validation of this element is performed by
        // AnyOtherElementVerifier and it doesn't care about this flag.
        super(Expression.nullSet,true);
        
        this.source = loc;
        this.includeNamespace = includeNamespace;
        this.excludeNamespace = excludeNamespace;
        
        if( includeNamespace==null && excludeNamespace==null )
            throw new IllegalArgumentException();
        if( includeNamespace!=null && excludeNamespace!=null )
            throw new IllegalArgumentException();
    }
    
    /** creates pseudo content model and name class.
     * 
     * This function is called by RELAXIslandSchema object.
     * Therefore, line information is not automatically available
     * when reporting error.
     * Implementator should keep this in mind and manually pass Locator to reportError method.
     */
    protected void wrapUp( Grammar owner, Expression pseudoContentModel, SchemaProvider provider, ErrorHandler errorHandler )
                            throws SAXException {
        StringTokenizer st;
        if( includeNamespace!=null )
            st = new StringTokenizer(includeNamespace);
        else
            st = new StringTokenizer(excludeNamespace);
        
        NameClass nc =null;
        
        while(st.hasMoreTokens()) {
            String uri = st.nextToken();
            
            if(uri.equals("##local"))    uri="";
            
            if( provider.getSchemaByNamespace(uri)!=null ) {
                // one cannot specify defined URI.
                errorHandler.warning( new SAXParseException(
                    Localizer.localize( Localizer.WRN_ANYOTHER_NAMESPACE_IGNORED, uri ), source ) );
                continue;
            }
            
            NamespaceNameClass nsnc = new NamespaceNameClass(uri);
            if( nc==null )    nc = nsnc;
            else            nc = new ChoiceNameClass(nc,nsnc);
        }
        
        if( excludeNamespace!=null )
        {
            // in case of 'excludeNamespace',
            // all defined namespace is also considered as illegal.
            
            Iterator itr = provider.iterateNamespace();
            while( itr.hasNext() )
            {
                NamespaceNameClass nsnc = new NamespaceNameClass( (String)itr.next() );
                if( nc==null )    nc = nsnc;
                else            nc = new ChoiceNameClass(nc,nsnc);
            }
            
            nc = new NotNameClass(nc);
        }
        
        this.nameClass = nc;
        
        // provide pseudo-content model.
        this.contentModel = 
            owner.getPool().createMixed(
                owner.getPool().createZeroOrMore(
                    owner.getPool().createChoice(this,pseudoContentModel) ) );
    }
    
    public boolean getFeature( String feature ) throws SAXNotRecognizedException {
        throw new SAXNotRecognizedException(feature);
    }
    
    public Object getProperty( String property ) throws SAXNotRecognizedException {
        throw new SAXNotRecognizedException(property);
    }
}
