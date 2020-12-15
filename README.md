The website is located at [https://siom79.github.io/japicmp](https://siom79.github.io/japicmp/).

# japicmp
japicmp is a tool to compare two versions of a jar archive:
``` bash
java -jar japicmp-0.14.4-jar-with-dependencies.jar -n new-version.jar -o old-version.jar
```
It can also be used as a library:
```java
JarArchiveComparatorOptions comparatorOptions = new JarArchiveComparatorOptions();
JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
List<JApiClass> jApiClasses = jarArchiveComparator.compare(oldArchives, newArchives);
```
japicmp is available in the Maven Central Repository:
[![mvnrepository](https://img.shields.io/maven-central/v/com.github.siom79.japicmp/japicmp.svg)](https://mvnrepository.com/artifact/com.github.siom79.japicmp/japicmp)
``` xml
<dependency>
	<groupId>com.github.siom79.japicmp</groupId>
	<artifactId>japicmp</artifactId>
	<version>0.14.4</version>
</dependency>
```
A maven plugin allows you to integrate the checks into your build:

``` xml
<plugin>
	<groupId>com.github.siom79.japicmp</groupId>
	<artifactId>japicmp-maven-plugin</artifactId>
	<version>0.14.4</version>
	<configuration>
		<oldVersion>
			<dependency>
				<groupId>japicmp</groupId>
				<artifactId>japicmp-test-v1</artifactId>
				<version>${oldversion}</version>
				<type>jar</type>
			</dependency>
		</oldVersion>
		<newVersion>
			<file>
				<path>${project.build.directory}/${project.artifactId}-${project.version}.${project.packaging}</path>
			</file>
		</newVersion>
		<parameter>
			<!-- see documentation -->
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
```

A Sonar Qube plugin integrates the results from the japicmp analysis into your code quality report: [sonar-japicmp-plugin](https://github.com/siom79/sonar-japicmp-plugin).

By using the available Ant task, you can also integrate japicmp into your Ant build files:

``` xml
<taskdef resource="japicmp/ant/antlib.xml" classpathref="task.classpath"/>
<japicmp oldjar="${project.build.directory}/guava-18.0.jar"
	 newjar="${project.build.directory}/guava-19.0.jar"
	 oldclasspathref="old.classpath"
	 newclasspathref="new.classpath"
	 onlybinaryincompatiblemodifications="false"
	 onlyModifications="true"
	 />
```

[melix](https://github.com/melix) has developed a [gradle plugin](https://github.com/melix/japicmp-gradle-plugin) for japicmp.

## Motivation

Every time you release a new version of a library or a product, you have to tell your clients or customers what
has changed in comparison to the last release. Without the appropriate tooling, this task is tedious and error-prone.
This tool/library helps you to determine the differences between the java class files that are contained in two given
jar archives.

This library does not use the Java Reflection API to compute the differences, as the usage of the Reflection API makes
it necessary to include all classes the jar archive under investigation depends on are available on the classpath.
To prevent the inclusion of all dependencies, which can be a lot of work for bigger applications, this library makes
use of the [javassist](https://www.javassist.org/) library to inspect the class files.
This way you only have to provide the two jar archives on the command line (and eventually libraries that contain
classes/interfaces you have extended/implemented).

This approach also detects changes in instrumented and generated classes. You can even evaluate changes in class file attributes (like synthetic) or annotations.
The comparison of annotations makes this approach suitable for annotation-based APIs like JAXB, JPA, JAX-RS, etc.


The goal of this project is to provide a fast and easy to use API comparison for Java. Therefore it does not aim
to integrate change tracking of other types of artifacts (configuration files, etc.) as a generic implementation means
to make compromises in terms of performance and ease of usage. japicmp for example compares two archives with about 1700 classes each
in less than one second and therewith can be easily integrated in each build.

## Features

* Comparison of two jar archives without the need to add all of their dependencies to the classpath.
* Differences are printed on the command line in a simple diff format.
* Differences can optionally be printed as XML or HTML file.
* Per default private and package protected classes and class members are not compared. If necessary, the access modifier of the classes and class members to be
  compared can be set to public, protected, package or private.
* Per default all classes are tracked. If necessary, certain packages, classes, methods or fields can be excluded or explicitly included. Inclusion and exclusion is also possible based on annotations.
* All changes between all classes/methods/fields are compared. japicmp differentiates between source and binary compatible changes (as described in the [Java Language Specification](http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html) and this [Oracle blog](https://blogs.oracle.com/darcy/entry/kinds_of_compatibility)).
* All changes between annotations are compared, hence japicmp can be used to track annotation-based APIs like JAXB, JPA, JAX-RS, etc.
* A maven plugin is available that allows you to compare the current artifact version with some older version from the repository.
* The option `--semantic-versioning` tells you which part of the version you have to increment in order to follow [semantic versioning](http://semver.org/).
* If a class is serializable, changes are evaluated regarding the [Java Object Serialization Specification](http://docs.oracle.com/javase/7/docs/platform/serialization/spec/serialTOC.html).
* Per default synthetic classes and class members (e.g. [bridge methods](https://docs.oracle.com/javase/tutorial/java/generics/bridgeMethods.html)) are hidden. They can be listed by using the option `--include-synthetic`.
* The maven plugin allows project-specific filtering and reports using a custom [Groovy](groovy-lang.org) script.

## Downloads

You can download the latest version from the [release page](https://github.com/siom79/japicmp/releases) or directly from the [maven central repository](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22japicmp%22).

# Development

* ![Build Status](https://travis-ci.org/siom79/japicmp.svg?branch=development)

## Reports

Use the maven site plugin (`mvn site`) to generate the following reports:
 * findbugs
 * checkstyle
 * japicmp
 * cobertura test coverage

## Release

This is the release procedure:
* Update ReleaseNotes.md.
* Increment version in README.md.
* Run release build (substitute passphrase with your GPG password):
``` bash
mvn release:clean release:prepare -DautoVersionSubmodules=true -Dgpg.passphrase=passphrase
mvn release:perform -Dgpg.passphrase=passphrase
```
* Login to [Sonatype's Nexus repository](https://oss.sonatype.org/)
	* Download released artifact from staging repository.
	* Close and release staging repository if sanity checks are successful.
* Update maven site report.

The second and third step can also be automated using the python script `release.py` in the root directory:
``` bash
python3 release.py --gpg-passphrase <gpgpassphrase> --dry-run --new-version <new-version> --release-version <release-version> --release-tag <release-tag> --old-version <old-version>
```

## Contributions

Pull requests are welcome, but please follow these rules:

* The basic editor settings (indentation, newline, etc.) are described in the `.editorconfig` file (see [EditorConfig](http://editorconfig.org/)).
* Provide a unit test for every change.
* Name classes/methods/fields expressively.
* Fork the repo and create a pull request (see [GitHub Flow](https://guides.github.com/introduction/flow/index.html)).

## Website

The website can be generated by using the maven-site-plugin:
```
mvn site:site
mvn site:stage
cp -r target/staging $SIOM79_GITHUB_IO_REPO
```

