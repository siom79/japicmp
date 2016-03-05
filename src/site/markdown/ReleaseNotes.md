#Versions#

The following versions of japicmp are available:

##0.7.1 (2016-02-21)##

* Method added to new interface is no longer detected as source incompatible [#112](https://github.com/siom79/japicmp/issues/112)
* Method that overrides in subinterface method from superinterface is no longer detected as source incompatible [#113](https://github.com/siom79/japicmp/issues/113)

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.7.1%22).

##0.7.0 (2016-02-21)##

* japicmp now also supports tracking of source incompatible changes. [#59](https://github.com/siom79/japicmp/issues/59)
* The XML report now also outputs the reason for each change for better traceability. [#105](https://github.com/siom79/japicmp/issues/105)
* The maven plugin comes now with the new option `breakBuildBasedOnSemanticVersioning` that breaks the build based on the version numbers of the old and new archive(s) assuming they are using semantic versioning. [#108](https://github.com/siom79/japicmp/issues/108)
* The maven plugin now has a new option `packagingSupported` that lets you define for which packaging types the plugin shoule be invoked. This is useful when the plugin is defined in the root pom and should be executed for all submodules, but you want to exclude some of these submodules by packaging type. [#100](https://github.com/siom79/japicmp/pull/100)
* Fixed NPE in JavadocLikePackageFilter when matching against classes in the default (empty) package. [#106](https://github.com/siom79/japicmp/pull/106)

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.7.0%22).

##0.6.2 (2015-12-20)##

* Change tracking can now be filtered by annotations ([#88](https://github.com/siom79/japicmp/issues/88)).
* Improved error message in case dependent class is not found ([#95](https://github.com/siom79/japicmp/issues/95)).
* Added new option `<ignoreNonResolvableArtifacts/>` for the maven plugin ([#94](https://github.com/siom79/japicmp/issues/94)).
* Added option `--no-annotations` to CLI tool and maven plugin ([#91](https://github.com/siom79/japicmp/issues/91)).
* fixed: Values for XML attribute value of JApiAnnotationElementValue are escaped properly ([#90](https://github.com/siom79/japicmp/issues/90)).

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.6.2%22).

##0.6.1 (2015-11-09)##

* semantic versioning information is now also part of the HTML report ([#81](https://github.com/siom79/japicmp/issues/81))
* title of HTML report is now configurable via maven-plugin ([#84](https://github.com/siom79/japicmp/issues/84))
* line numbers are reported for methods and constructors ([#77](https://github.com/siom79/japicmp/issues/77))
* support for `type` attribute in `japicmp.maven.Dependency`
* resolution filter that does not include optional dependencies([#76](https://github.com/siom79/japicmp/issues/76))
* fixed: methods pulled up to new superinterface are no longer reported as binary incompatible ([#85](https://github.com/siom79/japicmp/issues/85))
* fixed: project dependencies are not resolved transitively ([#83](https://github.com/siom79/japicmp/issues/83))

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.6.1%22).

##0.6.0 (2015-10-11)##

* You can provide now a list of jar files to the CLI tool or a list of dependencies to the maven plugin in order to assemble a report over a bunch of jar files ([#67](https://github.com/siom79/japicmp/issues/67)).
* It is now possible to provide separate classpaths for the old and new version ([#61](https://github.com/siom79/japicmp/issues/61)).
* You can now provide a classifier for the old and new version ([#73](https://github.com/siom79/japicmp/issues/73)).
* The maven plugin provides now an option to specify a separate CSS file for the HTML report ([#63](https://github.com/siom79/japicmp/issues/63)).
* Dependencies declared with one of the dependency elements (`<dependencies/>`, `<oldDependencies/>`, `<newDependencies/>`) are now resolved transitively and all transitive dependencies are added to the classpath ([#75](https://github.com/siom79/japicmp/issues/75)).

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.6.0%22).

##0.5.3 (2015-08-16)##

* Supported maven version is now 3.0.3 instead of 3.1.0 ([#70](https://github.com/siom79/japicmp/issues/70)).
* maven plugin does not fail if run consecutively ([#68](https://github.com/siom79/japicmp/issues/68)).

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.5.3%22).

##0.5.2 (2015-08-08)##

* The maven plugin now also includes transitive dependencies ([#65](https://github.com/siom79/japicmp/issues/65)).
* The maven plugin now automatically skips pom modules and can be configured to not skip them if necessary ([#66](https://github.com/siom79/japicmp/issues/66)).

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.5.2%22).

##0.5.1 (2015-07-19)##

* The new option `--ignore-missing-classes` allows to ignore superclasses or implemented interfaces from third party libraries that are not available on the classpath. [#52](https://github.com/siom79/japicmp/issues/52)
* The maven plugin can be invoked by the `maven-site-plugin` and therewith integrated into the site report. [#33](https://github.com/siom79/japicmp/issues/33)
* The new option `--html-stylesheet` allows to provide an individual CSS stylesheet for the HTML report. [#33](https://github.com/siom79/japicmp/issues/33)
* The maven plugin has a new parameter `skip` that allows to skip its execution (e.g. based on some profile properties). [#56](https://github.com/siom79/japicmp/issues/56)
* Fixed: Exception when class has no superclass (comparing rt.jar).
* Fixed: Parameter `includes` is ignored by the maven plugin.

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.5.1%22).

##0.5.0 (2015-05-26)##

* The parameters `--exclude` and `--include` have been extended such that next to packages now also classes, methods and fields can be excluded or only included. The syntax is similiar to the one used for javadoc references: `package.to.include;package.ClassToInclude;package.Class#methodToInclude();package.Class#fieldToInclude`. Please note that the separator char has changed from `,` to `;`. This was necessary as the `,` is now used to separate the arguments for a method in `package.Class#methodWithParams(long,int)` ([#51](https://github.com/siom79/japicmp/issues/51)).
* Parameters classes for methods with the same name but different signatures are no longer resolved through the classpath ([#55](https://github.com/siom79/japicmp/issues/55)).

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.5.0%22).
