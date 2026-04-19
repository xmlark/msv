module net.java.dev.msv.trexconverter {
    requires java.xml;
    requires net.java.dev.msv.core;
    requires net.java.dev.msv.xsdlib;
    requires relaxngDatatype;
    requires xercesImpl;

    exports com.sun.msv.trexconverter.datatype;
    exports com.sun.msv.writer.trex;
}
