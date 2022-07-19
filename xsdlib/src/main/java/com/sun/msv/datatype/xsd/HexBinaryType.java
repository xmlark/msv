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

import com.sun.msv.datatype.SerializationContext;
import org.relaxng.datatype.ValidationContext;

/**
 * "hexBinary" type.
 * 
 * type of the value object is {@link BinaryValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#hexBinary for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class HexBinaryType extends BinaryBaseType {
    
    public static final HexBinaryType theInstance = new HexBinaryType();
    private HexBinaryType() { super("hexBinary"); }
    
    
// hex decoder
//====================================
    
    private static int hexToBin( char ch ) {
        if( '0'<=ch && ch<='9' )    return ch-'0';
        if( 'A'<=ch && ch<='F' )    return ch-'A'+10;
        if( 'a'<=ch && ch<='f' )    return ch-'a'+10;
        return -1;
    }

    public Object _createValue( String lexicalValue, ValidationContext context ) {
        byte[] buf = load(lexicalValue);
        if(buf==null)   return null;
        else            return new BinaryValueType(buf);
    }
    
    public static byte[] load( String s ) {
        final int len = s.length();

        // "111" is not a valid hex encoding.
        if( len%2 != 0 )    return null;

        byte[] out = new byte[len/2];

        for( int i=0; i<len; i+=2 ) {
            int h = hexToBin(s.charAt(i  ));
            int l = hexToBin(s.charAt(i+1));
            if( h==-1 || l==-1 )
                return null;    // illegal character

            out[i/2] = (byte)(h*16+l);
        }

        return out;
    }

    protected boolean checkFormat( String lexicalValue, ValidationContext context ) {
        final int len = lexicalValue.length();

        // "111" is not a valid hex encoding.
        if( len%2 != 0 )    return false;

        for( int i=0; i<len; i++ )
            if( hexToBin(lexicalValue.charAt(i))==-1 )
                return false;

        return true;
    }

    public String serializeJavaObject( Object value, SerializationContext context ) {
        if(!(value instanceof byte[]))
            throw new IllegalArgumentException();

        return save( (byte[])value );
    }
    
    public static String save( byte[] data ) {
        StringBuffer r = new StringBuffer(data.length*2);
        for( int i=0; i<data.length; i++ ) {
            r.append( encode(data[i]>>4) );
            r.append( encode(data[i]&0xF) );
        }
        return r.toString();
    }

    
    public String convertToLexicalValue( Object value, SerializationContext context ) {
        if(!(value instanceof BinaryValueType))
            throw new IllegalArgumentException();
        
        return serializeJavaObject( ((BinaryValueType)value).rawData, context );
    }
    
    public static char encode( int ch ) {
        ch &= 0xF;
        if( ch<10 )        return (char)('0'+ch);
        else            return (char)('A'+(ch-10));
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
