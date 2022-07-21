# Control the compiler

Although the compiler takes every effort to extract a meaningful mapping
from the schema, but often it is not wise enough. By annotating schema
files, you can control the way the compiler maps schemas to Java
classes.

Tahiti defines a set of global attributes to annotate RELAX NG schema,
just like XLink does. All the attributes are placed in the
“http://www.sun.com/xml/tahiti/” namespace.

Tahiti defines only three attributes.

| Name    | Type                                         | Meaning                                                                                                                          |
| ------- | -------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------- |
| role    | class/interface/field/superClass/none/ignore | it controls how a RELAX NG pattern will be mapped to a Java item.                                                                |
| name    | Java identifier name                         | it provides a hint to the compiler about the identifier it should use. (e.g., class name, field name, etc.)                      |
| package | Java package name                            | It specified the package name in which the generated classes will reside. This field should be something like "org.example.foo". |

Tahiti annotation attributes

The following sections will guide you through each attributes and how
you can use them to obtain what you want.

# The role attribute

The role attribute is the most important annotation. It controls how
RELAX NG grammar should be mapped to a Java item.

The value of the role attribute can be one of
"class","interface","field","superClass","none", and "ignore".

## "none" - Suppress unnecessary classes

`role="none"` is an instruction to the compiler not to generate a class
for the particular pattern. Since the compiler usually maps \<element\>
patterns to Java classes, `role="none"` is usually used on \<element\>
patterns.

Sometimes the compiler generates Java classes that look unnecessary.
Consider the following grammar:

```xml
    <element name="header">
      <interleave>
        <element name="meta"><text/></element>
        <optional>
          <element name="info" t:role="none">
            <element name="author"><text/></element>
            <element name="version"><text/></element>
          </element>
        </optional>
      </interleave>
    </element>
```

Without the role attribute, The compiler will generate one Java class
for the "info" element, and another one for the "header" element. By
specifying the role attribute as none, the compiler will suppress a Java
class for the "info" element. As a result, the Header class will have
the author field and the version field.

## "ignore" - Ignore a part of the document

`role="ignore"` is an instruction to the compiler to ignore the
specified part of the grammar.

Many vocabularies (e.g., RELAX NG, W3C XML Schema) allows their
instances to be annotated by attributes from other namespaces. Many
grammars also provides a place holder element inside which elements from
other namespaces can appear (e.g., \<appInfo\> tag of W3C XML Schema).

For example, RELAX NG schema for RELAX NG has the following definition,
which allows any element of RELAX NG to have attributes from other
namespaces.

```xml
    <define name="common-atts">
      ....
      <zeroOrMore t:role="ignore">
        <attribute>
          <not>
            <choice>
              <nsName/>
              <nsName ns=""/>
            </choice>
          </not>
        </attribute>
      </zeroOrMore>
    </define>
```

Without the role attribute, the compiler will create a class to capture
each attribute from foreign namespaces. But chances are, you don't care
about those attributes.

With the role attribute as "ignore" on the zeroOrMore pattern, the
generated Java object model will ignore the entire descendants of that
pattern. In this case, this means that the all attributes from foreign
namespaces are ignored.

The effect of `role="ignore"` is not affected by the \<ref\> pattern.
The following example will result in the object model that only cares
about the contents of the head tag (and ignores the entire contents of
the body tag.)

```xml
    <element name="html">
      <optional>
        <ref name="head"/>
      </optional>
      <choice t:role="ignore">
        <ref name="frameset"/>
        <ref name="body"/>
      </choice>
    </element>
```

## "class" - Specifies a Java class

Sometimes, you want the compiler to map a certain pattern to a Java
class. `role="class"` is an instruction to the compiler to generate a
class for a particular pattern.

Consider the following example:

```xml
    <attribute name="distanceList">
      <list>
        <zeroOrMore>
          <group t:role="class" t:name="Length">
            <data type="integer"/>
            <choice><!-- unit -->
              <value>mm</value>
              <value>m</value>
              <value>km</value>
            </choice>
          </group>
        </zeroOrMore>
      </list>
    </attribute>
```

The `role` attribute forces the compiler to create a Java class, whose
name "Length" is also specified by the name attribute. The Length class
will have two fields; one for the integer value and another for the
unit.

Note that in the above example, an otherwise unnecessary \<group\>
pattern is added so that the Tahiti attributes can creep in. If we
specify the role attribute on the zeroOrMore pattern, then the class
will be generated for the entire list of distances, not for one
distance.

As done in the above example, sometimes you may want to modify a schema
a bit so that you can annotate it in the way you want.

## "field" - Specifies a field

`role="field"` is an instruction to the compiler to create a field and
accomodate child objects into that field.

The primary use case of this instruction is to change the way fields are
created. Consider the following example:

```xml
    <element name="grammar">
      <zeroOrMore>
        <choice>
          <ref name="start-element"/>
          <ref name="define-element"/>
          <ref name="include-element"/>
        </choice>
      </zeroOrMore>
    </element>
```

This is a (slightly modified) fragment of the RELAX NG schema for RELAX
NG. Let's assume that the compiler will generate four classes. The
Grammar class for the grammar element, the Start class for the start
element, the Define class for the define element, and finally the
Include class for the include element.

Since the content model of the grammar element is zero-or-more-ed choice
of three elements, the compiler will generate one field for the Grammar
class to store all three kinds of objects created for child elements.

But in this case, you may want to have three fields: one for the Include
class, one for the Define class, and one for the Start class. To do
this, use `role="field"` as follows:

```xml
    <element name="grammar">
      <zeroOrMore>
        <choice>
          <ref name="start-element" t:role="field" t:name="starts"/>
          <ref name="define-element" t:role="field" t:name="defines"/>
          <ref name="include-element" t:role="field" t:name="includes"/>
        </choice>
      </zeroOrMore>
    </element>
```

In this way, three fields ("starts","defines", and "includes") are
created for each child classes. The starts field will receive objects of
the Start class, so on, so forth.

You can specify the same name for multiple `role="field"` declaration.
In RELAX NG, the start pattern and the define pattern is very similar.
The following annotation will generate two fields for the Grammar class.

```xml
    <element name="grammar">
      <zeroOrMore>
        <choice>
          <ref name="start-element" t:role="field" t:name="defines"/>
          <ref name="define-element" t:role="field" t:name="defines"/>
          <ref name="include-element" t:role="field" t:name="includes"/>
        </choice>
      </zeroOrMore>
    </element>
```

Now the defines field will receive instances of both the Define class
and the Start class.

Take the opposite example. If you want to ensure that all three child
classes are stored into one field, then you can place the instruction to
the choice pattern or the zeroOrMore pattern.

```xml
    <element name="grammar">
      <zeroOrMore t:role="field" t:name="declarations">
        <choice>
          <ref name="start-element" />
          <ref name="define-element" />
          <ref name="include-element" />
        </choice>
      </zeroOrMore>
    </element>
```

It doesn't matter where you place the `role="field"` instruction, as
long as it is placed between the parent class and the child class.

## "interface" - Specifies an interface

`role="interface"` is an instruction to the compiler to generate an
interface and have designated classes implement that interface.

This is usually used in the choice pattern. Consider the following
example:

```xml
    <define name="inlineItem">
      <choice t:role="interface">
        <element name="bold"> ... </element>
        <element name="span"> ... </element>
        <element name="font"> ... </element>
      </choice>
    </define>
    <define name="foo">
      <element name="foo">
        <ref name="inlineItem"/>
      </element>
    </define>
```

By this instruction, the compiler will generate the InlineItem
interface. This interface is then implemented by the Bold class, the
Span class, and the Font class.

As a result of this, the Foo class will get the following signature:

```java
    class Foo {
      InlineItem value;
    }
```

Once `role="interface"` is specified, then this interface will be used
throughout in the grammar. If there is a bar element like this:

```xml
    <element name="bar">
      <choice>
        <element name="span"> ... </element>
        <element name="font"> ... </element>
      </choice>
    </element>
```

Then the corresponding Bar class will also get the field of the
InlineItem type.

`role="interface"` can be nested to create a hierarchy of interfaces.

```xml
    <define name="inlineItem">
      <choice t:role="interface">
        <element name="bold"> ... </element>
        <element name="span"> ... </element>
      </choice>
    </define>

    <define name="blockItem">
      <choice t:role="interface">
        <element name="table"> ... </element>
        <element name="list"> ... </element>
      </choice>
    </define>

    <define name="contentItem">
      <choice t:role="interface">
        <ref name="inlineItem"/>
        <ref name="blockItem"/>
      </choice>
    </define>
```

In this way, you'll get the following type hierarchy.

```java
    interface ContentItem {}
    interface InlineItem extends ContentItem {}
    interface BlockItem extends ContentItem {}

    class Bold implements InlineItem {}
    class Span implements InlineItem {}
    class Table implements BlockItem {}
    class List implements BlockItem {}
```

## "superClass" - Designates the super class

Tahiti allows you to specify the implementation inheritance by using
`role="superClass"`. Since the extraction of this information is very
difficult, the compiler will never attempt to automatically detect the
implementation inheritance relationships.

To designate the super class, place the `role="superClass"` instruction
between the derived class and the base class like this:

```xml
    <define name="expression">
      <choice>
        <element name="add">
          <ref name="binaryOperator" t:role="superClass"/>
        </element>
        <element name="sub">
          <ref name="binaryOperator" t:role="superClass"/>
        </element>
      </choice>
    </define>

    <define name="binaryOperator" t:role="class"/>
      <ref name="expression"/>
      <ref name="expression"/>
    </define>
```

The compiler will automatically generate the Add class and the Sub
class. And since their content model has the `role="superClass"`
instruction, the designated BinaryOperator class will be used as the
super class of these two classes.

The following pattern will achieve the same result:

```xml
    <define name="expression">
      <choice>
        <element name="add">
          <ref name="binaryOperator" />
        </element>
        <element name="sub">
          <ref name="binaryOperator" />
        </element>
      </choice>
    </define>

    <define name="binaryOperator" t:role="superClass"/>
      <group t:role="class"/>
        <ref name="expression"/>
        <ref name="expression"/>
      </group>
    </define>
```

As you see, there is no restriction about the place you can put the
`role="superClass"` instruction as long as it is between the derived
class and the base class.

However, there is one important restriction about the use of this
instruction. Intuitively, the derived class must always have one and
only one instance of the base class. Consider the following example:

```xml
    <element name="add">
      <zeroOrMore t:role="superClass">
        <ref name="binaryOperator" />
      </zeroOrMore>
    </element>

    <define name="binaryOperator">
      <group t:role="class">
        <ref name="expression"/>
        <ref name="expression"/>
      </group>
    </define>
```

In this example, the Add class can contain more than one instance of the
BinaryOperator classes. Apparently, this doesn' match with the fact that
the BinaryOperator class is the base class of the Add class. The use of
the `role="superClass"` instruction in this way is prohibited.

## The name attribute

The name attribute is a supplementary instruction that gives the
compiler the information about the name of classes, interfaces, and
fields.

As you have seen in the explanation of the role attribute, the name
attribute is usually coupled with the role attribute. But actually, the
name attribute can be used by itself to specify the name for classes and
fields automatically generated by the compiler.

Let's consider the following example.

```xml
    <zeroOrMore>
      <element>
        <anyName/>
        <attribute name="firstName" />
        <attribute name="lastName" />
      </element>
    </zeroOrMore>
```

The compiler will find that this element should be mapped to a Java
class. Usually, this generated class will be named after the name of
element/attribute names or the nearest name specified for \<define\>.
But in this particular case, there is no such hint.

To help the compiler, you can specify the name attribute on the element
pattern.

```xml
    <zeroOrMore>
      <element t:name="Name">
        <anyName/>
        <attribute name="firstName" />
        <attribute name="lastName" />
      </element>
    </zeroOrMore>
```

In this way, the compiler will generate the Name class, which will have
the firstName field and the lastName field.

Similarly, the name attribute can be also used to rename the field name.
For example, if you want to have the given field and the family field,
then you can do that by using the name attribute.

```xml
    <zeroOrMore>
      <element t:name="Name">
        <anyName/>
        <attribute name="firstName" t:name="given"/>
        <attribute name="lastName" t:name="family"/>
      </element>
    </zeroOrMore>
```

This technique is also useful to solve the name collision to Java
reserved names.

## The package attribute

The package attribute is used to specify the package name in which the
generated classes will be placed. Usually, you want to place everything
into one package. In that case, you should specify the package attribute
to the document element like this:

```xml
    <?xml version="1.0"?>
    <grammar xmlns="http://relaxng.org/ns/structure/0.9" xmlns:t="http://www.sun.com/xml/tahiti/"
        t:package="org.example.test">

      ....
    </grammar>
```

All the classes generated from this grammar, including included
grammars, will be placed into this package.

Actually, the package attribute can be specified anywhere in the
grammar, as in the following example. Once specified, it is effective on
all descendants, unless a new package name is specified.

```xml
    <?xml version="1.0"?>
    <grammar xmlns="http://relaxng.org/ns/structure/0.9" xmlns:t="http://www.sun.com/xml/tahiti/"
        t:package="org.example.test">

      <start>
        <element name="root">
          <ref name="child"/>
        </element>
      </start>

      <define name="child" t:package="org.example.test.foo">
        <element name="sub1">
          <element name="sub2" t:package="com.abcdef.bar">
            <text/>
          </element>
        </element>
      </define>
    </grammar>
```

In the above example, the Root class will go to the "org.example.test"
package, the Sub1 class will go to the "org.example.test.foo" package,
and the Sub2 class will go to the "com.abcdef.bar" package.
