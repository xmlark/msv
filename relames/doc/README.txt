======================================================================
   README FILE FOR SUN MULTI-SCHEMA XML VALIDATOR SCHEMATRON ADD-ON
                      Preview Version  July, 2001
                 Copyright (c) Sun Microsystems, 2001
Document written by Kohsuke Kawaguchi (kohsuke.kawaguchi@eng.sun.com)
                                                     $Revision$
======================================================================

The Sun Multi-Schema XML Validator Schematron add-on is a Java tool
to validate XML documents against RELAX NG [1] schemas annotated with
Schematron schemas [2]. This release includes software developed by
the Apache Software Foundation [3].


----------------------------------------------------------------------
OVERVIEW
----------------------------------------------------------------------

By using this tool, you can embed Schematron constraints into RELAX NG
schemas. Then this tool validates documents again both RELAX NG grammar
and embeded schematron constraints. Schematron makes it easy to write
many constraints which were difficult to achieve by RELAX NG alone.

To validate documents with Schematron-annotated RELAX NG grammar, enter
the following command:

$ java -jar relames.jar MySchema.rng doc1.xml [doc2.xml ...]



----------------------------------------------------------------------
HOW TO EMBED SCHEMATRON
----------------------------------------------------------------------

This release supports Schematron constraints to be embedded in
the <element> pattern of RELAX NG:

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


In this example, for every "foo" element, two assertions are checked.
In general, validation is performed in the following way: whenever
an element mathces a pattern, schematron constraints are checked for
that element.

In plain Schematron, a <rule> element supplies context information.
In Schematron-annotated RELAX NG grammars, on the other hand, an <element>
pattern supplies the context information instead.

This release supports <assert> and <report> of Schematron 1.3. You can
write as many constraints as you want in one <element> pattern.

Namespace prefixes found in XPath expression is resolved through xmlns
declarations in the grammar file. And the default namespace is bound to
the URI declared by the ns attribute of RELAX NG. Consider the following
example:

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

The XPath expression "foo:abc|def" will match
{http://www.example.org/foo}abc elements and {http://www.sun.com/xml}def
elements. Note that "def" does NOT match elements with the namespace
URI of "http://relaxng.org/ns/structure/0.9".

Annotated RELAX NG grammars are still interoperable. Other RELAX NG
processors that do not support Schematron will silently ignore all
Schematron constraints.



----------------------------------------------------------------------
REFERENCES
----------------------------------------------------------------------
[ 1] RELAX NG
      http://www.oasis-open.org/committees/relax-ng/
[ 2] Schematron
      http://www.ascc.net/xml/resource/schematron/schematron.html
[ 3] Apache Software Foundation
      http://www.apache.org/
======================================================================
END OF README
