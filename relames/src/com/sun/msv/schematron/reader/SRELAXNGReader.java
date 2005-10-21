package com.sun.msv.schematron.reader;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.NamespaceSupport;

import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.trex.TREXGrammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.State;
import com.sun.msv.reader.relax.core.RELAXCoreReader;
import com.sun.msv.reader.trex.ng.RELAXNGReader;
import com.sun.msv.util.StartTagInfo;

public class SRELAXNGReader extends RELAXNGReader {

	public static final String SchematronURI = "http://www.ascc.net/xml/schematron";
    
    public static final String RNG_PLUS_SCHEMATRON_URI =
        RELAXNGReader.RELAXNGNamespace + "+" + SchematronURI;


	
	/** loads RELAX NG pattern */
	public static TREXGrammar parse( String grammarURL,
		SAXParserFactory factory, GrammarReaderController controller )
	{
		SRELAXNGReader reader = new SRELAXNGReader(controller,factory);
		reader.parse(grammarURL);
		
		return reader.getResult();
	}
	
	/** loads RELAX NG pattern */
	public static TREXGrammar parse( InputSource grammar,
		SAXParserFactory factory, GrammarReaderController controller )
	{
		SRELAXNGReader reader = new SRELAXNGReader(controller,factory);
		reader.parse(grammar);
		
		return reader.getResult();
	}

	/** easy-to-use constructor. */
	public SRELAXNGReader( GrammarReaderController controller, SAXParserFactory parserFactory) {
		this( controller, parserFactory, new ExpressionPool() );
	}
	
	/** full constructor */
	public SRELAXNGReader( GrammarReaderController controller,
		SAXParserFactory parserFactory, ExpressionPool pool ) {
		super( controller, parserFactory, new SStateFactory(), pool );
	}
	
	// this reader overrides ElementState.
	static private class SStateFactory extends StateFactory {
		public State element	( State parent, StartTagInfo tag ) { return new SElementState(); }

        public State grammar(State parent, StartTagInfo tag) {
            return new SGrammarState();
        }
    }

	protected boolean isGrammarElement( StartTagInfo tag ) {
		if( tag.namespaceURI.equals(SchematronURI) ) {
            // let <s:ns> appear anywhere
            return !tag.localName.equals("ns");
        }

        return super.isGrammarElement(tag);
	}

    /**
     * Schematron namespace declared via &lt;s:ns>
     *
     * This is the namespace context we use to resolve prefixes in the XPath expression.
     */
    /*package*/ final NamespaceSupport schematronNs = new NamespaceSupport();

    public void startElement(String nsUri, String localName, String qname, Attributes atts) throws SAXException {
        if(nsUri.equals(SchematronURI) && localName.equals("ns")) {
            // allow <s:ns> to appear anywhere in the document
            String prefix = atts.getValue("prefix");
            String uri = atts.getValue("uri");

            if(prefix==null)
                reportError( RELAXCoreReader.ERR_MISSING_ATTRIBUTE, "prefix", "ns" );
            else
            if(uri==null)
                reportError( RELAXCoreReader.ERR_MISSING_ATTRIBUTE, "uri", "ns" );
            else {
                schematronNs.declarePrefix(prefix,uri);
            }
        } else {
            schematronNs.pushContext();
        }
        super.startElement(nsUri,localName,qname,atts);
    }

    public void endElement(String nsUri, String localName, String qname) throws SAXException {
        super.endElement(nsUri, localName, qname);
        if(nsUri.equals(SchematronURI) && localName.equals("ns")) {
            ;
        } else {
            schematronNs.popContext();
        }
    }

//
// error message handling
//
	protected String localizeMessage( String propertyName, Object[] args ) {
		String format;
		
		try {
			format = java.util.ResourceBundle.getBundle(
				"com.sun.msv.schematron.reader.Messages").getString(propertyName);
		} catch( Exception e ) {
			return super.localizeMessage(propertyName,args);
		}
		
	    return java.text.MessageFormat.format(format, args );
	}

	public static final String ERR_INVALID_XPATH =	// arg:2
		"SRELAXNGReader.InvalidXPath";
}
