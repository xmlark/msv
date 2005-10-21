package com.sun.msv.schematron.reader;

import org.apache.xml.utils.PrefixResolver;
import org.xml.sax.Locator;
import org.w3c.dom.Node;

import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.State;

/**
 * prefix resolver.
 * 
 * Note the default namespace is resolved to the current ns value.
 */
class PrefixResolverImpl implements PrefixResolver
{
	PrefixResolverImpl( State owner ) {
        reader = ((SRELAXNGReader) owner.reader);
        currentNs = reader.getTargetNamespace();
		currentResolver = reader.prefixResolver;
		location = owner.getLocation();
	}
	private final Locator location;
	private final String currentNs;
    private final SRELAXNGReader reader;
	private final GrammarReader.PrefixResolver currentResolver;
	
	public String getBaseIdentifier() {
		return location.getSystemId();
	}
	public String getNamespaceForPrefix( String prefix ) {
		if(prefix.equals(""))	return currentNs;

        String nsUri = reader.schematronNs.getURI(prefix);
        if(nsUri!=null)     return nsUri;

        // for the compatibility reason with the past version
        // also allow the current in-scope namespace bindings to take effect
        return currentResolver.resolve(prefix);
	}
	public String getNamespaceForPrefix( String prefix, Node n ) {
		return getNamespaceForPrefix(prefix);
	}
	public boolean handlesNullPrefixes() {
		return false;
	}
}
