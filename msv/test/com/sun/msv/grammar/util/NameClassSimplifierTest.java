package com.sun.msv.grammar.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.NotNameClass;
import com.sun.msv.grammar.SimpleNameClass;

public class NameClassSimplifierTest extends TestCase {
    
    public NameClassSimplifierTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(NameClassSimplifierTest.class);
    }
    
    public void testSimplifier1() throws Exception {
        assertSame(
            NameClass.ALL,
            NameClassSimplifier.simplify(
                new ChoiceNameClass(
                    NameClass.ALL,
                    new DifferenceNameClass(
                        new NamespaceNameClass("abc"),
                        new SimpleNameClass("abc","def")))));
    }
    
    public void testSimplifier2() throws Exception {
        NameClass nc = NameClassSimplifier.simplify(
            new DifferenceNameClass(
                new ChoiceNameClass(
                    NameClass.ALL,
                    new NamespaceNameClass("abc")
                ),
                new SimpleNameClass("abc","def")));
        
        assertTrue( nc instanceof NotNameClass );
        NotNameClass nnc = (NotNameClass)nc;
        assertTrue( nnc.child instanceof SimpleNameClass );
        SimpleNameClass snc = (SimpleNameClass)nnc.child;
        assertEquals( snc.namespaceURI, "abc" );
        assertEquals( snc.localName, "def");
    }
    
    public void testSimplifier3() throws Exception {
        NameClass nc = NameClassSimplifier.simplify(
            new DifferenceNameClass(
                 NameClass.ALL,
                new DifferenceNameClass(
                    new NamespaceNameClass("abc"),
                    new SimpleNameClass("abc","def"))));
        
        assertTrue( nc instanceof NotNameClass );
        NotNameClass nnc = (NotNameClass)nc;
        assertTrue( nnc.child instanceof DifferenceNameClass );
        DifferenceNameClass dnc = (DifferenceNameClass)nnc.child;
        assertTrue( dnc.nc1 instanceof NamespaceNameClass );
        NamespaceNameClass nc1 = (NamespaceNameClass)dnc.nc1;
        assertEquals( "abc",nc1.namespaceURI );
        assertTrue( dnc.nc2 instanceof SimpleNameClass );
        SimpleNameClass nc2 = (SimpleNameClass)dnc.nc2;
        assertEquals( "abc",nc2.namespaceURI );
        assertEquals( "def",nc2.localName );
    }
}
