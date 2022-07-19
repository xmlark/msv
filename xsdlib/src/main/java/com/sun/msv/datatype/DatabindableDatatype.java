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

package com.sun.msv.datatype;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.ValidationContext;

/**
 * Datatype interface that supports Java databinding.
 *
 * This interface can be used to do java/xml databinding.
 * 
 * @author    Kohsuke Kawaguchi
 */
public interface DatabindableDatatype extends Datatype {
    /**
     * converts lexcial value to a corresponding Java-friendly object
     * by using the given context information.
     * 
     * <p>
     * For the actual types returned by each type,
     * see <a href="package-summary.html#javaType">here</a>.
     * 
     * <p>
     * Note that due to the difference between those Java friendly types
     * and actual XML Schema specification, the returned object sometimes
     * loses accuracy. For example, the "time" type allows "0.0000000000001 sec"
     * which cannot be represented in <code>java.util.Calendar</code> class.
     * 
     * @return    null
     *        when the given lexical value is not a valid lexical value for this type.
     */
    Object createJavaObject( String literal, ValidationContext context );
    
    /**
     * converts a value object back to the lexical representation.
     * 
     * <p>
     * This method is a kind of the "reverse" function of the createJavaObject method.
     * 
     * @param context
     *        The context object is used to obtain information necessary to
     *        serialize the value object. For example, QName type uses the context
     *        to encode the URI into a prefix.
     * 
     * @exception IllegalArgumentException
     *        If the type of the specified value object is not recognized,
     *        this exception is thrown. For example, if you pass
     *        a <code>String<code> object to the serializeJavaObject method of
     *        the "positiveInteger" type, this exception is thrown.
     * 
     * @return    null
     *        if the given object is invalid with respect to this datatype.
     */
    String serializeJavaObject( Object value, SerializationContext context )
        throws IllegalArgumentException;
    
    /**
     * gets the type of the objects that are created by the createJavaObject method.
     */
    Class getJavaObjectType();
}
