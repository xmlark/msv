package msv;

import org.relaxng.testharness.validator.*;
import org.relaxng.testharness.model.XMLDocument;
import com.sun.msv.driver.textui.ReportErrorHandler;
import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.verifier.IVerifier;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.trex.ng.RELAXNGReader;
import javax.xml.parsers.SAXParserFactory;

/**
 * Test harness for RELAX NG conformance test suite.
 */
public class IValidatorImpl implements IValidator
{
	protected final SAXParserFactory factory;
	
	public IValidatorImpl() {
		factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
	}
	
	public boolean validate( ISchema schema, XMLDocument instance ) throws Exception {
		IVerifier verifier = getVerifier( ((ISchemaImpl)schema).grammar );
		instance.getAsSAX( verifier );
		return verifier.isValid();
	}
	
	public ISchema parseSchema( XMLDocument pattern ) throws Exception {
		GrammarReader reader = getReader();
		pattern.getAsSAX( reader );
		
		Grammar grammar = reader.getResultAsGrammar();
		
		if( grammar==null )	return null;
		else				return new ISchemaImpl( grammar );
	}

	/**
	 * creates a GrammarReader object to parse a grammar.
	 * 
	 * <p>
	 * override this method to use different reader implementation.
	 * RELAX NG test harness can be used to test XML Schema, TREX, etc.
	 */
	protected GrammarReader getReader() {
		return new RELAXNGReader( new DebugController(false,true), factory );;
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
