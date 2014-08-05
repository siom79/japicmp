japicmp
=======

japicmp is a tool to compare two versions of a jar archive:

    java -jar japicmp-0.2.0.jar -n new-version.jar -o old-version.jar

It can also be used as a library:

    JarArchiveComparatorOptions comparatorOptions = new JarArchiveComparatorOptions();
	JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
    List<JApiClass> jApiClasses = jarArchiveComparator.compare(oldArchive, newArchive);
    
japicmp is available in the Maven Central Repository. The corresponding dependency is:

    <dependency>
        <groupId>com.github.siom79.japicmp</groupId>
        <artifactId>japicmp</artifactId>
        <version>0.2.0</version>
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
In contrast to solutions that investigate the javadoc comments of two APIs, this approach also detects changes in
instrumented and generated classes. You can even evaluate changes in class file attributes like synthetic.

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
	<japicmp newJar="C:\projekte\im\moja\cmp\IMApiCmp\japicmp\target\guava-17.0.jar" oldJar="C:\projekte\im\moja\cmp\IMApiCmp\japicmp\target\guava-16.0.jar">
		<classes>
			<class changeStatus="MODIFIED" fullyQualifiedName="com.google.common.base.CharMatcher" type="CLASS">
				<attributes>
					<attribute changeStatus="UNCHANGED" newValue="NON_SYNTHETIC" oldValue="NON_SYNTHETIC"/>
				</attributes>
				<constructors/>
				<fields>
					<field changeStatus="NEW" name="WHITESPACE_TABLE">
						<attributes>
							<attribute changeStatus="NEW" newValue="NON_SYNTHETIC" oldValue="n.a."/>
						</attributes>
						<modifiers>
							<modifier changeStatus="NEW" newValue="PACKAGE" oldValue="n.a."/>
							<modifier changeStatus="NEW" newValue="STATIC" oldValue="n.a."/>
							<modifier changeStatus="NEW" newValue="FINAL" oldValue="n.a."/>
						</modifiers>
						<type changeStatus="NEW" newValue="n.a." oldValue="java.lang.String"/>
					</field>
					<field changeStatus="NEW" name="WHITESPACE_SHIFT">
						<attributes>
							<attribute changeStatus="NEW" newValue="NON_SYNTHETIC" oldValue="n.a."/>
						</attributes>
						<modifiers>
							<modifier changeStatus="NEW" newValue="PACKAGE" oldValue="n.a."/>
							<modifier changeStatus="NEW" newValue="STATIC" oldValue="n.a."/>
							<modifier changeStatus="NEW" newValue="FINAL" oldValue="n.a."/>
						</modifiers>
						<type changeStatus="NEW" newValue="n.a." oldValue="int"/>
					</field>
					<field changeStatus="NEW" name="WHITESPACE_MULTIPLIER">
						<attributes>
							<attribute changeStatus="NEW" newValue="NON_SYNTHETIC" oldValue="n.a."/>
						</attributes>
						<modifiers>
							<modifier changeStatus="NEW" newValue="PACKAGE" oldValue="n.a."/>
							<modifier changeStatus="NEW" newValue="STATIC" oldValue="n.a."/>
							<modifier changeStatus="NEW" newValue="FINAL" oldValue="n.a."/>
						</modifiers>
						<type changeStatus="NEW" newValue="n.a." oldValue="int"/>
					</field>
				</fields>
				<interfaces/>
				<methods/>
				<modifiers>
					<modifier changeStatus="UNCHANGED" newValue="NON_FINAL" oldValue="NON_FINAL"/>
					<modifier changeStatus="UNCHANGED" newValue="NON_STATIC" oldValue="NON_STATIC"/>
					<modifier changeStatus="UNCHANGED" newValue="PUBLIC" oldValue="PUBLIC"/>
					<modifier changeStatus="UNCHANGED" newValue="ABSTRACT" oldValue="ABSTRACT"/>
				</modifiers>
				<superclass changeStatus="UNCHANGED" superclassNew="n.a." superclassOld="n.a."/>
        </class>
		...

The differences between the two Java APIs are also printed on the command line for a quick overview:

	*** MODIFIED CLASS com.google.common.base.CharMatcher
		+++ NEW FIELD WHITESPACE_TABLE
			+++ NEW MODIFIER PACKAGE
			+++ NEW MODIFIER STATIC
			+++ NEW MODIFIER FINAL
		+++ NEW FIELD WHITESPACE_SHIFT
			+++ NEW MODIFIER PACKAGE
			+++ NEW MODIFIER STATIC
			+++ NEW MODIFIER FINAL
		+++ NEW FIELD WHITESPACE_MULTIPLIER
			+++ NEW MODIFIER PACKAGE
			+++ NEW MODIFIER STATIC
			+++ NEW MODIFIER FINAL
	*** MODIFIED CLASS com.google.common.base.Converter
		+++ NEW METHOD from(com.google.common.base.Function, com.google.common.base.Function)
			+++ NEW MODIFIER NON_FINAL
			+++ NEW MODIFIER NON_STATIC
			+++ NEW MODIFIER PUBLIC
			+++ NEW MODIFIER NON_ABSTRACT
	*** MODIFIED CLASS com.google.common.base.Enums
		+++ NEW FIELD enumConstantCache
			+++ NEW MODIFIER PRIVATE
			+++ NEW MODIFIER STATIC
			+++ NEW MODIFIER FINAL
	*** MODIFIED CLASS com.google.common.base.Stopwatch
		--- REMOVED CONSTRUCTOR Stopwatch()
			--- REMOVED MODIFIER NON_FINAL
			--- REMOVED MODIFIER NON_STATIC
			--- REMOVED MODIFIER PUBLIC
			--- REMOVED MODIFIER NON_ABSTRACT
		--- REMOVED CONSTRUCTOR Stopwatch(com.google.common.base.Ticker)
			--- REMOVED MODIFIER NON_FINAL
			--- REMOVED MODIFIER NON_STATIC
			--- REMOVED MODIFIER PUBLIC
			--- REMOVED MODIFIER NON_ABSTRACT
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
