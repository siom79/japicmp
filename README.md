japicmp
=======

japicmp is a tool to compare two versions of a jar archive:

    java -jar japicmp-0.1.1.jar -n new-version.jar -o old-version.jar

It can also be used as a library:

    JarArchiveComparatorOptions comparatorOptions = new JarArchiveComparatorOptions();
	JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
    List<JApiClass> jApiClasses = jarArchiveComparator.compare(oldArchive, newArchive);
    
The corresponding maven dependency is:

    <dependency>
        <groupId>com.github.siom79.japicmp</groupId>
        <artifactId>japicmp</artifactId>
        <version>0.1.1</version>
    </dependency>

##Motivation##

Every time you make a new release of a library or a product, you have to tell your clients or customers what
has changed in comparison to the last release. Without the appropriate tooling, this task is tedious and error-prone.
This tool/library helps you to determine the differences between the java class files that are contained in the two
jar archives.
In contrast to other libraries/tools, this library does not use the Java Reflection API to compute
the differences, as the usage of the Reflection API makes it necessary to include all classes the jar archive under
investigation depends on are available on the classpath. To prevent the inclusion of all dependent libraries, which
can be a lot of work for bigger applications, this library makes use of the [javassist](http://www.csg.ci.i.u-tokyo.ac.jp/~chiba/javassist/)
library to inspect the class files. This way you only have to provide the two jar archives on the command line, that's it.

##Features##

* Comparison of two jar archives without the need to add all of their dependencies to the classpath.
* Differences are printed on the command line in a simple diff format.
* Differences can optionally be printed to an xml file. This can be transformed to an HTML file using XSLT.
* Per default only public classes and class members are compared. If necessary, the access modifier of the classes and class members to be
  compared can be set to package, protected or private.
* Per default classes from all packages are compared. If necessary, certain packages can be excluded or only specific packages can be included.
* A maven plugin is available that allows you to compare the current artifact version with some older version from the repository.

##Usage CLI tool##

The tool has a set of CLI parameters that are described in the following:

    -h                        Prints this help.
    -o <pathToOldVersionJar>  Provides the path to the old version of the jar.
    -n <pathToNewVersionJar>  Provides the path to the new version of the jar.
    -x <pathToXmlOutputFile>  Provides the path to the xml output file. If not given, stdout is used.
    -a <accessModifier>       Sets the access modifier level (public, package, protected, private), which should be used.
    -i <packagesToInclude>    Comma separated list of package names to include, * can be used as wildcard.
    -e <packagesToExclude>    Comma separated list of package names to exclude, * can be used as wildcard.
    -m                        Outputs only modified classes/methods. If not given, all classes and methods are printed.
    
##Usage maven plugin##

The maven plugin can be included in the pom.xml file of your artifact in the following way:

    <build>
        <plugins>
            <plugin>
                <groupId>com.github.siom79.japicmp</groupId>
                <artifactId>japicmp-maven-plugin</artifactId>
                <version>0.1.1</version>
                <configuration>
                    <oldVersion>
                        <dependency>
                            <groupId>japicmp</groupId>
                            <artifactId>japicmp-test-v1</artifactId>
                            <version>0.1.1</version>
                        </dependency>
                    </oldVersion>
                    <newVersion>
                        <file>
                            <path>${project.build.directory}/${project.artifactId}-${project.version}.jar</path>
                        </file>
                    </newVersion>
                    <parameter>
                        <onlyModified>true</onlyModified>
                        <packagesToInclude>example</packagesToInclude>
                        <packagesToExclude>excludeMe</packagesToExclude>
                        <accessModifier>public</accessModifier>
                        <breakBuildOnModifications>false</breakBuildOnModifications>
                    </parameter>
                </configuration>
                <executions>
                    <execution>
                        <phase>verify</phase>
                        <goals>
                            <goal>cmp</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    
The elements &lt;oldVersion&gt; and &lt;newVersion&gt; elements let you specify which version you want to compare. Both elements
 support either a &lt;dependency&gt; or a &lt;file&gt; element. Through the &lt;parameter&gt; element you can provide the following options:
  
* onlyModified: Outputs only modified classes/methods. If not set to true, all classes and methods are printed.
* packagesToInclude: Comma separated list of package names to include, * can be used as wildcard.
* packagesToExclude: Comma separated list of package names to exclude, * can be used as wildcard.
* accessModifier: Sets the access modifier level (public, package, protected, private).
* breakBuildOnModifications: When set to true, the build breaks in case a modification has been detected.

The maven plugin produces the two files japicmp.diff and japicmp.xml within the directory ${project.build.directory}/japicmp
of your artifact.
	
###Example###

In the following you see the beginning of the xml output file after having computed the differences between the versions 16.0 and 17.0 of google's guava library:

    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <japicmp newJar="/home/martin/.m2/repository/com/google/guava/guava/17.0/guava-17.0.jar" oldJar="/home/martin/.m2/repository/com/google/guava/guava/16.0/guava-16.0.jar">
        <class accessModifierNew="PUBLIC" accessModifierOld="PUBLIC" changeStatus="MODIFIED" fullyQualifiedName="com.google.common.io.Closeables" type="CLASS">
            <method accessModifierNew="PUBLIC" accessModifierOld="n.a." changeStatus="NEW" name="closeQuietly" returnType="void">
                <parameter type="java.io.Reader"/>
            </method>
            <method accessModifierNew="PUBLIC" accessModifierOld="n.a." changeStatus="NEW" name="closeQuietly" returnType="void">
                <parameter type="java.io.InputStream"/>
            </method>
        </class>
        <class accessModifierNew="PUBLIC" accessModifierOld="PUBLIC" changeStatus="MODIFIED" fullyQualifiedName="com.google.common.util.concurrent.AbstractService" type="CLASS">
            <method accessModifierNew="n.a." accessModifierOld="PUBLIC" changeStatus="REMOVED" name="startAndWait" returnType="com.google.common.util.concurrent.Service$State"/>
            <method accessModifierNew="n.a." accessModifierOld="PUBLIC" changeStatus="REMOVED" name="stop" returnType="com.google.common.util.concurrent.ListenableFuture"/>
            <method accessModifierNew="n.a." accessModifierOld="PUBLIC" changeStatus="REMOVED" name="stopAndWait" returnType="com.google.common.util.concurrent.Service$State"/>
            <method accessModifierNew="n.a." accessModifierOld="PUBLIC" changeStatus="REMOVED" name="start" returnType="com.google.common.util.concurrent.ListenableFuture"/>
        </class>
        <class accessModifierNew="PUBLIC" accessModifierOld="PUBLIC" changeStatus="MODIFIED" fullyQualifiedName="com.google.common.io.CountingOutputStream" type="CLASS">
            <method accessModifierNew="PUBLIC" accessModifierOld="n.a." changeStatus="NEW" name="close" returnType="void"/>
        </class>
        <class accessModifierNew="PUBLIC" accessModifierOld="PUBLIC" changeStatus="MODIFIED" fullyQualifiedName="com.google.common.cache.CacheLoader" type="CLASS">
            <method accessModifierNew="PUBLIC" accessModifierOld="n.a." changeStatus="NEW" name="asyncReloading" returnType="com.google.common.cache.CacheLoader">
                <parameter type="com.google.common.cache.CacheLoader"/>
                <parameter type="java.util.concurrent.Executor"/>
            </method>
        </class>
		...

The differences between the two Java APIs are also printed on the command line for a quick overview:

    *** MODIFIED CLASS com.google.common.io.Closeables
        +++ NEW METHOD closeQuietly(java.io.Reader)
        +++ NEW METHOD closeQuietly(java.io.InputStream)
    *** MODIFIED CLASS com.google.common.util.concurrent.AbstractService
        --- REMOVED METHOD startAndWait()
        --- REMOVED METHOD stop()
        --- REMOVED METHOD stopAndWait()
        --- REMOVED METHOD start()
    *** MODIFIED CLASS com.google.common.io.CountingOutputStream
        +++ NEW METHOD close()
    *** MODIFIED CLASS com.google.common.cache.CacheLoader
        +++ NEW METHOD asyncReloading(com.google.common.cache.CacheLoader, java.util.concurrent.Executor)
    ...

##Downloads##

The following releases are available:

* [Version 0.1.1](https://github.com/siom79/japicmp/releases/tag/japicmp-base-0.1.1)
    * Changes:
        * [The maven-plugin should be available in the central repository](https://github.com/siom79/japicmp/issues/8)
* [Version 0.1.0](https://github.com/siom79/japicmp/releases/tag/japicmp-base-0.1.0)
    * Changes:
        * [The functionality of japicmp should be available as a maven plugin](https://github.com/siom79/japicmp/issues/6)
* [Version 0.0.2](https://github.com/siom79/japicmp/releases/tag/japicmp-base-0.0.2)
    * Changes:
        * [Command-line option to filter packages](https://github.com/siom79/japicmp/issues/1)
        * [CLI option for comparing public, package, protected or private classes/class members](https://github.com/siom79/japicmp/issues/2)
        * ["No differences" output when comparing the same file](https://github.com/siom79/japicmp/issues/4)
        * [Giving a non-jar file as argument should not output "Comparing..."](https://github.com/siom79/japicmp/issues/5)
* [Version 0.0.1](https://github.com/siom79/japicmp/releases/tag/japicmp-base-0.0.1)

##Development##

* [Jenkins build server](https://siom79.ci.cloudbees.com/job/japicmp)
* [Maven snapshot repository](https://oss.sonatype.org/content/repositories/snapshots/)