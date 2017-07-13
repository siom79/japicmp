#Ant Task#

##Basic Usage##

The Ant task can be included in the build file of a project like shown in the following (requires Ant >= 1.9.5):

```
<taskdef resource="japicmp/ant/antlib.xml" classpathref="task.classpath"/>
<japicmp 
	oldjar="target/${japicmp.jar}" 
	newjar="${japicmp.current.jar}" 
	classpathref="compare.classpath" 
	onlybinaryincompatiblemodifications="true"
	/>
```

First the new task japicmp is defined using the taskdef command. After this definition the japicmp task can be
invoked like any other Ant task. The attribute `oldjar` specifies the old version of the jar archive while `newjar`
defines the new one. `classpathref` lets you reference the classpath used by japicmp. If you only want to output
binary incompatible changes, you cah set the attribute `onlybinaryincompatiblemodifications` to true.

##Advance Usage##

The following table gives an overview of all available parameters of the Ant task.

| Parameter | Optional | Default | Description |
|-----------|----------|---------|-------------|
| oldjar | false | n.a. | Defines the path of the old version. |
| newjar | false | n.a. | Defines the path of the new version. |
| classpathref | true | n.a. | Defines the classpath used to compare old and new version. |
| onlyBinaryIncompatibleModifications | true | false | If set to true, only binary incompatible changes are included. |
| onlyModified | true  | false | Outputs only modified classes/methods. If not set to true, all classes and methods are printed.|
| includeSynthetic | true | false | If set to true, changes for synthetic classes and class members are tracked.|
| noAnnotations | true  | false | Setting this option to true disables the evaluation of annotations completely.|
| semanticVersioning | true | false | Tells you which part of the version to increment. |
| reportOnlyFilename | true | false | Reports just filenames (not full paths) in report description. |
| ignoreMissingClasses | true | n.a. | Ignores all superclasses/interfaces missing on the classpath. |
| ignoreMissingClassesByRegEx | true | n.a. | Ignores only those superclasses/interface missing on the classpath that are selected by a regular expression. |
| accessModifier | true | protected |  Sets the access modifier level (public, package, protected, private), which should be used.|      
| oldClassPath | true | n.a. | The classpath for the old version. |
| newClassPath | true | n.a. | The classpath for the new version. |
| includes | true | n.a. | Semicolon separated list of elements to include in the form package.Class#classMember, * can be used as wildcard. Annotations are given as FQN starting with @. Examples: mypackage;my.Class;other.Class#method(int,long);foo.Class#field;@my.Annotation.|
| excludes | true | n.a. | Semicolon separated list of elements to exclude in the form package.Class#classMember, * can be used as wildcard. Annotations are given as FQN starting with @. Examples: mypackage;my.Class;other.Class#method(int,long);foo.Class#field;@my.Annotation.|    
| pathToXmlOutputFile | true | n.a. | Provides the path to the xml output file. |     
| pathToHtmlOutputFile | true | n.a. | Provides the path to the html output file. |
| pathToHtmlStylesheet | true | n.a. | Provides the path to your own stylesheet. |

