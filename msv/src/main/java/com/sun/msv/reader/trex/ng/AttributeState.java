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

package com.sun.msv.reader.trex.ng;

import com.sun.msv.grammar.AnyNameClass;
import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.NameClassVisitor;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.NotNameClass;
import com.sun.msv.grammar.SimpleNameClass;

/**
 * parses &lt;attribute&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeState extends com.sun.msv.reader.trex.AttributeState
{
    private static final String infosetURI = "http://www.w3.org/2000/xmlns";
    protected void endSelf() {
        super.endSelf();
        
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        
        reader.restrictionChecker.checkNameClass(nameClass);
        
        nameClass.visit( new NameClassVisitor() {
            public Object onAnyName( AnyNameClass nc ) { return null; }
            public Object onSimple(SimpleNameClass nc) {
                if(nc.namespaceURI.equals(infosetURI))
                    reader.reportError( RELAXNGReader.ERR_INFOSET_URI_ATTRIBUTE );
                
                if(nc.namespaceURI.length()==0 && nc.localName.equals("xmlns"))
                    reader.reportError( RELAXNGReader.ERR_XMLNS_ATTRIBUTE );
                return null;
            }
            public Object onNsName( NamespaceNameClass nc ) {
                if(nc.namespaceURI.equals(infosetURI))
                    reader.reportError( RELAXNGReader.ERR_INFOSET_URI_ATTRIBUTE );
                return null;
            }
            public Object onNot( NotNameClass nc ) {
                nc.child.visit(this);
                return null;
            }
            public Object onDifference( DifferenceNameClass nc ) {
                nc.nc1.visit(this); nc.nc2.visit(this);
                return null;
            }
            public Object onChoice( ChoiceNameClass nc ) {
                nc.nc1.visit(this); nc.nc2.visit(this);
                return null;
            }
        });
    }
}
