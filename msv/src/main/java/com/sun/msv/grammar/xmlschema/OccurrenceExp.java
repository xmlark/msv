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

package com.sun.msv.grammar.xmlschema;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.OtherExp;

/**
 * Used to mark a occurrence constraint which cannot
 * be easily represented by primitive expressions.
 * 
 * <p>
 * This expression is just a marker, and the exp field
 * of this instance still contains the precise expression
 * of the occurrence constraint.
 * 
 * <p>
 * For example, if A is maxOccurs=5 and minOccurs=3,
 * then the exp field of this instance will be:
 * <code>A,A,A,(A,A?)?</code>, the maxOccurs field
 * will be 5, the minOccurs field will be 3, and
 * the itemExp field will hold a reference to <code>A</code>.
 * 
 * <p>
 * Note that MSV doesn't using this marker by itself.
 * It is intended to help other applications that use
 * the AGM of MSV.
 * 
 * <p>
 * Also note that this expression will not
 * be used in the following cases to avoid excessive allocation
 * of this expression:
 * 
 * <ul>
 *  <li>when maxOccurs=unbounded and minOccurs is 1 or 0
 *  <li>when maxOccurs=1
 * </ul>
 * 
 * <p>
 * Those cases can be expressed quite nicely with existing primitives
 * So the client shouldn't find it difficuult to process them.
 * I appreciate any feedback on this issue.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class OccurrenceExp extends OtherExp {
    public OccurrenceExp(
        Expression preciseExp,
        int maxOccurs, int minOccurs, Expression itemExp ) {
        super(preciseExp);
        this.maxOccurs = maxOccurs;
        this.minOccurs = minOccurs;
        this.itemExp = itemExp;
    }
    
    /** Maximum occurence. -1 to indicate "unbounded" */
    public final int maxOccurs;
    /** Minimum occurence. */
    public final int minOccurs;
    
    /** The unit of repetition. */
    public final Expression itemExp;
    
    /** Obtains a string representation suitable for quick debugging. */
    public String toString() {
        return itemExp.toString()+"["+minOccurs+","+
            (maxOccurs==-1?"inf":String.valueOf(maxOccurs))+"]";
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
