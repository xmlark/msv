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

package com.sun.msv.grammar.util;

import org.relaxng.datatype.Datatype;

import com.sun.msv.grammar.IDContextProvider;
import com.sun.msv.grammar.IDContextProvider2;
import com.sun.msv.verifier.regexp.StringToken;

/**
 * Wraps {@link IDContextProvider} so that it can be used
 * where {@link IDContextProvider2} is expected.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class IDContextProviderWrapper implements IDContextProvider2 {
    private final IDContextProvider core;
    
    public static IDContextProvider2 create( IDContextProvider core ) {
        if(core==null) return null;
        else            return new IDContextProviderWrapper(core);
    }
    
    private IDContextProviderWrapper( IDContextProvider _core ) {
        this.core = _core;
    }
    
    public String getBaseUri() {
        return core.getBaseUri();
    }

    public boolean isNotation(String arg0) {
        return core.isNotation(arg0);
    }

    public boolean isUnparsedEntity(String arg0) {
        return core.isUnparsedEntity(arg0);
    }

    public void onID(Datatype datatype, StringToken token) {
        core.onID(datatype, token.literal);
    }

    public String resolveNamespacePrefix(String arg0) {
        return core.resolveNamespacePrefix(arg0);
    }

}
