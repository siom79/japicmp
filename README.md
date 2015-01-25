#japicmp#

japicmp is a tool to compare two versions of a jar archive:

	java -jar japicmp-0.2.3-jar-with-dependencies.jar -n new-version.jar -o old-version.jar

It can also be used as a library:

	JarArchiveComparatorOptions comparatorOptions = new JarArchiveComparatorOptions();
	JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
	List<JApiClass> jApiClasses = jarArchiveComparator.compare(oldArchive, newArchive);
    
japicmp is available in the Maven Central Repository. The corresponding dependency is:

	<dependency>
		<groupId>com.github.siom79.japicmp</groupId>
		<artifactId>japicmp</artifactId>
		<version>0.2.3</version>
	</dependency>

##Motivation##

Every time you release a new version of a library or a product, you have to tell your clients or customers what
has changed in comparison to the last release. Without the appropriate tooling, this task is tedious and error-prone.
This tool/library helps you to determine the differences between the java class files that are contained in two given
jar archives.

This library does not use the Java Reflection API to compute the differences, as the usage of the Reflection API makes 
it necessary to include all classes the jar archive under investigation depends on are available on the classpath. 
To prevent the inclusion of all dependencies, which can be a lot of work for bigger applications, this library makes 
use of the [javassist](http://www.csg.ci.i.u-tokyo.ac.jp/~chiba/javassist/) library to inspect the class files. 
This way you only have to provide the two jar archives on the command line (and eventually libraries that contain
classes/interfaces you have extended/implemented).

This approach also detects changes in instrumented and generated classes. You can even evaluate changes in class file attributes (like synthetic) or annotations.
The comparison of annotations makes this approach suitable for annotation-based APIs like JAXB, JPA, JAX-RS, etc.

##Features##

* Comparison of two jar archives without the need to add all of their dependencies to the classpath.
* Differences are printed on the command line in a simple diff format.
* Differences can optionally be printed as XML or HTML file.
* Per default only public classes and class members are compared. If necessary, the access modifier of the classes and class members to be
  compared can be set to package, protected or private.
* Per default classes from all packages are compared. If necessary, certain packages can be excluded or only specific packages can be included.
* All changes between all classes/methods/fields are compared. If necessary, output can be limited to changes that are binary incompatible (as described in the [Java Language Specification](http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html)).
* All changes between annotations are compared, hence japicmp can be used to track annotation-based APIs like JAXB, JPA, JAX-RS, etc.
* A maven plugin is available that allows you to compare the current artifact version with some older version from the repository.
* The option `--semantic-versioning` tells you which part of the version you have to increment in order to follow [semantic versioning](http://semver.org/).

[melix](https://github.com/melix) has developed a [gradle plugin](https://github.com/melix/japicmp-gradle-plugin) for japicmp.

##Tools##

###Usage CLI tool###

japicmp has a set of CLI parameters that are described in the following:

	NAME
			java -jar japicmp.jar - Compares jars

	SYNOPSIS
			java -jar japicmp.jar [-a <accessModifier>] [(-b | --only-incompatible)]
					[(-e <packagesToExclude> | --exclude <packagesToExclude>)]
					[(-h | --help)] [--html-file <pathToHtmlOutputFile>]
					[(-i <packagesToInclude> | --include <packagesToInclude>)]
					[(-m | --only-modified)]
					[(-n <pathToNewVersionJar> | --new <pathToNewVersionJar>)]
					[(-o <pathToOldVersionJar> | --old <pathToOldVersionJar>)]
					[(-s | --semantic-versioning)]
					[(-x <pathToXmlOutputFile> | --xml-file <pathToXmlOutputFile>)]

	OPTIONS
			-a <accessModifier>
				Sets the access modifier level (public, package, protected,
				private), which should be used.

			-b, --only-incompatible
				Outputs only classes/methods that are binary incompatible. If not
				given, all classes and methods are printed.

			-e <packagesToExclude>, --exclude <packagesToExclude>
				Comma separated list of package names to exclude, * can be used as
				wildcard.

			-h, --help
				Display help information

			--html-file <pathToHtmlOutputFile>
				Provides the path to the html output file.

			-i <packagesToInclude>, --include <packagesToInclude>
				Comma separated list of package names to include, * can be used as
				wildcard.

			-m, --only-modified
				Outputs only modified classes/methods.

			-n <pathToNewVersionJar>, --new <pathToNewVersionJar>
				Provides the path to the new version of the jar.

			-o <pathToOldVersionJar>, --old <pathToOldVersionJar>
				Provides the path to the old version of the jar.

			-s, --semantic-versioning
				Tells you which part of the version to increment.

			-x <pathToXmlOutputFile>, --xml-file <pathToXmlOutputFile>
				Provides the path to the xml output file.

When your library under investigation implements interfaces or extends classes from other libraries than the JDK, you will
have to add these to the class path:

	java -cp japicmp-0.2.3-SNAPSHOT-jar-with-dependencies.jar;otherLibrary.jar japicmp.JApiCmp -n new-version.jar -o old-version.jar
    
###Usage maven plugin###

The maven plugin can be included in the pom.xml file of your artifact in the following way:

    <build>
        <plugins>
            <plugin>
                <groupId>com.github.siom79.japicmp</groupId>
                <artifactId>japicmp-maven-plugin</artifactId>
                <version>0.2.3</version>
                <configuration>
                    <oldVersion>
                        <dependency>
                            <groupId>japicmp</groupId>
                            <artifactId>japicmp-test-v1</artifactId>
                            <version>0.2.3</version>
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
                        <breakBuildOnBinaryIncompatibleModifications>false</breakBuildOnBinaryIncompatibleModifications>
                    </parameter>
					<dependencies>
						<dependency>
							<groupId>org.apache.commons</groupId>
							<artifactId>commons-math3</artifactId>
							<version>3.4</version>
						</dependency>
					</dependencies>
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
* breakBuildOnBinaryIncompatibleModifications: When set to true, the build breaks in case a binary incompatible modification has been detected.

If your library implements interfaces or extends classes from other libraries than the JDK, you can add these dependencies by using the
&lt;dependencies&gt; element. The &lt;systemPath&gt; element of the &lt;dependency&gt; element allows you to add local files as a dependency:

```
<dependency>
	<groupId>com.sun</groupId>
	<artifactId>tools</artifactId>
	<version>1.4.2</version>
	<scope>system</scope>
	<systemPath>${java.home}/../lib/tools.jar</systemPath>
</dependency>
```

The maven plugin produces the two files japicmp.diff and japicmp.xml within the directory ${project.build.directory}/japicmp
of your artifact.
	
##Examples##

###Comparing two versions of the guava library###

In the following you see the beginning of the differences between the versions 16.0 and 17.0 of Google's guava library. The differences between the two Java APIs are also printed on the command line for a quick overview. Please note that binary incompatible changes are flagged with an exclamation mark. 

	***! MODIFIED CLASS: PUBLIC FINAL com.google.common.base.Stopwatch
		***! MODIFIED CONSTRUCTOR: PACKAGE_PROTECTED (<- PUBLIC) Stopwatch()
			===  UNCHANGED ANNOTATION: java.lang.Deprecated
		***! MODIFIED CONSTRUCTOR: PACKAGE_PROTECTED (<- PUBLIC) Stopwatch(com.google.common.base.Ticker)
			===  UNCHANGED ANNOTATION: java.lang.Deprecated
	***! MODIFIED INTERFACE: PUBLIC ABSTRACT com.google.common.util.concurrent.Service
		---! REMOVED METHOD: PUBLIC(-) ABSTRACT(-) com.google.common.util.concurrent.Service$State startAndWait()
			---  REMOVED ANNOTATION: java.lang.Deprecated
		---! REMOVED METHOD: PUBLIC(-) ABSTRACT(-) com.google.common.util.concurrent.Service$State stopAndWait()
			---  REMOVED ANNOTATION: java.lang.Deprecated
		---! REMOVED METHOD: PUBLIC(-) ABSTRACT(-) com.google.common.util.concurrent.ListenableFuture start()
			---  REMOVED ANNOTATION: java.lang.Deprecated
		---! REMOVED METHOD: PUBLIC(-) ABSTRACT(-) com.google.common.util.concurrent.ListenableFuture stop()
			---  REMOVED ANNOTATION: java.lang.Deprecated
	***  MODIFIED CLASS: PUBLIC FINAL com.google.common.net.HttpHeaders
		+++  NEW FIELD: PUBLIC(+) STATIC(+) FINAL(+) java.lang.String FOLLOW_ONLY_WHEN_PRERENDER_SHOWN
	***! MODIFIED CLASS: PUBLIC ABSTRACT com.google.common.util.concurrent.AbstractScheduledService
		---! REMOVED METHOD: PUBLIC(-) STATIC(-) FINAL(-) com.google.common.util.concurrent.ListenableFuture start()
			---  REMOVED ANNOTATION: java.lang.Deprecated
		---! REMOVED METHOD: PUBLIC(-) STATIC(-) FINAL(-) com.google.common.util.concurrent.Service$State startAndWait()
			---  REMOVED ANNOTATION: java.lang.Deprecated
		---! REMOVED METHOD: PUBLIC(-) STATIC(-) FINAL(-) com.google.common.util.concurrent.Service$State stopAndWait()
			---  REMOVED ANNOTATION: java.lang.Deprecated
		---! REMOVED METHOD: PUBLIC(-) STATIC(-) FINAL(-) com.google.common.util.concurrent.ListenableFuture stop()
			---  REMOVED ANNOTATION: java.lang.Deprecated
	...

Optionally japicmp can also create an HTML report. An example for such a report can be found [here](http://htmlpreview.github.io/?https://github.com/siom79/japicmp/blob/development/doc/japicmp_guava.html).

You can also let japicmp create an XML report like the following one:

	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	<japicmp newJar="/home/siom79/dev/guava-17.0.jar" oldJar="/home/siom79/dev/guava-16.0.jar">
		<classes>
			<class binaryCompatible="false" changeStatus="MODIFIED" fullyQualifiedName="com.google.common.base.Stopwatch" type="CLASS">
				<annotations/>
				<attributes>
					<attribute changeStatus="UNCHANGED" newValue="NON_SYNTHETIC" oldValue="NON_SYNTHETIC"/>
				</attributes>
				<constructors>
					<constructor binaryCompatible="false" changeStatus="MODIFIED" name="Stopwatch">
						<annotations>
							<annotation fullyQualifiedName="java.lang.Deprecated">
								<elements/>
							</annotation>
						</annotations>
						<attributes>
							<attribute changeStatus="UNCHANGED" newValue="NON_SYNTHETIC" oldValue="NON_SYNTHETIC"/>
						</attributes>
						<modifiers>
							<modifier changeStatus="UNCHANGED" newValue="NON_FINAL" oldValue="NON_FINAL"/>
							<modifier changeStatus="UNCHANGED" newValue="NON_STATIC" oldValue="NON_STATIC"/>
							<modifier changeStatus="MODIFIED" newValue="PACKAGE_PROTECTED" oldValue="PUBLIC"/>
							<modifier changeStatus="UNCHANGED" newValue="NON_ABSTRACT" oldValue="NON_ABSTRACT"/>
						</modifiers>
						<parameters/>
					</constructor>
					<constructor binaryCompatible="false" changeStatus="MODIFIED" name="Stopwatch">
						<annotations>
							<annotation fullyQualifiedName="java.lang.Deprecated">
								<elements/>
							</annotation>
						</annotations>
						<attributes>
							<attribute changeStatus="UNCHANGED" newValue="NON_SYNTHETIC" oldValue="NON_SYNTHETIC"/>
						</attributes>
						<modifiers>
							<modifier changeStatus="UNCHANGED" newValue="NON_FINAL" oldValue="NON_FINAL"/>
							<modifier changeStatus="UNCHANGED" newValue="NON_STATIC" oldValue="NON_STATIC"/>
							<modifier changeStatus="MODIFIED" newValue="PACKAGE_PROTECTED" oldValue="PUBLIC"/>
							<modifier changeStatus="UNCHANGED" newValue="NON_ABSTRACT" oldValue="NON_ABSTRACT"/>
						</modifiers>
						<parameters>
							<parameter type="com.google.common.base.Ticker"/>
						</parameters>
					</constructor>
				</constructors>
				<fields/>
				<interfaces/>
				<methods/>
				<modifiers>
					<modifier changeStatus="UNCHANGED" newValue="FINAL" oldValue="FINAL"/>
					<modifier changeStatus="UNCHANGED" newValue="NON_STATIC" oldValue="NON_STATIC"/>
					<modifier changeStatus="UNCHANGED" newValue="PUBLIC" oldValue="PUBLIC"/>
					<modifier changeStatus="UNCHANGED" newValue="NON_ABSTRACT" oldValue="NON_ABSTRACT"/>
				</modifiers>
				<superclass binaryCompatible="true" changeStatus="UNCHANGED" superclassNew="n.a." superclassOld="n.a."/>
			</class>
		...
    
###Tracking changes of an XML document marshalled with JAXB###

The following output shows the changes of a model class with some JAXB bindings:

	***  MODIFIED CLASS: PUBLIC japicmp.test.jaxb.SimpleDocument
		***  MODIFIED METHOD: PUBLIC java.lang.String getTitle()
			---  REMOVED ANNOTATION: javax.xml.bind.annotation.XmlAttribute
			+++  NEW ANNOTATION: javax.xml.bind.annotation.XmlElement
		***  MODIFIED METHOD: PUBLIC java.lang.String getAuthor()
			---  REMOVED ANNOTATION: javax.xml.bind.annotation.XmlAttribute
			+++  NEW ANNOTATION: javax.xml.bind.annotation.XmlElement
		***  MODIFIED ANNOTATION: javax.xml.bind.annotation.XmlRootElement
			***  MODIFIED ELEMENT: name=document (<- simpleDocument)
			
As can bee seen from the output above, the XML attributes title and author have changed to an XML element. The name of the XML root element has also changed from "simpleDocument" to "document".

##Downloads##

You can download the latest version from the [release page](https://github.com/siom79/japicmp/releases).

##Development##

* ![Build Status](https://travis-ci.org/siom79/japicmp.svg?branch=development)

##Related work##

The following projects have related goals:

* [Java API Compliance Checker](http://ispras.linuxbase.org/index.php/Java_API_Compliance_Checker): A Perl script that uses javap to compare two jar archives. This approach cannot compare annotations and requires an installation of Perl.
* [Clirr](http://clirr.sourceforge.net/): A tool written in Java that compares two libraries for binary compatibility. Tracking of API changes is implemented only partially, tracking of annotations is not supported.
* [JDiff](http://javadiff.sourceforge.net/): A Javadoc doclet that generates an HTML report of all API changes. The source code for both versions has to be available, the differences are not distinguished between binary incompatible or not. Comparison of annotations is not supported.
