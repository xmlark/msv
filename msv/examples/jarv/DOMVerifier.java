/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package jarv;

import org.iso_relax.verifier.*;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import java.io.File;

/**
 * Uses <a href="http://iso-relax.sourceforge.net/apiDoc/">JARV</a>
 * to validate DOM documents/subtree.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DOMVerifier
{
    public static void main( String[] args ) throws Exception {
        if(args.length<2) {
            System.out.println("Usage: DOMVerifier <schema> <instance> ...");
            return;
        }
        
        // setup JARV and compile a schema.
        VerifierFactory factory = new com.sun.msv.verifier.jarv.TheFactoryImpl();
        Verifier verifier = factory.compileSchema(args[0]).newVerifier();
            // instead, you can call factory.newVerifier(args[0])
            // this will result in the same behavior.
        
        // setup JAXP
        DocumentBuilderFactory domf = DocumentBuilderFactory.newInstance();
        domf.setNamespaceAware(true);
        DocumentBuilder builder = domf.newDocumentBuilder();
        
        for( int i=1; i<args.length; i++ ) {
            // parse a document into a DOM.
            Document dom = builder.parse(new File(args[i]));
            
            // performs the validation on the whole tree.
            // instead, you can pass an Element to the verify method, too.
            // e.g.,  verifier.verify(dom.getDocumentElement())
            if(verifier.verify(dom))
                System.out.println("valid  :"+args[i]);
            else
                System.out.println("invalid:"+args[i]);
        }
    }
}
