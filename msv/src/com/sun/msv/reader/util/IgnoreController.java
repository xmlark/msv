package com.sun.tranquilo.reader.util;

import com.sun.tranquilo.reader.GrammarReaderController;
import org.xml.sax.Locator;

/**
 * Default implementation of GrammarReaderController.
 * 
 * This class ignores every errors and warnings.
 */
public class IgnoreController implements GrammarReaderController
{
	public void warning( Locator[] locs, String errorMessage ) {}
	public void error( Locator[] locs, String errorMessage, Exception nestedException ) {}
}
