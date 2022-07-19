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

package com.sun.msv.reader.relax.core;

import org.relaxng.datatype.DatatypeException;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.State;
import com.sun.msv.reader.datatype.xsd.FacetStateParent;
import com.sun.msv.reader.datatype.xsd.XSTypeIncubator;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;elementRule&gt; with 'type' attribute.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ElementRuleWithTypeState extends ElementRuleBaseState implements FacetStateParent
{
    protected XSTypeIncubator incubator;
    
    public XSTypeIncubator getIncubator()    { return incubator; }
    
    protected void startSelf() {
        super.startSelf();

        final RELAXCoreReader reader = (RELAXCoreReader)this.reader;
        
        // existance of type attribute has already checked before
        // this state is created.
        incubator = reader.resolveXSDatatype(startTag.getAttribute("type"))
            .createIncubator();
    }
    
    protected Expression getContentModel() {
        try {
            return incubator.derive(null,null);
        } catch( DatatypeException e ) {
            // derivation failed
            reader.reportError( e, RELAXCoreReader.ERR_BAD_TYPE, e.getMessage() );
            // recover by using harmless expression. anything will do.
            return Expression.anyString;
        }
    }
    
    protected State createChildState( StartTagInfo tag ) {
        State next = getReader().createFacetState(this,tag);
        if(next!=null)        return next;            // facets
        
        return super.createChildState(tag);            // or delegate to the base class
    }
}
