======================================================================
                  README FILE FOR SUN XML GENERATOR
                      Preview Version.  Jun, 2001
                 Copyright (c) Sun Microsystems, 2001
Document written by Kohsuke Kawaguchi (kohsuke.kawaguchi@eng.sun.com)
                                                    $Revision$
======================================================================

Sun XML Generator is a Java tool to generate various XML instances from
several kinds of schemata. It supports DTD, RELAX Namespace, RELAX Core,
TREX, and a subset of W3C XML Schema Part 1. This release includes
software developed by the Apache Software Foundation [1].


----------------------------------------------------------------------
OVERVIEW
----------------------------------------------------------------------

This tool is a command line tool that generates both valid and invalid
instances from schemata.

See HowToUse.html for details.


----------------------------------------------------------------------
CURRENT STATUS
----------------------------------------------------------------------

This release relies on Sun Multi Schema Validator[2] for parsing
schemata. Therefore, any limitation that applies to MSV will apply to
this release.


----------------------------------------------------------------------
REFERENCES
----------------------------------------------------------------------
[ 1] Apache Software Foundation
      http://www.apache.org/
[ 2] Sun Multi Schema Validator
      http://www.sun.com/xml

======================================================================
END OF README
