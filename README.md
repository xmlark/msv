# Multi-Schema Validator

Multi-Schema XML Validator (MSV) is a Java technology tool to validate XML documents against several kinds of XML schemata. It supports RELAX NG, RELAX Namespace, RELAX Core, TREX, XML DTDs, and a subset of XML Schema Part 1.

# MSV development

## Directory structure

MSV consists of a number of sub projects. Each sub projects has its own directory, its own build script, etc.

| sub project | description |
| :---------- | :---------- |
| xsdlib | XML Datatypes Library. An implementation of W3C XML Schema Part 2. |
| testharness | Test harness that is used to parse composite test suite files (.ssuite). |
| msv | Multi-Schema XML Validator. A schema model and validator implementation. Dependent on XSDLib and test harness. |
| rngconverter | RELAX NG Converter. reads a schema and produces an equivalent RELAX NG schema. Dependent on MSV. |
| generator | XML Instance Generator. A tool that produces valid XML documents by reading a schema. Dependent on MSV. |
| relames | Multi-Schema XML Validator Schematron add-on. An experimental implementation of RELAX NG + Schematron validation. Dependent on MSV. |
| sox2rng | SOX to RELAX NG converter. Dependent on MSV. |
| tahiti | Data-binding implementation |
| trexconverter | TREX Converter. Reads a schema and produces an equivalent TREX pattern. This project is no longer maintained. |

### There are several other directories which are used to store other materials.
| directory | description |
| :-------- | :---------- |
| shared | keeps shared information among multiple subprojects. |
| testCases | keeps test instances/schemas. |

### Sub-project structure
Most of the sub projects have a similar directory structure.

| directory | description |
| :-------- | :---------- |
| src | keeps source files. Files in this directory will be included in the release package. |
| test | keeps test files. Files in this directory will NOT be included in the release package. JUnit is used throughout the project, and every test code must have "Test.java" as suffix to be recognized as a test. |
| bin | keeps the compiled .class files. both "test" and "src" are compiled into this directory. |
| doc | release documentation. Files in this directory will be included in the release package. |

## Build instruction
To build the entire project, use Maven. 
'mvn install' 
builds the release packages for all sub projects in a proper order.
When preparing release packages, it is a good idea to use this target so that dependencies are processed correctly. (But you should run a project-local "release" first to make sure that there is no error in the repository.)

### Project-wise build
When you are working on a sub project, you can use the project-local build script. Most of the build scripts are similar, and usually they have following targets.

| target | description |
| :----- | :---------- |
| binary (default) | compiles everything into bin |
| javadoc | generates javadoc from the working copy. Useful to check the documentation. |
| dist | makes a distribution package in the dist/ directory. |
| release | Compresses the result of the dist target into a single zip file and put it to the package/ directory. |
