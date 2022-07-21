# Multi-Schema Validator Toolkit

The core component of this toolkit is the Multi-Schema XML Validator (MSV). It is a Java technology tool to validate XML documents against several kinds of XML schemata. It supports RELAX NG, RELAX Namespace, RELAX Core, TREX, XML DTDs, and a subset of XML Schema Part 1.
<br/>
Most outstanding is the design of MSV core using the [Abstract grammar model (AGM)](https://xmlark.github.io/msv/nativeAPI.html). This is a schema-independent grammar model. All supported schemata are parsed into this internal representation. This model, coupled with the grammar reader, may be useful for other applications. For instance, two use cases are the generation of source code using [schema2template](https://tdf.github.io/odftoolkit/generator/index.html) or the [ODF Validator](https://tdf.github.io/odftoolkit/conformance/ODFValidator.html).
<br/>The builds of all MSV sub-projects were tested sucessfully using JDK 8, JDK 11 and JDK 17 on Windows and Linux.

## Overview Sub Project

MSV consists of a number of sub-projects, the main projects in bold. Each sub-projects has its own directory, its own build script, etc.

| sub-project       | description & dev guide reference                                                                                                    |
|:------------------|:-------------------------------------------------------------------------------------------------------------------------------------------|
| **[xsdlib](./xsdlib)**        | **[XML Schema Datatype (XSD) Library](./docs/xsdlib/README.md)**<br/>An implementation of W3C XML Schema Part 2 [(see xsdlib JavaDoc)](https://xmlark.github.io/msv/xsdlib/api/index.html). |
| [testharness](./testharness/)       | [Test harness](./docs/testharness/README.md)<br/>Used to parse composite test suite files (.ssuite).                                                                   |
| **[msv core](./msv)**      | **[Multi-Schema XML Core Validator](./docs/core/index.html)**<br/>A schema model and validator implementation [(see MSV Core JavaDoc)](https://xmlark.github.io/msv/core/api/index.html).</br>Dependent on XSDLib and testharness.                       |
| **[generator](./generator/)**     | **[XML Instance Generator](./docs/generator/README.md)** A tool that produces valid XML documents by reading a schema. Dependent on MSV.                                 |
| [schmit](./schmit/)            | [MSV SCHema In Transformation XSLT add-on (Schmit)](./docs/schmit/readme.html)<br/>XSLT Extension For Schema Annotation.                                            |
| [relames](./relames/)           | [Multi-Schema XML Validator Schematron add-on](./docs/relames/README.md)<br/>An experimental implementation of RELAX NG + Schematron validation. Dependent on MSV. |
| **[rngconverter](./rngconverter/)**  | **[RELAX NG Converter](./docs/rngconverter/README.md)**<br/>reads a schema and produces an equivalent RELAX NG schema. Dependent on MSV.                                    |
| [tahiti](./tahiti/)            | [Data-binding implementation](./docs/tahiti/README.md)                                                                                         |
| [trexconverter](./trexconverter/)     | [TREX Converter](./docs/trexconverter/README.md)<br/>Reads a schema and produces an equivalent TREX pattern.                        |

## Installation

All subprojects are are available from
[Maven Central](https://search.maven.org/search?q=g:net.java.dev.msv).

## Contributing

You have three options if you have a feature request, found a bug or
simply have a question about System Rules.

* [Write an issue. (soon available)](https://github.com/xmlark/msv/issues/new)
* Create a pull request. (See [Understanding the GitHub Flow](https://guides.github.com/introduction/flow/index.html))
* [Write a mail to our mailing list (soon available)](mailto:svanteschubert@apache.org)

The basic coding style is described in the
[EditorConfig](http://editorconfig.org/) file `.editorconfig`.

## Development Guide

The MSV Toolkit development documentation can be found [at our GitHub pages](https://xmlark.github.io/msv/).</br>
