======================================================================
            README FILE FOR SUN MULTI-SCHEMA XML VALIDATOR
                            Apr,2002 Version
              Copyright (c) Sun Microsystems, 2001-2002
Document written by Kohsuke Kawaguchi (kohsuke.kawaguchi@eng.sun.com)
                                                    $Revision$
======================================================================

The Sun Multi-Schema XML Validator is a Java tool to validate XML
documents against several kinds of XML schemata. It supports DTD,
RELAX Namespace, RELAX Core, RELAX NG, TREX, and a subset of W3C
XML Schema Part 1. This release includes software developed by
the Apache Software Foundation [1].

----------------------------------------------------------------------
OVERVIEW
----------------------------------------------------------------------

This tool is:

* A command line tool that can read XML documents and validate them
  against a schema (DTD/RELAX/TREX/W3C). If an error is found,
  error messages are provided.

* A library that can be incorporated into Java applications as a
  validator.

See commandline.html for details on how to use MSV from the command line.
See developer.html for details on how to use it as a library.
ChangeLog.txt contains changes made from previous versions.


----------------------------------------------------------------------
TECHNICAL SUPPROT
----------------------------------------------------------------------

Sun does not provide any official support for this software. However,
Kohsuke KAWAGUCHI provides personal, best-effort support for this
software. Please post any questions/comments to the "msv-interest"
group [19].


----------------------------------------------------------------------
CURRENT STATUS
----------------------------------------------------------------------

This preview version implements:

1. XML DTD [2]

2. RELAX Core [3], based on the JIS:TR X 0029:2000 [4] specification,
   and enhancements based on discussions held on the mailing list
   relstdj [5], reluserj [6], and reldeve [7].

3. RELAX Namespace [3], based on the draft of 2001-03-11 [8] plus
   enhancements based on discussions held at the same mailing list.

4. RELAX NG [14], based on the spec [15].
   See commandline.html for details of limitations.

5. RELAX NG DTD compatibility extension[18].
   See commandline.html for details of limitations.

6. TREX [9], based on the draft of 2001-02-13 [10] and the 
   reference implementation [11]. The only datatype vocabulary available
   is W3C XML Schema Part 2 PR [12].

7. A limited subset of W3C XML Schema Part 1 REC [13].
   See commandline.html for details of limitations.

8. W3C XML Schema Part 2 REC [12] as a datatype vocabulary.

For limitations, see commandline.html.


----------------------------------------------------------------------
REFERENCES
----------------------------------------------------------------------
[ 1] Apache Software Foundation
      http://www.apache.org/
[ 2] XML DTD
      http://www.w3.org/TR/REC-xml
[ 3] RELAX homepage
      http://www.xml.gr.jp/relax/
[ 4] RELAX Core JIS:TR X 0029:2000
    The original written in Japanese
      http://www.y-adagio.com/public/standards/tr_relax_c/toc.htm
    Translation to English
      http://www.egroups.co.jp/files/reldeve/JISTRtranslation.pdf
[ 5] RELAX standardalization mailing list (Japanese)
      http://www2.xml.gr.jp/1ml_main.html?MLID=relax-std-j
[ 6] RELAX user's mailing list (Japanese)
      http://www2.xml.gr.jp/1ml_main.html?MLID=relax-users-j
[ 7] RELAX developer's mailing list (English)
      http://groups.yahoo.com/group/reldeve
[ 8] RELAX Namespace specification
    The original writen in Japanese
      http://www.y-adagio.com/public/standards/tr_relax_ns/toc.htm
    No translation available.
[ 9] TREX
      http://www.thaiopensource.com/trex/
[10] TREX draft specification
      http://www.thaiopensource.com/trex/spec.html
[11] TREX reference implementation (JTREX)
      http://www.thaiopensource.com/trex/jtrex.html
[12] W3C XML Schema Part 2: Datatypes
      http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/
[13] W3C XML Schema Part 1: Structure
      http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/
[14] RELAX NG
      http://www.oasis-open.org/committees/relax-ng/
[15] RELAX NG Spec (Upcoming version.1.0)
      (URL is not decided yet. follow the link from [14])
[16] RELAX NG tutorial
      http://www.oasis-open.org/committees/relax-ng/tutorial.html
[17] RELAX NG discussion list archive
      http://lists.oasis-open.org/archives/relax-ng/
[18] RELAX NG DTD Compatibility Spec (Upcoming version.1.0)
      (URL is not decided yet. follow the link from [14])
[19] MSV-interest group
      http://www.yahoogroups.com/group/msv-interest/
======================================================================
END OF README
