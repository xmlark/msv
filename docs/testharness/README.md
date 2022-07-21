# RELAX NG CONFORMANCE TEST HARNESS FOR JAVA

RELAX NG Conformance Test Harness for Java is an Open-source Java
framework to test the conformance of RELAX NG processors.

## OVERVIEW

This framework parses the test suite files and then use JUnit to test
RELAX NG processor. So first you need a test suite file.
See the examples folder of the distribution and "testSuite.rng" file
for how to write a test suite file.

Then, you need a RELAX NG processor and a driver implementation for
that processor. The driver impl for Sun Multi-Schema Validator is
included as "msvDriver.jar" in the distribution. See javadoc for
how to implement a driver for arbitrary RELAX NG processors.


To run a test, enter the following command:

$ java -jar rngtsth.jar <driver impl class name> <test suite file> ...

For example, to test the conformance of MSV with foo.rts and bar.rts,

$ java -jar rngtsth.jar jing.IValidatorImpl foo.rts bar.rts

Your classpath must include junit.jar, msv.jar and msvDriver.jar.


## WRITING AN ADAPTOR

This framework provides the core functionality to parse test suite
files and perform tests by them.  However, to use this framework with
your own RELAX NG processor, you need to write an "driver", which
bridges the framework and the processor.

To implement a driver, you need to write two classes that implements
the org.relaxng.testharness.validator.IValidator interface and
the org.relaxng.testharness.validator.ISchema interface. 

See msvDriver-src.zip for an example. It should be easy if the processor
supports SAX or DOM based input.
