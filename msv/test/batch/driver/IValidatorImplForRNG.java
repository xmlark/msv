package msv;

import com.sun.msv.reader.trex.ng.comp.RELAXNGCompReader;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.grammar.relaxng.RELAXNGGrammar;
import org.relaxng.testharness.model.RNGHeader;

/**
 * Driver for MSV as XML Schema validator.
 */
public class IValidatorImplForRNG extends IValidatorImpl {

	protected GrammarReader getReader( RNGHeader header ) {
		return new RELAXNGCompReader( createController() );
	}

	private void checkCompatibility( RNGHeader header, String propName, boolean isValid ) {
		boolean expected = true;
		
		if(header!=null) {
			String value = header.getProperty("",propName);
			if(value!=null) {
				if(!value.equals("no"))
					throw new Error("bad value for "+propName);
			
				expected = false;
			}
		}
		
		if( isValid!=expected ) {
			if(expected)	throw new Error("must be compatible with "+propName);
			else			throw new Error("must be incompatible with "+propName);
		}
	}
	
	protected Grammar getGrammarFromReader( GrammarReader reader, RNGHeader header ) {
		Grammar g = reader.getResultAsGrammar();
		if(g==null)		return g;
		
		RELAXNGGrammar ngg = (RELAXNGGrammar)g;
		
		checkCompatibility( header, "annotationCompatibility", ngg.isAnnotationCompatible );
		checkCompatibility( header, "idCompatibility", ngg.isIDcompatible );
		checkCompatibility( header, "defaultValueCompatibility", ngg.isDefaultAttributeValueCompatible );
		
		return g;
	}
}
