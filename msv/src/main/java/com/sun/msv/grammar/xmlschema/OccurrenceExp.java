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
