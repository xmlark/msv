
directory structure
-------------------
src				main MSV source codes
relaxng			RELAX NG datatype interfaces.
				Intended to be in the public domain.
rngconverter	RELAX NG Converter source code.
				(has dependency on src)
trexconverter	TREX Converter source code.
				(has dependency on src)
generator		XML Generator
				(has dependency on src)
test			Test harnesses.
				(has dependency on everything else)

testCases		schemas and instances that are used for
				the batch test.
testLog			the result of the batch test will be
				stored under this directory.

ref				reference to the external libraries.
				JavaCC is necessary.

doc				documents that will be attached to the
				distribution package. grouped by the product.

build			binary byte codes will go into this directory.


ant.xml			ant script to build everything. Contains a lot of
				targets. Read carefully.


To run tests
------------

To try individual test

	$ java batch.verifier.BatchVerifyTester

and see the usage.

To split ssuites,

	$ cd msv/testCases
	$ ant

To run the entire test,

	$ ant test

