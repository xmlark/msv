package msv;

//import org.relaxng.testharness.validator.*;
import com.sun.msv.verifier.identity.IDConstraintChecker;
import com.sun.msv.driver.textui.ReportErrorHandler;
import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.xmlschema.XMLSchemaReader;
import com.sun.msv.verifier.IVerifier;
import com.sun.msv.verifier.identity.IDConstraintChecker;

/**
 * Driver for MSV as XML Schema validator.
 */
public class IValidatorImplForXS extends IValidatorImpl {

	protected GrammarReader getReader() {
		return new XMLSchemaReader( new DebugController(false,true), factory );;
	}
	
	protected IVerifier getVerifier( Grammar grammar ) {
		return new IDConstraintChecker( (XMLSchemaGrammar)grammar,
			new ReportErrorHandler() );
	}
}
