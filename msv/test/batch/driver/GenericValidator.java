package batch.driver;

import org.xml.sax.InputSource;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.util.GrammarLoader;

/**
 * Generic Driver for MSV
 */
public class GenericValidator extends AbstractValidatorExImpl {
    
    public Grammar parseSchema( InputSource is, GrammarReaderController controller ) throws Exception {
        return GrammarLoader.loadSchema(is,controller);
    }
}
