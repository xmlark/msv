module net.java.dev.msv.xsdlib {
    requires java.xml;
    requires relaxngDatatype;

    exports com.sun.msv.datatype;
    exports com.sun.msv.datatype.regexp;
    exports com.sun.msv.datatype.xsd;
    exports com.sun.msv.datatype.xsd.datetime;
    exports com.sun.msv.datatype.xsd.ngimpl;
    exports com.sun.msv.datatype.xsd.regex;
    exports com.sun.xml.util;
}
