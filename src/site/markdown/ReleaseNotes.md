#Versions#

The following versions of japicmp are available:

##0.14.4 (2020-10-17)##
* New compatibility change METHOD_NO_LONGER_THROWS_CHECKED_EXCEPTION. [#263](https://github.com/siom79/japicmp/issues/263)
* More detailed output message when skipping the build. [#273](https://github.com/siom79/japicmp/issues/273)
* StdoutOutputGenerator prints modified superclasses if outputOnlyModifications=false. [#272](https://github.com/siom79/japicmp/issues/272)

Available at [Maven Central](https://search.maven.org/search?q=g:com.github.siom79.japicmp%20v:0.14.4).

##0.14.3 (2019-12-24)##
* METHOD_REMOVED_IN_SUPERCLASS is no longer reported if method is moved from superclass to superclass of superclass. [#253](https://github.com/siom79/japicmp/issues/253)
* Added METHOD_ADDED_TO_PUBLIC_CLASS as a new compatibility check. [#241](https://github.com/siom79/japicmp/issues/241)

Available at [Maven Central](https://search.maven.org/search?q=g:com.github.siom79.japicmp%20v:0.14.3).

##0.14.2 (2019-11-23)##
* New properties: japicmp.skip, japicmp.skipXmlReport, japicmp.skipHtmlReport, japicmp.breakBuildOnModifications, japicmp.breakBuildOnBinaryIncompatibleModifications, japicmp.breakBuildOnSourceIncompatibleModifications, japicmp.breakBuildBasedOnSemanticVersioning, japicmp.breakBuildBasedOnSemanticVersioningForMajorVersionZero. [#240](https://github.com/siom79/japicmp/issues/240)
* Do not filer modified classes when outputOnlyModifications is set and all members are unchanged. [#247](https://github.com/siom79/japicmp/issues/247)

Available at [Maven Central](https://search.maven.org/search?q=g:com.github.siom79.japicmp%20v:0.14.2).

##0.14.1 (2019-05-01)##
* New option reportLinkName that allows to define a name for the report link in site reports when using report sets. [#236](https://github.com/siom79/japicmp/issues/236)

Available at [Maven Central](https://search.maven.org/search?q=g:com.github.siom79.japicmp%20v:0.14.1).

##0.14.0 (2019-04-14)##
* Invocations from different reportSets supported. [#232](https://github.com/siom79/japicmp/issues/232)
* Optional dependencies are included, but can be excluded using the new option ignoreMissingOptionalDependency. [#230](https://github.com/siom79/japicmp/issues/230)
* Update to javassist 3.24.0-GA (no longer support for Java 1.7).
* Fixed ErrorOnSemanticIncompatibilityForMajorVersionZero. [#227](https://github.com/siom79/japicmp/issues/227)
* Support 'effective final'. [#229](https://github.com/siom79/japicmp/issues/229)
* Changed link to javassist site. [#233](https://github.com/siom79/japicmp/issues/233)

Available at [Maven Central](https://search.maven.org/search?q=g:com.github.siom79.japicmp%20v:0.14.0).

##0.13.1 (2019-02-25)##
* Do not report METHOD_REMOVED_IN_SUPERCLASS if method is pulled up. [#222](https://github.com/siom79/japicmp/issues/222)

Available at [Maven Central](https://search.maven.org/search?q=g:com.github.siom79.japicmp%20v:0.13.1).

##0.13.0 (2018-08-20)##
* Use javassist 3.23.1-GA.
* Added configuration option <semanticVersionLevel/> to <overrideCompatibilityChangeParameter/>. [#213](https://github.com/siom79/japicmp/issues/213)
* CLI support to error on incompatibilities. [#216](https://github.com/siom79/japicmp/issues/216)
* japicmp-maven-plugin writes now errors to the console if an incompatible change is detected. [#215](https://github.com/siom79/japicmp/issues/215)
* Use MavenParameters to obtain mavenProject in isPomModuleNeedingSkip(). [#210](https://github.com/siom79/japicmp/issues/210)
* CompatibilityChanges only detects ANNOTATION_DEPRECATED_ADDED if the annoation has change status NEW or MODIFIED. [#213](https://github.com/siom79/japicmp/issues/213)
* The two compatibility changes METHOD_NEW_DEFAULT and METHOD_ADDED_TO_INTERFACE are not set simultaneously. [#201](https://github.com/siom79/japicmp/issues/201)
* Added maven dependency on javax.activation. [#177](https://github.com/siom79/japicmp/issues/177)

Available at [Maven Central](https://search.maven.org/search?q=g:com.github.siom79.japicmp%20v:0.13.0).

##0.12.0 (2018-05-02)##
* If a change is evaluated as binary or source incompatible can be configured. [#209](https://github.com/siom79/japicmp/issues/209)
* Proper error message in case class could not loaded from old and new classpath if specified separately. [#200](https://github.com/siom79/japicmp/issues/200)
* Skip pom modules in site report. [#207](https://github.com/siom79/japicmp/issues/207)
* The parameters for exclusive filters are checked in CLI tool. [#202](https://github.com/siom79/japicmp/issues/202)

Available at [Maven Central](https://search.maven.org/search?q=g:com.github.siom79.japicmp%20v:0.12.0).

##0.11.1 (2018-03-12)##
* Reduced extraneous whitespace in the generated HTML report. [#197](https://github.com/siom79/japicmp/issues/197)
* Addition of a default method to an interface or making a previously abstract method to a default method is marked as incompatible. [#201](https://github.com/siom79/japicmp/issues/201)
* japicmp can be build under Oracle JDK 9.0.1 [#182(https://github.com/siom79/japicmp/issues/182)
* If maven artifact is a directory, no stacktrace is printed to stdout. [#196](https://github.com/siom79/japicmp/issues/196)
* If interface/class has been removed/added, the class file format version is printed as 'n.a.'. [#194](https://github.com/siom79/japicmp/issues/194)

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.11.1%22).

##0.11.0 (2017-10-18)##

* japicmp has now an Ant task. [#171](https://github.com/siom79/japicmp/issues/171)
* Two new options includeExclusively and excludeExclusively allow to exclude or include sub-packages. [#170](https://github.com/siom79/japicmp/issues/170)
* Changed class file format version is reported. [#168](https://github.com/siom79/japicmp/issues/168)
* Square brackets are escaped in regular expressions for behavior matching. [#176](https://github.com/siom79/japicmp/issues/176)
* japicmp can be build and run under JDK 1.9. [#182](https://github.com/siom79/japicmp/issues/182)

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.11.0%22).

##0.10.0 (2017-04-02)##

* The maven plugin relaxes the semantic versioning check for 0.x.x versions, it can be enabled with the option breakBuildBasedOnSemanticVersioningForMajorVersionZero. [#165](https://github.com/siom79/japicmp/issues/165)
* The XML report contains now the old and new version of the two archives compared. [#164](https://github.com/siom79/japicmp/issues/164)
* The maven plugin execution can be skipped using a CLI property (-Djapicmp.skip=true).
* Removed unnecessary XML element titleOptional from report. [#161](https://github.com/siom79/japicmp/issues/161)
* SemverOut now returns 0.0.0 in case no JApiClass(es) are there. [#162](https://github.com/siom79/japicmp/issues/162)
* Method throwing in new version RuntimeException is no longer reported to be source incompatible. [#167](https://github.com/siom79/japicmp/issues/167)

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.10.0%22).


##0.9.4 (2017-02-16)##

* exclude option now also works for inner classes and methods/fields of inner classes. [#157](https://github.com/siom79/japicmp/issues/157)
* Added threadSafe=true to @Mojo annotation to suppress @threadSafe warning in parallel builds. [#158](https://github.com/siom79/japicmp/issues/158)
* The automatic detection of old version does no longer only compares the qualifier with SNAPSHOT but checks if it ends with SNAPSHOT. [#160](https://github.com/siom79/japicmp/issues/160)

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.9.4%22).

##0.9.3 (2016-11-27)##

* Added new parameters includeModules and excludeModules to maven plugin. [#154](https://github.com/siom79/japicmp/issues/154)
* Do not break build if breakBuildBasedOnSemanticVersioning=true and ignoreMissingOldVersion=true and old version missing. [#153](https://github.com/siom79/japicmp/issues/153)
* FIELD_STATIC_AND_OVERRIDES_STATIC and FIELD_LESS_ACCESSIBLE_THAN_IN_SUPERCLASS should not be reported in case field in superclass is also new. [#154](https://github.com/siom79/japicmp/issues/154)
* Cover more edit operations on class and interface inheritance hierarchies. [#155](https://github.com/siom79/japicmp/issues/155)

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.9.3%22).

##0.9.2 (2016-11-07)##

* Illegal characters are removed from filename of diff report. [#152](https://github.com/siom79/japicmp/issues/152)
* If method is new, new checked exception is not considered as source incompatible. [#151](https://github.com/siom79/japicmp/issues/151)

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.9.2%22).

##0.9.1 (2016-09-28)##

* Added new option reportOnlyFilename. [#144](https://github.com/siom79/japicmp/issues/144)
* Added new parameter ignoreMissingNewVersion. [#148](https://github.com/siom79/japicmp/issues/148)
* Reworked CompatibilityChanges.forAllSuperclasses() such that evaluate() cannot be called recursively. [#146](https://github.com/siom79/japicmp/pull/146)

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.9.1%22).

##0.9.0 (2016-08-17)##

* Added new option --ignore-missing-classes-by-regex for CLI-Tool. [#124](https://github.com/siom79/japicmp/issues/124)
* Added new maven plugin option: breakBuildIfCausedByExclusion. [#126](https://github.com/siom79/japicmp/issues/126)
* Implemented skipDiffReport. [#139](https://github.com/siom79/japicmp/issues/139)
* Changed the maven site japicmp report to be more descriptive. [#141](https://github.com/siom79/japicmp/issues/141)
* Extended automatic detection for new version in case packaging is not jar. [#137](https://github.com/siom79/japicmp/issues/137)
* Moving abstract method to interface is binary compatible. [#140](https://github.com/siom79/japicmp/issues/140)

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.9.0%22).

##0.8.1 (2016-07-09)##

* Variable mavenProject in maven-plugin is read from MavenParameters and not from injected field. [#128](https://github.com/siom79/japicmp/issues/128)
* Moving protected field to superclass is no longer marked as binary incompatible. [#132](https://github.com/siom79/japicmp/issues/132)
* Maven plugin can be run without <parameter/> section. [#134](https://github.com/siom79/japicmp/issues/134)

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.8.1%22).

##0.8.0 (2016-05-14)##

* Declaration of <newVersion/> and <oldVersion/> should be optional. [#129](https://github.com/siom79/japicmp/issues/129)
* New parameter <oldVersionPattern/> allows filtering of automatically chosen old version. [#129](https://github.com/siom79/japicmp/issues/129)
* Better error message in case exception class could not be loaded from classpath. [#130](https://github.com/siom79/japicmp/issues/130)
* serialVersionUID is ignored for enums. [#131](https://github.com/siom79/japicmp/issues/131)

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.8.0%22).

##0.7.2 (2016-03-20)##

* maven plugin has new option to skip XML, HTML and diff report. [#121](https://github.com/siom79/japicmp/issues/121)
* maven-plugin has new option to ignore missing old version. [#122](https://github.com/siom79/japicmp/issues/122)
* Interface moved to abstract class is no longer reported to be source incompatible. [#123](https://github.com/siom79/japicmp/issues/123)
* When file for optional dependency could not be resolved the plugin does not break the build with 'could not resolve dependency...'. [#125](https://github.com/siom79/japicmp/issues/125)
* Improved output if build is broken due to found incompatibilities.

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.8.0%22).

##0.7.1 (2016-03-14)##

* Exceptions declared in method signatures are now tracked and evaluated regarding source incompatibility. [#110](https://github.com/siom79/japicmp/issues/110)
* HTML report now names all incompatible changes additional to the markers * and !. [#115](https://github.com/siom79/japicmp/issues/115)
* Maven plugin has new parameter postAnalysisScript that allows execution of Groovy script after comparison phase. [#120](https://github.com/siom79/japicmp/issues/120)
* Method added to new interface is no longer detected as source incompatible. [#112](https://github.com/siom79/japicmp/issues/112)
* Method that overrides in subinterface method from superinterface is no longer detected as source incompatible. [#113](https://github.com/siom79/japicmp/issues/113)
* Excluded xerces vom maven-reporting dependency in order to prevent warnings from SAXParserImpl. [#109](https://github.com/siom79/japicmp/issues/109)
* Indirectly implemented interfaces are no longer reported as removed. [#119](https://github.com/siom79/japicmp/issues/119)

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.7.1%22).

##0.7.0 (2016-02-21)##

* japicmp now also supports tracking of source incompatible changes. [#59](https://github.com/siom79/japicmp/issues/59)
* The XML report now also outputs the reason for each change for better traceability. [#105](https://github.com/siom79/japicmp/issues/105)
* The maven plugin comes now with the new option `breakBuildBasedOnSemanticVersioning` that breaks the build based on the version numbers of the old and new archive(s) assuming they are using semantic versioning. [#108](https://github.com/siom79/japicmp/issues/108)
* The maven plugin now has a new option `packagingSupported` that lets you define for which packaging types the plugin should be invoked. This is useful when the plugin is defined in the root pom and should be executed for all submodules, but you want to exclude some of these submodules by packaging type. [#100](https://github.com/siom79/japicmp/pull/100)
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

* The parameters `--exclude` and `--include` have been extended such that next to packages now also classes, methods and fields can be excluded or only included. The syntax is similar to the one used for javadoc references: `package.to.include;package.ClassToInclude;package.Class#methodToInclude();package.Class#fieldToInclude`. Please note that the separator char has changed from `,` to `;`. This was necessary as the `,` is now used to separate the arguments for a method in `package.Class#methodWithParams(long,int)` ([#51](https://github.com/siom79/japicmp/issues/51)).
* Parameters classes for methods with the same name but different signatures are no longer resolved through the classpath ([#55](https://github.com/siom79/japicmp/issues/55)).

Available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20v%3A%220.5.0%22).
