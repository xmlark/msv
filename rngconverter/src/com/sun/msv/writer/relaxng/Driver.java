/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.writer.relaxng;

import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.xml.parsers.SAXParserFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.xml.sax.SAXException;

import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.util.GrammarLoader;

/**
 * converts any supported languages into the equivalent RELAX NG grammar.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Driver {
    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.out.println(localize(MSG_USAGE));
            return;
        }

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        String input = args[0];
        String output = args[1];
        PrintStream out = new PrintStream(new FileOutputStream(output));

        // load a grammar.
        Grammar g = GrammarLoader.loadSchema(input, new DebugController(true, false, System.err), factory);

        if (g == null) {
            System.err.println(localize(MSG_GRAMMAR_ERROR));
            return;
        }

        writeGrammar(g, out);
        out.close();
    }
    
    /**
     * Writes a grammar to the specified output.
     */
    public static void writeGrammar(Grammar g, java.io.OutputStream out) throws SAXException {

        RELAXNGWriter writer = new RELAXNGWriter();
        // use XMLSerializer of Apache to serialize SAX event into plain text.
        // OutputFormat specifies "pretty printing".
        writer.setDocumentHandler(new XMLSerializer(out, new OutputFormat("xml", null, true)));
        // visit TREXGrammar and generate its XML representation.
        writer.write(g);
    }

    public static String localize(String propertyName, Object[] args) {
        String format =
            java.util.ResourceBundle.getBundle("com.sun.msv.writer.relaxng.Messages").getString(propertyName);
        return java.text.MessageFormat.format(format, args);
    }
    public static String localize(String prop) {
        return localize(prop, null);
    }
    public static String localize(String prop, Object arg1) {
        return localize(prop, new Object[] { arg1 });
    }
    public static String localize(String prop, Object arg1, Object arg2) {
        return localize(prop, new Object[] { arg1, arg2 });
    }

    private static final String MSG_USAGE = // arg:0
        "Driver.Usage";
    private static final String MSG_GRAMMAR_ERROR = // arg:0
        "Driver.GrammarError";
}
