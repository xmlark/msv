import com.sun.msv.grammar.Grammar;
import com.sun.tahiti.runtime.ll.*;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.XMLReader;

public class TestDriver
{
	public static void main( String args[] ) throws Exception {
		
		SAXParserFactory factory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
		factory.setNamespaceAware(true);

		System.out.println("preparing a grammar");
		long cnt = System.currentTimeMillis();
		
		Unmarshaller unmarshaller = new Unmarshaller( getGrammar() );
		
		System.out.println("preparation complete: ("+(System.currentTimeMillis()-cnt)+"ms)");
		
		XMLReader reader = factory.newSAXParser().getXMLReader();
		reader.setContentHandler(unmarshaller);
		
		reader.parse(args[0]);
		
		System.out.println(unmarshaller.getResult());
	}
	
	static BindableGrammar getGrammar() {
		return out.sub.Name.grammar;
	}
}
