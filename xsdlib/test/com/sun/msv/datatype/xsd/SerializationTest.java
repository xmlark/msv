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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * tests serialization of datatypes.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SerializationTest extends TestCase
{
	public SerializationTest( String name ) { super(name); }
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(SerializationTest.class);
	}
	
    // serialize and de-serialize
    public Object freezeDry( Object dt ) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        
        // serialize it
        oos.writeObject(dt);
        oos.flush();
        
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        
        return ois.readObject();
    }
    
	/** test singleton-ness of built-in datatypes. */
	public void testSingletonness() throws Exception {
        for( int i=0; i<Const.builtinTypeNames.length; i++ ) {
            String name = Const.builtinTypeNames[i];
            
            XSDatatype dt = DatatypeFactory.getTypeByName(name);
            assertNotNull(dt);
            
            assertEquals("freeze dry test for "+dt.getName(),
                freezeDry(dt), dt);  // it must be singleton.
        }
    }
    
    public void testSingletonness2() throws Exception {
        // built-in types use their names to keep singleton-ness.
        // make sure that it won't be deceived by datatypes
        // whose name is accidentally the same as the built-in type.
        
        TypeIncubator ti = new TypeIncubator(StringType.theInstance);
        ti.addFacet("maxLength","120",null);
        XSDatatype pseudoString = ti.derive("","string");
        
        assertTrue( freezeDry(pseudoString)!=StringType.theInstance );
    }
}
