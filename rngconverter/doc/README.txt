======================================================================
                README FILE FOR SUN RELAX NG CONVERTER
                     Preview Version. June, 2001
                 Copyright (c) Sun Microsystems, 2001
Document written by Kohsuke Kawaguchi (kohsuke.kawaguchi@eng.sun.com)
======================================================================

Sun RELAX NG Converter is a tool to convert schemas written in various
schema languages to the equivalent ones written in RELAX NG.

It supports XML DTD, RELAX Core, RELAX namespace, TREX, and W3C XML
Schema Part 1.

This release includes software developed by the Apache Software
Foundation (http://www.apache.org/).

----------------------------------------------------------------------
USAGE
----------------------------------------------------------------------

To convert a schema written in either RELAX Core, RELAX namespace, TREX
, or W3C XML Schema, type as follows:

C:\>java -jar rngconv.jar myschema.xsd > result.rng

The converter will detect the schema language and writes the result to
"result.rng".

To convert an XML DTD, type as follows:

C:\>java -jar rngconv.jar -dtd myschema.dtd > result.rng



----------------------------------------------------------------------
KNOWN LIMITATIONS
----------------------------------------------------------------------

* This software relies on Sun Multi-Schema Validator(MSV). Therefore
  any restrictions of MSV are also that of this converter. See [1] for
  more information.

* RELAX Core permits undeclared attributes to appear in instance
  documents. This semantics is not converted.

* Identity constraints of W3C XML Schema are not converted. the
  semantics of "ID", "IDREF", and "IDREFS" should be properly converted.

* It may fail to convert a datatype definition, especially if it is
  derived in very complex way with a lot of facets.

* Schemas that consist of multiple files are converted into one big
  RELAX NG grammar. This limitation can be used to parse modularized
  RELAX NG grammar and creates single monolithic grammar.


[1] Sun Multi-Schema Validator
     http://www.sun.com/xml/@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

======================================================================
END OF README
