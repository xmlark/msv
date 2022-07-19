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

package com.sun.msv.datatype.xsd.regex;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;

/**
 * {@link RegExpFactory} by a copy of Xerces in Sun's JDK 5.0.
 *
 * @author Kohsuke Kawaguchi
 */
final class JDK50Impl extends RegExpFactory {

    private final Class regexp;
    private final Constructor ctor;
    private final Method matches;

    JDK50Impl() throws Exception {
        regexp = getClass().getClassLoader().loadClass("com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression");
        ctor = regexp.getConstructor(new Class[]{String.class,String.class});
        matches = regexp.getMethod("matches",new Class[]{String.class});
    }

    public RegExp compile(String exp) throws ParseException {
        final Object re;

        try {
            // re = new RegularExpression(exp,"X");
            re = ctor.newInstance(new Object[]{exp, "X"});
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new ParseException(e.getTargetException().getMessage(),-1);
        } catch (InstantiationException e) {
            throw new InstantiationError(e.getMessage());
        }

        return new RegExp() {
            public boolean matches(String text) {
                try {
                    return ((Boolean)matches.invoke(re,new Object[]{text})).booleanValue();
                } catch (IllegalAccessException e) {
                    throw new IllegalAccessError(e.getMessage());
                } catch (InvocationTargetException e) {
                    throw new Error(e);
                }
            }
        };
    }

}
