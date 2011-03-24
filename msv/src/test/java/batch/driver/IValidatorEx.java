package batch.driver;

import org.xml.sax.InputSource;

import batch.model.IValidator;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.GrammarReaderController;

public interface IValidatorEx extends IValidator
{
    Grammar parseSchema( InputSource source, GrammarReaderController controller )
            throws Exception;

//    boolean validate( Grammar schema, XMLDocument instance ) throws Exception;
}
