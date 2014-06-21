japicmp
=======

japicmp is a tool to compare two versions of a jar archive:

    java -jar japicmp-0.0.3.jar -n new-version.jar -o old-version.jar

It can also be used as a library:

    JarArchiveComparatorOptions comparatorOptions = new JarArchiveComparatorOptions();
	JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
    List<JApiClass> jApiClasses = jarArchiveComparator.compare(oldArchive, newArchive);

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
                <groupId>japicmp</groupId>
                <artifactId>japicmp-maven-plugin</artifactId>
                <version>0.0.3</version>
                <configuration>
                    <oldVersion>
                        <dependency>
                            <groupId>japicmp</groupId>
                            <artifactId>japicmp-test-v1</artifactId>
                            <version>${project.version}</version>
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

In the following you see the beginning of the xml output file after having computed the differences between the versions 4.0.1 and 4.2.3 of httpclient:

    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <japicmp newJar="D:\Programmierung\japicmp\github\japicmp\japicmp\httpclient-4.2.3.jar" oldJar="D:\Programmierung\japicmp\github\japicmp\japicmp\httpclient-4.0.1.jar">
        <class accessModifierNew="n.a." accessModifierOld="PUBLIC" changeStatus="REMOVED" fullyQualifiedName="org.apache.http.annotation.GuardedBy" type="ANNOTATION">
            <method accessModifierNew="n.a." accessModifierOld="PUBLIC" changeStatus="REMOVED" name="value" returnType="java.lang.String"/>
        </class>
        <class accessModifierNew="n.a." accessModifierOld="PUBLIC" changeStatus="REMOVED" fullyQualifiedName="org.apache.http.annotation.Immutable" type="ANNOTATION"/>
        <class accessModifierNew="n.a." accessModifierOld="PUBLIC" changeStatus="REMOVED" fullyQualifiedName="org.apache.http.annotation.NotThreadSafe" type="ANNOTATION"/>
        <class accessModifierNew="n.a." accessModifierOld="PUBLIC" changeStatus="REMOVED" fullyQualifiedName="org.apache.http.annotation.ThreadSafe" type="ANNOTATION"/>
        <class accessModifierNew="PUBLIC" accessModifierOld="PUBLIC" changeStatus="UNCHANGED" fullyQualifiedName="org.apache.http.auth.AUTH" type="CLASS"/>
        <class accessModifierNew="PUBLIC" accessModifierOld="PUBLIC" changeStatus="UNCHANGED" fullyQualifiedName="org.apache.http.auth.AuthenticationException" type="CLASS"/>
        <class accessModifierNew="PUBLIC" accessModifierOld="n.a." changeStatus="NEW" fullyQualifiedName="org.apache.http.auth.AuthOption" type="CLASS">
            <method accessModifierNew="PUBLIC" accessModifierOld="n.a." changeStatus="NEW" name="getAuthScheme" returnType="org.apache.http.auth.AuthScheme"/>
            <method accessModifierNew="PUBLIC" accessModifierOld="n.a." changeStatus="NEW" name="getCredentials" returnType="org.apache.http.auth.Credentials"/>
            <method accessModifierNew="PUBLIC" accessModifierOld="n.a." changeStatus="NEW" name="toString" returnType="java.lang.String"/>
        </class>
        <class accessModifierNew="PUBLIC" accessModifierOld="n.a." changeStatus="NEW" fullyQualifiedName="org.apache.http.auth.AuthProtocolState" type="ENUM">
            <method accessModifierNew="PUBLIC" accessModifierOld="n.a." changeStatus="NEW" name="valueOf" returnType="org.apache.http.auth.AuthProtocolState">
                <parameter type="java.lang.String"/>
            </method>
            <method accessModifierNew="PUBLIC" accessModifierOld="n.a." changeStatus="NEW" name="values" returnType="org.apache.http.auth.AuthProtocolState[]"/>
        </class>
		...

The differences between the two Java APIs are also printed on the command line for a quick overview:

    --- REMOVED ANNOTATION org.apache.http.annotation.GuardedBy
        --- REMOVED METHOD value()
    --- REMOVED ANNOTATION org.apache.http.annotation.Immutable
    --- REMOVED ANNOTATION org.apache.http.annotation.NotThreadSafe
    --- REMOVED ANNOTATION org.apache.http.annotation.ThreadSafe
    === UNCHANGED CLASS org.apache.http.auth.AUTH
    === UNCHANGED CLASS org.apache.http.auth.AuthenticationException
    +++ NEW CLASS org.apache.http.auth.AuthOption
        +++ NEW METHOD getAuthScheme()
        +++ NEW METHOD getCredentials()
        +++ NEW METHOD toString()
    +++ NEW ENUM org.apache.http.auth.AuthProtocolState
        +++ NEW METHOD valueOf(java.lang.String)
        +++ NEW METHOD values()

##Downloads##

The following releases are available:

* [Version 0.0.2](https://github.com/siom79/japicmp/releases/tag/japicmp-base-0.0.2)
    * Changes:
        * [Command-line option to filter packages](https://github.com/siom79/japicmp/issues/1)
        * [CLI option for comparing public, package, protected or private classes/class members](https://github.com/siom79/japicmp/issues/2)
        * ["No differences" output when comparing the same file](https://github.com/siom79/japicmp/issues/4)
        * [Giving a non-jar file as argument should not output "Comparing..."](https://github.com/siom79/japicmp/issues/5)
* [Version 0.0.1](https://github.com/siom79/japicmp/releases/tag/japicmp-base-0.0.1)

The latest snapshot version can be downloaded here: [japicmp-SNAPSHOT](http://repository-siom79.forge.cloudbees.com/snapshot/japicmp/japicmp/)

##Development##

* [Jenkins build server](https://siom79.ci.cloudbees.com/job/japicmp)
* [Maven snapshot repository](https://repository-siom79.forge.cloudbees.com/snapshot)
* [Maven release repository](https://repository-siom79.forge.cloudbees.com/release)
