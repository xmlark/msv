package msv;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.GrammarReaderController;
import org.xml.sax.InputSource;
import org.relaxng.testharness.model.XMLDocument;

public interface IValidatorEx extends org.relaxng.testharness.validator.IValidator
{
	Grammar parseSchema( InputSource source, GrammarReaderController controller )
			throws Exception;

	boolean validate( Grammar schema, XMLDocument instance ) throws Exception;
}
