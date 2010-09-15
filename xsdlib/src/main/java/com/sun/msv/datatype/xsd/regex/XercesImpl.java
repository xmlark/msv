package com.sun.msv.datatype.xsd.regex;

import org.apache.xerces.impl.xpath.regex.RegularExpression;

import java.text.ParseException;

/**
 * {@link RegExpFactory} by Xerces.
 *
 * @author Kohsuke Kawaguchi
 */
final class XercesImpl extends RegExpFactory {
    public RegExp compile(String exp) throws ParseException {
        final RegularExpression re;

        try {
            re = new RegularExpression(exp,"X");
        } catch ( org.apache.xerces.impl.xpath.regex.ParseException e ) {
            throw new ParseException(e.getMessage(),e.getLocation());
        }

        return new RegExp() {
            public boolean matches(String text) {
                return re.matches(text);
            }
        };
    }

}
