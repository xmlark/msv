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

package com.sun.msv.reader.trex;

import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.reader.SequenceState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * Base implementation for ElementState and AttributeState
 * 
 * This class collects one name class and patterns
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class NameClassAndExpressionState extends SequenceState implements NameClassOwner {
    protected NameClass nameClass = null;

    /**
     * gets namespace URI to which this declaration belongs
     */
    protected String getNamespace() {
        // usually, propagated "ns" attribute should be used
        return ((TREXBaseReader) reader).targetNamespace;
    }

    protected void startSelf() {
        super.startSelf();
        // if name attribtue is specified, use it.
        final String name = startTag.getCollapsedAttribute("name");

        if (name == null)
            return;

        final int idx = name.indexOf(':');
        if (idx != -1) {
            // QName is specified. resolve this prefix.
            final String[] s = reader.splitQName(name);
            if (s == null) {
                reader.reportError(TREXBaseReader.ERR_UNDECLARED_PREFIX, name);
                // recover by using a dummy name
                nameClass = new SimpleNameClass("", name);
            } else
                nameClass = new SimpleNameClass(s[0], s[1]);
        } else
            nameClass = new SimpleNameClass(getNamespace(), name);
    }
    
    public void onEndChild(NameClass p) {
        if (nameClass != null) // name class has already specified
            reader.reportError(TREXBaseReader.ERR_MORE_THAN_ONE_NAMECLASS);
        nameClass = p;
    }

    protected State createChildState(StartTagInfo tag) {
        if (nameClass == null) // nameClass should be specified before content model.
            {
            State nextState = ((TREXBaseReader) reader).createNameClassChildState(this, tag);
            if (nextState != null)
                return nextState;

            // to provide better error message, analyze the situation further.
            // users tend to forget to supply nameClass and name attribute.

            nextState = reader.createExpressionChildState(this, tag);
            if (nextState != null) {
                // OK. tag is recognized as an content model.
                // so probably this user forgot to specify name class.
                // report so and recover by assuming some NameClass
                reader.reportError(TREXBaseReader.ERR_MISSING_CHILD_NAMECLASS);
                nameClass = NameClass.ALL;
                return nextState;
            } else
                // probably this user made a typo. let the default handler reports an error
                return null;
        } else
            return reader.createExpressionChildState(this, tag);
    }

    protected void endSelf() {
        if (nameClass == null) {
            // name class is missing
            reader.reportError(TREXBaseReader.ERR_MISSING_CHILD_NAMECLASS);
            nameClass = NameClass.ALL;
        }

        super.endSelf();
    }
}
