package batch.driver;

import batch.WordlessErrorReporter;
import batch.model.*;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import com.sun.resolver.tools.CatalogResolver;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.verifier.*;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.driver.textui.DebugController;
import java.io.File;
import javax.xml.parsers.SAXParserFactory;

abstract class AbstractValidatorExImpl implements IValidatorEx
{
	public boolean validate( ISchema schema, File instance ) throws Exception {
		return validate( ((ISchemaImpl)schema).grammar, instance );
	}

	public ISchema parseSchema( File schema ) throws Exception {
		
		InputSource source = com.sun.msv.util.Util.getInputSource( schema.getAbsolutePath() );
		
		if( source==null )
			throw new Error("this source doesn't support the getAsInputSource method");
		Grammar g = parseSchema( source, createController() );
		
		if(g==null)		return null;
		else			return new ISchemaImpl(g);
	}

    protected static SAXParserFactory factory = SAXParserFactory.newInstance();
	private final static CatalogResolver resolver = new CatalogResolver();
    
	public boolean validate( Grammar grammar, File instance ) throws Exception {
		IVerifier verifier = getVerifier( grammar );
        
        XMLReader reader = factory.newSAXParser().getXMLReader();
        reader.setContentHandler(verifier);
        reader.setEntityResolver(resolver);
        reader.setErrorHandler(new WordlessErrorReporter());
        reader.parse( com.sun.msv.util.Util.getInputSource(instance.getAbsolutePath()) );
		
        return verifier.isValid();
	}

	
	
	
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
