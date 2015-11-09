#Maven Plugin#

The maven plugin can be included in the pom.xml file of your artifact in the following way (requires maven >= 3.0.3):

    <build>
        <plugins>
            <plugin>
                <groupId>com.github.siom79.japicmp</groupId>
                <artifactId>japicmp-maven-plugin</artifactId>
                <version>0.6.1</version>
                <configuration>
                    <oldVersion>
                        <dependency>
                            <groupId>japicmp</groupId>
                            <artifactId>japicmp-test-v1</artifactId>
                            <version>0.6.1</version>
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
                        </includes>
                        <excludes>
							<exclude>package.to.exclude</exclude>
							<exclude>package.ClassToExclude</exclude>
							<exclude>package.Class#methodToExclude(long,int)</exclude>
							<exclude>package.Class#fieldToExclude</exclude>
						</excludes>
                        <accessModifier>public</accessModifier>
                        <breakBuildOnModifications>false</breakBuildOnModifications>
                        <breakBuildOnBinaryIncompatibleModifications>false</breakBuildOnBinaryIncompatibleModifications>
                        <onlyBinaryIncompatible>false</onlyBinaryIncompatible>
                        <includeSynthetic>false</includeSynthetic>
                        <ignoreMissingClasses>false</ignoreMissingClasses>
                        <skipPomModules>true</skipPomModules>
                        <htmlStylesheet>path/to/stylesheet.css</htmlStylesheet>
                        <htmlTitle>Title of report</htmlTitle>
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

The elements &lt;oldVersion&gt; and &lt;newVersion&gt; elements let you specify which version you want to compare. Both elements
support either a &lt;dependency&gt; or a &lt;file&gt; element. If necessary you can select the artifact by providing a &lt;classifier&gt; element inside
the &lt;dependency&gt; element. Through the &lt;parameter&gt; element you can provide the following options:

* onlyModified: Outputs only modified classes/methods. If not set to true, all classes and methods are printed.
* includes: List of package, classes, methods and field that should be included. The syntax is similar to the one use for javadoc references.
* excludes: List of package, classes, methods and field that should be excluded. The syntax is similar to the one use for javadoc references.
* accessModifier: Sets the access modifier level (public, package, protected, private).
* breakBuildOnModifications: When set to true, the build breaks in case a modification has been detected.
* breakBuildOnBinaryIncompatibleModifications: When set to true, the build breaks in case a binary incompatible modification has been detected.
* onlyBinaryIncompatible: When set to true, only binary incompatible changes are reported.
* includeSynthetic: When set to true, changes for synthetic classes and class members are tracked.
* ignoreMissingClasses: When set to true, superclasses and interfaces that cannot be resolved are ignored. Pleases note that in this case the results for the affected classes may not be accurate.
* skipPomModules: Setting this parameter to false (default: true) will not skip execution in modules with packaging type pom.
* skip: Setting this parameter to true will skip execution of the plugin.
* htmlStylesheet: Path to an individual CSS stylesheet for the HTML report.
* htmlTitle: A title for the HTML report (optional).

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

Alternatively it can be used inside the `<reporting/>` tag in order to be invoked by the
[maven-site-plugin](https://maven.apache.org/plugins/maven-site-plugin/) and therewith to be integrated into the site report:

```
<reporting>
	<plugins>
		<plugin>
			<groupId>com.github.siom79.japicmp</groupId>
			<artifactId>japicmp-maven-plugin</artifactId>
			<version>0.6.1</version>
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
