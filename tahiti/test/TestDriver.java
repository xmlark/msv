import com.sun.msv.grammar.Grammar;
import com.sun.tahiti.runtime.ll.*;
import com.sun.tahiti.runtime.sm.*;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;

public class TestDriver
{
	public static void main( String args[] ) throws Exception {
		
		if( args.length!=2 ) {
			System.out.println(
				"Usage: TestDriver <grammar class name> <XML file name>\n"+
				"  This test driver uses the specified grammar to \n"+
				"  parse the specified instance.");
			return;
		}
		SAXParserFactory factory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
		factory.setNamespaceAware(true);

		System.out.println("preparing a grammar");
		long cnt = System.currentTimeMillis();
		
		Unmarshaller unmarshaller = new Unmarshaller( getGrammar(args[0]) );
		
		System.out.println("preparation complete: ("+(System.currentTimeMillis()-cnt)+"ms)");
		
		XMLReader reader = factory.newSAXParser().getXMLReader();
		reader.setContentHandler(unmarshaller);
		
		try {
			reader.parse(args[1]);
		} catch( SAXException e ) {
			if( e.getException()!=null )
				e.getException().printStackTrace();
		}
		
		MarshallableObject mo = unmarshaller.getResult();
		System.out.println(mo);
		
		// serialize
		MarshallerAPI.marshall( mo, System.out, "UTF-8", true );
	}
	
	static BindableGrammar getGrammar( String className ) throws Exception {
		return (BindableGrammar)Class.forName(className).getField("grammar").get(null);
	}
}
