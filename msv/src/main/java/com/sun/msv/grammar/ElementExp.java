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

package com.sun.msv.grammar;

/**
 * Element declaration.
 * 
 * For RELAX, this is a base implementation of 'elementRule' declaration.
 * For TREX, this is a base implementation of 'element' pattern.
 * 
 * Each grammar must/can provide only one concrete implementation.
 * Therefore, they cannot override visit method.
 * 
 * <p>
 * This class can be extended.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ElementExp extends Expression implements NameClassAndExpression {
    /** content model of this element declaration. */
    public Expression contentModel;
    public final Expression getContentModel() { return contentModel; }
    
    /** a flag that indicates undeclared attributes should be ignored. */
    public boolean ignoreUndeclaredAttributes;
    
    /** obtains a constraint over tag name.
     * 
     * ElementExp is cannot be shared because NameClass has to be mutable
     * to absorb the difference of RELAX and TREX.
     * 
     * In case of TREX, name class will be determined when parsing ElementExp itself.
     * Thus effectively it's immutable.
     * 
     * In case of RELAX, name class will be determined when its corresponding Clause
     * object is parsed. 
     */
    abstract public NameClass getNameClass();
    
    public ElementExp( Expression contentModel, boolean ignoreUndeclaredAttributes ) {
        // since ElementExp is not unified, no two ElementExp objects are considered equal.
        // therefore essentially any value can be used as hash code.
        // that's why this code works even when content model may be changed later.
        super( contentModel.hashCode() );
        this.contentModel = contentModel;
        this.ignoreUndeclaredAttributes = ignoreUndeclaredAttributes;
    }
    
    protected final int calcHashCode() {
        return contentModel.hashCode();
    }

    public final boolean equals( Object o ) {
        return this==o;
    }

    public final Object visit( ExpressionVisitor visitor )                    { return visitor.onElement(this); }
    public final Expression visit( ExpressionVisitorExpression visitor )    { return visitor.onElement(this); }
    public final boolean visit( ExpressionVisitorBoolean visitor )            { return visitor.onElement(this); }
    public final void visit( ExpressionVisitorVoid visitor )                { visitor.onElement(this); }
    
    protected final boolean calcEpsilonReducibility()
    { return false; }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
