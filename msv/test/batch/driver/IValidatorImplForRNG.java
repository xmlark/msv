package batch.driver;

import org.iso_relax.verifier.Schema;

import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.trex.ng.comp.RELAXNGCompReader;

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
    
    protected GrammarReader getReader() {
        return new RELAXNGCompReader( createController(), factory, new ExpressionPool() );
    }
}
