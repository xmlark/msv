package batch.driver;

import org.iso_relax.verifier.Schema;

import com.sun.msv.driver.textui.ReportErrorHandler;
import com.sun.msv.grammar.ExpressionPool;
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
    
    public IValidatorImplForXS( boolean strict ) {
        super(strict);
    }
    
    protected Schema getSchemaForSchema() {
        return XMLSchemaReader.getXmlSchemaForXmlSchema();
    }

    protected GrammarReader getReader() {
        return new XMLSchemaReader( createController(), factory, new ExpressionPool() );
    }
    
    protected IVerifier getVerifier( Grammar grammar ) {
        return new IDConstraintChecker( (XMLSchemaGrammar)grammar,
            new ReportErrorHandler() );
    }
}
