package batch.driver;

import org.xml.sax.InputSource;

import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.dtd.DTDReader;

public class DTDValidator extends AbstractValidatorExImpl {

    public Grammar parseSchema( InputSource is, GrammarReaderController controller ) throws Exception {
        Grammar g = DTDReader.parse(is,controller,new ExpressionPool() );
        if(g==null)        return null;
        return g;
    }
/*    
    protected String toURL( String path ) throws Exception {
        path = new File(path).getAbsolutePath();
        if (File.separatorChar != '/')
            path = path.replace(File.separatorChar, '/');
        if (!path.startsWith("/"))
            path = "/" + path;
//        if (!path.endsWith("/") && isDirectory())
//            path = path + "/";
        return new URL("file", "", path).toExternalForm();
    }
*/
}
