package com.sun.tahiti.compiler.model;

import com.sun.tahiti.grammar.TypeItem;
import java.io.OutputStream;
import java.io.IOException;

/**
 * this interface will be implemented by the caller.
 */
public interface OutputResolver {
	/**
	 * the contents of the specified {@link TypeItem} will be sent
	 * to the returned DocumentHandler.
	 */
	OutputStream getOutput( TypeItem type ) throws IOException;
}
