/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package schemaLookup;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.xmlschema.MultiSchemaReader;
import com.sun.msv.reader.xmlschema.XMLSchemaReader;
import com.sun.msv.verifier.jarv.SchemaImpl;
import com.sun.resolver.tools.CatalogResolver;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class Main {
    private final Set ns = new HashSet();
    
    public static void main( String[] args ) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        
        for( int i=1; i<args.length; i++ ) {        
            String xmlFile = args[i];
            System.out.println(xmlFile);
            
            Document dom = dbf.newDocumentBuilder().parse(new File(xmlFile));
            
            // collect all namespaces and assemble a schema
            SchemaBuilder sb = new SchemaBuilder(args[0]);
            NamespaceScanner.scan( dom, sb );
            
            Schema schema = sb.getResult();
            if( schema==null ) {
                System.out.println("failed to parse a schema");
                continue;
            }
            Verifier verifier = schema.newVerifier();
            
            if( verifier.verify(dom) )
                System.out.println("valid");
            else
                System.out.println("invalid");
        }
    }

    private static class SchemaBuilder implements NamespaceReceiver {
        
        private final CatalogResolver resolver = new CatalogResolver();
        private final MultiSchemaReader msr = new MultiSchemaReader(
            new XMLSchemaReader(new DebugController(true)));
        
        public SchemaBuilder( String catalogFile ) throws IOException {
            resolver.getCatalog().parseCatalog(catalogFile);
        }
        
        public void onNamespace(String ns) {
            InputSource is = resolver.resolveEntity(ns,"");
            if(is==null) {
                System.out.println("no schema found for the namespace "+ns);
                return;
            }
            msr.parse(is);
        }
        
        public Schema getResult() {
            Grammar result = msr.getResult();
            if(result==null)    return null;
            else                return new SchemaImpl(result);
        }
    }
}
