
import org.iso_relax.verifier.*;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import java.io.File;

public class DOMVerifier
{
	public static void main( String[] args ) throws Exception {
		VerifierFactory factory = new com.sun.msv.verifier.jarv.TheFactoryImpl();
		Verifier verifier = factory.newVerifier(args[0]);
		
		DocumentBuilderFactory domf = DocumentBuilderFactory.newInstance();
		domf.setNamespaceAware(true);
		DocumentBuilder builder = domf.newDocumentBuilder();
		
		for( int i=1; i<args.length; i++ ) {
			Document dom = builder.parse(new File(args[i]));
			
			// test the verify method
			if(verifier.verify(dom.getDocumentElement()))
				System.out.println("valid");
			else
				System.out.println("invalid");
		
			// test VerifierHandler
			VerifierHandler handler = verifier.getVerifierHandler();
			com.sun.msv.util.xml.SAXEventGenerator.parse(dom,handler);
			if(handler.isValid())
				System.out.println("ok");
			else
				System.out.println("ng");
		
		}
	}
}
