#Ant Task#

## Download ##

You can download the Ant task from the Central Maven Repository: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.siom79.japicmp%22%20AND%20a%3A%22japicmp-ant-task%22.

##Basic Usage##

The Ant task can be included in the build file of a project like shown in the following (requires Ant >= 1.9.5):

```
<path id="task.classpath">
	<fileset file="${project.build.directory}/japicmp-jar-with-dependencies.jar"/>
	<fileset file="${project.build.directory}/japicmp-ant-task.jar"/>
</path>
<path id="old.classpath">
	<fileset file="${project.build.directory}/guava-18.0.jar"/>
</path>
<path id="new.classpath">
	<fileset file="${project.build.directory}/guava-19.0.jar"/>
</path>
<taskdef resource="japicmp/ant/antlib.xml" classpathref="task.classpath"/>
<japicmp oldjar="${project.build.directory}/guava-18.0.jar"
	 newjar="${project.build.directory}/guava-19.0.jar"
	 oldclasspathref="old.classpath"
	 newclasspathref="new.classpath"
	 onlybinaryincompatible="false"
	 onlymodifications="true"
	 />
```

First the new task japicmp is defined using the taskdef command. After this definition the japicmp task can be
invoked like any other Ant task. The attribute `oldjar` specifies the old version of the jar archive while `newjar`
defines the new one. `classpathref` lets you reference the classpath used by japicmp. If you only want to output
binary incompatible changes, you can set the attribute `onlybinaryincompatible` to true.

##Advanced Usage##

The following table gives an overview of all available parameters of the Ant task.

| Parameter | Optional | Default | Description |
|-----------|----------|---------|-------------|
| oldjar 									| false | n.a.  | Path to the old version(s) of the jar(s). Use `;` as list separator. |
| newjar 									| false | n.a.  | Path to the new version(s) of the jar(s). Use `;` as list separator. |
| classpath 								| true  | n.a.  | Classpath for the dependencies used to compare old and new versions. |
| classpathref 								| true  | n.a.  | Classpath reference for the dependencies used to compare old and new versions. |
| semanticVersioning 						| true  | false | Indicate which part of the version to increment according to semantic versioning rules. |
| onlyBinaryIncompatible 					| true  | false | If true, output only binary incompatible changes. |
| onlyModified 								| true  | false | If true, output only modified classes/methods, else print all classes and methods.|
| includeSynthetic 							| true  | false | If true, track changes for synthetic classes and class members.|
| noAnnotations 							| true  | false | If true, disable the evaluation of annotations completely.|
| reportOnlyFilename 						| true  | false | If true, report only filenames (not full paths). |
| ignoreMissingClasses 						| true  | n.a.  | Ignore all superclasses/interfaces missing on the classpath. |
| ignoreMissingClassesbyRegularExpressions	| true  | n.a.  | Ignore only those superclasses/interface missing on the classpath that are selected by a regular expression. |
| accessModifier 							| true  | protected | Ignore changes below the access modifier level (public, package, protected, private).|
| oldClassPath 								| true  | n.a.  | Classpath for the dependencies of the old version. |
| newClassPath 								| true  | n.a.  | Classpath for the dependencies of the new version. |
| oldClassPathRef 							| true  | n.a.  | Classpath reference for the dependencies of the old version. |
| newClassPathRef 							| true  | n.a.  | Classpath reference for the dependencies of the new version. |
| includes 									| true  | n.a.  | Semicolon separated list of elements to include in the form `package.Class#classMember`, `*` can be used as wildcard. Annotations are given as FQN starting with `@`. Examples: `mypackage;my.Class;other.Class#method(int,long);foo.Class#field;@my.Annotation`.|
| excludes 									| true  | n.a.  | Semicolon separated list of elements to exclude in the form `package.Class#classMember`, `*` can be used as wildcard. Annotations are given as FQN starting with `@`. Examples: `mypackage;my.Class;other.Class#method(int,long);foo.Class#field;@my.Annotation`.|
| includeExclusively						| true  | false	| Include only packages specified in the "includes" parameter, exclude their sub-packages.|
| excludeExclusively						| true  | false	| Exclude only packages specified in the "excludes" parameter, include their sub-packages.|
| xmlOutputFile 							| true  | n.a.  | Path to the xml output file. |
| htmlOutputFile 							| true  | n.a.  | Path to the html output file. |
| htmlStylesheet 							| true  | n.a.  | Path to your own stylesheet. |
| errorOnBinaryIncompatibility				| true	| false | Exit with an error if a binary incompatibility is detected. |
| errorOnSourceIncompatibility				| true	| false | Exit with an error if a source incompatibility is detected. |
| errorOnModifications						| true	| false | Exit with an error if any change between versions is detected. |
| errorOnExclusionIncompatibility			| true	| false | Ignore incompatible changes caused by an excluded class. |
| errorOnSemanticIncompatibility 			| true	| false | Exit with an error if the binary compatibility changes are inconsistent with Semantic Versioning. This expects versions of the form Major.Minor.Patch (e.g. 1.2.3 or 1.2.3-SNAPSHOT). |
| ignoreMissingOldVersion					| true	| false | When errorOnSemanticIncompatibility is true, ignore non-resolvable artifacts for the old version. |
| ignoreMissingNewVersion					| true	| false | When errorOnSemanticIncompatibility is true, ignore non-resolvable artifacts for the new version. |

