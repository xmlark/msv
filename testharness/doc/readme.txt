======================================================================
      README FILE FOR RELAX NG CONFORMANCE TEST HARNESS FOR JAVA
                 Copyright (c) Sun Microsystems, 2001

            Document by Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
                                                     $Revision$
======================================================================

RELAX NG Conformance Test Harness for Java is an Open-source Java
framework to test the conformance of RELAX NG processors.



----------------------------------------------------------------------
OVERVIEW
----------------------------------------------------------------------

This framework parses the test suite files and then use JUnit to test
RELAX NG processor. So first you need a test suite file.
See the examples folder of the distribution and "testSuite.rng" file
for how to write a test suite file.

Then, you need a RELAX NG processor and a driver implementation for
that processor. The driver impl for Jing is included as "jingDriver.jar"
in the distribution. See javadoc for how to implement a driver for
arbitrary RELAX NG processors.


To run a test, enter the following command:

$ java -jar rngtsth.jar <driver impl class name> <test suite file> ...

For example, to test the conformance of Jing with example1.rts and
example2.rts,

$ java -jar rngtsth.jar jing.IValidatorImpl example1.rts example2.rts


Your classpath must include junit.jar, jing.jar and jingDriver.jar.



======================================================================
END OF README
