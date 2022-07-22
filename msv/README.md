# MULTI-SCHEMA XML VALIDATOR

The MSV Multi-Schema XML Validator is a Java tool to validate XML
documents against several kinds of XML schemata. It supports DTD,
RELAX Namespace, RELAX Core, RELAX NG, TREX, and a subset of W3C
XML Schema Part 1. This release includes software developed by
the Apache Software Foundation [[1]](https://www.apache.org/).

## Overview

Most outstanding is the design of MSV core using the [Abstract grammar model (AGM)](https://xmlark.github.io/msv/nativeAPI.html). This is a schema-independent grammar model. All supported schemata are parsed into this internal representation. Allowing one API to deal them all.

This tool is:

* A command line tool that can read XML documents and validate them
  against a schema (DTD/RELAX/TREX/W3C). If an error is found,
  error messages are provided.

* A library that can be incorporated into Java applications as a
  validator (see [ODF Validator](https://tdf.github.io/odftoolkit/conformance/ODFValidator.html)) or code generation tool (see [schema2template](https://tdf.github.io/odftoolkit/generator/index.html)).

See [commandline.html](../docs/core/commandline.html) for details on how to use MSV from the command line.
See [developer doc (index.html)](/docs/core/index.html) for details on how to use it as a library.
ChangeLog.txt contains changes made from previous versions.

## License

The most of the code is licensed under the BSD license (see
[license.txt](../docs/core/license.txt)). The only exception to this is a part of the code of XSDLib,
which was taken from Apache, which is licensed under ASL 1.1
(see [Apache-LICENSE-1.1.txt](../docs/core/Apache-LICENSE-1.1.txt)).

## Current Status

This version implements:

1. XML DTD [2]

2. RELAX Core [3], based on the JIS:TR X 0029:2000 [4] specification,
   and enhancements based on discussions held on the mailing list
   relstdj [5], reluserj [6], and reldeve [7].

3. RELAX Namespace [3], based on the draft of 2001-03-11 [8] plus
   enhancements based on discussions held at the same mailing list.

4. RELAX NG [14], based on the spec [15].
   See [commandline.html](../docs/core/commandline.html) for details of limitations.

5. RELAX NG DTD compatibility extension[18].
   See [commandline.html](../docs/core/commandline.html) for details of limitations.

6. TREX [9], based on the draft of 2001-02-13 [10] and the
   reference implementation [11]. The only datatype vocabulary available
   is W3C XML Schema Part 2 PR [12].

7. A limited subset of W3C XML Schema Part 1 REC [13].
   See [commandline.html](../docs/core/commandline.html) for details of limitations.

8. W3C XML Schema Part 2 REC [12] as a datatype vocabulary.

For limitations, see [commandline.html](../docs/core/commandline.html).

## Follow-up Documenation

Please find the complete documentation [here](../docs/core/index.html).!

## References

[ 1] Apache Software Foundation
      https://www.apache.org/<br/>
[ 2] XML DTD
      https://www.w3.org/TR/REC-xml<br/>
[ 3] RELAX homepage
      http://www.xml.gr.jp/relax/<br/>
[ 4] RELAX Core JIS:TR X 0029:2000<br/>
    The original written in Japanese<br/>
      http://www.y-adagio.com/public/standards/tr_relax_c/toc.htm<br/>
    Translation to English<br/>
      http://www.egroups.co.jp/files/reldeve/JISTRtranslation.pdf<br/>
[ 5] RELAX standardalization mailing list (Japanese)
      http://www2.xml.gr.jp/1ml_main.html?MLID=relax-std-j<br/>
[ 6] RELAX user's mailing list (Japanese)
      http://www2.xml.gr.jp/1ml_main.html?MLID=relax-users-j<br/>
[ 7] RELAX developer's mailing list (English)
      http://groups.yahoo.com/group/reldeve<br/>
[ 8] RELAX Namespace specification<br/>
    The original writen in Japanese<br/>
      http://www.y-adagio.com/public/standards/tr_relax_ns/toc.htm<br/>
    No translation available.<br/>
[ 9] TREX
      http://www.thaiopensource.com/trex/<br/>
[10] TREX draft specification
      http://www.thaiopensource.com/trex/spec.html<br/>
[11] TREX reference implementation (JTREX)
      http://www.thaiopensource.com/trex/jtrex.html<br/>
[12] W3C XML Schema Part 2: Datatypes
      http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/<br/>
[13] W3C XML Schema Part 1: Structure
      http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/<br/>
[14] RELAX NG
      http://www.oasis-open.org/committees/relax-ng/<br/>
[15] RELAX NG Spec (Upcoming version.1.0)
      (URL is not decided yet. follow the link from [14])<br/>
[16] RELAX NG tutorial
      http://www.oasis-open.org/committees/relax-ng/tutorial.html<br/>
[17] RELAX NG discussion list archive
      http://lists.oasis-open.org/archives/relax-ng/<br/>
[18] RELAX NG DTD Compatibility Spec (Upcoming version.1.0)
      (URL is not decided yet. follow the link from [14])<br/>
[19] MSV project on GitHub
      https://github.com/xmlark/msv<br/>
