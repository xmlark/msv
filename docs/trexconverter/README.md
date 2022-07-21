# TREX CONVERTER

MSV TREX Converter is a tool to convert schemas written in various
schema languages to their equivalent in TREX.

It supports schemas written in XML DTD, RELAX Core, RELAX namespace,
RELAX NG, W3C XML Schema, and TREX itself.

This release includes software developed by the Apache Software
Foundation (https://www.apache.org/).

## Usage

To convert a schema written in either RELAX Core, RELAX namespace,
RELAX NG, or W3C XML Schema, enter the following:

```java
    java -jar rngconv.jar myschema.xsd > result.trex
```

The converter detects the schema language automatically (except for XML
DTDs) and writes the result to "result.trex".

To convert an XML DTD, enter the following:

```java
java -jar rngconv.jar -dtd myschema.dtd > result.trex
```

Note that you need the -dtd option.

## Known Limitations

* This software relies on Sun Multi-Schema Validator(MSV). Therefore
  any limitations of MSV apply also to this converter. See [[1]](https://xmlark.github.io/msv/core/) for
  more information.

* RELAX Core permits undeclared attributes to appear in instance
  documents. This semantics is not converted. Therefore, any undeclared
  attributes are considered invalid after the conversion.

* Identity constraints of W3C XML Schema and RELAX NG are not converted.
  So if the source schema uses &lt;xsd:key&gt;,&lt;xsd:keyref&gt;, or &lt;xsd:unique&gt;,
  this converter ignores those declarations.

* It may fail to convert some datatype definitions, especially those that
  are derived in a very complex way with lots of facets.

* Schemas that consist of multiple files are converted into one big
  TREX pattern. This limitation can be used to parse modularized
  TREX pattern and create a single monolithic pattern.

[1] Multi-Schema Validator https://xmlark.github.io/msv/core/
