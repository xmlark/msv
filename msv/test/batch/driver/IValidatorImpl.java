package msv;

import org.relaxng.testharness.validator.*;
import org.relaxng.testharness.model.XMLDocument;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.driver.textui.ReportErrorHandler;
import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.reader.trex.ng.RELAXNGReader;
import javax.xml.parsers.SAXParserFactory;

/**
 * Test harness for RELAX NG conformance test suite.
 */
public class IValidatorImpl implements IValidator
{
	private SAXParserFactory factory;
	
	public IValidatorImpl() {
		factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
	}
	
	public boolean validate( ISchema schema, XMLDocument instance ) throws Exception {
		Verifier verifier = new Verifier(
			((ISchemaImpl)schema).docDecl,
			new ReportErrorHandler() );
		instance.getAsSAX( verifier );
		return verifier.isValid();
	}
	
	public ISchema parseSchema( XMLDocument pattern ) throws Exception {
		RELAXNGReader reader = new RELAXNGReader( new DebugController(false,true), factory );
		pattern.getAsSAX( reader );
		if( reader.getResult()==null )	return null;
		
		return new ISchemaImpl(
			new REDocumentDeclaration(reader.getResult()));
	}
}
