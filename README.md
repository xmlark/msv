# Multi-Schema Validator

Multi-Schema XML Validator (MSV) is a Java technology tool to validate XML documents against several kinds of XML schemata. It supports RELAX NG, RELAX Namespace, RELAX Core, TREX, XML DTDs, and a subset of XML Schema Part 1.
MSV was tested sucessfully with JDK 8 and JDK 11 on Windows & Linux.

# MSV development

## Directory structure

MSV consists of a number of sub-projects. Each sub-projects has its own directory, its own build script, etc.

| sub-project | description |
| :---------- | :---------- |
| generator | XML Instance Generator. A tool that produces valid XML documents by reading a schema. Dependent on MSV. |
| msv | Multi-Schema XML Validator. A schema model and validator implementation. Dependent on XSDLib and testharness. |
| relames | Multi-Schema XML Validator Schematron add-on. An experimental implementation of RELAX NG + Schematron validation. Dependent on MSV. |
| rngconverter | RELAX NG Converter. reads a schema and produces an equivalent RELAX NG schema. Dependent on MSV. |
| schmit | MSV Schmit (Schema-in-transformation XSLT add-on). XSLT Extension For Schema Annotation. |
| tahiti | Data-binding implementation |
| testharness | Test harness that is used to parse composite test suite files (.ssuite). |
| trexconverter | TREX Converter. Reads a schema and produces an equivalent TREX pattern.|
| xsdlib | XML Datatypes Library. An implementation of W3C XML Schema Part 2. |

### There are several other directories which are used to store other materials.
| directory | description |
| :-------- | :---------- |
| docs | Release documentation. Files in this directory are shown at https://xmlark.github.io/msv/docs/. |

### Sub-project structure
Most of the sub-projects have a similar directory structure.

| directory | description |
| :-------- | :---------- |
| src | keeps source files. Files in this directory will be included in the release package. |
| src/test | keeps test files. Files in this directory will NOT be included in the release package. JUnit is used throughout the project, and every test code must have "Test.java" as suffix to be recognized as a test. |
| target | keeps the compiled .class files. both "test" and "src" are compiled into this directory. |


## Build instruction
To build the entire project, use [Maven 3](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html). [Maven is downloadable for free at Apache.](https://maven.apache.org/download.cgi). 
To build via command line at project root level use:  
'mvn install' 
This builds the release packages for all sub-projects in a proper order.
When preparing release packages, it is a good idea to use this target so that dependencies are processed correctly. (But you should run a project-local "release" first to make sure that there is no error in the repository.)

### Project-wise build
When you are working on a sub-project, you can build in the project directory via 'mvn install'.