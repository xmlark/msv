package batch.driver;

import com.sun.msv.verifier.identity.IDConstraintChecker;
import com.sun.msv.driver.textui.ReportErrorHandler;
import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.xmlschema.XMLSchemaReader;
import com.sun.msv.verifier.IVerifier;
import com.sun.msv.verifier.identity.IDConstraintChecker;
import org.relaxng.testharness.model.RNGHeader;
import org.iso_relax.verifier.Schema;

/**
 * Driver for MSV as XML Schema validator.
 */
public class IValidatorImplForXS extends IValidatorImpl {
	
	public IValidatorImplForXS( boolean strict ) {
		super(strict);
	}
	
	protected Schema getSchemaForSchema() {
		return XMLSchemaReader.getXmlSchemaForXmlSchema();
	}

	protected GrammarReader getReader( RNGHeader header ) {
		return new XMLSchemaReader( createController(), factory, new ExpressionPool() );
	}
	
	protected IVerifier getVerifier( Grammar grammar ) {
		return new IDConstraintChecker( (XMLSchemaGrammar)grammar,
			new ReportErrorHandler() );
	}
}
