package com.sun.msv.schematron.reader;

import org.apache.xml.utils.PrefixResolver;
import org.xml.sax.Locator;

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
		currentNs = ((SRELAXNGReader)owner.reader).getTargetNamespace();
		currentResolver = ((SRELAXNGReader)owner.reader).prefixResolver;
		location = owner.getLocation();
	}
	private final Locator location;
	private final String currentNs;
	private final GrammarReader.PrefixResolver currentResolver;
	
	public String getBaseIdentifier() {
		return location.getSystemId();
	}
	public String getNamespaceForPrefix( String prefix ) {
		if(prefix.equals(""))	return currentNs;
		else					return currentResolver.resolve(prefix);
	}
	public String getNamespaceForPrefix( String prefix, org.w3c.dom.Node n ) {
		return getNamespaceForPrefix(prefix);
	}
	public boolean handlesNullPrefixes() {
		return false;
	}
}
