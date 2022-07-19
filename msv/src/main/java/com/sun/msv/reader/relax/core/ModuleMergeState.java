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

package com.sun.msv.reader.relax.core;


/**
 * Used to parse module.
 * 
 * As stand-alone, this state is used to parse a module included by another module.
 * By a base class, this state is used to parse a "head" module.
 * 
 * This class checks consistency between targetNamespace attribute
 * and the namespace specified by its caller (grammar/module).
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ModuleMergeState extends DivInModuleState
{
    protected ModuleMergeState( String expectedTargetNamespace )
    {
        this.expectedTargetNamespace = expectedTargetNamespace;
    }
    
    /** expected targetNamespace for this module.
     * 
     * null indicates that module must have targetNamespace attribute.
     * 
     * <p>
     * If RELAX module has 'targetNamespace' attribute, then its value
     * must be equal to this value, or this value must be null.
     * 
     * <p>
     * If RELAX module doesn't have the attribute, then this value is
     * used as the target namespace. If this value is null, then it is
     * an error.
     */
    protected final String expectedTargetNamespace;

    /**
     * computed targetNamespace.
     * 
     * actual target namespace depends on expected target namespace
     * and module. this field is set in startSelf method.
     */
    protected String targetNamespace;
    
    protected void startSelf()
    {
        super.startSelf();
        
        {// check relaxCoreVersion
            final String coreVersion = startTag.getAttribute("relaxCoreVersion");
            if( coreVersion==null )
                reader.reportWarning( RELAXCoreReader.ERR_MISSING_ATTRIBUTE, "module", "relaxCoreVersion" );
            else
            if(!"1.0".equals(coreVersion))
                reader.reportWarning( RELAXCoreReader.WRN_ILLEGAL_RELAXCORE_VERSION, coreVersion );
        }
        
        targetNamespace = startTag.getAttribute("targetNamespace");
        
        if(targetNamespace!=null)
        {
            // check accordance with expected namespace
            if( expectedTargetNamespace!=null
            &&  !expectedTargetNamespace.equals(targetNamespace) )
            {// error
                reader.reportError( RELAXCoreReader.ERR_INCONSISTENT_TARGET_NAMESPACE,
                                    targetNamespace, expectedTargetNamespace );
                // recover by ignoring one specified in the module
                targetNamespace = expectedTargetNamespace;
            }
        }
        else
        {// no targetnamespace attribute is given.
            if( expectedTargetNamespace==null )
            {
                reader.reportError( RELAXCoreReader.ERR_MISSING_TARGET_NAMESPACE );
                targetNamespace = "";    // recover by assuming the default namespace
            }
            else
                targetNamespace = expectedTargetNamespace;
        }
    }
}
