package batch.model;

import org.relaxng.testharness.model.RNGHeader;
import java.io.File;

/**
 * {@link RNGHeader} implementation by using a File object.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
class RNGHeaderImpl implements RNGHeader
{
	private final File file;
	
	RNGHeaderImpl( File _file ) {
		file = _file;
	}
	
	public String getName() { return file.getPath(); }
	
	public String getProperty( String uri, String name ) {
		// filename property.
		if( name.equals("fileName") && uri.equals("") )
			return file.getName();
		
		return null;
	}
}
