package batch.driver;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.SAXParserFactory;

import org.apache.xml.resolver.tools.CatalogResolver;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import batch.WordlessErrorReporter;
import batch.model.ISchema;

import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.verifier.IVerifier;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;

abstract class AbstractValidatorExImpl implements IValidatorEx
{
    public boolean validate( ISchema schema, File instance ) throws Exception {
        return validate( schema.asGrammar(), instance );
    }

    public ISchema parseSchema( File schema ) throws Exception {
        return parseSchema(
            com.sun.msv.util.Util.getInputSource( schema.getAbsolutePath() ) );
    }

    public ISchema parseSchema( InputStream source ) throws Exception {
        return parseSchema( new InputSource(source) );
    }
        
    public ISchema parseSchema( InputSource source ) throws Exception {
        if( source==null )
            throw new Error("this source doesn't support the getAsInputSource method");
        Grammar g = parseSchema( source, createController() );
        
        if(g==null)        return null;
        else            return new ISchemaImpl(g);
    }
    
    protected static SAXParserFactory factory = SAXParserFactory.newInstance();
    static {
        factory.setNamespaceAware(true);
    }
    private final static CatalogResolver resolver = new CatalogResolver();
    
    public boolean validate( Grammar grammar, File instance ) throws Exception {
        IVerifier verifier = getVerifier( grammar );
        
        XMLReader reader = factory.newSAXParser().getXMLReader();
        reader.setContentHandler(verifier);
        reader.setEntityResolver(resolver);
        WordlessErrorReporter eh = new WordlessErrorReporter();
        reader.setErrorHandler(eh);
        verifier.setErrorHandler(eh);
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
