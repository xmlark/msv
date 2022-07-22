# RELAX NG Conformance Test Harness for Java

RELAX NG Conformance Test Harness for Java is an Open-source Java
framework to test the conformance of RELAX NG processors.

## Overview

This framework parses the test suite files and then use JUnit to test
RELAX NG processor. So first you need a test suite file.
See the examples folder of the distribution and "testSuite.rng" file
for how to write a test suite file.

Then, you need a RELAX NG processor and a driver implementation for
that processor. The driver impl for Sun Multi-Schema Validator is
included as "msvDriver.jar" in the distribution. See javadoc for
how to implement a driver for arbitrary RELAX NG processors.

## Follow-up Documenation

Please find the complete documentation [here](../docs/testharness/README.md)!
