package com.sun.msv.verifier.jarv;

import org.iso_relax.verifier.VerifierFactoryLoader;
import org.iso_relax.verifier.VerifierFactory;
import com.sun.msv.reader.relax.core.RELAXCoreReader;
import com.sun.msv.reader.trex.classic.TREXGrammarReader;
import com.sun.msv.reader.trex.ng.RELAXNGReader;
import com.sun.msv.reader.xmlschema.XMLSchemaReader;
import com.sun.msv.relaxns.reader.RELAXNSReader;
	
public class FactoryLoaderImpl implements VerifierFactoryLoader
{
	public FactoryLoaderImpl() {}
	
	public VerifierFactory createFactory( String language ) {
		
		// supported language
		if(language.equals(RELAXNGReader.RELAXNGNamespace))
			return new RELAXNGFactoryImpl();
		if(language.equals(RELAXCoreReader.RELAXCoreNamespace))
			return new RELAXCoreFactoryImpl();
		if(language.equals(TREXGrammarReader.TREXNamespace))
			return new TREXFactoryImpl();
		if(language.equals(XMLSchemaReader.XMLSchemaNamespace)
		|| language.equals(XMLSchemaReader.XMLSchemaNamespace_old))
			return new XSFactoryImpl();
		if(language.equals(RELAXNSReader.RELAXNamespaceNamespace))
			return new TheFactoryImpl();
		
		// backward compatibility
		if(language.equals("relax"))
			return new TheFactoryImpl();
		
		return null;
	}
}
