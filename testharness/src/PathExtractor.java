import java.io.File;

/**
 * Extracts the path part from "file://" URL.
 *
 * For example, extract("file://home/abc/def/ghi.xml")
 * returns "/home/abc/def/ghi"
 */
public class PathExtractor {
	public static String extract( String uri ) {
		
		String path = uri.substring(5); // file:
		
		int idx = path.lastIndexOf('.');
		path = path.substring(0,idx);
		
		if (File.separatorChar == '\\') {
			path = path.replace('/', '\\');
			while(path.charAt(0)=='\\')
				path = path.substring(1);
		} else {
			while(path.startsWith("//"))
				path = path.substring(1);
		}
		return path;
	}
}