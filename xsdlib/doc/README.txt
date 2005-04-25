======================================================================
            README FILE FOR THE SUN XML DATATYPES LIBRARY
                          version @@VERSION@@
              Copyright (c) Sun Microsystems, 2001-@@YEAR@@
Document written by Kohsuke Kawaguchi (kohsuke.kawaguchi@eng.sun.com)
======================================================================

Sun XML Datatypes Library, Sun's Java[tm] technology implementation of
W3C's XML Schema Part 2 (http://www.w3.org/TR/xmlschema-2/), is
intended for use with applications that incorporate XML Schema Part 2.

This preview version implements the recommendation version
(http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/) of the W3C XML
Schema Part 2 Datatype.

This product includes software developed by the Apache Software
Foundation (http://www.apache.org/).

----------------------------------------------------------------------
SAMPLE CODE FILE
----------------------------------------------------------------------

This distribution of the XML Datatypes Library includes a sample class
file, src/com/sun/tranquilo/datatype/CommandLineTester.java, which is
provided as a guide for implementing your own Java classes with the
Datatypes Library.

----------------------------------------------------------------------
LICENSE
----------------------------------------------------------------------

The most of the code is licensed under the BSD license (see
license.txt). The only exception to this is the code taken from Apache,
which is licensed under ASL 1.1 (see Apache-LICENSE-1.1.txt)

In the source tree, the code taken from Apache is hosted in 'src-apache'
so that XSDLib can be built without Apache code if necessary.


----------------------------------------------------------------------
KNOWN LIMITATIONS
----------------------------------------------------------------------

1. Types "float" and "double": the spec says a lexical value must be
   mapped to the closest value in the value space. However, This
   library cannot accept values that are larger than the maximum value
   for that type or smaller than the minimum value for that type. This
   should not be a problem for most users.

3. "NOTATION" type validates like the "token" type.

4. "length", "minLength", and "maxLength" facets are effectively
   limited to the value 2147483647. Values above this limit are
   recognized, but will be treated as this limit. Items larger than
   this limit will not be validated correctly. This limitation has no
   practical impact.

5. Regarding "length" and "min/maxLength" facets of "anyURI," the spec
   does not define what is the unit of length. This version implements
   "length" facet as being in units of XML characters in the lexical
   space.

6. Regarding "length" and "min/maxLength" facets of "QName," again the
   specification does not define the unit of length. This version
   implements "length" facet as being in units of XML characters in
   the value space ( # of chars in namespace URI + local part ). Users
   are strongly discouraged from applying length-related facets to
   "QName" type.

7. "anyURI" (formerly "uriReference") is made to accept several IP v6
   addresses like "::192.168.0.1," which are not accepted by the
   original BNF specified in RFC 2373. This modification should be
   considered as a "bug fix." Although the BNF specified in RFC 2373
   has several other problems, those are not fixed. For example, the
   current release accepts "1:2:3:4:5:6:7:8:9," which is not a valid
   IP v6 address.

8. "language" type is implemented in accordance with RFC 1766, and
   language identifiers are treated in a case-insensitive way. XML
   SchemaPart 2 says that the lexical space of the language type will
   be as defined in XML1.0 Recommendation 2nd edition, but that
   production was thrown away in the 2nd edition. Furthermore, the
   derivation shown inXML Schema Part 2 does not correctly implement
   the definition given in RFC 1766, so apparently there is a problem
   in the definition of the language type. However, by making
   "language" case-insensitive, it is no longer a derived type of
   "token" type.

9. Regarding "base64Binary" type, RFC 2045 states that "any characters
   outside of the base64 alphabet are to be ignored in base64-encoded
   data." This makes "validation" of base64Binary meaningless. For
   example, <picture>))))</picture> is considered as valid
   base64Binary. Developers should keep this in mind.

10. minInclusive, maxInclusive, minExclusive, and maxExclusive facets
    of date/time related types do not work properly. XML Schema Part 2
    is broken as regards the order relation of these types. This also
    affects the behavior of the "enumeration" facet (since equality is
    a part of order-relation).

    See Kawaguchi's comments to www-xml-schema-comments@w3.org
    (http://lists.w3.org/Archives/Public/www-xml-schema-comments/) for
    details 1, 2, 3, and 4:

    http://lists.w3.org/Archives/Public/www-xml-schema-comments/2001JanMar/0365.html
    http://lists.w3.org/Archives/Public/www-xml-schema-comments/2001JanMar/0366.html
    http://lists.w3.org/Archives/Public/www-xml-schema-comments/2001JanMar/0367.html
    http://lists.w3.org/Archives/Public/www-xml-schema-comments/2001JanMar/0368.html

======================================================================
END OF README
