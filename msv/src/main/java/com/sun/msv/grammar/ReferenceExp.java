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

package com.sun.msv.grammar;

/**
 * Reference to the other expression.
 * 
 * <p>
 * In RELAX grammar, this class is used as a base class of elementRule reference
 * and hedgeRule reference.
 * TREX uses this class directly.
 * 
 * <p>
 * This object is created and controlled by TREXGrammar/RELAXModule object,
 * rather than ExpressionPool. Therefore, this object is not a subject to unification.
 * 
 * <p>
 * This class can be derived. In fact, many classes derive this class.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ReferenceExp extends Expression {
    /** child expression. Due to the possible forward reference,
     * this variable is not available when the object is instanciated.
     * 
     * Actual expression will be set once if the definition is parsed.
     */
    public Expression exp = null;
    
    /** name of the referenced expression.
     * 
     * can be null for anonymously referenced expression.
     */
    public final String name;
    
    public ReferenceExp( String name ) {
        this.name = name;
    }

    public ReferenceExp( String name, Expression exp ) {
        this(name);
        this.exp = exp;
    }

    protected final int calcHashCode() {
        return System.identityHashCode(this);
    }
    
    /**
     * checks if this ReferenceExp is properly defined.
     * this method is used to detect undeclared definitions.
     * Derived classes can override this method.
     */
    public boolean isDefined() {
        return exp!=null;
    }
    
    public boolean equals( Object o ) {
        return this==o;
    }
    
    protected boolean calcEpsilonReducibility() {
        if(exp==null)
//            // actual expression is not supplied yet.
//            // actual definition of the referenced expression must be supplied
//            // before any computation over the grammar.
//            throw new Error();    // assertion failed.
            return false;
        // this method can be called while parsing a grammar.
        // in that case, epsilon reducibility is just used for approximation.
        // therefore we can safely return false.
        
        return exp.isEpsilonReducible();
    }
    
    // derived class must be able to behave as a ReferenceExp
    public final Object visit( ExpressionVisitor visitor )                { return visitor.onRef(this); }
    public final Expression visit( ExpressionVisitorExpression visitor ){ return visitor.onRef(this); }
    public final boolean visit( ExpressionVisitorBoolean visitor )        { return visitor.onRef(this); }
    public final void visit( ExpressionVisitorVoid visitor )            { visitor.onRef(this); }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
