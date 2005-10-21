/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.grammar.ExpressionPool;


/**
 * Test program for serialization compatibility of {@link ExpressionPool}. 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class SerialTestTool {
    public static void main(String[] args) throws Exception {
        if( args[0].equals("write") ) {
        	ExpressionPool pool = new ExpressionPool();
            pool.createAnyString();
            pool.createChoice( pool.createData(StringType.theInstance), pool.createEpsilon() );
        
            ObjectOutputStream oos = new ObjectOutputStream(System.out);
            oos.writeObject(pool);
            oos.close();
        } else {
            ObjectInputStream ois = new ObjectInputStream(System.in);
            ExpressionPool pool = (ExpressionPool)ois.readObject();
            System.out.println(pool);
        }
    }
}
