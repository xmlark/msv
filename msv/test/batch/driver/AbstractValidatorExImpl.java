package msv;

import org.relaxng.testharness.validator.*;
import org.relaxng.testharness.model.*;
import org.xml.sax.InputSource;
import com.sun.resolver.tools.CatalogResolver;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.verifier.*;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.driver.textui.DebugController;

abstract class AbstractValidatorExImpl implements IValidatorEx
{
	public boolean validate( ISchema schema, XMLDocument instance ) throws Exception {
		return validate( ((ISchemaImpl)schema).grammar, instance );
	}
	
	public ISchema parseSchema( XMLDocument pattern, RNGHeader header ) throws Exception {
		
		InputSource source = pattern.getAsInputSource();
		
		if( source==null )
			throw new Error("this source doesn't support the getAsInputSource method");
		Grammar g = parseSchema( source, createController() );
		
		if(g==null)		return null;
		else			return new ISchemaImpl(g);
	}
	
	public boolean validate( Grammar grammar, XMLDocument instance ) throws Exception {
		IVerifier verifier = getVerifier( grammar );
		instance.getAsSAX( verifier );
		return verifier.isValid();
	}

	
	protected final static CatalogResolver resolver = new CatalogResolver();
	
	
//
// overridable methods
//
	
	
	/**
	 * Creates an instance of GrammarReaderController that is used to
	 * parse a schema.
	 */
	protected GrammarReaderController createController() {
		return new DebugController(true,false,resolver);
	}

	/**
	 * Creates a new instance of Verifier that will be used to validate
	 * a document.
	 */
	protected IVerifier getVerifier( Grammar grammar ) {
		return new Verifier( new REDocumentDeclaration(grammar),
			new WordlessErrorReporter() );
	}
}
