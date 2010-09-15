/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * tests ConcreteType.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ConcreteTypeTest extends TestCase {
    
    public ConcreteTypeTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(ConcreteTypeTest.class);
    }
    
    
    /** serializes o and then returns de-serialized object. */
    public Object freezeDry( Object o ) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        
        // serialize it
        oos.writeObject( o );
        oos.flush();
        
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        
        return ois.readObject();
    }
    
    public void testSingleton( Object o ) throws Exception {
        assertSame( o, freezeDry(o) );
    }
    
    /** test serialization. */
    public void testSerialization() throws Exception {
        
        // ensure that serialization doesn't break singleton.
        testSingleton( StringType.theInstance );
        testSingleton( ByteType.theInstance );
    }
}
