package msv;

import org.relaxng.testharness.validator.*;
import org.relaxng.testharness.model.XMLDocument;
import org.relaxng.testharness.model.RNGHeader;
import org.xml.sax.*;
import com.sun.msv.driver.textui.ReportErrorHandler;
import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.verifier.IVerifier;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.util.GrammarLoader;
import javax.xml.parsers.SAXParserFactory;

/**
 * Test harness for RELAX NG conformance test suite.
 */
public abstract class IValidatorImpl extends AbstractValidatorExImpl
{
//	protected final SAXParserFactory factory;
	
	public IValidatorImpl() {
//		factory = SAXParserFactory.newInstance();
//		factory.setNamespaceAware(true);
	}
	
	public ISchema parseSchema( XMLDocument pattern, RNGHeader header ) throws Exception {
		GrammarReader reader = getReader(header);
		pattern.getAsSAX( reader );
		
		Grammar grammar = getGrammarFromReader(reader,header);
		
		if( grammar==null )	return null;
		else				return new ISchemaImpl( grammar );
	}

	public Grammar parseSchema( InputSource is, GrammarReaderController controller ) throws Exception {
		return GrammarLoader.loadSchema(is,createController());
	}

	/**
	 * creates a GrammarReader object to parse a grammar.
	 * 
	 * <p>
	 * override this method to use different reader implementation.
	 * RELAX NG test harness can be used to test XML Schema, TREX, etc.
	 */
	protected abstract GrammarReader getReader( RNGHeader header );
	
	protected Grammar getGrammarFromReader( GrammarReader reader, RNGHeader header ) {
		return reader.getResultAsGrammar();
	}
	
	/**
	 * creates a Verifier object to validate a document.
	 * 
	 * <p>
	 * override this method to use a different verifier implementation.
	 */
	protected IVerifier getVerifier( Grammar grammar ) {
		return new Verifier( new REDocumentDeclaration(grammar),
			new ReportErrorHandler() );
	}
}
