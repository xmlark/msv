
Design of ModuleReader
----------------------

* Use stack of State objects. State class has startElement method,
  which performs something useful to parse the module.

* Use factory pattern to instanciate particles, elementRule, etc.
  So that some application can extend the behavior of schema object model.
  They might want to retrieve extra information from schema.

* 


Unresolved issue
----------------

* license of com.sun.tranquilo.datatype.FloatValueType / FloatingDecimal 
  or otherwise tranquilo requires JDK1.3, which is probably unacceptable.
  ( ... maybe JDK1.2.2? )
  
  DoubleValueType / DecimalValueType are the same.

* what should it do when it find undefined facets (like "abcdef")

* semantics of "whiteSpace" facet in derivation.
  もし派生型でwhiteSpaceファセットを上書きされたら、基底型の指定が
  オーバーライドされるの？それとも、基底型は基底型で、派生型は派生型で、
  別にチェックされるのだろうか。

* QName
  Do I have to check that the prefix is actually declared?
  if so, implementation needs a change.

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

* derivation by list
  Are tab/CR/LF valid separators, or not?
  Are more than one #x20 allowed as a single separator?
  -> Yes, because whiteSpace is fixed to "collapse"
