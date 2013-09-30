japicmp
=======

japicmp is a tool to compare two versions of a jar archive:

    java -jar japicmp-0.0.1-SNAPSHOT.jar -n new-version.jar -o old-version.jar

It can also be used as a library to integration its functionality in some other kind of software:

    List<JApiClass> jApiClasses = jarArchiveComparator.compare(oldArchive, newArchive);

##Motivation##

Every time you make a new release of a library or a product you have to tell your clients or customers what
has changed in comparison to the last release. Without the appropriate tooling, this task is tedious and error-prone.
This tool/library helps you to determine the differences between the java class files that are contained in the two
jar archives.
In contrast to other libraries/tools, this library does not use the Java Reflection API to compute
the differences, as the usage of the Reflection API makes it necessary to include all classes the jar archive under
investigation depends on are available on the classpath. To prevent the inclusion of all dependent libraries, which
can be a lot of work for bigger applications, this library makes use of the [javassist](http://www.csg.ci.i.u-tokyo.ac.jp/~chiba/javassist/)
library to inspect the class files. This way you only have to provide the two jar archives on the command line, that's it.

##Usage##

The tool has a set of CLI parameters that are described in the following:

    -h                        Prints this help.
    -o <pathToOldVersionJar>  Provides the path to the old version of the jar.
    -n <pathToNewVersionJar>  Provides the path to the new version of the jar.
    -x <pathToXmlOutputFile>  Provides the path to the xml output file. If not given, stdout is used.
    -m                        Outputs only modified classes/methods. If not given, all classes and methods are printed.

##Downloads##

The latest snapshot version can be downloaded here: [japicmp-SNAPSHOT](http://repository-siom79.forge.cloudbees.com/snapshot/japicmp/japicmp/)

##Development##

* [Jenkins build server](https://siom79.ci.cloudbees.com/job/japicmp) [![Build Status](https://siom79.ci.cloudbees.com/job/japicmp/badge/icon)](https://siom79.ci.cloudbees.com/job/japicmp)
* [Maven snapshot repository](https://repository-siom79.forge.cloudbees.com/snapshot)