======================================================================
                       README FILE FOR "TAHITI"
                     Preview Version   July, 2001
                 Copyright (c) Sun Microsystems, 2001
   Document written by Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
                                                    $Revision$
======================================================================

"Tahiti" is a Java tool to generate a Java object model from a schema.
Currently, it supports RELAX NG and a subset of W3C XML Schema Part 1.
This release includes software developed by the Apache Software
Foundation (http://xml.apache.org/).


----------------------------------------------------------------------
OVERVIEW
----------------------------------------------------------------------

This tool is a command line tool that can compile RELAX NG grammar or
W3C XML Schema Part 1 to a Java object model that in turn parses XML
documents written in that schema.

The automatic Java object model generation makes it easy to read/write
XML documents from your application. Also, This tool lets you heavily
customize the generated object model.

See UsersGuide.html for details.


This tool should be considered as alpha-quality. And any feedback is
more than welcome! Please send it to kohsuke.kawaguchi@sun.com


======================================================================
END OF README
