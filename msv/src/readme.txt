
TODO
----

* "double" type. if value is out of range of java.lang.Double,
  it doesn't accept the value, although it is a valid double value.
* implement SmallDateTime/SmallTimeDuration
* test derivation and its error.
  - it can wait.
* prepare more test cases for DataTypeTest.xml
* make sure that separators are correctly handled in ListType.

Known limitation
----------------

* 'NOTATION'

 this type is validated by 'string' type. The Oct/24/2000 version
of the spec says NOTATION is derived from QName. This implies that

<simpleType name="myNotation">
  <restriction base="NOTATION" xmlns:myns="myURI">
    <enumeration value="myns:foo" />
    <enumeration value="myns:bar" />
  </restricted>
</simpleType>

should match the following:

<root xmlns:myURI = "myURI">
  <someElement a="myURI:foo" />

Even worse, if you understand XML Schema's NOTATION in terms of that of XML1.0,
you would be tempted to write as follows.

<simpleType name="mathType" xmlns="XSD">
  <restriction base="NOTATION">
    <enumeration value="tex" />
    <enumeration value="mathML" />
  </restricted>
</simpleType>

But this is a big mistake.
Because QName "tex" is mapped to {XSD}:tex, so it cannot match the following
statement.

<root xmlns="myURI">
  <formula type="tex"> x \subseteq y </formula>
</root>

This surprising result is enough to rethink about the rightness of
the design of NOTATION.

Also, one of the member of Schema WG acknowledged that "there is no connection
between them(NOTATION in XML Schema) and XML 1.0 Notations," whereas the spec
says NOTATION type is just for compatibility purpose.

I think that the Schema WG is aware of these problems along with several other
problems regarding NOTATION. Thus NOTATION type is highly likely to be changed
in future relase.

Therefore, at this moment, it is impossible to seriously implement NOTATION
type in consistent manner.



Design of ModuleReader
----------------------

* Use stack of State objects. State class has startElement method,
  which performs something useful to parse the module.

* Use factory pattern to instanciate particles, elementRule, etc.
  So that some application can extend the behavior of schema object model.
  They might want to retrieve extra information from schema.



Unresolved issue
----------------

* consistency check of RangeFacet with respect to the facets
  specified in somewhere of the derivation chain.

* enumeration may not be able to use hash, due to date/time related value types.

* what should it do when it find undefined facets (like "abcdef")

* semantics of "whiteSpace" facet in derivation.
  もし派生型でwhiteSpaceファセットを上書きされたら、基底型の指定が
  オーバーライドされるの？それとも、基底型は基底型で、派生型は派生型で、
  別にチェックされるのだろうか。

* ID / IDREF is not implemented. Due to the design issue.

  Treating ID and IDREF as a datatype is not a good idea, due to
  their uniqueness constraint. In case of RELAX, we have nice
  restriction that makes it possible to "quick-hack" IDType and IDREFType,
  so that we can treat them as if they were ordinary types.
  
  However, if we choose TREX ..... oh, men.

* ENTITY / NOTATION

  This is another example of complex datatypes. This datatype is dependent
  to the document...
  
  We have to reconsider the design to meet them.

* what happens if someone specifies "scale" for "integer"?

* what happens if someone specifies maxInclusive="100" for nonPositiveInteger?

* possible limit of maximum precision?
  
* 0 = 0.0 for decimal ? ( currently implemented in this way)
