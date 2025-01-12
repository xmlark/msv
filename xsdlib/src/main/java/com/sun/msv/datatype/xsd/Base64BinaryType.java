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
 * "base64Binary" type.
 * 
 * type of the value object is {@link BinaryValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#base64Binary for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Base64BinaryType extends BinaryBaseType {
    public static final Base64BinaryType theInstance = new Base64BinaryType();
    private Base64BinaryType() { super("base64Binary"); }
    
    
// base64 decoder
//====================================
    
    private static final byte[] decodeMap = initDecodeMap();
    private static final byte PADDING = 127;

    private static byte[] initDecodeMap() {
        byte[] map = new byte[256];
        int i;
        for( i=0; i<256; i++ )        map[i] = -1;

        for( i='A'; i<='Z'; i++ )    map[i] = (byte)(i-'A');
        for( i='a'; i<='z'; i++ )    map[i] = (byte)(i-'a'+26);
        for( i='0'; i<='9'; i++ )    map[i] = (byte)(i-'0'+52);
        map['+'] = 62;
        map['/'] = 63;
        map['='] = PADDING;

        return map;
    }

    public Object _createValue( String lexicalValue, ValidationContext context ) {
        // I know, these methods should throw an exception ...
        byte[] buf = load(lexicalValue);
        if(buf==null)   return null;
        else            return new BinaryValueType(buf);
    }
    
    public static byte[] load( String lexicalValue ) {
        final char[] buf = lexicalValue.toCharArray();

        final int outlen = calcLength(buf);
        if( outlen==-1 )    return null;
        final byte[] out = new byte[outlen];
        int o=0;

        final int len = buf.length;
        int i;

        final byte[] quadruplet = new byte[4];
        int q=0;

        // convert each quadruplet to three bytes.
        for( i=0; i<len; i++ ) {
            byte v = decodeMap[buf[i]];
            if( v!=-1 )
                quadruplet[q++] = v;

            if(q==4) {
                // quadruplet is now filled.
                out[o++] = (byte)((quadruplet[0]<<2)|(quadruplet[1]>>4));
                if( quadruplet[2]!=PADDING )
                    out[o++] = (byte)((quadruplet[1]<<4)|(quadruplet[2]>>2));
                if( quadruplet[3]!=PADDING )
                    out[o++] = (byte)((quadruplet[2]<<6)|(quadruplet[3]));
                q=0;
            }
        }

        // assertion failed.
        if(q!=0)    throw new IllegalStateException();

        return out;
    }

    protected boolean checkFormat( String lexicalValue, ValidationContext context ) {
        return calcLength( lexicalValue.toCharArray() ) != -1;
    }

    /**
     * computes the length of binary data.
     * 
     * This function also performs format check.
     * @return    -1        if format is illegal.
     * 
     */
    private static int calcLength( final char[] buf ) {
        final int len = buf.length;
        int base64count=0, paddingCount=0;
        int i;

        for( i=0; i<len; i++ ) {
            if( isXMLSpace(buf[i]) )
                continue;        // ignore whitespace
            if( buf[i]=='=' )    // decodeMap['=']!=-1, so we have to check this first.
                break;
            if( buf[i]>=256 || decodeMap[buf[i]] == -1 )
                return -1;      // incorrect character
            base64count++;
        }

        // once we saw '=', nothing but '=' can be appeared.
        for( ; i<len; i++ ) {
            if( isXMLSpace(buf[i]) )
                continue;        // ignore whitespace
            if( buf[i]!='=' )
                return -1;      // incorrect character
            paddingCount++;
        }

        // no more than two paddings are allowed.
        if( paddingCount > 2 )        return -1;
        // characters must be a multiple of 4.
        if( (base64count+paddingCount)%4 != 0 )    return -1;

        return ((base64count+paddingCount)/4)*3-paddingCount;
    }
    
    
    
    private static final char[] encodeMap = initEncodeMap();

    private static char[] initEncodeMap() {
        char[] map = new char[64];
        int i;
        for( i= 0; i<26; i++ )        map[i] = (char)('A'+i);
        for( i=26; i<52; i++ )        map[i] = (char)('a'+(i-26));
        for( i=52; i<62; i++ )        map[i] = (char)('0'+(i-52));
        map[62] = '+';
        map[63] = '/';

        return map;
    }

    protected static char encode( int i ) {
        return encodeMap[i&0x3F];
    }

    public String serializeJavaObject( Object value, SerializationContext context ) {
        if(!(value instanceof byte[]))
            throw new IllegalArgumentException();
        
        return save((byte[])value);
    }
    
    public static String save( byte[] input ) {
        
        StringBuffer r = new StringBuffer(input.length*4/3); /* rough estimate*/
        
        for( int i=0; i<input.length; i+=3 ) {
            switch( input.length-i ) {
            case 1:
                r.append( encode(input[i]>>2) );
                r.append( encode(((input[i])&0x3)<<4) );
                r.append("==");
                break;
            case 2:
                r.append( encode(input[i]>>2) );
                r.append( encode(
                            ((input[i]&0x3)<<4) |
                            ((input[i+1]>>4)&0xF)) );
                r.append( encode((input[i+1]&0xF)<<2) );
                r.append("=");
                break;
            default:
                r.append( encode(input[i]>>2) );
                r.append( encode(
                            ((input[i]&0x3)<<4) |
                            ((input[i+1]>>4)&0xF)) );
                r.append( encode(
                            ((input[i+1]&0xF)<<2)|
                            ((input[i+2]>>6)&0x3)) );
                r.append( encode(input[i+2]&0x3F) );
                break;
            }
        }
        
        return r.toString();
    }
    
    public String convertToLexicalValue( Object value, SerializationContext context ) {
        if(!(value instanceof BinaryValueType))
            throw new IllegalArgumentException();
        
        return serializeJavaObject( ((BinaryValueType)value).rawData, context );
    }

    private static boolean isXMLSpace(char c) {
        // Per https://www.w3.org/TR/xml/#NT-S
        return c == ' ' || c == '\r' || c == '\n' || c == '\t';
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
