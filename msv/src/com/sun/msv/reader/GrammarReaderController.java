package com.sun.tranquilo.reader;

import org.xml.sax.Locator;

/**
 * Event notification interface for controlling grammar parsing process.
 * 
 * <ol>
 *  <li>receives notification of errors and warnings while parsing a grammar
 *  <li>controls how inclusion of other grammars are processed.
 * </ol>
 */
public interface GrammarReaderController
{
	void warning( Locator[] locs, String errorMessage );
	void error( Locator[] locs, String errorMessage, Exception nestedException );
}
