package japicmp.maven;

import com.google.common.base.Optional;
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
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mojo(name = "cmp", requiresDependencyResolution = ResolutionScope.COMPILE, defaultPhase = LifecyclePhase.VERIFY)
public class JApiCmpMojo extends AbstractMojo {
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private Version oldVersion;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private List<DependencyDescriptor> oldVersions;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private Version newVersion;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private List<DependencyDescriptor> newVersions;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private Parameter parameter;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private List<Dependency> dependencies;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private String skip;
	@org.apache.maven.plugins.annotations.Parameter(property = "project.build.directory", required = true)
	private File projectBuildDir;
	@Component
	private ArtifactFactory artifactFactory;
	@Component
	private ArtifactResolver artifactResolver;
	@org.apache.maven.plugins.annotations.Parameter(defaultValue = "${localRepository}")
	private ArtifactRepository localRepository;
	@org.apache.maven.plugins.annotations.Parameter(defaultValue = "${project.remoteArtifactRepositories}")
	private List<ArtifactRepository> artifactRepositories;
	@org.apache.maven.plugins.annotations.Parameter(defaultValue = "${project}")
	private MavenProject mavenProject;
	@org.apache.maven.plugins.annotations.Parameter( defaultValue = "${mojoExecution}", readonly = true )
	private MojoExecution mojoExecution;

	public void execute() throws MojoExecutionException, MojoFailureException {
		MavenParameters mavenParameters = new MavenParameters(artifactRepositories, artifactFactory, localRepository, artifactResolver, mavenProject, mojoExecution);
		PluginParameters pluginParameters = new PluginParameters(skip, newVersion, oldVersion, parameter, dependencies, Optional.of(projectBuildDir), Optional.<String>absent(), true, oldVersions, newVersions);
		executeWithParameters(pluginParameters, mavenParameters);
	}

	Optional<XmlOutput> executeWithParameters(PluginParameters pluginParameters, MavenParameters mavenParameters) throws MojoFailureException {
		if (Boolean.TRUE.toString().equalsIgnoreCase(pluginParameters.getSkipParam())) {
			getLog().info("Skipping execution because parameter 'skip' was set to true.");
			return Optional.absent();
		}
		if (mavenProject != null && "pom".equals(mavenProject.getPackaging())) {
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
		List<File> oldArchives = new ArrayList<>();
		List<File> newArchives = new ArrayList<>();
		populateArchivesListsFromParameters(pluginParameters, mavenParameters, oldArchives, newArchives);
		Options options = createOptions(pluginParameters.getParameterParam(), oldArchives, newArchives);
		List<JApiClass> jApiClasses = compareArchives(options, pluginParameters.getDependenciesParam(), mavenParameters);
		try {
			File jApiCmpBuildDir = createJapiCmpBaseDir(pluginParameters);
			String diffOutput = generateDiffOutput(jApiClasses, options);
			createFileAndWriteTo(diffOutput, jApiCmpBuildDir, mavenParameters);
			XmlOutput xmlOutput = generateXmlOutput(jApiClasses, jApiCmpBuildDir, options, mavenParameters);
			if (pluginParameters.isWriteToFiles()) {
				List<File> filesWritten = XmlOutputGenerator.writeToFiles(options, xmlOutput);
				for (File file : filesWritten) {
					getLog().info("Written file '" + file.getAbsolutePath() + "'.");
				}
			}
			breakBuildIfNecessary(jApiClasses, pluginParameters.getParameterParam());
			return Optional.of(xmlOutput);
		} catch (IOException e) {
			throw new MojoFailureException(String.format("Failed to construct output directory: %s", e.getMessage()), e);
		}
	}

	private void populateArchivesListsFromParameters(PluginParameters pluginParameters, MavenParameters mavenParameters, List<File> oldArchives, List<File> newArchives) throws MojoFailureException {
		if (pluginParameters.getOldVersionParam() != null) {
			oldArchives.add(retrieveFileFromConfiguration(pluginParameters.getOldVersionParam(), "oldVersion", mavenParameters));
		}
		if (pluginParameters.getOldVersionsParam() != null) {
			for (DependencyDescriptor dependencyDescriptor : pluginParameters.getOldVersionsParam()) {
				if (dependencyDescriptor != null) {
					oldArchives.add(retrieveFileFromConfiguration(dependencyDescriptor, "oldVersions", mavenParameters));
				}
			}
		}
		if (pluginParameters.getNewVersionParam() != null) {
			newArchives.add(retrieveFileFromConfiguration(pluginParameters.getNewVersionParam(), "newVersion", mavenParameters));
		}
		if (pluginParameters.getNewVersionsParam() != null) {
			for (DependencyDescriptor dependencyDescriptor : pluginParameters.getNewVersionsParam()) {
				if (dependencyDescriptor != null) {
					newArchives.add(retrieveFileFromConfiguration(dependencyDescriptor, "newVersions", mavenParameters));
				}
			}
		}
		if (oldArchives.size() == 0) {
			throw new MojoFailureException("Please provide at least one valid old version using one of the configuration elements <oldVersion/> or <oldVersions/>.");
		}
		if (newArchives.size() == 0) {
			throw new MojoFailureException("Please provide at least one valid new version using one of the configuration elements <newVersion/> or <newVersions/>.");
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

	private Options createOptions(Parameter parameterParam, List<File> oldVersions, List<File> newVersions) throws MojoFailureException {
		Options options = new Options();
		options.getOldArchives().addAll(oldVersions);
		options.getNewArchives().addAll(newVersions);
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
			String htmlStylesheet = parameterParam.getHtmlStylesheet();
			if (htmlStylesheet != null) {
				options.setHtmlStylesheet(Optional.of(htmlStylesheet));
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

	private void createFileAndWriteTo(String diffOutput, File jApiCmpBuildDir, MavenParameters mavenParameters) throws IOException, MojoFailureException {
		File output = new File(jApiCmpBuildDir.getCanonicalPath() + File.separator + createFilename(mavenParameters) + ".diff");
		writeToFile(diffOutput, output);
	}

	private File createJapiCmpBaseDir(PluginParameters pluginParameters) throws MojoFailureException {
		if (pluginParameters.getProjectBuildDirParam().isPresent()) {
			try {
				File projectBuildDirParam = pluginParameters.getProjectBuildDirParam().get();
				if (projectBuildDirParam != null) {
					File jApiCmpBuildDir = new File(projectBuildDirParam.getCanonicalPath() + File.separator + "japicmp");
					boolean mkdirs = jApiCmpBuildDir.mkdirs();
					if (mkdirs || jApiCmpBuildDir.isDirectory() && jApiCmpBuildDir.canWrite()) {
						return jApiCmpBuildDir;
					}

					throw new MojoFailureException(String.format("Failed to create directory '%s'.", jApiCmpBuildDir.getAbsolutePath()));
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
				boolean mkdirs = outputDirFile.mkdirs();
				if (mkdirs || outputDirFile.isDirectory() && outputDirFile.canWrite()) {
					return outputDirFile;
				}

				throw new MojoFailureException(String.format("Failed to create directory '%s'.", outputDirFile.getAbsolutePath()));
			} else {
				throw new MojoFailureException("Maven parameter outputDirectory is not set.");
			}
		} else {
			throw new MojoFailureException("None of the two parameters projectBuildDir and outputDirectory are present");
		}
	}

	private String generateDiffOutput(List<JApiClass> jApiClasses, Options options) {
		StdoutOutputGenerator stdoutOutputGenerator = new StdoutOutputGenerator(options, jApiClasses);
		return stdoutOutputGenerator.generate();
	}

	private XmlOutput generateXmlOutput(List<JApiClass> jApiClasses, File jApiCmpBuildDir, Options options, MavenParameters mavenParameters) throws IOException, MojoFailureException {
		String filename = createFilename(mavenParameters);
		options.setXmlOutputFile(Optional.of(jApiCmpBuildDir.getCanonicalPath() + File.separator + filename + ".xml"));
		options.setHtmlOutputFile(Optional.of(jApiCmpBuildDir.getCanonicalPath() + File.separator + filename + ".html"));
		XmlOutputGenerator xmlGenerator = new XmlOutputGenerator(jApiClasses, options, false);
		return xmlGenerator.generate();
	}

	private String createFilename(MavenParameters mavenParameters) {
		String filename = "japicmp";
		String executionId = mavenParameters.getMojoExecution().getExecutionId();
		if (executionId != null && !"default".equals(executionId)) {
			filename = executionId;
		}
		return filename;
	}

	private List<JApiClass> compareArchives(Options options, List<Dependency> dependenciesParam, MavenParameters mavenParameters) throws MojoFailureException {
		JarArchiveComparatorOptions comparatorOptions = JarArchiveComparatorOptions.of(options);
		setUpClassPath(comparatorOptions, dependenciesParam, mavenParameters);
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
		return jarArchiveComparator.compare(options.getOldArchives(), options.getNewArchives());
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

	private File retrieveFileFromConfiguration(DependencyDescriptor dependencyDescriptor, String parameterName, MavenParameters mavenParameters) throws MojoFailureException {
		File file = null;
		if (dependencyDescriptor instanceof Dependency) {
			Dependency dependency = (Dependency) dependencyDescriptor;
			file = resolveDependencyToFile(parameterName, dependency, mavenParameters);
		} else if(dependencyDescriptor instanceof ConfigurationFile) {
			ConfigurationFile configurationFile = (ConfigurationFile) dependencyDescriptor;
			file = resolveConfigurationFileToFile(parameterName, configurationFile);
		}
		if (file == null) {
			throw new MojoFailureException("DependencyDescriptor is not of type <dependency/> nor of type <configurationFile/>.");
		}
		return file;
	}

	private File retrieveFileFromConfiguration(Version version, String parameterName, MavenParameters mavenParameters) throws MojoFailureException {
		if (version != null) {
			Dependency dependency = version.getDependency();
			if (dependency != null) {
				return resolveDependencyToFile(parameterName, dependency, mavenParameters);
			} else if (version.getFile() != null) {
				ConfigurationFile configurationFile = version.getFile();
				return resolveConfigurationFileToFile(parameterName, configurationFile);
			} else {
				throw new MojoFailureException("Missing configuration parameter 'dependency'.");
			}
		}
		throw new MojoFailureException(String.format("Missing configuration parameter: %s", parameterName));
	}

	private File resolveConfigurationFileToFile(String parameterName, ConfigurationFile configurationFile) throws MojoFailureException {
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
			getLog().info("Written file '" + outputfile.getAbsolutePath() + "'.");
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
		Artifact artifact = mavenParameters.getArtifactFactory().createArtifactWithClassifier(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), "jar", dependency.getClassifier());
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
