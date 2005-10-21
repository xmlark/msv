package com.sun.msv.schematron.grammar;

/**
 * Set of {@link SRule}s.
 *
 * @author Kohsuke Kawaguchi
 */
public class SPattern {
    public final SRule[] rules;

    public SPattern(SRule[] rules) {
        this.rules = rules;
    }
}
