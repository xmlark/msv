package batch.driver;

import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.dtd.DTDReader;
import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.driver.textui.ReportErrorHandler;
import com.sun.msv.grammar.*;
import com.sun.msv.verifier.IVerifier;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.resolver.tools.CatalogResolver;
import org.relaxng.testharness.validator.*;
import org.relaxng.testharness.model.*;
import org.xml.sax.*;
import java.io.File;
import java.net.URL;

public class DTDValidator extends AbstractValidatorExImpl {

	public Grammar parseSchema( InputSource is, GrammarReaderController controller ) throws Exception {
		is.setSystemId( toURL(is.getSystemId()) );
		Grammar g = DTDReader.parse(is,controller,"",new ExpressionPool() );
		if(g==null)		return null;
		return g;
	}
	
	protected String toURL( String path ) throws Exception {
		path = new File(path).getAbsolutePath();
		if (File.separatorChar != '/')
			path = path.replace(File.separatorChar, '/');
		if (!path.startsWith("/"))
			path = "/" + path;
//		if (!path.endsWith("/") && isDirectory())
//			path = path + "/";
		return new URL("file", "", path).toExternalForm();
	}
}
