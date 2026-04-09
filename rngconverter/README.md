# RELAX NG Converter

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
    java -jar msv-rngconverter-<VERSION>-jar-with-dependencies.jar myschema.xsd result.rng
```

The converter detects the schema language automatically (except for XML
DTDs) and writes the result to "result.rng".

To convert an XML DTD, enter the following:

```java
    java -jar msv-rngconverter-<VERSION>-jar-with-dependencies.jar myschema.dtd result.rng
```

## Follow-up Documenation

Please find the complete documentation [here](../docs/rngconverter/README.md)!
