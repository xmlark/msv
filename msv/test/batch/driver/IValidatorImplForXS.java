package msv;

import com.sun.msv.verifier.identity.IDConstraintChecker;
import com.sun.msv.driver.textui.ReportErrorHandler;
import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.xmlschema.XMLSchemaReader;
import com.sun.msv.verifier.IVerifier;
import com.sun.msv.verifier.identity.IDConstraintChecker;
import org.relaxng.testharness.model.RNGHeader;

/**
 * Driver for MSV as XML Schema validator.
 */
public class IValidatorImplForXS extends IValidatorImpl {

	protected GrammarReader getReader( RNGHeader header ) {
		return new XMLSchemaReader( createController() );
	}
	
	protected IVerifier getVerifier( Grammar grammar ) {
		return new IDConstraintChecker( (XMLSchemaGrammar)grammar,
			new ReportErrorHandler() );
	}
}
