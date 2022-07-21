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

## How to embed Schematron

This release supports Schematron constraints to be embedded in
the &lt;element&gt; pattern of RELAX NG:

```xml
<define name="foo" xmlns:s="http://www.ascc.net/xml/schematron">
  <element name="foo">
    <!-- content model definition in RELAX NG, as usual -->
    ...

    <!-- embedded schematron constraints -->
    <s:assert test="@min < @max">
      the max attribute must be greater than the min attribute.
    </s:assert>
    <s:assert test="count(*)>1">
      at least one child element is necessary.
    </s:assert>
    <!-- as many as you want -->
  </element>
</define>
```

In this example, for every "foo" element, two assertions are checked.
In general, validation is performed in the following way: whenever
an element mathces a pattern, schematron constraints are checked for
that element.

In plain Schematron, a &lt;rule&gt; element determines the context node
against which those assertions are evaluated.
In Schematron-annotated RELAX NG, on the other hand, the element
that matches the &lt;element&gt; pattern will become the context
information instead.

Relames also supports the use of &lt;rule&gt; elements in RELAX NG grammar.

```xml
<define name="root" xmlns:s="http://www.ascc.net/xml/schematron">
  <element name="root">
    <!-- content model definition in RELAX NG, as usual -->
    ...

    <!-- for any hotel element found within this element -->
    <s:rule context="hotel">
      <s:assert test="count(*)>1">
        at least one child element is necessary.
      </s:assert>
    </s:rule>
  </element>
</define>
```

First, nodes that match the context attribute are computed.
Then assertions are tested for each node.

Relames can also handle Schematron's &lt;pattern&gt; element --- it basically
just ignores the &lt;pattern&gt; element itself and process &lt;rule&gt;s in it
directly.

Relames also handles Schematron's &lt;ns&gt; element. They can appear in
anywhere in the RELAX NG schema, and they affect other schematron
elements that appear as descendants of siblings. IOW, the following works:

```xml
 <element name="foo">
   <s:ns prefix="abc" uri="..." />
   <s:report test="abc:someNode" ... />
   <element name="child">
     <s:report test="abc:someNode" ... />
     ...
   </element>
 </element>
```

For the backward compatibility with earlier versions of relames,
Namespace prefixes found in XPath expression (like the one above)
are also resolved through xmlns declarations in the grammar file,
if it's not declared by <s:ns>. Note that the default namespace is
bound to the URI declared by the ns attribute of RELAX NG. Consider
the following example:

```xml
<grammar xmlns="http://relaxng.org/ns/structure/0.9"
         xmlns:foo="http://www.example.org/foo"
         ns="http://www.sun.com/xml">
  <start xmlns:s="http://www.ascc.net/xml/schematron">
    <element name="foo">
      <!-- content model definition in RELAX NG, as usual -->
      ...

      <s:assert test=" foo:abc | def ">
        ...
      </s:assert>
    </element>
  </start>
</grammar>
```

The XPath expression "foo:abc|def" will match
{http://www.example.org/foo}abc elements and {http://www.sun.com/xml}def
elements. Note that "def" does NOT match elements with the namespace
URI of "http://relaxng.org/ns/structure/0.9".

This release supports <rule>, <assert> and <report> of Schematron 1.3
and <pattern> and <ns> of Schematron 1.?. You can write as many
constraints as you want in one <element> pattern.

Annotated RELAX NG grammars are still interoperable in the sense that
other RELAX NG processors will silently ignore all Schematron constraints.

## Using from Command Line

The jar file can be used as a command-line validation tool.
Type as follows:

```java
    java -jar relames.jar
```

To get the usage screen.

## Using from your Program

The schematron extension can be used through JARV API [[5]](http://iso-relax.sourceforge.net/JARV/), which makes
it very simple to use this library from your application.

When you call the VerifierFactory.newInstance method, type as follows:

VerifierFactory factory = VerifierFactory.newInstance(
  "http://relaxng.org/ns/structure/1.0+http://www.ascc.net/xml/schematron");

to create a verifier factory from this extension library.

## Limitation

- id() function works correctly only if Xerces or Crimson is used as
  the DOM implementaion. This is because of the limitation in W3C DOM.

## References

[ 1] RELAX NG
      https://www.oasis-open.org/committees/relax-ng/</br>
[ 2] Schematron
      https://www.schematron.com/</br>
[ 3] Apache Software Foundation
      https://www.apache.org/</br>
[ 4] Xalan-Java
      https://xml.apache.org/xalan-j/</br>
[ 5] JARV API
      http://iso-relax.sourceforge.net/JARV/</br>
