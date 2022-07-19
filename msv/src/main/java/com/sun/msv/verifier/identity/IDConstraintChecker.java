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

package com.sun.msv.verifier.identity;

import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.relaxng.datatype.Datatype;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.sun.msv.grammar.xmlschema.ElementDeclExp;
import com.sun.msv.grammar.xmlschema.IdentityConstraint;
import com.sun.msv.grammar.xmlschema.KeyRefConstraint;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.util.LightStack;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.verifier.Acceptor;
import com.sun.msv.verifier.ErrorInfo;
import com.sun.msv.verifier.ValidityViolation;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.regexp.xmlschema.XSREDocDecl;

/**
 * Verifier with XML Schema-related enforcement.
 * 
 * <p>
 * This class can be used in the same way as {@link Verifier}.
 * This class also checks XML Schema's identity constraint.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IDConstraintChecker extends Verifier {

    public IDConstraintChecker( XMLSchemaGrammar grammar, ErrorHandler errorHandler ) {
        super(new XSREDocDecl(grammar),errorHandler);
        this.grammar = grammar;
    }
    
    /** the grammar object against which we are validating. */
    protected final XMLSchemaGrammar grammar;
    
    /** active mathcers. */
    protected final Vector matchers = new Vector();
    
    protected void add( Matcher matcher ) {
        matchers.add(matcher);
    }
    protected void remove( Matcher matcher ) {
        matchers.remove(matcher);
    }
    
    /**
     * a map from <code>SelectorMatcher</code> to set of <code>KeyValue</code>s.
     * 
     * One SelectorMatcher correponds to one scope of the identity constraint.
     */
    private final Map keyValues = new java.util.HashMap();
    
    /**
     * a map from keyref <code>SelectorMatcher</code> to key/unique
     * <code>SelectorMatcher</code>.
     * 
     * Given a keyref scope, this map stores which key scope should it refer to.
     */
    private final Map referenceScope = new java.util.HashMap();
    
    /**
     * a map from <code>IdentityConstraint</code> to a <code>LightStack</code> of
     * <code>SelectorMatcher</code>.
     * 
     * Each stack top keeps the currently active scope for the given IdentityConstraint.
     */
    private final Map activeScopes = new java.util.HashMap();
    protected SelectorMatcher getActiveScope( IdentityConstraint c ) {
        LightStack s = (LightStack)activeScopes.get(c);
        if(s==null)    return null;
        if(s.size()==0)    return null;
        return (SelectorMatcher)s.top();
    }
    protected void pushActiveScope( IdentityConstraint c, SelectorMatcher matcher ) {
        LightStack s = (LightStack)activeScopes.get(c);
        if(s==null)
            activeScopes.put(c,s=new LightStack());
        s.push(matcher);
    }
    protected void popActiveScope( IdentityConstraint c, SelectorMatcher matcher ) {
        LightStack s = (LightStack)activeScopes.get(c);
        if(s==null)
            // since it's trying to pop, there must be a non-empty stack.
            throw new Error();
        if(s.pop()!=matcher)
            // trying to pop a non-active scope.
            throw new Error();
    }
        
    
    /**
     * adds a new KeyValue to the value set.
     * @return true        if this is a new value.
     */
    protected boolean addKeyValue( SelectorMatcher scope, KeyValue value ) {
        Set keys = (Set)keyValues.get(scope);
        if(keys==null)
            keyValues.put(scope, keys = new java.util.HashSet());
        return keys.add(value);
    }
    /**
     * gets the all <code>KeyValue</code>s that were added within the specified scope.
     */
    protected KeyValue[] getKeyValues( SelectorMatcher scope ) {
        Set keys = (Set)keyValues.get(scope);
        if(keys==null)
            return new KeyValue[0];
        return (KeyValue[])keys.toArray(new KeyValue[keys.size()]);
    }
    
    
    
    public void startDocument() throws SAXException {
        super.startDocument();
        keyValues.clear();
    }
    
    public void endDocument() throws SAXException {
        super.endDocument();
        
        // keyref check
        Map.Entry[] scopes = (Map.Entry[])
            keyValues.entrySet().toArray(new Map.Entry[keyValues.size()]);
        if(com.sun.msv.driver.textui.Debug.debug)
            System.out.println("key/keyref check: there are "+keyValues.size()+" scope(s)");
        
        for( int i=0; i<scopes.length; i++ ) {
            final SelectorMatcher key = (SelectorMatcher)scopes[i].getKey();
            final Set value = (Set)scopes[i].getValue();
            
            if( key.idConst instanceof KeyRefConstraint ) {
                // get the set of corresponding keys.
                Set keys = (Set)keyValues.get( referenceScope.get(key) );
                KeyValue[] keyrefs = (KeyValue[])
                    value.toArray(new KeyValue[value.size()]);
                
                for( int j=0; j<keyrefs.length; j++ ) {
                    if( keys==null || !keys.contains(keyrefs[j]) )
                        // this keyref doesn't have a corresponding key.
                        reportError( keyrefs[j].locator, null, ERR_UNDEFINED_KEY,
                            new Object[]{
                                key.idConst.namespaceURI,
                                key.idConst.localName} );
                }
            }
        }
    }
    
    protected void onNextAcceptorReady( StartTagInfo sti, Acceptor next ) throws SAXException {
        
        // call matchers
        int len = matchers.size();
        for( int i=0; i<len; i++ ) {
            Matcher m = (Matcher)matchers.get(i);
            m.startElement(sti.namespaceURI,sti.localName);
        }
        
        // introduce newly found identity constraints.
        Object e = next.getOwnerType();
        if( e instanceof ElementDeclExp.XSElementExp ) {
            ElementDeclExp.XSElementExp exp = (ElementDeclExp.XSElementExp)e;
            if( exp.identityConstraints!=null ) {
                int m = exp.identityConstraints.size();
                for( int i=0; i<m; i++ )
                    add( new SelectorMatcher( this,
                            (IdentityConstraint)exp.identityConstraints.get(i),
                            sti.namespaceURI, sti.localName ) );
                
                // SelectorMathcers will register themselves as active scopes 
                // in their constructor.
                
                // augment the referenceScope field by adding newly introduced keyrefs.
                for( int i=0; i<m; i++ ) {
                    IdentityConstraint c = (IdentityConstraint)
                        exp.identityConstraints.get(i);
                    if(c instanceof KeyRefConstraint) {
                        SelectorMatcher keyScope =
                            getActiveScope( ((KeyRefConstraint)c).key );
                        if(keyScope==null)
                            ;    // there is no active scope of the key scope now.
                        
                        referenceScope.put(
                            getActiveScope(c),
                            keyScope );
                    }
                }
            }
        }
    }

    protected Datatype[] feedAttribute( Acceptor child, String uri, String localName, String qName, String value ) throws SAXException {
        Datatype[] result = super.feedAttribute( child, uri, localName, qName, value );
        
        final int len = matchers.size();
        // call matchers for attributes.
        for( int i=0; i<len; i++ ) {
            Matcher m = (Matcher)matchers.get(i);
            m.onAttribute( uri, localName, value, 
                (result==null || result.length==0)?null:result[0] );
        }
        
        return result;
    }

    
    
    public void characters( char[] buf, int start, int len ) throws SAXException {
        super.characters(buf,start,len);
        
        int m = matchers.size();
        for( int i=0; i<m; i++ )
            ((Matcher)matchers.get(i)).characters(buf,start,len);
    }


    public void endElement( String namespaceUri, String localName, String qName )
                                throws SAXException {
        super.endElement(namespaceUri,localName,qName);
        
        // getLastCharacterType may sometimes return null. For example,
        // 1) this element should be empty and there was only whitespace characters.
        Datatype dt;
        Datatype[] lastType = getLastCharacterType();
        if( lastType==null || lastType.length==0 )    dt = null;
        else                                        dt = getLastCharacterType()[0];
            
        // call matchers
        int len = matchers.size();
        for( int i=len-1; i>=0; i-- ) {
            // Matcher may remove itself from the vector.
            // Therefore, to make it work correctly, we have to
            // enumerate Matcher in reverse direction.
            ((Matcher)matchers.get(i)).endElement( dt );
        }
    }
    

    
    /** reports an error. */
    protected void reportError( ErrorInfo ei, String propKey, Object[] args ) throws SAXException {
        // use the current location.
        reportError( getLocator(), ei, propKey, args );
    }
    
    protected void reportError( Locator loc, ErrorInfo ei, String propKey, Object[] args ) throws SAXException {
        hadError = true;
        errorHandler.error( new ValidityViolation( loc,
                localizeMessage(propKey,args), ei ) );
    }
    
    public static String localizeMessage( String propertyName, Object arg ) {
        return localizeMessage( propertyName, new Object[]{arg} );
    }

    public static String localizeMessage( String propertyName, Object[] args ) {
        String format = java.util.ResourceBundle.getBundle(
            "com.sun.msv.verifier.identity.Messages").getString(propertyName);
        
        return java.text.MessageFormat.format(format, args );
    }
    
    public static final String ERR_UNMATCHED_KEY_FIELD =
        "IdentityConstraint.UnmatchedKeyField";    // arg :3
    public static final String ERR_NOT_UNIQUE =
        "IdentityConstraint.NotUnique"; // arg:2
    public static final String ERR_NOT_UNIQUE_DIAG =
        "IdentityConstraint.NotUnique.Diag";    // arg:2
    public static final String ERR_DOUBLE_MATCH =
        "IdentityConstraint.DoubleMatch"; // arg:3
    public static final String ERR_UNDEFINED_KEY =
        "IdentityConstraint.UndefinedKey"; // arg:2 
    
}
