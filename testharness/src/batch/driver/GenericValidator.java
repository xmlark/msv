package batch.driver;

import com.sun.msv.reader.relax.core.RELAXCoreReader;
import com.sun.msv.reader.util.GrammarLoader;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.driver.textui.ReportErrorHandler;
import com.sun.msv.grammar.relaxng.RELAXNGGrammar;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.verifier.IVerifier;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.resolver.tools.CatalogResolver;
import batch.model.*;
import org.xml.sax.*;

/**
 * Generic Driver for MSV
 */
public class GenericValidator extends AbstractValidatorExImpl {
	
	public Grammar parseSchema( InputSource is, GrammarReaderController controller ) throws Exception {
		return GrammarLoader.loadSchema(is,controller);
	}
}
