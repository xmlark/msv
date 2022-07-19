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

package com.sun.msv.grammar.trex;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.reader.datatype.DataTypeVocabularyMap;

/**
 * TREX grammar, which is expressed as &lt;grammar&gt; element.
 * 
 * <p>
 * The <code>exp</code> field keeps the start pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TREXGrammar extends ReferenceExp implements Grammar{
    
    static final public class RefContainer extends ReferenceContainer {
        public ReferenceExp getOrCreate( String name ) {
            return super._getOrCreate(name);
        }
        protected ReferenceExp createReference( String name ) {
            return new ReferenceExp(name);
        }
    }
    
    /** named patterns which are defined by using &lt;define&gt; element.
     * 
     * this is a map from pattern name to RefPattern object
     */
    public final RefContainer namedPatterns = new RefContainer();
    
    /** gets the start pattern.
     * 
     * The pattern defined under &lt;start&gt; element.
     * This pattern will be used to verify document element.
     */
    public Expression getTopLevel() { return this.exp; }
    
    /** ExpressionPool that is associated with this grammar */
    public final ExpressionPool pool;
    public ExpressionPool getPool() { return pool; }
    
    /** in case of nested grammar, this variable points to the parent grammar.
     *  Otherwise null.
     */
    protected final TREXGrammar parentGrammar;
    
    /**
     * gets a parent TREXGrammar.
     * 
     * In case of nested grammar, the parent grammar will be returned.
     * Otherwise, it returns null.
     */
    public final TREXGrammar getParentGrammar() { return parentGrammar; }
    
    /**
     * data type vocabularies used and defined by this grammar.
     */
    public final DataTypeVocabularyMap dataTypes = new DataTypeVocabularyMap();
    
    /**
     * 
     * @param parentGrammar
     *        this object is used to resolve &lt;ref&gt; element with parent
     *        attribute.
     */
    public TREXGrammar( ExpressionPool pool, TREXGrammar parentGrammar ) {
        super(null);
        this.pool = pool;
        this.parentGrammar = parentGrammar;
    }
    
    public TREXGrammar( ExpressionPool pool )    { this(pool,null); }
    public TREXGrammar() { this(new ExpressionPool(),null); }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
