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

package com.sun.msv.datatype.xsd;

import com.sun.xml.util.XmlChars;

/**
 * This class contains static methods used to determine whether identifiers
 * may appear in certain roles in XML documents.  Such methods are used
 * both to parse and to create such documents.
 *
 * @version 1.4
 * @author David Brownell
 */
public class XmlNames 
{
    private XmlNames () { }


    /**
     * Returns true if the value is a legal XML name.
     *
     * @param value the string being tested
     */
    public static boolean isName (String value)
    {
        if( value==null || value.length()==0 )
            return false;
    

    char c = value.charAt (0);
    if (!XmlChars.isLetter (c) && c != '_' && c != ':')
        return false;
    for (int i = 1; i < value.length (); i++)
        if (!XmlChars.isNameChar (value.charAt (i)))
        return false;
    return true;
    }

    /**
     * Returns true if the value is a legal "unqualified" XML name, as
     * defined in the XML Namespaces proposed recommendation.
     * These are normal XML names, except that they may not contain
     * a "colon" character.
     *
     * @param value the string being tested
     */
    public static boolean isUnqualifiedName (String value)
    {
        if (value == null || value.length() == 0)
            return false;

    char c = value.charAt (0);
    if (!XmlChars.isLetter (c) && c != '_')
        return false;
    for (int i = 1; i < value.length (); i++)
        if (!XmlChars.isNCNameChar (value.charAt (i)))
        return false;
    return true;
    }

    /**
     * Returns true if the value is a legal "qualified" XML name, as defined
     * in the XML Namespaces proposed recommendation.  Qualified names are
     * composed of an optional prefix (an unqualified name), followed by a
     * colon, and a required "local part" (an unqualified name).  Prefixes are
     * declared, and correspond to particular URIs which scope the "local
     * part" of the name.  (This method cannot check whether the prefix of a
     * name has been declared.)
     *
     * @param value the string being tested
     */
    public static boolean isQualifiedName (String value)
    {
        if (value == null || value.length() == 0)
            return false;

        // [6] QName ::= (Prefix ':')? LocalPart
        // [7] Prefix ::= NCName
        // [8] LocalPart ::= NCName

    int    first = value.indexOf (':');

        // no Prefix, only check LocalPart
        if (first <= 0)
            return isUnqualifiedName (value);

        // Prefix exists, check everything

    int    last = value.lastIndexOf (':');
    if (last != first)
        return false;
    
    return isUnqualifiedName (value.substring (0, first))
        && isUnqualifiedName (value.substring (first + 1));
    }

    /**
     * This method returns true if the identifier is a "name token"
     * as defined in the XML specification.  Like names, these
     * may only contain "name characters"; however, they do not need
     * to have letters as their initial characters.  Attribute values
     * defined to be of type NMTOKEN(S) must satisfy this predicate.
     *
     * @param token the string being tested
     */
    public static boolean isNmtoken(String token)
    {
        if (token == null || token.length() == 0)    return false;

        int    length = token.length ();

        for (int i = 0; i < length; i++)
            if (!XmlChars.isNameChar (token.charAt (i)))
                return false;
        return true;
    }


    /**
     * This method returns true if the identifier is a "name token" as
     * defined by the XML Namespaces proposed recommendation.
     * These are like XML "name tokens" but they may not contain the
     * "colon" character.
     *
     * @see #isNmtoken(String)
     *
     * @param token the string being tested
     */
    public static boolean isNCNmtoken (String token)
    {
    return isNmtoken (token) && token.indexOf (':') < 0;
    }
    
    public static boolean isNCName( String token )
    {
        return isName(token) && token.indexOf(':') < 0;
    }
}
