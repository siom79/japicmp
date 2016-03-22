#Maven Plugin#

##Basic Usage##

The maven plugin can be included in the pom.xml file of your artifact in the following way (requires maven >= 3.0.3):

```
<plugin>
	<groupId>com.github.siom79.japicmp</groupId>
	<artifactId>japicmp-maven-plugin</artifactId>
	<version>0.7.2</version>
	<configuration>
		<oldVersion>
			<dependency>
				<groupId>${project.groupId}</groupId>
				<artifactId>${project.artifactId}</artifactId>
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

You can also leave out the &lt;oldVersion&gt; and &lt;newVersion&gt; elements:

```
<plugin>
	<groupId>com.github.siom79.japicmp</groupId>
	<artifactId>japicmp-maven-plugin</artifactId>
	<version>0.7.2</version>
	<configuration>
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

This way the plugin tries to determine the artifacts output and the latest released version (no SNAPSHOT version).

##Advance Usage##

An advanced configuration can utilize the following parameters:

```
<build>
	<plugins>
		<plugin>
			<groupId>com.github.siom79.japicmp</groupId>
			<artifactId>japicmp-maven-plugin</artifactId>
			<version>0.7.2</version>
			<configuration>
				<oldVersion>
					<dependency>
						<groupId>japicmp</groupId>
						<artifactId>japicmp-test-v1</artifactId>
						<version>0.7.2</version>
						<type>jar</type>
					</dependency>
				</oldVersion>
				<newVersion>
					<file>
						<path>${project.build.directory}/${project.artifactId}-${project.version}.${project.packaging}</path>
					</file>
				</newVersion>
				<parameter>
					<onlyModified>true</onlyModified>
					<includes>
						<include>package.to.include</include>
						<include>package.ClassToInclude</include>
						<include>package.Class#methodToInclude(long,int)</include>
						<include>package.Class#fieldToInclude</include>
						<inclue>@my.AnnotationToInclude</include>
					</includes>
					<excludes>
						<exclude>package.to.exclude</exclude>
						<exclude>package.ClassToExclude</exclude>
						<exclude>package.Class#methodToExclude(long,int)</exclude>
						<exclude>package.Class#fieldToExclude</exclude>
						<exclude>@my.AnnotationToExcluce</exclude>
					</excludes>
					<accessModifier>public</accessModifier>
					<breakBuildOnModifications>false</breakBuildOnModifications>
					<breakBuildOnBinaryIncompatibleModifications>false</breakBuildOnBinaryIncompatibleModifications>
					<breakBuildOnSourceIncompatibleModifications>false</breakBuildOnSourceIncompatibleModifications>
					<breakBuildBasedOnSemanticVersioning>false</breakBuildBasedOnSemanticVersioning>
					<onlyBinaryIncompatible>false</onlyBinaryIncompatible>
					<includeSynthetic>false</includeSynthetic>
					<ignoreMissingClasses>false</ignoreMissingClasses>
					<skipPomModules>true</skipPomModules>
					<htmlStylesheet>path/to/stylesheet.css</htmlStylesheet>
					<htmlTitle>Title of report</htmlTitle>
					<noAnnotations>false</noAnnotations>
					<ignoreNonResolvableArtifacts>false</ignoreNonResolvableArtifacts>
					<packagingSupporteds>
						<packagingSupported>jar</packagingSupported>
					</packagingSupporteds>
					<postAnalysisScript>${project.basedir}/src/main/groovy/postAnalysisScript.groovy</postAnalysisScript>
					<skipXmlReport>false</skipXmlReport>
					<skipHtmlReport>false</skipHtmlReport>
					<skipDiffReport>false</skipDiffReport>
				</parameter>
				<dependencies>
					<dependency>
						<groupId>org.apache.commons</groupId>
						<artifactId>commons-math3</artifactId>
						<version>3.4</version>
					</dependency>
				</dependencies>
				<skip>false</skip>
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
```

The elements &lt;oldVersion&gt; and &lt;newVersion&gt; elements let you specify which version you want to compare. Both elements
support either a &lt;dependency&gt; or a &lt;file&gt; element. If necessary you can select the artifact by providing a &lt;classifier&gt; element inside
the &lt;dependency&gt; element. Through the &lt;parameter&gt; element you can provide the following options:

| Parameter | Optional | Default | Description |
|-----------|----------|---------|-------------|
| onlyModified									| true | false 		| Outputs only modified classes/methods. If not set to true, all classes and methods are printed.|
| includes										| true | n.a.  		| List of package, classes, methods and field that should be included. The syntax is similar to the one used for javadoc references. Annotations can also be used for filtering, just let the fully qualified name start with @.|
| excludes										| true | n.a.		| List of package, classes, methods and field that should be excluded. The syntax is similar to the one used for javadoc references. Annotations can also be used for filtering, just let the fully qualified name start with @.|
| accessModifier								| true | protected	| Sets the access modifier level (public, package, protected, private).|
| breakBuildOnModifications						| true | false		| If set to true, the build breaks in case a modification has been detected.|
| breakBuildOnBinaryIncompatibleModifications	| true | false		| If set to true, the build breaks in case a binary incompatible modification has been detected.|
| breakBuildOnSourceIncompatibleModifications	| true | false		| If set to true, the build breaks in case a source incompatible modification has been detected.|
| breakBuildBasedOnSemanticVersioning			| true | false		| If set to true, the plugin analyzes the versions of the old and new archives and decides based on these versions if binary compatible or incompatible changes are allowed or not. This option expects versions in the form Major.Minor.Patch (e.g. 1.2.3 or 1.2.3-SNAPSHOT).|
| onlyBinaryIncompatible						| true | false		| If set to true, only binary incompatible changes are reported.|
| includeSynthetic								| true | false		| If set to true, changes for synthetic classes and class members are tracked.|
| ignoreMissingClasses							| true | false		| If set to true, superclasses and interfaces that cannot be resolved are ignored. Pleases note that in this case the results for the affected classes may not be accurate.|
| skipPomModules								| true | true		| Setting this parameter to false (default: true) will not skip execution in modules with packaging type pom.|
| skip											| true | false		| Setting this parameter to true will skip execution of the plugin.|
| htmlStylesheet								| true | n.a.		| Path to an individual CSS stylesheet for the HTML report.|
| htmlTitle										| true | n.a.		| A title for the HTML report (optional).|
| noAnnotations									| true | false		| Setting this option to true disables the evaluation of annotations completely.|
| ignoreNonResolvableArtifacts					| true | false		| Set this to true in order to ignore artifacts that cannot be resolved, i.e. the build does not break in case a dependency cannto be resolved to a file.|
| packagingSupported							| true | n.a.		| List all packaging type for which the plugin should be executed. Helpful if you define the plugin in a root pom.|
| postAnalysisScript							| true | n.a.		| A [Groovy](http|//www.groovy-lang.org/) script that gets invoked after analysis is completed and before the output is written. This way it can be used to filter the output or break the build on specific conditions.|
| skipXmlReport									| true | false		| If set to true, no XML report will be generated.|
| skipHtmlReport								| true | false		| If set to true, no HTML report will be generated.|
| skipDiffReport								| true | false		| If set to true, no diff report will be generated.|
| ignoreMissingOldVersion						| true | false		| If set to true, not resolvable artifacts for the old version do not break the build.|

If your library implements interfaces or extends classes from other libraries than the JDK, you can add these dependencies by using the
&lt;dependencies&gt; element:

```
<dependencies>
	<dependency>
		<groupId>org.apache.commons</groupId>
		<artifactId>commons-math3</artifactId>
		<version>3.4</version>
	</dependency>
</dependencies>
```

Dependencies declared in the enclosing pom.xml and its parents are added automatically. The dependencies declared explicitly for this plugin
are appended to the classpath before the ones from the enclosing pom.xml, hence you can override them.

In case the classpath between both versions differs, you can add the dependencies for the new and old version separately:

```
<oldClassPathDependencies>
	<dependency>
		<groupId>org.apache.commons</groupId>
		<artifactId>commons-math3</artifactId>
		<version>3.4</version>
	</dependency>
</oldClassPathDependencies>
<newClassPathDependencies>
	<dependency>
		<groupId>org.apache.commons</groupId>
		<artifactId>commons-math3</artifactId>
		<version>3.5</version>
	</dependency>
</newClassPathDependencies>
```

The maven plugin produces the two files `japicmp.diff` and `japicmp.xml` within the directory `${project.build.directory}/japicmp`
of your artifact. If you run the plugin multiple times within the same module using the &lt;executions&gt; element, the reports
are named after the execution id.

##Site report##

Alternatively it can be used inside the `<reporting/>` tag in order to be invoked by the
[maven-site-plugin](https://maven.apache.org/plugins/maven-site-plugin/) and therewith to be integrated into the site report:

```
<reporting>
	<plugins>
		<plugin>
			<groupId>com.github.siom79.japicmp</groupId>
			<artifactId>japicmp-maven-plugin</artifactId>
			<version>0.7.2</version>
			<reportSets>
				<reportSet>
					<reports>
						<report>cmp-report</report>
					</reports>
				</reportSet>
			</reportSets>
			<configuration>
				<!-- see above -->
			</configuration>
		</plugin>
	</plugins>
</reporting>
```
To create a summary report, you can also provide multiple old and new versions:

```
<configuration>
	<oldVersions>
		<dependency>
			<groupId>com.github.siom79.japicmp</groupId>
			<artifactId>japicmp-test-v1</artifactId>
			<version>${project.version}</version>
		</dependency>
		<dependency>
			<groupId>com.github.siom79.japicmp</groupId>
			<artifactId>japicmp-test2-v1</artifactId>
			<version>${project.version}</version>
		</dependency>
	</oldVersions>
	<newVersions>
		<dependency>
			<groupId>com.github.siom79.japicmp</groupId>
			<artifactId>japicmp-test-v2</artifactId>
			<version>${project.version}</version>
		</dependency>
		<dependency>
			<groupId>com.github.siom79.japicmp</groupId>
			<artifactId>japicmp-test2-v2</artifactId>
			<version>${project.version}</version>
		</dependency>
	</newVersions>
	...
</configuration>
```
The configuration above will create one report for all the declared dependencies.

##Using Groovy scripts as post analysis script##

The parameter &lt;postAnalysisScript/&gt; can be used to invoke a [Groovy](http://www.groovy-lang.org/) script after the analysis but before the output is written.
This is helpful if you want to apply some custom filtering that is not possible with the standard means of japicmp. The following script for example filters out
all classes that reside within the package `japicmp.test.annotation` and all methods that start with `get...()` and `set...()`.

```
def it = jApiClasses.iterator()
while (it.hasNext()) {
	def jApiClass = it.next()
	def fqn = jApiClass.getFullyQualifiedName()
	if (fqn.startsWith("japicmp.test.annotation")) {
		it.remove()
	}
	def methodIt = jApiClass.getMethods().iterator()
	while (methodIt.hasNext()) {
		def method = methodIt.next()
		if (method.getName().startsWith("get") || method.getName().startsWith("set")) {
			methodIt.remove()
		}
	}
}
return jApiClasses
```

Please note that the script has to return a list of `JApiClass` objects, otherwise the maven plugin will report an error.

Beyond that the script can also be used to break the build on some project specific requirement. Let's assume that for the next release no classes within the package
`japicmp.test.annotation` should be modified in any way. The following Groovy script iterates over all classes and throws an exception if a class is not `UNCHANGED`.

```
import static japicmp.model.JApiChangeStatus.*

def it = jApiClasses.iterator()
while (it.hasNext()) {
	def jApiClass = it.next()
	def fqn = jApiClass.getFullyQualifiedName()
	if (fqn.startsWith("japicmp.test.annotation")) {
		if (jApiClass.getChangeStatus() != UNCHANGED) {
			throw new Exception("Class in package japicmp.test.annotation has been modified.")
		}
	}
}
return jApiClasses
```
