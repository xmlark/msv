# RELAX NG CONVERTER

RELAX NG Converter is a tool to convert schemas written in various
schema languages to their equivalent in RELAX NG.

It supports schemas written in XML DTD, RELAX Core, RELAX namespace,
TREX, W3C XML Schema, and RELAX NG itself.

This release includes software developed by the Apache Software
Foundation (https://www.apache.org/).

## Usage

To convert a schema written in either RELAX Core, RELAX namespace, TREX,
or W3C XML Schema, enter the following:

```java
    java -jar rngconv.jar myschema.xsd &gt; result.rng
```

The converter detects the schema language automatically (except for XML
DTDs) and writes the result to "result.rng".

To convert an XML DTD, enter the following:

```java
    java -jar rngconv.jar -dtd myschema.dtd &gt; result.rng
```

Note that you need the -dtd option.

## License

The most of the code is licensed under the BSD license (see
license.txt). The only exception to this is a part of the code of XSDLib,
which was taken from Apache, which is licensed under ASL 1.1
(see Apache-LICENSE-1.1.txt)

## Known Limitations

* This software relies on the Multi-Schema Validator (MSV). Therefore
  any limitations of MSV apply also to this converter. See [[1]](https://xmlark.github.io/msv/core/) for
  more information.

* RELAX Core permits undeclared attributes to appear in instance
  documents. This semantics is not converted. Therefore, any undeclared
  attributes are considered invalid after the conversion.

* Identity constraints of W3C XML Schema are not converted. So if the
  source schema uses &lt;xsd:key&gt;,&lt;xsd:keyref&gt;, or &lt;xsd:unique&gt;, this
  converter ignores those declarations. The semantics of "ID", "IDREF",
  and "IDREFS" are converted properly.

* It may fail to convert some datatype definitions, especially those that
  are derived in a very complex way with lots of facets.

* Schemas that consist of multiple files are converted into one big
  RELAX NG grammar. This limitation can be used to parse modularized
  RELAX NG grammar and create a single monolithic grammar.

[1] Multi-Schema Validator (MSV)
     https://xmlark.github.io/msv/core/
