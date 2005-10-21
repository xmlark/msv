package com.sun.msv.schematron.reader;

import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.reader.relax.core.RELAXCoreReader;
import com.sun.msv.util.StartTagInfo;
import org.xml.sax.helpers.NamespaceSupport;

/**
 * Handles schematron's "ns" element.
 *
 * @author Kohsuke Kawaguchi
 */
public class SNsState extends SimpleState {
    protected State createChildState(StartTagInfo tag) {
        return null;
    }

    protected void endSelf() {
        String prefix = startTag.getAttribute("prefix");
        String nsUri = startTag.getAttribute("uri");

        if(prefix==null)
            reader.reportError( RELAXCoreReader.ERR_MISSING_ATTRIBUTE, "prefix", "ns" );
        else
        if(nsUri==null)
            reader.reportError( RELAXCoreReader.ERR_MISSING_ATTRIBUTE, "uri", "ns" );
        else {
            // UGLY, but the namespace for schematron needs to be activated on
            // the parent element level, or else the new namespace binding
            // will go out of scope as soon as we hit </s:ns>.
            NamespaceSupport nsContext = ((SRELAXNGReader) reader).schematronNs;
            nsContext.popContext();
            nsContext.declarePrefix(prefix,nsUri);
            nsContext.pushContext();
        }
    }
}
