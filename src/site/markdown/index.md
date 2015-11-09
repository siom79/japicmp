#japicmp#

japicmp is a tool to compare two versions of a jar archive:

	java -jar japicmp-0.6.1-jar-with-dependencies.jar -n new-version.jar -o old-version.jar

It can also be used as a library:

	JarArchiveComparatorOptions comparatorOptions = new JarArchiveComparatorOptions();
	JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
	List<JApiClass> jApiClasses = jarArchiveComparator.compare(oldArchives, newArchives);

japicmp is available in the Maven Central Repository:

	<dependency>
		<groupId>com.github.siom79.japicmp</groupId>
		<artifactId>japicmp</artifactId>
		<version>0.6.1</version>
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
* Per default private and package protected classes and class members are not compared. If necessary, the access modifier of the classes and class members to be
  compared can be set to public, protected, package or private.
* Per default all classes are tracked. If necessary, certain packages, classes, methods or fields can be excluded or explicitly included.
* All changes between all classes/methods/fields are compared. If necessary, output can be limited to changes that are binary incompatible (as described in the [Java Language Specification](http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html)).
* All changes between annotations are compared, hence japicmp can be used to track annotation-based APIs like JAXB, JPA, JAX-RS, etc.
* A maven plugin is available that allows you to compare the current artifact version with some older version from the repository.
* The option `--semantic-versioning` tells you which part of the version you have to increment in order to follow [semantic versioning](http://semver.org/).
* If a class is serializable, changes are evaluated regarding the [Java Object Serialization Specification](http://docs.oracle.com/javase/7/docs/platform/serialization/spec/serialTOC.html).
* Per default synthetic classes and class members (e.g. [bridge methods](https://docs.oracle.com/javase/tutorial/java/generics/bridgeMethods.html)) are hidden. They can be listed by using the option `--include-synthetic`.

[melix](https://github.com/melix) has developed a [gradle plugin](https://github.com/melix/japicmp-gradle-plugin) for japicmp.

##Downloads##

You can download the latest version from the [release page](https://github.com/siom79/japicmp/releases) or directly from the [maven central repository](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22japicmp%22).

##Related work##

The following projects have related goals:

* [Java API Compliance Checker](http://ispras.linuxbase.org/index.php/Java_API_Compliance_Checker): A Perl script that uses javap to compare two jar archives. This approach cannot compare annotations and you need to have Perl installed.
* [Clirr](http://clirr.sourceforge.net/): A tool written in Java that compares two libraries for binary compatibility. Tracking of API changes is implemented only partially, tracking of annotations is not supported.
* [JDiff](http://javadiff.sourceforge.net/): A Javadoc doclet that generates an HTML report of all API changes. The source code for both versions has to be available, the differences are not distinguished between binary incompatible or not. Comparison of annotations is not supported.
