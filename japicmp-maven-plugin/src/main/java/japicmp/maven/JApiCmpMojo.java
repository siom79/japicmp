package japicmp.maven;

import com.google.common.base.Joiner;
import japicmp.cli.JApiCli;
import japicmp.cmp.JApiCmpArchive;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.exception.JApiCmpException;
import japicmp.model.AccessModifier;
import japicmp.model.JApiClass;
import japicmp.model.JApiCompatibilityChange;
import japicmp.model.JApiSemanticVersionLevel;
import japicmp.output.incompatible.IncompatibleErrorOutput;
import japicmp.output.semver.SemverOut;
import japicmp.output.stdout.StdoutOutputGenerator;
import japicmp.output.xml.XmlOutput;
import japicmp.output.xml.XmlOutputGenerator;
import japicmp.output.xml.XmlOutputGeneratorOptions;
import japicmp.util.Optional;
import japicmp.versioning.SemanticVersion;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Mojo(name = "cmp", requiresDependencyResolution = ResolutionScope.COMPILE, defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
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
	private List<Dependency> oldClassPathDependencies;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private List<Dependency> newClassPathDependencies;
	@org.apache.maven.plugins.annotations.Parameter(property = "japicmp.skip", required = false)
	private boolean skip;
	@org.apache.maven.plugins.annotations.Parameter(property = "japicmp.skipXmlReport", required = false)
	private boolean skipXmlReport;
	@org.apache.maven.plugins.annotations.Parameter(property = "japicmp.skipHtmlReport", required = false)
	private boolean skipHtmlReport;
	@org.apache.maven.plugins.annotations.Parameter(property = "japicmp.breakBuildOnModifications", required = false)
	private boolean breakBuildOnModifications;
	@org.apache.maven.plugins.annotations.Parameter(property = "japicmp.breakBuildOnBinaryIncompatibleModifications", required = false)
	private boolean breakBuildOnBinaryIncompatibleModifications;
	@org.apache.maven.plugins.annotations.Parameter(property = "japicmp.breakBuildOnSourceIncompatibleModifications", required = false)
	private boolean breakBuildOnSourceIncompatibleModifications;
	@org.apache.maven.plugins.annotations.Parameter(property = "japicmp.breakBuildBasedOnSemanticVersioning", required = false)
	private boolean breakBuildBasedOnSemanticVersioning;
	@org.apache.maven.plugins.annotations.Parameter(property = "japicmp.breakBuildBasedOnSemanticVersioningForMajorVersionZero", required = false)
	private boolean breakBuildBasedOnSemanticVersioningForMajorVersionZero;
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
	@org.apache.maven.plugins.annotations.Parameter(defaultValue = "${mojoExecution}", readonly = true)
	private MojoExecution mojoExecution;
	@org.apache.maven.plugins.annotations.Parameter(defaultValue = "(,${project.version})", readonly = true)
	private String versionRangeWithProjectVersion;
	@Component
	private ArtifactMetadataSource metadataSource;
	private Options options;

	public void execute() throws MojoExecutionException, MojoFailureException {
		MavenParameters mavenParameters = new MavenParameters(artifactRepositories, artifactFactory, localRepository, artifactResolver, mavenProject, mojoExecution, versionRangeWithProjectVersion, metadataSource);
		PluginParameters pluginParameters = new PluginParameters(skip, newVersion, oldVersion, parameter, dependencies, Optional.of(projectBuildDir), Optional.<String>absent(), true, oldVersions, newVersions, oldClassPathDependencies, newClassPathDependencies);
		executeWithParameters(pluginParameters, mavenParameters);
	}

	Optional<XmlOutput> executeWithParameters(PluginParameters pluginParameters, MavenParameters mavenParameters) throws MojoFailureException, MojoExecutionException {
		if (pluginParameters.getSkipParam()) {
			getLog().info("Skipping execution because parameter 'skip' was set to true.");
			return Optional.absent();
		}
		if (isPomModuleNeedingSkip(pluginParameters, mavenParameters)) {
			getLog().info("Skipping execution because parameter 'skipPomModules' was set to true and this is artifact is of type pom.");
			return Optional.absent();
		}
		if (skipModule(pluginParameters, mavenParameters)) {
			return Optional.absent();
		}
		Options options = getOptions(pluginParameters, mavenParameters);
		JarArchiveComparatorOptions comparatorOptions = JarArchiveComparatorOptions.of(options);
		setUpClassPath(comparatorOptions, pluginParameters, mavenParameters);
		setUpOverrideCompatibilityChanges(comparatorOptions, pluginParameters);
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
		if (options.getNewArchives().isEmpty()) {
			getLog().warn("Skipping execution because no new version could be resolved/found.");
			return Optional.absent();
		}
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(options.getOldArchives(), options.getNewArchives());
		try {
			jApiClasses = applyPostAnalysisScript(pluginParameters.getParameterParam(), jApiClasses);
			File jApiCmpBuildDir = createJapiCmpBaseDir(pluginParameters);
			generateDiffOutput(mavenParameters, pluginParameters, options, jApiClasses, jApiCmpBuildDir);
			XmlOutput xmlOutput = generateXmlOutput(jApiClasses, jApiCmpBuildDir, options, mavenParameters, pluginParameters);
			if (pluginParameters.isWriteToFiles()) {
				List<File> filesWritten = XmlOutputGenerator.writeToFiles(options, xmlOutput);
				for (File file : filesWritten) {
					getLog().info("Written file '" + file.getAbsolutePath() + "'.");
				}
			}
			breakBuildIfNecessary(jApiClasses, pluginParameters.getParameterParam(), options, jarArchiveComparator);
			return Optional.of(xmlOutput);
		} catch (IOException e) {
			throw new MojoFailureException(String.format("Failed to construct output directory: %s", e.getMessage()), e);
		}
	}

	private void setUpOverrideCompatibilityChanges(JarArchiveComparatorOptions comparatorOptions, PluginParameters pluginParameters) throws MojoFailureException {
		if (pluginParameters.getParameterParam() != null && pluginParameters.getParameterParam().getOverrideCompatibilityChangeParameters() != null) {
			List<Parameter.OverrideCompatibilityChangeParameter> overrideCompatibilityChangeParameters = pluginParameters.getParameterParam().getOverrideCompatibilityChangeParameters();
			for (Parameter.OverrideCompatibilityChangeParameter configChange : overrideCompatibilityChangeParameters) {

				String compatibilityChange = configChange.getCompatibilityChange();
				JApiCompatibilityChange foundChange = null;
				for (JApiCompatibilityChange change : JApiCompatibilityChange.values()) {
					if (change.name().equalsIgnoreCase(compatibilityChange)) {
						foundChange = change;
						break;
					}
				}
				if (foundChange == null) {
					throw new MojoFailureException("Unknown compatibility change '" + compatibilityChange + "'. Supported values: " + Joiner.on(',').join(JApiCompatibilityChange.values()));
				}

				JApiSemanticVersionLevel foundSemanticVersionLevel = foundChange.getSemanticVersionLevel();
				String semanticVersionLevel = configChange.getSemanticVersionLevel();
				for (JApiSemanticVersionLevel level : JApiSemanticVersionLevel.values()) {
					if (level.name().equalsIgnoreCase(semanticVersionLevel)) {
						foundSemanticVersionLevel = level;
						break;
					}
				}
				if (foundSemanticVersionLevel == null) {
					throw new MojoFailureException("Unknown semantic version level '" + semanticVersionLevel + "'. Supported values: " + Joiner.on(',').join(JApiSemanticVersionLevel.values()));
				}

				comparatorOptions.addOverrideCompatibilityChange(new JarArchiveComparatorOptions.OverrideCompatibilityChange(foundChange, configChange.isBinaryCompatible(), configChange.isSourceCompatible(), foundSemanticVersionLevel));
			}
		}
	}

	private List<JApiClass> applyPostAnalysisScript(Parameter parameter, List<JApiClass> jApiClasses) throws MojoFailureException {
		List<JApiClass> filteredList = jApiClasses;
		if (parameter != null) {
			String postAnalysisFilterScript = parameter.getPostAnalysisScript();
			if (postAnalysisFilterScript != null) {
				if (Files.exists(Paths.get(postAnalysisFilterScript))) {
					ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("groovy");
					Bindings bindings = scriptEngine.createBindings();
					bindings.put("jApiClasses", jApiClasses);
					try (InputStreamReader fileReader = new InputStreamReader(new FileInputStream(postAnalysisFilterScript), Charset.forName("UTF-8"))) {
						Object returnValue = scriptEngine.eval(fileReader, bindings);
						if (returnValue instanceof List) {
							List returnedList = (List) returnValue;
							filteredList = new ArrayList<>(returnedList.size());
							for (Object obj : returnedList) {
								if (obj instanceof JApiClass) {
									JApiClass jApiClass = (JApiClass) obj;
									filteredList.add(jApiClass);
								}
							}
						} else {
							throw new MojoFailureException("Post-analysis script does not return a list.");
						}
					} catch (ScriptException e) {
						throw new MojoFailureException("Execution of post-analysis script failed: " + e.getMessage(), e);
					} catch (FileNotFoundException e) {
						throw new MojoFailureException("Post-analysis script '" + postAnalysisFilterScript + " does not exist.", e);
					} catch (IOException e) {
						throw new MojoFailureException("Failed to load post-analysis script '" + postAnalysisFilterScript + ": " + e.getMessage(), e);
					}
				} else {
					throw new MojoFailureException("Post-analysis script '" + postAnalysisFilterScript + " does not exist.");
				}
			} else {
				getLog().debug("No post-analysis script provided.");
			}
		}
		return filteredList;
	}

	private boolean skipModule(PluginParameters pluginParameters, MavenParameters mavenParameters) {
		SkipModuleStrategy skipModuleStrategy = new SkipModuleStrategy(pluginParameters, mavenParameters, getLog());
		return skipModuleStrategy.skip();
	}

	private enum ConfigurationVersion {
		OLD, NEW
	}

	private Artifact getComparisonArtifact(MavenParameters mavenParameters, PluginParameters pluginParameters) throws MojoFailureException, MojoExecutionException {
		VersionRange versionRange;
		try {
			versionRange = VersionRange.createFromVersionSpec(mavenParameters.getVersionRangeWithProjectVersion());
		} catch (InvalidVersionSpecificationException e) {
			throw new MojoFailureException("Invalid version versionRange: " + e.getMessage(), e);
		}
		Artifact previousArtifact;
		try {
			MavenProject project = mavenParameters.getMavenProject();
			previousArtifact = mavenParameters.getArtifactFactory().createDependencyArtifact(project.getGroupId(), project.getArtifactId(), versionRange, project.getPackaging(), null, Artifact.SCOPE_COMPILE);
			if (!previousArtifact.getVersionRange().isSelectedVersionKnown(previousArtifact)) {
				getLog().debug("Searching for versions in versionRange: " + previousArtifact.getVersionRange());
				List<ArtifactVersion> availableVersions = mavenParameters.getMetadataSource().retrieveAvailableVersions(previousArtifact, mavenParameters.getLocalRepository(), project.getRemoteArtifactRepositories());
				filterSnapshots(availableVersions);
				filterVersionPattern(availableVersions, pluginParameters);
				ArtifactVersion version = versionRange.matchVersion(availableVersions);
				if (version != null) {
					previousArtifact.selectVersion(version.toString());
				}
			}
		} catch (OverConstrainedVersionException e) {
			throw new MojoFailureException("Invalid comparison version: " + e.getMessage(), e);
		} catch (ArtifactMetadataRetrievalException e) {
			throw new MojoExecutionException("Error determining previous version: " + e.getMessage(), e);
		}
		if (previousArtifact.getVersion() == null) {
			getLog().info("Unable to find a previous version of the project in the repository.");
		}
		return previousArtifact;
	}

	private void filterVersionPattern(List<ArtifactVersion> availableVersions, PluginParameters pluginParameters) throws MojoFailureException {
		if (pluginParameters.getParameterParam() != null && pluginParameters.getParameterParam().getOldVersionPattern() != null) {
			String versionPattern = pluginParameters.getParameterParam().getOldVersionPattern();
			Pattern pattern;
			try {
				pattern = Pattern.compile(versionPattern);
			} catch (PatternSyntaxException e) {
				throw new MojoFailureException("Could not compile provided versionPattern '" + versionPattern + "' as regular expression: " + e.getMessage(), e);
			}
			for (Iterator<ArtifactVersion> versionIterator = availableVersions.iterator(); versionIterator.hasNext(); ) {
				ArtifactVersion version = versionIterator.next();
				Matcher matcher = pattern.matcher(version.toString());
				if (!matcher.matches()) {
					versionIterator.remove();
					if (getLog().isDebugEnabled()) {
						getLog().debug("Filtering version '" + version.toString() + "' because it does not match configured versionPattern '" + versionPattern + "'.");
					}
				}
			}
		} else {
			getLog().debug("Parameter <oldVersionPattern> not configured, i.e. no version filtered.");
		}
	}

	private void filterSnapshots(List versions) {
		for (Iterator versionIterator = versions.iterator(); versionIterator.hasNext(); ) {
			ArtifactVersion version = (ArtifactVersion) versionIterator.next();
			String qualifier = version.getQualifier();
			if (qualifier != null && qualifier.endsWith("SNAPSHOT")) {
				versionIterator.remove();
			}
		}
	}

	private void populateArchivesListsFromParameters(PluginParameters pluginParameters, MavenParameters mavenParameters, List<JApiCmpArchive> oldArchives, List<JApiCmpArchive> newArchives) throws MojoFailureException {
		if (pluginParameters.getOldVersionParam() != null) {
			oldArchives.addAll(retrieveFileFromConfiguration(pluginParameters.getOldVersionParam(), "oldVersion", mavenParameters, pluginParameters, ConfigurationVersion.OLD));
		}
		if (pluginParameters.getOldVersionsParam() != null) {
			for (DependencyDescriptor dependencyDescriptor : pluginParameters.getOldVersionsParam()) {
				if (dependencyDescriptor != null) {
					oldArchives.addAll(retrieveFileFromConfiguration(dependencyDescriptor, "oldVersions", mavenParameters, pluginParameters, ConfigurationVersion.OLD));
				}
			}
		}
		if (pluginParameters.getOldVersionParam() == null && pluginParameters.getOldVersionsParam() == null) {
			try {
				Artifact comparisonArtifact = getComparisonArtifact(mavenParameters, pluginParameters);
				if (comparisonArtifact.getVersion() != null) {
					Set<Artifact> artifacts = resolveArtifact(comparisonArtifact, mavenParameters, false, pluginParameters, ConfigurationVersion.OLD);
					for (Artifact artifact : artifacts) {
						File file = artifact.getFile();
						if (file != null) {
							oldArchives.add(new JApiCmpArchive(file, guessVersion(file)));
						} else {
							handleMissingArtifactFile(pluginParameters, artifact);
						}
					}
				}
			} catch (MojoExecutionException e) {
				throw new MojoFailureException("Computing and resolving comparison artifact failed: " + e.getMessage(), e);
			}
		}
		if (pluginParameters.getNewVersionParam() != null) {
			newArchives.addAll(retrieveFileFromConfiguration(pluginParameters.getNewVersionParam(), "newVersion", mavenParameters, pluginParameters, ConfigurationVersion.NEW));
		}
		if (pluginParameters.getNewVersionsParam() != null) {
			for (DependencyDescriptor dependencyDescriptor : pluginParameters.getNewVersionsParam()) {
				if (dependencyDescriptor != null) {
					newArchives.addAll(retrieveFileFromConfiguration(dependencyDescriptor, "newVersions", mavenParameters, pluginParameters, ConfigurationVersion.NEW));
				}
			}
		}
		if (pluginParameters.getNewVersionParam() == null && pluginParameters.getNewVersionsParam() == null) {
			if (mavenParameters.getMavenProject() != null && mavenParameters.getMavenProject().getArtifact() != null) {
				Artifact artifact = mavenParameters.getMavenProject().getArtifact();
				File file = artifact.getFile();
				if (file != null) {
					try (JarFile jarFile = new JarFile(file)) {
						getLog().debug("Could open file '" + file.getAbsolutePath() + "' of artifact as jar archive: " + jarFile.getName());
						newArchives.add(new JApiCmpArchive(file, guessVersion(file)));
					} catch (IOException e) {
						getLog().warn("No new version specified and file '" + file.getAbsolutePath() + "' of artifact could not be opened as jar archive: " + e.getMessage(), e);
					}
				} else {
					// maven projects with e.g. packaging type ejb don't have a file -> try to resolve artifact through
					// ArtifactHandler
					if (artifact.getArtifactHandler() != null) {
						try {
							VersionRange versionSpec = VersionRange.createFromVersionSpec(artifact.getVersion());
							Artifact dependencyArtifact = mavenParameters.getArtifactFactory().createDependencyArtifact(artifact.getGroupId(), artifact.getArtifactId(), versionSpec, artifact.getArtifactHandler().getExtension(), null, null);
							Set<Artifact> artifacts = resolveArtifact(dependencyArtifact, mavenParameters, false, pluginParameters, ConfigurationVersion.NEW);
							for (Artifact a : artifacts) {
								file = a.getFile();
								if (file != null) {
									if (!file.isFile()) {
										getLog().info("No new version specified and artifact is not a file (" + file.getAbsolutePath() + ").");
										continue;
									}
									try (JarFile jarFile = new JarFile(file)) {
										getLog().debug("Could open file '" + file.getAbsolutePath() + "' of artifact as jar archive: " + jarFile.getName());
										newArchives.add(new JApiCmpArchive(file, guessVersion(file)));
									} catch (IOException e) {
										getLog().warn("No new version specified and file '" + file.getAbsolutePath() + "' of artifact could not be opened as jar archive: " + e.getMessage(), e);
									}
								}
							}
						} catch (InvalidVersionSpecificationException e) {
							getLog().error("Failed to obtain file for artifact " + artifact + ": " + e.getMessage(), e);
						}
					} else {
						getLog().warn("Artifact " + artifact + " does not have an ArtifactHandler. Cannot resolve artifact automatically.");
					}
				}
			}
		}
		if (oldArchives.size() == 0) {
			String message = "Please provide at least one resolvable old version using one of the configuration elements <oldVersion/> or <oldVersions/>.";
			if (ignoreMissingArtifact(pluginParameters, ConfigurationVersion.OLD)) {
				getLog().warn(message);
			} else {
				throw new MojoFailureException(message);
			}
		}
		if (newArchives.size() == 0) {
			String message = "Please provide at least one resolvable new version using one of the configuration elements <newVersion/> or <newVersions/>.";
			if (ignoreMissingArtifact(pluginParameters, ConfigurationVersion.NEW)) {
				getLog().warn(message);
			} else {
				throw new MojoFailureException(message);
			}
		}
	}

	void breakBuildIfNecessary(List<JApiClass> jApiClasses, Parameter parameterParam, Options options, JarArchiveComparator jarArchiveComparator) throws MojoFailureException, MojoExecutionException {
		if (breakBuildBasedOnSemanticVersioning(parameterParam)) {
			options.setErrorOnSemanticIncompatibility(true);
		}
		if (breakBuildBasedOnSemanticVersioningForMajorVersionZero(parameterParam)) {
			options.setErrorOnSemanticIncompatibilityForMajorVersionZero(true);
		}
		if (breakBuildOnBinaryIncompatibleModifications(parameterParam)) {
			options.setErrorOnBinaryIncompatibility(true);
		}
		if (breakBuildOnSourceIncompatibleModifications(parameterParam)) {
			options.setErrorOnSourceIncompatibility(true);
		}
		if (breakBuildOnModificationsParameter(parameterParam)) {
			options.setErrorOnModifications(true);
		}
		if (!parameterParam.isBreakBuildIfCausedByExclusion()) {
			options.setErrorOnExclusionIncompatibility(false);
		}
		if (parameter != null && parameter.getIgnoreMissingOldVersion()) {
			options.setIgnoreMissingOldVersion(true);
		}
		if (parameter != null && parameter.getIgnoreMissingNewVersion()) {
			options.setIgnoreMissingNewVersion(true);
		}

		IncompatibleErrorOutput errorOutput = new IncompatibleErrorOutput(options, jApiClasses, jarArchiveComparator) {
			@Override
			protected void warn(String msg, Throwable error) {
				getLog().warn(msg, error);
			}
		};
		try {
			errorOutput.generate();
		} catch (JApiCmpException e) {
			if (e.getReason() == JApiCmpException.Reason.IncompatibleChange) {
				throw new MojoFailureException(e.getMessage());
			} else {
				throw new MojoExecutionException("Error while checking for incompatible changes", e);
			}
		}
	}

	private boolean breakBuildBasedOnSemanticVersioningForMajorVersionZero(Parameter parameterParam) {
		boolean retVal = false;
		if (parameter != null) {
			retVal = parameter.isBreakBuildBasedOnSemanticVersioningForMajorVersionZero();
		}
		return retVal || breakBuildBasedOnSemanticVersioningForMajorVersionZero;
	}

	Options getOptions(PluginParameters pluginParameters, MavenParameters mavenParameters) throws MojoFailureException {
		if (options != null) {
			return options;
		}
		options = Options.newDefault();
		populateArchivesListsFromParameters(pluginParameters, mavenParameters, options.getOldArchives(), options.getNewArchives());
		Parameter parameterParam = pluginParameters.getParameterParam();
		if (parameterParam != null) {
			String accessModifierArg = parameterParam.getAccessModifier();
			if (accessModifierArg != null) {
				try {
					AccessModifier accessModifier = AccessModifier.valueOf(accessModifierArg.toUpperCase());
					options.setAccessModifier(accessModifier);
				} catch (IllegalArgumentException e) {
					throw new MojoFailureException(String.format("Invalid value for option accessModifier: %s. Possible values are: %s.", accessModifierArg, AccessModifier.listOfAccessModifier()), e);
				}
			}
			options.setOutputOnlyBinaryIncompatibleModifications(parameterParam.getOnlyBinaryIncompatible());
			options.setOutputOnlyModifications(parameterParam.getOnlyModified());

			List<String> excludes = parameterParam.getExcludes();
			if (excludes != null) {
				for (String exclude : excludes) {
					options.addExcludeFromArgument(Optional.fromNullable(exclude), parameterParam.isExcludeExclusively());
				}
			}
			List<String> includes = parameterParam.getIncludes();
			if (includes != null) {
				for (String include : includes) {
					options.addIncludeFromArgument(Optional.fromNullable(include), parameterParam.isIncludeExlusively());
				}
			}

			options.setIncludeSynthetic(parameterParam.getIncludeSynthetic());
			options.setIgnoreMissingClasses(parameterParam.getIgnoreMissingClasses());

			List<String> ignoreMissingClassesByRegularExpressions = parameterParam.getIgnoreMissingClassesByRegularExpressions();
			if (ignoreMissingClassesByRegularExpressions != null) {
				for (String ignoreMissingClassRegularExpression : ignoreMissingClassesByRegularExpressions) {
					options.addIgnoreMissingClassRegularExpression(ignoreMissingClassRegularExpression);
				}
			}
			String htmlStylesheet = parameterParam.getHtmlStylesheet();
			if (htmlStylesheet != null) {
				options.setHtmlStylesheet(Optional.of(htmlStylesheet));
			}
			options.setNoAnnotations(parameterParam.getNoAnnotations());
			options.setReportOnlyFilename(parameterParam.isReportOnlyFilename());
		}
		return options;
	}

	private boolean breakBuildOnModificationsParameter(Parameter parameterParam) {
		boolean retVal = false;
		if (parameterParam != null) {
			retVal = parameterParam.getBreakBuildOnModifications();
		}
		return retVal || breakBuildOnModifications;
	}

	private boolean breakBuildOnBinaryIncompatibleModifications(Parameter parameterParam) {
		boolean retVal = false;
		if (parameterParam != null) {
			retVal = parameterParam.getBreakBuildOnBinaryIncompatibleModifications();
		}
		return retVal || breakBuildOnBinaryIncompatibleModifications;
	}

	private boolean breakBuildOnSourceIncompatibleModifications(Parameter parameter) {
		boolean retVal = false;
		if (parameter != null) {
			retVal = parameter.getBreakBuildOnSourceIncompatibleModifications();
		}
		return retVal || breakBuildOnSourceIncompatibleModifications;
	}

	private boolean breakBuildBasedOnSemanticVersioning(Parameter parameter) {
		boolean retVal = false;
		if (parameter != null) {
			retVal = parameter.getBreakBuildBasedOnSemanticVersioning();
		}
		return retVal || breakBuildBasedOnSemanticVersioning;
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

	private void generateDiffOutput(MavenParameters mavenParameters, PluginParameters pluginParameters, Options options, List<JApiClass> jApiClasses, File jApiCmpBuildDir) throws IOException, MojoFailureException {
		boolean skipDiffReport = false;
		if (pluginParameters.getParameterParam() != null) {
			skipDiffReport = pluginParameters.getParameterParam().isSkipDiffReport();
		}
		if (!skipDiffReport) {
			StdoutOutputGenerator stdoutOutputGenerator = new StdoutOutputGenerator(options, jApiClasses);
			String diffOutput = stdoutOutputGenerator.generate();
			File output = new File(jApiCmpBuildDir.getCanonicalPath() + File.separator + createFilename(mavenParameters) + ".diff");
			writeToFile(diffOutput, output);
		}
	}

	private XmlOutput generateXmlOutput(List<JApiClass> jApiClasses, File jApiCmpBuildDir, Options options, MavenParameters mavenParameters, PluginParameters pluginParameters) throws IOException {
		String filename = createFilename(mavenParameters);
		if (!skipXmlReport(pluginParameters)) {
			options.setXmlOutputFile(Optional.of(jApiCmpBuildDir.getCanonicalPath() + File.separator + filename + ".xml"));
		}
		if (!skipHtmlReport(pluginParameters)) {
			options.setHtmlOutputFile(Optional.of(jApiCmpBuildDir.getCanonicalPath() + File.separator + filename + ".html"));
		}
		SemverOut semverOut = new SemverOut(options, jApiClasses);
		XmlOutputGeneratorOptions xmlOutputGeneratorOptions = new XmlOutputGeneratorOptions();
		xmlOutputGeneratorOptions.setCreateSchemaFile(true);
		xmlOutputGeneratorOptions.setSemanticVersioningInformation(semverOut.generate());
		if (pluginParameters.getParameterParam() != null) {
			String optionalTitle = pluginParameters.getParameterParam().getHtmlTitle();
			xmlOutputGeneratorOptions.setTitle(optionalTitle!=null ?optionalTitle :options.getDifferenceDescription());
		}
		XmlOutputGenerator xmlGenerator = new XmlOutputGenerator(jApiClasses, options, xmlOutputGeneratorOptions);
		return xmlGenerator.generate();
	}

	private boolean skipHtmlReport(PluginParameters pluginParameters) {
		boolean skipReport = false;
		if (pluginParameters.getParameterParam() != null) {
			skipReport = pluginParameters.getParameterParam().getSkipHtmlReport();
		}
		return skipReport || skipHtmlReport;
	}

	private boolean skipXmlReport(PluginParameters pluginParameters) {
		boolean skipReport = false;
		if (pluginParameters.getParameterParam() != null) {
			skipReport = pluginParameters.getParameterParam().getSkipXmlReport();
		}
		return skipReport || skipXmlReport;
	}

	private String createFilename(MavenParameters mavenParameters) {
		String filename = "japicmp";
		String executionId = mavenParameters.getMojoExecution().getExecutionId();
		if (executionId != null && !"default".equals(executionId)) {
			filename = executionId;
		}
		StringBuilder sb = new StringBuilder();
		for (char c : filename.toCharArray()) {
			if (c == '.' || Character.isJavaIdentifierPart(c) || c == '-') {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	private void setUpClassPath(JarArchiveComparatorOptions comparatorOptions, PluginParameters pluginParameters, MavenParameters mavenParameters) throws MojoFailureException {
		if (pluginParameters != null) {
			if (pluginParameters.getDependenciesParam() != null) {
				if (pluginParameters.getOldClassPathDependencies() != null || pluginParameters.getNewClassPathDependencies() != null) {
					throw new MojoFailureException("Please specify either a <dependencies/> element or the two elements <oldClassPathDependencies/> and <newClassPathDependencies/>. " +
						"With <dependencies/> you can specify one common classpath for both versions and with <oldClassPathDependencies/> and <newClassPathDependencies/> a " +
						"separate classpath for the new and old version.");
				} else {
					if (getLog().isDebugEnabled()) {
						getLog().debug("Element <dependencies/> found. Using " + JApiCli.ClassPathMode.ONE_COMMON_CLASSPATH);
					}
					for (Dependency dependency : pluginParameters.getDependenciesParam()) {
						List<JApiCmpArchive> jApiCmpArchives = resolveDependencyToFile("dependencies", dependency, mavenParameters, true, pluginParameters, ConfigurationVersion.NEW);
						for (JApiCmpArchive jApiCmpArchive : jApiCmpArchives) {
							comparatorOptions.getClassPathEntries().add(jApiCmpArchive.getFile().getAbsolutePath());
						}
						comparatorOptions.setClassPathMode(JarArchiveComparatorOptions.ClassPathMode.ONE_COMMON_CLASSPATH);
					}
				}
			} else {
				if (pluginParameters.getOldClassPathDependencies() != null || pluginParameters.getNewClassPathDependencies() != null) {
					if (getLog().isDebugEnabled()) {
						getLog().debug("At least one of the elements <oldClassPathDependencies/> or <newClassPathDependencies/> found. Using " + JApiCli.ClassPathMode.TWO_SEPARATE_CLASSPATHS);
					}
					if (pluginParameters.getOldClassPathDependencies() != null) {
						for (Dependency dependency : pluginParameters.getOldClassPathDependencies()) {
							List<JApiCmpArchive> jApiCmpArchives = resolveDependencyToFile("oldClassPathDependencies", dependency, mavenParameters, true, pluginParameters, ConfigurationVersion.OLD);
							for (JApiCmpArchive archive : jApiCmpArchives) {
								comparatorOptions.getOldClassPath().add(archive.getFile().getAbsolutePath());
							}
						}
					}
					if (pluginParameters.getNewClassPathDependencies() != null) {
						for (Dependency dependency : pluginParameters.getNewClassPathDependencies()) {
							List<JApiCmpArchive> jApiCmpArchives = resolveDependencyToFile("newClassPathDependencies", dependency, mavenParameters, true, pluginParameters, ConfigurationVersion.NEW);
							for (JApiCmpArchive archive : jApiCmpArchives) {
								comparatorOptions.getNewClassPath().add(archive.getFile().getAbsolutePath());
							}
						}
					}
					comparatorOptions.setClassPathMode(JarArchiveComparatorOptions.ClassPathMode.TWO_SEPARATE_CLASSPATHS);
				} else {
					if (getLog().isDebugEnabled()) {
						getLog().debug("None of the elements <oldClassPathDependencies/>, <newClassPathDependencies/> or <dependencies/> found. Using " + JApiCli.ClassPathMode.ONE_COMMON_CLASSPATH);
					}
					comparatorOptions.setClassPathMode(JarArchiveComparatorOptions.ClassPathMode.ONE_COMMON_CLASSPATH);
				}
			}
		}
		setUpClassPathUsingMavenProject(comparatorOptions, mavenParameters, pluginParameters, ConfigurationVersion.NEW);
	}

	private void setUpClassPathUsingMavenProject(JarArchiveComparatorOptions comparatorOptions, MavenParameters mavenParameters, PluginParameters pluginParameters, ConfigurationVersion configurationVersion) throws MojoFailureException {
		notNull(mavenParameters.getMavenProject(), "Maven parameter mavenProject should be provided by maven container.");
		Set<Artifact> dependencyArtifacts = mavenParameters.getMavenProject().getArtifacts();
		Set<String> classPathEntries = new HashSet<>();
		for (Artifact artifact : dependencyArtifacts) {
			String scope = artifact.getScope();
			if (!"test".equals(scope)) {
				Set<Artifact> artifacts = resolveArtifact(artifact, mavenParameters, false, pluginParameters, configurationVersion);
				for (Artifact resolvedArtifact : artifacts) {
					File resolvedFile = resolvedArtifact.getFile();
					if (resolvedFile != null) {
						String absolutePath = resolvedFile.getAbsolutePath();
						if (!classPathEntries.contains(absolutePath)) {
							if (getLog().isDebugEnabled()) {
								getLog().debug("Adding to classpath: " + absolutePath + "; scope: " + scope);
							}
							classPathEntries.add(absolutePath);
						}
					} else {
						handleMissingArtifactFile(pluginParameters, artifact);
					}
				}
			}
		}
		for (String classPathEntry : classPathEntries) {
			comparatorOptions.getClassPathEntries().add(classPathEntry);
		}
	}

	private void handleMissingArtifactFile(PluginParameters pluginParameters, Artifact artifact) {
		if (artifact.isOptional()) {
			if (pluginParameters.getParameterParam().isIgnoreMissingOptionalDependency()) {
				getLog().info("Ignoring missing optional dependency: " + toDescriptor(artifact));
			} else {
				getLog().warn("Could not resolve optional artifact: " + toDescriptor(artifact));
			}
		} else {
			getLog().warn("Could not resolve artifact: " + toDescriptor(artifact));
		}
	}

	private String toDescriptor(Artifact artifact) {
		return artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion();
	}

	private List<JApiCmpArchive> retrieveFileFromConfiguration(DependencyDescriptor dependencyDescriptor, String parameterName, MavenParameters mavenParameters, PluginParameters pluginParameters, ConfigurationVersion configurationVersion) throws MojoFailureException {
		List<JApiCmpArchive> jApiCmpArchives;
		if (dependencyDescriptor instanceof Dependency) {
			Dependency dependency = (Dependency) dependencyDescriptor;
			jApiCmpArchives = resolveDependencyToFile(parameterName, dependency, mavenParameters, false, pluginParameters, configurationVersion);
		} else if (dependencyDescriptor instanceof ConfigurationFile) {
			ConfigurationFile configurationFile = (ConfigurationFile) dependencyDescriptor;
			jApiCmpArchives = resolveConfigurationFileToFile(parameterName, configurationFile, configurationVersion, pluginParameters);
		} else {
			throw new MojoFailureException("DependencyDescriptor is not of type <dependency/> nor of type <configurationFile/>.");
		}
		return jApiCmpArchives;
	}

	private List<JApiCmpArchive> retrieveFileFromConfiguration(Version version, String parameterName, MavenParameters mavenParameters, PluginParameters pluginParameters, ConfigurationVersion configurationVersion) throws MojoFailureException {
		if (version != null) {
			Dependency dependency = version.getDependency();
			if (dependency != null) {
				return resolveDependencyToFile(parameterName, dependency, mavenParameters, false, pluginParameters, configurationVersion);
			} else if (version.getFile() != null) {
				ConfigurationFile configurationFile = version.getFile();
				return resolveConfigurationFileToFile(parameterName, configurationFile, configurationVersion, pluginParameters);
			} else {
				throw new MojoFailureException("Missing configuration parameter 'dependency'.");
			}
		}
		throw new MojoFailureException(String.format("Missing configuration parameter: %s", parameterName));
	}

	private List<JApiCmpArchive> resolveConfigurationFileToFile(String parameterName, ConfigurationFile configurationFile, ConfigurationVersion configurationVersion, PluginParameters pluginParameters) throws MojoFailureException {
		String path = configurationFile.getPath();
		if (path == null) {
			throw new MojoFailureException(String.format("The path element in the configuration of the plugin is missing for %s.", parameterName));
		}
		File file = new File(path);
		if (!file.exists()) {
			if (!ignoreMissingArtifact(pluginParameters, configurationVersion)) {
				throw new MojoFailureException(String.format("The path '%s' does not point to an existing file.", path));
			} else {
				getLog().warn("The file given by path '" + file.getAbsolutePath() + "' does not exist.");
			}
		}
		if (!file.isFile() || !file.canRead()) {
			if (!ignoreMissingArtifact(pluginParameters, configurationVersion)) {
				throw new MojoFailureException(String.format("The file given by path '%s' is either not a file or is not readable.", path));
			} else {
				getLog().warn("The file given by path '" + file.getAbsolutePath() + "' is either not a file or is not readable.");
			}
		}
		return Collections.singletonList(new JApiCmpArchive(file, guessVersion(file)));
	}

	private List<JApiCmpArchive> resolveDependencyToFile(String parameterName, Dependency dependency, MavenParameters mavenParameters,
			boolean transitively, PluginParameters pluginParameters, ConfigurationVersion configurationVersion) throws MojoFailureException {
		List<JApiCmpArchive> jApiCmpArchives = new ArrayList<>();
		if (getLog().isDebugEnabled()) {
			getLog().debug("Trying to resolve dependency '" + dependency + "' to file.");
		}
		if (dependency.getSystemPath() == null) {
			String descriptor = dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion();
			getLog().debug(parameterName + ": " + descriptor);
			Set<Artifact> artifacts = resolveArtifact(dependency, mavenParameters, transitively, pluginParameters, configurationVersion);
			for (Artifact artifact : artifacts) {
				File file = artifact.getFile();
				if (file != null) {
					jApiCmpArchives.add(new JApiCmpArchive(file, artifact.getVersion()));
				} else {
					handleMissingArtifactFile(pluginParameters, artifact);
				}
			}
			if (jApiCmpArchives.size() == 0) {
				String message = String.format("Could not resolve dependency with descriptor '%s'.", descriptor);
				if (ignoreMissingArtifact(pluginParameters, configurationVersion)) {
					getLog().warn(message);
				} else {
					throw new MojoFailureException(message);
				}
			}
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
			boolean addFile = true;
			if (!file.exists()) {
				if (ignoreMissingArtifact(pluginParameters, configurationVersion)) {
					getLog().warn("Could not find file, but ignoreMissingOldVersion is set tot true: " + file.getAbsolutePath());
				} else {
					throw new MojoFailureException("File '" + file.getAbsolutePath() + "' does not exist.");
				}
				addFile = false;
			}
			if (!file.canRead()) {
				if (ignoreMissingArtifact(pluginParameters, configurationVersion)) {
					getLog().warn("File is not readable, but ignoreMissingOldVersion is set tot true: " + file.getAbsolutePath());
				} else {
					throw new MojoFailureException("File '" + file.getAbsolutePath() + "' is not readable.");
				}
				addFile = false;
			}
			String version = guessVersion(file);
			if (addFile) {
				jApiCmpArchives.add(new JApiCmpArchive(file, version));
			}
		}
		return jApiCmpArchives;
	}

	private String guessVersion(File file) {
		String name = file.getName();
		Optional<SemanticVersion> semanticVersion = japicmp.versioning.Version.getSemanticVersion(name);
		String version = semanticVersion.isPresent() ? semanticVersion.get().toString() : "n.a.";
		if (name.contains("SNAPSHOT")) {
			version += "-SNAPSHOT";
		}
		return version;
	}

	private boolean ignoreMissingArtifact(PluginParameters pluginParameters, ConfigurationVersion configurationVersion) {
		return ignoreNonResolvableArtifacts(pluginParameters)
			|| ignoreMissingOldVersion(pluginParameters, configurationVersion)
			|| ignoreMissingNewVersion(pluginParameters, configurationVersion);
	}

	private boolean ignoreNonResolvableArtifacts(PluginParameters pluginParameters) {
		boolean ignoreNonResolvableArtifacts = false;
		Parameter parameterParam = pluginParameters.getParameterParam();
		if (parameterParam != null) {
			String ignoreNonResolvableArtifactsAsString = parameterParam.getIgnoreNonResolvableArtifacts();
			if (Boolean.TRUE.toString().equalsIgnoreCase(ignoreNonResolvableArtifactsAsString)) {
				ignoreNonResolvableArtifacts = true;
			}
		}
		return ignoreNonResolvableArtifacts;
	}

	private boolean ignoreMissingOldVersion(PluginParameters pluginParameters, ConfigurationVersion configurationVersion) {
		return (configurationVersion == ConfigurationVersion.OLD && ignoreMissingOldVersion(pluginParameters));
	}

	private boolean ignoreMissingNewVersion(PluginParameters pluginParameters, ConfigurationVersion configurationVersion) {
		return (configurationVersion == ConfigurationVersion.NEW && ignoreMissingNewVersion(pluginParameters));
	}

	private boolean ignoreMissingOldVersion(PluginParameters pluginParameters) {
		boolean ignoreMissingOldVersion = false;
		if (pluginParameters.getParameterParam() != null) {
			ignoreMissingOldVersion = pluginParameters.getParameterParam().getIgnoreMissingOldVersion();
		}
		return ignoreMissingOldVersion;
	}

	private boolean ignoreMissingNewVersion(PluginParameters pluginParameters) {
		boolean ignoreMissingNewVersion = false;
		if (pluginParameters.getParameterParam() != null) {
			ignoreMissingNewVersion = pluginParameters.getParameterParam().getIgnoreMissingNewVersion();
		}
		return ignoreMissingNewVersion;
	}

	private void writeToFile(String output, File outputfile) throws MojoFailureException, IOException {
		try (OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(outputfile), Charset.forName("UTF-8"))) {
			fileWriter.write(output);
			getLog().info("Written file '" + outputfile.getAbsolutePath() + "'.");
		} catch (Exception e) {
			throw new MojoFailureException(String.format("Failed to write diff file: %s", e.getMessage()), e);
		}
	}

	private Set<Artifact> resolveArtifact(Dependency dependency, MavenParameters mavenParameters, boolean transitively, PluginParameters pluginParameters, ConfigurationVersion configurationVersion) throws MojoFailureException {
		notNull(mavenParameters.getArtifactRepositories(), "Maven parameter artifactRepositories should be provided by maven container.");
		Artifact artifact = mavenParameters.getArtifactFactory().createArtifactWithClassifier(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), dependency.getType(), dependency.getClassifier());
		return resolveArtifact(artifact, mavenParameters, transitively, pluginParameters, configurationVersion);
	}

	private Set<Artifact> resolveArtifact(Artifact artifact, MavenParameters mavenParameters, boolean transitively, PluginParameters pluginParameters, ConfigurationVersion configurationVersion) throws MojoFailureException {
		notNull(mavenParameters.getLocalRepository(), "Maven parameter localRepository should be provided by maven container.");
		notNull(mavenParameters.getArtifactResolver(), "Maven parameter artifactResolver should be provided by maven container.");
		ArtifactResolutionRequest request = new ArtifactResolutionRequest();
		request.setArtifact(artifact);
		request.setLocalRepository(mavenParameters.getLocalRepository());
		request.setRemoteRepositories(mavenParameters.getArtifactRepositories());
		request.setResolutionFilter(new ArtifactFilter() {
			@Override
			public boolean include(Artifact artifact) {
				boolean include = true;
				if (artifact != null) {
					include = false;
				}
				return include;
			}
		});
		if (transitively) {
			request.setResolveTransitively(true);
		}
		ArtifactResolutionResult resolutionResult = mavenParameters.getArtifactResolver().resolve(request);
		if (resolutionResult.hasExceptions()) {
			List<Exception> exceptions = resolutionResult.getExceptions();
			String message = "Could not resolve " + artifact;
			if (ignoreMissingArtifact(pluginParameters, configurationVersion)) {
				getLog().warn(message);
			} else {
				throw new MojoFailureException(message, exceptions.get(0));
			}
		}
		Set<Artifact> artifacts = resolutionResult.getArtifacts();
		if (artifacts.size() == 0) {
			String message = "Could not resolve " + artifact;
			if (ignoreMissingArtifact(pluginParameters, configurationVersion)) {
				getLog().warn(message);
			} else {
				throw new MojoFailureException(message);
			}
		}
		return artifacts;
	}

	private boolean isPomModuleNeedingSkip(PluginParameters pluginParameters, MavenParameters mavenParameters) {
		return pluginParameters.getParameterParam().getSkipPomModules()
			&& "pom".equalsIgnoreCase(mavenParameters.getMavenProject().getArtifact().getType());
	}

	private static <T> T notNull(T value, String msg) throws MojoFailureException {
		if (value == null) {
			throw new MojoFailureException(msg);
		}
		return value;
	}
}
