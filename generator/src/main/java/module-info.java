module net.java.dev.msv.generator {
    requires java.xml;
    requires net.java.dev.msv.core;
    requires net.java.dev.msv.xsdlib;
    requires relaxngDatatype;
    requires xercesImpl;

    exports com.sun.msv.generator;
}
