package batch.model;

import org.relaxng.testharness.model.*;
import java.io.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import com.sun.resolver.tools.CatalogResolver;

/**
 * {@link XMLDocument} implementation by using a File object.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class XMLDocumentImpl implements XMLDocument
{
	private final File file;
	private final RNGHeader header;
	private static final CatalogResolver resolver = new CatalogResolver();
	
	XMLDocumentImpl( File _file ) {
		file = _file;
		if( file.isDirectory() )
			throw new Error("assertion failed");
		
		header = new RNGHeaderImpl(file);
	}
	
	public RNGHeader getHeader() { return header; }
	
	public void getAsSAX( ContentHandler handler ) throws Exception {
		XMLReader reader = DirectoryTestReader.saxFactory.newSAXParser().getXMLReader();
		
		reader.setEntityResolver(resolver);
		reader.setContentHandler(handler);
		reader.parse(file.getPath());
	}
	
	public Document getAsDOM() throws Exception {
		return DirectoryTestReader.domFactory.newDocumentBuilder().parse(file);
	}
	
	public InputSource getAsInputSource() throws Exception {
		InputSource is = new InputSource(file.getAbsolutePath());
		is.setSystemId(file.getAbsolutePath());
		return is;
	}
}
