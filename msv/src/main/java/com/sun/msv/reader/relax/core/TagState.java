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

import org.xml.sax.Locator;

import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.relax.TagClause;

/**
 * parses &lt;tag&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TagState extends ClauseState
{
    protected void endSelf( )
    {
        super.endSelf();
        
        final String name = startTag.getAttribute("name");
        String role = startTag.getAttribute("role");
        if(role==null)    role=name;    // role defaults to name
        
        if(name==null)
        {
            reader.reportError(RELAXCoreReader.ERR_MISSING_ATTRIBUTE, "tag","name");
            return;
        }
        
        TagClause c = getReader().module.tags.getOrCreate(role);
        
        if(c.nameClass!=null)
        {
            // someone has already initialized this clause.
            // this happens when more than one tag element declares the same role.
            reader.reportError(
                new Locator[]{getReader().getDeclaredLocationOf(c),location},
                RELAXCoreReader.ERR_MULTIPLE_TAG_DECLARATIONS, new Object[]{role} );
            // recover from error by ignoring previous tag declaration
        }
        
        c.nameClass = new SimpleNameClass(
            getReader().module.targetNamespace,
            name );
        
        c.exp = exp;    // exp holds a sequence of AttributeExp
        getReader().setDeclaredLocationOf(c);    // remember where this tag is declared
        
        return;
    }
}
