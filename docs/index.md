# Multi-Schema Validator Toolkit

The core component of this toolkit is the Multi-Schema XML Validator (MSV). It is a Java technology tool to validate XML documents against several kinds of XML schemata. It supports RELAX NG, RELAX Namespace, RELAX Core, TREX, XML DTDs, and a subset of XML Schema Part 1.
<br/>
Most outstanding is the design of MSV core using the [Abstract grammar model (AGM)](https://xmlark.github.io/msv/nativeAPI.html). This is a schema-independent grammar model. All supported schemata are parsed into this internal representation. This model, coupled with the grammar reader, may be useful for other applications. For instance, two use cases are the generation of source code using [schema2template](https://tdf.github.io/odftoolkit/generator/index.html) or the [ODF Validator](https://tdf.github.io/odftoolkit/conformance/ODFValidator.html).
<br/><br/>The builds of all MSV sub-projects were tested sucessfully using JDK 8, JDK 11 and JDK 17 on Windows and Linux.

## Development Guides

The MSV toolkit consists of a eight sub-projects, the main projects in bold. Each sub-projects has its own directory, its own build script, etc.

| sub-project       | description & dev guide reference                                                                                 |
|:------------------|:---------------------------------------------------------------------------------------------------------------------------------------|
| **[xsdlib](../xsdlib)**              | **[XML Schema Datatype (XSD) Library](./xsdlib/README.md)**<br/>An implementation of W3C XML Schema Part 2 [(see xsdlib JavaDoc)](https://xmlark.github.io/msv/xsdlib/api/index.html). |
| **[msv core](../msv)**               | **[Multi-Schema XML Core Validator](./core/index.html)**<br/>A schema model and validator implementation [(see MSV Core JavaDoc)](https://xmlark.github.io/msv/core/api/index.html).</br>Dependent on XSDLib and testharness.                                                               |
| **[generator](../generator/)**     | **[XML Instance Generator](./generator/README.md)** A tool that produces valid XML documents by reading a schema. Dependent on MSV.                                 |
| [schmit](../schmit/)            | [MSV SCHema In Transformation XSLT add-on (Schmit)](./schmit/readme.html)<br/>XSLT Extension For Schema Annotation.                                            |
| [relames](../relames/)           | [Multi-Schema XML Validator Schematron add-on](./relames/README.md)<br/>An experimental implementation of RELAX NG + Schematron validation. Dependent on MSV. |
| **[rngconverter](../rngconverter/)**  | **[RELAX NG Converter](./rngconverter/README.md)**<br/>reads a schema and produces an equivalent RELAX NG schema. Dependent on MSV.                                    |
| [tahiti](../tahiti/)            | [Data-binding implementation](./tahiti/README.md)                                                                                         |
| [trexconverter](../trexconverter/)     | [TREX Converter](./trexconverter/README.md)<br/>Reads a schema and produces an equivalent TREX pattern.                        |

***NOTE:*** Not [all previous forks and releases](https://github.com/svanteschubert/msv-merge-project) embrace all the projects below, only the latest msv does.

## Contributing

If you want to contribute code than

* Please write a test for your change.
* Ensure that you didn't break the build by running `mvn test`.
* Fork the repo and create a pull request. (See [Understanding the GitHub Flow](https://guides.github.com/introduction/flow/index.html))

## Building

MSV is build with [Maven](http://maven.apache.org/).
To build the entire project, use [Maven 3](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html).</br> [Maven is downloadable for free at Apache.](https://maven.apache.org/download.cgi).
To build via command line at project root level use:
'***mvn install***'
This builds the release packages for all sub-projects in a proper order.
When preparing release packages, it is a good idea to use this target so that dependencies are processed correctly. (But you should run a project-local "release" first to make sure that there is no error in the repository.)

### Project-wise build

When you are working on a sub-project, you can build in the sub-project directory via '***mvn install***' saving some time by building this alone.

## Directory structure

### Maven directories (each subproject)

Most of the sub-projects have a similar directory structure aligned to [the standard directory layout of the Maven build system](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html).

| directory | description |
|:----------| :---------- |
| src/main  | keeps source files. Files in this directory will be included in the release package. |
| src/test  | keeps test files. Files in this directory will NOT be included in the release package. JUnit is used throughout the project. |
| target    | keeps the compiled .class files. both "test" and "src" are compiled into this directory. |

### GitHub Page directory (root)

| directory | description |
| :-------- | :---------- |
| docs      | Project documentation. Files in this directory are shown at [https://xmlark.github.io/msv/](https://xmlark.github.io/msv/). |

## MSV Copyright

The sources of the deliverables of the sub-projects in bold (in the subproject table above) have a [BSD license](https://en.wikipedia.org/wiki/BSD_licenses). but their tests and all other sources have missing licing headers.
Sometimes Apache 1.1 licence header do exist.
The original MSV code repository from Sun/Oracle is no longer accessible. The Glassfish team as new owner is not responding [https://javaee.github.io/other-migrated-projects.html](https://javaee.github.io/other-migrated-projects.html) but [a fork exists from the former Code Owner Kohsuke Kawaguchi (KK) at Oracle](https://github.com/kohsuke/msv).

* KK's fork embraces the Maven releases from 2010 to 2011.
* The Maven release 2011 by RedHat is identical to the one 2011 on Maven central adding Generic Java Types and the default attribute value feature.
* Oracle did several releases 2013. They fixed the copyright header for the deliverables of the sub project in bold above. The sources are [taken from the source JAR of the Maven Central repository](https://github.com/svanteschubert/msv-merge-project).
* KK's fork is at the moment being located and maintained on branches at [https://github.com/xmlark/msv/](https://github.com/xmlark/msv/).
