# MULTI-SCHEMA XML VALIDATOR SCHEMATRON ADD-ON

The Multi-Schema XML Validator Schematron add-on is a Java tool
to validate XML documents against RELAX NG [[1]](https://www.oasis-open.org/committees/relax-ng/) schemas annotated with
Schematron schemas [[2]](https://www.schematron.com/). This release includes software developed by the Apache Software Foundation [[3]](https://www.apache.org/)

## Overview

By using this tool, you can embed Schematron constraints into RELAX NG
schemas. Then this tool validates documents against both RELAX NG grammar
and embedded schematron constraints. Schematron makes it easy to write
many constraints which are difficult to achieve by RELAX NG alone.

To validate documents with Schematron-annotated RELAX NG grammar, enter
the following command:

```java
    java -jar relames.jar MySchema.rng doc1.xml [doc2.xml ...]
```

To run the program, you must have Xalan-Java [[4]](https://xml.apache.org/xalan-j/) and JAXP-compliant
XML parser in your classpath.

## Follow-up Documenation

Please find the complete documentation [here](../docs/relames/README.md).!
