package japicmp.maven;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.AccessModifier;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.output.stdout.StdoutOutputGenerator;
import japicmp.output.xml.XmlOutput;
import japicmp.output.xml.XmlOutputGenerator;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @goal cmp
 * @phase verify
 * @requiresDependencyResolution compile
 */
public class JApiCmpMojo extends AbstractMojo {
	/**
	 * @parameter
	 * @required
	 */
	private Version oldVersion;

	/**
	 * @parameter
	 * @required
	 */
	private Version newVersion;

	/**
	 * @parameter
	 */
	private Parameter parameter;

	/**
	 * @parameter
	 */
	private List<Dependency> dependencies;

	/**
	 * @parameter
	 */
	private String skip;

	/**
	 * @parameter property="project.build.directory"
	 * @required
	 */
	private File projectBuildDir;

	/**
	 * @component
	 */
	private ArtifactFactory artifactFactory;

	/**
	 * @component
	 */
	private ArtifactResolver artifactResolver;

	/**
	 * @parameter default-value="${localRepository}"
	 */
	private ArtifactRepository localRepository;

	/**
	 * @parameter default-value="${project.remoteArtifactRepositories}"
	 */
	private List<ArtifactRepository> artifactRepositories;

	/**
	 * @parameter default-value="${project}"
	 */
	private MavenProject mavenProject;

	public void execute() throws MojoExecutionException, MojoFailureException {
		MavenParameters mavenParameters = new MavenParameters(artifactRepositories, artifactFactory, localRepository, artifactResolver, mavenProject);
		PluginParameters pluginParameters = new PluginParameters(skip, newVersion, oldVersion, parameter, dependencies, Optional.of(projectBuildDir), Optional.<String>absent(), true);
		executeWithParameters(pluginParameters, mavenParameters);
	}

	Optional<XmlOutput> executeWithParameters(PluginParameters pluginParameters, MavenParameters mavenParameters) throws MojoFailureException {
		Preconditions.checkNotNull(mavenProject, "MavenProject has not been injected.");
		if (Boolean.TRUE.toString().equalsIgnoreCase(pluginParameters.getSkipParam())) {
			getLog().info("Skipping execution because parameter 'skip' was set to true.");
			return Optional.absent();
		}
		if ("pom".equals(mavenProject.getPackaging())) {
			boolean skipPomModules = true;
			Parameter parameterParam = pluginParameters.getParameterParam();
			if (parameterParam != null) {
				String skipPomModulesAsString = parameterParam.getSkipPomModules();
				if (skipPomModulesAsString != null) {
					skipPomModules = Boolean.valueOf(skipPomModulesAsString);
				}
			}
			if (skipPomModules) {
				getLog().info("Skipping execution because packaging of this module is 'pom'.");
				return Optional.absent();
			}
		}
		File newVersionFile = retrieveFileFromConfiguration(pluginParameters.getNewVersionParam(), "newVersion", mavenParameters);
		File oldVersionFile = retrieveFileFromConfiguration(pluginParameters.getOldVersionParam(), "oldVersion", mavenParameters);
		Options options = createOptions(pluginParameters.getParameterParam());
		List<JApiClass> jApiClasses = compareArchives(newVersionFile, oldVersionFile, options, pluginParameters.getDependenciesParam(), mavenParameters);
		try {
			File jApiCmpBuildDir = createJapiCmpBaseDir(pluginParameters);
			String diffOutput = generateDiffOutput(newVersionFile, oldVersionFile, jApiClasses, options);
			createFileAndWriteTo(diffOutput, jApiCmpBuildDir);
			XmlOutput xmlOutput = generateXmlOutput(newVersionFile, oldVersionFile, jApiClasses, jApiCmpBuildDir, options);
			if (pluginParameters.isWriteToFiles()) {
				XmlOutputGenerator.writeToFiles(options, xmlOutput);
			}
			breakBuildIfNecessary(jApiClasses, pluginParameters.getParameterParam());
			return Optional.of(xmlOutput);
		} catch (IOException e) {
			throw new MojoFailureException(String.format("Failed to construct output directory: %s", e.getMessage()), e);
		}
	}

	private void breakBuildIfNecessary(List<JApiClass> jApiClasses, Parameter parameterParam) throws MojoFailureException {
		if (breakBuildOnModificationsParameter(parameterParam)) {
			for (JApiClass jApiClass : jApiClasses) {
				if (jApiClass.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
					throw new MojoFailureException(String.format("Breaking the build because there is at least one modified class: %s", jApiClass.getFullyQualifiedName()));
				}
			}
		}
		if (breakBuildOnBinaryIncompatibleModifications(parameterParam)) {
			for (JApiClass jApiClass : jApiClasses) {
				if (jApiClass.getChangeStatus() != JApiChangeStatus.UNCHANGED && !jApiClass.isBinaryCompatible()) {
					throw new MojoFailureException(String.format("Breaking the build because there is at least one modified class: %s", jApiClass.getFullyQualifiedName()));
				}
			}
		}
	}

	private Options createOptions(Parameter parameterParam) throws MojoFailureException {
		Options options = new Options();
		if (parameterParam != null) {
			String accessModifierArg = parameterParam.getAccessModifier();
			if (accessModifierArg != null) {
				try {
					AccessModifier accessModifier = AccessModifier.valueOf(accessModifierArg.toUpperCase());
					options.setAccessModifier(accessModifier);
				} catch (IllegalArgumentException e) {
					throw new MojoFailureException(String.format("Invalid value for option accessModifier: %s. Possible values are: %s.", accessModifierArg, AccessModifier.listOfAccessModifier()));
				}
			}
			String onlyBinaryIncompatible = parameterParam.getOnlyBinaryIncompatible();
			if (onlyBinaryIncompatible != null) {
				Boolean booleanOnlyBinaryIncompatible = Boolean.valueOf(onlyBinaryIncompatible);
				options.setOutputOnlyBinaryIncompatibleModifications(booleanOnlyBinaryIncompatible);
			}
			String onlyModified = parameterParam.getOnlyModified();
			if (onlyModified != null) {
				Boolean booleanOnlyModified = Boolean.valueOf(onlyModified);
				options.setOutputOnlyModifications(booleanOnlyModified);
			}
			List<String> excludes = parameterParam.getExcludes();
			if (excludes != null) {
				for (String exclude : excludes) {
					options.addExcludeFromArgument(Optional.fromNullable(exclude));
				}
			}
			List<String> includes = parameterParam.getIncludes();
			if (includes != null) {
				for (String include : includes) {
					options.addIncludeFromArgument(Optional.fromNullable(include));
				}
			}
			String includeSyntheticString = parameterParam.getIncludeSynthetic();
			if (includeSyntheticString != null) {
				Boolean includeSynthetic = Boolean.valueOf(includeSyntheticString);
				options.setIncludeSynthetic(includeSynthetic);
			}
			String ignoreMissingClassesString = parameterParam.getIgnoreMissingClasses();
			if (ignoreMissingClassesString != null) {
				Boolean ignoreMissingClasses = Boolean.valueOf(ignoreMissingClassesString);
				options.setIgnoreMissingClasses(ignoreMissingClasses);
			}
		}
		return options;
	}

	private boolean breakBuildOnModificationsParameter(Parameter parameterParam) {
		boolean retVal = false;
		if (parameterParam != null) {
			retVal = Boolean.valueOf(parameterParam.getBreakBuildOnModifications());
		}
		return retVal;
	}

	private boolean breakBuildOnBinaryIncompatibleModifications(Parameter parameterParam) {
		boolean retVal = false;
		if (parameterParam != null) {
			retVal = Boolean.valueOf(parameterParam.getBreakBuildOnBinaryIncompatibleModifications());
		}
		return retVal;
	}

	private void createFileAndWriteTo(String diffOutput, File jApiCmpBuildDir) throws IOException, MojoFailureException {
		File outputfile = new File(jApiCmpBuildDir.getCanonicalPath() + File.separator + "japicmp.diff");
		writeToFile(diffOutput, outputfile);
	}

	private File createJapiCmpBaseDir(PluginParameters pluginParameters) throws MojoFailureException {
		if (pluginParameters.getProjectBuildDirParam().isPresent()) {
			try {
				File projectBuildDirParam = pluginParameters.getProjectBuildDirParam().get();
				if (projectBuildDirParam != null) {
					File jApiCmpBuildDir = new File(projectBuildDirParam.getCanonicalPath() + File.separator + "japicmp");
					boolean mkdirs = jApiCmpBuildDir.mkdirs();
					if (!mkdirs) {
						throw new MojoFailureException(String.format("Failed to create directory '%s'.", jApiCmpBuildDir.getAbsolutePath()));
					}
					return jApiCmpBuildDir;
				} else {
					throw new MojoFailureException("Maven parameter projectBuildDir is not set.");
				}
			} catch (IOException e) {
				throw new MojoFailureException("Failed to create output directory: " + e.getMessage(), e);
			}
		} else if (pluginParameters.getOutputDirectory().isPresent()) {
			String outputDirectory = pluginParameters.getOutputDirectory().get();
			if (outputDirectory != null) {
				File outputDirFile = new File(outputDirectory);
				if (!outputDirFile.exists()) {
					boolean mkdirs = outputDirFile.mkdirs();
					if (!mkdirs) {
						throw new MojoFailureException(String.format("Failed to create directory '%s'.", outputDirFile.getAbsolutePath()));
					}
				}
				return outputDirFile;
			} else {
				throw new MojoFailureException("Maven parameter outputDirectory is not set.");
			}
		} else {
			throw new MojoFailureException("None of the two parameters projectBuildDir and outputDirectory are present");
		}
	}

	private String generateDiffOutput(File newVersionFile, File oldVersionFile, List<JApiClass> jApiClasses, Options options) {
		StdoutOutputGenerator stdoutOutputGenerator = new StdoutOutputGenerator(options, jApiClasses, oldVersionFile, newVersionFile);
		String diffOutput = stdoutOutputGenerator.generate();
		getLog().info(diffOutput);
		return diffOutput;
	}

	private XmlOutput generateXmlOutput(File newVersionFile, File oldVersionFile, List<JApiClass> jApiClasses, File jApiCmpBuildDir, Options options) throws IOException, MojoFailureException {
		options.setXmlOutputFile(Optional.of(jApiCmpBuildDir.getCanonicalPath() + File.separator + "japicmp.xml"));
		options.setHtmlOutputFile(Optional.of(jApiCmpBuildDir.getCanonicalPath() + File.separator + "japicmp.html"));
		XmlOutputGenerator xmlGenerator = new XmlOutputGenerator(oldVersionFile.getAbsolutePath(), newVersionFile.getAbsolutePath(), jApiClasses, options, false);
		return xmlGenerator.generate();
	}

	private List<JApiClass> compareArchives(File newVersionFile, File oldVersionFile, Options options, List<Dependency> dependenciesParam, MavenParameters mavenParameters) throws MojoFailureException {
		JarArchiveComparatorOptions comparatorOptions = JarArchiveComparatorOptions.of(options);
		setUpClassPath(comparatorOptions, dependenciesParam, mavenParameters);
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
		return jarArchiveComparator.compare(oldVersionFile, newVersionFile);
	}

	private void setUpClassPath(JarArchiveComparatorOptions comparatorOptions, List<Dependency> dependenciesParam, MavenParameters mavenParameters) throws MojoFailureException {
		if (dependenciesParam != null) {
			for (Dependency dependency : dependenciesParam) {
				File file = resolveDependencyToFile("dependencies", dependency, mavenParameters);
				if (getLog().isDebugEnabled()) {
					getLog().debug("Resolved dependency " + dependency + " to file '" + file.getAbsolutePath() + "'.");
				}
				comparatorOptions.getClassPathEntries().add(file.getAbsolutePath());
			}
		}
		setUpClassPathUsingMavenProject(comparatorOptions, mavenParameters);
	}

	private void setUpClassPathUsingMavenProject(JarArchiveComparatorOptions comparatorOptions, MavenParameters mavenParameters) throws MojoFailureException {
		notNull(mavenParameters.getMavenProject(), "Maven parameter mavenProject should be provided by maven container.");
		Set<Artifact> dependencyArtifacts = mavenParameters.getMavenProject().getArtifacts();
		for (Artifact artifact : dependencyArtifacts) {
			String scope = artifact.getScope();
			if (!"test".equals(scope)) {
				Optional<File> file = resolveArtifact(artifact, mavenParameters);
				if (file.isPresent()) {
					if (getLog().isDebugEnabled()) {
						getLog().debug("Adding to classpath: " + file.get().getAbsolutePath() + "; scope: " + scope);
					}
					comparatorOptions.getClassPathEntries().add(file.get().getAbsolutePath());
				} else {
					getLog().error("Could not resolve artifact " + artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion());
				}
			}
		}
	}

	private File retrieveFileFromConfiguration(Version version, String parameterName, MavenParameters mavenParameters) throws MojoFailureException {
		if (version != null) {
			Dependency dependency = version.getDependency();
			if (dependency != null) {
				return resolveDependencyToFile(parameterName, dependency, mavenParameters);
			} else if (version.getFile() != null) {
				ConfigurationFile configurationFile = version.getFile();
				String path = configurationFile.getPath();
				if (path == null) {
					throw new MojoFailureException(String.format("The path element in the configuration of the plugin is missing for %s.", parameterName));
				}
				File file = new File(path);
				if (!file.exists()) {
					throw new MojoFailureException(String.format("The path '%s' does not point to an existing file.", path));
				}
				if (!file.isFile() || !file.canRead()) {
					throw new MojoFailureException(String.format("The file given by path '%s' is either not a file or is not readable.", path));
				}
				return file;
			} else {
				throw new MojoFailureException(String.format("Missing configuration parameter 'dependency'."));
			}
		}
		throw new MojoFailureException(String.format("Missing configuration parameter: %s", parameterName));
	}

	private File resolveDependencyToFile(String parameterName, Dependency dependency, MavenParameters mavenParameters) throws MojoFailureException {
		if (getLog().isDebugEnabled()) {
			getLog().debug("Trying to resolve dependency '" + dependency + "' to file.");
		}
		if (dependency.getSystemPath() == null) {
			String descriptor = dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion();
			getLog().debug(parameterName + ": " + descriptor);
			Optional<File> file = resolveArtifact(dependency, mavenParameters);
			if (!file.isPresent()) {
				throw new MojoFailureException(String.format("Could not resolve dependency with descriptor '%s'.", descriptor));
			}
			return file.get();
		} else {
			String systemPath = dependency.getSystemPath();
			Pattern pattern = Pattern.compile("\\$\\{([^\\}])");
			Matcher matcher = pattern.matcher(systemPath);
			if (matcher.matches()) {
				for (int i = 1; i <= matcher.groupCount(); i++) {
					String property = matcher.group(i);
					String propertyResolved = mavenParameters.getMavenProject().getProperties().getProperty(property);
					if (propertyResolved != null) {
						systemPath = systemPath.replaceAll("${" + property + "}", propertyResolved);
					} else {
						throw new MojoFailureException("Could not resolve property '" + property + "'.");
					}
				}
			}
			File file = new File(systemPath);
			if (!file.exists()) {
				throw new MojoFailureException("File '" + file.getAbsolutePath() + "' does not exist.");
			}
			if (!file.canRead()) {
				throw new MojoFailureException("File '" + file.getAbsolutePath() + "' is not readable.");
			}
			return file;
		}
	}

	private void writeToFile(String output, File outputfile) throws MojoFailureException, IOException {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(outputfile);
			fileWriter.write(output);
		} catch (Exception e) {
			throw new MojoFailureException(String.format("Failed to write diff file: %s", e.getMessage()), e);
		} finally {
			if (fileWriter != null) {
				fileWriter.close();
			}
		}
	}

	private Optional<File> resolveArtifact(Dependency dependency, MavenParameters mavenParameters) throws MojoFailureException {
		notNull(mavenParameters.getArtifactRepositories(), "Maven parameter artifactRepositories should be provided by maven container.");
		Artifact artifact = mavenParameters.getArtifactFactory().createBuildArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), "jar");
		return resolveArtifact(artifact, mavenParameters);
	}

	private Optional<File> resolveArtifact(Artifact artifact, MavenParameters mavenParameters) throws MojoFailureException {
		notNull(mavenParameters.getLocalRepository(), "Maven parameter localRepository should be provided by maven container.");
		notNull(mavenParameters.getArtifactResolver(), "Maven parameter artifactResolver should be provided by maven container.");
		ArtifactResolutionRequest request = new ArtifactResolutionRequest();
		request.setArtifact(artifact);
		request.setLocalRepository(mavenParameters.getLocalRepository());
		request.setRemoteRepositories(mavenParameters.getArtifactRepositories());
		mavenParameters.getArtifactResolver().resolve(request);
		return Optional.fromNullable(artifact.getFile());
	}

	private static <T> T notNull(T value, String msg) throws MojoFailureException {
		if (value == null) {
			throw new MojoFailureException(msg);
		}
		return value;
	}
}
