package batch.driver;

import com.sun.msv.reader.trex.ng.comp.RELAXNGCompReader;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.grammar.relaxng.RELAXNGGrammar;
import org.relaxng.testharness.model.RNGHeader;
import org.iso_relax.verifier.Schema;

/**
 * Driver for MSV as XML Schema validator.
 */
public class IValidatorImplForRNG extends IValidatorImpl {
	
	public IValidatorImplForRNG( boolean strict ) {
		super(strict);
	}
	
	protected Schema getSchemaForSchema() {
		return RELAXNGCompReader.getRELAXNGSchema4Schema();
	}
	
	protected GrammarReader getReader( RNGHeader header ) {
		return new RELAXNGCompReader( createController(), factory, new ExpressionPool() );
	}
}
