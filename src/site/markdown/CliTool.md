#CLI-Tool#

The command line tool has the following options:

```
SYNOPSIS
        java -jar japicmp.jar [-a <accessModifier>] [(-b | --only-incompatible)]
                [(-e <excludes> | --exclude <excludes>)] [--exclude-exclusively]
                [(-h | --help)] [--html-file <pathToHtmlOutputFile>]
                [--html-stylesheet <pathToHtmlStylesheet>]
                [(-i <includes> | --include <includes>)] [--ignore-missing-classes]
                [--ignore-missing-classes-by-regex <ignoreMissingClassesByRegEx>...]
                [--include-exclusively] [--include-synthetic] [(-m | --only-modified)]
                [(-n <pathToNewVersionJar> | --new <pathToNewVersionJar>)]
                [--new-classpath <newClassPath>] [--no-annotations]
                [(-o <pathToOldVersionJar> | --old <pathToOldVersionJar>)]
                [--old-classpath <oldClassPath>] [--report-only-filename]
                [(-s | --semantic-versioning)]
                [(-x <pathToXmlOutputFile> | --xml-file <pathToXmlOutputFile>)]
                [--error-on-binary-incompatibility]
                [--error-on-source-incompatibility]
                [--error-on-modifications]
                [--no-error-on-exclusion-incompatibility]
                [--error-on-semantic-incompatibility]
                [--ignore-missing-old-version] [--ignore-missing-new-version]

OPTIONS
        -a <accessModifier>
            Sets the access modifier level (public, package, protected,
            private), which should be used.

        -b, --only-incompatible
            Outputs only classes/methods that are binary incompatible. If not
            given, all classes and methods are printed.

        -e <excludes>, --exclude <excludes>
            Semicolon separated list of elements to exclude in the form
            package.Class#classMember, * can be used as wildcard. Annotations
            are given as FQN starting with @. Examples:
            mypackage;my.Class;other.Class#method(int,long);foo.Class#field;@my.Annotation.

        --exclude-exclusively
            Exclude only packages specified in the "exclude" option, include
            their sub-packages

        -h, --help
            Display help information

        --html-file <pathToHtmlOutputFile>
            Provides the path to the html output file.

        --html-stylesheet <pathToHtmlStylesheet>
            Provides the path to your own stylesheet.

        -i <includes>, --include <includes>
            Semicolon separated list of elements to include in the form
            package.Class#classMember, * can be used as wildcard. Annotations
            are given as FQN starting with @. Examples:
            mypackage;my.Class;other.Class#method(int,long);foo.Class#field;@my.Annotation.

        --ignore-missing-classes
            Ignores all superclasses/interfaces missing on the classpath.

        --ignore-missing-classes-by-regex <ignoreMissingClassesByRegEx>
            Ignores only those superclasses/interface missing on the classpath
            that are selected by a regular expression.

        --include-exclusively
            Include only packages specified in the "include" option, exclude
            their sub-packages

        --include-synthetic
            Include synthetic classes and class members that are hidden per
            default.

        -m, --only-modified
            Outputs only modified classes/methods.

        -n <pathToNewVersionJar>, --new <pathToNewVersionJar>
            Provides the path to the new version(s) of the jar(s). Use ; to
            separate jar files.

        --new-classpath <newClassPath>
            The classpath for the new version.

        --no-annotations
            Do not evaluate annotations.

        -o <pathToOldVersionJar>, --old <pathToOldVersionJar>
            Provides the path to the old version(s) of the jar(s). Use ; to
            separate jar files.

        --old-classpath <oldClassPath>
            The classpath for the old version.

        --report-only-filename
            Reports just filenames (not full paths) in report description.

        -s, --semantic-versioning
            Tells you which part of the version to increment.

        -x <pathToXmlOutputFile>, --xml-file <pathToXmlOutputFile>
            Provides the path to the xml output file.

        --error-on-binary-incompatibility
            Exit with an error if a binary incompatibility is detected.

        --error-on-source-incompatibility
            Exit with an error if a source incompatibility is detected.

        --error-on-modifications
            Exit with an error if any change between versions is detected.

        --no-error-on-exclusion-incompatibility
            Ignore incompatible changes caused by an excluded class
            (e.g. excluded interface removed from not excluded class) when
            deciding whether to exit with an error.

        --error-on-semantic-incompatibility
            Exit with an error if the binary compatibility changes are
            inconsistent with Semantic Versioning. This expects versions of
            the form Major.Minor.Patch (e.g. 1.2.3 or 1.2.3-SNAPSHOT).
            See http://semver.org/spec/v2.0.0.html for more information about
            Semantic Versioning.

        --ignore-missing-old-version
            When --error-on-semantic-incompatibility is passed, ignore
            non-resolvable artifacts for the old version.

        --ignore-missing-new-version
            When --error-on-semantic-incompatibility is passed, ignore
            non-resolvable artifacts for the new version.

```

When your library implements interfaces or extends classes from other libraries than the JDK and you want to evaluate binary
compatibility you must specify the classpath for the two different versions:

    java -jar japicmp-0.14.3-jar-with-dependencies.jar -n new-version.jar -o old-version.jar --new-classpath other-library-v2.jar
        --old-classpath other-library-v1.jar

In case the classpath for both versions did not change, you can add the library using the standard way:

	java -cp japicmp-0.14.3-jar-with-dependencies.jar;otherLibrary.jar japicmp.JApiCmp -n new-version.jar -o old-version.jar

For reporting purposes you can also provide more than one jar as old or new version(s):

	java -jar japicmp-0.14.3-jar-with-dependencies.jar -o lib1-old.jar;lib2-old.jar -n lib1-new.jar;lib2-new.jar
