This example shows how a schema can be composed from multiple schemas and by looking at
instances.

--------------------------------------

It is often desirable if the schema location hints are not present in documents.
For example, when you are exchanging documents that adhere to some standards

Suppose you are exchanging documents that adhere to a fairly complicated standard,
such as UBL. It is often desirable:

   1. not to have schema locations inplanted in documents. Such references are hard
      or non-efficient to resolve correctly, and practically does more harm than good.
   
   2. to compose schemas from namespaces it uses.

This example does those. In particular, it uses catalog resolver to resolve
namespace URIs into schema documents. Try:

    java schemaLookup.Main catalog.cat test.xml
