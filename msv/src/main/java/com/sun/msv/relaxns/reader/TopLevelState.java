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

package com.sun.msv.relaxns.reader;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.State;
import com.sun.msv.reader.relax.HedgeRuleBaseState;
import com.sun.msv.reader.relax.core.RELAXCoreReader;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;topLevel&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TopLevelState extends HedgeRuleBaseState
{
    protected void endSelf( Expression contentModel ) {
        ((RELAXNSReader)reader).grammar.topLevel = contentModel;
    }

    protected State createChildState( StartTagInfo tag )
    {
        // user tends to forget to specify RELAX Core namespace for
        // topLevel elements. see if this is the case
        if( tag.namespaceURI.equals(RELAXNSReader.RELAXNamespaceNamespace))
        {// bingo.
            reader.reportError( RELAXNSReader.ERR_TOPLEVEL_PARTICLE_MUST_BE_RELAX_CORE );
            // return null so that user will also receive "malplaced element" error.
            return null;
        }
        
        return super.createChildState(tag);
    }

    protected boolean isGrammarElement( StartTagInfo tag ) {
        // children of <topLevel> must be RELAXCore.
        if( tag.namespaceURI.equals(RELAXCoreReader.RELAXCoreNamespace) )
            return true;
        
        // for better error message, allow RELAX Namespace elements.
        // this error is handled at createChildState method.
        if( tag.namespaceURI.equals(RELAXNSReader.RELAXNamespaceNamespace) )
            return true;
        
        return false;
    }
}
