package com.sun.msv.schematron.reader;

import com.sun.msv.schematron.grammar.SPattern;

/**
 * @author Kohsuke Kawaguchi
 */
public interface SPatternReceiver {
    void onPattern(SPattern p);
}
