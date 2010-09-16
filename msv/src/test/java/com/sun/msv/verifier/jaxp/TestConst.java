package com.sun.msv.verifier.jaxp;

public class TestConst
{
    static final String incorrectSchema =
        "<element name='root' xmlns='what:the:heck:is:this'>"+
            "<optional>"+
                "<attribute name='foo'/>"+
            "</optional>"+
            "<text/>"+
        "</element>";
    
    static final String rngSchema =
        "<element name='root' xmlns='http://relaxng.org/ns/structure/1.0'>"+
            "<optional>"+
                "<attribute name='foo'/>"+
            "</optional>"+
            "<text/>"+
        "</element>";
    
    static final String xsdSchema =
        "<schema xmlns='http://www.w3.org/2001/XMLSchema'>"+
            "<element name='root' type='string'/>"+
        "</schema>";
    
    static final String validDocument = "<root>abc</root>";
    static final String invalidDocument = "<root2/>";
}
