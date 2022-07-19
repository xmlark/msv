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

package com.sun.msv.grammar.relaxng;

import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.trex.TREXGrammar;

/**
 * Grammar for RELAX NG (with DTD compatibility annotation).
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXNGGrammar extends TREXGrammar {

    public RELAXNGGrammar( ExpressionPool pool, TREXGrammar parentGrammar ) {
        super(pool,parentGrammar);
    }
    public RELAXNGGrammar( ExpressionPool pool )    { super(pool); }
    public RELAXNGGrammar() { super(); }

    /**
     * the compatibility property of the ID/IDREF feature.
     * 
     * This flag is set to true if this schema is compatible in terms of
     * ID/IDREF, as defined in the spec.
     */
    public boolean isIDcompatible = true;
    
    /**
     * the compatibility property of the default attribute feature.
     * 
     * This flag is set to true if this schema is compatible in terms of
     * the default attribute value feature, as defined in the spec.
     * 
     * <p>
     * Note that the currently we don't support infoset augmentation.
     */
    public boolean isDefaultAttributeValueCompatible = true;
    
    /**
     * the compatibility property of the annotation feature.
     * 
     * This flag is set to true if this schema is compatible in terms of
     * the annotation feature, as defined in the spec.
     */
    public boolean isAnnotationCompatible = true;
    
    
    
/*
    /**
     * add an entry to the default value table.
     * 
     * @return
     *        <b>false</b> if the default attribute value for this element and attribute
     *        was already specified. <b>true</b> if this is the first time.
     */
/*    public final boolean addDefaultValue(
            String elementNamespaceURI, String elementLocalName,
            String attributeNamespaceURI, String attributeLocalName,
            String value ) {
        
        if(defaultValues==null)
            defaultValues = new HashMap();
        
        return defaultValues.put(
            new ElemAttrNamePair(elementNamespaceURI,elementLocalName,attributeNamespaceURI,attributeLocalName),
            value )==null;
    }
    
    /**
     * checks if this grammar has any attribute default value.
     */
/*    public final boolean hasDefaultValue() {
        return defaultValues!=null && defaultValues.size()!=0;
    }
    
    /**
     * gets the list of defaultable attributes of the given element.
     * 
     * @param (namespaceURI,localName)
     *        this pair designates the name of the element.
     * 
     * @return
     *        if there is no defaultable attribute, it returns empty array.
     *        this method never returns null.
     */
/*    public final String[] getDefaultableAttributes( String namespaceURI, String localName ) {
        
    }
    
    // TODO: this can be implemented much efficiently, I guess.
    
    // map from NamePair (element name) to
    //        (a map from NamePair (attribute name) to attribute value)
    private Map defaultValues;
*/
/*    
    public static final int KEYTYPE_NONE    =0;
    public static final int KEYTYPE_ID        =1;
    public static final int KEYTYPE_IDREF    =2;
    
    public final int getAttributeKeyType(
            String elementNamespaceURI, String elementLocalName,
            String attributeNamespaceURI, String attributeLocalName,
            String value ) {
        
    }
    
*/
        
    // serialization support
    private static final long serialVersionUID = 1;    
}
