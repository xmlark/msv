# Schmit: SCHema In Transformation

Schmit is an [XSLT extension](https://xml.apache.org/xalan-j/extensions.html) that allows you to access schema annotation information from within the stylesheet. This functionality can be used to write more generic stylesheet that isn't tied to any particular XML vocabulary. The current release works for Apache Xalan.

## Simple Test

To run an easy examples, build 'mvn install' and run:

```java
     java -jar schmit.jar -XSL <stylesheet> -IN <input>
```

For instance, from the schmit project directory call (by exchanging the correct <VERSION> on a bash) the following line

```java
     java -jar ./target/msv-schmit-2022.8-SNAPSHOT-jar-with-dependencies.jar  -IN ./src/resources/examples/simple/test.xml -XSL ./src/resources/examples/simple/test.xsl
```

To see the details of the command line syntax, just run:

```java
    java -jar ./target/schmit-<VERSION>-jar-with-dependencies.jar
```java

## Follow-up Documenation

Please find the complete documentation [here](../docs/schmit/readme.html)!
