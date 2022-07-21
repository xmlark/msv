# MSV XML Generator

Sun XML Generator is a Java tool to generate various XML instances from
several kinds of schemas. It supports DTD, RELAX Namespace, RELAX Core,
TREX, and a subset of W3C XML Schema Part 1. This release includes
software developed by the Apache Software Foundation [1].

## Overview

This is a command-line tool that can generate both valid and invalid
instances from schemas. It can be used for generating test cases for XML
applications that need to conform to a particular schema. For example:

* to load-test applications against large documents
* to ensure that documents are processed correctly
* to check the behavior of XSL stylesheets

See [HowToUse.html](./HowToUse.html) for details.

## Change Log

* Added a new option "-root" to specify the root element name of the
  generated XML.

## Current Status

This release relies on Sun Multi Schema Validator[2] for parsing
schemas. Therefore, any limitation that applies to MSV will apply to
this release.

## References

[1] Apache Software Foundation   https://www.apache.org/
[2] Multi Schema Validator (MSV) https:://xmlark.github.io/msv/core/
